package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AiGeneratedEstimate
import com.example.data.ChatMessageEntity
import com.example.data.TradePersona
import com.example.data.GeminiScreenShareState
import com.example.data.PttWalkieTalkieState
import com.example.data.TieredQuoteResult
import com.example.data.TierEstimateOption
import com.example.data.DispatchIntakeLead
import com.example.data.WebSocketConnectionStatus
import com.example.ui.MainViewModel
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusAmber
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(viewModel: MainViewModel) {
    var activeChatMode by remember { mutableIntStateOf(0) } // 0 = AI Estimate Assistant, 1 = Field Team & Slack Comms

    val messages by viewModel.messages.collectAsState()
    val slackEnabled by viewModel.slackSyncEnabled.collectAsState()
    val activeTradePersona by viewModel.selectedTradePersona.collectAsState()
    val isGeneratingEstimate by viewModel.isGeneratingEstimate.collectAsState()
    val currentEstimate by viewModel.currentAiEstimate.collectAsState()
    val selectedAiModel by viewModel.selectedAiModel.collectAsState()

    var estimatePromptInput by remember { mutableStateOf("") }
    var teamMessageInput by remember { mutableStateOf("") }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var exportedHistoryDialog by remember { mutableStateOf<String?>(null) }
    var selectedRoleSender by remember { mutableStateOf("Tech") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("chat_screen")
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Top Navigation Header: 4 Specialized Contractor Marketplace Communication Modes
        ScrollableTabRow(
            selectedTabIndex = activeChatMode,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = OrangePrimary,
            edgePadding = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .testTag("chat_mode_tabs")
        ) {
            Tab(
                selected = activeChatMode == 0,
                onClick = { activeChatMode = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp), tint = OrangePrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Agent Gateway", fontWeight = if (activeChatMode == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp)
                    }
                }
            )
            Tab(
                selected = activeChatMode == 1,
                onClick = { activeChatMode = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ScreenShare, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Gemini Screen", fontWeight = if (activeChatMode == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp)
                    }
                }
            )
            Tab(
                selected = activeChatMode == 2,
                onClick = { activeChatMode = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Radio, contentDescription = null, modifier = Modifier.size(16.dp), tint = StatusGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Crew Walkie-Talkie (PTT)", fontWeight = if (activeChatMode == 2) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp)
                    }
                }
            )
            Tab(
                selected = activeChatMode == 3,
                onClick = { activeChatMode = 3 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("2-Way Comms & SMS", fontWeight = if (activeChatMode == 3) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (activeChatMode) {
            0 -> {
                // === MODE 0: MULTI-MODEL AI AGENT GATEWAY ===
                AiAgentGatewayView(
                    viewModel = viewModel,
                    isGenerating = isGeneratingEstimate,
                    currentEstimate = currentEstimate,
                    selectedPersona = activeTradePersona,
                    selectedModel = selectedAiModel,
                    promptInput = estimatePromptInput,
                    onPromptChange = { estimatePromptInput = it },
                    onGenerate = {
                        viewModel.generateAiEstimate(estimatePromptInput)
                        estimatePromptInput = ""
                    }
                )
            }
            1 -> {
                // === MODE 1: LIVE GEMINI SCREEN SHARE (PRIVACY FILTER GUARDRAIL) ===
                LiveGeminiScreenShareView(viewModel = viewModel)
            }
            2 -> {
                // === MODE 2: REAL-TIME PUSH-TO-TALK (PTT) WALKIE-TALKIE ===
                CrewPushToTalkWalkieTalkieView(viewModel = viewModel)
            }
            3 -> {
                // === MODE 3: FIELD TEAM & SLACK COMMS (WITH TWILIO SMS FALLBACK) ===
                TeamSlackChatView(
                    viewModel = viewModel,
                    messages = messages,
                    slackEnabled = slackEnabled,
                    messageInput = teamMessageInput,
                    onMessageChange = { teamMessageInput = it },
                    selectedRole = selectedRoleSender,
                    onSelectRole = { selectedRoleSender = it },
                    onSend = {
                        viewModel.sendMessage(teamMessageInput, role = selectedRoleSender)
                        teamMessageInput = ""
                    },
                    onExportHistory = {
                        exportedHistoryDialog = viewModel.exportChatHistory()
                    }
                )
            }
        }
    }

    // Modal: Message Audit History
    exportedHistoryDialog?.let { history ->
        Dialog(onDismissRequest = { exportedHistoryDialog = null }) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("export_history_modal")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Chat & Slack Message Audit History", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            item {
                                Text(history, fontSize = 11.sp, color = Color.White, lineHeight = 16.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { exportedHistoryDialog = null },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close Audit Log")
                    }
                }
            }
        }
    }
}

// AI ESTIMATE CHAT COMPONENT
@Composable
fun AiEstimateChatView(
    viewModel: MainViewModel,
    isGenerating: Boolean,
    currentEstimate: AiGeneratedEstimate?,
    selectedPersona: TradePersona,
    selectedModel: String,
    promptInput: String,
    onPromptChange: (String) -> Unit,
    onGenerate: () -> Unit
) {
    var expandedModelSelector by remember { mutableStateOf(false) }

    val quickPrompts = listOf(
        "Gourmet Kitchen: 42\" Shaker Cabinets, Quartz Countertops & Island Plumbing",
        "Master Bath Suite: Curbless Shower Pan, Wet Room Tile & Freestanding Tub",
        "200A Electrical Service Upgrade & EV Charger Circuit Drop",
        "Custom Hardwood Deck & Pergola Framing with Trex Composite"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_estimate_chat_view"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Trade Persona & Gemini Model Telemetry Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "c520x AI ESTIMATOR ASSISTANT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Gemini Model Dropdown Badge
                        Box {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AccentCyan.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { expandedModelSelector = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedModel,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentCyan
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expandedModelSelector,
                                onDismissRequest = { expandedModelSelector = false }
                            ) {
                                listOf("gemini-3.5-flash", "gemini-3.1-pro-preview", "gemini-3.1-flash-lite-preview").forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m, fontSize = 12.sp) },
                                        onClick = {
                                            viewModel.setAiModel(m)
                                            expandedModelSelector = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Instant Estimates & Proposals from Voice or Typed Prompts",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Includes trade-specific labor hours, materials, subcontracts, and local market pricing.",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Trade Personas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TradePersona.values().forEach { persona ->
                            val isSel = selectedPersona == persona
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.setTradePersona(persona) },
                                label = { Text(persona.label, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OrangePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Quick Prompt Suggestions
        item {
            Text(
                text = "SAMPLE ESTIMATE PROMPTS (TAP TO LOAD):",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickPrompts) { prompt ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.clickable {
                            onPromptChange(prompt)
                        }
                    ) {
                        Text(
                            text = prompt.take(32) + "...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Input Box for Voice / Typed Prompt
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = onPromptChange,
                        placeholder = { Text("Describe job scope, specs, or materials (e.g. Master bath remodel, tile shower, quartz vanity)...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("ai_estimate_prompt_input"),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    // Voice prompt simulation
                                    onPromptChange("Kitchen remodel with custom 42-inch cabinets, quartz waterfall island, undercabinet LED lighting, and Delta Champagne Bronze fixture plumbing")
                                    viewModel.showToastNotice("Voice dictation transcribed!")
                                },
                                modifier = Modifier.size(32.dp).testTag("voice_dictation_btn")
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice Dictation", tint = OrangePrimary, modifier = Modifier.size(18.dp))
                            }
                            Text("Voice Dictation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = onGenerate,
                            enabled = !isGenerating && promptInput.isNotBlank(),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            modifier = Modifier.height(36.dp).testTag("generate_ai_estimate_btn")
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Building Estimate...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create Estimate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Generated Estimate Result Display
        currentEstimate?.let { estimate ->
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary),
                    modifier = Modifier.fillMaxWidth().testTag("generated_estimate_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = estimate.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Client: ${estimate.clientName} • Trade: ${estimate.tradePersona}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = { viewModel.clearCurrentAiEstimate() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = estimate.projectSummary,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Total Price Banner
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Navy900,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("TOTAL ESTIMATED PRICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                                    Text(
                                        text = NumberFormat.getCurrencyInstance(Locale.US).format(estimate.totalCost),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StatusGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "EST. ${estimate.estimatedDurationWeeks} WEEKS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Line Items Breakdown
                        Text(
                            text = "ITEMIZED LABOR & MATERIALS (${estimate.lineItems.size}):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        estimate.lineItems.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.description, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text("${item.category} • ${item.quantity.toInt()} ${item.unit} @ $${item.unitPrice.toInt()}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        text = NumberFormat.getCurrencyInstance(Locale.US).format(item.totalPrice),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Phases / Framing
                        Text(
                            text = "SCHEDULED PHASES (${estimate.projectPhases.size}):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        estimate.projectPhases.forEachIndexed { idx, phase ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Phase ${idx + 1}: ${phase.name}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text("${phase.durationDays} days • ${phase.milestoneDeliverable.take(24)}", fontSize = 10.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // ACTION BUTTONS: Convert to Proposal, Turn into Schedule, Copy Text
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.convertEstimateToProposal(estimate) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                modifier = Modifier.weight(1f).height(42.dp).testTag("convert_estimate_to_proposal_btn")
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Convert to Proposal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.turnEstimateIntoProjectSchedule(estimate) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                modifier = Modifier.weight(1f).height(42.dp).testTag("turn_into_schedule_btn")
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Turn to Schedule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// TEAM & SLACK LIVE COMMS COMPONENT
@Composable
fun TeamSlackChatView(
    viewModel: MainViewModel,
    messages: List<ChatMessageEntity>,
    slackEnabled: Boolean,
    messageInput: String,
    onMessageChange: (String) -> Unit,
    selectedRole: String,
    onSelectRole: (String) -> Unit,
    onSend: () -> Unit,
    onExportHistory: () -> Unit
) {
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var editingChatMessage by remember { mutableStateOf<ChatMessageEntity?>(null) }
    var reviewingChatMessage by remember { mutableStateOf<ChatMessageEntity?>(null) }

    val wsStatus by viewModel.webSocketStatus.collectAsState()
    val wsUrl by viewModel.webSocketUrl.collectAsState()
    val wsSent by viewModel.webSocketTotalSent.collectAsState()
    val wsReceived by viewModel.webSocketTotalReceived.collectAsState()
    val wsLatency by viewModel.webSocketLatencyMs.collectAsState()

    var showWsConfigDialog by remember { mutableStateOf(false) }
    var customWsInput by remember { mutableStateOf(wsUrl) }

    Column(modifier = Modifier.fillMaxSize().testTag("team_slack_chat_view")) {
        // OkHttp WebSocket 2-Way Messaging Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (wsStatus is WebSocketConnectionStatus.Connected) AccentCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth().testTag("okhttp_websocket_status_card")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
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
                                .background(
                                    when (wsStatus) {
                                        is WebSocketConnectionStatus.Connected -> StatusGreen
                                        is WebSocketConnectionStatus.Connecting,
                                        is WebSocketConnectionStatus.Reconnecting -> StatusAmber
                                        else -> Color.Gray
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "OkHttp WebSocket 2-Way Relay",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (wsStatus) {
                                        is WebSocketConnectionStatus.Connected -> StatusGreen.copy(alpha = 0.15f)
                                        is WebSocketConnectionStatus.Connecting,
                                        is WebSocketConnectionStatus.Reconnecting -> StatusAmber.copy(alpha = 0.15f)
                                        else -> Color.Gray.copy(alpha = 0.15f)
                                    }
                                ) {
                                    Text(
                                        text = when (wsStatus) {
                                            is WebSocketConnectionStatus.Connected -> "LIVE ${wsLatency}ms"
                                            is WebSocketConnectionStatus.Connecting -> "CONNECTING"
                                            is WebSocketConnectionStatus.Reconnecting -> "RECONNECTING"
                                            is WebSocketConnectionStatus.Error -> "RETRYING"
                                            else -> "OFFLINE"
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (wsStatus) {
                                            is WebSocketConnectionStatus.Connected -> StatusGreen
                                            is WebSocketConnectionStatus.Connecting,
                                            is WebSocketConnectionStatus.Reconnecting -> StatusAmber
                                            else -> Color.Gray
                                        },
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Contractor ⇄ Customer Duplex JSON • Tx: $wsSent | Rx: $wsReceived",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                customWsInput = wsUrl
                                showWsConfigDialog = true
                            },
                            modifier = Modifier.size(28.dp).testTag("ws_settings_button")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "WebSocket Settings", modifier = Modifier.size(16.dp))
                        }

                        if (wsStatus is WebSocketConnectionStatus.Connected) {
                            OutlinedButton(
                                onClick = { viewModel.disconnectWebSocket() },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp).testTag("ws_disconnect_button")
                            ) {
                                Text("Disconnect", fontSize = 10.sp, color = StatusRed)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.connectWebSocket(wsUrl) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp).testTag("ws_connect_button")
                            ) {
                                Text("Connect", fontSize = 10.sp, color = Navy900, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2-Way Quick Action bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wsUrl.take(35) + if (wsUrl.length > 35) "..." else "",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    OutlinedButton(
                        onClick = {
                            viewModel.simulateCustomerWebSocketReply()
                        },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(24.dp).testTag("simulate_customer_ws_button")
                    ) {
                        Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(12.dp), tint = StatusGreen)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simulate Customer Inbound Msg", fontSize = 10.sp, color = StatusGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Slack Banner & Export Audit Button
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (slackEnabled) StatusGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tag, contentDescription = null, tint = if (slackEnabled) StatusGreen else Color.Gray, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (slackEnabled) "Slack: #field-ops Synced" else "Slack Integration Off",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onExportHistory,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.height(26.dp).testTag("export_chat_audit_button")
                ) {
                    Text("Export Audit Log", fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Role sender selection chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Send as:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            listOf("Tech" to "Alex Ramirez (Tech)", "Dispatch" to "Dispatch Desk", "Client" to "Helena (Client)").forEach { (roleKey, label) ->
                val isSel = selectedRole == roleKey
                FilterChip(
                    selected = isSel,
                    onClick = { onSelectRole(roleKey) },
                    label = { Text(label, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("chat_messages_list"),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderRole == selectedRole
                val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Text(
                            text = msg.senderName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (msg.senderRole) {
                                "Tech" -> OrangePrimary
                                "Dispatch" -> AccentCyan
                                else -> StatusGreen
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = timeFormatted, fontSize = 9.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isMe) 12.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 12.dp
                        ),
                        color = if (isMe) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = msg.message,
                                fontSize = 12.sp,
                                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (msg.attachmentName.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isMe) Navy900 else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (msg.attachmentType) {
                                                "photo" -> Icons.Default.PhotoCamera
                                                "voice" -> Icons.Default.GraphicEq
                                                "receipt" -> Icons.Default.Receipt
                                                "proposal" -> Icons.Default.Description
                                                else -> Icons.Default.AttachFile
                                            },
                                            contentDescription = null,
                                            tint = if (isMe) OrangePrimary else AccentCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = msg.attachmentName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Message action tools: Review, Edit, Copy, Delete
                    Row(
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        IconButton(
                            onClick = { reviewingChatMessage = msg },
                            modifier = Modifier.size(24.dp).testTag("review_chat_msg_${msg.id}")
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "Review Message Details",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        IconButton(
                            onClick = { editingChatMessage = msg },
                            modifier = Modifier.size(24.dp).testTag("edit_chat_msg_${msg.id}")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Message",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.showToastNotice("Message text copied")
                            },
                            modifier = Modifier.size(24.dp).testTag("copy_chat_msg_${msg.id}")
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy Message Text",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.deleteChatMessage(msg.id) },
                            modifier = Modifier.size(24.dp).testTag("delete_chat_msg_${msg.id}")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Message",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(
                    onClick = { showAttachmentMenu = true },
                    modifier = Modifier.size(36.dp).testTag("chat_attachment_button")
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = OrangePrimary)
                }

                DropdownMenu(
                    expanded = showAttachmentMenu,
                    onDismissRequest = { showAttachmentMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Snap Jobsite Photo", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        onClick = {
                            viewModel.sendMessage("Attached jobsite progress photo", role = selectedRole, attachmentType = "photo", attachmentName = "Jobsite_Progress_Cam.jpg")
                            showAttachmentMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Record Voice Memo (15s)", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        onClick = {
                            viewModel.sendMessage("Voice memo regarding load-bearing post", role = selectedRole, attachmentType = "voice", attachmentName = "Voice_Memo_01.m4a")
                            showAttachmentMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Attach Supply Receipt", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        onClick = {
                            viewModel.sendMessage("Material expense receipt for QBO sync", role = selectedRole, attachmentType = "receipt", attachmentName = "HD_Pro_Receipt_7749.pdf")
                            showAttachmentMenu = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = messageInput,
                onValueChange = onMessageChange,
                placeholder = { Text("Type message to field tech or Slack...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .testTag("chat_message_input"),
                singleLine = true
            )

            IconButton(
                onClick = onSend,
                enabled = messageInput.isNotBlank(),
                modifier = Modifier.size(36.dp).testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (messageInput.isNotBlank()) OrangePrimary else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }

    // Modal: Review Chat Message Dialog
    reviewingChatMessage?.let { msg ->
        ReviewChatMessageDialog(
            msg = msg,
            onDismiss = { reviewingChatMessage = null },
            onEdit = {
                reviewingChatMessage = null
                editingChatMessage = msg
            },
            onDelete = {
                viewModel.deleteChatMessage(msg.id)
                reviewingChatMessage = null
            },
            onCopy = {
                viewModel.showToastNotice("Message copied to clipboard")
                reviewingChatMessage = null
            }
        )
    }

    // Modal: Edit Chat Message Dialog
    editingChatMessage?.let { msg ->
        EditChatMessageDialog(
            initialMessage = msg,
            onDismiss = { editingChatMessage = null },
            onSave = { newText ->
                viewModel.editChatMessage(msg.id, newText)
                editingChatMessage = null
            }
        )
    }

    // Modal: OkHttp WebSocket Server Setup Dialog
    if (showWsConfigDialog) {
        Dialog(onDismissRequest = { showWsConfigDialog = false }) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(12.dp).testTag("ws_config_dialog")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("OkHttp WebSocket Relay Setup", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        IconButton(onClick = { showWsConfigDialog = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Configure endpoint URL for full-duplex real-time 2-way messaging between contractors and customers.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customWsInput,
                        onValueChange = { customWsInput = it },
                        label = { Text("WebSocket URL (wss://)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ws_url_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                customWsInput = "wss://echo.websocket.events"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Echo Server", fontSize = 10.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                customWsInput = "wss://ws.c520express.com/v1/contractor-customer-relay"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("C520X Cloud", fontSize = 10.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.connectWebSocket(customWsInput)
                            showWsConfigDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.fillMaxWidth().testTag("ws_save_connect_button")
                    ) {
                        Text("Save & Connect Relay")
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewChatMessageDialog(
    msg: ChatMessageEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    val timeFormatted = java.text.SimpleDateFormat("MMM d, yyyy • h:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date(msg.timestamp))

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(12.dp).testTag("review_chat_message_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (msg.senderRole) {
                                "Tech" -> OrangePrimary.copy(alpha = 0.15f)
                                "Dispatch" -> AccentCyan.copy(alpha = 0.15f)
                                else -> StatusGreen.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = "${msg.senderRole.uppercase()} LOG",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (msg.senderRole) {
                                    "Tech" -> OrangePrimary
                                    "Dispatch" -> AccentCyan
                                    else -> StatusGreen
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = msg.senderName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = timeFormatted,
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Message Body
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg.message,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                if (msg.attachmentName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OrangePrimary.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (msg.attachmentType) {
                                    "photo" -> Icons.Default.PhotoCamera
                                    "voice" -> Icons.Default.GraphicEq
                                    "receipt" -> Icons.Default.Receipt
                                    "proposal" -> Icons.Default.Description
                                    else -> Icons.Default.AttachFile
                                },
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = msg.attachmentName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Type: ${msg.attachmentType.replaceFirstChar { it.uppercase() }}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action tools: Edit, Copy, Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("review_chat_edit_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onCopy,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("review_chat_copy_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onDelete,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("review_chat_delete_btn")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 11.sp)
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text("Close", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EditChatMessageDialog(
    initialMessage: ChatMessageEntity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var textInput by remember { mutableStateOf(initialMessage.message) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(12.dp).testTag("edit_chat_message_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Edit Chat Message",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sent as: ${initialMessage.senderName} (${initialMessage.senderRole})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Message Text") },
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_chat_message_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                onSave(textInput.trim())
                            }
                        },
                        enabled = textInput.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.testTag("save_chat_message_btn")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// 1. MULTI-MODEL AI AGENT GATEWAY VIEW (Gemini 2.0 + OpenAI GPT-4o)
// =========================================================================
@Composable
fun AiAgentGatewayView(
    viewModel: MainViewModel,
    isGenerating: Boolean,
    currentEstimate: AiGeneratedEstimate?,
    selectedPersona: TradePersona,
    selectedModel: String,
    promptInput: String,
    onPromptChange: (String) -> Unit,
    onGenerate: () -> Unit
) {
    var activeSubAgent by remember { mutableIntStateOf(0) } // 0 = Instant Estimator, 1 = Auto Dispatch Intake, 2 = Subcontractor Voice
    val tieredQuote by viewModel.tieredQuote.collectAsState()
    val dispatchLeads by viewModel.dispatchIntakeLeads.collectAsState()
    val voiceNotes by viewModel.subcontractorVoiceNotes.collectAsState()

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_agent_gateway_view")
    ) {
        // Sub-Agent Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = activeSubAgent == 0,
                onClick = { activeSubAgent = 0 },
                label = { Text("1. Instant Estimator (3-Tier)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = OrangePrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = activeSubAgent == 1,
                onClick = { activeSubAgent = 1 },
                label = { Text("2. Auto-Dispatch & Intake", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusBlue,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = activeSubAgent == 2,
                onClick = { activeSubAgent = 2 },
                label = { Text("3. Hands-Free Voice Tech", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusGreen,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (activeSubAgent) {
            0 -> {
                // Instant Estimator (Good / Better / Best)
                var projectTitleInput by remember { mutableStateOf(tieredQuote.projectTitle) }
                var sqftInput by remember { mutableStateOf(tieredQuote.squareFootage.toFloat()) }
                var laborRateInput by remember { mutableStateOf(95f) }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(OrangePrimary.copy(alpha = 0.5f))
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("AI 3-Tier Proposal Engine", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = OrangePrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text("Gemini 2.0 Flash + GPT-4o", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OrangePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = projectTitleInput,
                                    onValueChange = { projectTitleInput = it },
                                    label = { Text("Project Title / Trade Scope") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Square Footage / Service Area:", fontSize = 11.sp)
                                    Text("${sqftInput.toInt()} sq. ft.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                                }
                                Slider(
                                    value = sqftInput,
                                    onValueChange = { sqftInput = it },
                                    valueRange = 500f..8000f,
                                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Labor Rate ($/hour):", fontSize = 11.sp)
                                    Text("${currencyFormat.format(laborRateInput)}/hr", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusBlue)
                                }
                                Slider(
                                    value = laborRateInput,
                                    onValueChange = { laborRateInput = it },
                                    valueRange = 65f..160f,
                                    colors = SliderDefaults.colors(thumbColor = StatusBlue, activeTrackColor = StatusBlue),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Button(
                                    onClick = {
                                        viewModel.generateGoodBetterBestEstimate(
                                            projectTitle = projectTitleInput,
                                            squareFootage = sqftInput.toInt(),
                                            laborRate = laborRateInput.toDouble()
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("generate_tiered_quote_btn")
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Calculate Good / Better / Best Tiers", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // 3 TIERS DISPLAY
                    item {
                        Text("Generated 3-Tier Customer Options (Good / Better / Best):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    item {
                        TierOptionCard(
                            tier = tieredQuote.goodTier,
                            badgeColor = Color(0xFF78909C),
                            isRecommended = false,
                            currencyFormat = currencyFormat
                        )
                    }

                    item {
                        TierOptionCard(
                            tier = tieredQuote.betterTier,
                            badgeColor = OrangePrimary,
                            isRecommended = true,
                            currencyFormat = currencyFormat
                        )
                    }

                    item {
                        TierOptionCard(
                            tier = tieredQuote.bestTier,
                            badgeColor = StatusGreen,
                            isRecommended = false,
                            currencyFormat = currencyFormat
                        )
                    }

                    // 80/20 SPLIT SUMMARY
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StatusGreen.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("80% Contractor Payout (Recommended Tier):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(currencyFormat.format(tieredQuote.contractorCut80), fontSize = 16.sp, fontWeight = FontWeight.Black, color = StatusGreen)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("20% Platform Fee (Escrow):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(currencyFormat.format(tieredQuote.platformFee20), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Automated Dispatch & Intake Agent
                var rawLeadInput by remember { mutableStateOf("") }
                var clientNameInput by remember { mutableStateOf("") }
                var addressInput by remember { mutableStateOf("") }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(StatusBlue.copy(alpha = 0.5f))
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Sms, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Autonomous Lead Intake & Dispatch Agent", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Surface(shape = RoundedCornerShape(4.dp), color = StatusBlue.copy(alpha = 0.15f)) {
                                        Text("Twilio Proxy + Calendar", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusBlue, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Simulate an incoming lead email/form. Agent parses scope, checks tech schedule, and auto-dispatches appointment with SMS:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = clientNameInput,
                                    onValueChange = { clientNameInput = it },
                                    label = { Text("Customer Name / Business") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = addressInput,
                                    onValueChange = { addressInput = it },
                                    label = { Text("Property Address") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = rawLeadInput,
                                    onValueChange = { rawLeadInput = it },
                                    label = { Text("Customer Raw Inquiry Text") },
                                    placeholder = { Text("e.g. Main electrical breaker tripped, smoke odor from sub-panel. Need emergency tech today.") },
                                    minLines = 2,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        viewModel.runAutomatedDispatchIntake(
                                            rawInquiry = rawLeadInput,
                                            customerName = clientNameInput,
                                            address = addressInput
                                        )
                                        rawLeadInput = ""
                                        clientNameInput = ""
                                        addressInput = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("run_intake_agent_btn")
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ingest & Auto-Dispatch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Text("Active Auto-Dispatched Leads (${dispatchLeads.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    items(dispatchLeads) { lead ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(lead.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(shape = RoundedCornerShape(4.dp), color = StatusGreen.copy(alpha = 0.15f)) {
                                        Text(lead.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(lead.extractedAddress, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Scope: ${lead.extractedScope}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Assigned Slot: ${lead.calendarSlotAssigned}", fontSize = 10.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("AUTOMATED SMS SENT VIA TWILIO:", fontSize = 9.sp, fontWeight = FontWeight.Black, color = StatusBlue)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(lead.automatedSmsResponseSent, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Hands-Free Subcontractor Voice Assistant
                var quickNoteInput by remember { mutableStateOf("") }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(StatusGreen.copy(alpha = 0.5f))
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Engineering, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Subcontractor Hands-Free Voice Assistant", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Surface(shape = RoundedCornerShape(4.dp), color = StatusGreen.copy(alpha = 0.15f)) {
                                        Text("On-Site Voice Engine", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Speak or dictate field notes on job sites to automatically log material purchases, record change orders, or document code compliance without touching a keyboard.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = quickNoteInput,
                                    onValueChange = { quickNoteInput = it },
                                    label = { Text("Dictate or Type Field Voice Note") },
                                    placeholder = { Text("e.g. Purchased 200ft 10/2 Romex at Lowe's for $142.60. Added change order for 2 dedicated freezer outlets.") },
                                    minLines = 2,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (quickNoteInput.isNotBlank()) {
                                            viewModel.addSubcontractorVoiceNote(quickNoteInput.trim())
                                            quickNoteInput = ""
                                        }
                                    },
                                    enabled = quickNoteInput.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("save_voice_note_btn")
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Hands-Free Voice Entry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Text("Logged Voice Notes & Change Orders (${voiceNotes.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    items(voiceNotes) { note ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(note, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TierOptionCard(
    tier: TierEstimateOption,
    badgeColor: Color,
    isRecommended: Boolean,
    currencyFormat: java.text.NumberFormat
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isRecommended) 2.dp else 1.dp,
            color = if (isRecommended) badgeColor else MaterialTheme.colorScheme.outlineVariant
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
                    Surface(shape = RoundedCornerShape(4.dp), color = badgeColor.copy(alpha = 0.15f)) {
                        Text(tier.tierName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    if (isRecommended) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = OrangePrimary) {
                            Text("POPULAR", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }

                Text(
                    text = currencyFormat.format(tier.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = badgeColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Labor: ${tier.laborHours} hrs", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Materials: ${currencyFormat.format(tier.materialsCost)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Warranty: ${tier.warrantyYears} Year(s)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            tier.features.forEach { feature ->
                Row(modifier = Modifier.padding(vertical = 1.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = badgeColor, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(feature, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

// =========================================================================
// 2. LIVE GEMINI SCREEN SHARE (WITH CLIENT-SIDE PRIVACY FILTER GUARDRAIL)
// =========================================================================
@Composable
fun LiveGeminiScreenShareView(viewModel: MainViewModel) {
    val screenState by viewModel.geminiScreenShareState.collectAsState()

    val subjects = listOf(
        "Commercial Blueprint E-102 (Highland Plaza)",
        "Roof Hail Damage & Flashing Inspection",
        "QuickBooks Supplier Invoice #9182 (Ferguson)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("live_gemini_screen_share_view"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top WebRTC SFU Status Banner
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (screenState.isActive) AccentCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (screenState.isActive) AccentCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (screenState.isActive) StatusGreen else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (screenState.isActive) "LiveKit WebRTC SFU: Connected" else "LiveKit WebRTC SFU: Standby",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${screenState.resolution} • Latency: ${screenState.sfuLatencyMs}ms",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.toggleGeminiScreenShare() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (screenState.isActive) StatusRed else AccentCyan
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("toggle_screen_share_btn")
                    ) {
                        Icon(
                            imageVector = if (screenState.isActive) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (screenState.isActive) "Stop Share" else "Start Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Privacy Guardrail Filter Toggle & Explanation
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (screenState.privacyFilterActive) StatusGreen.copy(alpha = 0.08f) else StatusAmber.copy(alpha = 0.08f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (screenState.privacyFilterActive) StatusGreen.copy(alpha = 0.4f) else StatusAmber.copy(alpha = 0.4f)
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
                            Icon(
                                imageVector = if (screenState.privacyFilterActive) Icons.Default.Shield else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (screenState.privacyFilterActive) StatusGreen else StatusAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Client-Side Privacy Filter (On-Device OCR)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (screenState.privacyFilterActive)
                                        "${screenState.detectedSensitiveCount} Sensitive Bounding Boxes Redacted (PCI/PII)"
                                    else
                                        "Filter Disabled — Raw Screen Video Streaming",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = screenState.privacyFilterActive,
                            onCheckedChange = { viewModel.togglePrivacyFilter() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = StatusGreen,
                                checkedTrackColor = StatusGreen.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("privacy_filter_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ML Kit detects regex patterns for Credit Cards, SSNs, Password fields, and Bank accounts before frames leave the device. Detected areas are blurred in real-time at 1.5 FPS.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 13.sp
                    )
                }
            }
        }

        // Subject Selector Chips
        item {
            Text("Simulated Screen Subject:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subjects.forEach { subject ->
                    val isSelected = screenState.subjectTitle == subject
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) AccentCyan else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.analyzeScreenWithGemini(subject) }
                            .testTag("subject_${subject.take(6)}")
                    ) {
                        Text(
                            text = when (subject) {
                                subjects[0] -> "Blueprint E-102"
                                subjects[1] -> "Roof Damage"
                                else -> "QBO Invoice #9182"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Screen Capture Frame Simulation Window
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhoneIphone, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(screenState.subjectTitle, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Text("1.5 FPS Live Stream", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Visual frame simulation box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            when (screenState.subjectTitle) {
                                subjects[0] -> {
                                    Text("HIGH-VOLTAGE POWER DISTRIBUTION SHEET E-102", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Service Entrance: 400A 3-Phase 120/208V | Feeders: 3/0 AWG THHN", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(4.dp)) {
                                            Text("Sub-Panel B: 240V 50A\nConduit: 3/4\" EMT\nFill: 38%", fontSize = 9.sp, modifier = Modifier.padding(6.dp))
                                        }
                                        if (screenState.privacyFilterActive) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color.Black.copy(alpha = 0.85f), modifier = Modifier.padding(4.dp)) {
                                                Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Lock, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("[REDACTED: ARCHITECT SSN & CLIENT PIN]", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                                subjects[1] -> {
                                    Text("ROOF INSPECTION: 4810 E CAMP LOWELL DR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("South Slope: 18 Impact Punctures • Chimney Step Flashing Detached", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    if (screenState.privacyFilterActive) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color.Black.copy(alpha = 0.85f)) {
                                            Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Lock, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("[REDACTED: HOMEOWNER POLICY #TRV-89412]", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    Text("FERGUSON ENTERPRISES INVOICE #9182", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("14 Items Billed: 2\" Copper fittings, Ball valves, PEX-A pipe ($3,421.80)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    if (screenState.privacyFilterActive) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color.Black.copy(alpha = 0.85f)) {
                                            Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Lock, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("[REDACTED: VISA CARD ••••4829 & ROUTING NUMBER]", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Multimodal Gemini Feedback
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini 2.0 Live Multimodal Feedback", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        if (screenState.isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = AccentCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = screenState.aiLiveFeedback,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.analyzeScreenWithGemini(screenState.subjectTitle) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("refresh_gemini_analysis_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Re-Analyze Screen", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. CREW PUSH-TO-TALK (PTT) WALKIE-TALKIE VIEW
// =========================================================================
@Composable
fun CrewPushToTalkWalkieTalkieView(viewModel: MainViewModel) {
    val pttState by viewModel.pttWalkieTalkieState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("crew_push_to_talk_view"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // PTT Channel Selector & Codec Header
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Radio, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crew Radio Channels", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = StatusGreen.copy(alpha = 0.15f)) {
                            Text("<120ms Opus SFU", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Channel Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        pttState.availableChannels.take(2).forEach { channel ->
                            val isSelected = pttState.activeChannel == channel
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) StatusGreen else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.switchPttChannel(channel) }
                            ) {
                                Text(
                                    text = channel.replace("Channel ", "Ch "),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        pttState.availableChannels.drop(2).forEach { channel ->
                            val isSelected = pttState.activeChannel == channel
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) StatusGreen else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.switchPttChannel(channel) }
                            ) {
                                Text(
                                    text = channel.replace("Channel ", "Ch "),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // BIG PUSH-TO-TALK TRANSMIT BUTTON
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (pttState.isTransmitting) 3.dp else 1.dp,
                    color = if (pttState.isTransmitting) StatusGreen else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.fillMaxWidth().testTag("ptt_main_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (pttState.isTransmitting) "TRANSMITTING TO ${pttState.activeChannel.uppercase()}..." else "HOLD BUTTON TO TALK TO CREW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (pttState.isTransmitting) StatusGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Push-to-Talk Circle Button
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                if (pttState.isTransmitting) StatusGreen else OrangePrimary
                            )
                            .clickable {
                                if (pttState.isTransmitting) {
                                    viewModel.stopPttTransmission()
                                } else {
                                    viewModel.startPttTransmission()
                                }
                            }
                            .testTag("ptt_transmit_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (pttState.isTransmitting) Icons.Default.VolumeUp else Icons.Default.Mic,
                                contentDescription = "Push To Talk",
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = if (pttState.isTransmitting) "RELEASE" else "PUSH",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Waveform Simulation Bar
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(16) { index ->
                            val height = if (pttState.isTransmitting) {
                                (10 + (index * 7) % 24).dp
                            } else {
                                4.dp
                            }
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(height)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (pttState.isTransmitting) StatusGreen else Color.Gray.copy(alpha = 0.3f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Canned Transmit Actions for Field Techs
                    Text("1-Tap Quick Field Dispatches:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.startPttTransmission()
                                viewModel.stopPttTransmission("Main service panel breakers torqued to code specs. Ready for utility meter drop.")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Panel Torqued", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.startPttTransmission()
                                viewModel.stopPttTransmission("Pressure test holding 350 PSI on line set. Zero leak down observed.")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Pressure Test OK", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.startPttTransmission()
                                viewModel.stopPttTransmission("Logged material receipt: 250ft Romex + junction boxes from supplier.")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Log Receipt", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // RECENT TRANSMISSIONS FEED
        item {
            Text("Recent Radio Transmissions & Transcripts:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        items(pttState.transmissions) { transmission ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(transmission.speaker, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Text("${transmission.timeAgo} • ${transmission.durationSec}s", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(transmission.channel, fontSize = 9.sp, color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("\"${transmission.transcript}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }
    }
}

