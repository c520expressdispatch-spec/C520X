package com.example.ui.components

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hvac
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomerInquiryCloudSyncService
import com.example.data.CustomerLeadNotificationManager
import com.example.data.LeadEntity
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed

/**
 * Real-Time Customer Inquiry & Cloud Ingestion Console.
 * Manages Firebase Cloud Messaging (FCM) push notifications, Firestore real-time listeners,
 * and external webhook endpoints (WordPress, Zapier, Webflow, Thumbtack, Angi).
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RealtimeCustomerInquiryHubCard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fcmToken by viewModel.fcmDeviceToken.collectAsState()
    val isFirestoreActive by viewModel.isFirestoreActive.collectAsState()
    val lastSyncTime by viewModel.lastCloudSyncTime.collectAsState()
    val totalSynced by viewModel.totalInquiriesSynced.collectAsState()
    val recentInquiries by viewModel.recentLiveInquiries.collectAsState()

    var showWebhookDetails by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .fillMaxWidth()
            .testTag("realtime_customer_inquiry_hub_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Live Inquiry Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(StatusGreen.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Real-Time Customer Inquiries",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StatusGreen.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(StatusGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE LISTENER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusGreen
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Connected to Firebase Firestore & Cloud Messaging (FCM)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Live Pipeline Telemetry Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("FCM Push Alerts", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Active & Ready", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                        Text("Android Heads-Up Channel", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Firestore Ingestion", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(if (isFirestoreActive) "Syncing Live" else "Standby Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isFirestoreActive) StatusGreen else StatusBlue)
                        Text("$totalSynced Leads Logged", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Real-Time Test Simulations (Instant Phone Vibration & Push Verification)
            Text(
                text = "TEST REAL-TIME INBOUND LEAD PIPELINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap any scenario below to verify your phone's instant notification, vibration, and auto-dispatch:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // SVG Dropdown for Lead Simulation
            var showSimLeadMenu by remember { mutableStateOf(false) }
            val leadTooltipState = rememberTooltipState()

            Box {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip(
                            shape = RoundedCornerShape(8.dp),
                            containerColor = Navy900,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = "Simulate Inbound Customer Leads • Tap for dropdown options",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    state = leadTooltipState
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary),
                        modifier = Modifier
                            .clickable { showSimLeadMenu = true }
                            .testTag("simulate_leads_dropdown_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_trades_tools),
                                contentDescription = "Simulate Leads SVG",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Select Simulation Lead Scenario",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Electrical, HVAC, EV Charger instant dispatch",
                                    fontSize = 10.sp,
                                    color = OrangePrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = showSimLeadMenu,
                    onDismissRequest = { showSimLeadMenu = false },
                    modifier = Modifier.background(Navy900)
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = StatusRed, modifier = Modifier.size(18.dp))
                        },
                        text = {
                            Column {
                                Text("Emergency Panel Trip ($1,450)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Sarah Jenkins • Main 200A burning odor", fontSize = 10.sp, color = Color.LightGray)
                            }
                        },
                        onClick = {
                            showSimLeadMenu = false
                            viewModel.simulateInboundCustomerInquiry(
                                trade = "Electrical",
                                scope = "Main 200A breaker tripping continuously. Burning odor from panel in utility closet.",
                                budget = 1450.0,
                                clientName = "Sarah Jenkins (Tanque Verde)",
                                phone = "(520) 441-8920",
                                urgency = "Emergency"
                            )
                        }
                    )

                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(Icons.Default.Hvac, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                        },
                        text = {
                            Column {
                                Text("HVAC Outage ($3,200)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("David Chen • 5-ton RTU blower failure", fontSize = 10.sp, color = Color.LightGray)
                            }
                        },
                        onClick = {
                            showSimLeadMenu = false
                            viewModel.simulateInboundCustomerInquiry(
                                trade = "HVAC",
                                scope = "5-ton commercial rooftop unit blower failure. Restaurant kitchen overheating.",
                                budget = 3200.0,
                                clientName = "David Chen (Highland Plaza)",
                                phone = "(520) 882-9011",
                                urgency = "Emergency"
                            )
                        }
                    )

                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(Icons.Default.ElectricalServices, contentDescription = null, tint = StatusPurple, modifier = Modifier.size(18.dp))
                        },
                        text = {
                            Column {
                                Text("EV Charger Install ($1,680)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Michael Ross • Tesla 48A 240V dedicated", fontSize = 10.sp, color = Color.LightGray)
                            }
                        },
                        onClick = {
                            showSimLeadMenu = false
                            viewModel.simulateInboundCustomerInquiry(
                                trade = "EV Charger",
                                scope = "Tesla Universal Wall Connector 48A 240V dedicated circuit installation in garage.",
                                budget = 1680.0,
                                clientName = "Michael Ross (Catalina Foothills)",
                                phone = "(520) 773-4412",
                                urgency = "Today"
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Webhook & FCM Cloud Integration Credentials Drawer
            Surface(
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Webhook, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "External Webhooks & FCM Token Bridge",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        OutlinedButton(
                            onClick = { showWebhookDetails = !showWebhookDetails },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(if (showWebhookDetails) "Hide" else "View Endpoints", fontSize = 10.sp)
                        }
                    }

                    AnimatedVisibility(visible = showWebhookDetails) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = "1. INBOUND WEBHOOK ENDPOINT (POST)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Navy900,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = CustomerInquiryCloudSyncService.cloudWebhookEndpoint,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = AccentCyan,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Webhook URL", CustomerInquiryCloudSyncService.cloudWebhookEndpoint))
                                            viewModel.showNotice("Webhook URL copied to clipboard!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "2. THIS DEVICE FCM REGISTRATION TOKEN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Navy900,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = fcmToken.take(45) + "...",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = StatusGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("FCM Token", fcmToken))
                                            viewModel.showNotice("FCM Device Token copied!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Payload Schema (JSON):",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "{\n  \"clientName\": \"Customer Name\",\n  \"clientPhone\": \"(520) 555-0100\",\n  \"serviceTrade\": \"Electrical\",\n  \"projectDescription\": \"Project scope details\",\n  \"budget\": 1200.0,\n  \"urgency\": \"Emergency\"\n}",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Live Stream Feed (Most recent in-flight leads)
            if (recentInquiries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE INCOMING STREAM (${recentInquiries.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Text(lastSyncTime, fontSize = 10.sp, color = StatusBlue)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    recentInquiries.take(3).forEach { lead ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
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
                                        Text(lead.clientName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (lead.urgency.lowercase() == "emergency") StatusRed.copy(alpha = 0.15f) else StatusAmber.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = lead.urgency.uppercase(),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (lead.urgency.lowercase() == "emergency") StatusRed else StatusAmber,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${lead.serviceTrade} • $${"%,.0f".format(lead.budget)} • ${lead.clientPhone}",
                                        fontSize = 11.sp,
                                        color = StatusBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = lead.projectDescription,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.selectTab(AppTab.LEADS_QUOTES) },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Open", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
