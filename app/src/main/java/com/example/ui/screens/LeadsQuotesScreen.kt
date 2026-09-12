package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.LeadEntity
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.LocationPermissionCard
import com.example.ui.components.RealtimeCustomerInquiryHubCard
import com.example.ui.components.ThumbtackLeadCanvasComponent
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.Navy700
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple

@Composable
fun LeadsQuotesScreen(viewModel: MainViewModel) {
    val leads by viewModel.leads.collectAsState()

    var activeLeadForQuote by remember { mutableStateOf<LeadEntity?>(null) }
    var showNewLeadDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().testTag("leads_quotes_screen")) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Primary Canvas Layout (Thumbtack Lead Log & Pipeline)
                ThumbtackLeadCanvasComponent(
                    onConvertToJob = { canvasLead ->
                        viewModel.addJob(
                            title = "${canvasLead.serviceRequested} - ${canvasLead.customerName}",
                            trade = canvasLead.serviceRequested,
                            clientName = canvasLead.customerName,
                            phone = canvasLead.contactDetail.ifEmpty { "(520) 555-0199" },
                            address = "${canvasLead.city}, AZ ${canvasLead.zipCode}",
                            status = "Dispatched",
                            pipelineValue = canvasLead.quoteAmount.replace("$", "").replace(",", "").toDoubleOrNull() ?: 3500.0,
                            assignedTech = "Marcus Vance (Lead Tech)",
                            notes = "${canvasLead.projectScope}\n${canvasLead.notes}"
                        )
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Banner
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "THUMBTACK & TASKRABBIT LEADS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = OrangePrimary,
                                letterSpacing = 0.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StatusGreen.copy(alpha = 0.15f),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = "LIVE API FEED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Instant Lead Response & Quick Quote Engine",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Generate professional itemized estimates in 30 seconds and convert leads directly into dispatched jobs.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = com.example.ui.theme.OrangePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Smart Custom Proposal Sheet Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Button(
                                onClick = { viewModel.openCustomTemplateDialog("ESTIMATE") },
                                colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.OrangePrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp).testTag("customize_estimate_template_btn")
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Customize Sheet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                RealtimeCustomerInquiryHubCard(viewModel = viewModel)
            }

            item {
                LocationPermissionCard(
                    viewModel = viewModel,
                    title = "Territory Lead Dispatch & Matching",
                    description = "Filter commercial and residential inquiries to your active service zip codes."
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE INBOUND LEADS (${leads.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )

                    Button(
                        onClick = {
                            activeLeadForQuote = leads.firstOrNull() ?: LeadEntity(
                                clientName = "Commercial Client",
                                clientPhone = "(520) 555-0188",
                                serviceTrade = "Electrical",
                                projectDescription = "Custom Facility Scope",
                                budget = 1500.0,
                                source = "Direct Dispatch",
                                status = "New Lead"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("open_quote_calc_button")
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quote Calculator", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (leads.isEmpty()) {
                item {
                    Text("No leads currently in queue.", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                }
            } else {
                items(leads, key = { it.id }) { lead ->
                    LeadCardItem(
                        lead = lead,
                        onGenerateQuote = { activeLeadForQuote = lead },
                        onChat = { viewModel.selectTab(AppTab.CHAT_SLACK) },
                        onUpdateStatus = { st -> viewModel.updateLeadStatus(lead.id, st) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        FloatingActionButton(
            onClick = { showNewLeadDialog = true },
            containerColor = OrangePrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_lead_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Lead")
        }
    }

    // Quick Quote Generator Dialog
    activeLeadForQuote?.let { lead ->
        QuickQuoteDialog(
            lead = lead,
            onDismiss = { activeLeadForQuote = null },
            onSendAndBook = { quoteTotal ->
                viewModel.convertLeadToJob(lead, quoteTotal)
                activeLeadForQuote = null
            }
        )
    }

    if (showNewLeadDialog) {
        NewLeadDialog(
            onDismiss = { showNewLeadDialog = false },
            onSave = { name, phone, trade, desc, budget, source, urgency ->
                viewModel.addLead(name, phone, trade, desc, budget, source, urgency)
                showNewLeadDialog = false
            }
        )
    }
}

@Composable
fun LeadCardItem(
    lead: LeadEntity,
    onGenerateQuote: () -> Unit,
    onChat: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth().testTag("lead_item_${lead.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source badge (Thumbtack / TaskRabbit)
                val sourceColor = when (lead.source) {
                    "Thumbtack Pro" -> AccentBlue
                    "TaskRabbit" -> StatusGreen
                    "Yelp" -> StatusAmber
                    else -> OrangePrimary
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lead.source,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = sourceColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(sourceColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = lead.serviceTrade,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (lead.status) {
                        "Booked" -> StatusGreen.copy(alpha = 0.15f)
                        "Quoted" -> StatusPurple.copy(alpha = 0.15f)
                        "Contacted" -> AccentCyan.copy(alpha = 0.15f)
                        else -> StatusAmber.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = lead.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (lead.status) {
                            "Booked" -> StatusGreen
                            "Quoted" -> StatusPurple
                            "Contacted" -> AccentCyan
                            else -> StatusAmber
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = lead.clientName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = lead.projectDescription,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Client Budget: $${String.format("%,.0f", lead.budget)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = StatusGreen
                    )
                    Text(
                        text = "Urgency: ${lead.urgency} • ${lead.clientPhone}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onChat,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat", fontSize = 10.sp)
                    }

                    Button(
                        onClick = onGenerateQuote,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("generate_quote_button_${lead.id}")
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Instant Quote", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickQuoteDialog(
    lead: LeadEntity,
    onDismiss: () -> Unit,
    onSendAndBook: (Double) -> Unit
) {
    var laborHours by remember { mutableStateOf("4.5") }
    var hourlyRate by remember { mutableStateOf("125") }
    var materialsCost by remember { mutableStateOf("450") }
    var permitFee by remember { mutableStateOf("85") }
    var taxRate by remember { mutableStateOf("8.6") }

    val laborTotal = (laborHours.toDoubleOrNull() ?: 0.0) * (hourlyRate.toDoubleOrNull() ?: 0.0)
    val matTotal = materialsCost.toDoubleOrNull() ?: 0.0
    val permitTotal = permitFee.toDoubleOrNull() ?: 0.0
    val subtotal = laborTotal + matTotal + permitTotal
    val taxTotal = subtotal * ((taxRate.toDoubleOrNull() ?: 0.0) / 100.0)
    val grandTotal = subtotal + taxTotal

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("quick_quote_dialog")
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Instant Quote Generator", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("For ${lead.clientName} (${lead.serviceTrade})", fontSize = 11.sp, color = OrangePrimary)
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = OrangePrimary.copy(alpha = 0.15f)
                        ) {
                            Text("C520X ENGINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OrangePrimary, modifier = Modifier.padding(4.dp))
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = laborHours,
                            onValueChange = { laborHours = it },
                            label = { Text("Est. Hours", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = hourlyRate,
                            onValueChange = { hourlyRate = it },
                            label = { Text("Labor Rate ($/h)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = materialsCost,
                            onValueChange = { materialsCost = it },
                            label = { Text("Materials & Parts ($)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = permitFee,
                            onValueChange = { permitFee = it },
                            label = { Text("Permit/Equip ($)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    // Itemized calculation preview
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("ITEMIZED ESTIMATE BREAKDOWN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Labor ($laborHours hrs @ $$hourlyRate/h):", fontSize = 11.sp)
                                Text("$${String.format("%.2f", laborTotal)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Materials & Equipment:", fontSize = 11.sp)
                                Text("$${String.format("%.2f", matTotal + permitTotal)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Sales Tax ($taxRate%):", fontSize = 11.sp)
                                Text("$${String.format("%.2f", taxTotal)}", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Quote Total:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("$${String.format("%,.2f", grandTotal)}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = StatusGreen)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Button(
                            onClick = { onSendAndBook(grandTotal) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            modifier = Modifier.testTag("send_quote_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send & Book Job")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewLeadDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Double, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var trade by remember { mutableStateOf("Electrical") }
    var desc by remember { mutableStateOf("") }
    var budgetText by remember { mutableStateOf("1200") }
    var source by remember { mutableStateOf("Thumbtack Pro") }
    var urgency by remember { mutableStateOf("Immediate") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Capture Inbound Mobile Lead", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                item {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Client Name") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Project Need / Problem") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = budgetText, onValueChange = { budgetText = it }, label = { Text("Budget ($)") }, modifier = Modifier.fillMaxWidth())
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Button(
                            onClick = {
                                val b = budgetText.toDoubleOrNull() ?: 1000.0
                                onSave(name.ifBlank { "Client" }, phone.ifBlank { "(520) 555-0100" }, trade, desc.ifBlank { "Service Inquiry" }, b, source, urgency)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Save Inbound Lead")
                        }
                    }
                }
            }
        }
    }
}
