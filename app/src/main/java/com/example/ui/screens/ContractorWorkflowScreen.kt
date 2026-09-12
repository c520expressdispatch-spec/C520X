package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CustomerBookingEntity
import com.example.data.ProjectMilestoneEntity
import com.example.data.ThumbtackOpportunity
import com.example.data.TaskRabbitMilestoneStep
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.EditContractorPlacardDialog
import com.example.ui.components.LocationPermissionCard
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ContractorWorkflowScreen(viewModel: MainViewModel) {
    val bookings by viewModel.customerBookings.collectAsState()
    val allMilestones by viewModel.projectMilestones.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    // Active contractor claimed/booked jobs
    val activeJobs = remember(bookings) {
        bookings.filter { it.status != "Open For Bids" && it.status != "Cancelled" }
    }

    var selectedBookingId by remember(activeJobs) {
        mutableIntStateOf(activeJobs.firstOrNull()?.id ?: 0)
    }

    val currentJob = activeJobs.find { it.id == selectedBookingId } ?: activeJobs.firstOrNull()

    // Dialog state for adding a milestone
    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var newMilestoneTitle by remember { mutableStateOf("") }
    var newMilestoneDesc by remember { mutableStateOf("") }
    var newMilestoneTargetDate by remember { mutableStateOf("In 3 Days") }
    var newMilestoneDrawAmount by remember { mutableStateOf("500") }

    // Dialog state for field notes & progress photos
    var showFieldNotesDialog by remember { mutableStateOf(false) }
    var fieldNotesInput by remember { mutableStateOf("") }

    // 4 Modular Workflow Views: 0=Active Milestones, 1=Thumbtack Leads, 2=TaskRabbit Checklists, 3=Stripe 80/20 Escrow
    var activeWorkflowTab by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("contractor_workflow_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 860.dp)
        ) {
            // Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Handyman,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Contractor Workflow",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Pro Market Project Management & Milestones",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Switch to Live Marketboard
                        OutlinedButton(
                            onClick = { activeWorkflowTab = 1 },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                            border = ButtonDefaults.outlinedButtonBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(OrangePrimary)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("goto_marketboard_button")
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Marketboard", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 5-Tab Navigation: Milestones, Live Marketboard, Thumbtack Leads, TaskRabbit Checklists, Stripe 80/20 Escrow
                    ScrollableTabRow(
                        selectedTabIndex = activeWorkflowTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = OrangePrimary,
                        edgePadding = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .testTag("workflow_sub_tabs")
                    ) {
                        Tab(
                            selected = activeWorkflowTab == 0,
                            onClick = { activeWorkflowTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(14.dp), tint = OrangePrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Active Milestones", fontSize = 11.sp, fontWeight = if (activeWorkflowTab == 0) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                        Tab(
                            selected = activeWorkflowTab == 1,
                            onClick = { activeWorkflowTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(14.dp), tint = OrangePrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Live Marketboard", fontSize = 11.sp, fontWeight = if (activeWorkflowTab == 1) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                        Tab(
                            selected = activeWorkflowTab == 2,
                            onClick = { activeWorkflowTab = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusBlue)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Thumbtack Pro Leads", fontSize = 11.sp, fontWeight = if (activeWorkflowTab == 2) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                        Tab(
                            selected = activeWorkflowTab == 3,
                            onClick = { activeWorkflowTab = 3 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusGreen)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TaskRabbit Checklists", fontSize = 11.sp, fontWeight = if (activeWorkflowTab == 3) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                        Tab(
                            selected = activeWorkflowTab == 4,
                            onClick = { activeWorkflowTab = 4 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(14.dp), tint = OrangeDark)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Stripe 80/20 Escrow", fontSize = 11.sp, fontWeight = if (activeWorkflowTab == 4) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                    }

                    // Active Projects Horizontal Selector Chips
                    if (activeWorkflowTab == 0 && activeJobs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Active Contract Projects (${activeJobs.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activeJobs.forEach { job ->
                                val isSelected = job.id == currentJob?.id
                                val isCommercial = job.serviceType.equals("Commercial", ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            if (isSelected) OrangePrimary else MaterialTheme.colorScheme.outlineVariant
                                        )
                                    ),
                                    modifier = Modifier
                                        .clickable { selectedBookingId = job.id }
                                        .testTag("project_tab_${job.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isCommercial) Icons.Default.Business else Icons.Default.Home,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else (if (isCommercial) StatusAmber else StatusBlue),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = job.projectTitle.take(24) + if (job.projectTitle.length > 24) "..." else "",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${job.customerName} • ${job.status}",
                                                fontSize = 9.sp,
                                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            when (activeWorkflowTab) {
                0 -> {
                    // If no active jobs exist, provide empty prompt with direct link to Marketboard
                    if (currentJob == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handyman,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "No Active Projects Claimed Yet",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Browse the Live Marketboard to claim new residential and commercial customer requests and manage their project milestones.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Button(
                            onClick = { activeWorkflowTab = 1 },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Browse Live Marketboard", fontSize = 13.sp)
                        }
                    }
                }
            } else {
                // Active Job Detail & Project Milestones
                val projectMilestones = remember(allMilestones, currentJob.id) {
                    allMilestones.filter { it.bookingId == currentJob.id }.sortedBy { it.phaseNumber }
                }

                val completedMilestonesCount = projectMilestones.count { it.status == "Completed" }
                val totalMilestonesCount = projectMilestones.size
                val progressFraction = if (totalMilestonesCount > 0) completedMilestonesCount.toFloat() / totalMilestonesCount else 0f
                val totalProjectValue = if (currentJob.acceptedQuoteAmount > 0) currentJob.acceptedQuoteAmount else currentJob.budget
                val completedDrawsSum = projectMilestones.filter { it.status == "Completed" }.sumOf { it.drawAmount }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("project_milestones_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Item 1: Active Project Header Card
                    item {
                        ActiveProjectHeaderCard(
                            job = currentJob,
                            currencyFormat = currencyFormat,
                            totalProjectValue = totalProjectValue,
                            completedDrawsSum = completedDrawsSum,
                            progressFraction = progressFraction,
                            completedMilestonesCount = completedMilestonesCount,
                            totalMilestonesCount = totalMilestonesCount,
                            onEnRoute = { viewModel.dispatchContractor(currentJob.id) },
                            onStartJob = { viewModel.updateJobExecutionStatus(currentJob.id, "In Progress") },
                            onCompleteJob = { viewModel.completeContractorJob(currentJob.id, "Job finished and approved by client.") },
                            onOpenFieldNotes = { showFieldNotesDialog = true },
                            onEditJob = { updated -> viewModel.updateCustomerBooking(updated) }
                        )
                    }

                    // Item 2: Milestones Section Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Flag, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Project Milestones",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "$completedMilestonesCount of $totalMilestonesCount phases completed • ${currencyFormat.format(completedDrawsSum)} of ${currencyFormat.format(totalProjectValue)} drawn",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    val nextPhase = (projectMilestones.maxOfOrNull { it.phaseNumber } ?: 0) + 1
                                    newMilestoneTitle = "Phase $nextPhase: "
                                    newMilestoneDesc = ""
                                    newMilestoneTargetDate = "In 3 Days"
                                    newMilestoneDrawAmount = (totalProjectValue * 0.25).toInt().toString()
                                    showAddMilestoneDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("add_milestone_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Phase", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Item 3: Project Milestones List
                    if (projectMilestones.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No milestones configured for this project.",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            newMilestoneTitle = "Phase 1: Initial Site Assessment & Scope Alignment"
                                            newMilestoneDesc = "Verify on-site conditions, order primary materials, pull city permit."
                                            newMilestoneTargetDate = "Day 1"
                                            newMilestoneDrawAmount = (totalProjectValue * 0.3).toInt().toString()
                                            showAddMilestoneDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Generate Initial Milestone", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        items(projectMilestones, key = { it.id }) { milestone ->
                            MilestonePhaseCard(
                                milestone = milestone,
                                currencyFormat = currencyFormat,
                                onAdvanceStatus = { nextStatus ->
                                    viewModel.advanceMilestoneStatus(milestone.id, nextStatus)
                                },
                                onDelete = {
                                    viewModel.deleteProjectMilestone(milestone.id)
                                }
                            )
                        }
                    }

                    // Bottom padding for scroll clearance
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
        1 -> {
            LiveMarketboardScreen(viewModel = viewModel)
        }
        2 -> {
            ThumbtackProOpportunitiesSection(viewModel = viewModel, currencyFormat = currencyFormat)
        }
        3 -> {
            TaskRabbitChecklistSection(viewModel = viewModel)
        }
        4 -> {
            StripeConnectEscrowSection(viewModel = viewModel, currencyFormat = currencyFormat)
        }
    }
        }
    }

    // Modal Dialog: Add Project Milestone
    if (showAddMilestoneDialog && currentJob != null) {
        val nextPhaseNum = (allMilestones.filter { it.bookingId == currentJob.id }.maxOfOrNull { it.phaseNumber } ?: 0) + 1
        AlertDialog(
            onDismissRequest = { showAddMilestoneDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Project Milestone Phase", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newMilestoneTitle,
                        onValueChange = { newMilestoneTitle = it },
                        label = { Text("Milestone Phase Title") },
                        placeholder = { Text("e.g. Phase $nextPhaseNum: Rough-In Plumbing & Inspection") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("milestone_title_input")
                    )

                    OutlinedTextField(
                        value = newMilestoneDesc,
                        onValueChange = { newMilestoneDesc = it },
                        label = { Text("Scope & Deliverables") },
                        placeholder = { Text("Describe the work required to pass this milestone") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("milestone_desc_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newMilestoneDrawAmount,
                            onValueChange = { newMilestoneDrawAmount = it },
                            label = { Text("Draw Amount ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("milestone_draw_input")
                        )
                        OutlinedTextField(
                            value = newMilestoneTargetDate,
                            onValueChange = { newMilestoneTargetDate = it },
                            label = { Text("Target Date") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("milestone_date_input")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val draw = newMilestoneDrawAmount.toDoubleOrNull() ?: 0.0
                        viewModel.addProjectMilestone(
                            bookingId = currentJob.id,
                            title = newMilestoneTitle.ifBlank { "Phase $nextPhaseNum: Site Deliverable" },
                            description = newMilestoneDesc,
                            phaseNumber = nextPhaseNum,
                            targetDate = newMilestoneTargetDate,
                            drawAmount = draw
                        )
                        showAddMilestoneDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.testTag("confirm_add_milestone_button")
                ) {
                    Text("Add Phase to Project")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMilestoneDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal Dialog: Field Notes & Site Progress Photo
    if (showFieldNotesDialog && currentJob != null) {
        AlertDialog(
            onDismissRequest = { showFieldNotesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Site Field Note & Photo Log", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Project: ${currentJob.projectTitle}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = fieldNotesInput,
                        onValueChange = { fieldNotesInput = it },
                        label = { Text("Inspection or Job Progress Notes") },
                        placeholder = { Text("Document materials installed, city inspector approvals, or special customer requests...") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth().testTag("field_notes_input")
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Geo-stamped with GPS coordinates and active job timestamp.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showNotice("Field note attached to project #${currentJob.id}")
                        showFieldNotesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Save to Project Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFieldNotesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ActiveProjectHeaderCard(
    job: CustomerBookingEntity,
    currencyFormat: NumberFormat,
    totalProjectValue: Double,
    completedDrawsSum: Double,
    progressFraction: Float,
    completedMilestonesCount: Int,
    totalMilestonesCount: Int,
    onEnRoute: () -> Unit,
    onStartJob: () -> Unit,
    onCompleteJob: () -> Unit,
    onOpenFieldNotes: () -> Unit,
    onEditJob: (CustomerBookingEntity) -> Unit
) {
    val isCommercial = job.serviceType.equals("Commercial", ignoreCase = true)
    var showEditPlacardDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isCommercial) StatusAmber.copy(alpha = 0.35f) else OrangePrimary.copy(alpha = 0.3f)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Badges & Job Status Row with SVG Icon & Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Custom SVG Trades Tools Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OrangePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_trades_tools),
                            contentDescription = "Trades SVG",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCommercial) StatusAmber.copy(alpha = 0.15f) else StatusBlue.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isCommercial) "🏢 COMMERCIAL" else "🏡 HOUSE / HOME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCommercial) StatusAmber else StatusBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = job.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Edit Placard Pop-up Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OrangePrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary),
                        modifier = Modifier
                            .clickable { showEditPlacardDialog = true }
                            .testTag("edit_contractor_placard_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Placard", tint = OrangePrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Edit", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                    }

                    // Execution Status Badge
                    val statusColor = when (job.status) {
                        "Completed" -> StatusGreen
                        "In Progress" -> OrangePrimary
                        "En Route" -> StatusBlue
                        else -> StatusAmber
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "● ${job.status.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Project Title & Budget
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.projectTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Customer: ${job.customerName} • ${job.customerPhone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(totalProjectValue),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = StatusGreen
                    )
                    Text(
                        text = "Contract Value",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(job.address, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Milestone Progress Bar & Financial Draws
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Milestone Progress: ${(progressFraction * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${currencyFormat.format(completedDrawsSum)} Drawn of ${currencyFormat.format(totalProjectValue)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusGreen
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    color = OrangePrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )

                // 80% Contractor Payout / 20% C520X Platform Clearing
                val contractorTakeHome = completedDrawsSum * 0.80
                val c520xRetained = completedDrawsSum * 0.20
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tradesman Take (80%): ${currencyFormat.format(contractorTakeHome)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                        Text("C520X Fee (20%): ${currencyFormat.format(c520xRetained)}", fontSize = 11.sp, color = OrangePrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Field Execution Action Buttons (Touch targets >= 48dp, cleanly arranged)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. En Route
                OutlinedButton(
                    onClick = onEnRoute,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (job.status == "En Route") StatusBlue else MaterialTheme.colorScheme.onSurface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (job.status == "En Route") StatusBlue else MaterialTheme.colorScheme.outlineVariant
                        )
                    ),
                    modifier = Modifier.weight(1f).height(44.dp).testTag("action_enroute_button")
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("En Route", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // 2. Start Work / In Progress
                OutlinedButton(
                    onClick = onStartJob,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (job.status == "In Progress") OrangePrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (job.status == "In Progress") OrangePrimary else MaterialTheme.colorScheme.outlineVariant
                        )
                    ),
                    modifier = Modifier.weight(1f).height(44.dp).testTag("action_start_work_button")
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clock In", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // 3. Field Notes / Photos
                OutlinedButton(
                    onClick = onOpenFieldNotes,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(44.dp).testTag("action_field_notes_button")
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Notes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // 4. Complete & Invoice
                Button(
                    onClick = onCompleteJob,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                    modifier = Modifier.weight(1.2f).height(44.dp).testTag("action_complete_job_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sign-Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showEditPlacardDialog) {
        EditContractorPlacardDialog(
            job = job,
            onDismiss = { showEditPlacardDialog = false },
            onSave = { updatedJob ->
                onEditJob(updatedJob)
                showEditPlacardDialog = false
            }
        )
    }
}

@Composable
private fun MilestonePhaseCard(
    milestone: ProjectMilestoneEntity,
    currencyFormat: NumberFormat,
    onAdvanceStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = milestone.status == "Completed"
    val isInProgress = milestone.status == "In Progress"

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                when {
                    isCompleted -> StatusGreen.copy(alpha = 0.4f)
                    isInProgress -> OrangePrimary.copy(alpha = 0.45f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_card_${milestone.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Phase Number, Title, Status Badge, Draw Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.weight(1f)
                ) {
                    // Phase Step Circle
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = when {
                                    isCompleted -> StatusGreen
                                    isInProgress -> OrangePrimary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text(
                                text = "${milestone.phaseNumber}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isInProgress) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = milestone.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Target: ${milestone.targetDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Draw Amount & Status Pill
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(milestone.drawAmount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCompleted) StatusGreen else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when {
                            isCompleted -> StatusGreen.copy(alpha = 0.15f)
                            isInProgress -> OrangePrimary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = milestone.status.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCompleted -> StatusGreen
                                isInProgress -> OrangePrimary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (milestone.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = milestone.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            if (milestone.verificationNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = milestone.verificationNotes,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = StatusGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_milestone_${milestone.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Milestone",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Milestone Phase State Progression Button
                when {
                    milestone.status == "Pending" -> {
                        Button(
                            onClick = { onAdvanceStatus("In Progress") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp).testTag("start_milestone_${milestone.id}")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start Phase", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    milestone.status == "In Progress" -> {
                        Button(
                            onClick = { onAdvanceStatus("Completed") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp).testTag("complete_milestone_${milestone.id}")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Complete & Draw (${currencyFormat.format(milestone.drawAmount)})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verified & Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// THUMBTACK PRO CLONE: DIRECT LEAD OPPORTUNITIES & 1-TAP QUOTES
// =========================================================================
@Composable
fun ThumbtackProOpportunitiesSection(
    viewModel: MainViewModel,
    currencyFormat: NumberFormat
) {
    val opportunities by viewModel.thumbtackOpportunities.collectAsState()
    val locationState by viewModel.userLocationState.collectAsState()
    val matchedOpportunities = opportunities.filter { it.distanceMiles <= locationState.matchingRadiusMiles }
    val displayOpportunities = if (matchedOpportunities.isNotEmpty()) matchedOpportunities else opportunities

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("thumbtack_opportunities_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thumbtack Pro Direct Opportunities",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real-time homeowner & commercial inquiries matched to your trade. Zero lead-generation fees on C520X — guaranteed 80% contractor payout on every accepted quote.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusGreen.copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lead Cost", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$0 Free", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = OrangePrimary.copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Payout Split", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("80% Pro / 20% C520X", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusBlue.copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Matched In-Radius", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${matchedOpportunities.size} Leads", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusBlue)
                            }
                        }
                    }
                }
            }
        }

        // Real-Time GPS & Zip-Code Matching Flow Card
        item {
            LocationPermissionCard(
                viewModel = viewModel,
                title = "Live GPS Territory & Lead Matching",
                description = "Radius: ${locationState.matchingRadiusMiles.toInt()} mi around ${locationState.city} (Zip ${locationState.zipCode}). ${matchedOpportunities.size} local opportunities matched."
            )
        }

        items(displayOpportunities, key = { it.id }) { opp ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("thumbtack_opp_${opp.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = opp.category,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${opp.location} • ${opp.distanceMiles} mi away",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (opp.urgency.contains("Immediate", ignoreCase = true)) StatusRed.copy(alpha = 0.15f) else StatusAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = opp.urgency,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (opp.urgency.contains("Immediate", ignoreCase = true)) StatusRed else StatusAmber,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = opp.jobDescription,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Customer Budget", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = opp.budgetRange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        }

                        if (opp.isContacted) {
                            OutlinedButton(
                                onClick = { /* Already contacted */ },
                                enabled = false,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quote Sent • In Escrow Chat", fontSize = 11.sp, color = StatusGreen)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.contactThumbtackOpportunity(opp.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("send_quote_btn_${opp.id}")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send 1-Tap Quote (80% Lock)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TASKRABBIT CLONE: PHASED MILESTONE CHECKLISTS & REAL-TIME VERIFICATION
// =========================================================================
@Composable
fun TaskRabbitChecklistSection(viewModel: MainViewModel) {
    val steps by viewModel.taskRabbitMilestoneSteps.collectAsState()
    val completedCount = steps.count { it.isCompleted }
    val totalCount = steps.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("taskrabbit_checklist_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Checklist, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TaskRabbit Live Checklists & Sign-Off",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "$completedCount/$totalCount Done",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (progress >= 1f) StatusGreen else OrangePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (progress >= 1f) StatusGreen else OrangePrimary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Interactive step-by-step milestone execution. Checking off verified tasks notifies the client and triggers automatic C520X 80% escrow milestone release.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(steps, key = { it.id }) { step ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (step.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("step_card_${step.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = step.isCompleted,
                        onCheckedChange = { viewModel.toggleTaskRabbitStep(step.id) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = StatusGreen,
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.testTag("checkbox_${step.id}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = step.phaseName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (step.isCompleted) StatusGreen else OrangePrimary
                            )
                            if (step.isCompleted) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Verified & Escrow Approved", fontSize = 10.sp, color = StatusGreen, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = step.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (step.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = step.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// STRIPE CONNECT 80/20 ESCROW CLEARING & TREASURY LEDGER
// =========================================================================
@Composable
fun StripeConnectEscrowSection(
    viewModel: MainViewModel,
    currencyFormat: NumberFormat
) {
    val bookings by viewModel.customerBookings.collectAsState()
    val allMilestones by viewModel.projectMilestones.collectAsState()

    val totalEscrowVolume = bookings.sumOf { if (it.acceptedQuoteAmount > 0) it.acceptedQuoteAmount else it.budget }
    val contractor80Share = totalEscrowVolume * 0.80
    val platform20Treasury = totalEscrowVolume * 0.20

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("stripe_escrow_ledger"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = OrangeDark, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stripe Connect 80/20 Escrow Treasury",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Automated split-payments architecture. Clients deposit 100% upfront into C520X Vault. Upon milestone sign-off, Stripe Connect Custom transfers 80% to contractor and 20% platform revenue.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Payout Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusGreen.copy(alpha = 0.12f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Contractor Share (80%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = currencyFormat.format(contractor80Share),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                        Text("Disbursed to linked bank", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangePrimary.copy(alpha = 0.12f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("C520X Platform Fee (20%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = currencyFormat.format(platform20Treasury),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                        Text("Master Admin Vault revenue", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Escrow Lifecycle 4-Step Diagram Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Escrow Clearing Lifecycle", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                    val steps = listOf(
                        "1. Upfront Deposit" to "Client funds 100% of approved quote into C520X Stripe Escrow Hold.",
                        "2. Milestone Execution" to "Contractor executes phase and logs live photo proof in mobile workflow.",
                        "3. Client Sign-Off" to "Client or field inspector verifies quality and releases milestone.",
                        "4. Instant 80/20 Disburse" to "Stripe Connect transfers 80% directly to Pro bank account and 20% to C520X."
                    )

                    steps.forEach { (title, desc) ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Connected Account Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Linked Stripe Connect Account", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Chase Business Checking •••• 4892 (Active)", fontSize = 11.sp, color = StatusGreen)
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = StatusGreen.copy(alpha = 0.15f)) {
                            Text("Verified Pro", fontSize = 10.sp, color = StatusGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.showNotice("Stripe Connect Express dashboard opened in secure browser!") },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("open_stripe_dashboard_btn")
                    ) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Manage Stripe Payouts & Tax Documents (1099-NEC)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
