package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.UserRole
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen

@Composable
fun SettingsSecurityScreen(viewModel: MainViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val syncMessage by viewModel.syncStatusMessage.collectAsState()
    val pendingCount by viewModel.syncPendingCount.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val slackEnabled by viewModel.slackSyncEnabled.collectAsState()
    val calendarEnabled by viewModel.calendarSyncEnabled.collectAsState()
    val language by viewModel.language.collectAsState()

    // Platform Auto-Update Engine (All Users)
    val isAutoUpdateEnabled by viewModel.isAutoUpdateEnabled.collectAsState()
    val platformVersion by viewModel.platformVersion.collectAsState()
    val autoUpdateStatus by viewModel.autoUpdateStatus.collectAsState()

    // Official Website & Work Email
    val officialWebsiteUrl by viewModel.officialWebsiteUrl.collectAsState()
    val workEmail by viewModel.workEmail.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showEditEmailDialog by remember { mutableStateOf(false) }
    var editedEmailInput by remember { mutableStateOf("") }

    // Workflow Integrity Verification
    val workflowIntegrity by viewModel.workflowIntegrity.collectAsState()
    var showWorkflowMatrix by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Security Header with SVG Shield
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OrangePrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_settings_shield),
                                    contentDescription = "Security Shield SVG",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SYSTEM INTEGRITY & RBAC",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "AES-256 ENCRYPTED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Security, Integrations & Platform Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Configure Role-Based Access Controls (RBAC), multi-user live auto-updates, workflow verification, and biometric security.",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        // =================================================================
        // 1. REAL-TIME PLATFORM AUTO-UPDATE (ALL USERS SYNCHRONIZED)
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("auto_update_platform_card")
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
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OrangePrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_settings_auto_update),
                                    contentDescription = "Platform Auto Update SVG",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "PLATFORM AUTO-UPDATE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = OrangePrimary
                                )
                                Text(
                                    text = "Broadcast changes across all users in real-time",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isAutoUpdateEnabled) StatusGreen.copy(alpha = 0.15f) else StatusAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = platformVersion,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAutoUpdateEnabled) StatusGreen else StatusAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto-Update All Users & Workflows",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = autoUpdateStatus,
                                fontSize = 10.sp,
                                color = if (isAutoUpdateEnabled) StatusGreen else Color.Gray
                            )
                        }
                        Switch(
                            checked = isAutoUpdateEnabled,
                            onCheckedChange = { viewModel.toggleAutoUpdate() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OrangePrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.triggerPlatformAutoUpdate(manual = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("broadcast_auto_update_btn")
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcast Sync To All Users Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // =================================================================
        // OFFICIAL WEBSITE & WORK EMAIL (GOOGLE SITE & CORPORATE DOMAIN)
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("settings_official_website_card")
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
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StatusGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Language,
                                    contentDescription = "Official Website",
                                    tint = StatusGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "OFFICIAL WEBSITE & WORK EMAIL",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Verified Domain & Public Presence",
                                    fontSize = 10.sp,
                                    color = StatusGreen
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(StatusGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VERIFIED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Website URL
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Official Web Portal", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    text = officialWebsiteUrl,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(officialWebsiteUrl))
                                        Toast.makeText(context, "Website link copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                }
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(officialWebsiteUrl))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Work Email
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Corporate Work Email", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    text = workEmail,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary,
                                    maxLines = 1
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:$workEmail")
                                        }
                                        try {
                                            context.startActivity(mailIntent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Mail, contentDescription = "Send Email", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                }
                                Button(
                                    onClick = {
                                        editedEmailInput = workEmail
                                        showEditEmailDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // =================================================================
        // 2. WORKFLOW INTEGRITY & VERIFICATION (CHECK EACH WORKFLOW IS RIGHT)
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, StatusBlue.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("workflow_verification_card")
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
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StatusBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_trades_tools),
                                    contentDescription = "Workflow Tools SVG",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "WORKFLOW VERIFICATION ENGINE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = StatusBlue
                                )
                                Text(
                                    text = "Validate every role's pipeline and security boundaries",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "VERIFIED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Current user workflow check summary
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Active Role: ${currentRole.label}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = viewModel.checkWorkflowIntegrity(currentRole),
                                    fontSize = 11.sp,
                                    color = StatusGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    showWorkflowMatrix = !showWorkflowMatrix
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(if (showWorkflowMatrix) "Hide Matrix" else "Inspect All", fontSize = 10.sp)
                            }
                        }
                    }

                    // Collapsible matrix of all 7 roles' workflows
                    AnimatedVisibility(visible = showWorkflowMatrix) {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            workflowIntegrity.forEach { (role, status) ->
                                val isCurrent = role == currentRole
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isCurrent) OrangePrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    border = if (isCurrent) BorderStroke(1.dp, OrangePrimary) else null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = if (isCurrent) OrangePrimary else StatusGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(role.label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (isCurrent) OrangePrimary else MaterialTheme.colorScheme.onSurface)
                                            Text(status, fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // =================================================================
        // 3. ROLE-BASED ACCESS CONTROL (RBAC) WITH BULK SVG ICONS
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROLE-BASED ACCESS CONTROL (RBAC)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Active: ${currentRole.badge}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    UserRole.values().forEach { role ->
                        val isCurrent = role == currentRole

                        // Bulk SVG Icons per role
                        val roleSvgRes = when (role) {
                            UserRole.MASTER_ADMIN -> R.drawable.ic_master_vault
                            UserRole.ADMIN -> R.drawable.ic_settings_shield
                            UserRole.CONTRACTOR -> R.drawable.ic_contractor_helmet
                            UserRole.OWNER_OPERATOR -> R.drawable.ic_driver_truck
                            UserRole.DISPATCHER -> R.drawable.ic_trades_tools
                            UserRole.LEAD_TECH -> R.drawable.ic_trades_tools
                            UserRole.CLIENT -> R.drawable.ic_customer_avatar
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) OrangePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isCurrent) BorderStroke(1.dp, OrangePrimary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.setRole(role) }
                                .testTag("select_role_${role.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    // Custom SVG icon for every user role
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isCurrent) OrangePrimary.copy(alpha = 0.25f) else Color.Gray.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = roleSvgRes),
                                            contentDescription = "${role.label} SVG",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = role.label,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = if (isCurrent) OrangePrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        val permText = when (role) {
                                            UserRole.MASTER_ADMIN -> "100% Platform Authority: master clearing vault, rate overrides, 80/20 & 88-93/7-12 revenue splits"
                                            UserRole.ADMIN -> "Full access to dispatch, billing, team payroll, rates, API settings"
                                            UserRole.DISPATCHER -> "Job schedule creation, tech assignments, customer quotes, bulk import"
                                            UserRole.LEAD_TECH -> "Field operations, time clock punch, site photo uploads, client chat"
                                            UserRole.CLIENT -> "View invoices, pay quotes, submit 5-star ratings, booking escrow"
                                            UserRole.CONTRACTOR -> "Claim open marketplace leads, submit bids, 80/20 escrow payout"
                                            UserRole.OWNER_OPERATOR -> "DAT One live loadboard, sign rate confirmations, 24hr QuickPay"
                                        }
                                        Text(text = permText, fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                if (isCurrent) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // =================================================================
        // 4. BIOMETRICS & AUTHENTICATION (SVG ICON)
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(OrangePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_settings_biometrics),
                                contentDescription = "Biometrics SVG",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BIOMETRIC AUTHENTICATION",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("FaceID / Fingerprint Lock", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Require biometrics for payout transfers and overrides", fontSize = 10.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = { viewModel.toggleBiometric() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OrangePrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.lockBiometric() },
                        modifier = Modifier.fillMaxWidth().testTag("simulate_lock_button")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Biometric Lockout Flow", fontSize = 12.sp)
                    }
                }
            }
        }

        // =================================================================
        // 5. OFFLINE SYNC & CLOUD PERSISTENCE (SVG ICON)
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(OrangePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_settings_cloud_sync),
                                contentDescription = "Cloud Sync SVG",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OFFLINE SYNC & LOCAL PERSISTENCE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(syncMessage, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Room Database (SQLite) • $pendingCount queued writes", fontSize = 10.sp, color = Color.Gray)
                        }

                        Button(
                            onClick = { viewModel.triggerSync() },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Now", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // =================================================================
        // 6. EXTERNAL INTEGRATIONS (SLACK & CALENDAR) (SVG ICON)
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(OrangePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_settings_slack),
                                contentDescription = "Slack SVG",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EXTERNAL INTEGRATIONS & NOTIFICATIONS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Slack toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Slack Webhooks (#field-ops)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("Mirror field dispatches and payments in real time", fontSize = 10.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = slackEnabled,
                            onCheckedChange = { viewModel.toggleSlackSync() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OrangePrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Google Calendar Sync", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("Auto-sync dispatched jobs to tech calendars", fontSize = 10.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = calendarEnabled,
                            onCheckedChange = { viewModel.toggleCalendarSync() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OrangePrimary)
                        )
                    }
                }
            }
        }

        // =================================================================
        // 7. MULTILINGUAL LOCALIZATION
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MULTILINGUAL LOCALIZATION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("EN" to "English (US)", "ES" to "Español", "FR" to "Français").forEach { (code, lbl) ->
                            val isSel = language == code
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setLanguage(code) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = code,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = lbl,
                                        fontSize = 9.sp,
                                        color = if (isSel) Color.White.copy(alpha = 0.9f) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =================================================================
        // 8. EXPORTABLE AUDIT REPORTS
        // =================================================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "COMPLIANCE & AUDIT REPORTING",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.showNotice("Exported C520X Operations Audit Report (CSV/PDF) generated") },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("export_audit_report_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Full Dispatch & Financial Audit (PDF/CSV)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showEditEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEditEmailDialog = false },
            title = {
                Text("Update Website Work Email", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Enter your newly configured corporate work email address. This will be updated across all dispatch communication, contractor receipts, and invoice headers.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editedEmailInput,
                        onValueChange = { editedEmailInput = it },
                        label = { Text("Corporate Work Email") },
                        placeholder = { Text("dispatch@yourdomain.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedEmailInput.isNotBlank()) {
                            viewModel.updateWorkEmail(editedEmailInput)
                            Toast.makeText(context, "Work email updated to ${editedEmailInput.trim()}", Toast.LENGTH_SHORT).show()
                        }
                        showEditEmailDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Save Email")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditEmailDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
