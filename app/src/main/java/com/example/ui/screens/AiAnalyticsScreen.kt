package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProjectTimelinePrediction
import com.example.data.ResourceAllocationStrategy
import com.example.data.RiskFactor
import com.example.data.RiskSeverity
import com.example.ui.MainViewModel
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

enum class AiAnalyticsFilter(val label: String) {
    OVERVIEW("Overview"),
    TIMELINES("Timelines"),
    RISKS_DELAYS("Risks & Delays"),
    RESOURCES("Resource Plan"),
    OVERRUNS("Cost Overruns"),
    WHAT_IF("What-If Simulator")
}

@Composable
fun AiAnalyticsScreen(viewModel: MainViewModel) {
    val report by viewModel.aiPredictiveReport.collectAsState()
    val isAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    var selectedFilter by remember { mutableStateOf(AiAnalyticsFilter.OVERVIEW) }
    var selectedTradeFilter by remember { mutableStateOf("All Trades") }
    var simulatedOvertimeReduction by remember { mutableStateOf(15f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp)
            .testTag("ai_analytics_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Model Engine Header
        item {
            Spacer(modifier = Modifier.height(6.dp))
            AiEngineHeaderCard(
                isLiveGemini = report.isLiveGeminiCall,
                isAnalyzing = isAnalyzing,
                lastUpdated = report.lastUpdated,
                onRefresh = { viewModel.refreshAiAnalytics() }
            )
        }

        // Executive AI Summary Insight
        item {
            AiExecutiveSummaryCard(
                summaryText = report.summaryInsight,
                isLive = report.isLiveGeminiCall
            )
        }

        // Key KPI Scorecards
        item {
            AiScorecardRow(
                onTimeProb = report.overallOnTimeProbability,
                riskCount = report.riskFactors.count { !it.isMitigated },
                criticalCount = report.riskFactors.count { it.severity == RiskSeverity.CRITICAL && !it.isMitigated },
                preventedSavings = report.overrunForecast.preventedSavings,
                laborScore = report.overrunForecast.laborEfficiencyScore
            )
        }

        // Filter tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AiAnalyticsFilter.values()) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
                    )
                }
            }
        }

        // Content based on filter
        when (selectedFilter) {
            AiAnalyticsFilter.OVERVIEW -> {
                item {
                    SectionHeaderWithAction(
                        title = "Projected Timelines & Velocity",
                        actionLabel = "View All",
                        onAction = { selectedFilter = AiAnalyticsFilter.TIMELINES }
                    )
                }
                items(report.timelinePredictions.take(3)) { prediction ->
                    TimelinePredictionCard(prediction = prediction)
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    SectionHeaderWithAction(
                        title = "Delay Risk Radar",
                        actionLabel = "Mitigate",
                        onAction = { selectedFilter = AiAnalyticsFilter.RISKS_DELAYS }
                    )
                }
                items(report.riskFactors.take(2)) { risk ->
                    RiskFactorCard(
                        risk = risk,
                        onMitigate = { viewModel.mitigateRisk(risk.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    SectionHeaderWithAction(
                        title = "Optimal Resource Allocation",
                        actionLabel = "Rebalance",
                        onAction = { selectedFilter = AiAnalyticsFilter.RESOURCES }
                    )
                }
                items(report.resourceStrategies.take(2)) { strategy ->
                    ResourceStrategyCard(
                        strategy = strategy,
                        onApply = { viewModel.applyResourceStrategy(strategy) }
                    )
                }
            }

            AiAnalyticsFilter.TIMELINES -> {
                item {
                    TradeVelocityIndexBar(tradeIndices = report.tradeVelocityIndices)
                }
                item {
                    Text(
                        text = "Real-Time Completion Forecasts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                items(report.timelinePredictions) { prediction ->
                    TimelinePredictionCard(prediction = prediction)
                }
            }

            AiAnalyticsFilter.RISKS_DELAYS -> {
                item {
                    RiskOverviewSummaryCard(
                        riskFactors = report.riskFactors,
                        onMitigateAll = {
                            report.riskFactors.forEach { viewModel.mitigateRisk(it.id) }
                            viewModel.showNotice("All proactive mitigations dispatched to field technicians")
                        }
                    )
                }
                items(report.riskFactors) { risk ->
                    RiskFactorCard(
                        risk = risk,
                        onMitigate = { viewModel.mitigateRisk(risk.id) }
                    )
                }
            }

            AiAnalyticsFilter.RESOURCES -> {
                item {
                    ResourceEfficiencySummaryCard(
                        strategies = report.resourceStrategies,
                        onAutoOptimize = {
                            viewModel.autoOptimizeSchedule()
                        }
                    )
                }
                items(report.resourceStrategies) { strategy ->
                    ResourceStrategyCard(
                        strategy = strategy,
                        onApply = { viewModel.applyResourceStrategy(strategy) }
                    )
                }
            }

            AiAnalyticsFilter.OVERRUNS -> {
                item {
                    CostOverrunDetailCard(overrunForecast = report.overrunForecast)
                }
                item {
                    TradeVelocityIndexBar(tradeIndices = report.tradeVelocityIndices)
                }
            }

            AiAnalyticsFilter.WHAT_IF -> {
                item {
                    WhatIfSimulationCard(
                        currentProb = report.overallOnTimeProbability,
                        simulatedReduction = simulatedOvertimeReduction,
                        onReductionChange = { simulatedOvertimeReduction = it },
                        onApplySimulation = {
                            viewModel.applySimulation(simulatedOvertimeReduction)
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AiEngineHeaderCard(
    isLiveGemini: Boolean,
    isAnalyzing: Boolean,
    lastUpdated: String,
    onRefresh: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Navy800),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI Predictive Analytics",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isLiveGemini) StatusGreen.copy(alpha = 0.2f) else AccentCyan.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isLiveGemini) "Gemini 3.5 Flash" else "Predictive Model",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLiveGemini) StatusGreen else AccentCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Historical modeling • Synchronized: $lastUpdated",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onRefresh,
                enabled = !isAnalyzing,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .testTag("refresh_ai_analytics_button")
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = OrangePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Run AI Analysis",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiExecutiveSummaryCard(summaryText: String, isLive: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLive) Navy900 else DarkSurfaceElevated
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isLive) AccentCyan.copy(alpha = 0.4f) else DarkBorder
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = StatusAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Operational Intelligence Forecast",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = summaryText,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun AiScorecardRow(
    onTimeProb: Int,
    riskCount: Int,
    criticalCount: Int,
    preventedSavings: Double,
    laborScore: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScorecardItem(
            label = "On-Time Prob.",
            value = "$onTimeProb%",
            statusColor = if (onTimeProb >= 90) StatusGreen else StatusAmber,
            icon = Icons.Default.Speed,
            modifier = Modifier.weight(1f)
        )
        ScorecardItem(
            label = "Active Risks",
            value = "$riskCount (${criticalCount} crit)",
            statusColor = if (criticalCount > 0) StatusRed else StatusAmber,
            icon = Icons.Default.CrisisAlert,
            modifier = Modifier.weight(1f)
        )
        ScorecardItem(
            label = "Overrun Shield",
            value = "$${preventedSavings.toInt()}",
            statusColor = AccentCyan,
            icon = Icons.Default.Shield,
            modifier = Modifier.weight(1f)
        )
        ScorecardItem(
            label = "Labor Score",
            value = "$laborScore/100",
            statusColor = OrangePrimary,
            icon = Icons.Default.Engineering,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ScorecardItem(
    label: String,
    value: String,
    statusColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurface,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TimelinePredictionCard(prediction: ProjectTimelinePrediction) {
    val statusColor = when (prediction.status) {
        "Ahead of Schedule" -> StatusGreen
        "On Track" -> StatusGreen
        "At Risk" -> StatusAmber
        else -> StatusRed
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = prediction.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Confidence: ${prediction.confidencePercent}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = prediction.jobTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${prediction.clientName} • Trade: ${prediction.trade}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Planned Window",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = prediction.scheduledDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "AI Projected Delivery",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = prediction.predictedCompletionDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { prediction.confidencePercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = statusColor,
                trackColor = DarkBorder,
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Reasoning: ${prediction.rationale}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun RiskFactorCard(
    risk: RiskFactor,
    onMitigate: () -> Unit
) {
    val severityColor = when (risk.severity) {
        RiskSeverity.CRITICAL -> StatusRed
        RiskSeverity.HIGH -> OrangePrimary
        RiskSeverity.MODERATE -> StatusAmber
        RiskSeverity.LOW -> StatusGreen
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (risk.isMitigated) StatusGreen.copy(alpha = 0.4f) else severityColor.copy(alpha = 0.3f)
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = severityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${risk.severity.label.uppercase()} RISK",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = severityColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = risk.category,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Text(
                    text = "${risk.delayProbabilityPercent}% probability",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = severityColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = risk.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Target: ${risk.affectedJobTitle} • Est. Delay: +${risk.estimatedDelayHours}h",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Root Cause:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = risk.rootCause,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AI Mitigation Strategy:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )
                    Text(
                        text = risk.recommendedMitigation,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (risk.isMitigated) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mitigation Protocol Active ✓",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusGreen
                    )
                }
            } else {
                Button(
                    onClick = onMitigate,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .testTag("apply_mitigation_${risk.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Deploy AI Mitigation",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ResourceStrategyCard(
    strategy: ResourceAllocationStrategy,
    onApply: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strategy.techName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${strategy.role} • ${strategy.primaryTrade}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (strategy.currentLoadPercent > 85) StatusRed.copy(alpha = 0.2f) else StatusGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${strategy.currentLoadPercent}% Load",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (strategy.currentLoadPercent > 85) StatusRed else StatusGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { strategy.currentLoadPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (strategy.currentLoadPercent > 85) StatusRed else AccentCyan,
                trackColor = DarkBorder,
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Optimal Strategy: ${strategy.recommendedAction}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+${strategy.efficiencyGainPercent}% Efficiency • Prevents $${strategy.hourlyCostSavings.toInt()} Overtime",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusGreen
                )

                OutlinedButton(
                    onClick = onApply,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(text = "Apply Rebalance", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun TradeVelocityIndexBar(tradeIndices: Map<String, Double>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Historical Trade Velocity (Avg Hours per Project)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tradeIndices.toList()) { (trade, hours) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceElevated,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = trade,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${hours}h",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiskOverviewSummaryCard(
    riskFactors: List<RiskFactor>,
    onMitigateAll: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Navy800),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Automated Risk Radar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${riskFactors.size} total operational vulnerabilities detected",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Button(
                    onClick = onMitigateAll,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "Mitigate All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ResourceEfficiencySummaryCard(
    strategies: List<ResourceAllocationStrategy>,
    onAutoOptimize: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Navy800),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Crew Capacity Rebalancing",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Minimizes overtime overhead ($125/hr billable penalty)",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Button(
                    onClick = onAutoOptimize,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "Auto-Optimize", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CostOverrunDetailCard(overrunForecast: com.example.data.OverrunForecast) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Financial Overrun Forecast & Variance",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Pipeline at Risk",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$${overrunForecast.totalPipelineAtRisk.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusAmber
                    )
                }
                Column {
                    Text(
                        text = "Projected Variance",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "+$${overrunForecast.projectedCostVariance.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusRed
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Savings Shield",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$${overrunForecast.preventedSavings.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Actionable Overrun Mitigation Checklist:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• Cluster 6 commercial downtown service calls within 3-mile radius to reduce transit overhead by 4.5h.\n• Enforce pre-approval on overtime shifts exceeding 40h/week across field subcontractors.\n• Lock bulk material pricing for high-voltage commercial electrical retrofits.",
                fontSize = 10.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
fun WhatIfSimulationCard(
    currentProb: Int,
    simulatedReduction: Float,
    onReductionChange: (Float) -> Unit,
    onApplySimulation: () -> Unit
) {
    val projectedProb = (currentProb + (simulatedReduction * 0.4f)).toInt().coerceAtMost(99)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "What-If Capacity & Overrun Simulation",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Simulate reallocating subcontractor hours to reduce peak bottleneck shifts:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reallocated Crew Hours: ${simulatedReduction.toInt()} hrs",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OrangePrimary
                )
                Text(
                    text = "Projected On-Time: $projectedProb%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusGreen
                )
            }

            Slider(
                value = simulatedReduction,
                onValueChange = onReductionChange,
                valueRange = 0f..40f,
                steps = 7,
                colors = SliderDefaults.colors(
                    thumbColor = OrangePrimary,
                    activeTrackColor = OrangePrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onApplySimulation,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Commit Simulated Roster Adjustment ($projectedProb% Success)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SectionHeaderWithAction(
    title: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = actionLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = OrangePrimary,
            modifier = Modifier.clickable { onAction() }
        )
    }
}
