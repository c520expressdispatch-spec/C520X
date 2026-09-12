package com.example.data.remote

import android.util.Log
import com.example.data.models.EstimateResult
import com.example.data.models.LeadItem
import com.example.data.models.PartnerType
import com.example.data.models.WebhookConfig
import com.example.data.models.WebhookDeliveryLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WebhookDispatcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun testWebhook(
        webhookName: String,
        url: String,
        httpMethod: String = "POST",
        authHeader: String = "",
        customHeadersJson: String = "{\"Content-Type\":\"application/json\"}",
        payloadJson: String
    ): WebhookDeliveryLog = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = payloadJson.toRequestBody(mediaType)
            val requestBuilder = Request.Builder().url(url)
            when (httpMethod.uppercase()) {
                "GET" -> requestBuilder.get()
                "PUT" -> requestBuilder.put(requestBody)
                "PATCH" -> requestBuilder.patch(requestBody)
                else -> requestBuilder.post(requestBody)
            }
            if (authHeader.isNotBlank()) {
                if (authHeader.startsWith("Bearer ") || authHeader.startsWith("Basic ")) {
                    requestBuilder.addHeader("Authorization", authHeader)
                } else {
                    requestBuilder.addHeader("Authorization", "Bearer $authHeader")
                }
            }
            if (customHeadersJson.isNotBlank()) {
                try {
                    val headersObj = JSONObject(customHeadersJson)
                    val keys = headersObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = headersObj.getString(key)
                        requestBuilder.addHeader(key, value)
                    }
                } catch (e: Exception) {
                    Log.e("WebhookDispatcher", "Error parsing custom headers: ${e.message}")
                }
            }
            val response = client.newCall(requestBuilder.build()).execute()
            val latency = System.currentTimeMillis() - startTime
            val statusCode = response.code
            val bodyString = response.body?.string() ?: ""
            WebhookDeliveryLog(
                webhookName = webhookName,
                url = url,
                event = "TEST_PING",
                payloadJson = payloadJson,
                statusCode = statusCode,
                responseBody = if (bodyString.length > 500) bodyString.take(500) + "..." else bodyString,
                latencyMs = latency,
                isSuccess = response.isSuccessful,
                timestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            WebhookDeliveryLog(
                webhookName = webhookName,
                url = url,
                event = "TEST_PING_FAILED",
                payloadJson = payloadJson,
                statusCode = 0,
                responseBody = "Connection error: ${e.message}",
                latencyMs = latency,
                isSuccess = false,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    suspend fun dispatchEstimateWebhook(
        webhook: WebhookConfig,
        estimate: EstimateResult
    ): WebhookDeliveryLog {
        val payload = JSONObject().apply {
            put("event", "ESTIMATE_CREATED")
            put("timestamp", System.currentTimeMillis())
            put("app", "C520X Dispatch")
            put("title", estimate.title)
            put("trade", estimate.trade.displayName)
            put("targetCost", estimate.targetCost)
            put("minCost", estimate.totalMinCost)
            put("maxCost", estimate.totalMaxCost)
            put("laborCost", estimate.laborCost)
            put("materialCost", estimate.materialCost)
            put("estimatedDays", estimate.estimatedDays)
            put("modelUsed", estimate.modelUsed)
            put("styleUsed", estimate.styleUsed)
            put("summary", estimate.scopeSummary)
        }.toString(2)
        return testWebhook(
            webhookName = webhook.name,
            url = webhook.url,
            httpMethod = webhook.httpMethod,
            authHeader = webhook.authHeader,
            customHeadersJson = webhook.customHeadersJson,
            payloadJson = payload
        )
    }

    suspend fun dispatchQuoteToPartner(
        partner: PartnerType,
        clientName: String,
        projectTitle: String,
        quoteAmount: Double,
        proposalText: String,
        partnerWebhookUrl: String
    ): WebhookDeliveryLog {
        val payload = JSONObject().apply {
            put("partner", partner.name)
            put("brandTag", partner.brandTag)
            put("event", "DISPATCH_QUOTE_TO_PRO_NETWORK")
            put("clientName", clientName)
            put("projectTitle", projectTitle)
            put("quoteAmount", quoteAmount)
            put("currency", "USD")
            put("formattedProposal", proposalText)
            put("sentAt", System.currentTimeMillis())
        }.toString(2)
        val targetUrl = if (partnerWebhookUrl.isNotBlank()) partnerWebhookUrl else "https://pro-dispatch-gateway.tradehub.io/${partner.name.lowercase()}/quote"
        return testWebhook(
            webhookName = "${partner.displayName} Dispatcher",
            url = targetUrl,
            httpMethod = "POST",
            authHeader = "Bearer pro_partner_tok_9981",
            customHeadersJson = "{\"Content-Type\":\"application/json\", \"X-Partner-Route\":\"${partner.name}\"}",
            payloadJson = payload
        )
    }
}
