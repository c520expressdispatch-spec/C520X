package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DispatchJobItem
import com.example.data.models.DispatchJobStatus
import com.example.data.models.OperationsDashboardMetrics
import com.example.data.models.PartnerType
import com.example.data.models.ServiceCategoryPerformance
import com.example.ui.components.C520XBrandBadge
import com.example.ui.components.NotificationSettingsDialog
import com.example.ui.components.PartnerBadge
import com.example.ui.theme.CanyonCobalt
import com.example.ui.theme.CanyonNavyBorder
import com.example.ui.theme.CanyonNavyCard
import com.example.ui.theme.CanyonNavyDark
import com.example.ui.theme.CanyonNavyElevated
import com.example.ui.theme.CanyonOrange
import com.example.ui.theme.CanyonOrangeLight
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SafetyGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.ProTradeViewModel

@Composable
fun DashboardScreen(
    viewModel: com.example.ui.MainViewModel,
    modifier: Modifier = Modifier
) {
    val proTradeViewModel: ProTradeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    DashboardScreen(
        viewModel = proTradeViewModel,
        modifier = modifier
    )
}

@Composable
fun DashboardScreen(
    viewModel: ProTradeViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.operationsMetrics.collectAsState()
    val categories by viewModel.serviceCategories.collectAsState()
    val jobs by viewModel.dispatchJobs.collectAsState()
    val partnerConfigs by viewModel.partnerConfigs.collectAsState()
    val leads by viewModel.leads.collectAsState()
    val tradeFilter by viewModel.selectedTradeFilter.collectAsState()
    val statusFilter by viewModel.selectedStatusFilter.collectAsState()

    val isNotificationSettingsOpen by viewModel.isNotificationSettingsOpen.collectAsState()

    var assigningJobId by remember { mutableStateOf<String?>(null) }
    var selectedJobForStatusChange by remember { mutableStateOf<DispatchJobItem?>(null) }

    if (isNotificationSettingsOpen) {
        NotificationSettingsDialog(
            viewModel = viewModel,
            onDismissRequest = { viewModel.closeNotificationSettings() }
        )
    }

    val filteredJobs = jobs.filter { job ->
        val matchTrade = tradeFilter == null || job.serviceTrade.equals(tradeFilter, ignoreCase = true)
        val matchStatus = statusFilter == null || job.status == statusFilter
        matchTrade && matchStatus
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanyonNavyDark)
            .testTag("operations_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                C520XBrandBadge(height = 36.dp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = CanyonOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CanyonOrange.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(MintGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "DISPATCH LIVE",
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.openNotificationSettings() },
                        modifier = Modifier
                            .size(34.dp)
                            .background(CanyonNavyElevated, CircleShape)
                            .border(1.dp, CanyonOrange.copy(alpha = 0.5f), CircleShape)
                            .testTag("open_notification_settings_button")
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = "Alert Settings",
                            tint = CanyonOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        item {
            OperationsKpiMatrix(metrics = metrics)
        }

        item {
            Text(
                text = "5 CONNECTED PLATFORMS",
                color = TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PartnerType.entries) { partner ->
                    val partnerLeadsCount = leads.count { it.partner == partner }
                    Surface(
                        modifier = Modifier.clickable { viewModel.selectTab(AppNavTab.LEADS) },
                        color = CanyonNavyCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, partner.badgeColor.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(partner.badgeColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = partner.brandTag,
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$partnerLeadsCount",
                                color = CanyonOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISPATCH & JOB BOARD (${filteredJobs.size})",
                    color = TextSubtle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (tradeFilter != null || statusFilter != null) {
                    Row(
                        modifier = Modifier.clickable {
                            viewModel.filterByTrade(null)
                            viewModel.filterByStatus(null)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = CanyonOrange, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Reset Filters", color = CanyonOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val trades = listOf("All Trades", "Plumbing", "Electrical", "HVAC", "Carpentry", "Painting", "Handyman", "Roofing")
                items(trades) { t ->
                    val isSelected = (t == "All Trades" && tradeFilter == null) || (tradeFilter == t)
                    Surface(
                        modifier = Modifier.clickable {
                            viewModel.filterByTrade(if (t == "All Trades") null else t)
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) CanyonOrange else CanyonNavyElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CanyonOrange else CanyonNavyBorder)
                    ) {
                        Text(
                            text = t,
                            color = if (isSelected) TextWhite else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val statuses = listOf(null) + DispatchJobStatus.entries
                items(statuses) { s ->
                    val isSelected = statusFilter == s
                    val label = s?.displayName ?: "All Statuses"
                    Surface(
                        modifier = Modifier.clickable { viewModel.filterByStatus(s) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) CanyonCobalt else CanyonNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CanyonCobalt else CanyonNavyBorder)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) TextWhite else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        if (filteredJobs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CanyonNavyCard)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No dispatch jobs match the current filters.", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(filteredJobs) { job ->
                DispatchJobCard(
                    job = job,
                    onAssignTech = { assigningJobId = job.id },
                    onUpdateStatus = { selectedJobForStatusChange = job }
                )
            }
        }

        item {
            Text(
                text = "PERFORMANCE BY SERVICE TRADE",
                color = TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(categories) { cat ->
            ServiceCategoryCard(cat = cat)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CanyonNavyElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, CanyonOrange.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = CanyonOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Automated AI Bidding Available",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Instantly run material takeoffs, itemized scope pricing, and dispatch quotes with Gemini 2.5 Flash for any incoming partner inquiry.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.selectTab(AppNavTab.ESTIMATOR) },
                        colors = ButtonDefaults.buttonColors(containerColor = CanyonOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Launch AI Estimator Engine", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    assigningJobId?.let { jId ->
        var techInput by remember { mutableStateOf("") }
        val techSuggestions = listOf("Master Dave Miller", "Electrician Ray Vasquez", "Alex Rivera (Tech #2)", "Crew Alpha Electrics", "Master Carpenter Joe")
        AlertDialog(
            onDismissRequest = { assigningJobId = null },
            title = { Text("Assign Technician to $jId", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = techInput,
                        onValueChange = { techInput = it },
                        label = { Text("Technician or Crew Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CanyonOrange,
                            unfocusedBorderColor = CanyonNavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    Text("Quick Select:", color = TextMuted, fontSize = 11.sp)
                    techSuggestions.forEach { suggestion ->
                        Text(
                            text = "• $suggestion",
                            color = CanyonCobalt,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable { techInput = suggestion }
                                .padding(vertical = 2.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (techInput.isNotBlank()) {
                            viewModel.assignTechToJob(jId, techInput)
                            assigningJobId = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CanyonOrange)
                ) {
                    Text("Confirm Assignment", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { assigningJobId = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CanyonNavyCard
        )
    }

    selectedJobForStatusChange?.let { job ->
        AlertDialog(
            onDismissRequest = { selectedJobForStatusChange = null },
            title = { Text("Update Status: ${job.id}", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select new operational status:", color = TextMuted, fontSize = 12.sp)
                    DispatchJobStatus.entries.forEach { status ->
                        val isCurrent = job.status == status
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateJobDispatchStatus(job.id, status)
                                    selectedJobForStatusChange = null
                                },
                            color = if (isCurrent) CanyonOrange else CanyonNavyElevated,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) CanyonOrange else CanyonNavyBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = status.displayName,
                                    color = if (isCurrent) TextWhite else TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isCurrent) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedJobForStatusChange = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CanyonNavyCard
        )
    }
}

@Composable
fun OperationsKpiMatrix(metrics: OperationsDashboardMetrics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("operations_kpi_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CanyonNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CanyonNavyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "LIVE DISPATCH & REVENUE PIPELINE",
                color = CanyonOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiPill(
                    title = "Active Dispatch",
                    value = "${metrics.activeJobsCount} Jobs",
                    subtitle = "${metrics.totalTrackedJobs} Total Tracked",
                    color = CanyonOrange,
                    modifier = Modifier.weight(1f)
                )
                KpiPill(
                    title = "Revenue Pipeline",
                    value = "$${"%,.0f".format(metrics.pipelineValue)}",
                    subtitle = "Quoted / Scheduled",
                    color = SafetyGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiPill(
                    title = "Billed Revenue",
                    value = "$${"%,.0f".format(metrics.billedRevenue)}",
                    subtitle = "Work in Progress",
                    color = CanyonCobalt,
                    modifier = Modifier.weight(1f)
                )
                KpiPill(
                    title = "Collected Cash",
                    value = "$${"%,.0f".format(metrics.collectedPayments)}",
                    subtitle = "${metrics.completionRatePercent}% Closed",
                    color = MintGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Completion Rate", color = TextMuted, fontSize = 11.sp)
                Text("${metrics.completionRatePercent}%", color = MintGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (metrics.completionRatePercent / 100f).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MintGreen,
                trackColor = CanyonNavyElevated
            )
        }
    }
}

@Composable
fun KpiPill(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = CanyonNavyElevated,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, color = TextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = TextSubtle, fontSize = 9.sp)
        }
    }
}

@Composable
fun DispatchJobCard(
    job: DispatchJobItem,
    onAssignTech: () -> Unit,
    onUpdateStatus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CanyonNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CanyonNavyBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PartnerBadge(partner = job.partnerSource)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = job.id,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    modifier = Modifier.clickable { onUpdateStatus() },
                    color = job.status.badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, job.status.badgeColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(job.status.badgeColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = job.status.displayName,
                            color = job.status.badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = job.clientName,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = job.description,
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = CanyonOrange, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(job.serviceAddress, color = TextMuted, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = CanyonNavyBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onAssignTech() }
                        .background(CanyonNavyElevated, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = CanyonCobalt, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.assignedTech,
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Pipeline: $${"%,.0f".format(job.estimatedPipelineValue)}",
                        color = SafetyGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (job.billedAmount > 0) {
                        Text(
                            text = "Billed: $${"%,.0f".format(job.billedAmount)}",
                            color = MintGreen,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCategoryCard(cat: ServiceCategoryPerformance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CanyonNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CanyonNavyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cat.category,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${cat.completedJobs} Closed • ${cat.activeJobs} Active",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${"%,.0f".format(cat.billedRevenue)} Billed",
                    color = MintGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${"%,.0f".format(cat.pipelineValue)} Pipeline",
                    color = SafetyGold,
                    fontSize = 10.sp
                )
            }
        }
    }
}
