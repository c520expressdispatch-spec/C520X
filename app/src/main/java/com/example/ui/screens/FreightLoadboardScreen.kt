package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.FreightLoadEntity
import com.example.ui.MainViewModel
import com.example.ui.components.EditFreightPlacardDialog
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue

@Composable
fun FreightLoadboardScreen(viewModel: MainViewModel) {
    val allLoads by viewModel.freightLoads.collectAsState()
    val driverFee by viewModel.driverFeePercent.collectAsState()

    var selectedSection by remember { mutableStateOf(0) } // 0: Live Loadboard, 1: Active Hauls & Dispatch, 2: Trip Profit Calculator
    var selectedEquipmentFilter by remember { mutableStateOf("All") }
    var searchLaneQuery by remember { mutableStateOf("") }

    // Dialog state
    var showPostLoadDialog by remember { mutableStateOf(false) }
    var bookingLoad by remember { mutableStateOf<FreightLoadEntity?>(null) }
    var deliverLoadTarget by remember { mutableStateOf<FreightLoadEntity?>(null) }

    val equipmentTypes = listOf("All", "Dry Van", "Reefer", "Flatbed", "Box Truck 26ft", "Hotshot", "Cargo Van / Sprinter")

    // Filter available loads
    val availableLoads = allLoads.filter { it.status == "Available" }.filter { load ->
        (selectedEquipmentFilter == "All" || load.equipmentType.equals(selectedEquipmentFilter, ignoreCase = true)) &&
        (searchLaneQuery.isEmpty() ||
                load.originCity.contains(searchLaneQuery, ignoreCase = true) ||
                load.destinationCity.contains(searchLaneQuery, ignoreCase = true) ||
                load.commodity.contains(searchLaneQuery, ignoreCase = true) ||
                load.loadNumber.contains(searchLaneQuery, ignoreCase = true))
    }

    // Active hauls in flight
    val activeHauls = allLoads.filter { it.status in listOf("Booked", "Dispatched", "In Transit", "Delivered", "Factored") }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showPostLoadDialog = true },
                icon = { Icon(Icons.Default.AddRoad, contentDescription = null) },
                text = { Text("Post Load / Freight Order", fontWeight = FontWeight.Bold) },
                containerColor = OrangePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("post_freight_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // DAT One Live Market Pulse & Freight Hero
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth().testTag("dat_one_hero")
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
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OrangePrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "C520X Freight & Logistics",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "DAT One Live Loadboard & Owner-Operator Fleet",
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
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(6.dp).clip(CircleShape).background(StatusGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ELD Live",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusGreen
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        // Real-time Freight Spot Market Metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DatPulseMetric(title = "Avg. Spot Rate", value = "$3.82 / mi", color = StatusGreen)
                            DatPulseMetric(title = "Available Loads", value = "${availableLoads.size} Live", color = OrangePrimary)
                            DatPulseMetric(title = "US Diesel Index", value = "$3.84 / gal", color = StatusBlue)
                            DatPulseMetric(title = "Load-to-Truck", value = "5.8 L/T", color = Color(0xFFFFB300))
                        }
                    }
                }
            }

            // View Selector Tabs (Live Loadboard, Active Hauls & Dispatch, Trip Profit Calculator)
            item {
                TabRow(
                    selectedTabIndex = selectedSection,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = OrangePrimary,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).testTag("freight_tabrow")
                ) {
                    Tab(
                        selected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Dvr, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Live Loadboard (${availableLoads.size})", fontSize = 11.sp, fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Medium)
                            }
                        },
                        modifier = Modifier.testTag("tab_loadboard")
                    )
                    Tab(
                        selected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RvHookup, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Active Hauls (${activeHauls.size})", fontSize = 11.sp, fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Medium)
                            }
                        },
                        modifier = Modifier.testTag("tab_active_hauls")
                    )
                    Tab(
                        selected = selectedSection == 2,
                        onClick = { selectedSection = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Profit Calc", fontSize = 11.sp, fontWeight = if (selectedSection == 2) FontWeight.Bold else FontWeight.Medium)
                            }
                        },
                        modifier = Modifier.testTag("tab_profit_calc")
                    )
                }
            }

            if (selectedSection == 0) {
                // ==========================================
                // SECTION 0: LIVE LOADBOARD (DAT ONE STYLE)
                // ==========================================
                item {
                    // Search Lane Bar
                    OutlinedTextField(
                        value = searchLaneQuery,
                        onValueChange = { searchLaneQuery = it },
                        placeholder = { Text("Search lane (e.g. Phoenix, Dallas, Atlanta, Chicago)") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (searchLaneQuery.isNotEmpty()) {
                                IconButton(onClick = { searchLaneQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("lane_search_input")
                    )
                }

                item {
                    // Equipment Selector Filter Chips
                    Column {
                        Text(
                            text = "Equipment Type",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth().testTag("equipment_filter_row")
                        ) {
                            items(equipmentTypes) { equip ->
                                val isSel = selectedEquipmentFilter == equip
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedEquipmentFilter = equip },
                                    label = { Text(equip, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = OrangePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                if (availableLoads.isEmpty()) {
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
                                Icon(Icons.Default.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No DAT freight loads match this filter.", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        selectedEquipmentFilter = "All"
                                        searchLaneQuery = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                                ) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    }
                }

                items(availableLoads, key = { it.id }) { load ->
                    DatLoadCard(
                        load = load,
                        driverFeePercent = driverFee,
                        onBookLoad = { bookingLoad = load },
                        onToggleSave = { viewModel.toggleSaveFreightLoad(load.id) },
                        onEditLoad = { updated -> viewModel.updateFreightLoad(updated) }
                    )
                }
            } else if (selectedSection == 1) {
                // ==========================================
                // SECTION 1: ACTIVE HAULS & DISPATCH WORKFLOW
                // ==========================================
                item {
                    Text(
                        text = "Owner-Operator Active Trip Pipeline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time dispatch, active transit ELD tracking, consignee sign-off, and instant 24hr QuickPay factoring.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (activeHauls.isEmpty()) {
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
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No active hauls currently rolling.", fontWeight = FontWeight.SemiBold)
                                Text("Book an available load from the DAT Live Loadboard to dispatch.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                items(activeHauls, key = { it.id }) { haul ->
                    ActiveHaulWorkflowCard(
                        haul = haul,
                        onDispatch = { viewModel.dispatchFreightHaul(haul.id) },
                        onUpdateGps = { newGps -> viewModel.updateHaulGps(haul.id, newGps) },
                        onDeliver = { deliverLoadTarget = haul },
                        onFactorQuickPay = { viewModel.factorLoadInvoice(haul.id) },
                        onEditHaul = { updated -> viewModel.updateFreightLoad(updated) }
                    )
                }
            } else {
                // ==========================================
                // SECTION 2: TRIP PROFIT CALCULATOR
                // ==========================================
                item {
                    OwnerOperatorTripCalculator()
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Modal: Book Load & Sign Rate Con
    bookingLoad?.let { load ->
        BookLoadDialog(
            load = load,
            driverFeePercent = driverFee,
            onDismiss = { bookingLoad = null },
            onConfirmBook = { driverName ->
                viewModel.bookFreightLoad(load.id, driverName)
                bookingLoad = null
            }
        )
    }

    // Modal: Deliver Load & Sign Bill of Lading (BOL)
    deliverLoadTarget?.let { haul ->
        DeliverBolDialog(
            haul = haul,
            onDismiss = { deliverLoadTarget = null },
            onConfirmDeliver = { bol ->
                viewModel.deliverFreightLoad(haul.id, bol)
                deliverLoadTarget = null
            }
        )
    }

    // Modal: Post New Load (Broker / Logistics Shipper)
    if (showPostLoadDialog) {
        PostFreightLoadDialog(
            onDismiss = { showPostLoadDialog = false },
            onSubmit = { origin, dest, equip, trip, deadhead, weight, commodity, rate, brokerName, brokerPhone, pickup, delivery ->
                viewModel.postNewFreightLoad(
                    originCity = origin,
                    destinationCity = dest,
                    equipmentType = equip,
                    tripMiles = trip,
                    deadheadMiles = deadhead,
                    weightLbs = weight,
                    commodity = commodity,
                    rateTotal = rate,
                    brokerName = brokerName,
                    brokerPhone = brokerPhone,
                    pickupDate = pickup,
                    deliveryDate = delivery
                )
                showPostLoadDialog = false
            }
        )
    }
}

@Composable
private fun DatPulseMetric(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DatLoadCard(
    load: FreightLoadEntity,
    driverFeePercent: Double = 10.0,
    onBookLoad: () -> Unit,
    onToggleSave: () -> Unit,
    onEditLoad: (FreightLoadEntity) -> Unit = {}
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val driverNet = load.rateTotal * ((100.0 - driverFeePercent) / 100.0)
    val c520xFee = load.rateTotal * (driverFeePercent / 100.0)

    var showDropdownMenu by remember { mutableStateOf(false) }
    var showEditPlacardDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    // Pro calculations
    val estGallons = if (load.tripMiles > 0) load.tripMiles / 6.5 else 0.0
    val estFuelCost = estGallons * 3.85
    val estNetProfit = driverNet - estFuelCost

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().testTag("dat_load_card_${load.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Freight SVG, Load ID, Equipment Pill, Actions Dropdown & Save Star
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
                            .background(OrangePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_freight_cargo),
                            contentDescription = "Freight SVG",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = load.loadNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = OrangePrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = load.equipmentType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showEditPlacardDialog = true },
                        modifier = Modifier.size(30.dp).testTag("edit_load_placard_${load.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Load Placard",
                            tint = OrangePrimary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier.size(30.dp).testTag("save_load_${load.id}")
                    ) {
                        Icon(
                            imageVector = if (load.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Load",
                            tint = if (load.isSaved) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Professional Actions Dropdown Menu
                    Box {
                        IconButton(
                            onClick = { showDropdownMenu = true },
                            modifier = Modifier.size(30.dp).testTag("load_menu_btn_${load.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Load Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = { showDropdownMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("⚡ Edit Freight Placard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary) },
                                onClick = {
                                    showDropdownMenu = false
                                    showEditPlacardDialog = true
                                },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_freight_cargo),
                                        contentDescription = "Edit SVG",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Rate & Factoring Split (88-93%)", fontSize = 12.sp) },
                                onClick = {
                                    showDropdownMenu = false
                                    isExpanded = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Fuel & Net Estimator", fontSize = 12.sp) },
                                onClick = {
                                    showDropdownMenu = false
                                    isExpanded = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Broker MC# & Authority Verification", fontSize = 12.sp) },
                                onClick = {
                                    showDropdownMenu = false
                                    isExpanded = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(16.dp))
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Instant One-Touch Booking", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary) },
                                onClick = {
                                    showDropdownMenu = false
                                    onBookLoad()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Main Lane: Origin -> Destination & Gross Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusGreen))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = load.originCity,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusRed))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = load.destinationCity,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(load.rateTotal),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = StatusGreen
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = StatusGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$${load.ratePerMile}/mi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            // Revenue Split Notice with Driver Truck SVG
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusBlue.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_driver_truck),
                            contentDescription = "Driver SVG",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Driver (${String.format(Locale.US, "%.0f", 100 - driverFeePercent)}%): ${currencyFormat.format(driverNet)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                    }
                    Text(
                        text = "C520X (${String.format(Locale.US, "%.0f", driverFeePercent)}%): ${currencyFormat.format(c520xFee)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Trip Metrics: Loaded Miles, Deadhead, Weight, Commodity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${load.tripMiles} mi (DHO: ${load.deadheadMiles} mi)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${NumberFormat.getIntegerInstance().format(load.weightLbs)} lbs",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Pickup: ${load.pickupDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Commodity: ${load.commodity}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Collapsible Pro Specs & Fuel Analysis Accordion
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "PRO FINANCIAL & FLEET ANALYSIS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Est. Diesel Fuel", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format(Locale.US, "%.1f", estGallons)} gal @ $3.85", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("-${currencyFormat.format(estFuelCost)}", fontSize = 11.sp, color = StatusRed, fontWeight = FontWeight.SemiBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Est. Net Profit", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(currencyFormat.format(estNetProfit), fontSize = 13.sp, fontWeight = FontWeight.Black, color = StatusGreen)
                            Text("Post fuel & C520X fee", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "MC Authority: MC-${100000 + (load.id.hashCode().absoluteValue % 899999)}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Surety Bond: $75,000 BMC-84",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Broker Details, Pro Analysis Toggle & Book Now
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = load.brokerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StatusBlue.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${load.brokerCreditScore} A+ Credit",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusBlue,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = load.daysToPay,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusGreen
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(if (isExpanded) "Hide Details" else "Pro Specs", fontSize = 11.sp)
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Button(
                        onClick = onBookLoad,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("book_load_btn_${load.id}")
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    if (showEditPlacardDialog) {
        EditFreightPlacardDialog(
            load = load,
            onDismiss = { showEditPlacardDialog = false },
            onSave = { updatedLoad ->
                onEditLoad(updatedLoad)
                showEditPlacardDialog = false
            }
        )
    }
}

@Composable
fun ActiveHaulWorkflowCard(
    haul: FreightLoadEntity,
    onDispatch: () -> Unit,
    onUpdateGps: (String) -> Unit,
    onDeliver: () -> Unit,
    onFactorQuickPay: () -> Unit,
    onEditHaul: (FreightLoadEntity) -> Unit = {}
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    var showHaulMenu by remember { mutableStateOf(false) }
    var showEditPlacardDialog by remember { mutableStateOf(false) }
    var isHaulExpanded by remember { mutableStateOf(false) }

    val (statusBg, statusText) = when (haul.status) {
        "Booked" -> StatusBlue.copy(alpha = 0.15f) to StatusBlue
        "Dispatched" -> Color(0xFF9C27B0).copy(alpha = 0.15f) to Color(0xFFBA68C8)
        "In Transit" -> OrangePrimary.copy(alpha = 0.15f) to OrangePrimary
        "Delivered" -> StatusGreen.copy(alpha = 0.15f) to StatusGreen
        "Factored" -> Color(0xFF00897B).copy(alpha = 0.15f) to Color(0xFF26A69A)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().testTag("active_haul_${haul.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top: Driver Truck SVG + Load # and Status & Dropdown menu
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
                            .background(OrangePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_driver_truck),
                            contentDescription = "Driver Truck SVG",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(haul.loadNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = OrangePrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = haul.equipmentType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusBg
                    ) {
                        Text(
                            text = haul.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showHaulMenu = true },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Haul Menu", modifier = Modifier.size(18.dp))
                        }

                        DropdownMenu(
                            expanded = showHaulMenu,
                            onDismissRequest = { showHaulMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("⚡ Edit Haul Placard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary) },
                                onClick = {
                                    showHaulMenu = false
                                    showEditPlacardDialog = true
                                },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_driver_truck),
                                        contentDescription = "Edit Haul SVG",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Log Electronic ELD Checkpoint", fontSize = 12.sp) },
                                onClick = {
                                    showHaulMenu = false
                                    onUpdateGps("ELD Automatic Waypoint Pinged")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.GpsFixed, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("View Full Driver Dispatch Specs", fontSize = 12.sp) },
                                onClick = {
                                    showHaulMenu = false
                                    isHaulExpanded = !isHaulExpanded
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(16.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Instant 24-hr Factoring Payout", fontSize = 12.sp) },
                                onClick = {
                                    showHaulMenu = false
                                    onFactorQuickPay()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Lane & Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${haul.originCity}  ➜  ${haul.destinationCity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currencyFormat.format(haul.rateTotal),
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = StatusGreen
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_freight_cargo),
                    contentDescription = "Freight Cargo",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${haul.tripMiles} miles • ${haul.commodity} • Broker: ${haul.brokerName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Assigned Driver / ELD
            if (haul.driverAssigned.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonPin, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Driver Assigned: ${haul.driverAssigned}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // GPS Live Location Strip
            if (haul.status in listOf("Dispatched", "In Transit")) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OrangePrimary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.GpsFixed, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (haul.currentGpsLocation.isNotEmpty()) haul.currentGpsLocation else "ELD GPS Active - Wheels Rolling",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { onUpdateGps("Milepost ${(40..380).random()} En Route (Check-in)") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Ping GPS", modifier = Modifier.size(14.dp), tint = OrangePrimary)
                        }
                    }
                }
            }

            // Expandable Haul Details
            AnimatedVisibility(visible = isHaulExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(10.dp)
                ) {
                    Text("HAUL DISPATCH & COMPLIANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Equipment: ${haul.equipmentType} • Weight: ${haul.weightLbs} lbs", fontSize = 10.sp)
                    Text("Rate Per Mile: $${haul.ratePerMile}/mi • Deadhead: ${haul.deadheadMiles} mi", fontSize = 10.sp)
                    Text("Broker Contact: ${haul.brokerName} (${haul.brokerCreditScore} Score)", fontSize = 10.sp)
                }
            }

            // BOL verification info
            if (haul.bolNumber.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Signed BOL: ${haul.bolNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Action Buttons by State
            when (haul.status) {
                "Booked" -> {
                    Button(
                        onClick = onDispatch,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.fillMaxWidth().testTag("dispatch_haul_${haul.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Transit / Wheels Rolling", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                "Dispatched", "In Transit" -> {
                    Button(
                        onClick = onDeliver,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                        modifier = Modifier.fillMaxWidth().testTag("deliver_haul_${haul.id}")
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Deliver Load & Upload Signed BOL", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                "Delivered" -> {
                    Button(
                        onClick = onFactorQuickPay,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                        modifier = Modifier.fillMaxWidth().testTag("factor_haul_${haul.id}")
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Factor Invoice (Instant 24hr QuickPay)", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                "Factored" -> {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF00897B).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00897B).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00897B), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Settled & Paid via 24hr QuickPay ACH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00897B))
                        }
                    }
                }
            }
        }
    }

    if (showEditPlacardDialog) {
        EditFreightPlacardDialog(
            load = haul,
            onDismiss = { showEditPlacardDialog = false },
            onSave = { updatedLoad ->
                onEditHaul(updatedLoad)
                showEditPlacardDialog = false
            }
        )
    }
}

@Composable
fun OwnerOperatorTripCalculator() {
    var grossRateInput by remember { mutableStateOf("2950") }
    var tripMilesInput by remember { mutableStateOf("781") }
    var deadheadInput by remember { mutableStateOf("25") }
    var fuelPriceInput by remember { mutableStateOf("3.84") }
    var truckMpgInput by remember { mutableStateOf("6.5") }
    var tollsInput by remember { mutableStateOf("45") }

    val gross = grossRateInput.toDoubleOrNull() ?: 0.0
    val tripMiles = tripMilesInput.toDoubleOrNull() ?: 0.0
    val deadhead = deadheadInput.toDoubleOrNull() ?: 0.0
    val totalMiles = tripMiles + deadhead
    val fuelPrice = fuelPriceInput.toDoubleOrNull() ?: 3.84
    val mpg = truckMpgInput.toDoubleOrNull() ?: 6.5
    val tolls = tollsInput.toDoubleOrNull() ?: 0.0

    val fuelGallons = if (mpg > 0) totalMiles / mpg else 0.0
    val fuelCost = fuelGallons * fuelPrice
    val factorFee = gross * 0.019 // 1.9% QuickPay fee
    val totalExpenses = fuelCost + tolls + factorFee
    val netProfit = gross - totalExpenses
    val netPerMile = if (totalMiles > 0) netProfit / totalMiles else 0.0

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth().testTag("trip_profit_calculator")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Owner-Operator Trip Net Profit Engine", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Text(
                text = "Calculate true net earnings factoring fuel MPG, deadhead burn, tolls, and QuickPay factoring fees.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = grossRateInput,
                    onValueChange = { grossRateInput = it },
                    label = { Text("Gross Rate ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = tripMilesInput,
                    onValueChange = { tripMilesInput = it },
                    label = { Text("Loaded Miles") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = deadheadInput,
                    onValueChange = { deadheadInput = it },
                    label = { Text("Deadhead Miles") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = fuelPriceInput,
                    onValueChange = { fuelPriceInput = it },
                    label = { Text("Diesel $/gal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = truckMpgInput,
                    onValueChange = { truckMpgInput = it },
                    label = { Text("Truck MPG") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = tollsInput,
                    onValueChange = { tollsInput = it },
                    label = { Text("Tolls & Scales ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            // Result Card
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = StatusGreen.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ESTIMATED TAKE-HOME NET", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StatusGreen)
                        Text(currencyFormat.format(netProfit), fontWeight = FontWeight.Black, fontSize = 20.sp, color = StatusGreen)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Net $/Mile: $${String.format(Locale.US, "%.2f", netPerMile)}/mi", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("Total Miles: ${totalMiles.toInt()} mi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = StatusGreen.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fuel: ${currencyFormat.format(fuelCost)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("QuickPay (1.9%): ${currencyFormat.format(factorFee)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Tolls: ${currencyFormat.format(tolls)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun BookLoadDialog(
    load: FreightLoadEntity,
    driverFeePercent: Double = 10.0,
    onDismiss: () -> Unit,
    onConfirmBook: (driverName: String) -> Unit
) {
    var driverName by remember { mutableStateOf("Alex Ramirez (Unit #104)") }
    var agreedToRateCon by remember { mutableStateOf(true) }
    val driverNet = load.rateTotal * ((100.0 - driverFeePercent) / 100.0)
    val c520xFee = load.rateTotal * (driverFeePercent / 100.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lock Rate & Book Load", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${load.loadNumber}: ${load.originCity} ➜ ${load.destinationCity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Gross Rate: $${load.rateTotal} ($${load.ratePerMile}/mi) • ${load.tripMiles} mi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = StatusGreen
                )
                Text(
                    text = "Equipment: ${load.equipmentType} • Weight: ${load.weightLbs} lbs",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Broker: ${load.brokerName} (${load.daysToPay})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Master Vault Split details
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusBlue.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("C520X Master Vault Clearing:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("• Master Inflow (100%): $${load.rateTotal}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• Driver Take (${String.format(Locale.US, "%.0f", 100 - driverFeePercent)}%): $${String.format(Locale.US, "%.2f", driverNet)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                        Text("• C520X Fee (${String.format(Locale.US, "%.0f", driverFeePercent)}%): $${String.format(Locale.US, "%.2f", c520xFee)}", fontSize = 10.sp, color = StatusBlue)
                        Text("• QuickPay: Direct payout released via Master App within 24h", fontSize = 10.sp, color = OrangePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = driverName,
                    onValueChange = { driverName = it },
                    label = { Text("Assign Driver / Truck Unit #") },
                    modifier = Modifier.fillMaxWidth().testTag("assign_driver_input")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { agreedToRateCon = !agreedToRateCon }
                ) {
                    Checkbox(
                        checked = agreedToRateCon,
                        onCheckedChange = { agreedToRateCon = it }
                    )
                    Text("Digitally sign DAT Rate Confirmation agreement", fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmBook(driverName) },
                enabled = agreedToRateCon && driverName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("confirm_book_load_btn")
            ) {
                Text("Sign & Confirm Load", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeliverBolDialog(
    haul: FreightLoadEntity,
    onDismiss: () -> Unit,
    onConfirmDeliver: (bol: String) -> Unit
) {
    var bolInput by remember { mutableStateOf("BOL-C520X-${(10000..99999).random()}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Delivery & BOL", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Load ${haul.loadNumber} delivered to ${haul.destinationCity}.",
                    fontSize = 13.sp
                )
                Text(
                    text = "Enter signed Bill of Lading (BOL) confirmation number for immediate QuickPay processing:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = bolInput,
                    onValueChange = { bolInput = it },
                    label = { Text("Signed BOL #") },
                    modifier = Modifier.fillMaxWidth().testTag("bol_number_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmDeliver(bolInput) },
                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                modifier = Modifier.testTag("confirm_deliver_btn")
            ) {
                Text("Sign Off Delivery", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PostFreightLoadDialog(
    onDismiss: () -> Unit,
    onSubmit: (origin: String, dest: String, equip: String, trip: Int, deadhead: Int, weight: Int, commodity: String, rate: Double, brokerName: String, brokerPhone: String, pickup: String, delivery: String) -> Unit
) {
    var origin by remember { mutableStateOf("Phoenix, AZ") }
    var destination by remember { mutableStateOf("Dallas, TX") }
    var selectedEquip by remember { mutableStateOf("Dry Van") }
    var tripMiles by remember { mutableStateOf("1065") }
    var weightInput by remember { mutableStateOf("40000") }
    var commodity by remember { mutableStateOf("Building Materials & Fasteners") }
    var rateInput by remember { mutableStateOf("3800") }
    var brokerName by remember { mutableStateOf("C520X Freight Brokerage") }

    val equipList = listOf("Dry Van", "Reefer", "Flatbed", "Box Truck 26ft", "Hotshot", "Cargo Van / Sprinter")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddRoad, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Post Load to Live DAT Board", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = origin,
                            onValueChange = { origin = it },
                            label = { Text("Origin City, ST") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text("Dest City, ST") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text("Equipment", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(equipList) { eq ->
                            FilterChip(
                                selected = eq == selectedEquip,
                                onClick = { selectedEquip = eq },
                                label = { Text(eq, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = tripMiles,
                            onValueChange = { tripMiles = it },
                            label = { Text("Trip Miles") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rateInput,
                            onValueChange = { rateInput = it },
                            label = { Text("Total Rate ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (lbs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = commodity,
                        onValueChange = { commodity = it },
                        label = { Text("Commodity Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = brokerName,
                        onValueChange = { brokerName = it },
                        label = { Text("Broker / Logistics Company") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trip = tripMiles.toIntOrNull() ?: 500
                    val weight = weightInput.toIntOrNull() ?: 20000
                    val rate = rateInput.toDoubleOrNull() ?: 1500.0
                    onSubmit(
                        origin, destination, selectedEquip, trip, 15, weight, commodity, rate,
                        brokerName, "(520) 880-9200", "Tomorrow 08:00", "Next Day"
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("submit_post_load_btn")
            ) {
                Text("Post Load to Board", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
