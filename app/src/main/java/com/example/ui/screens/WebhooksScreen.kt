package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.IncomingWebhookEvent
import com.example.data.models.IncomingWebhookStatus
import com.example.data.models.PartnerType
import com.example.data.models.WebhookConfig
import com.example.data.models.WebhookDeliveryLog
import com.example.ui.components.NotificationSettingsDialog
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SafetyGold
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.SlateMedium
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.ProTradeViewModel

@Composable
fun WebhooksScreen(
    viewModel: ProTradeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableIntStateOf(0) }

    val isNotificationSettingsOpen by viewModel.isNotificationSettingsOpen.collectAsState()

    if (isNotificationSettingsOpen) {
        NotificationSettingsDialog(
            viewModel = viewModel,
            onDismissRequest = { viewModel.closeNotificationSettings() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "APIs & Webhook Pipeline",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time Partner Feeds, Zapier & Custom Endpoints",
                        color = SafetyGold,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick = { viewModel.openNotificationSettings() },
                    modifier = Modifier
                        .background(SlateElevated, CircleShape)
                        .border(1.dp, SafetyGold.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = "Notification Settings",
                        tint = SafetyGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ScrollableTabRow(
                selectedTabIndex = activeSubTab,
                containerColor = SlateCard,
                contentColor = SafetyGold,
                edgePadding = 0.dp,
                divider = { HorizontalDivider(color = SlateBorder) }
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Incoming Partner Webhooks", fontSize = 12.sp, fontWeight = if (activeSubTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Webhook, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Outbound Webhook Tester", fontSize = 12.sp, fontWeight = if (activeSubTab == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = activeSubTab == 2,
                    onClick = { activeSubTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Custom AI API Endpoint", fontSize = 12.sp, fontWeight = if (activeSubTab == 2) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }
        }

        when (activeSubTab) {
            0 -> IncomingWebhooksView(viewModel = viewModel, context = context)
            1 -> OutboundWebhooksView(viewModel = viewModel)
            2 -> CustomAiEndpointView(viewModel = viewModel)
        }
    }
}

@Composable
fun IncomingWebhooksView(
    viewModel: ProTradeViewModel,
    context: Context
) {
    val incomingList by viewModel.incomingWebhooks.collectAsState()
    val searchQuery by viewModel.webhookSearchQuery.collectAsState()
    val selectedPartner by viewModel.selectedWebhookPartner.collectAsState()
    val selectedStatus by viewModel.selectedWebhookStatus.collectAsState()
    val onlyImportant by viewModel.webhookOnlyImportant.collectAsState()

    val selectedIds by viewModel.selectedWebhookIds.collectAsState()
    val isSelectionMode by viewModel.isWebhookSelectionMode.collectAsState()

    val filteredList = incomingList.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
            item.eventType.contains(searchQuery, ignoreCase = true) ||
            item.details.contains(searchQuery, ignoreCase = true) ||
            item.partner.displayName.contains(searchQuery, ignoreCase = true)
        val matchesPartner = selectedPartner == null || item.partner == selectedPartner
        val matchesStatus = selectedStatus == null || item.status == selectedStatus
        val matchesImportant = !onlyImportant || item.isImportant
        matchesSearch && matchesPartner && matchesStatus && matchesImportant
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateWebhookSearchQuery(it) },
                        placeholder = { Text("Search webhook events, details, or payload...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.updateWebhookSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SafetyGold,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = SlateElevated,
                            unfocusedContainerColor = SlateElevated
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterTagChip(
                                label = "All Partners",
                                isSelected = selectedPartner == null,
                                onClick = { viewModel.filterWebhooksByPartner(null) }
                            )
                        }
                        items(PartnerType.entries) { partner ->
                            FilterTagChip(
                                label = partner.brandTag,
                                isSelected = selectedPartner == partner,
                                color = partner.badgeColor,
                                onClick = {
                                    viewModel.filterWebhooksByPartner(if (selectedPartner == partner) null else partner)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterTagChip(
                                label = "All Statuses",
                                isSelected = selectedStatus == null,
                                onClick = { viewModel.filterWebhooksByStatus(null) }
                            )
                        }
                        items(IncomingWebhookStatus.entries) { status ->
                            FilterTagChip(
                                label = status.name,
                                isSelected = selectedStatus == status,
                                color = when (status) {
                                    IncomingWebhookStatus.PENDING -> SafetyGold
                                    IncomingWebhookStatus.PROCESSED -> MintGreen
                                    IncomingWebhookStatus.FAILED -> Color(0xFFEF4444)
                                    IncomingWebhookStatus.ARCHIVED -> TextMuted
                                },
                                onClick = {
                                    viewModel.filterWebhooksByStatus(if (selectedStatus == status) null else status)
                                }
                            )
                        }
                        item {
                            FilterTagChip(
                                label = "★ Starred",
                                isSelected = onlyImportant,
                                color = SafetyGold,
                                onClick = { viewModel.toggleWebhookOnlyImportant() }
                            )
                        }
                    }
                }
            }
        }

        if (isSelectionMode) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = SlateElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SafetyGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedIds.size} Selected",
                            color = SafetyGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(onClick = { viewModel.markSelectedWebhooksAsProcessed() }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Check, contentDescription = "Mark Processed", tint = MintGreen)
                            }
                            IconButton(onClick = { viewModel.toggleImportantSelectedWebhooks() }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Bookmark, contentDescription = "Toggle Starred", tint = SafetyGold)
                            }
                            IconButton(onClick = { viewModel.archiveSelectedWebhooks() }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Archive, contentDescription = "Archive Selected", tint = TextMuted)
                            }
                            IconButton(onClick = { viewModel.restoreSelectedWebhooks() }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Restore, contentDescription = "Restore Selected", tint = ElectricBlue)
                            }
                            IconButton(onClick = { viewModel.clearWebhookSelections() }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Selection", tint = TextMuted)
                            }
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
                    text = "INCOMING EVENTS (${filteredList.size})",
                    color = TextSubtle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Long-press to multi-select",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No matching incoming webhook events found.", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(filteredList) { event ->
                val isSelected = selectedIds.contains(event.id)
                IncomingWebhookCard(
                    event = event,
                    isSelected = isSelected,
                    onSelect = { viewModel.toggleWebhookSelection(event.id) },
                    onArchive = { viewModel.archiveWebhook(event.id) },
                    onRestore = { viewModel.restoreWebhook(event.id) },
                    onToggleImportant = { viewModel.toggleWebhookImportant(event.id) },
                    onCopyJson = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Webhook JSON", "{\n  \"id\": \"${event.id}\",\n  \"partner\": \"${event.partner.name}\",\n  \"event\": \"${event.eventType}\",\n  \"details\": \"${event.details}\",\n  \"status\": \"${event.status.name}\"\n}")
                        clipboard.setPrimaryClip(clip)
                        viewModel.showMessage("Webhook payload copied to clipboard")
                    }
                )
            }
        }
    }
}

@Composable
fun FilterTagChip(
    label: String,
    isSelected: Boolean,
    color: Color = SafetyGold,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) color else SlateElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else SlateBorder)
    ) {
        Text(
            text = label,
            color = if (isSelected) SlateDark else TextWhite,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun IncomingWebhookCard(
    event: IncomingWebhookEvent,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onArchive: () -> Unit,
    onRestore: () -> Unit,
    onToggleImportant: () -> Unit,
    onCopyJson: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SlateElevated else SlateCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) SafetyGold else if (event.isImportant) SafetyGold.copy(alpha = 0.5f) else SlateBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(event.partner.badgeColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.partner.displayName,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = when (event.status) {
                            IncomingWebhookStatus.PENDING -> SafetyGold.copy(alpha = 0.15f)
                            IncomingWebhookStatus.PROCESSED -> MintGreen.copy(alpha = 0.15f)
                            IncomingWebhookStatus.FAILED -> Color(0xFFEF4444).copy(alpha = 0.15f)
                            IncomingWebhookStatus.ARCHIVED -> TextMuted.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = event.status.name,
                            color = when (event.status) {
                                IncomingWebhookStatus.PENDING -> SafetyGold
                                IncomingWebhookStatus.PROCESSED -> MintGreen
                                IncomingWebhookStatus.FAILED -> Color(0xFFEF4444)
                                IncomingWebhookStatus.ARCHIVED -> TextMuted
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleImportant, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (event.isImportant) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Star",
                            tint = if (event.isImportant) SafetyGold else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onCopyJson, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy JSON", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                    if (event.status == IncomingWebhookStatus.ARCHIVED) {
                        IconButton(onClick = onRestore, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Unarchive, contentDescription = "Restore", tint = ElectricBlue, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        IconButton(onClick = onArchive, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Archive, contentDescription = "Archive", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.eventType,
                color = SafetyGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.details,
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${event.id}",
                    color = TextSubtle,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
                Text(
                    text = java.text.SimpleDateFormat("MMM dd, hh:mm a", java.util.Locale.US).format(java.util.Date(event.timestampMs)),
                    color = TextSubtle,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun OutboundWebhooksView(viewModel: ProTradeViewModel) {
    val webhooks by viewModel.webhooks.collectAsState()
    val logs by viewModel.webhookLogs.collectAsState()
    val testUrl by viewModel.testWebhookUrl.collectAsState()
    val testMethod by viewModel.testWebhookMethod.collectAsState()
    val testHeaders by viewModel.testWebhookHeaders.collectAsState()
    val testPayload by viewModel.testWebhookPayload.collectAsState()
    val isTesting by viewModel.isTestingWebhook.collectAsState()
    val lastResult by viewModel.lastTestResult.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE OUTBOUND WEBHOOK TESTER",
                            color = ElectricBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            color = ElectricBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = testMethod,
                                color = ElectricBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = testUrl,
                        onValueChange = { viewModel.testWebhookUrl.value = it },
                        label = { Text("Destination URL (Zapier / Make / AWS)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = SlateElevated,
                            unfocusedContainerColor = SlateElevated
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = testHeaders,
                        onValueChange = { viewModel.testWebhookHeaders.value = it },
                        label = { Text("Custom HTTP Headers (JSON)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = SlateElevated,
                            unfocusedContainerColor = SlateElevated
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = testPayload,
                        onValueChange = { viewModel.testWebhookPayload.value = it },
                        label = { Text("Dispatch Event Payload (JSON)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = SlateElevated,
                            unfocusedContainerColor = SlateElevated
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.runLiveWebhookTest() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isTesting,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(color = SlateDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Transmitting HTTP Request...", color = SlateDark)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SlateDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send Live Outbound Webhook Test", color = SlateDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    lastResult?.let { res ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (res.isSuccess) MintGreen.copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (res.isSuccess) MintGreen else Color(0xFFEF4444))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (res.isSuccess) "DELIVERY SUCCESS: HTTP ${res.statusCode}" else "RESPONSE CODE: HTTP ${res.statusCode}",
                                        color = if (res.isSuccess) MintGreen else Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text("${res.latencyMs}ms", color = TextMuted, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = res.responseBody.take(150),
                                    color = TextWhite,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
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
                    text = "REGISTERED WEBHOOK ENDPOINTS (${webhooks.size})",
                    color = TextSubtle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "+ Add Endpoint",
                    color = SafetyGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAddDialog = true }
                )
            }
        }

        items(webhooks) { hook ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(hook.name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = SafetyGold.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text(hook.triggerEvent, color = SafetyGold, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(hook.url, color = TextMuted, fontSize = 11.sp, maxLines = 1)
                    }
                    IconButton(onClick = { viewModel.deleteWebhook(hook.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
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
                    text = "OUTBOUND DELIVERY AUDIT LOG (${logs.size})",
                    color = TextSubtle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (logs.isNotEmpty()) {
                    Text(
                        text = "Clear Logs",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable { viewModel.clearAllWebhookLogs() }
                    )
                }
            }
        }

        items(logs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SlateElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, SlateBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (log.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (log.isSuccess) MintGreen else Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(log.webhookName, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("HTTP ${log.statusCode}", color = if (log.isSuccess) MintGreen else Color(0xFFEF4444), fontSize = 11.sp)
                        }
                        Text(log.url, color = TextMuted, fontSize = 10.sp, maxLines = 1)
                    }
                    Text("${log.latencyMs}ms", color = TextSubtle, fontSize = 10.sp)
                }
            }
        }
    }

    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newUrl by remember { mutableStateOf("") }
        var newEvent by remember { mutableStateOf("ESTIMATE_CREATED") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Register Webhook Endpoint", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Endpoint Name (e.g. Zapier Job Sync)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text("Target Webhook URL") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = newEvent,
                        onValueChange = { newEvent = it },
                        label = { Text("Trigger Event (e.g. LEAD_RECEIVED)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newUrl.isNotBlank()) {
                            viewModel.saveWebhook(newName, newUrl, newEvent, "POST", "", "{}")
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyGold)
                ) {
                    Text("Save Endpoint", color = SlateDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = TextMuted) }
            },
            containerColor = SlateCard
        )
    }
}

@Composable
fun CustomAiEndpointView(viewModel: ProTradeViewModel) {
    val config by viewModel.customApiConfig.collectAsState()
    var urlText by remember { mutableStateOf(config.endpointUrl) }
    var keyText by remember { mutableStateOf(config.apiKey) }
    var modelText by remember { mutableStateOf(config.modelIdentifier) }
    var headerText by remember { mutableStateOf(config.customAuthHeader) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAccent)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CUSTOM AI REST API ENGINE",
                            color = PurpleAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            color = PurpleAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "BYO-KEY READY",
                                color = PurpleAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Connect custom OpenAI-compatible endpoints, private Ollama servers, or enterprise LLMs for contract estimation:",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = urlText,
                        onValueChange = { urlText = it },
                        label = { Text("OpenAI / LLM Compatible Endpoint URL") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleAccent,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = SlateElevated,
                            unfocusedContainerColor = SlateElevated
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = keyText,
                        onValueChange = { keyText = it },
                        label = { Text("API Bearer Token / Secret Key") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleAccent,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = SlateElevated,
                            unfocusedContainerColor = SlateElevated
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = modelText,
                            onValueChange = { modelText = it },
                            label = { Text("Model Identifier") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleAccent,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = SlateElevated,
                                unfocusedContainerColor = SlateElevated
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = headerText,
                            onValueChange = { headerText = it },
                            label = { Text("Auth Prefix") },
                            modifier = Modifier.weight(0.7f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleAccent,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = SlateElevated,
                                unfocusedContainerColor = SlateElevated
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.customApiConfig.value = com.example.data.models.CustomApiConfig(
                                endpointUrl = urlText,
                                apiKey = keyText,
                                modelIdentifier = modelText,
                                customAuthHeader = headerText
                            )
                            viewModel.showMessage("Custom AI API configuration updated successfully!")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Custom AI Endpoint", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
