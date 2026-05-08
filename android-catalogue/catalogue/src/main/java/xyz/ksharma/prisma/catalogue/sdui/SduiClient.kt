package xyz.ksharma.prisma.catalogue.sdui

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.pow

// Filter logcat with: `adb logcat -s SDUI:*` to see connection lifecycle.
private const val TAG = "SDUI"

/**
 * Connection state for the SDUI screen. Renderer maps each state to a
 * specific UI affordance (banner, full-screen error, content).
 */
public sealed class SduiState {
    public data object Connecting : SduiState()
    public data object Connected : SduiState()
    public data class Loaded(val doc: SduiDocument) : SduiState()
    public data class InvalidPayload(val message: String) : SduiState()
    public data class Disconnected(val reason: String) : SduiState()
}

/**
 * WebSocket client. Emits [SduiState] into [state]; recovers from drops with
 * exponential backoff. Lifecycle is owned by the screen — call [start] when
 * the screen enters composition and [stop] when it leaves.
 *
 * Wire format:
 *   layout frame  →  { "kind": "layout", "doc": <SduiDocument> }
 *   error  frame  →  { "kind": "error",  "errors": [...] }
 */
public class SduiClient(private val url: String) {

    private val http = OkHttpClient.Builder()
        // Server pings clients via TCP keepalive; we ping back at 20s so a
        // half-open NAT path on the dev laptop fails fast instead of hanging
        // for minutes before the OS notices.
        .pingInterval(20, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        // Each polymorphic hierarchy carries its own @JsonClassDiscriminator,
        // so no global discriminator setting needed.
    }

    private val _state = MutableStateFlow<SduiState>(SduiState.Connecting)
    public val state: StateFlow<SduiState> = _state.asStateFlow()

    private val supervisor = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + supervisor)
    private var socket: WebSocket? = null
    private var retryJob: Job? = null
    private var attempt = 0

    public fun start() {
        connect()
    }

    public fun stop() {
        retryJob?.cancel()
        socket?.close(1000, "screen left")
        socket = null
        scope.cancel()
        // Don't shutdown the OkHttp dispatcher — the executor lives in
        // a singleton-by-default OkHttp pool and other parts of the app
        // (Coil, etc.) might share it. Closing the WebSocket is enough.
    }

    private fun connect() {
        Log.d(TAG, "connect → $url (attempt ${attempt + 1})")
        _state.value = if (attempt == 0) SduiState.Connecting else SduiState.Disconnected("Reconnecting (attempt ${attempt + 1})…")
        socket = http.newWebSocket(
            Request.Builder().url(url).build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d(TAG, "open ← $url")
                    attempt = 0
                    _state.value = SduiState.Connected
                }
                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d(TAG, "frame ${text.length}B head=${text.take(80).replace('\n', ' ')}")
                    handleFrame(text)
                }
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(TAG, "closed code=$code reason=$reason")
                    _state.value = SduiState.Disconnected(if (reason.isBlank()) "Connection closed" else reason)
                    scheduleReconnect()
                }
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e(TAG, "fail ${t.javaClass.simpleName}: ${t.message}", t)
                    _state.value = SduiState.Disconnected("${t.javaClass.simpleName}: ${t.message ?: "Connection failed"}")
                    scheduleReconnect()
                }
            },
        )
    }

    private fun handleFrame(text: String) {
        runCatching {
            val root = json.parseToJsonElement(text).jsonObject
            when (val kind = root["kind"]?.jsonPrimitive?.content) {
                "layout" -> {
                    val docElement = root["doc"] ?: error("layout frame missing doc")
                    val doc = json.decodeFromJsonElement(SduiDocument.serializer(), docElement)
                    _state.value = SduiState.Loaded(doc)
                }
                "error" -> {
                    val errors = root["errors"]?.toString() ?: "(no detail)"
                    _state.value = SduiState.InvalidPayload(errors)
                }
                else -> _state.value = SduiState.InvalidPayload("Unknown frame kind: $kind")
            }
        }.onFailure { e ->
            _state.value = SduiState.InvalidPayload(e.message ?: "decode error")
        }
    }

    private fun scheduleReconnect() {
        retryJob?.cancel()
        retryJob = scope.launch {
            attempt++
            // 0.5s, 1s, 2s, 4s, 8s, capped at 15s — fast enough that a quick
            // server restart is invisible, slow enough that a real outage
            // doesn't hammer the host.
            val delayMs = min(15_000L, (500.0 * 2.0.pow(attempt - 1)).toLong())
            delay(delayMs)
            connect()
        }
    }
}
