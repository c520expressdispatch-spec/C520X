package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LeadItem
import com.example.data.models.PartnerType
import com.example.data.models.TradeCategory
import com.example.ui.components.PartnerBadge
import com.example.ui.components.ThumbtackLeadCanvasComponent
import com.example.ui.components.getTradeIcon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SafetyGold
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.ProTradeViewModel

@Composable
fun LeadsScreen(
    viewModel: ProTradeViewModel,
    modifier: Modifier = Modifier
) {
    val leads by viewModel.leads.collectAsState()
    val selectedPartnerFilter by viewModel.selectedPartnerFilter.collectAsState()
    val isEstimating by viewModel.isEstimating.collectAsState()
    var showManualLeadDialog by remember { mutableStateOf(false) }
    var activeViewMode by remember { mutableStateOf("CANVAS") } // "CANVAS" or "UNIVERSAL"

    val filteredLeads = if (selectedPartnerFilter == null) {
        leads
    } else {
        leads.filter { it.partner == selectedPartnerFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // View Mode Selector Tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateCard, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (activeViewMode == "CANVAS") com.example.ui.theme.ThumbtackBlue else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeViewMode = "CANVAS" }
                ) {
                    Text(
                        text = "📊 Thumbtack Lead Canvas",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (activeViewMode == "UNIVERSAL") com.example.ui.theme.SafetyGold else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeViewMode = "UNIVERSAL" }
                ) {
                    Text(
                        text = "🌐 Universal Partner Queue",
                        color = if (activeViewMode == "UNIVERSAL") SlateDark else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        if (activeViewMode == "CANVAS") {
            item {
                ThumbtackLeadCanvasComponent()
            }
        } else {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Universal Leads Feed",
                                color = TextWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Unified Inbound Queue from 5 Networks",
                                color = SafetyGold,
                                fontSize = 12.sp
                            )
                        }
                        Button(
                            onClick = { showManualLeadDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = SlateDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Lead", color = SlateDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isSelected = selectedPartnerFilter == null
                    Surface(
                        modifier = Modifier.clickable { viewModel.filterLeadsByPartner(null) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) SafetyGold else SlateElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SafetyGold else SlateBorder)
                    ) {
                        Text(
                            text = "All Sources (${leads.size})",
                            color = if (isSelected) SlateDark else TextWhite,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                items(PartnerType.entries) { partner ->
                    val isSelected = selectedPartnerFilter == partner
                    val count = leads.count { it.partner == partner }
                    Surface(
                        modifier = Modifier.clickable {
                            viewModel.filterLeadsByPartner(if (isSelected) null else partner)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) partner.badgeColor else SlateElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) partner.badgeColor else SlateBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (isSelected) SlateDark else partner.badgeColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${partner.brandTag} ($count)",
                                color = if (isSelected) SlateDark else TextWhite,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        if (filteredLeads.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No leads currently in this feed.", color = TextMuted, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(filteredLeads) { lead ->
                LeadItemCard(
                    lead = lead,
                    isEstimating = isEstimating,
                    onAiEstimate = { viewModel.generateAiEstimateForLead(lead) },
                    onMarkBooked = { viewModel.updateLeadStatus(lead.id, "Booked") },
                    onMarkCompleted = { viewModel.updateLeadStatus(lead.id, "Completed") }
                )
            }
        }
    }
}

    if (showManualLeadDialog) {
        ManualLeadCreationDialog(
            onDismiss = { showManualLeadDialog = false },
            onConfirm = { name, loc, partner, trade, title, desc, budget, urgency ->
                viewModel.addNewManualLead(name, loc, partner, trade, title, desc, budget, urgency)
                showManualLeadDialog = false
            }
        )
    }
}

@Composable
fun LeadItemCard(
    lead: LeadItem,
    isEstimating: Boolean,
    onAiEstimate: () -> Unit,
    onMarkBooked: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PartnerBadge(partner = lead.partner)
                Surface(
                    color = when (lead.status.lowercase()) {
                        "new" -> SafetyGold.copy(alpha = 0.2f)
                        "quoted" -> ElectricBlue.copy(alpha = 0.2f)
                        "booked" -> MintGreen.copy(alpha = 0.2f)
                        "completed" -> PurpleAccent.copy(alpha = 0.2f)
                        else -> SlateElevated
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = lead.status.uppercase(),
                        color = when (lead.status.lowercase()) {
                            "new" -> SafetyGold
                            "quoted" -> ElectricBlue
                            "booked" -> MintGreen
                            "completed" -> PurpleAccent
                            else -> TextWhite
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = lead.projectTitle,
                color = TextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = lead.projectDescription,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getTradeIcon(lead.trade),
                        contentDescription = null,
                        tint = SafetyGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(lead.trade.displayName, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(lead.clientLocation, color = TextMuted, fontSize = 11.sp)
                }
                Text(
                    text = lead.customerBudget,
                    color = MintGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAiEstimate,
                    enabled = !isEstimating,
                    modifier = Modifier.weight(1.3f),
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SlateDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Auto-Quote", color = SlateDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (lead.status != "Booked" && lead.status != "Completed") {
                    OutlinedButton(
                        onClick = onMarkBooked,
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MintGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MintGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MintGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Booked", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (lead.status == "Booked") {
                    OutlinedButton(
                        onClick = onMarkCompleted,
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PurpleAccent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAccent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Completed", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ManualLeadCreationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, PartnerType, TradeCategory, String, String, String, String) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Austin, TX") }
    var partner by remember { mutableStateOf(PartnerType.THUMBTACK) }
    var trade by remember { mutableStateOf(TradeCategory.REMODELING) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("$2,500 - $4,000") }
    var urgency by remember { mutableStateOf("Within 2 Weeks") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Inbound Partner Lead", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Project Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget Range") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            clientName.ifBlank { "Homeowner Client" },
                            location,
                            partner,
                            trade,
                            title,
                            desc,
                            budget,
                            urgency
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SafetyGold)
            ) {
                Text("Add Lead to Feed", color = SlateDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        },
        containerColor = SlateCard
    )
}
