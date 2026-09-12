package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomerBookingEntity
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LiveMarketboardScreen(viewModel: MainViewModel) {
    val bookings by viewModel.customerBookings.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    var selectedSector by remember { mutableStateOf("ALL") } // "ALL", "RESIDENTIAL", "COMMERCIAL"
    var selectedTrade by remember { mutableStateOf("All Trades") }
    var selectedUrgency by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog state for submitting contractor quote
    var showQuoteDialogForBooking by remember { mutableStateOf<CustomerBookingEntity?>(null) }
    var quoteAmountInput by remember { mutableStateOf("") }
    var quoteAvailabilityInput by remember { mutableStateOf("Today 2:00 PM") }
    var quoteHoursInput by remember { mutableStateOf("3.0") }
    var quotePitchInput by remember { mutableStateOf("") }

    // Claim confirmation dialog
    var claimedJobConfirmation by remember { mutableStateOf<CustomerBookingEntity?>(null) }

    // Filter only open marketplace requests
    val openLeads = remember(bookings, selectedSector, selectedTrade, selectedUrgency, searchQuery) {
        bookings.filter { booking ->
            val isOpen = booking.status == "Open For Bids"
            val matchesSector = when (selectedSector) {
                "RESIDENTIAL" -> booking.serviceType.equals("Residential", ignoreCase = true)
                "COMMERCIAL" -> booking.serviceType.equals("Commercial", ignoreCase = true)
                else -> true
            }
            val matchesTrade = if (selectedTrade == "All Trades") true else booking.category.equals(selectedTrade, ignoreCase = true)
            val matchesUrgency = if (selectedUrgency == "All") true else booking.urgency.contains(selectedUrgency, ignoreCase = true)
            val matchesQuery = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().lowercase()
                booking.projectTitle.lowercase().contains(q) ||
                        booking.customerName.lowercase().contains(q) ||
                        booking.category.lowercase().contains(q) ||
                        booking.address.lowercase().contains(q) ||
                        booking.description.lowercase().contains(q)
            }
            isOpen && matchesSector && matchesTrade && matchesUrgency && matchesQuery
        }
    }

    val totalOpenCount = bookings.count { it.status == "Open For Bids" }
    val residentialCount = bookings.count { it.status == "Open For Bids" && it.serviceType.equals("Residential", ignoreCase = true) }
    val commercialCount = bookings.count { it.status == "Open For Bids" && it.serviceType.equals("Commercial", ignoreCase = true) }
    val totalMarketValue = bookings.filter { it.status == "Open For Bids" }.sumOf { it.budget }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("live_marketboard_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 860.dp)
        ) {
            // Header Banner
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Live Marketboard",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Real-time Customer & Commercial Service Requests",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Switch to Workflow shortcut
                        Button(
                            onClick = { viewModel.selectTab(AppTab.CONTRACTOR_WORKFLOW) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("goto_workflow_button")
                        ) {
                            Icon(Icons.Default.Handyman, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("My Workflow", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Stats Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MarketStatPill(
                            label = "Open Requests",
                            value = "$totalOpenCount Active",
                            icon = Icons.Default.Work,
                            color = OrangePrimary,
                            modifier = Modifier.weight(1f)
                        )
                        MarketStatPill(
                            label = "Market Value",
                            value = currencyFormat.format(totalMarketValue),
                            icon = Icons.Default.TrendingUp,
                            color = StatusGreen,
                            modifier = Modifier.weight(1f)
                        )
                        MarketStatPill(
                            label = "House / Comm",
                            value = "$residentialCount 🏡 / $commercialCount 🏢",
                            icon = Icons.Default.Apartment,
                            color = StatusBlue,
                            modifier = Modifier.weight(1.1f)
                        )
                    }
                }
            }

            // Search Bar & Primary Sector Switcher
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search house or commercial requests, trade, location...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangePrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("marketboard_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Sector Segmented Toggle: All, House / Home Services, Commercial Services
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SectorToggleTab(
                        title = "All ($totalOpenCount)",
                        icon = Icons.Default.FilterList,
                        isSelected = selectedSector == "ALL",
                        onClick = { selectedSector = "ALL" },
                        modifier = Modifier.weight(1f)
                    )
                    SectorToggleTab(
                        title = "🏡 House ($residentialCount)",
                        icon = Icons.Default.Home,
                        isSelected = selectedSector == "RESIDENTIAL",
                        onClick = { selectedSector = "RESIDENTIAL" },
                        modifier = Modifier.weight(1f)
                    )
                    SectorToggleTab(
                        title = "🏢 Commercial ($commercialCount)",
                        icon = Icons.Default.Business,
                        isSelected = selectedSector == "COMMERCIAL",
                        onClick = { selectedSector = "COMMERCIAL" },
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Trade Category Filter Chips (Horizontal Scrollable)
                val trades = listOf(
                    "All Trades", "Plumbing", "Electrical", "HVAC", "Remodeling",
                    "Carpentry", "Roofing", "Painting", "Refrigeration",
                    "Facility Maintenance", "Moving & Freight"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    trades.forEach { trade ->
                        val isSelected = selectedTrade == trade
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTrade = trade },
                            label = { Text(trade, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimary.copy(alpha = 0.15f),
                                selectedLabelColor = OrangePrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
            }

            // Market Listings Content
            if (openLeads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No open service requests match filters",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Try clearing search queries or switching sector tabs.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                selectedSector = "ALL"
                                selectedTrade = "All Trades"
                                selectedUrgency = "All"
                                searchQuery = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Reset All Filters", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("market_leads_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(openLeads, key = { it.id }) { booking ->
                        MarketLeadCard(
                            booking = booking,
                            currencyFormat = currencyFormat,
                            onInstantClaim = {
                                viewModel.claimInstantJob(
                                    bookingId = booking.id,
                                    proName = "Alex Ramirez (Lead Contractor)",
                                    quoteAmount = booking.budget
                                )
                                claimedJobConfirmation = booking
                            },
                            onOpenQuoteDialog = {
                                showQuoteDialogForBooking = booking
                                quoteAmountInput = booking.budget.toInt().toString()
                                quoteAvailabilityInput = "Today 2:00 PM"
                                quoteHoursInput = "4.0"
                                quotePitchInput = "Licensed ROC contractor. Commercial & residential certified with full C520X guarantee."
                            }
                        )
                    }

                    // Bottom padding for scroll clearance
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Modal Dialog: Send Custom Contractor Quote
    showQuoteDialogForBooking?.let { booking ->
        AlertDialog(
            onDismissRequest = { showQuoteDialogForBooking = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Quote & Proposal", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Project: ${booking.projectTitle}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Client: ${booking.customerName} • Budget Target: ${currencyFormat.format(booking.budget)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = quoteAmountInput,
                        onValueChange = { quoteAmountInput = it },
                        label = { Text("Your Proposed Price ($)") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("quote_amount_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quoteAvailabilityInput,
                            onValueChange = { quoteAvailabilityInput = it },
                            label = { Text("Earliest Arrival") },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = quoteHoursInput,
                            onValueChange = { quoteHoursInput = it },
                            label = { Text("Est. Hours") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    OutlinedTextField(
                        value = quotePitchInput,
                        onValueChange = { quotePitchInput = it },
                        label = { Text("Contractor Proposal Pitch & Scope") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("quote_pitch_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = quoteAmountInput.toDoubleOrNull() ?: booking.budget
                        val hours = quoteHoursInput.toDoubleOrNull() ?: 3.0
                        viewModel.submitContractorBid(
                            bookingId = booking.id,
                            proName = "Alex Ramirez (Lead Contractor)",
                            proCompany = "VoltCraft & Apex Pro Contracting",
                            proRating = 4.95f,
                            reviewCount = 112,
                            badge = "Thumbtack Top Pro • Yelp 5★",
                            bidAmount = amount,
                            estimatedHours = hours,
                            earliestAvailability = quoteAvailabilityInput,
                            proposalNote = quotePitchInput
                        )
                        showQuoteDialogForBooking = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.testTag("confirm_submit_quote_button")
                ) {
                    Text("Send Quote to Client")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuoteDialogForBooking = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal Confirmation: Claimed Job
    claimedJobConfirmation?.let { booking ->
        AlertDialog(
            onDismissRequest = { claimedJobConfirmation = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Job Claimed Successfully!", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You have claimed '${booking.projectTitle}' for ${currencyFormat.format(booking.budget)}.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sequential project milestones have been auto-generated for this job. You can now execute site assessments, track progress draws, and mark milestones complete.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        claimedJobConfirmation = null
                        viewModel.selectTab(AppTab.CONTRACTOR_WORKFLOW)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.testTag("open_workflow_after_claim_button")
                ) {
                    Text("Go to Contractor Workflow")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            },
            dismissButton = {
                TextButton(onClick = { claimedJobConfirmation = null }) {
                    Text("Stay on Marketboard")
                }
            }
        )
    }
}

@Composable
private fun MarketLeadCard(
    booking: CustomerBookingEntity,
    currencyFormat: NumberFormat,
    onInstantClaim: () -> Unit,
    onOpenQuoteDialog: () -> Unit
) {
    val isCommercial = booking.serviceType.equals("Commercial", ignoreCase = true)
    val tradeIcon = getTradeIcon(booking.category)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isCommercial) StatusAmber.copy(alpha = 0.35f) else OrangePrimary.copy(alpha = 0.25f)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lead_card_${booking.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Badge Row: Sector Badge + Urgency + Category Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Sector badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCommercial) StatusAmber.copy(alpha = 0.15f) else StatusBlue.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = if (isCommercial) Icons.Default.Business else Icons.Default.Home,
                                contentDescription = null,
                                tint = if (isCommercial) StatusAmber else StatusBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCommercial) "COMMERCIAL" else "HOUSE / HOME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCommercial) StatusAmber else StatusBlue
                            )
                        }
                    }

                    // Urgency Badge
                    val urgencyColor = when {
                        booking.urgency.contains("Emergency", ignoreCase = true) -> StatusRed
                        booking.urgency.contains("24", ignoreCase = true) || booking.urgency.contains("48", ignoreCase = true) -> OrangePrimary
                        else -> StatusGreen
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = urgencyColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = urgencyColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = booking.urgency,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = urgencyColor
                            )
                        }
                    }
                }

                // Category Trade Indicator
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Icon(tradeIcon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(booking.category, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Project Title & Budget Headline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = booking.projectTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(booking.budget),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = StatusGreen
                    )
                    Text(
                        text = "Target Budget",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Scope Description
            Text(
                text = booking.description,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Meta Info: Customer, Location, Preferred timing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = booking.address,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "Client: ${booking.customerName}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Instant Claim ($X) & Submit Bid
            // Sized cleanly with min 48dp touch target and no button overlap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary action: Send Custom Quote
                OutlinedButton(
                    onClick = onOpenQuoteDialog,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(OrangePrimary)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("send_quote_button_${booking.id}")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send Quote", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Primary action: Instant Claim Job
                Button(
                    onClick = onInstantClaim,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("claim_job_button_${booking.id}")
                ) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Instant Claim (${currencyFormat.format(booking.budget)})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MarketStatPill(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.08f),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(color.copy(alpha = 0.2f))
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SectorToggleTab(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isSelected) OrangePrimary else MaterialTheme.colorScheme.outlineVariant
            )
        ),
        modifier = modifier
            .height(38.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

private fun getTradeIcon(category: String): ImageVector {
    return when {
        category.contains("Plumbing", ignoreCase = true) -> Icons.Default.Plumbing
        category.contains("Electric", ignoreCase = true) -> Icons.Default.ElectricBolt
        category.contains("HVAC", ignoreCase = true) || category.contains("Heat", ignoreCase = true) -> Icons.Default.AcUnit
        category.contains("Refrig", ignoreCase = true) -> Icons.Default.AcUnit
        category.contains("Carpentry", ignoreCase = true) || category.contains("Remodel", ignoreCase = true) -> Icons.Default.Carpenter
        category.contains("Roof", ignoreCase = true) -> Icons.Default.Roofing
        category.contains("Paint", ignoreCase = true) -> Icons.Default.FormatPaint
        category.contains("Freight", ignoreCase = true) || category.contains("Moving", ignoreCase = true) -> Icons.Default.LocalShipping
        category.contains("Facility", ignoreCase = true) -> Icons.Default.Business
        else -> Icons.Default.Handyman
    }
}
