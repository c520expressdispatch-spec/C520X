package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "estimates")
data class EstimateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val tradeName: String,
    val modelUsed: String,
    val styleUsed: String,
    val totalMinCost: Double,
    val totalMaxCost: Double,
    val targetCost: Double,
    val laborCost: Double,
    val materialCost: Double,
    val permitsCost: Double,
    val equipmentCost: Double,
    val overheadAndProfit: Double,
    val contingencyAmount: Double,
    val estimatedDays: Int,
    val scopeSummary: String,
    val lineItemsJson: String,
    val milestonesJson: String,
    val rawProposal: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientName: String,
    val clientLocation: String,
    val partnerType: String,
    val tradeName: String,
    val projectTitle: String,
    val projectDescription: String,
    val customerBudget: String,
    val urgency: String,
    val status: String,
    val quoteSentAmount: Double? = null,
    val receivedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "webhooks")
data class WebhookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val url: String,
    val triggerEvent: String,
    val httpMethod: String,
    val authHeader: String,
    val customHeadersJson: String,
    val isEnabled: Boolean = true,
    val lastStatusCode: Int? = null,
    val lastLatencyMs: Long? = null,
    val lastTriggeredAt: Long? = null
)

@Entity(tableName = "webhook_logs")
data class WebhookLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val webhookName: String,
    val url: String,
    val event: String,
    val payloadJson: String,
    val statusCode: Int,
    val responseBody: String,
    val latencyMs: Long,
    val isSuccess: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "partner_configs")
data class PartnerConfigEntity(
    @PrimaryKey val partnerKey: String,
    val isConnected: Boolean,
    val apiKey: String,
    val webhookUrl: String,
    val rating: Double,
    val reviewsCount: Int,
    val activeLeadsCount: Int,
    val customNotes: String
)

object JsonHelper {
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}
