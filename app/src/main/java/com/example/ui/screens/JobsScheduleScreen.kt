package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewKanban
import com.example.ui.components.BulkScheduleImportDialog
import com.example.ui.components.GoogleCalendarView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.JobEntity
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.Navy700
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen

@Composable
fun JobsScheduleScreen(viewModel: MainViewModel) {
    val jobs by viewModel.filteredJobs.collectAsState()
    val selectedTrade by viewModel.selectedTrade.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddJobDialog by remember { mutableStateOf(false) }
    var showBulkImportDialog by remember { mutableStateOf(false) }
    var selectedJobForStatusChange by remember { mutableStateOf<JobEntity?>(null) }
    var activeScheduleSubTab by remember { mutableStateOf(0) } // 0 = Google Calendar (Firestore), 1 = Dispatch Jobs List

    val tradeList = listOf("All", "Electrical", "Plumbing", "HVAC", "Carpentry", "Painting", "Handyman", "Roofing")
    val statusList = listOf("All", "Pending Estimate", "Scheduled", "Dispatched", "In Progress", "Completed")

    Column(modifier = Modifier.fillMaxSize().testTag("dispatch_schedule_screen")) {
        // Mode Switcher: Google Calendar (Firestore) vs Dispatch Jobs List
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tab 0: Google Calendar UI (Firestore)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeScheduleSubTab == 0) Color(0xFF1A73E8) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .weight(1.2f)
                        .clickable { activeScheduleSubTab = 0 }
                        .testTag("tab_google_calendar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (activeScheduleSubTab == 0) Color.White else Color(0xFF1A73E8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Google Calendar",
                            fontSize = 12.sp,
                            fontWeight = if (activeScheduleSubTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (activeScheduleSubTab == 0) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Tab 1: Dispatch Jobs List
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeScheduleSubTab == 1) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeScheduleSubTab = 1 }
                        .testTag("tab_dispatch_list")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = if (activeScheduleSubTab == 1) Color.White else OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dispatch List (${jobs.size})",
                            fontSize = 12.sp,
                            fontWeight = if (activeScheduleSubTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (activeScheduleSubTab == 1) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (activeScheduleSubTab == 0) {
            GoogleCalendarView(viewModel = viewModel)
        } else {
            Box(modifier = Modifier.fillMaxSize().weight(1f).testTag("dispatch_list_container")) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Quick Bulk Upload and Add Dispatch Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = OrangePrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary),
                        modifier = Modifier
                            .weight(1.2f)
                            .clickable { showBulkImportDialog = true }
                            .testTag("bulk_import_loads_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⚡ Bulk Import Loads",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showAddJobDialog = true }
                            .testTag("add_single_dispatch_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+ Add Load",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // View Switcher Bar (Dispatch List vs Kanban Board)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectTab(AppTab.KANBAN_BOARD) }
                        .testTag("switch_to_kanban_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(OrangePrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewKanban,
                                    contentDescription = "Kanban",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Drag-and-Drop Kanban Board View",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Manage tasks, states & priorities with Room persistence",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = "Open Board >",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by client, address, trade, or tech...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = OrangePrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("job_search_input")
                )
            }

            // Trade Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tradeList.forEach { trade ->
                        val isSelected = trade == selectedTrade
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clickable { viewModel.setTradeFilter(trade) }
                                .padding(vertical = 2.dp)
                                .testTag("trade_filter_$trade")
                        ) {
                            Text(
                                text = trade,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Status Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusList.forEach { status ->
                        val isSelected = status == selectedStatus
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Navy700 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary) else null,
                            modifier = Modifier
                                .clickable { viewModel.setStatusFilter(status) }
                                .padding(vertical = 2.dp)
                                .testTag("status_filter_$status")
                        ) {
                            Text(
                                text = status,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // Schedule & Drag Reorder Explanation Strip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCHEDULE & DISPATCH QUEUE (${jobs.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "▲ ▼ Priority Controls",
                        fontSize = 11.sp,
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Jobs List
            if (jobs.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No jobs found matching your filters.", fontWeight = FontWeight.Bold)
                            Text("Try clearing trade/status filters or search query.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(jobs, key = { it.id }) { job ->
                    JobCardItem(
                        job = job,
                        onMoveUp = { viewModel.reorderJob(job, moveUp = true) },
                        onMoveDown = { viewModel.reorderJob(job, moveUp = false) },
                        onStatusClick = { selectedJobForStatusChange = job },
                        onClockIn = {
                            viewModel.startTimer(job)
                            viewModel.selectTab(AppTab.TIME_TRACKING)
                        },
                        onContact = {
                            viewModel.selectTab(AppTab.CHAT_SLACK)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Add New Job
        FloatingActionButton(
            onClick = { showAddJobDialog = true },
            containerColor = OrangePrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_job_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Job")
        }
    }
    }
}

    // Status Change Dialog
    selectedJobForStatusChange?.let { job ->
        Dialog(onDismissRequest = { selectedJobForStatusChange = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Update Dispatch Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = job.title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val statuses = listOf(
                        "Pending Estimate",
                        "Scheduled",
                        "Dispatched",
                        "In Progress",
                        "Completed"
                    )

                    statuses.forEach { st ->
                        val isCurrent = st == job.status
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) OrangePrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.updateJobStatus(job, st)
                                    selectedJobForStatusChange = null
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = st,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (isCurrent) OrangePrimary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isCurrent) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { selectedJobForStatusChange = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

    // Add Job Dialog
    if (showAddJobDialog) {
        AddJobDialog(
            onDismiss = { showAddJobDialog = false },
            onSave = { title, trade, client, phone, address, status, pipeline, tech, notes ->
                viewModel.addJob(title, trade, client, phone, address, status, pipeline, tech, notes)
                showAddJobDialog = false
            }
        )
    }

    // Bulk Import Schedules & Loads Dialog
    if (showBulkImportDialog) {
        BulkScheduleImportDialog(
            viewModel = viewModel,
            onDismiss = { showBulkImportDialog = false }
        )
    }
}

@Composable
fun JobCardItem(
    job: JobEntity,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onStatusClick: () -> Unit,
    onClockIn: () -> Unit,
    onContact: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth().testTag("job_item_${job.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Trade, Status Badge, Reorder controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = job.trade.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = OrangePrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(OrangePrimary.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.clickable { onStatusClick() }.testTag("job_status_${job.id}")) {
                        StatusBadge(status = job.status)
                    }
                }

                // Drag/Priority reorder buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMoveUp, modifier = Modifier.size(28.dp).testTag("move_up_${job.id}")) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Prioritize Up", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onMoveDown, modifier = Modifier.size(28.dp).testTag("move_down_${job.id}")) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = job.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Client & Tech Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.clientName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.assignedTech,
                        fontSize = 11.sp,
                        color = OrangePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = job.address,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            if (job.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• ${job.notes}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Breakdown and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Value: $${String.format("%,.0f", job.pipelineValue)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (job.billedAmount > 0) {
                        Text(
                            text = "Billed: $${String.format("%,.0f", job.billedAmount)}",
                            fontSize = 10.sp,
                            color = StatusGreen
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onContact,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Comms", fontSize = 10.sp)
                    }

                    Button(
                        onClick = onClockIn,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("clock_in_job_${job.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clock In", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddJobDialog(
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        trade: String,
        client: String,
        phone: String,
        address: String,
        status: String,
        pipeline: Double,
        tech: String,
        notes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var trade by remember { mutableStateOf("Electrical") }
    var client by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Scheduled") }
    var pipelineText by remember { mutableStateOf("1500") }
    var tech by remember { mutableStateOf("Alex Ramirez (Lead Tech)") }
    var notes by remember { mutableStateOf("") }

    val tradeOptions = listOf("Electrical", "Plumbing", "HVAC", "Carpentry", "Painting", "Handyman", "Roofing")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Dispatch New Contractor Job", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Job Scope Title") },
                        placeholder = { Text("e.g., Commercial Water Line Trench") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Trade Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        tradeOptions.forEach { opt ->
                            val isSel = opt == trade
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { trade = opt }
                            ) {
                                Text(
                                    text = opt,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = client,
                        onValueChange = { client = it },
                        label = { Text("Client Name / Facility") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Site Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = pipelineText,
                        onValueChange = { pipelineText = it },
                        label = { Text("Contract Value ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = tech,
                        onValueChange = { tech = it },
                        label = { Text("Assigned Tech / Lead") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Field Notes / Permits") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Button(
                            onClick = {
                                val valD = pipelineText.toDoubleOrNull() ?: 1000.0
                                onSave(
                                    title.ifBlank { "Service Operation" },
                                    trade,
                                    client.ifBlank { "Commercial Client" },
                                    phone.ifBlank { "(520) 555-0199" },
                                    address.ifBlank { "Tucson, AZ" },
                                    status,
                                    valD,
                                    tech,
                                    notes
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Dispatch Job")
                        }
                    }
                }
            }
        }
    }
}
