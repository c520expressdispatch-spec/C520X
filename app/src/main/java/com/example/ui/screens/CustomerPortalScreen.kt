package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomerBookingEntity
import com.example.data.MarketplaceBidEntity
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CustomerPortalScreen(viewModel: MainViewModel) {
    val bookings by viewModel.customerBookings.collectAsState()
    val allBids by viewModel.marketplaceBids.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showRequestDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "All", "Plumbing", "Electrical", "HVAC", "Remodeling",
        "Roofing", "Handyman", "Carpentry", "Painting", "Moving & Freight"
    )

    val filteredBookings = bookings.filter { booking ->
        (selectedCategoryFilter == "All" || booking.category.equals(selectedCategoryFilter, ignoreCase = true)) &&
        when (selectedStatusFilter) {
            "All" -> true
            "Open" -> booking.status == "Open For Bids"
            "Active" -> booking.status in listOf("Booked", "En Route", "In Progress")
            "Completed" -> booking.status == "Completed"
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showRequestDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Request a Pro / Book Now", fontWeight = FontWeight.Bold) },
                containerColor = OrangePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("request_pro_fab")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Platform Header & Value Proposition Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth().testTag("customer_hub_banner")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OrangePrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "C520X Customer Hub",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Thumbtack Pro • TaskRabbit • Angie • Yelp",
                                        fontSize = 11.sp,
                                        color = OrangePrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "100% Guaranteed",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Hire background-checked master plumbers, certified electricians, remodeling contractors, and logistics delivery crews. Receive multiple competitive bids in minutes.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        // Summary Stats Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickHubStat(
                                label = "Open Requests",
                                value = "${bookings.count { it.status == "Open For Bids" }}",
                                color = StatusAmber
                            )
                            QuickHubStat(
                                label = "Active Jobs",
                                value = "${bookings.count { it.status in listOf("Booked", "En Route", "In Progress") }}",
                                color = StatusBlue
                            )
                            QuickHubStat(
                                label = "Completed",
                                value = "${bookings.count { it.status == "Completed" }}",
                                color = StatusGreen
                            )
                            QuickHubStat(
                                label = "Available Pros",
                                value = "48 Live",
                                color = OrangePrimary
                            )
                        }
                    }
                }
            }

            // Trade Category Selector Carousel
            item {
                Text(
                    text = "Browse Services",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("category_filter_row")
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategoryFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Status Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Open", "Active", "Completed").forEach { statusLabel ->
                        val isSel = selectedStatusFilter == statusLabel
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) OrangePrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) OrangePrimary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedStatusFilter = statusLabel }
                                .testTag("status_filter_$statusLabel")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = statusLabel,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
                        text = "My Bookings & Marketplace Requests (${filteredBookings.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { showRequestDialog = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = OrangePrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Request", fontSize = 12.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Empty State
            if (filteredBookings.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No service bookings found in this filter.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showRequestDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                            ) {
                                Text("Post a New Job Request", color = Color.White)
                            }
                        }
                    }
                }
            }

            // Bookings List
            items(filteredBookings, key = { it.id }) { booking ->
                val bidsForThisBooking = allBids.filter { it.bookingRequestId == booking.id }
                CustomerBookingCard(
                    booking = booking,
                    bids = bidsForThisBooking,
                    onAcceptBid = { bid ->
                        viewModel.acceptProBid(
                            bookingId = booking.id,
                            bidId = bid.id,
                            proName = bid.proName,
                            quoteAmount = bid.bidAmount
                        )
                    },
                    onCancelBooking = { viewModel.cancelCustomerBooking(booking.id) },
                    onContactPro = {
                        viewModel.selectTab(AppTab.CHAT_SLACK)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Modal: New Pro Request Intake (Thumbtack / Angie Style)
    if (showRequestDialog) {
        NewBookingRequestDialog(
            onDismiss = { showRequestDialog = false },
            onSubmit = { name, phone, email, address, category, title, desc, budget, date, urgency ->
                viewModel.submitCustomerBooking(
                    customerName = name,
                    phone = phone,
                    email = email,
                    address = address,
                    category = category,
                    title = title,
                    description = desc,
                    budget = budget,
                    preferredDate = date,
                    urgency = urgency
                )
                showRequestDialog = false
            }
        )
    }
}

@Composable
private fun QuickHubStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CustomerBookingCard(
    booking: CustomerBookingEntity,
    bids: List<MarketplaceBidEntity>,
    onAcceptBid: (MarketplaceBidEntity) -> Unit,
    onCancelBooking: () -> Unit,
    onContactPro: () -> Unit
) {
    var expandedBids by remember { mutableStateOf(booking.status == "Open For Bids") }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    val (statusBg, statusText) = when (booking.status) {
        "Open For Bids" -> StatusAmber.copy(alpha = 0.15f) to StatusAmber
        "Booked" -> StatusBlue.copy(alpha = 0.15f) to StatusBlue
        "En Route" -> Color(0xFF9C27B0).copy(alpha = 0.15f) to Color(0xFFBA68C8)
        "In Progress" -> OrangePrimary.copy(alpha = 0.15f) to OrangePrimary
        "Completed" -> StatusGreen.copy(alpha = 0.15f) to StatusGreen
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Category + Urgency + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = OrangePrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = booking.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (booking.urgency.contains("Emergency", ignoreCase = true)) StatusRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = booking.urgency,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (booking.urgency.contains("Emergency", ignoreCase = true)) StatusRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg
                ) {
                    Text(
                        text = booking.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Project Title & Budget
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = booking.projectTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(if (booking.acceptedQuoteAmount > 0) booking.acceptedQuoteAmount else booking.budget),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = if (booking.acceptedQuoteAmount > 0) StatusGreen else OrangePrimary
                    )
                    Text(
                        text = if (booking.acceptedQuoteAmount > 0) "Locked Quote" else "Est. Budget",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = booking.description,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Address and Preferred Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = booking.address,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = booking.preferredDate,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // If Assigned / Booked Pro: Show Pro Dispatch Card
            if (booking.status in listOf("Booked", "En Route", "In Progress", "Completed") && booking.assignedProName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = StatusGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = booking.assignedProName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "${booking.assignedProRating} • ${booking.assignedProBadge}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Row {
                            IconButton(
                                onClick = onContactPro,
                                modifier = Modifier.size(32.dp).testTag("chat_pro_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Message Pro",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { /* Call pro */ },
                                modifier = Modifier.size(32.dp).testTag("call_pro_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call Pro",
                                    tint = StatusGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Marketplace Bids Section (Thumbtack / Angie style)
            if (bids.isNotEmpty() && booking.status == "Open For Bids") {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedBids = !expandedBids },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StatusGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${bids.size} Verified Pro Quotes Received",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = StatusGreen
                        )
                    }
                    Text(
                        text = if (expandedBids) "Hide Quotes ▲" else "Compare Quotes ▼",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimary
                    )
                }

                AnimatedVisibility(visible = expandedBids) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bids.forEach { bid ->
                            ContractorBidItem(
                                bid = bid,
                                onAccept = { onAcceptBid(bid) }
                            )
                        }
                    }
                }
            }

            // Bottom Actions (Cancel / Details)
            if (booking.status == "Open For Bids") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onCancelBooking,
                        colors = ButtonDefaults.textButtonColors(contentColor = StatusRed)
                    ) {
                        Text("Cancel Request", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ContractorBidItem(
    bid: MarketplaceBidEntity,
    onAccept: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth().testTag("bid_card_${bid.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = bid.proName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = bid.proCompany,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(bid.bidAmount),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = StatusGreen
                    )
                    Text(
                        text = "${bid.estimatedHours} hrs est.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            // Rating & Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${bid.proRating} (${bid.reviewCount})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = OrangePrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = bid.badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bid.proposalNote,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Earliest: ${bid.earliestAvailability}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("accept_bid_${bid.id}")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Accept Quote & Book", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun NewBookingRequestDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, email: String, address: String, category: String, title: String, desc: String, budget: Double, date: String, urgency: String) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Plumbing") }
    var projectTitle by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var budgetInput by remember { mutableStateOf("500") }
    var preferredDate by remember { mutableStateOf("Tomorrow") }
    var urgency by remember { mutableStateOf("Within 48 Hours") }

    val categories = listOf("Plumbing", "Electrical", "HVAC", "Remodeling", "Roofing", "Handyman", "Carpentry", "Painting", "Moving & Freight")
    val urgencies = listOf("Emergency Today", "Within 48 Hours", "Flexible This Week")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PostAdd, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Request a Verified Pro", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Select Trade Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = cat == selectedCategory,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = projectTitle,
                        onValueChange = { projectTitle = it },
                        label = { Text("What do you need done? (e.g. Water Heater Replacement)") },
                        modifier = Modifier.fillMaxWidth().testTag("req_title_input"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = projectDescription,
                        onValueChange = { projectDescription = it },
                        label = { Text("Describe the project details & requirements") },
                        modifier = Modifier.fillMaxWidth().testTag("req_desc_input"),
                        minLines = 3
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = budgetInput,
                            onValueChange = { budgetInput = it },
                            label = { Text("Target Budget ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("req_budget_input")
                        )
                        OutlinedTextField(
                            value = preferredDate,
                            onValueChange = { preferredDate = it },
                            label = { Text("Desired Date") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text("Urgency Level", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        urgencies.forEach { urg ->
                            FilterChip(
                                selected = urg == urgency,
                                onClick = { urgency = urg },
                                label = { Text(urg, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    Text("Contact & Jobsite Location", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Your Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number for Pro SMS/Call") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Property Address (Tucson / Metro Area)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budget = budgetInput.toDoubleOrNull() ?: 500.0
                    onSubmit(
                        customerName.ifEmpty { "Verified Customer" },
                        phone.ifEmpty { "(520) 555-0199" },
                        email,
                        address.ifEmpty { "Tucson, AZ" },
                        selectedCategory,
                        projectTitle.ifEmpty { "General $selectedCategory Service" },
                        projectDescription.ifEmpty { "Need professional $selectedCategory technician." },
                        budget,
                        preferredDate.ifEmpty { "Tomorrow" },
                        urgency
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("submit_request_button")
            ) {
                Text("Broadcast to Pros", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
