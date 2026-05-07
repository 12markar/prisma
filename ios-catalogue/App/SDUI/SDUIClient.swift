import Foundation

enum SDUIState {
    case connecting
    case loaded(SDUIDocument)
    case invalidPayload(String)
    case disconnected(String)
}

/// WebSocket client that listens to the Prisma SDUI dev server and publishes
/// state for SwiftUI to bind to. Runs on the main actor so `@Published`
/// updates land on the right thread without dispatch hops.
@MainActor
final class SDUIClient: ObservableObject {

    @Published var state: SDUIState = .connecting

    private let url: URL
    private var task: URLSessionWebSocketTask?
    private var session: URLSession
    private var attempt = 0
    private var stopped = false
    private let decoder = JSONDecoder()

    init(urlString: String) {
        // Force-unwrap acceptable here — URL is a hardcoded constant. If it
        // ever fails to parse, that's a programmer error worth crashing on.
        guard let parsed = URL(string: urlString) else {
            preconditionFailure("Invalid SDUI URL: \(urlString)")
        }
        self.url = parsed
        self.session = URLSession(configuration: .default)
    }

    func start() {
        stopped = false
        connect()
    }

    func stop() {
        stopped = true
        task?.cancel(with: .goingAway, reason: nil)
        task = nil
    }

    private func connect() {
        if attempt == 0 { state = .connecting }
        let t = session.webSocketTask(with: url)
        task = t
        t.resume()
        Task { await self.receiveLoop(t) }
    }

    private func receiveLoop(_ t: URLSessionWebSocketTask) async {
        do {
            while !stopped {
                let msg = try await t.receive()
                attempt = 0
                switch msg {
                case .string(let s): handle(s)
                case .data(let d):
                    if let s = String(data: d, encoding: .utf8) { handle(s) }
                @unknown default: break
                }
            }
        } catch {
            guard !stopped else { return }
            state = .disconnected(error.localizedDescription)
            scheduleReconnect()
        }
    }

    private func handle(_ text: String) {
        guard
            let data = text.data(using: .utf8),
            let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
            let kind = json["kind"] as? String
        else {
            state = .invalidPayload("Could not parse frame")
            return
        }
        switch kind {
        case "layout":
            guard
                let docDict = json["doc"],
                let docData = try? JSONSerialization.data(withJSONObject: docDict)
            else {
                state = .invalidPayload("Missing doc in layout frame")
                return
            }
            do {
                let doc = try decoder.decode(SDUIDocument.self, from: docData)
                state = .loaded(doc)
            } catch {
                state = .invalidPayload("Decode failed: \(error.localizedDescription)")
            }
        case "error":
            let detail = (json["errors"] as? [Any]).map { String(describing: $0) } ?? "(no detail)"
            state = .invalidPayload(detail)
        default:
            state = .invalidPayload("Unknown frame kind: \(kind)")
        }
    }

    private func scheduleReconnect() {
        guard !stopped else { return }
        attempt += 1
        // 0.5s, 1s, 2s, 4s, 8s, capped at 15s.
        let delaySeconds = min(15.0, 0.5 * pow(2.0, Double(attempt - 1)))
        Task { [weak self] in
            try? await Task.sleep(nanoseconds: UInt64(delaySeconds * 1_000_000_000))
            guard let self, !self.stopped else { return }
            self.connect()
        }
    }
}
