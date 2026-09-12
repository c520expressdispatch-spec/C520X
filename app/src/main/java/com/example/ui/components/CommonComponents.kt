package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.PushAlert
import com.example.ui.UserRole
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LightBorder
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SvgTooltipBox(
    tooltipTitle: String,
    tooltipDetail: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberTooltipState()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip(
                shape = RoundedCornerShape(8.dp),
                containerColor = Navy900,
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                    Text(
                        text = tooltipTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )
                    if (tooltipDetail != null) {
                        Text(
                            text = tooltipDetail,
                            fontSize = 9.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        },
        state = state,
        modifier = modifier
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    viewModel: MainViewModel,
    onOpenNotifications: () -> Unit
) {
    val isOffline = viewModel.isOffline.value
    val isDark = viewModel.isDarkMode.value
    val alerts = viewModel.pushAlerts.value
    val currentRole by viewModel.currentRole.collectAsState()
    val isMasterUnlocked by viewModel.isMasterUnlocked.collectAsState()

    var showMasterMenu by remember { mutableStateOf(false) }
    var showToolsMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth().testTag("app_header")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleAsideNav() },
                        modifier = Modifier.size(36.dp).testTag("header_aside_nav_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle Navigation Rail",
                            tint = OrangePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.selectTab(AppTab.DASHBOARD) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Navy900)
                                .border(1.dp, OrangePrimary, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_c520x_logo),
                                contentDescription = "C520X Logo",
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "C520X",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "100% Inflow • 80/20 & 88-93 Splits",
                                fontSize = 10.sp,
                                color = OrangePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Action Controls: All SVG Dropdowns & Icon Triggers with Hover Details (No standard buttons)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 1. MASTER ADMIN SVG DROPDOWN
                    Box {
                        SvgTooltipBox(
                            tooltipTitle = "Master Admin Vault (100% Inflow)",
                            tooltipDetail = if (isMasterUnlocked) "Vault Unlocked • Tap for Master Controls" else "PIN Protected • Tap to Authenticate"
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (currentRole == UserRole.MASTER_ADMIN) OrangePrimary.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(1.dp, if (currentRole == UserRole.MASTER_ADMIN) OrangePrimary else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { showMasterMenu = true }
                                    .testTag("master_admin_svg_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_master_vault),
                                    contentDescription = "Master Admin Vault SVG",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showMasterMenu,
                            onDismissRequest = { showMasterMenu = false },
                            modifier = Modifier.background(Navy900)
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_master_vault),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                text = {
                                    Column {
                                        Text("Open Master 100% Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Direct Clearing Treasury & Settlements", fontSize = 10.sp, color = OrangePrimary)
                                    }
                                },
                                onClick = {
                                    showMasterMenu = false
                                    viewModel.selectTab(AppTab.MASTER_VAULT)
                                }
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isMasterUnlocked) StatusGreen else StatusAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                text = {
                                    Text(
                                        text = if (isMasterUnlocked) "Lock Master Admin PIN Session" else "Authenticate Master Vault",
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    showMasterMenu = false
                                    if (isMasterUnlocked) {
                                        viewModel.lockMasterAdminSession()
                                    } else {
                                        viewModel.requestSwitchToMasterAdmin()
                                    }
                                }
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Column {
                                        Text("Split Matrix Configuration", fontSize = 12.sp, color = Color.White)
                                        Text("80/20 Contractor & 88-93% Driver Rules", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    showMasterMenu = false
                                    viewModel.selectTab(AppTab.MASTER_VAULT)
                                }
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.Sync, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Text("Broadcast Platform Cloud Sync", fontSize = 12.sp, color = Color.White)
                                },
                                onClick = {
                                    showMasterMenu = false
                                    viewModel.triggerPlatformAutoUpdate(manual = true)
                                }
                            )
                        }
                    }

                    // 2. ALL-PLATFORM EDITING TOOLS SVG DROPDOWN
                    Box {
                        SvgTooltipBox(
                            tooltipTitle = "Platform Editing & Trade Tools",
                            tooltipDetail = "Smart Invoice & Estimate sheets, Kanban, Simulation • Tap for tools"
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showToolsMenu = true }
                                    .testTag("platform_editing_tools_svg_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_trades_tools),
                                    contentDescription = "Editing Tools SVG",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showToolsMenu,
                            onDismissRequest = { showToolsMenu = false },
                            modifier = Modifier.background(Navy900)
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Column {
                                        Text("Custom Smart Estimate Sheet", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Materials, labor & markup dynamic builder", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    showToolsMenu = false
                                    viewModel.openCustomTemplateDialog("ESTIMATE")
                                }
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Column {
                                        Text("Custom Smart Invoice Sheet", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Milestones, payment terms & lien release", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    showToolsMenu = false
                                    viewModel.openCustomTemplateDialog("INVOICE")
                                }
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.ViewKanban, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Column {
                                        Text("Project Task Kanban Board", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Mobile-optimized drag & status cards", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    showToolsMenu = false
                                    viewModel.selectTab(AppTab.KANBAN_BOARD)
                                }
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = StatusRed, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Text("⚡ Simulate Lead: 200A Electrical ($1,450)", fontSize = 11.5.sp, color = Color.White)
                                },
                                onClick = {
                                    showToolsMenu = false
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
                                    Icon(Icons.Default.Handyman, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Text("❄️ Simulate Lead: Commercial HVAC ($3,200)", fontSize = 11.5.sp, color = Color.White)
                                },
                                onClick = {
                                    showToolsMenu = false
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
                                    Icon(Icons.Default.ViewInAr, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Text("📐 3D LiDAR Room Scanner", fontSize = 12.sp, color = Color.White)
                                },
                                onClick = {
                                    showToolsMenu = false
                                    viewModel.selectTab(AppTab.ROOM_SCAN_3D)
                                }
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = StatusAmber, modifier = Modifier.size(18.dp))
                                },
                                text = {
                                    Text("⏱️ GPS Field Time Clock", fontSize = 12.sp, color = Color.White)
                                },
                                onClick = {
                                    showToolsMenu = false
                                    viewModel.selectTab(AppTab.TIME_TRACKING)
                                }
                            )
                        }
                    }

                    // 3. ACTIVE ROLE SVG DROPDOWN (Strict Dashboard Isolation)
                    val activeRoleSvg = when (currentRole) {
                        UserRole.MASTER_ADMIN, UserRole.ADMIN -> R.drawable.ic_master_vault
                        UserRole.CONTRACTOR -> R.drawable.ic_contractor_helmet
                        UserRole.OWNER_OPERATOR -> R.drawable.ic_driver_truck
                        UserRole.CLIENT -> R.drawable.ic_customer_avatar
                        else -> R.drawable.ic_c520x_logo
                    }

                    Box {
                        SvgTooltipBox(
                            tooltipTitle = "Current Role: ${currentRole.label}",
                            tooltipDetail = "Tap to switch role • Isolates tools strictly to role dashboard"
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showRoleMenu = true }
                                    .testTag("active_role_svg_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = activeRoleSvg),
                                    contentDescription = currentRole.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false },
                            modifier = Modifier.background(Navy900)
                        ) {
                            UserRole.values().forEach { role ->
                                val isSelected = role == currentRole
                                val roleSvg = when (role) {
                                    UserRole.MASTER_ADMIN, UserRole.ADMIN -> R.drawable.ic_master_vault
                                    UserRole.CONTRACTOR -> R.drawable.ic_contractor_helmet
                                    UserRole.OWNER_OPERATOR -> R.drawable.ic_driver_truck
                                    UserRole.CLIENT -> R.drawable.ic_customer_avatar
                                    else -> R.drawable.ic_c520x_logo
                                }

                                DropdownMenuItem(
                                    leadingIcon = {
                                        Image(
                                            painter = painterResource(id = roleSvg),
                                            contentDescription = role.label,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    text = {
                                        Column {
                                            Text(
                                                text = role.label,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) OrangePrimary else Color.White
                                            )
                                            Text(
                                                text = "${role.badge} • Locks to dashboard",
                                                fontSize = 9.5.sp,
                                                color = Color.White.copy(alpha = 0.65f)
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Active",
                                                tint = StatusGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    onClick = {
                                        showRoleMenu = false
                                        viewModel.setRole(role)
                                    }
                                )
                            }
                        }
                    }

                    // 4. OFFLINE SYNC SVG ICON
                    SvgTooltipBox(
                        tooltipTitle = if (isOffline) "Offline Mode (Local SQLite)" else "Cloud & WebSockets Live",
                        tooltipDetail = if (isOffline) "Tap to connect & sync queue" else "Tap to simulate field offline mode"
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleOffline() }
                                .testTag("offline_toggle_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                contentDescription = "Toggle Offline Mode",
                                tint = if (isOffline) StatusRed else StatusGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // 5. THEME TOGGLE SVG ICON
                    SvgTooltipBox(
                        tooltipTitle = if (isDark) "Night Vision Mode" else "Daylight High-Contrast Mode",
                        tooltipDetail = "Tap to toggle visual theme"
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleDarkMode() }
                                .testTag("dark_mode_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark Mode",
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // 6. PUSH ALERTS BELL SVG ICON
                    SvgTooltipBox(
                        tooltipTitle = "Emergency Alerts & Inquiries",
                        tooltipDetail = "${alerts.size} pending notifications • Tap to open"
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenNotifications() }
                                .testTag("alerts_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            BadgedBox(
                                badge = {
                                    if (alerts.isNotEmpty()) {
                                        Badge(containerColor = OrangePrimary) {
                                            Text("${alerts.size}", fontSize = 9.sp, color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Alerts",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Top Status strip (Timer running, offline warning)
            if (viewModel.isTimerRunning.value) {
                Spacer(modifier = Modifier.height(6.dp))
                val seconds = viewModel.timerSeconds.value
                val hrs = seconds / 3600
                val mins = (seconds % 3600) / 60
                val secs = seconds % 60
                val timeFormatted = String.format("%02d:%02d:%02d", hrs, mins, secs)

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OrangePrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary),
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.selectTab(AppTab.TIME_TRACKING) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AUTOMATED CLOCK ACTIVE: $timeFormatted",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        }
                        Text(
                            text = "Tap to Manage >",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else if (isOffline) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusAmber.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline",
                            tint = StatusAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Offline Mode: 100% Local SQLite active. 0 data loss.",
                            fontSize = 11.sp,
                            color = StatusAmber
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppTabNavigation(
    currentTab: AppTab,
    availableTabs: List<AppTab>? = null,
    onTabSelected: (AppTab) -> Unit
) {
    val allTabIcons = mapOf(
        AppTab.MASTER_VAULT to Icons.Default.Shield,
        AppTab.DASHBOARD to Icons.Default.BarChart,
        AppTab.USER_PORTAL to Icons.Default.Person,
        AppTab.FREIGHT_LOADBOARD to Icons.Default.LocalShipping,
        AppTab.LIVE_MARKETBOARD to Icons.Default.Storefront,
        AppTab.CONTRACTOR_WORKFLOW to Icons.Default.Handyman,
        AppTab.CUSTOMER_HUB to Icons.AutoMirrored.Filled.Assignment,
        AppTab.PAYMENTS to Icons.Default.Payment,
        AppTab.KANBAN_BOARD to Icons.Default.ViewKanban,
        AppTab.AI_ANALYTICS to Icons.Default.AutoAwesome,
        AppTab.DISPATCH_SCHEDULE to Icons.Default.AccessTime,
        AppTab.TIME_TRACKING to Icons.Default.AccessTime,
        AppTab.LEADS_QUOTES to Icons.Default.People,
        AppTab.CHAT_SLACK to Icons.AutoMirrored.Filled.Chat,
        AppTab.ROOM_SCAN_3D to Icons.Default.ViewInAr,
        AppTab.REVIEWS to Icons.Default.Star,
        AppTab.SECURITY_SETTINGS to Icons.Default.Security
    )

    val activeTabsList = availableTabs ?: listOf(
        AppTab.MASTER_VAULT,
        AppTab.DASHBOARD,
        AppTab.USER_PORTAL,
        AppTab.FREIGHT_LOADBOARD,
        AppTab.LIVE_MARKETBOARD,
        AppTab.CONTRACTOR_WORKFLOW,
        AppTab.CUSTOMER_HUB,
        AppTab.PAYMENTS,
        AppTab.KANBAN_BOARD,
        AppTab.AI_ANALYTICS,
        AppTab.DISPATCH_SCHEDULE,
        AppTab.TIME_TRACKING,
        AppTab.LEADS_QUOTES,
        AppTab.CHAT_SLACK,
        AppTab.ROOM_SCAN_3D,
        AppTab.REVIEWS,
        AppTab.SECURITY_SETTINGS
    )

    val tabs = activeTabsList.map { it to (allTabIcons[it] ?: Icons.Default.Star) }

    ScrollableTabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == currentTab }.coerceAtLeast(0),
        edgePadding = 8.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = OrangePrimary,
        modifier = Modifier.fillMaxWidth().testTag("main_tab_row")
    ) {
        tabs.forEach { (tab, icon) ->
            val isSelected = tab == currentTab
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, textCol) = when (status) {
        "Completed" -> StatusGreen.copy(alpha = 0.18f) to StatusGreen
        "In Progress" -> StatusBlue.copy(alpha = 0.18f) to StatusBlue
        "Dispatched" -> OrangePrimary.copy(alpha = 0.18f) to OrangePrimary
        "Scheduled" -> StatusPurple.copy(alpha = 0.18f) to StatusPurple
        "Pending Estimate" -> StatusAmber.copy(alpha = 0.18f) to StatusAmber
        else -> Color.Gray.copy(alpha = 0.15f) to Color.Gray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(0.5.dp, textCol.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status,
            color = textCol,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NotificationSheetDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val alerts by viewModel.pushAlerts.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    var reviewingAlert by remember { mutableStateOf<PushAlert?>(null) }
    var editingAlert by remember { mutableStateOf<PushAlert?>(null) }
    var isCreatingAlert by remember { mutableStateOf(false) }

    val filteredAlerts = alerts.filter { alert ->
        val matchesFilter = when (selectedFilter) {
            "ALL" -> true
            "UNREAD" -> !alert.isRead
            "URGENT" -> alert.priority.equals("Urgent", ignoreCase = true)
            else -> alert.type.equals(selectedFilter, ignoreCase = true)
        }
        val matchesSearch = searchQuery.isBlank() ||
            alert.title.contains(searchQuery, ignoreCase = true) ||
            alert.message.contains(searchQuery, ignoreCase = true) ||
            alert.sender.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(6.dp)
                .testTag("notification_sheet_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OrangePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alerts",
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Notifications & Comms Hub",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val unreadCount = alerts.count { !it.isRead }
                            Text(
                                text = "$unreadCount unread • ${alerts.size} total alerts",
                                fontSize = 11.sp,
                                color = if (unreadCount > 0) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        IconButton(
                            onClick = { isCreatingAlert = true },
                            modifier = Modifier.testTag("new_alert_broadcast_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Broadcast", tint = OrangePrimary)
                        }
                        IconButton(
                            onClick = { viewModel.markAllAlertsRead() },
                            modifier = Modifier.testTag("mark_all_alerts_read_btn")
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Mark All Read", tint = StatusGreen)
                        }
                        IconButton(
                            onClick = { viewModel.clearAllPushAlerts() },
                            modifier = Modifier.testTag("clear_all_alerts_btn")
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = MaterialTheme.colorScheme.error)
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_notification_sheet_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search notifications, messages, alerts...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("notification_search_input")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        "ALL" to "All (${alerts.size})",
                        "UNREAD" to "Unread (${alerts.count { !it.isRead }})",
                        "URGENT" to "Urgent (${alerts.count { it.priority.equals("Urgent", ignoreCase = true) }})",
                        "slack" to "Messages (${alerts.count { it.type == "slack" }})",
                        "payment" to "Payments (${alerts.count { it.type == "payment" }})",
                        "lead" to "Leads (${alerts.count { it.type == "lead" }})",
                        "dispatch" to "Dispatch (${alerts.count { it.type == "dispatch" }})"
                    ).forEach { (filterKey, label) ->
                        val isSelected = selectedFilter == filterKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filterKey },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimary.copy(alpha = 0.2f),
                                selectedLabelColor = OrangePrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Alert Items List
                if (filteredAlerts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No notifications match your filters", color = Color.Gray, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { isCreatingAlert = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Create Alert Broadcast", fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredAlerts, key = { it.id }) { alert ->
                            NotificationAlertItemCard(
                                alert = alert,
                                onReview = {
                                    reviewingAlert = alert
                                    if (!alert.isRead) {
                                        viewModel.toggleAlertRead(alert.id)
                                    }
                                },
                                onEdit = { editingAlert = alert },
                                onToggleRead = { viewModel.toggleAlertRead(alert.id) },
                                onDelete = { viewModel.deletePushAlert(alert.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Review Alert Details & Deep-Link Navigation
    reviewingAlert?.let { alert ->
        ReviewNotificationDialog(
            alert = alert,
            onDismiss = { reviewingAlert = null },
            onEdit = {
                reviewingAlert = null
                editingAlert = alert
            },
            onDelete = {
                viewModel.deletePushAlert(alert.id)
                reviewingAlert = null
            },
            onReply = { replyText ->
                viewModel.replyToNotificationAlert(alert.id, replyText)
                reviewingAlert = null
            },
            onNavigateToModule = { targetTab ->
                reviewingAlert = null
                onDismiss()
                viewModel.selectTab(targetTab)
            }
        )
    }

    // Modal: Edit Notification
    editingAlert?.let { alert ->
        EditPushAlertDialog(
            initialAlert = alert,
            onDismiss = { editingAlert = null },
            onSave = { updated ->
                viewModel.updatePushAlert(updated)
                editingAlert = null
            }
        )
    }

    // Modal: Create Notification
    if (isCreatingAlert) {
        EditPushAlertDialog(
            initialAlert = null,
            onDismiss = { isCreatingAlert = false },
            onSave = { newAlert ->
                viewModel.createPushAlert(
                    title = newAlert.title,
                    message = newAlert.message,
                    type = newAlert.type,
                    priority = newAlert.priority,
                    sender = newAlert.sender,
                    actionPayload = newAlert.actionPayload
                )
                isCreatingAlert = false
            }
        )
    }
}

@Composable
fun NotificationSheetDialog(
    alerts: List<PushAlert>,
    onDismiss: () -> Unit
) {
    // Read-only fallback dialog
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f).padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = OrangePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(alerts, key = { it.id }) { alert ->
                        NotificationAlertItemCard(
                            alert = alert,
                            onReview = {},
                            onEdit = {},
                            onToggleRead = {},
                            onDelete = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationAlertItemCard(
    alert: PushAlert,
    onReview: () -> Unit,
    onEdit: () -> Unit,
    onToggleRead: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = when (alert.type) {
        "payment" -> StatusGreen
        "lead" -> OrangePrimary
        "slack" -> StatusPurple
        "dispatch" -> StatusBlue
        else -> MaterialTheme.colorScheme.primary
    }

    val isUrgent = alert.priority.equals("Urgent", ignoreCase = true)
    val isHigh = alert.priority.equals("High", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!alert.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (!alert.isRead) 1.5.dp else 1.dp,
            color = if (isUrgent) StatusRed.copy(alpha = 0.6f) else if (!alert.isRead) OrangePrimary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_card_${alert.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Category, Priority, Unread dot, and Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!alert.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary)
                        )
                    }

                    // Category Pill
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = categoryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = when (alert.type) {
                                "slack" -> "COMMS / SLACK"
                                "payment" -> "PAYMENTS"
                                "lead" -> "LEAD / CRM"
                                "dispatch" -> "DISPATCH"
                                else -> alert.type.uppercase()
                            },
                            color = categoryColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    // Priority Pill
                    if (isUrgent || isHigh) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isUrgent) StatusRed.copy(alpha = 0.15f) else StatusAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = alert.priority.uppercase(),
                                color = if (isUrgent) StatusRed else StatusAmber,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alert.timestamp,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sender attribution
            if (alert.sender.isNotBlank()) {
                Text(
                    text = "From: ${alert.sender}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = categoryColor
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Title
            Text(
                text = alert.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Message Body (up to 3 lines preview)
            Text(
                text = alert.message,
                fontSize = 12.sp,
                maxLines = 3,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Toolbar: Review, Edit, Toggle Read, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onReview,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("review_notification_btn_${alert.id}")
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Review Message", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("edit_notification_btn_${alert.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Edit", fontSize = 11.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onToggleRead,
                        modifier = Modifier.size(28.dp).testTag("toggle_read_btn_${alert.id}")
                    ) {
                        Icon(
                            imageVector = if (alert.isRead) Icons.Default.MarkEmailUnread else Icons.Default.MarkEmailRead,
                            contentDescription = if (alert.isRead) "Mark Unread" else "Mark Read",
                            tint = if (alert.isRead) Color.Gray else OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).testTag("delete_notification_btn_${alert.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewNotificationDialog(
    alert: PushAlert,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReply: (String) -> Unit,
    onNavigateToModule: (AppTab) -> Unit
) {
    var replyText by remember { mutableStateOf("") }
    val categoryColor = when (alert.type) {
        "payment" -> StatusGreen
        "lead" -> OrangePrimary
        "slack" -> StatusPurple
        "dispatch" -> StatusBlue
        else -> MaterialTheme.colorScheme.primary
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(12.dp)
                .testTag("review_notification_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = categoryColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = alert.type.uppercase(),
                                color = categoryColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (alert.priority.equals("Urgent", ignoreCase = true)) StatusRed.copy(alpha = 0.15f) else OrangePrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${alert.priority.uppercase()} PRIORITY",
                                color = if (alert.priority.equals("Urgent", ignoreCase = true)) StatusRed else OrangePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sender & Timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (alert.sender.isNotBlank()) "Sender: ${alert.sender}" else "System Alert",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = alert.timestamp,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Full Title
                Text(
                    text = alert.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Full Message Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = alert.message,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Deep-link Action Button
                val (targetTab, buttonLabel, buttonIcon) = when (alert.type) {
                    "slack" -> Triple(AppTab.CHAT_SLACK, "Open in Live Comms & Slack", Icons.AutoMirrored.Filled.Chat)
                    "payment" -> Triple(AppTab.PAYMENTS, "Go to Payments & QuickBooks", Icons.Default.Payment)
                    "lead" -> Triple(AppTab.LEADS_QUOTES, "Go to Leads & CRM", Icons.Default.People)
                    "dispatch" -> Triple(AppTab.DISPATCH_SCHEDULE, "Go to Dispatch & Schedule", Icons.Default.AccessTime)
                    else -> Triple(AppTab.KANBAN_BOARD, "Go to Project Kanban", Icons.Default.ViewKanban)
                }

                Button(
                    onClick = { onNavigateToModule(targetTab) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = categoryColor),
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("notification_deeplink_action_btn")
                ) {
                    Icon(buttonIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(buttonLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Reply Field
                Text("Quick Reply / Note:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Type quick reply to team...", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(44.dp).testTag("notification_reply_input")
                    )

                    Button(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                onReply(replyText)
                                replyText = ""
                            }
                        },
                        enabled = replyText.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.height(44.dp).testTag("notification_send_reply_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Send", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer editing tools: Edit Notification, Delete Notification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("review_dialog_edit_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Alert", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onDelete,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("review_dialog_delete_btn")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 11.sp)
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text("Close", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EditPushAlertDialog(
    initialAlert: PushAlert?,
    onDismiss: () -> Unit,
    onSave: (PushAlert) -> Unit
) {
    var title by remember { mutableStateOf(initialAlert?.title ?: "") }
    var message by remember { mutableStateOf(initialAlert?.message ?: "") }
    var sender by remember { mutableStateOf(initialAlert?.sender ?: "Alex Ramirez (Tech)") }
    var priority by remember { mutableStateOf(initialAlert?.priority ?: "Normal") }
    var type by remember { mutableStateOf(initialAlert?.type ?: "slack") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("edit_push_alert_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (initialAlert != null) "Edit Notification Alert" else "Create Notification Broadcast",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Configure message details, sender attribution, and urgency level.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Alert Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_alert_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Message Input
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message Body") },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_alert_message_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sender Input
                OutlinedTextField(
                    value = sender,
                    onValueChange = { sender = it },
                    label = { Text("Sender / Author Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_alert_sender_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Priority Selection
                Text("Urgency Level:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Normal", "High", "Urgent").forEach { pOption ->
                        val isSelected = priority.equals(pOption, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { priority = pOption },
                            label = { Text(pOption, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (pOption == "Urgent") StatusRed.copy(alpha = 0.2f) else OrangePrimary.copy(alpha = 0.2f),
                                selectedLabelColor = if (pOption == "Urgent") StatusRed else OrangePrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Selection
                Text("Notification Type:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "slack" to "Comms / Slack",
                        "lead" to "Leads",
                        "payment" to "Payments",
                        "dispatch" to "Dispatch",
                        "system" to "System"
                    ).forEach { (typeKey, label) ->
                        val isSelected = type == typeKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { type = typeKey },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimary.copy(alpha = 0.2f),
                                selectedLabelColor = OrangePrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save & Cancel Buttons
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
                            if (title.isNotBlank() && message.isNotBlank()) {
                                val result = initialAlert?.copy(
                                    title = title.trim(),
                                    message = message.trim(),
                                    sender = sender.trim(),
                                    priority = priority,
                                    type = type
                                ) ?: PushAlert(
                                    id = System.currentTimeMillis().toString(),
                                    title = title.trim(),
                                    message = message.trim(),
                                    type = type,
                                    timestamp = "Just now",
                                    priority = priority,
                                    sender = sender.trim()
                                )
                                onSave(result)
                            }
                        },
                        enabled = title.isNotBlank() && message.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.testTag("save_notification_alert_btn")
                    ) {
                        Text(if (initialAlert != null) "Save Changes" else "Broadcast Alert", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BiometricLockDialog(
    onUnlock: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("biometric_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric Security",
                        tint = OrangePrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Biometric Security Check",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "C520X End-to-End Encryption active. Confirm biometric credentials to unlock contractor operations.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onUnlock,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.fillMaxWidth().testTag("unlock_biometric_button")
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Authenticate with Biometrics", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MasterAuthPinDialog(
    errorMessage: String?,
    onUnlock: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pinText by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary),
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("master_pin_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Master Authority Lock",
                        tint = OrangePrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Master C520X Admin Sign-On",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Master admin access is strictly isolated. Enter your Master Security Passcode to access 100% Platform Treasury & Vault.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pinText,
                    onValueChange = { pinText = it },
                    label = { Text("Master PIN (Default: 5200)") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        } else {
                            Text("Default master code: 5200", color = Color.Gray, fontSize = 10.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("master_pin_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).testTag("cancel_master_pin_button")
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onUnlock(pinText) },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.weight(1.3f).testTag("unlock_master_pin_button")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unlock 100%", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
