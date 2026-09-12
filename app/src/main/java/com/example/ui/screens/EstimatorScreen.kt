package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.EstimateResult
import com.example.data.models.EstimatorModel
import com.example.data.models.EstimatorStyle
import com.example.data.models.PartnerType
import com.example.data.models.TradeCategory
import com.example.ui.components.PartnerBadge
import com.example.ui.components.getTradeIcon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SafetyGold
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.SlateMedium
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.ProTradeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimatorScreen(
    viewModel: ProTradeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val projectTitle by viewModel.projectTitle.collectAsState()
    val selectedTrade by viewModel.selectedTrade.collectAsState()
    val scopeDetails by viewModel.scopeDetails.collectAsState()
    val dimensions by viewModel.dimensionsOrUnits.collectAsState()
    val materialGrade by viewModel.materialGrade.collectAsState()
    val zipCode by viewModel.locationZip.collectAsState()
    val markup by viewModel.markupPercent.collectAsState()
    val contingency by viewModel.contingencyPercent.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val isEstimating by viewModel.isEstimating.collectAsState()
    val estimateResult by viewModel.currentEstimateResult.collectAsState()
    val webhooks by viewModel.webhooks.collectAsState()

    var showModelMenu by remember { mutableStateOf(false) }
    var showStyleMenu by remember { mutableStateOf(false) }
    var showAdvancedParams by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Universal AI Cost Estimator",
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Itemized Takeoff & Bidding across all 5 Partners",
                            color = SafetyGold,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = SafetyGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "AI MODEL & ESTIMATION STYLE",
                        color = TextSubtle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showModelMenu = true },
                                color = SlateElevated,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Model Engine", color = TextMuted, fontSize = 10.sp)
                                        Text(selectedModel.displayName, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = ElectricBlue)
                                }
                            }
                            DropdownMenu(
                                expanded = showModelMenu,
                                onDismissRequest = { showModelMenu = false },
                                modifier = Modifier.background(SlateMedium)
                            ) {
                                EstimatorModel.entries.forEach { model ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(model.displayName, color = TextWhite, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(" ${model.speedTag}", color = MintGreen, fontSize = 11.sp)
                                                }
                                                Text(model.description, color = TextMuted, fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectedModel.value = model
                                            showModelMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showStyleMenu = true },
                                color = SlateElevated,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SafetyGold.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Persona Style", color = TextMuted, fontSize = 10.sp)
                                        Text(selectedStyle.displayName, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SafetyGold)
                                }
                            }
                            DropdownMenu(
                                expanded = showStyleMenu,
                                onDismissRequest = { showStyleMenu = false },
                                modifier = Modifier.background(SlateMedium)
                            ) {
                                EstimatorStyle.entries.forEach { style ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(style.displayName, color = TextWhite, fontWeight = FontWeight.Bold)
                                                Text(style.subtitle, color = TextMuted, fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectedStyle.value = style
                                            showStyleMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column {
                Text(
                    text = "SELECT TRADE / SPECIALTY",
                    color = TextSubtle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TradeCategory.entries) { trade ->
                        val isSelected = trade == selectedTrade
                        Surface(
                            modifier = Modifier.clickable { viewModel.selectedTrade.value = trade },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SafetyGold else SlateElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SafetyGold else SlateBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = getTradeIcon(trade),
                                    contentDescription = null,
                                    tint = if (isSelected) SlateDark else SafetyGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = trade.displayName,
                                    color = if (isSelected) SlateDark else TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = projectTitle,
                        onValueChange = { viewModel.projectTitle.value = it },
                        label = { Text("Project Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SafetyGold,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = SlateElevated,
                            unfocusedContainerColor = SlateElevated
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = scopeDetails,
                        onValueChange = { viewModel.scopeDetails.value = it },
                        label = { Text("Scope Details / Client Specifications") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SafetyGold,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = SlateElevated,
                            unfocusedContainerColor = SlateElevated
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = dimensions,
                            onValueChange = { viewModel.dimensionsOrUnits.value = it },
                            label = { Text("Area / Units (${selectedTrade.typicalUnit})") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SafetyGold,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = SlateElevated,
                                unfocusedContainerColor = SlateElevated
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = zipCode,
                            onValueChange = { viewModel.locationZip.value = it },
                            label = { Text("Zip Code / City") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SafetyGold,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = SlateElevated,
                                unfocusedContainerColor = SlateElevated
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Material Grade", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Economy", "Standard", "Premium", "Ultra-Luxury").forEach { grade ->
                            val isSelected = materialGrade == grade
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.materialGrade.value = grade },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ElectricBlue else SlateElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricBlue else SlateBorder)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = grade,
                                        color = if (isSelected) SlateDark else TextWhite,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAdvancedParams = !showAdvancedParams },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = SafetyGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Contractor Markup & Contingency (${markup}% / ${contingency}%)", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Icon(
                            imageVector = if (showAdvancedParams) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                    AnimatedVisibility(visible = showAdvancedParams) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text("Contractor Markup & Overhead: $markup%", color = TextMuted, fontSize = 11.sp)
                            Slider(
                                value = markup.toFloat(),
                                onValueChange = { viewModel.markupPercent.value = it.toInt() },
                                valueRange = 5f..40f,
                                colors = SliderDefaults.colors(
                                    thumbColor = SafetyGold,
                                    activeTrackColor = SafetyGold,
                                    inactiveTrackColor = SlateElevated
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Safety Contingency Buffer: $contingency%", color = TextMuted, fontSize = 11.sp)
                            Slider(
                                value = contingency.toFloat(),
                                onValueChange = { viewModel.contingencyPercent.value = it.toInt() },
                                valueRange = 0f..25f,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricBlue,
                                    activeTrackColor = ElectricBlue,
                                    inactiveTrackColor = SlateElevated
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.runAiEstimate() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isEstimating,
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isEstimating) {
                            CircularProgressIndicator(color = SlateDark, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generating with ${selectedModel.displayName}...", color = SlateDark, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SlateDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Calculate AI Cost Estimate", color = SlateDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        estimateResult?.let { result ->
            item {
                EstimateResultCard(
                    estimate = result,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Contractor Proposal", result.rawAiProposal)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showMessage("Proposal copied to clipboard!")
                    },
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Project Estimate: ${result.title}")
                            putExtra(Intent.EXTRA_TEXT, result.rawAiProposal)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Estimate Quote"))
                    },
                    onDispatchToPartner = { partner ->
                        viewModel.showMessage("Dispatched formal quote of $${"%,.2f".format(result.targetCost)} to ${partner.displayName}!")
                    },
                    onTriggerWebhook = {
                        val activeHook = webhooks.firstOrNull { it.isEnabled }
                        if (activeHook != null) {
                            viewModel.testWebhookUrl.value = activeHook.url
                            viewModel.runLiveWebhookTest()
                        } else {
                            viewModel.showMessage("Please enable a webhook in the APIs & Webhooks tab first.")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EstimateResultCard(
    estimate: EstimateResult,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onDispatchToPartner: (PartnerType) -> Unit,
    onTriggerWebhook: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SafetyGold)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ESTIMATE BREAKDOWN",
                        color = SafetyGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = estimate.title,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SafetyGold)
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = ElectricBlue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SlateElevated,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Target Contract Bid", color = TextMuted, fontSize = 12.sp)
                            Text(
                                text = "$${"%,.2f".format(estimate.targetCost)}",
                                color = SafetyGold,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Estimated Range", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = "$${"%,.0f".format(estimate.totalMinCost)} - $${"%,.0f".format(estimate.totalMaxCost)}",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Est. Turnaround: ${estimate.estimatedDays} Days", color = MintGreen, fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("FINANCIAL COMPONENT MATRIX", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CostPill(label = "Labor", amount = estimate.laborCost, color = ElectricBlue, modifier = Modifier.weight(1f))
                CostPill(label = "Materials", amount = estimate.materialCost, color = MintGreen, modifier = Modifier.weight(1f))
                CostPill(label = "Permits", amount = estimate.permitsAndFeesCost, color = SafetyGold, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CostPill(label = "Equipment", amount = estimate.equipmentCost, color = TextMuted, modifier = Modifier.weight(1f))
                CostPill(label = "Markup/Profit", amount = estimate.overheadAndProfit, color = PurpleAccent, modifier = Modifier.weight(1f))
                CostPill(label = "Contingency", amount = estimate.contingencyAmount, color = Color(0xFFF97316), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (estimate.lineItems.isNotEmpty()) {
                Text("ITEMIZED WORK SPECIFICATIONS", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                estimate.lineItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("${item.category} • ${item.quantity} ${item.unit} @ $${"%,.2f".format(item.unitCost)}", color = TextMuted, fontSize = 11.sp)
                        }
                        Text(
                            text = "$${"%,.2f".format(item.totalCost)}",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (estimate.milestones.isNotEmpty()) {
                Text("PAYMENT MILESTONE SCHEDULE", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                estimate.milestones.forEach { milestone ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        color = SlateElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(milestone.phase, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(milestone.description, color = TextMuted, fontSize = 10.sp)
                            }
                            Text(
                                "${milestone.percentage}% ($${"%,.2f".format(milestone.amount)})",
                                color = SafetyGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text("DISPATCH TO PARTNER PORTAL", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PartnerType.entries.forEach { partner ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDispatchToPartner(partner) },
                        color = partner.badgeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, partner.badgeColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = partner.badgeColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(partner.brandTag, color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onTriggerWebhook,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Webhook, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Trigger Active Outbound Webhook (Zapier / Make)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CostPill(
    label: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = SlateElevated,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = TextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text("$${"%,.0f".format(amount)}", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
