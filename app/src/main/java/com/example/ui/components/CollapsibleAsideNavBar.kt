package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.UserRole
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen

@Composable
fun CollapsibleAsideNavBar(
    viewModel: MainViewModel,
    currentTab: AppTab,
    availableTabs: List<AppTab>,
    onSelectTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpanded by viewModel.isAsideNavExpanded.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    var showRoleMenu by remember { mutableStateOf(false) }

    val navWidth by animateDpAsState(
        targetValue = if (isExpanded) 205.dp else 54.dp,
        label = "asideNavWidthAnimation"
    )

    // Role-specific SVG avatar drawable
    val roleAvatarRes = when (currentRole) {
        UserRole.MASTER_ADMIN, UserRole.ADMIN -> R.drawable.ic_master_vault
        UserRole.CONTRACTOR -> R.drawable.ic_contractor_helmet
        UserRole.OWNER_OPERATOR -> R.drawable.ic_driver_truck
        UserRole.CLIENT -> R.drawable.ic_customer_avatar
        else -> R.drawable.ic_c520x_logo
    }

    val roleShortLabel = when (currentRole) {
        UserRole.MASTER_ADMIN, UserRole.ADMIN -> "Master 100%"
        UserRole.CONTRACTOR -> "Contractor Pro"
        UserRole.OWNER_OPERATOR -> "Driver / DAT"
        UserRole.CLIENT -> "Customer Hub"
        UserRole.DISPATCHER -> "Dispatcher"
        UserRole.LEAD_TECH -> "Lead Tech"
    }

    val roleSplitBadge = when (currentRole) {
        UserRole.MASTER_ADMIN, UserRole.ADMIN -> "100% Vault"
        UserRole.CONTRACTOR -> "80/20 Take"
        UserRole.OWNER_OPERATOR -> "88-93% Take"
        UserRole.CLIENT -> "Escrow"
        else -> "Active"
    }

    Surface(
        modifier = modifier
            .width(navWidth)
            .fillMaxHeight()
            .testTag("collapsible_aside_nav"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 8.dp, horizontal = if (isExpanded) 10.dp else 4.dp)
        ) {
            // Header: Expand / Collapse Toggle + Role Identity
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = if (isExpanded) Arrangement.SpaceBetween else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isExpanded) {
                    // Role Profile chip / dropdown toggle
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .clickable { showRoleMenu = true }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("aside_role_selector_btn")
                        ) {
                            Image(
                                painter = painterResource(id = roleAvatarRes),
                                contentDescription = "Active Role Avatar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = roleShortLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = roleSplitBadge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OrangePrimary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch Role",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Role Switcher Dropdown
                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            UserRole.values().forEach { role ->
                                val isCurrent = role == currentRole
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val svgIcon = when (role) {
                                                UserRole.MASTER_ADMIN, UserRole.ADMIN -> R.drawable.ic_master_vault
                                                UserRole.CONTRACTOR -> R.drawable.ic_contractor_helmet
                                                UserRole.OWNER_OPERATOR -> R.drawable.ic_driver_truck
                                                UserRole.CLIENT -> R.drawable.ic_customer_avatar
                                                else -> R.drawable.ic_c520x_logo
                                            }
                                            Image(
                                                painter = painterResource(id = svgIcon),
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = role.label,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(
                                                    text = role.badge,
                                                    fontSize = 10.sp,
                                                    color = OrangePrimary
                                                )
                                            }
                                            if (isCurrent) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = StatusGreen,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
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

                    // Collapse Chevron Button
                    IconButton(
                        onClick = { viewModel.toggleAsideNav() },
                        modifier = Modifier.size(32.dp).testTag("aside_collapse_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Collapse Aside Nav",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    // Collapsed mode: Expand Chevron & Role Mini Avatar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleAsideNav() },
                            modifier = Modifier.size(32.dp).testTag("aside_expand_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Expand Aside Nav",
                                tint = OrangePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary.copy(alpha = 0.12f))
                                .border(1.dp, OrangePrimary.copy(alpha = 0.4f), CircleShape)
                                .clickable { viewModel.toggleAsideNav() },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = roleAvatarRes),
                                contentDescription = "Role Avatar",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Dynamic Navigation Destinations Filtered By User Role
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                availableTabs.forEach { tab ->
                    val isSelected = tab == currentTab
                    val tabIcon = getTabIcon(tab)
                    val specialSvg = getTabSpecialSvg(tab)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) OrangePrimary else Color.Transparent,
                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTab(tab) }
                            .testTag("aside_nav_${tab.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = if (isExpanded) 10.dp else 6.dp,
                                    vertical = 8.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
                        ) {
                            if (specialSvg != null) {
                                Image(
                                    painter = painterResource(id = specialSvg),
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = tabIcon,
                                    contentDescription = tab.label,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (isExpanded) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = tab.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Footer: WebSocket Status / Collapse state indicator
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(StatusGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Role Synced",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusGreen
                        )
                    }
                    Text(
                        text = "C520X Aside Nav",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(StatusGreen)
                    )
                }
            }
        }
    }
}

private fun getTabSpecialSvg(tab: AppTab): Int? {
    return when (tab) {
        AppTab.FREIGHT_LOADBOARD -> R.drawable.ic_driver_truck
        AppTab.CUSTOMER_HUB -> R.drawable.ic_customer_avatar
        AppTab.MASTER_VAULT -> R.drawable.ic_master_vault
        AppTab.CONTRACTOR_WORKFLOW -> R.drawable.ic_contractor_helmet
        else -> null
    }
}

private fun getTabIcon(tab: AppTab): ImageVector {
    return when (tab) {
        AppTab.MASTER_VAULT -> Icons.Default.Shield
        AppTab.DASHBOARD -> Icons.Default.BarChart
        AppTab.USER_PORTAL -> Icons.Default.Person
        AppTab.FREIGHT_LOADBOARD -> Icons.Default.LocalShipping
        AppTab.LIVE_MARKETBOARD -> Icons.Default.Storefront
        AppTab.CONTRACTOR_WORKFLOW -> Icons.Default.Handyman
        AppTab.CUSTOMER_HUB -> Icons.Default.People
        AppTab.PAYMENTS -> Icons.Default.Payment
        AppTab.KANBAN_BOARD -> Icons.Default.ViewKanban
        AppTab.AI_ANALYTICS -> Icons.Default.AutoAwesome
        AppTab.DISPATCH_SCHEDULE -> Icons.Default.AccessTime
        AppTab.TIME_TRACKING -> Icons.Default.AccessTime
        AppTab.LEADS_QUOTES -> Icons.Default.People
        AppTab.CHAT_SLACK -> Icons.AutoMirrored.Filled.Chat
        AppTab.ROOM_SCAN_3D -> Icons.Default.ViewInAr
        AppTab.REVIEWS -> Icons.Default.Star
        AppTab.ESTIMATOR -> Icons.Default.Calculate
        AppTab.PARTNERS -> Icons.Default.Hub
        AppTab.WEBHOOKS -> Icons.Default.Webhook
        AppTab.SECURITY_SETTINGS -> Icons.Default.Security
    }
}
