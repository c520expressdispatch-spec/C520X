package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.UserRole
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.ProjectTaskEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed
import kotlin.math.roundToInt

data class KanbanColumnInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val color: Color
)

enum class KanbanLayoutMode {
    STACKED_GRID,      // Responsive stacked grid layout for small Android screens
    HORIZONTAL_BOARD,  // 5-Column side-by-side board view with horizontal scroll
    FOCUSED_COLUMN     // Single focused column view
}

val KANBAN_COLUMNS = listOf(
    KanbanColumnInfo("Backlog", "Backlog", "Triage & Prep", Color(0xFF64748B)),
    KanbanColumnInfo("To Do", "To Do", "Ready for Work", StatusAmber),
    KanbanColumnInfo("In Progress", "In Progress", "Active Field Work", StatusBlue),
    KanbanColumnInfo("In Review", "In Review", "Quality & Inspect", StatusPurple),
    KanbanColumnInfo("Done", "Done", "Verified Complete", StatusGreen)
)

val KANBAN_PRIORITIES = listOf("Urgent", "High", "Medium", "Low")

fun priorityColor(priority: String): Color {
    return when (priority.lowercase()) {
        "urgent" -> StatusRed
        "high" -> OrangePrimary
        "medium" -> StatusAmber
        "low" -> StatusGreen
        else -> Color.Gray
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanBoardScreen(viewModel: MainViewModel) {
    val tasks by viewModel.filteredKanbanTasks.collectAsState()
    val allTasks by viewModel.projectTasks.collectAsState()
    val selectedProject by viewModel.kanbanSelectedProject.collectAsState()
    val selectedPriority by viewModel.kanbanSelectedPriority.collectAsState()
    val searchQuery by viewModel.kanbanSearchQuery.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToAddToColumn by remember { mutableStateOf("To Do") }
    var taskToDelete by remember { mutableStateOf<ProjectTaskEntity?>(null) }
    var selectedColumnFilter by remember { mutableStateOf("ALL") }

    // Drag-and-Drop Global State
    var draggingTask by remember { mutableStateOf<ProjectTaskEntity?>(null) }
    var dragGlobalPosition by remember { mutableStateOf(Offset.Zero) }
    var hoveredColumnId by remember { mutableStateOf<String?>(null) }
    val columnBounds = remember { mutableStateMapOf<String, Rect>() }

    // Projects list for filter chips
    val projectList = remember(allTasks) {
        val names = allTasks.map { it.projectName }.distinct().filter { it.isNotBlank() }
        listOf("All") + names
    }

    // Task stats
    val totalCount = allTasks.size
    val doneCount = allTasks.count { it.state == "Done" }
    val inProgressCount = allTasks.count { it.state == "In Progress" }
    val urgentCount = allTasks.count { it.priority.equals("Urgent", ignoreCase = true) }
    val completionPercent = if (totalCount > 0) (doneCount.toFloat() / totalCount) else 0f

    // Role-isolated filtered tasks (Strict dashboard integrity)
    val roleTasks = remember(tasks, currentRole) {
        when (currentRole) {
            UserRole.CONTRACTOR -> tasks.filter { it.trade.isNotBlank() && it.trade != "Logistics" }
            UserRole.OWNER_OPERATOR -> tasks.filter { it.trade.contains("Logistics", ignoreCase = true) || it.trade.contains("Transport", ignoreCase = true) || it.trade.contains("Haul", ignoreCase = true) || it.projectName.contains("Delivery", ignoreCase = true) }.ifEmpty { tasks }
            UserRole.CLIENT -> tasks.filter { it.state != "Backlog" }
            else -> tasks
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("kanban_board_screen")
    ) {
        // Default to compact header on mobile so columns and cards have maximum vertical height
        var isHeaderCompact by remember { mutableStateOf(true) }
        var kanbanLayoutMode by remember { mutableStateOf(KanbanLayoutMode.STACKED_GRID) }
        var activeFocusColumnId by remember { mutableStateOf("To Do") }
        val expandedColumns = remember {
            mutableStateMapOf<String, Boolean>().apply {
                KANBAN_COLUMNS.forEach { put(it.id, true) }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Header stats banner - Sleek & Mobile Fast
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                Image(
                                    painter = painterResource(id = R.drawable.ic_trades_tools),
                                    contentDescription = "Kanban SVG",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "KANBAN",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(3.dp),
                                        color = OrangePrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = currentRole.label.uppercase(),
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OrangePrimary,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = when (kanbanLayoutMode) {
                                        KanbanLayoutMode.STACKED_GRID -> "Responsive Stacked Columns • Mobile Adaptive"
                                        KanbanLayoutMode.HORIZONTAL_BOARD -> "5-Column Board View • Horizontal Scroll"
                                        KanbanLayoutMode.FOCUSED_COLUMN -> "Single Column Focus • Tap ▶ to advance"
                                    },
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // SVG / Icon Triggers with Hover Details (No standard buttons)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // View Switcher (Stacked Grid vs Horizontal Board vs Single Focus) SVG Trigger
                            val modeTooltipState = rememberTooltipState()
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    PlainTooltip(shape = RoundedCornerShape(6.dp), containerColor = Navy900, contentColor = Color.White) {
                                        Text(
                                            when (kanbanLayoutMode) {
                                                KanbanLayoutMode.STACKED_GRID -> "Current: Responsive Stacked Grid • Tap for Horizontal Board"
                                                KanbanLayoutMode.HORIZONTAL_BOARD -> "Current: Horizontal Board • Tap for Single Focus"
                                                KanbanLayoutMode.FOCUSED_COLUMN -> "Current: Single Focus • Tap for Responsive Stacked Grid"
                                            },
                                            fontSize = 10.sp
                                        )
                                    }
                                },
                                state = modeTooltipState
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (kanbanLayoutMode == KanbanLayoutMode.STACKED_GRID) OrangePrimary.copy(alpha = 0.2f)
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .border(
                                            1.dp,
                                            if (kanbanLayoutMode == KanbanLayoutMode.STACKED_GRID) OrangePrimary else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            kanbanLayoutMode = when (kanbanLayoutMode) {
                                                KanbanLayoutMode.STACKED_GRID -> KanbanLayoutMode.HORIZONTAL_BOARD
                                                KanbanLayoutMode.HORIZONTAL_BOARD -> KanbanLayoutMode.FOCUSED_COLUMN
                                                KanbanLayoutMode.FOCUSED_COLUMN -> KanbanLayoutMode.STACKED_GRID
                                            }
                                        }
                                        .testTag("kanban_mode_toggle_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (kanbanLayoutMode) {
                                            KanbanLayoutMode.STACKED_GRID -> Icons.Default.ViewAgenda
                                            KanbanLayoutMode.HORIZONTAL_BOARD -> Icons.Default.ViewKanban
                                            KanbanLayoutMode.FOCUSED_COLUMN -> Icons.Default.FilterList
                                        },
                                        contentDescription = "Toggle Kanban Layout",
                                        tint = if (kanbanLayoutMode == KanbanLayoutMode.STACKED_GRID) OrangePrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Add New Task SVG Trigger
                            val addTooltipState = rememberTooltipState()
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    PlainTooltip(shape = RoundedCornerShape(6.dp), containerColor = Navy900, contentColor = Color.White) {
                                        Text("Create New Project Task • Add to ${if (kanbanLayoutMode == KanbanLayoutMode.FOCUSED_COLUMN) activeFocusColumnId else "To Do"}", fontSize = 10.sp)
                                    }
                                },
                                state = addTooltipState
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(OrangePrimary)
                                        .clickable {
                                            taskToAddToColumn = if (kanbanLayoutMode == KanbanLayoutMode.FOCUSED_COLUMN) activeFocusColumnId else "To Do"
                                            showAddTaskDialog = true
                                        }
                                        .testTag("kanban_add_task_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }

                            // Header Expand / Compact Filter Toggle
                            IconButton(
                                onClick = { isHeaderCompact = !isHeaderCompact },
                                modifier = Modifier.size(30.dp).testTag("kanban_toggle_compact_btn")
                            ) {
                                Icon(
                                    imageVector = if (isHeaderCompact) Icons.Default.FilterList else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Toggle Compact",
                                    tint = if (!isHeaderCompact) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = !isHeaderCompact) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))

                            // Progress metric bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "COMPLETION PROGRESS: ${(completionPercent * 100).toInt()}% ($doneCount of $totalCount Done)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (urgentCount > 0) {
                                        Surface(
                                            color = StatusRed.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "$urgentCount URGENT",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusRed,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Surface(
                                        color = StatusBlue.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "$inProgressCount ACTIVE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { completionPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = StatusGreen,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = !isHeaderCompact) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Search Bar & Filter Chips
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setKanbanSearchQuery(it) },
                placeholder = { Text("Search task, trade, tech, or project...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = OrangePrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setKanbanSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kanban_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Priority Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Priority:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                listOf("All", "Urgent", "High", "Medium", "Low").forEach { priority ->
                    val isSelected = priority.equals(selectedPriority, ignoreCase = true)
                    val pColor = if (priority == "All") OrangePrimary else priorityColor(priority)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) pColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) null else BorderStroke(1.dp, pColor.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable { viewModel.setKanbanPriorityFilter(priority) }
                            .testTag("kanban_priority_filter_$priority")
                    ) {
                        Text(
                            text = priority,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                HorizontalDivider(modifier = Modifier.height(16.dp).width(1.dp))
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Project:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                projectList.forEach { proj ->
                    val isSelected = proj == selectedProject
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) Navy700 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = if (isSelected) BorderStroke(1.dp, OrangePrimary) else null,
                        modifier = Modifier
                            .clickable { viewModel.setKanbanProjectFilter(proj) }
                            .testTag("kanban_project_filter_$proj")
                    ) {
                        Text(
                            text = if (proj.length > 20) proj.take(18) + "…" else proj,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick column viewport selector (useful for fast navigation on narrow screens)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (selectedColumnFilter == "ALL") OrangePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.clickable { selectedColumnFilter = "ALL" }
                ) {
                    Text(
                        text = "Show All 5 Columns",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedColumnFilter == "ALL") Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                KANBAN_COLUMNS.forEach { col ->
                    val isSelected = selectedColumnFilter == col.id
                    val count = tasks.count { it.state == col.id }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) col.color else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, col.color.copy(alpha = 0.5f)),
                        modifier = Modifier.clickable { selectedColumnFilter = col.id }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else col.color)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${col.title} ($count)",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Active Drag Info Strip (when user is dragging a card)
            AnimatedVisibility(visible = draggingTask != null) {
                draggingTask?.let { task ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OrangePrimary.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, OrangePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DragIndicator,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DRAGGING: \"${task.title.take(28)}\"",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                            }
                            Text(
                                text = if (hoveredColumnId != null) "Hovering: $hoveredColumnId" else "Drag over a column to drop",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (hoveredColumnId != null) StatusGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Fast Mobile Column Focus Selector Tabs (active in Focus mode)
            if (kanbanLayoutMode == KanbanLayoutMode.FOCUSED_COLUMN) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        KANBAN_COLUMNS.forEach { col ->
                            val isSelected = activeFocusColumnId == col.id
                            val count = tasks.count { it.state == col.id }
                            Surface(
                                shape = RoundedCornerShape(7.dp),
                                color = if (isSelected) col.color else Color.Transparent,
                                border = if (isSelected) null else BorderStroke(1.dp, col.color.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .clickable { activeFocusColumnId = col.id }
                                    .testTag("focus_col_tab_${col.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else col.color)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = col.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color.White.copy(alpha = 0.25f) else col.color.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "$count",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else col.color,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // KANBAN BOARD RESPONSIVE COLUMNS DISPLAY
            val columnsToShow = if (selectedColumnFilter == "ALL") {
                KANBAN_COLUMNS
            } else {
                KANBAN_COLUMNS.filter { it.id == selectedColumnFilter }
            }

            when (kanbanLayoutMode) {
                KanbanLayoutMode.STACKED_GRID -> {
                    // Responsive Stacked Grid for small Android screens: stacks columns vertically without requiring horizontal scrolling
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .testTag("kanban_stacked_grid"),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(columnsToShow, key = { it.id }) { col ->
                            val columnTasks = roleTasks.filter { it.state == col.id }.sortedBy { it.sortOrder }
                            val isExpanded = expandedColumns[col.id] ?: true

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                                border = BorderStroke(1.dp, col.color.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("kanban_stacked_col_${col.id}")
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedColumns[col.id] = !isExpanded
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(col.color)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = col.title.uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = col.color.copy(alpha = 0.18f)
                                            ) {
                                                Text(
                                                    text = "${columnTasks.size}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = col.color,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    taskToAddToColumn = col.id
                                                    showAddTaskDialog = true
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add task to ${col.title}",
                                                    tint = OrangePrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { expandedColumns[col.id] = !isExpanded },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    AnimatedVisibility(visible = isExpanded) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (columnTasks.isEmpty()) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "No tasks in ${col.title} • Tap + to create",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.padding(12.dp)
                                                    )
                                                }
                                            } else {
                                                columnTasks.forEach { task ->
                                                    KanbanTaskCard(
                                                        task = task,
                                                        isDragging = draggingTask?.id == task.id,
                                                        onDragStart = { offset ->
                                                            draggingTask = task
                                                            dragGlobalPosition = offset
                                                            viewModel.setDraggingTaskId(task.id)
                                                        },
                                                        onDrag = { delta ->
                                                            dragGlobalPosition += delta
                                                        },
                                                        onDragEnd = {
                                                            draggingTask = null
                                                            viewModel.setDraggingTaskId(null)
                                                        },
                                                        onDragCancel = {
                                                            draggingTask = null
                                                            viewModel.setDraggingTaskId(null)
                                                        },
                                                        onMoveToColumn = { colId ->
                                                            viewModel.moveKanbanTask(task.id, colId)
                                                        },
                                                        onAdvance = { viewModel.advanceKanbanTask(task.id) },
                                                        onRegress = { viewModel.regressKanbanTask(task.id) },
                                                        onUpdatePriority = { p -> viewModel.updateTaskPriority(task.id, p) },
                                                        onDelete = { taskToDelete = task }
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

                KanbanLayoutMode.HORIZONTAL_BOARD -> {
                    // Multi-column side-by-side board view (horizontal scrolling enabled only where necessary)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        columnsToShow.forEach { col ->
                            val columnTasks = roleTasks.filter { it.state == col.id }.sortedBy { it.sortOrder }
                            val isHovered = hoveredColumnId == col.id && draggingTask?.state != col.id

                            KanbanColumnView(
                                column = col,
                                tasks = columnTasks,
                                isHovered = isHovered,
                                draggingTask = draggingTask,
                                isFullWidth = false,
                                onColumnPositioned = { rect ->
                                    columnBounds[col.id] = rect
                                },
                                onAddTask = {
                                    taskToAddToColumn = col.id
                                    showAddTaskDialog = true
                                },
                                onDragStart = { task, position ->
                                    draggingTask = task
                                    dragGlobalPosition = position
                                    viewModel.setDraggingTaskId(task.id)
                                },
                                onDrag = { amount ->
                                    dragGlobalPosition += amount
                                    val target = columnBounds.entries.find { (_, rect) ->
                                        rect.contains(dragGlobalPosition)
                                    }?.key
                                    hoveredColumnId = target
                                    viewModel.setDragHoverColumn(target)
                                },
                                onDragEnd = {
                                    val targetCol = hoveredColumnId
                                    val task = draggingTask
                                    if (task != null && targetCol != null && targetCol != task.state) {
                                        viewModel.moveKanbanTask(task.id, targetCol)
                                    }
                                    draggingTask = null
                                    hoveredColumnId = null
                                    viewModel.setDraggingTaskId(null)
                                    viewModel.setDragHoverColumn(null)
                                },
                                onDragCancel = {
                                    draggingTask = null
                                    hoveredColumnId = null
                                    viewModel.setDraggingTaskId(null)
                                    viewModel.setDragHoverColumn(null)
                                },
                                onMoveTask = { taskId, targetCol ->
                                    viewModel.moveKanbanTask(taskId, targetCol)
                                },
                                onAdvanceTask = { taskId ->
                                    viewModel.advanceKanbanTask(taskId)
                                },
                                onRegressTask = { taskId ->
                                    viewModel.regressKanbanTask(taskId)
                                },
                                onUpdatePriority = { taskId, priority ->
                                    viewModel.updateTaskPriority(taskId, priority)
                                },
                                onDeleteTask = { task ->
                                    taskToDelete = task
                                }
                            )
                        }
                    }
                }

                KanbanLayoutMode.FOCUSED_COLUMN -> {
                    // Single column focus view
                    val col = KANBAN_COLUMNS.find { it.id == activeFocusColumnId } ?: KANBAN_COLUMNS[1]
                    val columnTasks = roleTasks.filter { it.state == col.id }.sortedBy { it.sortOrder }
                    val isHovered = hoveredColumnId == col.id && draggingTask?.state != col.id

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        KanbanColumnView(
                            column = col,
                            tasks = columnTasks,
                            isHovered = isHovered,
                            draggingTask = draggingTask,
                            isFullWidth = true,
                            onColumnPositioned = { rect ->
                                columnBounds[col.id] = rect
                            },
                            onAddTask = {
                                taskToAddToColumn = col.id
                                showAddTaskDialog = true
                            },
                            onDragStart = { task, position ->
                                draggingTask = task
                                dragGlobalPosition = position
                                viewModel.setDraggingTaskId(task.id)
                            },
                            onDrag = { amount ->
                                dragGlobalPosition += amount
                                val target = columnBounds.entries.find { (_, rect) ->
                                    rect.contains(dragGlobalPosition)
                                }?.key
                                hoveredColumnId = target
                                viewModel.setDragHoverColumn(target)
                            },
                            onDragEnd = {
                                val targetCol = hoveredColumnId
                                val task = draggingTask
                                if (task != null && targetCol != null && targetCol != task.state) {
                                    viewModel.moveKanbanTask(task.id, targetCol)
                                }
                                draggingTask = null
                                hoveredColumnId = null
                                viewModel.setDraggingTaskId(null)
                                viewModel.setDragHoverColumn(null)
                            },
                            onDragCancel = {
                                draggingTask = null
                                hoveredColumnId = null
                                viewModel.setDraggingTaskId(null)
                                viewModel.setDragHoverColumn(null)
                            },
                            onMoveTask = { taskId, targetCol ->
                                viewModel.moveKanbanTask(taskId, targetCol)
                            },
                            onAdvanceTask = { taskId ->
                                viewModel.advanceKanbanTask(taskId)
                            },
                            onRegressTask = { taskId ->
                                viewModel.regressKanbanTask(taskId)
                            },
                            onUpdatePriority = { taskId, priority ->
                                viewModel.updateTaskPriority(taskId, priority)
                            },
                            onDeleteTask = { task ->
                                taskToDelete = task
                            }
                        )
                    }
                }
            }
        }

        // Add Task Dialog
        if (showAddTaskDialog) {
            AddTaskDialog(
                initialState = taskToAddToColumn,
                projectList = projectList.filter { it != "All" },
                onDismiss = { showAddTaskDialog = false },
                onConfirm = { title, project, desc, state, priority, tech, dueDate, estHours, trade ->
                    viewModel.addProjectTask(
                        title = title,
                        projectName = project,
                        description = desc,
                        state = state,
                        priority = priority,
                        assignedTech = tech,
                        dueDate = dueDate,
                        estimatedHours = estHours,
                        trade = trade
                    )
                    showAddTaskDialog = false
                }
            )
        }

        // Delete Task Confirmation
        taskToDelete?.let { task ->
            AlertDialog(
                onDismissRequest = { taskToDelete = null },
                title = { Text("Delete Project Task?") },
                text = {
                    Text("Are you sure you want to delete \"${task.title}\" from the ${task.state} column? This removes it from the Room database.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteProjectTask(task.id)
                            taskToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { taskToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun KanbanColumnView(
    column: KanbanColumnInfo,
    tasks: List<ProjectTaskEntity>,
    isHovered: Boolean,
    draggingTask: ProjectTaskEntity?,
    isFullWidth: Boolean = false,
    onColumnPositioned: (Rect) -> Unit,
    onAddTask: () -> Unit,
    onDragStart: (ProjectTaskEntity, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onMoveTask: (Int, String) -> Unit,
    onAdvanceTask: (Int) -> Unit,
    onRegressTask: (Int) -> Unit,
    onUpdatePriority: (Int, String) -> Unit,
    onDeleteTask: (ProjectTaskEntity) -> Unit
) {
    val columnBorderColor by animateColorAsState(
        targetValue = if (isHovered) OrangePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        label = "colBorder"
    )
    val columnBgColor by animateColorAsState(
        targetValue = if (isHovered) OrangePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f),
        label = "colBg"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = columnBgColor,
        border = BorderStroke(if (isHovered) 2.dp else 1.dp, columnBorderColor),
        modifier = (if (isFullWidth) Modifier.fillMaxWidth() else Modifier.width(235.dp))
            .fillMaxHeight()
            .onGloballyPositioned { coordinates ->
                onColumnPositioned(coordinates.boundsInRoot())
            }
            .testTag("kanban_column_${column.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Column Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(column.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = column.title.uppercase(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = column.color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${tasks.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = column.color,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onAddTask,
                    modifier = Modifier.size(28.dp).testTag("add_task_to_${column.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to ${column.title}",
                        tint = column.color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = column.subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 18.dp, bottom = 4.dp)
            )

            // Drop zone indicator when hovered
            if (isHovered) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OrangePrimary.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, OrangePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DROP HERE TO MOVE TO ${column.title.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }
                }
            }

            // Task list in column
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No tasks in ${column.title}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = onAddTask) {
                            Text("+ Add Task", fontSize = 11.sp, color = OrangePrimary)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(top = 2.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        val isThisTaskDragging = draggingTask?.id == task.id

                        KanbanTaskCard(
                            task = task,
                            isDragging = isThisTaskDragging,
                            onDragStart = { pos -> onDragStart(task, pos) },
                            onDrag = onDrag,
                            onDragEnd = onDragEnd,
                            onDragCancel = onDragCancel,
                            onMoveToColumn = { colId -> onMoveTask(task.id, colId) },
                            onAdvance = { onAdvanceTask(task.id) },
                            onRegress = { onRegressTask(task.id) },
                            onUpdatePriority = { p -> onUpdatePriority(task.id, p) },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanTaskCard(
    task: ProjectTaskEntity,
    isDragging: Boolean,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onMoveToColumn: (String) -> Unit,
    onAdvance: () -> Unit,
    onRegress: () -> Unit,
    onUpdatePriority: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }
    var showMoveMenu by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val pColor = priorityColor(task.priority)
    val cardElevation = if (isDragging) 8.dp else 1.dp
    val scale by animateFloatAsState(if (isDragging) 1.02f else 1.0f, label = "cardScale")

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        border = BorderStroke(
            1.dp,
            if (isDragging) OrangePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .zIndex(if (isDragging) 99f else 1f)
            .testTag("kanban_task_card_${task.id}")
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset -> onDragStart(offset) },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragCancel() }
                )
            }
            .clickable { isExpanded = !isExpanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left Priority Stripe (Instant Visual Cue)
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .fillMaxHeight()
                    .background(pColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                // Line 1: Title + Priority Pill + Options Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isExpanded) 3 else 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Priority Pill
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = pColor.copy(alpha = 0.15f),
                        border = BorderStroke(0.5.dp, pColor.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable { showPriorityMenu = true }
                            .testTag("priority_pill_${task.id}")
                    ) {
                        Text(
                            text = task.priority.uppercase(),
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = pColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    // Options menu anchor
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(20.dp).testTag("task_menu_${task.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Change Priority (${task.priority})", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = pColor) },
                                onClick = {
                                    showMenu = false
                                    showPriorityMenu = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Move to Column...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.ViewKanban, contentDescription = null, tint = OrangePrimary) },
                                onClick = {
                                    showMenu = false
                                    showMoveMenu = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Task", fontSize = 12.sp, color = StatusRed) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRed) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }

                        DropdownMenu(
                            expanded = showPriorityMenu,
                            onDismissRequest = { showPriorityMenu = false }
                        ) {
                            KANBAN_PRIORITIES.forEach { p ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(priorityColor(p))
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = p,
                                                fontWeight = if (p == task.priority) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                        }
                                    },
                                    onClick = {
                                        onUpdatePriority(p)
                                        showPriorityMenu = false
                                    }
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showMoveMenu,
                            onDismissRequest = { showMoveMenu = false }
                        ) {
                            KANBAN_COLUMNS.forEach { c ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(c.color)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = c.title,
                                                fontWeight = if (c.id == task.state) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                        }
                                    },
                                    onClick = {
                                        onMoveToColumn(c.id)
                                        showMoveMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Line 2: Project • Tech • Hours • Fast 1-Tap Advance/Regress Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (task.projectName.length > 14) task.projectName.take(12) + "…" else task.projectName,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • ",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = task.assignedTech.split(" ").firstOrNull() ?: "",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • ",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "${task.estimatedHours}h",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }

                    // Direct 1-Tap Advance / Regress Buttons (Speed First!)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val canRegress = task.state != "Backlog"
                        val canAdvance = task.state != "Done"

                        if (canRegress) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onRegress() }
                                    .testTag("regress_task_${task.id}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Previous Column",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }

                        if (canRegress && canAdvance) {
                            Spacer(modifier = Modifier.width(3.dp))
                        }

                        if (canAdvance) {
                            Surface(
                                shape = CircleShape,
                                color = OrangePrimary.copy(alpha = 0.18f),
                                border = BorderStroke(0.5.dp, OrangePrimary.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onAdvance() }
                                    .testTag("advance_task_${task.id}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Advance Column",
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Expandable details (shown if card is tapped)
                if (isExpanded) {
                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${task.trade} • Due: ${task.dueDate}",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (task.checklistTotal > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = StatusGreen.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "☑ ${task.checklistDone}/${task.checklistTotal}",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    initialState: String,
    projectList: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        project: String,
        desc: String,
        state: String,
        priority: String,
        tech: String,
        dueDate: String,
        estHours: Double,
        trade: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf(projectList.firstOrNull() ?: "Foothills Luxury Kitchen Remodel") }
    var description by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(initialState) }
    var priority by remember { mutableStateOf("High") }
    var tech by remember { mutableStateOf("Alex Ramirez (Lead)") }
    var dueDate by remember { mutableStateOf("Tomorrow") }
    var estHoursText by remember { mutableStateOf("3.5") }
    var trade by remember { mutableStateOf("Electrical") }

    val tradeOptions = listOf("Electrical", "Plumbing", "HVAC", "Carpentry", "Tile", "Drywall", "Permits", "Painting")
    val techOptions = listOf("Alex Ramirez (Lead)", "Carlos Mendez", "Sarah Lin", "Marcus Vance", "Dispatch Desk")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Project Task", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("e.g. Rough-in subpanel feed") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_task_title_input")
                )

                // Project Selector
                Text("Project:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val availableProjects = if (projectList.isNotEmpty()) projectList else listOf(
                        "Foothills Luxury Kitchen Remodel",
                        "Catalina Ridge Solar & EV Upgrade",
                        "Tucson Foothills Bath Suite Remodel"
                    )
                    availableProjects.forEach { proj ->
                        val isSelected = proj == selectedProject
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.clickable { selectedProject = proj }
                        ) {
                            Text(
                                text = if (proj.length > 22) proj.take(20) + "…" else proj,
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Priority Level Selector (Urgent, High, Medium, Low)
                Text("Priority Level (Room Persisted):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    KANBAN_PRIORITIES.forEach { p ->
                        val isSelected = p == priority
                        val pColor = priorityColor(p)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) pColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, pColor),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { priority = p }
                        ) {
                            Text(
                                text = p,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else pColor,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                // Starting Column State
                Text("Starting Kanban Column:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    KANBAN_COLUMNS.forEach { col ->
                        val isSelected = col.id == state
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) col.color else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.clickable { state = col.id }
                        ) {
                            Text(
                                text = col.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Specs") },
                    placeholder = { Text("Specific code requirements, torque specs, model numbers...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Trade Selector
                Text("Trade Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tradeOptions.forEach { t ->
                        val isSelected = t == trade
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) Navy700 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isSelected) BorderStroke(1.dp, OrangePrimary) else null,
                            modifier = Modifier.clickable { trade = t }
                        ) {
                            Text(
                                text = t,
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Assignee & Est Hours
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tech,
                        onValueChange = { tech = it },
                        label = { Text("Assignee") },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f)
                    )
                    OutlinedTextField(
                        value = estHoursText,
                        onValueChange = { estHoursText = it },
                        label = { Text("Est. Hours") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val hours = estHoursText.toDoubleOrNull() ?: 2.0
                        onConfirm(
                            title,
                            selectedProject,
                            description,
                            state,
                            priority,
                            tech,
                            dueDate,
                            hours,
                            trade
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("save_new_task_btn")
            ) {
                Text("Save to Kanban (Room)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
