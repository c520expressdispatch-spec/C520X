package com.example.data

/**
 * Data structures supporting the full-scale Contractor Marketplace Platform Architecture:
 * - Live Gemini Screen Share with Sensitive Data Guardrails (OCR Bounding Box Redaction)
 * - Multi-Model AI Agent Gateway (Gemini 2.0 + OpenAI GPT-4o)
 * - Push-to-Talk (PTT) Walkie-Talkie for Field Crews & Techs
 * - Stripe Connect 80/20 Escrow Clearing Model
 * - Industry Clones (Thumbtack Pro, Housecall Pro, TaskRabbit, Yelp)
 */

data class GeminiScreenShareState(
    val isActive: Boolean = false,
    val subjectTitle: String = "Commercial Blueprint E-102 (Highland Plaza)",
    val privacyFilterActive: Boolean = true,
    val detectedSensitiveCount: Int = 4, // 2 Credit Cards, 1 SSN, 1 Password
    val frameRateFps: Double = 1.5,
    val sfuLatencyMs: Int = 94,
    val resolution: String = "1080p @ 1.5 FPS (WebRTC Opus)",
    val aiLiveFeedback: String = "Gemini 2.0 Live: Blueprint E-102 verified. 240V 50A sub-panel load matches NEC Art. 210. Privacy Shield active: 4 sensitive PII/PCI bounding boxes masked prior to stream.",
    val isAnalyzing: Boolean = false
)

data class PttTransmissionRecord(
    val id: String,
    val speaker: String,
    val role: String,
    val channel: String,
    val timeAgo: String,
    val durationSec: Int,
    val transcript: String
)

data class PttWalkieTalkieState(
    val isTransmitting: Boolean = false,
    val activeChannel: String = "Channel 1: Electrical Crew Tucson",
    val latencyMs: Int = 112,
    val codec: String = "WebRTC Opus 24kbps Half-Duplex",
    val activeSpeaker: String? = null,
    val availableChannels: List<String> = listOf(
        "Channel 1: Electrical Crew Tucson",
        "Channel 2: HVAC & Plumbing Field Ops",
        "Channel 3: Heavy Logistics & DAT Dispatch",
        "Channel 4: Subcontractor AI Voice Assistant"
    ),
    val transmissions: List<PttTransmissionRecord> = listOf(
        PttTransmissionRecord(
            id = "PTT-101",
            speaker = "Marcus Vance (Lead Tech)",
            role = "Lead Tech",
            channel = "Channel 1: Electrical Crew Tucson",
            timeAgo = "3m ago",
            durationSec = 4,
            transcript = "Main feeder pull completed on Building B. Moving crew to breaker tie-ins now."
        ),
        PttTransmissionRecord(
            id = "PTT-102",
            speaker = "Dispatch Central",
            role = "Dispatcher",
            channel = "Channel 1: Electrical Crew Tucson",
            timeAgo = "5m ago",
            durationSec = 6,
            transcript = "Copy that Marcus. Inspector has confirmed arrival window between 14:00 and 15:00."
        ),
        PttTransmissionRecord(
            id = "PTT-103",
            speaker = "Elena Torres (Foreman)",
            role = "HVAC Lead",
            channel = "Channel 2: HVAC & Plumbing Field Ops",
            timeAgo = "12m ago",
            durationSec = 5,
            transcript = "Rooftop crane lift for the 10-ton Trane condenser is cleared with building security."
        )
    )
)

data class TierEstimateOption(
    val tierName: String, // Good, Better, Best
    val price: Double,
    val laborHours: Int,
    val materialsCost: Double,
    val warrantyYears: Int,
    val features: List<String>
)

data class TieredQuoteResult(
    val projectTitle: String,
    val squareFootage: Int,
    val goodTier: TierEstimateOption,
    val betterTier: TierEstimateOption,
    val bestTier: TierEstimateOption,
    val contractorCut80: Double,
    val platformFee20: Double
)

data class DispatchIntakeLead(
    val id: String,
    val customerName: String,
    val rawInquiry: String,
    val extractedAddress: String,
    val extractedScope: String,
    val extractedUrgency: String,
    val calendarSlotAssigned: String,
    val automatedSmsResponseSent: String,
    val status: String = "Auto-Dispatched"
)

data class ThumbtackOpportunity(
    val id: String,
    val customerName: String,
    val category: String,
    val location: String,
    val distanceMiles: Double,
    val budgetRange: String,
    val urgency: String,
    val jobDescription: String,
    val leadFeeCredits: Int = 0, // Free for pros on C520X!
    val isContacted: Boolean = false
)

data class TaskRabbitMilestoneStep(
    val id: String,
    val phaseName: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val photoProofUrl: String? = null
)

data class UserLocationState(
    val hasPermission: Boolean = false,
    val latitude: Double = 32.2226,
    val longitude: Double = -110.9747,
    val zipCode: String = "85718",
    val city: String = "Tucson, AZ",
    val matchingRadiusMiles: Double = 25.0,
    val isTrackingJobSite: Boolean = true,
    val currentJobSiteStatus: String = "Highland Plaza Commercial (4810 E Camp Lowell Dr)",
    val lastGpsPingTime: String = "Just now"
)

data class GoogleAiStudioConfig(
    val selectedModel: String = "gemini-3.5-flash",
    val customApiKey: String = "",
    val isConnected: Boolean = true,
    val totalTokensProcessed: Long = 124800,
    val monthlyApiCostEstimate: Double = 0.58,
    val systemDirective: String = "You are C520X Master AI Engine. Enforce the 80/20 contractor escrow split, auto-audit electrical panel photos for 2026 NEC compliance, and dispatch high-priority emergency leads instantly.",
    val autoAuditSitePhotos: Boolean = true,
    val autoDispatchClosestTech: Boolean = true,
    val autoGenerateQuotes: Boolean = true,
    val autoEscrowSplitValidation: Boolean = true,
    val liveInspectionStatus: String = "Online • Master Backend Active on Device Install"
)

data class AiStudioExecutionLog(
    val id: String,
    val timestamp: String,
    val triggerType: String,
    val modelUsed: String,
    val promptSummary: String,
    val outputSummary: String,
    val latencyMs: Long,
    val status: String
)
