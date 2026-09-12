package com.example.data.models

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AngiRed
import com.example.ui.theme.HouzzGreen
import com.example.ui.theme.TaskRabbitGreen
import com.example.ui.theme.ThumbtackBlue
import com.example.ui.theme.YelpRed

enum class PartnerType(
    val displayName: String,
    val brandTag: String,
    val badgeColor: Color,
    val feeDescription: String,
    val defaultFeePercent: Double,
    val specialties: String
) {
    THUMBTACK("Thumbtack Pro", "THUMBTACK", ThumbtackBlue, "10-12% Lead Contact Fee", 11.0, "Direct Client Requests & Instant Bookings"),
    TASKRABBIT("TaskRabbit", "TASKRABBIT", TaskRabbitGreen, "15% Platform Service Fee", 15.0, "Hourly Gigs, Mounting, Moving & Fast Tasks"),
    ANGI("Angi Pro (Angie's List)", "ANGI", AngiRed, "Annual Tier + Pay-Per-Lead ($35-$90)", 8.5, "Major Remodeling, Roofing, Certified Contractors"),
    YELP("Yelp for Business", "YELP", YelpRed, "CPC Ad Model ($5-$15/click)", 7.0, "Local Reputation, Direct Messaging & Quote Requests"),
    HOUZZ("Houzz Pro", "HOUZZ", HouzzGreen, "Pro SaaS Tier ($99-$199/mo)", 5.0, "High-End Architectural, 3D Rooms & Luxury Builds")
}

enum class EstimatorModel(
    val id: String,
    val displayName: String,
    val apiModelKey: String,
    val description: String,
    val speedTag: String
) {
    GEMINI_3_5_FLASH(
        "gemini_3_5_flash",
        "Gemini 3.5 Flash",
        "gemini-3.5-flash",
        "Ultra-fast itemized contractor cost calculations and material takeoff",
        "Lightning (< 1.2s)"
    ),
    GEMINI_3_1_PRO(
        "gemini_3_1_pro",
        "Gemini 3.1 Pro",
        "gemini-3.1-pro-preview",
        "Deep structural engineering reasoning, permit code compliance and risk analysis",
        "High Reasoning"
    ),
    GEMINI_FLASH_LITE(
        "gemini_flash_lite",
        "Gemini Flash Lite",
        "gemini-3.1-flash-lite-preview",
        "Lightweight pocket estimation for fast on-site ballparks",
        "Instant (< 0.8s)"
    ),
    GEMINI_THINKING(
        "gemini_thinking",
        "Gemini Thinking Mode",
        "gemini-3.1-pro-preview",
        "Step-by-step contractor cost decomposition with detailed chain-of-thought",
        "Deep Analysis"
    ),
    CUSTOM_API(
        "custom_api",
        "Custom API / BYO Model",
        "custom",
        "Connect to your own custom OpenAI/Ollama/vLLM or Enterprise API endpoint",
        "Custom Host"
    )
}

enum class EstimatorStyle(
    val displayName: String,
    val subtitle: String,
    val focusArea: String
) {
    CONTRACTOR_PRECISION(
        "Contractor Precision",
        "Itemized labor hours, materials, permits & markup",
        "Exact breakdown for formal bids, subcontractors & itemized invoices"
    ),
    CONSUMER_BALLPARK(
        "Homeowner Ballpark",
        "Good / Better / Best tiered ranges with friendly terms",
        "Clear low/mid/high range suited for initial client consultations"
    ),
    INSURANCE_XACTIMATE(
        "Insurance / Xactimate Style",
        "Unit costs, code upgrades, depreciation & regional index",
        "Calibrated for claims, insurance adjusters & disaster restorations"
    ),
    LUXURY_ARCHITECTURAL(
        "Luxury Architectural / Houzz",
        "High-end bespoke finishes, master craftsmanship & premium grade",
        "Tailored for luxury estates, custom architectural fixtures & designer remodels"
    ),
    COMPETITIVE_BIDDER(
        "Aggressive Market Bidder",
        "Value-engineered pricing to win Thumbtack & TaskRabbit leads",
        "Sharpened price point designed to maximize lead conversion while preserving margins"
    )
}

enum class TradeCategory(
    val displayName: String,
    val iconName: String,
    val avgHourlyRate: Int,
    val typicalUnit: String
) {
    REMODELING("Kitchen & Bath Remodel", "Kitchen", 85, "sq ft"),
    PLUMBING("Plumbing & Piping", "Plumbing", 95, "per fixture / hr"),
    ELECTRICAL("Electrical & EV / Panels", "Bolt", 100, "per circuit / hr"),
    ROOFING("Roofing & Gutters", "Roofing", 75, "per square (100 sq ft)"),
    PAINTING("Interior & Exterior Painting", "FormatPaint", 55, "sq ft wall"),
    HVAC("HVAC Heating & Cooling", "AcUnit", 90, "per ton / system"),
    FLOORING("Tile, Hardwood & Vinyl", "GridOn", 60, "sq ft"),
    CARPENTRY("Carpentry & Custom Decks", "Handyman", 70, "sq ft / custom"),
    LANDSCAPING("Landscaping & Hardscape", "Park", 50, "sq ft / project"),
    HANDYMAN("General Handyman & Assembly", "Build", 65, "per hour"),
    CONCRETE("Concrete, Masonry & Paving", "Foundation", 75, "sq ft / cu yd"),
    CUSTOM("Custom Commercial / Multi-Trade", "Architecture", 85, "custom project")
}

data class LineItem(
    val category: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val unitCost: Double,
    val totalCost: Double,
    val notes: String = ""
)

data class ProjectMilestone(
    val phase: String,
    val percentage: Int,
    val amount: Double,
    val description: String
)

data class EstimateResult(
    val title: String,
    val trade: TradeCategory,
    val modelUsed: String,
    val styleUsed: String,
    val totalMinCost: Double,
    val totalMaxCost: Double,
    val targetCost: Double,
    val laborCost: Double,
    val materialCost: Double,
    val permitsAndFeesCost: Double,
    val equipmentCost: Double,
    val overheadAndProfit: Double,
    val contingencyAmount: Double,
    val estimatedDays: Int,
    val lineItems: List<LineItem>,
    val milestones: List<ProjectMilestone>,
    val scopeSummary: String,
    val rawAiProposal: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class LeadItem(
    val id: Long = 0,
    val clientName: String,
    val clientLocation: String,
    val partner: PartnerType,
    val trade: TradeCategory,
    val projectTitle: String,
    val projectDescription: String,
    val customerBudget: String,
    val urgency: String,
    val status: String,
    val quoteSentAmount: Double? = null,
    val receivedAt: Long = System.currentTimeMillis()
)

data class WebhookConfig(
    val id: Long = 0,
    val name: String,
    val url: String,
    val triggerEvent: String,
    val httpMethod: String = "POST",
    val authHeader: String = "",
    val customHeadersJson: String = "{\"Content-Type\":\"application/json\"}",
    val isEnabled: Boolean = true,
    val lastStatusCode: Int? = null,
    val lastLatencyMs: Long? = null,
    val lastTriggeredAt: Long? = null
)

data class WebhookDeliveryLog(
    val id: Long = 0,
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

data class CustomApiConfig(
    val endpointUrl: String = "https://api.openai.com/v1/chat/completions",
    val apiKey: String = "",
    val modelIdentifier: String = "gpt-4o-mini",
    val customAuthHeader: String = "Bearer",
    val systemPromptOverride: String = ""
)

enum class DispatchJobStatus(val displayName: String, val colorHex: Long) {
    PENDING_ESTIMATE("Pending Estimate", 0xFF94A3B8),
    SCHEDULED("Scheduled", 0xFF1B64D1),
    DISPATCHED("Dispatched", 0xFFFF5E14),
    IN_PROGRESS("In Progress", 0xFFFFA726),
    COMPLETED("Completed", 0xFF10B981);

    val badgeColor: androidx.compose.ui.graphics.Color get() = androidx.compose.ui.graphics.Color(colorHex)
}

data class ServiceCategoryPerformance(
    val category: String,
    val totalJobs: Int,
    val activeJobs: Int,
    val completedJobs: Int,
    val billedRevenue: Double,
    val pipelineValue: Double
)

data class DispatchStatusOverview(
    val status: DispatchJobStatus,
    val count: Int,
    val billedAmount: Double
)

data class OperationsDashboardMetrics(
    val activeJobsCount: Int = 12,
    val pipelineValue: Double = 36370.0,
    val billedRevenue: Double = 26410.0,
    val collectedPayments: Double = 7020.0,
    val totalTrackedJobs: Int = 24,
    val completionRatePercent: Double = 16.7
)

data class DispatchJobItem(
    val id: String,
    val serviceTrade: String,
    val clientName: String,
    val serviceAddress: String,
    val status: DispatchJobStatus,
    val billedAmount: Double,
    val estimatedPipelineValue: Double,
    val assignedTech: String,
    val scheduledTime: String,
    val partnerSource: PartnerType,
    val description: String
)

enum class IncomingWebhookStatus(val displayName: String, val colorHex: Long) {
    PROCESSED("Processed", 0xFF10B981),
    PENDING("Pending Action", 0xFFFFA726),
    FAILED("Failed", 0xFFEF4444),
    ARCHIVED("Archived", 0xFF94A3B8)
}

data class IncomingWebhookEvent(
    val id: String,
    val partner: PartnerType,
    val eventType: String,
    val details: String,
    val status: IncomingWebhookStatus,
    val isImportant: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis()
)

data class PlatformNotificationSetting(
    val partner: PartnerType,
    val enabled: Boolean = true,
    val immediatePush: Boolean = true,
    val soundEnabled: Boolean = true
)

data class NotificationHistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val partner: PartnerType,
    val title: String,
    val message: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isHighPriority: Boolean = true
)

data class NotificationPreferences(
    val masterPushEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val platformSettings: Map<PartnerType, PlatformNotificationSetting> = mapOf(
        PartnerType.YELP to PlatformNotificationSetting(PartnerType.YELP, enabled = true, immediatePush = true),
        PartnerType.THUMBTACK to PlatformNotificationSetting(PartnerType.THUMBTACK, enabled = true, immediatePush = true),
        PartnerType.ANGI to PlatformNotificationSetting(PartnerType.ANGI, enabled = true, immediatePush = false),
        PartnerType.TASKRABBIT to PlatformNotificationSetting(PartnerType.TASKRABBIT, enabled = true, immediatePush = false),
        PartnerType.HOUZZ to PlatformNotificationSetting(PartnerType.HOUZZ, enabled = true, immediatePush = false)
    ),
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
    val emergencyBypassQuietHours: Boolean = true,
    val alertTone: String = "Canyon Siren (High Urgency)",
    val vibrationEnabled: Boolean = true,
    val minLeadValueThreshold: Int = 0,
    val alertHistory: List<NotificationHistoryItem> = listOf(
        NotificationHistoryItem(
            partner = PartnerType.YELP,
            title = "New Yelp High-Priority Lead",
            message = "Emergency Slab Leak ($2,400) - Canyon Crest",
            isHighPriority = true,
            timestampMs = System.currentTimeMillis() - 120_000
        ),
        NotificationHistoryItem(
            partner = PartnerType.THUMBTACK,
            title = "Thumbtack Direct Booking",
            message = "Commercial Panel Upgrade ($3,400) - Riverside",
            isHighPriority = true,
            timestampMs = System.currentTimeMillis() - 600_000
        )
    )
)
