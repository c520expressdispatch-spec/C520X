package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.FreightLoadEntity
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary

@Composable
fun EditFreightPlacardDialog(
    load: FreightLoadEntity,
    onDismiss: () -> Unit,
    onSave: (FreightLoadEntity) -> Unit
) {
    var loadNumber by remember { mutableStateOf(load.loadNumber) }
    var origin by remember { mutableStateOf(load.originCity) }
    var destination by remember { mutableStateOf(load.destinationCity) }
    var equipmentType by remember { mutableStateOf(load.equipmentType) }
    var rateText by remember { mutableStateOf(load.rateTotal.toString()) }
    var tripMilesText by remember { mutableStateOf(load.tripMiles.toString()) }
    var deadheadText by remember { mutableStateOf(load.deadheadMiles.toString()) }
    var weightText by remember { mutableStateOf(load.weightLbs.toString()) }
    var commodity by remember { mutableStateOf(load.commodity) }
    var status by remember { mutableStateOf(load.status) }

    var showEquipmentDropdown by remember { mutableStateOf(false) }
    val equipmentOptions = listOf("Dry Van", "Reefer", "Flatbed", "Step Deck", "Power Only", "Box Truck 26ft")

    var showStatusDropdown by remember { mutableStateOf(false) }
    val statusOptions = listOf("Available", "Booked", "In Transit", "Delivered", "Factored")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Navy900,
            border = BorderStroke(1.5.dp, OrangePrimary),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("edit_freight_placard_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header with SVG Icons
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
                            Image(
                                painter = painterResource(id = R.drawable.ic_driver_truck),
                                contentDescription = "Truck SVG",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "EDIT DRIVER & FREIGHT PLACARD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = OrangePrimary
                            )
                            Text(
                                text = "DAT One & Freight Rate Editor",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = loadNumber,
                        onValueChange = { loadNumber = it },
                        label = { Text("Load Reference #") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = origin,
                            onValueChange = { origin = it },
                            label = { Text("Origin City & State") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text("Destination City") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Equipment Type Selector
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = equipmentType,
                            onValueChange = {},
                            label = { Text("Equipment Type") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showEquipmentDropdown = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Change Equipment", tint = OrangePrimary)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = showEquipmentDropdown,
                            onDismissRequest = { showEquipmentDropdown = false }
                        ) {
                            equipmentOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        equipmentType = opt
                                        showEquipmentDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = rateText,
                            onValueChange = { rateText = it },
                            label = { Text("Total Rate ($)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tripMilesText,
                            onValueChange = { tripMilesText = it },
                            label = { Text("Trip Miles") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = deadheadText,
                            onValueChange = { deadheadText = it },
                            label = { Text("Deadhead Miles") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            label = { Text("Weight (lbs)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Status Selector
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            label = { Text("Freight Load Status") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showStatusDropdown = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Change Status", tint = OrangePrimary)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = showStatusDropdown,
                            onDismissRequest = { showStatusDropdown = false }
                        ) {
                            statusOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        status = opt
                                        showStatusDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = commodity,
                        onValueChange = { commodity = it },
                        label = { Text("Commodity & Cargo Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons
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
                            val newRate = rateText.toDoubleOrNull() ?: load.rateTotal
                            val newMiles = tripMilesText.toIntOrNull() ?: load.tripMiles
                            val newDeadhead = deadheadText.toIntOrNull() ?: load.deadheadMiles
                            val newWeight = weightText.toIntOrNull() ?: load.weightLbs
                            val newRatePerMile = if (newMiles > 0) newRate / newMiles else load.ratePerMile

                            val updated = load.copy(
                                loadNumber = loadNumber.trim(),
                                originCity = origin.trim(),
                                destinationCity = destination.trim(),
                                equipmentType = equipmentType,
                                rateTotal = newRate,
                                tripMiles = newMiles,
                                deadheadMiles = newDeadhead,
                                weightLbs = newWeight,
                                ratePerMile = newRatePerMile,
                                commodity = commodity.trim(),
                                status = status
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f).testTag("save_freight_placard_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Placard Changes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
