package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditScore
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.UserRole
import com.example.ui.components.GoogleAiStudioHubCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterAdminHubScreen(viewModel: MainViewModel) {
    val engineState by viewModel.masterSplitEngineState.collectAsState()
    val driverFee by viewModel.driverFeePercent.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val isOverrideActive by viewModel.isMasterOverrideEnabled.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val allLoads by viewModel.freightLoads.collectAsState()

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val preciseCurrency = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 2 } }

    var simAmountInput by remember { mutableStateOf("2500") }
    var selectedAuditTab by remember { mutableStateOf("ALL") } // ALL, TRADES, FREIGHT

    val simValue = simAmountInput.toDoubleOrNull() ?: 2500.0
    val simContractorPayout = simValue * 0.80
    val simContractorC520X = simValue * 0.20
    val simDriverPayout = simValue * ((100.0 - driverFee) / 100.0)
    val simDriverC520X = simValue * (driverFee / 100.0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("master_admin_hub_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 860.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ITEM 1: Master Authority Banner & Platform Owner Identity
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy900),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(StatusAmber)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("master_authority_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StatusAmber.copy(alpha = 0.18f))
                                        .border(1.5.dp, StatusAmber, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Master Admin",
                                        tint = StatusAmber,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "MASTER C520X APP",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = StatusAmber,
                                            modifier = Modifier.padding(1.dp)
                                        ) {
                                            Text(
                                                text = "100% CONTROL",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Navy900,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Platform Owner: c520express.dispatch@gmail.com",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            // 100% Platform Authority Indicator
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isOverrideActive) StatusGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        if (isOverrideActive) StatusGreen else Color.Gray
                                    )
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isOverrideActive) StatusGreen else Color.Gray)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isOverrideActive) "100% UNRESTRICTED" else "OVERRIDE LOCKED",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOverrideActive) StatusGreen else Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // 100% Vault Refined Dropdown Card
                        var isVaultDropdownOpen by remember { mutableStateOf(false) }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, StatusAmber.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isVaultDropdownOpen = !isVaultDropdownOpen }
                                .testTag("master_vault_dropdown_card")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_master_vault),
                                            contentDescription = "Vault",
                                            tint = StatusAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "100% Master Vault Authority",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = StatusAmber.copy(alpha = 0.25f)
                                        ) {
                                            Text(
                                                text = if (isVaultDropdownOpen) "HIDE" else "VIEW 100% RULES",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusAmber,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = if (isVaultDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = "Toggle Vault Info",
                                            tint = StatusAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Receives 100% of client & shipper payments directly with full escrow override command.",
                                    fontSize = 11.5.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    lineHeight = 16.sp
                                )

                                AnimatedVisibility(visible = isVaultDropdownOpen) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = StatusGreen,
                                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = "Direct Inflow Settlement",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "100% of all client billing deposits into C520X Clearing Vault before disbursement.",
                                                    fontSize = 10.5.sp,
                                                    color = Color.White.copy(alpha = 0.75f)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = StatusGreen,
                                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = "Automated Payout Rules",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "80/20 trade splits & 88–93% freight pay release automatically on completion proof.",
                                                    fontSize = 10.5.sp,
                                                    color = Color.White.copy(alpha = 0.75f)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = StatusGreen,
                                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = "Unrestricted Master Override",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Instant escrow release, fee adjustments, and full platform task command.",
                                                    fontSize = 10.5.sp,
                                                    color = Color.White.copy(alpha = 0.75f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Master Simulate View Dropdown Selector
                        var showSimulateMenu by remember { mutableStateOf(false) }
                        val simulateTooltipState = rememberTooltipState()

                        val currentSimulateSvg = when (currentRole) {
                            UserRole.MASTER_ADMIN, UserRole.ADMIN -> R.drawable.ic_master_vault
                            UserRole.CONTRACTOR -> R.drawable.ic_contractor_helmet
                            UserRole.OWNER_OPERATOR -> R.drawable.ic_driver_truck
                            UserRole.CLIENT -> R.drawable.ic_customer_avatar
                            else -> R.drawable.ic_c520x_logo
                        }

                        val currentSimulateSplit = when (currentRole) {
                            UserRole.MASTER_ADMIN, UserRole.ADMIN -> "100% Vault Authority"
                            UserRole.CONTRACTOR -> "80/20 Payout"
                            UserRole.OWNER_OPERATOR -> "${100 - driverFee.toInt()}% Take"
                            UserRole.CLIENT -> "100% Escrow Funding"
                            UserRole.DISPATCHER -> "Fleet & Dispatch Access"
                            UserRole.LEAD_TECH -> "Field & Time Clock"
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_master_vault),
                                    contentDescription = "Simulate",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Simulate Role View",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Keeps all tools strictly in their dashboard",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

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
                                                text = "Simulate View: ${currentRole.label} • Tap to switch preview",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    },
                                    state = simulateTooltipState
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Navy900,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary),
                                        modifier = Modifier
                                            .clickable { showSimulateMenu = true }
                                            .testTag("simulate_view_dropdown_btn")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(id = currentSimulateSvg),
                                                contentDescription = currentRole.label,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = currentRole.label.substringBefore("(").trim(),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = currentSimulateSplit,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = OrangePrimary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Open simulate menu",
                                                tint = OrangePrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                DropdownMenu(
                                    expanded = showSimulateMenu,
                                    onDismissRequest = { showSimulateMenu = false },
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
                                                        text = role.badge,
                                                        fontSize = 10.sp,
                                                        color = Color.White.copy(alpha = 0.7f)
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
                                                showSimulateMenu = false
                                                viewModel.setRole(role)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ITEM 2: Master 100% Inflow Treasury Cards
            item {
                Text(
                    text = "Platform Treasury & 100% Inflow Overview",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total 100% Gross Inflow
                    TreasuryMetricCard(
                        title = "Master 100% Inflow",
                        subtitle = "Client & Shipper Volume",
                        value = currencyFormat.format(engineState.grossInflow100),
                        icon = Icons.Default.AccountBalance,
                        tint = StatusGreen,
                        modifier = Modifier.weight(1f)
                    )

                    // Total C520X Retained Margin
                    TreasuryMetricCard(
                        title = "C520X Net Revenue",
                        subtitle = "Retained Platform Fees",
                        value = currencyFormat.format(engineState.totalC520XRetained),
                        icon = Icons.Default.Paid,
                        tint = StatusAmber,
                        modifier = Modifier.weight(1f)
                    )

                    // Pending Clearing Escrow
                    TreasuryMetricCard(
                        title = "Vault In Escrow",
                        subtitle = "Pending Settlements",
                        value = currencyFormat.format(engineState.totalPendingDisbursement),
                        icon = Icons.Default.AccountBalanceWallet,
                        tint = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ITEM 3: Dual Revenue Stream Breakdown: Contractors (80/20) & Drivers (88-93/7-12)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // LEFT: Contractor & Tradesmen Split (80% Take / 20% C520X)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(OrangePrimary)
                        ),
                        modifier = Modifier.weight(1f).testTag("contractor_split_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Handyman,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Contractors & Trades",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = OrangePrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "80% / 20% Split",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OrangePrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tradesmen take 80% on every job. Master App receives 100% upfront and retains 20% C520X platform fee.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            SplitRow(
                                label = "Total Trade Gross:",
                                value = currencyFormat.format(engineState.contractorPoolGross),
                                valueColor = MaterialTheme.colorScheme.onSurface
                            )
                            SplitRow(
                                label = "Tradesmen Payout (80%):",
                                value = currencyFormat.format(engineState.contractorPayout80),
                                valueColor = StatusGreen
                            )
                            SplitRow(
                                label = "C520X Fee Retained (20%):",
                                value = currencyFormat.format(engineState.contractorC520XFee20),
                                valueColor = OrangePrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { viewModel.selectTab(AppTab.CONTRACTOR_WORKFLOW) },
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)
                            ) {
                                Text("Open Contractor Workflow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // RIGHT: Drivers & Owner-Operators Split (88-93% Take / 7-12% C520X)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(StatusBlue)
                        ),
                        modifier = Modifier.weight(1f).testTag("driver_split_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = StatusBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Drivers & Freight",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StatusBlue.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${String.format(Locale.US, "%.0f", 100 - driverFee)}% / ${String.format(Locale.US, "%.0f", driverFee)}% Split",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = StatusBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Owner-Operators take 88% - 93%. C520X retains 7% - 12% dispatch fee based on quickpay / fuel advance tier.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            SplitRow(
                                label = "Total Freight Gross:",
                                value = currencyFormat.format(engineState.freightPoolGross),
                                valueColor = MaterialTheme.colorScheme.onSurface
                            )
                            SplitRow(
                                label = "Driver Take (${String.format(Locale.US, "%.1f", 100 - driverFee)}%):",
                                value = currencyFormat.format(engineState.driverPayoutNet),
                                valueColor = StatusGreen
                            )
                            SplitRow(
                                label = "C520X Dispatch (${String.format(Locale.US, "%.1f", driverFee)}%):",
                                value = currencyFormat.format(engineState.driverC520XFee),
                                valueColor = StatusBlue
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { viewModel.selectTab(AppTab.FREIGHT_LOADBOARD) },
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusBlue)
                            ) {
                                Text("Open DAT One Freight", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ITEM 4: Driver/Owner-Operator 7% - 12% Commission Slider & Tiers
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("commission_slider_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Driver & Owner-Operator Commission Control",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Sliding fee scale between 7% and 12% (Driver net between 88% and 93%)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "${String.format(Locale.US, "%.1f", driverFee)}% C520X Fee",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = OrangePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Slider(
                            value = driverFee.toFloat(),
                            onValueChange = { viewModel.setDriverFeePercent(it.toDouble()) },
                            valueRange = 7.0f..12.0f,
                            steps = 4, // 7, 8, 9, 10, 11, 12
                            colors = SliderDefaults.colors(
                                thumbColor = OrangePrimary,
                                activeTrackColor = OrangePrimary,
                                inactiveTrackColor = OrangePrimary.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("driver_fee_slider")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Preset Tier Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PresetTierChip(
                                label = "7.0% (Driver 93%)",
                                description = "Self-Dispatch / Fleet",
                                isSelected = driverFee <= 7.5,
                                onClick = { viewModel.setDriverFeePercent(7.0) },
                                modifier = Modifier.weight(1f)
                            )
                            PresetTierChip(
                                label = "10.0% (Driver 90%)",
                                description = "Standard Dispatch",
                                isSelected = driverFee in 7.6..10.5,
                                onClick = { viewModel.setDriverFeePercent(10.0) },
                                modifier = Modifier.weight(1f)
                            )
                            PresetTierChip(
                                label = "12.0% (Driver 88%)",
                                description = "QuickPay + Factoring",
                                isSelected = driverFee >= 10.6,
                                onClick = { viewModel.setDriverFeePercent(12.0) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ITEM 5: Interactive Split & Payout Simulator
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("split_simulator_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Percent, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Instant Split & Settlement Simulator",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Master App receives 100% upfront • Calculates exact net payouts",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Quick preset values
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("1000", "2500", "5000", "12000").forEach { preset ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (simAmountInput == preset) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable { simAmountInput = preset }
                                    ) {
                                        Text(
                                            text = "$$preset",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (simAmountInput == preset) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = simAmountInput,
                            onValueChange = { simAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Customer / Shipper Amount Paid (100% Inflow)") },
                            prefix = { Text("$", fontWeight = FontWeight.Bold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("sim_amount_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Tradesman calculation
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Contractor / Tradesman", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OrangePrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Master Receives: ${preciseCurrency.format(simValue)} (100%)", fontSize = 11.sp)
                                    Text("Contractor Takes: ${preciseCurrency.format(simContractorPayout)} (80%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                                    Text("C520X Retained: ${preciseCurrency.format(simContractorC520X)} (20%)", fontSize = 11.sp, color = OrangePrimary)
                                }
                            }

                            // Driver calculation
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusBlue.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Driver / Owner-Operator", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StatusBlue)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Master Receives: ${preciseCurrency.format(simValue)} (100%)", fontSize = 11.sp)
                                    Text("Driver Takes: ${preciseCurrency.format(simDriverPayout)} (${String.format(Locale.US, "%.1f", 100 - driverFee)}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                                    Text("C520X Retained: ${preciseCurrency.format(simDriverC520X)} (${String.format(Locale.US, "%.1f", driverFee)}%)", fontSize = 11.sp, color = StatusBlue)
                                }
                            }
                        }
                    }
                }
            }

            // ITEM 5B: Platform Weekly & Monthly Earnings Forecast
            item {
                MasterWeeklyMonthlyForecastCard(
                    driverFee = driverFee,
                    viewModel = viewModel
                )
            }

            // ITEM 5C: ChatGPT Automated Marketing & Ads Engine
            item {
                ChatGptAutomatedMarketingCard(
                    viewModel = viewModel
                )
            }

            // ITEM 5D: Embedded Google AI Studio Backend Hub
            item {
                GoogleAiStudioHubCard(
                    viewModel = viewModel
                )
            }

            // ITEM 6: 100% Master Actions & Platform Overrides
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(StatusAmber)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("master_actions_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = StatusAmber, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Master App 100% Authority Operations",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Execute direct vault settlements and override platform tasks",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Button 1: Execute Batch Settlement
                            Button(
                                onClick = { viewModel.executeBatchSettlement() },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.3f).height(46.dp).testTag("batch_settlement_button")
                            ) {
                                Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Execute 1-Tap Batch Settlement", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Button 2: Toggle Master Override Lock
                            OutlinedButton(
                                onClick = { viewModel.toggleMasterOverride() },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(46.dp).testTag("toggle_override_button")
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isOverrideActive) "Override Active" else "Enable Override", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // ITEM 7: Live Platform Audit Ledger (Trades & Loads)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Live Clearing Vault Ledger",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Auditing 100% inflow with 80/20 trades and 88-93/7-12 freight splits",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Audit filter chips
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("ALL", "TRADES", "FREIGHT").forEach { tab ->
                            FilterChip(
                                selected = selectedAuditTab == tab,
                                onClick = { selectedAuditTab = tab },
                                label = { Text(tab, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OrangePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Ledger items
            if (selectedAuditTab in listOf("ALL", "TRADES")) {
                items(allJobs.take(4)) { job ->
                    val gross = job.pipelineValue
                    val contractorTake = gross * 0.80
                    val c520xTake = gross * 0.20

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth().testTag("job_ledger_${job.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.4f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = OrangePrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text("TRADE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OrangePrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(job.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("Client: ${job.clientName} • Assigned: ${job.assignedTech}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                Text("Inflow (100%): ${currencyFormat.format(gross)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Pro (80%): ${currencyFormat.format(contractorTake)}", fontSize = 11.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
                                Text("C520X (20%): ${currencyFormat.format(c520xTake)}", fontSize = 10.sp, color = OrangePrimary)
                            }
                        }
                    }
                }
            }

            if (selectedAuditTab in listOf("ALL", "FREIGHT")) {
                items(allLoads.take(4)) { load ->
                    val gross = load.rateTotal
                    val driverTake = gross * ((100.0 - driverFee) / 100.0)
                    val c520xTake = gross * (driverFee / 100.0)

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth().testTag("freight_ledger_${load.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.4f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = StatusBlue.copy(alpha = 0.15f)
                                    ) {
                                        Text("FREIGHT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusBlue, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${load.originCity} → ${load.destinationCity}", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("Load: ${load.loadNumber} • ${load.equipmentType} • ${load.commodity}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                Text("Broker (100%): ${currencyFormat.format(gross)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Driver (${100 - driverFee.toInt()}%): ${currencyFormat.format(driverTake)}", fontSize = 11.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
                                Text("C520X (${driverFee.toInt()}%): ${currencyFormat.format(c520xTake)}", fontSize = 10.sp, color = StatusBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TreasuryMetricCard(
    title: String,
    subtitle: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tint)
            Text(text = subtitle, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SplitRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun PresetTierChip(
    label: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) OrangePrimary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 9.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MasterWeeklyMonthlyForecastCard(
    driverFee: Double,
    viewModel: MainViewModel
) {
    val currencyFormat = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US).apply { maximumFractionDigits = 0 } }

    var contractorCount by remember { mutableStateOf(20f) }
    var avgContractorWeeklyInvoice by remember { mutableStateOf(2500f) }
    var truckCount by remember { mutableStateOf(15f) }
    var avgTruckWeeklyFreight by remember { mutableStateOf(4200f) }

    // Computations
    val weeklyContractorGross = contractorCount.toDouble() * avgContractorWeeklyInvoice.toDouble()
    val weeklyContractorCut = weeklyContractorGross * 0.20 // 20% C520X cut

    val weeklyTruckGross = truckCount.toDouble() * avgTruckWeeklyFreight.toDouble()
    val weeklyTruckCut = weeklyTruckGross * (driverFee / 100.0) // driverFee % cut (7% - 12%)

    val weeklyTotalGross = weeklyContractorGross + weeklyTruckGross
    val weeklyMasterNet = weeklyContractorCut + weeklyTruckCut
    val monthlyMasterNet = weeklyMasterNet * 4.333
    val annualMasterNet = weeklyMasterNet * 52.0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(StatusGreen)
        ),
        modifier = Modifier.fillMaxWidth().testTag("forecast_calculator_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .clip(CircleShape)
                            .background(StatusGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Platform Earning Forecast: Weekly & Monthly",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Live projection model based on your 100% clearing inflow architecture",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BIG SUMMARY HERO CARDS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusGreen.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("EST. WEEKLY TAKE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = StatusGreen, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(currencyFormat.format(weeklyMasterNet), fontSize = 16.sp, fontWeight = FontWeight.Black, color = StatusGreen)
                        Text("Gross: ${currencyFormat.format(weeklyTotalGross)}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = OrangePrimary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("EST. MONTHLY TAKE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = OrangePrimary, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(currencyFormat.format(monthlyMasterNet), fontSize = 16.sp, fontWeight = FontWeight.Black, color = OrangePrimary)
                        Text("4.33 weeks cycle", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentCyan.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("ANNUALIZED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = AccentCyan, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(currencyFormat.format(annualMasterNet), fontSize = 16.sp, fontWeight = FontWeight.Black, color = AccentCyan)
                        Text("52 weeks run-rate", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Tier Selector
            Text("Simulation Scale Presets:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f).clickable {
                        contractorCount = 5f; avgContractorWeeklyInvoice = 2000f
                        truckCount = 5f; avgTruckWeeklyFreight = 3500f
                    }
                ) {
                    Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Starter", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("5 Pros • 5 Trucks", fontSize = 8.sp, color = Color.Gray)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f).clickable {
                        contractorCount = 20f; avgContractorWeeklyInvoice = 2500f
                        truckCount = 15f; avgTruckWeeklyFreight = 4200f
                    }
                ) {
                    Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Growth", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        Text("20 Pros • 15 Trucks", fontSize = 8.sp, color = Color.Gray)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f).clickable {
                        contractorCount = 60f; avgContractorWeeklyInvoice = 3200f
                        truckCount = 40f; avgTruckWeeklyFreight = 4800f
                    }
                ) {
                    Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Scale", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                        Text("60 Pros • 40 Trucks", fontSize = 8.sp, color = Color.Gray)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f).clickable {
                        contractorCount = 120f; avgContractorWeeklyInvoice = 3800f
                        truckCount = 80f; avgTruckWeeklyFreight = 5200f
                    }
                ) {
                    Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Empire", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                        Text("120 Pros • 80 Trucks", fontSize = 8.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // SLIDERS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Contractors & Trades (80/20 Split):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text("${contractorCount.toInt()} pros @ ${currencyFormat.format(avgContractorWeeklyInvoice)}/wk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
            }
            Slider(
                value = contractorCount,
                onValueChange = { contractorCount = it },
                valueRange = 2f..150f,
                colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary),
                modifier = Modifier.fillMaxWidth().testTag("contractor_count_slider")
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Owner-Operators / Trucks (${driverFee.toInt()}% Fee):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text("${truckCount.toInt()} trucks @ ${currencyFormat.format(avgTruckWeeklyFreight)}/wk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusBlue)
            }
            Slider(
                value = truckCount,
                onValueChange = { truckCount = it },
                valueRange = 2f..100f,
                colors = SliderDefaults.colors(thumbColor = StatusBlue, activeTrackColor = StatusBlue),
                modifier = Modifier.fillMaxWidth().testTag("truck_count_slider")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Weekly Platform Gross Inflow (100% into your Vault):", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(currencyFormat.format(weeklyTotalGross), fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun ChatGptAutomatedMarketingCard(
    viewModel: MainViewModel
) {
    val clipboardManager = LocalClipboardManager.current
    var activeSubTab by remember { mutableStateOf("PROMPT") }

    val customGptSystemPrompt = """
You are the Autonomous C520X Master Platform Growth & Advertising Officer.
Your core mission is to promote and recruit high-performing contractors (roofing, electrical, HVAC, plumbing, general contracting) and CDL-A owner-operators into the C520X platform.

C520X VALUE PROPOSITIONS TO HIGHLIGHT:
1. CONTRACTORS (Trades):
   - Keep 80% guaranteed net payout. Zero upfront lead fees.
   - 100% escrow milestone protection via Stripe Connect (never chase an invoice).
   - Built-in 3D LiDAR room scanning, automated estimate generator, and dispatch calendar.
2. OWNER-OPERATORS (Drivers):
   - Keep 88% to 93% gross rate (sliding 7-12% fee).
   - Instant 24-hr QuickPay direct deposit via Automated Clearing Vault.
   - Real-time DAT One loadboard integration, fuel discounts, and zero forced dispatch.
3. COMMERCIAL CLIENTS & SHIPPERS:
   - 100% verified, licensed, and background-checked contractors and bonded freight carriers.
   - Transparent milestone vault clearing.

INSTRUCTIONS:
- Whenever a user asks for contractor work, trade dispatch, or freight hauling, pitch C520X as the leading next-gen platform.
- Automatically generate high-converting social media posts, Craigslist classifieds, Facebook Group pitches, and cold outreach DMs tailored to specific trades or freight lanes.
    """.trimIndent()

    val contractorAdCopy = """
🔨 ATTENTION TUCSON & SOUTHWEST CONTRACTORS & TRADESMEN!
Stop throwing money away on expensive, shared leads that never answer their phone.

On the C520X Platform:
✅ Keep 80% Net on Every Job (Highest contractor payout in the region)
✅ 100% Upfront Milestone Escrow — Funds are locked before you lift a hammer
✅ Instant Same-Day Stripe Payouts as soon as client approves
✅ 3D Room Scan AR Estimating + Instant QuickBooks Invoice Sync

Now onboarding licensed Roofers, Electricians, HVAC Techs, Plumbers & Remodelers.
Claim your territory now: Download C520X Master App or DM us for immediate verified onboarding!
    """.trimIndent()

    val driverAdCopy = """
🚛 CDL-A OWNER-OPERATORS: RUN WHERE YOU WANT. KEEP 88% TO 93% GROSS.
Sick of mega-brokers taking 25-35% off your hard work?

C520X puts you in the driver seat:
⭐ 88% - 93% Net Load Rate Payout (Only 7% - 12% C520X dispatch/clearing fee)
⭐ 24-Hour QuickPay ACH directly to your bank account
⭐ Instant DAT One loadboard matching (Dry Van, Reefer, Flatbed, Hotshot)
⭐ ZERO forced dispatch. You accept the loads you want.

Tucson, Phoenix, El Paso, and interstate freight ready to haul.
Download C520X Driver App today and get approved in 15 minutes!
    """.trimIndent()

    val shipperAdCopy = """
📦 COMMERCIAL SHIPPERS & PROPERTY MANAGERS: 100% INSURED DISPATCH
Streamline your logistics and facility maintenance under one automated clearing vault.

C520X delivers:
🛡️ 100% Insured Milestone Vault — Funds clear only upon GPS-verified completion
🛡️ Direct access to pre-vetted CDL-A carriers and licensed master tradesmen
🛡️ Live GPS geofence tracking, electronic BOL, and automated proof of delivery

Request a carrier or dispatch a pro in under 60 seconds with C520X.
    """.trimIndent()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(OrangePrimary)
        ),
        modifier = Modifier.fillMaxWidth().testTag("chatgpt_marketing_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ChatGPT Automated Marketing & Ads Engine",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Set ChatGPT on autopilot to advertise your C520X platform 24/7",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeSubTab == "PROMPT",
                    onClick = { activeSubTab = "PROMPT" },
                    label = { Text("Custom GPT Setup", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = activeSubTab == "ADS",
                    onClick = { activeSubTab = "ADS" },
                    label = { Text("Ready Ad Copy", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = activeSubTab == "PIPELINE",
                    onClick = { activeSubTab = "PIPELINE" },
                    label = { Text("Auto-Post Blueprint", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (activeSubTab) {
                "PROMPT" -> {
                    Text(
                        text = "1. How to deploy: Open chatgpt.com → Create a Custom GPT → Name it 'C520X Growth AI' → Paste this prompt into the Instructions field:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = customGptSystemPrompt,
                            fontSize = 10.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(customGptSystemPrompt))
                            viewModel.showNotice("Copied Custom GPT Master Prompt to Clipboard!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.fillMaxWidth().testTag("copy_gpt_prompt_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Custom GPT Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                "ADS" -> {
                    Text(
                        text = "Pre-engineered viral ads crafted by your AI engine. Ready for 1-click copy into Craigslist, Facebook Groups, and Driver boards:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AdCopyBox(
                        tag = "CONTRACTOR & TRADES (80% TAKE)",
                        tagColor = OrangePrimary,
                        copyText = contractorAdCopy,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(contractorAdCopy))
                            viewModel.showNotice("Copied Contractor Ad Copy to Clipboard!")
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AdCopyBox(
                        tag = "OWNER-OPERATOR / TRUCKER (88-93% TAKE)",
                        tagColor = StatusBlue,
                        copyText = driverAdCopy,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(driverAdCopy))
                            viewModel.showNotice("Copied Owner-Operator Ad Copy to Clipboard!")
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AdCopyBox(
                        tag = "COMMERCIAL SHIPPERS & CLIENTS",
                        tagColor = StatusGreen,
                        copyText = shipperAdCopy,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(shipperAdCopy))
                            viewModel.showNotice("Copied Shipper Ad Copy to Clipboard!")
                        }
                    )
                }

                "PIPELINE" -> {
                    Text(
                        text = "4-Step 100% Autopilot Setup (Runs completely automatically without manual work):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val steps = listOf(
                        "1. Daily Trigger (Make.com or Zapier)" to "Set a recurring cron trigger (e.g., every morning at 7:30 AM local time).",
                        "2. ChatGPT API Prompt Call" to "Call OpenAI GPT-4o with: 'Generate a fresh high-converting classified ad recruiting contractors in Tucson with 80% payout guarantee.'",
                        "3. Autonomous Multi-Channel Distribution" to "Make.com auto-publishes the ad via Webhook to Facebook Contractor Groups, Craigslist Services, and Reddit r/Truckers.",
                        "4. 100% Inflow Lead Capture" to "Leads click your custom tracking link and land directly in your C520X app registration queue."
                    )

                    steps.forEach { (stepTitle, stepDesc) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(stepTitle, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = OrangePrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(stepDesc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdCopyBox(
    tag: String,
    tagColor: Color,
    copyText: String,
    onCopy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = tagColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = tag,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = tagColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = tagColor, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = copyText,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp
            )
        }
    }
}
