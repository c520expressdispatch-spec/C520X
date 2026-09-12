package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.FreightLoadEntity
import com.example.data.JobEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusGreen
import java.text.NumberFormat
import java.util.Locale

data class ParsedScheduleRow(
    val loadNumber: String,
    val origin: String,
    val destination: String,
    val equipmentType: String,
    val rate: Double,
    val miles: Int,
    val pickupDate: String,
    val deliveryDate: String,
    val trade: String,
    val isValid: Boolean,
    val errorMessage: String? = null
)

@Composable
fun BulkScheduleImportDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val sampleTemplateData = remember {
        """
        DAT-90410,Dallas TX,Atlanta GA,Reefer,3400.0,780,Tomorrow 08:00,In 2 Days 14:00,Freight Dispatch
        DAT-90411,Chicago IL,Denver CO,Dry Van,2850.0,1010,Tomorrow 10:00,In 2 Days 18:00,Freight Dispatch
        CTR-30812,Phoenix AZ,Scottsdale AZ,Box Truck 26ft,1850.0,45,Tomorrow 07:00,Tomorrow 16:00,Electrical
        CTR-30813,Tucson AZ,Mesa AZ,Hotshot,2100.0,115,In 2 Days 09:00,In 2 Days 17:00,Plumbing
        DAT-90414,Los Angeles CA,Las Vegas NV,Flatbed,1950.0,270,Tomorrow 06:00,Tomorrow 13:00,Freight Dispatch
        """.trimIndent()
    }

    var rawInputText by remember { mutableStateOf(sampleTemplateData) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    // Parse input in real-time
    val parsedRows by remember(rawInputText) {
        derivedStateOf {
            val lines = rawInputText.lines().map { it.trim() }.filter { it.isNotEmpty() }
            lines.mapIndexedNotNull { index, line ->
                if (line.startsWith("#") || line.startsWith("LoadNumber", ignoreCase = true)) {
                    null // skip headers or comments
                } else {
                    val tokens = line.split(",").map { it.trim() }
                    if (tokens.size >= 9) {
                        val loadNumber = tokens[0]
                        val origin = tokens[1]
                        val destination = tokens[2]
                        val equipment = tokens[3]
                        val rate = tokens[4].toDoubleOrNull() ?: 0.0
                        val miles = tokens[5].toIntOrNull() ?: 0
                        val pickup = tokens[6]
                        val delivery = tokens[7]
                        val trade = tokens[8]
                        ParsedScheduleRow(
                            loadNumber = loadNumber,
                            origin = origin,
                            destination = destination,
                            equipmentType = equipment,
                            rate = rate,
                            miles = miles,
                            pickupDate = pickup,
                            deliveryDate = delivery,
                            trade = trade,
                            isValid = loadNumber.isNotEmpty() && rate > 0
                        )
                    } else {
                        ParsedScheduleRow(
                            loadNumber = "ROW-${index + 1}",
                            origin = "Invalid",
                            destination = "Invalid",
                            equipmentType = "Unknown",
                            rate = 0.0,
                            miles = 0,
                            pickupDate = "",
                            deliveryDate = "",
                            trade = "",
                            isValid = false,
                            errorMessage = "Expected 9 comma-separated fields, got ${tokens.size}"
                        )
                    }
                }
            }
        }
    }

    val validRows = parsedRows.filter { it.isValid }
    val totalRevenue = validRows.sumOf { it.rate }
    val contractorTake = totalRevenue * 0.80 // 80% take / 20% C520X
    val driverTake = totalRevenue * 0.88 // 88% DAT take

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Navy900,
            border = BorderStroke(1.5.dp, OrangePrimary),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("bulk_schedule_import_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Dialog Header
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
                                .background(OrangePrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BULK IMPORT DISPATCH LOADS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = OrangePrimary
                            )
                            Text(
                                text = "Structured template for multiple contractor & freight loads",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Structured Template Bar & Actions
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy700),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STRUCTURED CSV TEMPLATE SCHEMA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = OrangePrimary.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, OrangePrimary),
                                    modifier = Modifier.clickable { rawInputText = sampleTemplateData }
                                ) {
                                    Text(
                                        text = "⚡ Load 5 Sample Loads",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.DarkGray.copy(alpha = 0.5f),
                                    modifier = Modifier.clickable { rawInputText = "" }
                                ) {
                                    Text(
                                        text = "Clear",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.LightGray,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "LoadNumber,Origin,Destination,EquipmentType,RateTotal,Miles,PickupDate,DeliveryDate,ContractorTrade",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Color(0xFF64B5F6)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Text Input Area
                OutlinedTextField(
                    value = rawInputText,
                    onValueChange = { rawInputText = it },
                    label = { Text("Paste or Edit Structured CSV Records", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Live Validation Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy700),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("VALID LOADS", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text("${validRows.size} of ${parsedRows.size}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = StatusGreen)
                        }
                    }
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy700),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("TOTAL GROSS REVENUE", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(currencyFormat.format(totalRevenue), fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy700),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("PRO PAYOUT (80-88%)", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(currencyFormat.format(contractorTake), fontSize = 15.sp, fontWeight = FontWeight.Black, color = OrangePrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Parsed Table Preview
                Text(
                    text = "VALIDATED RECORDS PREVIEW (${validRows.size})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Navy700.copy(alpha = 0.4f))
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(parsedRows) { row ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (row.isValid) Navy900 else Color(0xFF3E1C1C),
                            border = BorderStroke(1.dp, if (row.isValid) Color(0xFF1E293B) else Color.Red.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (row.isValid) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (row.isValid) StatusGreen else Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${row.loadNumber} • ${row.trade}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${row.origin} ➔ ${row.destination} (${row.equipmentType})",
                                            fontSize = 10.sp,
                                            color = Color.LightGray
                                        )
                                        if (!row.isValid && row.errorMessage != null) {
                                            Text(text = row.errorMessage, fontSize = 9.sp, color = Color.Red)
                                        }
                                    }
                                }

                                if (row.isValid) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = currencyFormat.format(row.rate),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusGreen
                                        )
                                        Text(
                                            text = "${row.miles} mi • ${(row.rate / (if (row.miles > 0) row.miles else 1)).let { String.format(Locale.US, "$%.2f/mi", it) }}",
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = {
                            if (validRows.isNotEmpty()) {
                                val loadsToImport = validRows.map { row ->
                                    FreightLoadEntity(
                                        loadNumber = row.loadNumber,
                                        originCity = row.origin,
                                        destinationCity = row.destination,
                                        deadheadMiles = 15,
                                        tripMiles = row.miles,
                                        equipmentType = row.equipmentType,
                                        weightLbs = 26000,
                                        commodity = "Commercial Cargo & Trade Tools",
                                        rateTotal = row.rate,
                                        ratePerMile = if (row.miles > 0) (row.rate / row.miles) else 3.0,
                                        brokerName = "C520X Logistics Clearing",
                                        pickupDate = row.pickupDate,
                                        deliveryDate = row.deliveryDate,
                                        status = "Available"
                                    )
                                }

                                val jobsToImport = validRows.map { row ->
                                    JobEntity(
                                        title = "${row.equipmentType} Haul - ${row.origin} to ${row.destination}",
                                        trade = if (row.trade.isNotBlank() && row.trade != "Freight Dispatch") row.trade else "Handyman",
                                        clientName = "Bulk Enterprise Client",
                                        clientPhone = "(520) 880-9200",
                                        address = "${row.origin} / ${row.destination}",
                                        status = "Scheduled",
                                        billedAmount = row.rate,
                                        pipelineValue = row.rate,
                                        scheduledDate = row.pickupDate,
                                        assignedTech = "Fleet Contractor Pro",
                                        priority = "High",
                                        notes = "Bulk imported template dispatch #${row.loadNumber}"
                                    )
                                }

                                viewModel.bulkImportDispatchLoads(loadsToImport, jobsToImport)
                                onDismiss()
                            }
                        },
                        enabled = validRows.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(2f).testTag("confirm_bulk_import_btn")
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Import All ${validRows.size} Loads Simultaneously",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
