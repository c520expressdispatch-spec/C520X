package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CalendarJobAssignment
import com.example.data.ContractorAvailability
import com.example.data.JobEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class CalendarViewMode(val label: String) {
    DAY("Day"),
    THREE_DAY("3-Day"),
    SCHEDULE("Agenda")
}

@Composable
fun GoogleCalendarView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val contractorAvailabilities by viewModel.contractorAvailabilities.collectAsState()
    val calendarAssignments by viewModel.calendarAssignments.collectAsState()
    val isFirestoreConnected by viewModel.isFirestoreCalendarConnected.collectAsState()
    val syncTimestamp by viewModel.calendarLastSyncTime.collectAsState()
    val roomJobs by viewModel.allJobs.collectAsState()

    var viewMode by remember { mutableStateOf(CalendarViewMode.DAY) }
    var selectedContractorId by remember { mutableStateOf<String?>(null) }
    var selectedDayOffset by remember { mutableStateOf(0) } // 0 = Today, +1 = Tomorrow, etc.

    var selectedAssignmentForDetail by remember { mutableStateOf<CalendarJobAssignment?>(null) }
    var selectedContractorForEditAvailability by remember { mutableStateOf<ContractorAvailability?>(null) }
    var showCreateAssignmentDialog by remember { mutableStateOf(false) }
    var showAssignFromRoomDialog by remember { mutableStateOf(false) }
    var prefilledHourForNewAssignment by remember { mutableStateOf(9) }

    // Date calculations
    val calendar = remember(selectedDayOffset) {
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, selectedDayOffset)
        }
    }
    val currentDisplayDate = calendar.time
    val dateKey = remember(currentDisplayDate) {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(currentDisplayDate)
    }
    val monthYearText = remember(currentDisplayDate) {
        SimpleDateFormat("MMMM yyyy", Locale.US).format(currentDisplayDate)
    }
    val dayHeaderText = remember(currentDisplayDate) {
        SimpleDateFormat("EEEE, MMMM d", Locale.US).format(currentDisplayDate)
    }

    // Filtered assignments
    val filteredAssignments = remember(calendarAssignments, selectedContractorId, dateKey, viewMode) {
        calendarAssignments.filter { assignment ->
            val matchesContractor = selectedContractorId == null || assignment.contractorId == selectedContractorId
            val matchesDate = when (viewMode) {
                CalendarViewMode.SCHEDULE -> true
                CalendarViewMode.DAY -> assignment.date == dateKey || assignment.date.isEmpty()
                CalendarViewMode.THREE_DAY -> true
            }
            matchesContractor && matchesDate
        }
    }

    Box(modifier = modifier.fillMaxSize().testTag("google_calendar_view")) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. Google Calendar Header Bar
            GoogleCalendarTopHeader(
                monthYearText = monthYearText,
                isToday = selectedDayOffset == 0,
                viewMode = viewMode,
                isFirestoreConnected = isFirestoreConnected,
                syncTimestamp = syncTimestamp,
                onPrev = { selectedDayOffset -= 1 },
                onNext = { selectedDayOffset += 1 },
                onToday = { selectedDayOffset = 0 },
                onSelectViewMode = { viewMode = it },
                onAssignFromRoom = { showAssignFromRoomDialog = true }
            )

            // 2. Contractor Availability Strip (Firestore backed)
            ContractorAvailabilityStrip(
                contractors = contractorAvailabilities,
                selectedContractorId = selectedContractorId,
                onSelectContractor = { id ->
                    selectedContractorId = if (selectedContractorId == id) null else id
                },
                onEditAvailability = { contractor ->
                    selectedContractorForEditAvailability = contractor
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // 3. Content View depending on Mode
            when (viewMode) {
                CalendarViewMode.DAY -> {
                    GoogleCalendarDayTimeline(
                        dayHeaderText = dayHeaderText,
                        assignments = filteredAssignments,
                        onSlotClick = { hour ->
                            prefilledHourForNewAssignment = hour
                            showCreateAssignmentDialog = true
                        },
                        onAssignmentClick = { assignment ->
                            selectedAssignmentForDetail = assignment
                        }
                    )
                }
                CalendarViewMode.THREE_DAY -> {
                    GoogleCalendarMultiDayView(
                        baseCalendar = calendar,
                        assignments = calendarAssignments.filter {
                            selectedContractorId == null || it.contractorId == selectedContractorId
                        },
                        onAssignmentClick = { assignment ->
                            selectedAssignmentForDetail = assignment
                        },
                        onSlotClick = { _, hour ->
                            prefilledHourForNewAssignment = hour
                            showCreateAssignmentDialog = true
                        }
                    )
                }
                CalendarViewMode.SCHEDULE -> {
                    GoogleCalendarAgendaView(
                        assignments = filteredAssignments,
                        onAssignmentClick = { assignment ->
                            selectedAssignmentForDetail = assignment
                        },
                        onAddNew = { showCreateAssignmentDialog = true }
                    )
                }
            }
        }

        // Floating Action Button (+) for Quick Add
        FloatingActionButton(
            onClick = {
                prefilledHourForNewAssignment = 9
                showCreateAssignmentDialog = true
            },
            containerColor = Color(0xFF1A73E8), // Google Calendar Blue
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 16.dp)
                .testTag("google_calendar_add_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Job Assignment", modifier = Modifier.size(28.dp))
        }
    }

    // Modal 1: Job Assignment Details & Reassignment Dialog
    selectedAssignmentForDetail?.let { assignment ->
        JobAssignmentDetailDialog(
            assignment = assignment,
            contractors = contractorAvailabilities,
            onDismiss = { selectedAssignmentForDetail = null },
            onUpdateAssignment = { updated ->
                viewModel.saveCalendarAssignment(updated)
                selectedAssignmentForDetail = null
            },
            onDeleteAssignment = { id ->
                viewModel.deleteCalendarAssignment(id)
                selectedAssignmentForDetail = null
            }
        )
    }

    // Modal 2: Edit Contractor Availability Dialog
    selectedContractorForEditAvailability?.let { contractor ->
        EditContractorAvailabilityDialog(
            contractor = contractor,
            onDismiss = { selectedContractorForEditAvailability = null },
            onSave = { updated ->
                viewModel.updateContractorAvailability(updated)
                selectedContractorForEditAvailability = null
            }
        )
    }

    // Modal 3: Create New Job Assignment Dialog
    if (showCreateAssignmentDialog) {
        CreateJobAssignmentDialog(
            initialHour = prefilledHourForNewAssignment,
            currentDateKey = dateKey,
            contractors = contractorAvailabilities,
            onDismiss = { showCreateAssignmentDialog = false },
            onSave = { newAssignment ->
                viewModel.saveCalendarAssignment(newAssignment)
                showCreateAssignmentDialog = false
            }
        )
    }

    // Modal 4: Assign from Room Jobs Dialog
    if (showAssignFromRoomDialog) {
        AssignFromRoomJobsDialog(
            jobs = roomJobs,
            contractors = contractorAvailabilities,
            onDismiss = { showAssignFromRoomDialog = false },
            onAssign = { job, contractor, hour, duration ->
                viewModel.assignJobToCalendar(job, contractor, hour, duration)
                showAssignFromRoomDialog = false
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// Top Header & Toolbar
// -------------------------------------------------------------------------------------------------
@Composable
private fun GoogleCalendarTopHeader(
    monthYearText: String,
    isToday: Boolean,
    viewMode: CalendarViewMode,
    isFirestoreConnected: Boolean,
    syncTimestamp: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onSelectViewMode: (CalendarViewMode) -> Unit,
    onAssignFromRoom: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            // Row 1: Google Calendar Logo + Month/Year + Navigation Buttons + Firestore Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A73E8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = SimpleDateFormat("d", Locale.US).format(Date()),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = monthYearText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isFirestoreConnected) StatusGreen else StatusAmber)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFirestoreConnected) "Firestore: Live" else "Offline Cache",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isFirestoreConnected) StatusGreen else StatusAmber
                            )
                        }
                    }
                }

                // Nav Controls: <, Today, >
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onPrev,
                        modifier = Modifier.size(32.dp).testTag("cal_nav_prev")
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", modifier = Modifier.size(20.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isToday) Color(0xFF1A73E8).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isToday) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A73E8)) else null,
                        modifier = Modifier
                            .clickable { onToday() }
                            .testTag("cal_nav_today")
                    ) {
                        Text(
                            text = "Today",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isToday) Color(0xFF1A73E8) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(32.dp).testTag("cal_nav_next")
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: View Mode Switcher (Day, 3-Day, Agenda) + Assign from Room button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View Mode Tabs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarViewMode.values().forEach { mode ->
                        val isSelected = viewMode == mode
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF1A73E8) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clickable { onSelectViewMode(mode) }
                                .testTag("cal_mode_${mode.name.lowercase()}")
                        ) {
                            Text(
                                text = mode.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Quick Dispatch Button from Room
                OutlinedButton(
                    onClick = onAssignFromRoom,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp).testTag("cal_assign_room_btn")
                ) {
                    Icon(Icons.Default.Build, contentDescription = "Assign", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Assign Lead", fontSize = 11.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Contractor Availability Bar (Firestore Synced)
// -------------------------------------------------------------------------------------------------
@Composable
private fun ContractorAvailabilityStrip(
    contractors: List<ContractorAvailability>,
    selectedContractorId: String?,
    onSelectContractor: (String) -> Unit,
    onEditAvailability: (ContractorAvailability) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contractor Availability (Firestore Synced)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap to filter • Long tap to edit",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            contractors.forEach { contractor ->
                val isSelected = selectedContractorId == contractor.contractorId
                val tradeColor = when (contractor.trade) {
                    "Electrical" -> Color(0xFF1A73E8)
                    "HVAC" -> Color(0xFF039BE5)
                    "Plumbing" -> Color(0xFF0B8043)
                    "Carpentry" -> Color(0xFFF4511E)
                    else -> Color(0xFF8E24AA)
                }
                val statusBg = when (contractor.status) {
                    "Available" -> StatusGreen.copy(alpha = 0.15f)
                    "Booked" -> Color(0xFF1A73E8).copy(alpha = 0.15f)
                    "Emergency On-Call" -> StatusAmber.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val statusColor = when (contractor.status) {
                    "Available" -> StatusGreen
                    "Booked" -> Color(0xFF1A73E8)
                    "Emergency On-Call" -> StatusAmber
                    else -> Color.Gray
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) tradeColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) tradeColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .clickable { onSelectContractor(contractor.contractorId) }
                        .testTag("contractor_chip_${contractor.contractorId}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Initial
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(tradeColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contractor.contractorName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = contractor.contractorName.substringBefore(" ("),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "• ${contractor.trade}",
                                    fontSize = 10.sp,
                                    color = tradeColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = statusBg
                                ) {
                                    Text(
                                        text = contractor.status,
                                        fontSize = 9.sp,
                                        color = statusColor,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${contractor.startTime} - ${contractor.endTime}",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        // Quick edit pencil
                        IconButton(
                            onClick = { onEditAvailability(contractor) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Availability",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Day Timeline View (Hour-by-hour with Google Calendar styling)
// -------------------------------------------------------------------------------------------------
@Composable
private fun GoogleCalendarDayTimeline(
    dayHeaderText: String,
    assignments: List<CalendarJobAssignment>,
    onSlotClick: (Int) -> Unit,
    onAssignmentClick: (CalendarJobAssignment) -> Unit
) {
    val scrollState = rememberScrollState()
    val hours = (7..19).toList() // 7 AM to 7 PM

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp)
    ) {
        // Date indicator strip
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF1A73E8),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = dayHeaderText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${assignments.size} assignments scheduled",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Hourly Grid
        hours.forEach { hour ->
            val hourAssignments = assignments.filter { it.startHour == hour }
            val timeLabel = formatHour(hour)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clickable { onSlotClick(hour) }
                    .testTag("cal_hour_slot_$hour"),
                verticalAlignment = Alignment.Top
            ) {
                // Time label column
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .padding(start = 12.dp, top = 6.dp)
                ) {
                    Text(
                        text = timeLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Divider and event column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 1.dp
                    )

                    if (hourAssignments.isNotEmpty()) {
                        hourAssignments.forEach { assignment ->
                            GoogleCalendarEventChip(
                                assignment = assignment,
                                onClick = { onAssignmentClick(assignment) }
                            )
                        }
                    } else {
                        // Empty slot tap hint
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "+ Tap to assign contractor",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Google Calendar Event Chip
// -------------------------------------------------------------------------------------------------
@Composable
private fun GoogleCalendarEventChip(
    assignment: CalendarJobAssignment,
    onClick: () -> Unit
) {
    val eventColor = Color(assignment.colorHex)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = eventColor.copy(alpha = 0.16f),
        border = androidx.compose.foundation.BorderStroke(1.dp, eventColor.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onClick() }
            .testTag("cal_event_${assignment.id}")
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Accent Pillar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(eventColor)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = assignment.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = assignment.timeSlotFormatted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = eventColor
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👤 ${assignment.clientName}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🛠️ ${assignment.contractorName}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = eventColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (assignment.status) {
                            "Completed" -> StatusGreen.copy(alpha = 0.2f)
                            "In Progress" -> Color(0xFF1A73E8).copy(alpha = 0.2f)
                            "Dispatched" -> StatusAmber.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = assignment.status,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (assignment.status) {
                                "Completed" -> StatusGreen
                                "In Progress" -> Color(0xFF1A73E8)
                                "Dispatched" -> StatusAmber
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 3-Day Multi-Column View
// -------------------------------------------------------------------------------------------------
@Composable
private fun GoogleCalendarMultiDayView(
    baseCalendar: Calendar,
    assignments: List<CalendarJobAssignment>,
    onAssignmentClick: (CalendarJobAssignment) -> Unit,
    onSlotClick: (String, Int) -> Unit
) {
    val dayDates = remember(baseCalendar) {
        (0..2).map { offset ->
            val cal = (baseCalendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset) }
            val key = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
            val header = SimpleDateFormat("EEE d", Locale.US).format(cal.time)
            Pair(key, header)
        }
    }
    val hours = (8..18).toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Multi-day Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(vertical = 8.dp)
        ) {
            Box(modifier = Modifier.width(48.dp))
            dayDates.forEach { (_, header) ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = header,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        HorizontalDivider()

        // Hourly Grid with 3 columns
        hours.forEach { hour ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
            ) {
                // Hour label
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .padding(start = 6.dp, top = 4.dp)
                ) {
                    Text(
                        text = formatHour(hour),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 3 Day Columns
                dayDates.forEach { (dateKey, _) ->
                    val dayAssignments = assignments.filter {
                        (it.date == dateKey || it.date.isEmpty()) && it.startHour == hour
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            .clickable { onSlotClick(dateKey, hour) }
                            .padding(2.dp)
                    ) {
                        dayAssignments.firstOrNull()?.let { assignment ->
                            val color = Color(assignment.colorHex)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = color.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, color),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onAssignmentClick(assignment) }
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    Text(
                                        text = assignment.title,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = assignment.contractorName.substringBefore(" "),
                                        fontSize = 8.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Agenda / Schedule View
// -------------------------------------------------------------------------------------------------
@Composable
private fun GoogleCalendarAgendaView(
    assignments: List<CalendarJobAssignment>,
    onAssignmentClick: (CalendarJobAssignment) -> Unit,
    onAddNew: () -> Unit
) {
    if (assignments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF1A73E8),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Job Assignments Scheduled",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap below or any open timeline slot to dispatch a contractor.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Assignment")
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assignments, key = { it.id }) { assignment ->
                GoogleCalendarEventChip(
                    assignment = assignment,
                    onClick = { onAssignmentClick(assignment) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Dialog 1: Job Assignment Details & Contractor Reassignment
// -------------------------------------------------------------------------------------------------
@Composable
private fun JobAssignmentDetailDialog(
    assignment: CalendarJobAssignment,
    contractors: List<ContractorAvailability>,
    onDismiss: () -> Unit,
    onUpdateAssignment: (CalendarJobAssignment) -> Unit,
    onDeleteAssignment: (String) -> Unit
) {
    var selectedContractorId by remember { mutableStateOf(assignment.contractorId) }
    var selectedStatus by remember { mutableStateOf(assignment.status) }
    var showContractorDropdown by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }

    val statusOptions = listOf("Scheduled", "Dispatched", "In Progress", "Completed")
    val currentContractor = contractors.find { it.contractorId == selectedContractorId }
        ?: contractors.firstOrNull()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("cal_detail_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(assignment.colorHex))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = assignment.trade,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(assignment.colorHex)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = assignment.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Time and Location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF1A73E8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = assignment.timeSlotFormatted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = assignment.address, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${assignment.clientName} • ${assignment.clientPhone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Reassignment Section (Firestore backed)
                Text(
                    text = "Assign Contractor (Firestore Synced)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box {
                    OutlinedButton(
                        onClick = { showContractorDropdown = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("cal_reassign_dropdown_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentContractor?.contractorName ?: "Select Contractor",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    DropdownMenu(
                        expanded = showContractorDropdown,
                        onDismissRequest = { showContractorDropdown = false }
                    ) {
                        contractors.forEach { c ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(c.contractorName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${c.trade} • ${c.status} (${c.startTime}-${c.endTime})", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    selectedContractorId = c.contractorId
                                    showContractorDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status Selector
                Text(
                    text = "Job Status",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusOptions.forEach { st ->
                        val isSel = selectedStatus == st
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF1A73E8) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedStatus = st }
                        ) {
                            Text(
                                text = st,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Delete and Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onDeleteAssignment(assignment.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("cal_delete_assignment_btn")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val updated = assignment.copy(
                                contractorId = currentContractor?.contractorId ?: assignment.contractorId,
                                contractorName = currentContractor?.contractorName ?: assignment.contractorName,
                                status = selectedStatus
                            )
                            onUpdateAssignment(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f).testTag("cal_save_reassign_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save & Sync", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Dialog 2: Edit Contractor Availability
// -------------------------------------------------------------------------------------------------
@Composable
private fun EditContractorAvailabilityDialog(
    contractor: ContractorAvailability,
    onDismiss: () -> Unit,
    onSave: (ContractorAvailability) -> Unit
) {
    var startTime by remember { mutableStateOf(contractor.startTime) }
    var endTime by remember { mutableStateOf(contractor.endTime) }
    var status by remember { mutableStateOf(contractor.status) }
    var notes by remember { mutableStateOf(contractor.notes) }

    val statusOptions = listOf("Available", "Booked", "Emergency On-Call", "Off Duty")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(12.dp).testTag("cal_edit_avail_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Contractor Availability",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${contractor.contractorName} • ${contractor.trade}",
                    fontSize = 12.sp,
                    color = Color(0xFF1A73E8),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Status (Firestore Collection: contractor_availability)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusOptions.forEach { opt ->
                        val isSel = status == opt
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF1A73E8) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { status = opt }
                        ) {
                            Text(
                                text = opt,
                                fontSize = 9.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Availability Notes", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updated = contractor.copy(
                            startTime = startTime,
                            endTime = endTime,
                            status = status,
                            notes = notes
                        )
                        onSave(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("cal_save_avail_btn")
                ) {
                    Text("Save Availability to Firestore")
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Dialog 3: Create New Job Assignment
// -------------------------------------------------------------------------------------------------
@Composable
private fun CreateJobAssignmentDialog(
    initialHour: Int,
    currentDateKey: String,
    contractors: List<ContractorAvailability>,
    onDismiss: () -> Unit,
    onSave: (CalendarJobAssignment) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("(520) ") }
    var address by remember { mutableStateOf("Tucson, AZ") }
    var trade by remember { mutableStateOf("Electrical") }
    var startHour by remember { mutableStateOf(initialHour) }
    var durationHours by remember { mutableStateOf(2.0) }
    var selectedContractorId by remember { mutableStateOf(contractors.firstOrNull()?.contractorId ?: "alex_ramirez") }
    var selectedColor by remember { mutableStateOf(0xFF1A73E8) }

    val tradeOptions = listOf("Electrical", "HVAC", "Plumbing", "Carpentry", "Handyman", "Roofing")
    val colorOptions = listOf(
        0xFF1A73E8, // Google Blue
        0xFFD50000, // Tomato Red
        0xFF0B8043, // Sage Green
        0xFFF4511E, // Tangerine
        0xFF8E24AA, // Grape
        0xFFF6BF26  // Banana
    )

    val selectedContractor = contractors.find { it.contractorId == selectedContractorId }
        ?: contractors.firstOrNull()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("cal_create_assignment_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New Job Assignment", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Job Title (e.g. 200A Panel Replacement)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cal_input_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = clientPhone,
                        onValueChange = { clientPhone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Service Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Trade Chip Row
                Text("Trade Category", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tradeOptions.forEach { t ->
                        val isSel = trade == t
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF1A73E8) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { trade = t }
                        ) {
                            Text(
                                text = t,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Assign to Contractor
                Text("Assigned Contractor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    contractors.forEach { c ->
                        val isSel = selectedContractorId == c.contractorId
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF1A73E8).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = if (isSel) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A73E8)) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedContractorId = c.contractorId }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(c.contractorName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("${c.trade} • ${c.status}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Google Calendar Color Tag
                Text("Calendar Event Color", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { col ->
                        val isSel = selectedColor == col
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(col))
                                .clickable { selectedColor = col }
                                .border(
                                    width = if (isSel) 3.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isBlank()) title = "$trade Service Job"
                        if (clientName.isBlank()) clientName = "Scheduled Client"

                        val assignment = CalendarJobAssignment(
                            title = title,
                            trade = trade,
                            clientName = clientName,
                            clientPhone = clientPhone,
                            address = address,
                            contractorId = selectedContractor?.contractorId ?: "alex_ramirez",
                            contractorName = selectedContractor?.contractorName ?: "Alex Ramirez",
                            date = currentDateKey,
                            startHour = startHour,
                            startMinute = 0,
                            durationHours = durationHours,
                            status = "Scheduled",
                            colorHex = selectedColor
                        )
                        onSave(assignment)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("cal_submit_assignment_btn")
                ) {
                    Text("Schedule & Sync to Firestore")
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Dialog 4: Assign from Room Database Jobs
// -------------------------------------------------------------------------------------------------
@Composable
private fun AssignFromRoomJobsDialog(
    jobs: List<JobEntity>,
    contractors: List<ContractorAvailability>,
    onDismiss: () -> Unit,
    onAssign: (JobEntity, ContractorAvailability, Int, Double) -> Unit
) {
    var selectedJob by remember { mutableStateOf(jobs.firstOrNull()) }
    var selectedContractor by remember { mutableStateOf(contractors.firstOrNull()) }
    var selectedHour by remember { mutableStateOf(10) }
    var duration by remember { mutableStateOf(2.0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("cal_assign_room_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Assign Lead to Google Calendar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Select Unassigned Job / Lead (Room DB):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    jobs.take(5).forEach { job ->
                        val isSel = selectedJob?.id == job.id
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF1A73E8).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = if (isSel) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A73E8)) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedJob = job }
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(job.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(job.trade, fontSize = 10.sp, color = Color(0xFF1A73E8))
                                }
                                Text("Client: ${job.clientName} • ${job.address}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Assign to Contractor (Firestore Synced):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    contractors.forEach { c ->
                        val isSel = selectedContractor?.contractorId == c.contractorId
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF1A73E8).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = if (isSel) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A73E8)) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedContractor = c }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(c.contractorName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("${c.trade} • ${c.status}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val job = selectedJob
                        val contractor = selectedContractor
                        if (job != null && contractor != null) {
                            onAssign(job, contractor, selectedHour, duration)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("cal_confirm_assign_room_btn")
                ) {
                    Text("Schedule onto Google Calendar")
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Helper Utilities
// -------------------------------------------------------------------------------------------------
private fun formatHour(hour: Int): String {
    return when (hour) {
        0 -> "12 AM"
        in 1..11 -> "$hour AM"
        12 -> "12 PM"
        else -> "${hour - 12} PM"
    }
}
