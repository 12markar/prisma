import SwiftUI
import CoreUI

/// Hardcoded URL — change for real-device testing. iOS Simulator and macOS
/// reach the host process at `localhost`. A physical iPhone needs the Mac's
/// LAN IP (e.g. `ws://192.168.1.42:7331/ws`).
private let SDUI_WS_URL = "ws://localhost:7331/ws"

struct SDUIScreen: View {
    @StateObject private var client = SDUIClient(urlString: SDUI_WS_URL)
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
            connectionBanner
            switch client.state {
            case .loaded(let doc):
                SDUIRender(node: doc.root)
            case .invalidPayload(let message):
                invalidState(message)
            case .connecting, .disconnected:
                waitingState
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .onAppear { client.start() }
        .onDisappear { client.stop() }
    }

    private var connectionBanner: some View {
        let (label, dot): (String, PrismaSemanticColor) = {
            switch client.state {
            case .connecting:        return ("Connecting to \(SDUI_WS_URL)", PrismaSemanticColors.statusInfoDefault)
            case .loaded:            return ("Live · changes to layout.json hot-reload", PrismaSemanticColors.statusSuccessDefault)
            case .invalidPayload:    return ("Invalid layout — see schema validation below", PrismaSemanticColors.statusDangerDefault)
            case .disconnected(let r): return ("Disconnected — \(r)", PrismaSemanticColors.statusWarningDefault)
            }
        }()
        return HStack(spacing: PrismaSpacing.sp3) {
            Circle()
                .fill(dot.themed(scheme))
                .frame(width: 8, height: 8)
            Text(label)
                .font(PrismaTypography.labelMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
            Spacer()
        }
        .padding(.horizontal, PrismaSpacing.sp4)
        .padding(.vertical, PrismaSpacing.sp3)
        .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
    }

    private var waitingState: some View {
        let (headline, body): (String, String) = {
            switch client.state {
            case .connecting:          return ("Connecting…", "Reaching \(SDUI_WS_URL)")
            case .disconnected(let r): return ("Can't reach the dev server", r)
            default:                   return ("No layout yet", "")
            }
        }()
        return VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
            Text(headline)
                .font(PrismaTypography.titleLg.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            if !body.isEmpty {
                Text(body)
                    .font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
            }
            Spacer().frame(height: PrismaSpacing.sp3)
            Group {
                Text("1. Run ./startserver from the repo root.")
                Text("2. Confirm http://localhost:7331 opens in a browser on the host.")
                Text("3. WS URL on iOS Simulator is \(SDUI_WS_URL) — change SDUIScreen.swift for a real device.")
                Text("Logs: filter the Xcode console for [SDUI]")
            }
            .font(PrismaTypography.bodySm.font)
            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
        }
        .padding(PrismaSpacing.sp5)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.lg))
    }

    private func invalidState(_ message: String) -> some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
            Text("Schema validation failed")
                .font(PrismaTypography.titleMd.font)
                .foregroundStyle(PrismaSemanticColors.statusDangerDefault.themed(scheme))
            Text(message)
                .font(PrismaTypography.bodySm.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
        }
        .padding(PrismaSpacing.sp5)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(PrismaSemanticColors.statusDangerSubtle.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.lg))
    }
}
