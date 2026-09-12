package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.PartnerType
import com.example.data.models.PlatformNotificationSetting
import com.example.ui.theme.CanyonCobalt
import com.example.ui.theme.CanyonNavyBorder
import com.example.ui.theme.CanyonNavyCard
import com.example.ui.theme.CanyonNavyDark
import com.example.ui.theme.CanyonNavyElevated
import com.example.ui.theme.CanyonOrange
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SafetyGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.ProTradeViewModel

@Composable
fun NotificationSettingsDialog(
    viewModel: ProTradeViewModel,
    onDismissRequest: () -> Unit
) {
    val preferences by viewModel.notificationPreferences.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 740.dp)
                .testTag("notification_settings_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CanyonNavyDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CanyonNavyBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(CanyonOrange.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = CanyonOrange,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PUSH ALERT SETTINGS",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "High-priority platform dispatch rules",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CanyonNavyCard,
                    contentColor = CanyonOrange,
                    edgePadding = 4.dp,
                    divider = { HorizontalDivider(color = CanyonNavyBorder) }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Platforms", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quiet Hours", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alert Tones", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Filter & Log", fontSize = 12.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> PlatformsTabContent(preferences = preferences, viewModel = viewModel)
                        1 -> QuietHoursTabContent(preferences = preferences, viewModel = viewModel)
                        2 -> AlertTonesTabContent(preferences = preferences, viewModel = viewModel)
                        3 -> FiltersAndLogTabContent(preferences = preferences, viewModel = viewModel)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_notification_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CanyonOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Save & Apply Settings",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PlatformsTabContent(
    preferences: com.example.data.models.NotificationPreferences,
    viewModel: ProTradeViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (preferences.masterPushEnabled) CanyonNavyElevated else CanyonNavyCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (preferences.masterPushEnabled) CanyonCobalt.copy(alpha = 0.5f) else CanyonNavyBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            if (preferences.masterPushEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (preferences.masterPushEnabled) MintGreen else TextSubtle,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Master Dispatch Alerts",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (preferences.masterPushEnabled) "Push alerts active on all devices" else "All push notifications paused",
                                color = if (preferences.masterPushEnabled) MintGreen else TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = preferences.masterPushEnabled,
                        onCheckedChange = { viewModel.toggleMasterPush(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = CanyonOrange,
                            uncheckedThumbColor = TextSubtle,
                            uncheckedTrackColor = CanyonNavyCard
                        )
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SafetyGold.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                    .border(1.dp, SafetyGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = SafetyGold,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Immediate lead alerts bypass background throttling to deliver lock-screen notifications within seconds. Highly recommended for competitive sources like Yelp & Thumbtack.",
                        color = SafetyGold,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        items(PartnerType.entries.toTypedArray()) { partner ->
            val setting = preferences.platformSettings[partner] ?: PlatformNotificationSetting(partner)
            PlatformNotificationCard(
                partner = partner,
                setting = setting,
                masterEnabled = preferences.masterPushEnabled,
                onToggleEnabled = { viewModel.togglePlatformPush(partner, it) },
                onToggleImmediate = { viewModel.toggleImmediatePush(partner, it) },
                onSendTest = { viewModel.sendTestNotification(partner) }
            )
        }
    }
}

@Composable
private fun QuietHoursTabContent(
    preferences: com.example.data.models.NotificationPreferences,
    viewModel: ProTradeViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "QUIET HOURS & OFF-SHIFT SCHEDULE",
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Automatically silence standard notifications during off-duty hours to protect technician rest periods.",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CanyonNavyElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, CanyonNavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (preferences.quietHoursEnabled) CanyonOrange else TextSubtle,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nightly Quiet Hours",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (preferences.quietHoursEnabled) "Active: ${preferences.quietHoursStart} - ${preferences.quietHoursEnd}" else "Disabled (Always alert)",
                                color = if (preferences.quietHoursEnabled) CanyonOrange else TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = preferences.quietHoursEnabled,
                        onCheckedChange = { viewModel.toggleQuietHours(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = CanyonOrange,
                            uncheckedThumbColor = TextSubtle,
                            uncheckedTrackColor = CanyonNavyCard
                        )
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CanyonNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, CanyonNavyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Schedule Window",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val presets = listOf(
                        Triple("10:00 PM - 7:00 AM", "22:00", "07:00"),
                        Triple("11:00 PM - 6:00 AM", "23:00", "06:00"),
                        Triple("9:00 PM - 8:00 AM", "21:00", "08:00")
                    )
                    presets.forEach { (label, start, end) ->
                        val isSelected = preferences.quietHoursStart == start && preferences.quietHoursEnd == end
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    if (isSelected) CanyonNavyElevated else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateQuietHours(start, end) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = CanyonOrange, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CanyonNavyElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, SafetyGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.weight(1f)) {
                        Icon(
                            Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = SafetyGold,
                            modifier = Modifier.size(22.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Emergency Lead Bypass",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = SafetyGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "RECOMMENDED",
                                        color = SafetyGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "High-priority leads (e.g., Yelp emergency leaks or commercial outages) will punch through quiet hours and ring aloud to capture fast-turnaround jobs.",
                                color = TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                    Switch(
                        checked = preferences.emergencyBypassQuietHours,
                        onCheckedChange = { viewModel.toggleEmergencyBypass(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = SafetyGold,
                            uncheckedThumbColor = TextSubtle,
                            uncheckedTrackColor = CanyonNavyCard
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertTonesTabContent(
    preferences: com.example.data.models.NotificationPreferences,
    viewModel: ProTradeViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ALERT TONES & HAPTICS",
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Choose loud, distinctive audio chimes so dispatchers and technicians instantly identify high-urgency contractor leads.",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        val tones = listOf(
            Pair("Canyon Siren (High Urgency)", "Loud dual-tone alarm tailored for emergency water line & HVAC leaks"),
            Pair("Radar Pulse (High Pitch)", "Rapid sonar pings to alert for fast-response competitive Yelp leads"),
            Pair("Pro Dispatch Chime", "Crisp high-fidelity multi-tone chime for standard job bookings"),
            Pair("Rapid Ping", "Subtle minimal acoustic chime for continuous shop alerts")
        )

        items(tones) { (toneName, description) ->
            val isSelected = preferences.alertTone == toneName
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.updateAlertTone(toneName) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CanyonNavyElevated else CanyonNavyCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) CanyonOrange else CanyonNavyBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = toneName,
                                color = if (isSelected) CanyonOrange else TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = CanyonOrange.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        color = CanyonOrange,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = description,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.showMessage("Previewing audio chime: $toneName")
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Preview Tone", tint = CanyonCobalt)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CanyonNavyElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, CanyonNavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Haptic Vibration Pulse",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Intense 3-stage pulse vibration on high-urgency lead arrivals",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = preferences.vibrationEnabled,
                        onCheckedChange = { viewModel.toggleVibration(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = CanyonOrange,
                            uncheckedThumbColor = TextSubtle,
                            uncheckedTrackColor = CanyonNavyCard
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltersAndLogTabContent(
    preferences: com.example.data.models.NotificationPreferences,
    viewModel: ProTradeViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "MINIMUM ESTIMATE VALUE FILTER",
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Filter push alerts by job budget threshold so you only get pinged for high-margin projects.",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CanyonNavyElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, CanyonNavyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Threshold Requirement",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val thresholds = listOf(0, 250, 500, 1000)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        thresholds.forEach { amt ->
                            val isSelected = preferences.minLeadValueThreshold == amt
                            val label = if (amt == 0) "All Leads" else "$$amt+"
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.updateMinLeadValueThreshold(amt) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CanyonOrange else CanyonNavyCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) CanyonOrange else CanyonNavyBorder
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = TextWhite,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RECENT ALERT AUDIT LOG",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${preferences.alertHistory.size} push notifications delivered",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
                if (preferences.alertHistory.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clickable { viewModel.clearAlertHistory() }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Log", tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        if (preferences.alertHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CanyonNavyCard)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No recent alerts recorded. Test alerts will appear here.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(preferences.alertHistory) { item ->
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(item.partner.badgeColor.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.partner.displayName.take(1),
                                    color = item.partner.badgeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.title, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    if (item.isHighPriority) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = CanyonOrange.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "HIGH PRIORITY",
                                                color = CanyonOrange,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(item.message, color = TextMuted, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                        Text(
                            text = formatTimeAgo(item.timestampMs),
                            color = TextSubtle,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimeAgo(timestampMs: Long): String {
    val diffSec = (System.currentTimeMillis() - timestampMs) / 1000
    return when {
        diffSec < 60 -> "Just now"
        diffSec < 3600 -> "${diffSec / 60}m ago"
        diffSec < 86400 -> "${diffSec / 3600}h ago"
        else -> "${diffSec / 86400}d ago"
    }
}

@Composable
private fun PlatformNotificationCard(
    partner: PartnerType,
    setting: PlatformNotificationSetting,
    masterEnabled: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    onToggleImmediate: (Boolean) -> Unit,
    onSendTest: () -> Unit
) {
    val isCardActive = masterEnabled && setting.enabled
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CanyonNavyElevated),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCardActive && setting.immediatePush) partner.badgeColor.copy(alpha = 0.5f) else CanyonNavyBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(partner.badgeColor.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, partner.badgeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = partner.displayName.take(1),
                            color = partner.badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = partner.displayName,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isCardActive && setting.immediatePush) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = CanyonOrange.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CanyonOrange.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "HIGH PRIORITY",
                                        color = CanyonOrange,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = partner.specialties,
                            color = TextMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
                Switch(
                    checked = setting.enabled,
                    onCheckedChange = onToggleEnabled,
                    enabled = masterEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextWhite,
                        checkedTrackColor = partner.badgeColor,
                        uncheckedThumbColor = TextSubtle,
                        uncheckedTrackColor = CanyonNavyCard
                    )
                )
            }
            if (setting.enabled && masterEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CanyonNavyBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (setting.immediatePush) CanyonNavyCard else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onToggleImmediate(!setting.immediatePush) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = if (setting.immediatePush) SafetyGold else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Notify me immediately for new ${partner.displayName} leads",
                                color = if (setting.immediatePush) TextWhite else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (setting.immediatePush) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                        Text(
                            text = "Instant sound chime & banner priority bypass",
                            color = TextSubtle,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 18.dp)
                        )
                    }
                    Switch(
                        checked = setting.immediatePush,
                        onCheckedChange = onToggleImmediate,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = SafetyGold,
                            uncheckedThumbColor = TextSubtle,
                            uncheckedTrackColor = CanyonNavyCard
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Send Test Alert",
                        color = CanyonCobalt,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onSendTest() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
