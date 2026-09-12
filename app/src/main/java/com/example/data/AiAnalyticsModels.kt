package com.example.data

data class ProjectTimelinePrediction(
    val jobId: Int,
    val jobTitle: String,
    val trade: String,
    val clientName: String,
    val scheduledDate: String,
    val predictedCompletionDate: String,
    val varianceHours: Double, // positive = delay, negative = ahead
    val confidencePercent: Int,
    val status: String, // "On Track", "At Risk", "Critical Delay", "Ahead of Schedule"
    val rationale: String
)

enum class RiskSeverity(val label: String, val level: Int) {
    CRITICAL("Critical", 3),
    HIGH("High", 2),
    MODERATE("Moderate", 1),
    LOW("Low", 0)
}

data class RiskFactor(
    val id: String,
    val title: String,
    val category: String, // "Permits", "Material Supply", "Capacity", "Weather/Heat", "Client Access"
    val severity: RiskSeverity,
    val delayProbabilityPercent: Int,
    val estimatedDelayHours: Double,
    val affectedJobTitle: String,
    val rootCause: String,
    val recommendedMitigation: String,
    val isMitigated: Boolean = false
)

data class ResourceAllocationStrategy(
    val techName: String,
    val role: String,
    val primaryTrade: String,
    val currentLoadPercent: Int,
    val recommendedAction: String,
    val targetJobTitle: String,
    val efficiencyGainPercent: Int,
    val overtimeRisk: Boolean,
    val hourlyCostSavings: Double
)

data class OverrunForecast(
    val totalPipelineAtRisk: Double,
    val projectedCostVariance: Double,
    val projectedOvertimeHours: Double,
    val preventedSavings: Double,
    val laborEfficiencyScore: Int // 0-100
)

data class AiPredictiveReport(
    val lastUpdated: String,
    val isLiveGeminiCall: Boolean,
    val overallOnTimeProbability: Int,
    val summaryInsight: String,
    val timelinePredictions: List<ProjectTimelinePrediction>,
    val riskFactors: List<RiskFactor>,
    val resourceStrategies: List<ResourceAllocationStrategy>,
    val overrunForecast: OverrunForecast,
    val tradeVelocityIndices: Map<String, Double> // Trade -> Average Hours per Job
)
