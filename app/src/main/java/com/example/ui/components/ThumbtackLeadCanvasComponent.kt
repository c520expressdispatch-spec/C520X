package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CanvasLeadDataSource
import com.example.data.models.CanvasLeadEntry
import com.example.data.models.CanvasViewMode
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CanyonNavyBorder
import com.example.ui.theme.CanyonNavyCard
import com.example.ui.theme.CanyonNavyDark
import com.example.ui.theme.CanyonNavyElevated
import com.example.ui.theme.CanyonOrange
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MintGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SafetyGold
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.ThumbtackBlue

@Composable
fun ThumbtackLeadCanvasComponent(
    modifier: Modifier = Modifier,
    onConvertToJob: ((CanvasLeadEntry) -> Unit)? = null
) {
    val context = LocalContext.current
    var leadsList by remember { mutableStateOf(CanvasLeadDataSource.initialLeads) }
    var selectedChannel by remember { mutableStateOf("Thumbtack") }
    var selectedStatus by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showSpam by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(CanvasViewMode.SPREADSHEET_GRID) }

    var inspectedLead by remember { mutableStateOf<CanvasLeadEntry?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showExportPreviewDialog by remember { mutableStateOf(false) }

    // Filter computation
    val channelFiltered = if (selectedChannel == "All Sources") {
        leadsList
    } else {
        leadsList.filter { it.sourceChannel.contains(selectedChannel, ignoreCase = true) }
    }

    val spamFiltered = if (showSpam) {
        channelFiltered
    } else {
        channelFiltered.filter { !it.isSpam }
    }

    val statusFiltered = when (selectedStatus) {
        "Needs Action" -> spamFiltered.filter { it.status.equals("New", ignoreCase = true) }
        "In Pipeline" -> spamFiltered.filter { it.status.equals("Quoted", ignoreCase = true) || it.status.equals("In Pipeline", ignoreCase = true) }
        "Closed Won" -> spamFiltered.filter { it.status.equals("Closed Won", ignoreCase = true) || it.status.equals("Won", ignoreCase = true) }
        else -> spamFiltered
    }

    val finalFilteredLeads = if (searchQuery.isBlank()) {
        statusFiltered
    } else {
        statusFiltered.filter {
            it.customerName.contains(searchQuery, ignoreCase = true) ||
            it.serviceRequested.contains(searchQuery, ignoreCase = true) ||
            it.city.contains(searchQuery, ignoreCase = true) ||
            it.zipCode.contains(searchQuery, ignoreCase = true) ||
            it.projectScope.contains(searchQuery, ignoreCase = true) ||
            it.notes.contains(searchQuery, ignoreCase = true)
        }
    }

    // KPI Metrics calculation based on selected channel
    val totalLeadsCount = spamFiltered.size
    val newLeadsCount = spamFiltered.count { it.status.equals("New", ignoreCase = true) }
    val inPipelineCount = spamFiltered.count { it.status.equals("Quoted", ignoreCase = true) || it.status.equals("In Pipeline", ignoreCase = true) }
    val wonJobsCount = spamFiltered.count { it.status.equals("Closed Won", ignoreCase = true) || it.status.equals("Won", ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("thumbtack_lead_canvas_component")
    ) {
        // -------------------------------------------------------------
        // 1. CANVAS HEADER (Direct translation of CSV top banner)
        // -------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CanyonNavyCard),
            border = BorderStroke(1.dp, ThumbtackBlue.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(ThumbtackBlue, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedChannel == "Thumbtack") "Thumbtack Lead Log & Pipeline" else "$selectedChannel Lead Log & Pipeline",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Real-time tracker for incoming customer inquiries, job estimates, and pipeline status",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    // Live badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StatusGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(StatusGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "LIVE CANVAS",
                                color = StatusGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // -------------------------------------------------------------
                // 2. SUMMARY KPI METRIC CARDS (Exact match to CSV layout)
                // TOTAL LEADS | NEW LEADS | IN PIPELINE | WON JOBS
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card 1: TOTAL LEADS
                    CanvasKpiCard(
                        modifier = Modifier.weight(1f),
                        headerLabel = "TOTAL LEADS",
                        emojiIcon = "📋",
                        metricLabel = "Inquiries",
                        value = totalLeadsCount.toString(),
                        caption = "All recorded leads",
                        accentColor = AccentCyan,
                        isSelected = selectedStatus == "All",
                        onClick = { selectedStatus = "All" }
                    )

                    // Card 2: NEW LEADS
                    CanvasKpiCard(
                        modifier = Modifier.weight(1f),
                        headerLabel = "NEW LEADS",
                        emojiIcon = "🆕",
                        metricLabel = "Needs Action",
                        value = newLeadsCount.toString(),
                        caption = "Awaiting first response",
                        accentColor = StatusAmber,
                        isSelected = selectedStatus == "Needs Action",
                        onClick = { selectedStatus = "Needs Action" }
                    )

                    // Card 3: IN PIPELINE
                    CanvasKpiCard(
                        modifier = Modifier.weight(1f),
                        headerLabel = "IN PIPELINE",
                        emojiIcon = "💬",
                        metricLabel = "In Progress",
                        value = inPipelineCount.toString(),
                        caption = "Quoted or in discussion",
                        accentColor = AccentBlue,
                        isSelected = selectedStatus == "In Pipeline",
                        onClick = { selectedStatus = "In Pipeline" }
                    )

                    // Card 4: WON JOBS
                    CanvasKpiCard(
                        modifier = Modifier.weight(1f),
                        headerLabel = "WON JOBS",
                        emojiIcon = "⭐",
                        metricLabel = "Closed Won",
                        value = wonJobsCount.toString(),
                        caption = "Successfully won jobs",
                        accentColor = StatusGreen,
                        isSelected = selectedStatus == "Closed Won",
                        onClick = { selectedStatus = "Closed Won" }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -------------------------------------------------------------
        // 3. CONTROLS: CHANNELS, SEARCH & ACTIONS
        // -------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = CanyonNavyElevated),
            border = BorderStroke(1.dp, CanyonNavyBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Channel Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NETWORK CHANNELS:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )

                    // View Mode Switcher
                    Row(
                        modifier = Modifier
                            .background(CanyonNavyDark, RoundedCornerShape(6.dp))
                            .border(1.dp, CanyonNavyBorder, RoundedCornerShape(6.dp))
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (viewMode == CanvasViewMode.SPREADSHEET_GRID) ThumbtackBlue else Color.Transparent,
                            modifier = Modifier.clickable { viewMode = CanvasViewMode.SPREADSHEET_GRID }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.TableChart, contentDescription = null, tint = TextWhite, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Canvas Sheet", fontSize = 11.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (viewMode == CanvasViewMode.PIPELINE_CARDS) OrangePrimary else Color.Transparent,
                            modifier = Modifier.clickable { viewMode = CanvasViewMode.PIPELINE_CARDS }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ViewAgenda, contentDescription = null, tint = TextWhite, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cards", fontSize = 11.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Channels Row
                val channels = listOf("Thumbtack", "All Sources", "Angi", "Yelp", "TaskRabbit", "PPW", "SMS / Text", "Email Inquiries")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(channels) { ch ->
                        val isSel = selectedChannel == ch
                        val count = if (ch == "All Sources") leadsList.size else leadsList.count { it.sourceChannel.contains(ch, ignoreCase = true) }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) ThumbtackBlue else CanyonNavyDark,
                            border = BorderStroke(1.dp, if (isSel) ThumbtackBlue else CanyonNavyBorder),
                            modifier = Modifier.clickable { selectedChannel = ch }
                        ) {
                            Text(
                                text = "$ch ($count)",
                                color = if (isSel) TextWhite else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search & Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Filter leads by customer, scope, zip, quote...", fontSize = 11.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(15.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CanyonNavyDark,
                            unfocusedContainerColor = CanyonNavyDark,
                            focusedBorderColor = ThumbtackBlue,
                            unfocusedBorderColor = CanyonNavyBorder
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Autonomous Gemini Bid Button
                    Button(
                        onClick = {
                            // Auto bid any unquoted leads
                            leadsList = leadsList.map { lead ->
                                if (lead.status.equals("New", ignoreCase = true)) {
                                    val suggestedAmount = when {
                                        lead.estBudget.isNotBlank() -> lead.estBudget
                                        lead.serviceRequested.contains("Kitchen", ignoreCase = true) -> "$14,850.00"
                                        lead.serviceRequested.contains("Bath", ignoreCase = true) -> "$7,400.00"
                                        lead.serviceRequested.contains("Remodel", ignoreCase = true) -> "$4,850.00"
                                        else -> "$3,400.00"
                                    }
                                    lead.copy(
                                        status = "Quoted",
                                        quoteAmount = suggestedAmount,
                                        nextFollowUp = "2026-09-13",
                                        notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $suggestedAmount (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.\n" + lead.notes
                                    )
                                } else lead
                            }
                            Toast.makeText(context, "Gemini Autonomous Bids generated for all pending inquiries!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CanyonOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-Bid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Copy / Export CSV Button
                    OutlinedButton(
                        onClick = {
                            val csvContent = CanvasLeadDataSource.exportToCsv(finalFilteredLeads)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Thumbtack_Lead_Log_Canvas_CSV", csvContent)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Canvas CSV copied to clipboard (${finalFilteredLeads.size} leads)!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CanyonNavyBorder),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SafetyGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy CSV", fontSize = 11.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                    }

                    // Add Lead
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ThumbtackBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -------------------------------------------------------------
        // 4. MAIN CANVAS CONTENT (SPREADSHEET GRID OR PIPELINE CARDS)
        // -------------------------------------------------------------
        if (finalFilteredLeads.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CanyonNavyCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No matching leads found for current filter.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else if (viewMode == CanvasViewMode.SPREADSHEET_GRID) {
            // SPREADSHEET CANVAS GRID VIEW
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CanyonNavyDark),
                border = BorderStroke(1.dp, CanyonNavyBorder)
            ) {
                Column {
                    // Header Bar with count info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CanyonNavyCard)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CANVAS SPREADSHEET GRID (${finalFilteredLeads.size} of ${leadsList.size} leads)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SafetyGold
                        )
                        Text(
                            text = "← Scroll horizontally for all columns • Tap any row to inspect & edit →",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    HorizontalDivider(color = CanyonNavyBorder, thickness = 1.dp)

                    // Horizontal scrolling table
                    val horizontalScrollState = rememberScrollState()
                    Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        Column {
                            // SPREADSHEET HEADER ROW
                            Row(
                                modifier = Modifier
                                    .background(CanyonNavyElevated)
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableHeaderCell("Lead Date", width = 95.dp)
                                TableHeaderCell("Customer Name", width = 120.dp)
                                TableHeaderCell("Service Requested", width = 160.dp)
                                TableHeaderCell("City", width = 110.dp)
                                TableHeaderCell("Zip Code", width = 75.dp)
                                TableHeaderCell("Project Scope", width = 220.dp)
                                TableHeaderCell("Status", width = 100.dp)
                                TableHeaderCell("Est. Budget", width = 100.dp)
                                TableHeaderCell("Quote Amount", width = 110.dp)
                                TableHeaderCell("Next Follow-Up", width = 105.dp)
                                TableHeaderCell("Notes & Autonomous Bid", width = 260.dp)
                            }

                            HorizontalDivider(color = CanyonNavyBorder, thickness = 1.dp)

                            // TABLE ROWS
                            finalFilteredLeads.forEachIndexed { index, lead ->
                                val isEven = index % 2 == 0
                                val rowBg = if (isEven) CanyonNavyDark else CanyonNavyCard.copy(alpha = 0.6f)

                                Row(
                                    modifier = Modifier
                                        .background(rowBg)
                                        .clickable { inspectedLead = lead }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Lead Date
                                    TableCell(lead.leadDate, width = 95.dp, fontFamily = FontFamily.Monospace, fontSize = 11.sp)

                                    // Customer Name
                                    Box(modifier = Modifier.width(120.dp).padding(horizontal = 6.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(lead.channelColor, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = lead.customerName,
                                                color = TextWhite,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    // Service Requested
                                    TableCell(lead.serviceRequested, width = 160.dp, color = SafetyGold, fontWeight = FontWeight.Medium)

                                    // City
                                    TableCell(lead.city, width = 110.dp)

                                    // Zip Code
                                    TableCell(lead.zipCode, width = 75.dp, fontFamily = FontFamily.Monospace)

                                    // Project Scope
                                    TableCell(lead.projectScope, width = 220.dp, maxLines = 2)

                                    // Status Pill
                                    Box(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .padding(horizontal = 6.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = lead.statusBadgeColor.copy(alpha = 0.18f),
                                            border = BorderStroke(1.dp, lead.statusBadgeColor.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = lead.status,
                                                color = lead.statusBadgeColor,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Est. Budget
                                    TableCell(
                                        if (lead.estBudget.isNotBlank()) lead.estBudget else "-",
                                        width = 100.dp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (lead.estBudget.isNotBlank()) TextWhite else TextMuted
                                    )

                                    // Quote Amount
                                    TableCell(
                                        if (lead.quoteAmount.isNotBlank()) lead.quoteAmount else "Needs Quote",
                                        width = 110.dp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (lead.quoteAmount.isNotBlank()) StatusGreen else StatusAmber,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Next Follow-Up
                                    TableCell(lead.nextFollowUp, width = 105.dp, fontFamily = FontFamily.Monospace, fontSize = 11.sp)

                                    // Notes & Autonomous Bid
                                    TableCell(
                                        lead.notes,
                                        width = 260.dp,
                                        fontSize = 10.sp,
                                        color = TextMuted,
                                        maxLines = 2
                                    )
                                }

                                HorizontalDivider(color = CanyonNavyBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        } else {
            // PIPELINE CARDS VIEW
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                finalFilteredLeads.forEach { lead ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { inspectedLead = lead },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CanyonNavyCard),
                        border = BorderStroke(1.dp, CanyonNavyBorder)
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
                                        color = lead.channelColor.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, lead.channelColor.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = lead.sourceChannel.uppercase(),
                                            color = lead.channelColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = lead.customerName,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${lead.leadDate}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = lead.statusBadgeColor.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, lead.statusBadgeColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = lead.status,
                                        color = lead.statusBadgeColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${lead.serviceRequested} — ${lead.projectScope}",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = CanyonOrange, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${lead.city} (${lead.zipCode})", color = TextMuted, fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (lead.quoteAmount.isNotBlank()) {
                                        Text(
                                            text = "Quote: ${lead.quoteAmount}",
                                            color = StatusGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    } else if (lead.estBudget.isNotBlank()) {
                                        Text(
                                            text = "Budget: ${lead.estBudget}",
                                            color = StatusAmber,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            if (lead.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CanyonNavyDark,
                                    border = BorderStroke(0.5.dp, CanyonNavyBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = lead.notes,
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // 5. INSPECT & EDIT LEAD DIALOG
    // -------------------------------------------------------------
    inspectedLead?.let { lead ->
        AlertDialog(
            onDismissRequest = { inspectedLead = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(lead.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextWhite)
                        Text("${lead.sourceChannel} • ${lead.leadDate}", fontSize = 11.sp, color = TextMuted)
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = lead.statusBadgeColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, lead.statusBadgeColor)
                    ) {
                        Text(
                            text = lead.status,
                            color = lead.statusBadgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Service Requested: ${lead.serviceRequested}",
                        fontWeight = FontWeight.SemiBold,
                        color = SafetyGold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Location: ${lead.city} • Zip ${lead.zipCode}",
                        color = TextWhite,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Scope: ${lead.projectScope}",
                        color = TextWhite,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Est. Budget: ${if (lead.estBudget.isNotBlank()) lead.estBudget else "N/A"}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Quote: ${if (lead.quoteAmount.isNotBlank()) lead.quoteAmount else "Unquoted"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                    }
                    Text(
                        text = "Next Follow-Up: ${lead.nextFollowUp}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    HorizontalDivider(color = CanyonNavyBorder, thickness = 1.dp)

                    Text(
                        text = "Notes & AI Autonomous Log:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = TextWhite
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CanyonNavyDark,
                        border = BorderStroke(1.dp, CanyonNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = lead.notes,
                            fontSize = 11.sp,
                            color = TextWhite,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (lead.status != "Closed Won" && lead.status != "Won") {
                        Button(
                            onClick = {
                                leadsList = leadsList.map {
                                    if (it.id == lead.id) it.copy(status = "Closed Won") else it
                                }
                                inspectedLead = null
                                Toast.makeText(context, "Lead marked Closed Won!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark Won", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = {
                            onConvertToJob?.invoke(lead)
                            inspectedLead = null
                            Toast.makeText(context, "Lead dispatched to live job board!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        Text("Dispatch Job", fontSize = 12.sp)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectedLead = null }) {
                    Text("Close", color = TextMuted)
                }
            },
            containerColor = CanyonNavyCard
        )
    }

    // -------------------------------------------------------------
    // 6. ADD MANUAL INBOUND LEAD DIALOG
    // -------------------------------------------------------------
    if (showAddDialog) {
        var customerNameInput by remember { mutableStateOf("") }
        var serviceInput by remember { mutableStateOf("Kitchen Remodel") }
        var cityInput by remember { mutableStateOf("Tucson, AZ") }
        var zipInput by remember { mutableStateOf("85701") }
        var scopeInput by remember { mutableStateOf("") }
        var budgetInput by remember { mutableStateOf("") }
        var channelInput by remember { mutableStateOf(selectedChannel.takeIf { it != "All Sources" } ?: "Thumbtack") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Inbound Lead to Pipeline", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customerNameInput,
                        onValueChange = { customerNameInput = it },
                        label = { Text("Customer Name (e.g. J. Smith)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = serviceInput,
                        onValueChange = { serviceInput = it },
                        label = { Text("Service Requested") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = cityInput,
                            onValueChange = { cityInput = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = zipInput,
                            onValueChange = { zipInput = it },
                            label = { Text("Zip Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = scopeInput,
                        onValueChange = { scopeInput = it },
                        label = { Text("Project Scope Details") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        label = { Text("Estimated Budget (optional, e.g. $5,000)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customerNameInput.isNotBlank()) {
                            val newEntry = CanvasLeadEntry(
                                id = "MANUAL-${System.currentTimeMillis() % 10000}",
                                leadDate = "2026-09-12",
                                customerName = customerNameInput,
                                serviceRequested = serviceInput,
                                city = cityInput,
                                zipCode = zipInput,
                                projectScope = if (scopeInput.isNotBlank()) scopeInput else "Customer inquiry via $channelInput",
                                status = "New",
                                estBudget = budgetInput,
                                quoteAmount = "",
                                nextFollowUp = "2026-09-13",
                                notes = "Inbound lead recorded manually for $channelInput",
                                sourceChannel = channelInput
                            )
                            leadsList = listOf(newEntry) + leadsList
                            showAddDialog = false
                            Toast.makeText(context, "Inbound lead added to canvas queue!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ThumbtackBlue)
                ) {
                    Text("Add Lead")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CanyonNavyCard
        )
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLES: KPI CARD & SPREADSHEET CELLS
// -------------------------------------------------------------
@Composable
private fun CanvasKpiCard(
    modifier: Modifier = Modifier,
    headerLabel: String,
    emojiIcon: String,
    metricLabel: String,
    value: String,
    caption: String,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CanyonNavyElevated else CanyonNavyDark
        ),
        border = BorderStroke(1.dp, if (isSelected) accentColor else CanyonNavyBorder)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = headerLabel,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) accentColor else TextMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = emojiIcon, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = metricLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                }
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = caption,
                fontSize = 9.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TableHeaderCell(
    title: String,
    width: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp)
    ) {
        Text(
            text = title,
            color = SafetyGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    color: Color = TextWhite,
    fontSize: androidx.compose.ui.unit.TextUnit = 11.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    fontFamily: FontFamily? = null,
    maxLines: Int = 1
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}
