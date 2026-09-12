package com.example.data

import android.util.Log
import com.example.BuildConfig
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class GeminiLiveService(private val listener: GeminiLiveListener) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val apiKey = BuildConfig.GEMINI_API_KEY

    fun connect() {
        if (apiKey.isEmpty()) {
            Log.e("GeminiLiveService", "API Key is empty")
            return
        }

        val request = Request.Builder()
            .url("wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent?key=$apiKey")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("GeminiLiveService", "Connected")
                listener.onConnected()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("GeminiLiveService", "Message: $text")
                listener.onMessageReceived(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                listener.onBytesReceived(bytes)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("GeminiLiveService", "Failure: ${t.message}")
                listener.onError(t)
            }
        })
    }

    fun sendRealtimeInput(json: String) {
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected")
    }
}

interface GeminiLiveListener {
    fun onConnected()
    fun onMessageReceived(text: String)
    fun onBytesReceived(bytes: ByteString)
    fun onError(t: Throwable)
}
