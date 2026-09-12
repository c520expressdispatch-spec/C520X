package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.AppTabNavigation
import com.example.ui.components.BiometricLockDialog
import com.example.ui.components.CollapsibleAsideNavBar
import com.example.ui.components.DraggableAiOverlay
import com.example.ui.components.MasterAuthPinDialog
import com.example.ui.components.NotificationSheetDialog
import com.example.ui.components.SmartCustomTemplateSheetDialog
import com.example.ui.screens.AiAnalyticsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ContractorWorkflowScreen
import com.example.ui.screens.CustomerPortalScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EstimatorScreen
import com.example.ui.screens.FreightLoadboardScreen
import com.example.ui.screens.JobsScheduleScreen
import com.example.ui.screens.KanbanBoardScreen
import com.example.ui.screens.LeadsQuotesScreen
import com.example.ui.screens.LiveMarketboardScreen
import com.example.ui.screens.MasterAdminHubScreen
import com.example.ui.screens.PartnersScreen
import com.example.ui.screens.PaymentsScreen
import com.example.ui.screens.ReviewsScreen
import com.example.ui.screens.RoomScan3dScreen
import com.example.ui.screens.SettingsSecurityScreen
import com.example.ui.screens.TimeTrackingScreen
import com.example.ui.screens.UserPortalScreen
import com.example.ui.screens.WebhooksScreen
import com.example.ui.viewmodel.ProTradeViewModel
import com.example.ui.theme.C520XTheme
import com.example.ui.theme.OrangePrimary

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            com.google.firebase.FirebaseApp.initializeApp(this)
        } catch (_: Exception) {
        }
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            C520XTheme(darkTheme = isDark) {
                C520XApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val target = intent?.getStringExtra("TARGET_TAB")
        if (target == "LEADS") {
            viewModel.selectTab(AppTab.LEADS_QUOTES)
        }
    }
}

@Composable
fun C520XApp(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Request Android 13+ Notification Permission for Real-Time Customer Leads & Emergency Pushes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                viewModel.showNotice("Emergency push notifications active!")
            }
        }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val currentTab by viewModel.currentTab.collectAsState()
    val availableTabs by viewModel.availableTabs.collectAsState()
    val isBiometricLocked by viewModel.isBiometricLocked.collectAsState()
    val showMasterAuthDialog by viewModel.showMasterAuthDialog.collectAsState()
    val masterAuthError by viewModel.masterAuthError.collectAsState()
    val toastNotice by viewModel.toastNotice.collectAsState()
    val pushAlerts by viewModel.pushAlerts.collectAsState()
    val isAiOverlayVisible by viewModel.isAiOverlayVisible.collectAsState()
    val showCustomTemplateDialog by viewModel.showCustomTemplateDialog.collectAsState()
    val customDocumentTemplate by viewModel.customDocumentTemplate.collectAsState()
    val customTemplateDocMode by viewModel.customTemplateDocMode.collectAsState()

    var showNotificationsSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag("c520x_scaffold"),
            topBar = {
                AppHeader(
                    viewModel = viewModel,
                    onOpenNotifications = { showNotificationsSheet = true }
                )
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Aside Collapsible Navigation Bar Across All User Dashboards
                CollapsibleAsideNavBar(
                    viewModel = viewModel,
                    currentTab = currentTab,
                    availableTabs = availableTabs,
                    onSelectTab = { viewModel.selectTab(it) }
                )

                MainContentArea(
                    viewModel = viewModel,
                    currentTab = currentTab,
                    toastNotice = toastNotice,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        if (isAiOverlayVisible) {
            DraggableAiOverlay(
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            )
        }
    }

    // Biometric security modal
    if (isBiometricLocked) {
        BiometricLockDialog(
            onUnlock = { viewModel.unlockBiometric() }
        )
    }

    // Notifications Dialog
    if (showNotificationsSheet) {
        NotificationSheetDialog(
            viewModel = viewModel,
            onDismiss = { showNotificationsSheet = false }
        )
    }

    // Master Admin PIN authentication modal
    if (showMasterAuthDialog) {
        MasterAuthPinDialog(
            errorMessage = masterAuthError,
            onUnlock = { pin -> viewModel.authenticateMasterAdmin(pin) },
            onDismiss = { viewModel.dismissMasterAuthDialog() }
        )
    }

    // Smart Custom Invoice & Estimate Template Sheets
    if (showCustomTemplateDialog) {
        SmartCustomTemplateSheetDialog(
            initialTemplate = customDocumentTemplate,
            initialDocMode = customTemplateDocMode,
            onSaveTemplate = { updated ->
                viewModel.updateCustomDocumentTemplate(updated)
            },
            onDismiss = { viewModel.dismissCustomTemplateDialog() },
            onShowNotice = { viewModel.showNotice(it) }
        )
    }
}

@Composable
private fun MainContentArea(
    viewModel: MainViewModel,
    currentTab: AppTab,
    toastNotice: String?,
    modifier: Modifier = Modifier
) {
    val proTradeViewModel: ProTradeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    Box(modifier = modifier) {
        // Main Screen content switcher
        when (currentTab) {
            AppTab.MASTER_VAULT -> MasterAdminHubScreen(viewModel = viewModel)
            AppTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
            AppTab.USER_PORTAL -> UserPortalScreen(viewModel = viewModel)
            AppTab.LIVE_MARKETBOARD -> LiveMarketboardScreen(viewModel = viewModel)
            AppTab.CONTRACTOR_WORKFLOW -> ContractorWorkflowScreen(viewModel = viewModel)
            AppTab.CUSTOMER_HUB -> CustomerPortalScreen(viewModel = viewModel)
            AppTab.FREIGHT_LOADBOARD -> FreightLoadboardScreen(viewModel = viewModel)
            AppTab.KANBAN_BOARD -> KanbanBoardScreen(viewModel = viewModel)
            AppTab.AI_ANALYTICS -> AiAnalyticsScreen(viewModel = viewModel)
            AppTab.DISPATCH_SCHEDULE -> JobsScheduleScreen(viewModel = viewModel)
            AppTab.TIME_TRACKING -> TimeTrackingScreen(viewModel = viewModel)
            AppTab.LEADS_QUOTES -> LeadsQuotesScreen(viewModel = viewModel)
            AppTab.CHAT_SLACK -> ChatScreen(viewModel = viewModel)
            AppTab.ROOM_SCAN_3D -> RoomScan3dScreen(viewModel = viewModel)
            AppTab.PAYMENTS -> PaymentsScreen(viewModel = viewModel)
            AppTab.REVIEWS -> ReviewsScreen(viewModel = viewModel)
            AppTab.ESTIMATOR -> EstimatorScreen(viewModel = proTradeViewModel)
            AppTab.PARTNERS -> PartnersScreen(viewModel = proTradeViewModel)
            AppTab.WEBHOOKS -> WebhooksScreen(viewModel = proTradeViewModel)
            AppTab.SECURITY_SETTINGS -> SettingsSecurityScreen(viewModel = viewModel)
        }

        // Real-time Action Banner / Toast Notice
        AnimatedVisibility(
            visible = toastNotice != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            toastNotice?.let { notice ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.clearNotice() }
                        .testTag("toast_notice_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = notice,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearNotice() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.inverseOnSurface,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

