package com.example.data

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Firebase Cloud Messaging Service for C520X Platform.
 * Receives incoming customer inquiries, emergency trade leads, and webhook dispatches in real-time,
 * saves them to the on-device database, and alerts the operator with high-priority notifications.
 */
class C520xFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("C520x_FCM", "New Firebase Cloud Messaging registration token generated: $token")
        CustomerInquiryCloudSyncService.updateFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i("C520x_FCM", "Inbound message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val clientName = data["clientName"] ?: notification?.title?.replace("New Lead: ", "") ?: "Inbound Web Customer"
        val clientPhone = data["clientPhone"] ?: "(520) 555-0199"
        val serviceTrade = data["serviceTrade"] ?: "Electrical / HVAC"
        val projectDescription = data["projectDescription"] ?: notification?.body ?: "Customer inquiry submitted via online webhook."
        val budget = data["budget"]?.toDoubleOrNull() ?: 1200.0
        val source = data["source"] ?: "Cloud Webhook (FCM)"
        val urgency = data["urgency"] ?: "Emergency"

        val lead = LeadEntity(
            clientName = clientName,
            clientPhone = clientPhone,
            serviceTrade = serviceTrade,
            projectDescription = projectDescription,
            budget = budget,
            source = source,
            status = "New Lead",
            urgency = urgency,
            timestamp = System.currentTimeMillis()
        )

        serviceScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val newId = db.leadDao().insertLead(lead)
                val storedLead = lead.copy(id = newId.toInt())

                CustomerLeadNotificationManager.showCustomerLeadNotification(
                    context = applicationContext,
                    lead = storedLead
                )
                CustomerInquiryCloudSyncService.recordInboundInquiry(storedLead)
            } catch (e: Exception) {
                Log.e("C520x_FCM", "Error processing incoming FCM lead", e)
            }
        }
    }
}
