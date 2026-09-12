package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.CanyonNavyBorder
import com.example.ui.theme.CanyonNavyCard
import com.example.ui.theme.CanyonNavyDark
import com.example.ui.theme.CanyonOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.ProTradeViewModel

@Composable
fun ProTradeMainScreen(
    viewModel: ProTradeViewModel = viewModel(),
    onNavigateBackToMasterHub: (() -> Unit)? = null
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val leads by viewModel.leads.collectAsState()
    val incomingWebhooks by viewModel.incomingWebhooks.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val pendingLeadsCount = leads.count { it.status.equals("New", ignoreCase = true) }
    val pendingWebhooksCount = incomingWebhooks.count { it.status == com.example.data.models.IncomingWebhookStatus.PENDING }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("pro_trade_main_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = CanyonNavyCard,
                contentColor = CanyonOrange
            ) {
                AppNavTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    val badgeCount = when (tab) {
                        AppNavTab.LEADS -> pendingLeadsCount
                        AppNavTab.INTEGRATIONS -> pendingWebhooksCount
                        else -> 0
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (badgeCount > 0) {
                                        Badge(containerColor = CanyonOrange, contentColor = TextWhite) {
                                            Text("$badgeCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                val icon = when (tab) {
                                    AppNavTab.DASHBOARD -> Icons.Default.Dashboard
                                    AppNavTab.ESTIMATOR -> Icons.Default.Calculate
                                    AppNavTab.LEADS -> Icons.Default.Inbox
                                    AppNavTab.INTEGRATIONS -> Icons.Default.Webhook
                                    AppNavTab.AI_ASSISTANT -> Icons.Default.AutoAwesome
                                }
                                Icon(icon, contentDescription = tab.label, modifier = Modifier.size(20.dp))
                            }
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CanyonOrange,
                            selectedTextColor = CanyonOrange,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CanyonNavyBorder
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CanyonNavyDark)
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppNavTab.ESTIMATOR -> EstimatorScreen(viewModel = viewModel)
                AppNavTab.LEADS -> LeadsScreen(viewModel = viewModel)
                AppNavTab.INTEGRATIONS -> WebhooksScreen(viewModel = viewModel)
                AppNavTab.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
            }
        }
    }
}
