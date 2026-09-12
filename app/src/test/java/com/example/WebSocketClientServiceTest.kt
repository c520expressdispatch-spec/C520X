package com.example

import com.example.data.RealtimeChatMessage
import com.example.data.WebSocketConnectionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WebSocketClientServiceTest {

    @Test
    fun testRealtimeChatMessageSerialization() {
        val original = RealtimeChatMessage(
            id = "msg-12345",
            conversationId = "trade-electric-42",
            senderRole = "Contractor",
            senderName = "Alex Ramirez (Lead Tech)",
            message = "Arriving in 15 minutes with the 200A square D main breaker.",
            timestamp = 1726050000000L,
            attachmentType = "image",
            attachmentName = "panel_diagram.jpg",
            clientPhone = "(520) 441-8920"
        )

        val json = original.toJson()
        assertNotNull(json)
        assertTrue(json.contains("msg-12345"))
        assertTrue(json.contains("Alex Ramirez"))
        assertTrue(json.contains("200A square D"))

        val parsed = RealtimeChatMessage.fromJson(json)
        assertNotNull(parsed)
        assertEquals(original.id, parsed?.id)
        assertEquals(original.conversationId, parsed?.conversationId)
        assertEquals(original.senderRole, parsed?.senderRole)
        assertEquals(original.senderName, parsed?.senderName)
        assertEquals(original.message, parsed?.message)
        assertEquals(original.timestamp, parsed?.timestamp)
        assertEquals(original.attachmentType, parsed?.attachmentType)
        assertEquals(original.attachmentName, parsed?.attachmentName)
        assertEquals(original.clientPhone, parsed?.clientPhone)
    }

    @Test
    fun testPlainTextFallbackDeserialization() {
        val rawText = "Hello contractor, what is the quote for the HVAC diagnosis?"
        val parsed = RealtimeChatMessage.fromJson(rawText)

        assertNotNull(parsed)
        assertEquals("Customer", parsed?.senderRole)
        assertEquals(rawText, parsed?.message)
    }

    @Test
    fun testWebSocketStatusStates() {
        val disconnected: WebSocketConnectionStatus = WebSocketConnectionStatus.Disconnected
        val connecting: WebSocketConnectionStatus = WebSocketConnectionStatus.Connecting
        val connected: WebSocketConnectionStatus = WebSocketConnectionStatus.Connected("wss://echo.websocket.events")
        val reconnecting: WebSocketConnectionStatus = WebSocketConnectionStatus.Reconnecting(attempt = 2, maxAttempts = 5)
        val error: WebSocketConnectionStatus = WebSocketConnectionStatus.Error("Connection reset by peer")

        assertTrue(disconnected is WebSocketConnectionStatus.Disconnected)
        assertTrue(connecting is WebSocketConnectionStatus.Connecting)
        assertTrue(connected is WebSocketConnectionStatus.Connected)
        assertEquals("wss://echo.websocket.events", (connected as WebSocketConnectionStatus.Connected).url)
        assertEquals(2, (reconnecting as WebSocketConnectionStatus.Reconnecting).attempt)
        assertEquals("Connection reset by peer", (error as WebSocketConnectionStatus.Error).message)
    }
}
