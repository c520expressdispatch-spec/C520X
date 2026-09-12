package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CustomDocumentTemplate
import com.example.data.SmartTemplatePresets
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SmartCustomTemplateSheetDialog(
    initialTemplate: CustomDocumentTemplate,
    initialDocMode: String = "INVOICE", // "INVOICE" or "ESTIMATE"
    onSaveTemplate: (CustomDocumentTemplate) -> Unit,
    onDismiss: () -> Unit,
    onShowNotice: (String) -> Unit
) {
    var template by remember { mutableStateOf(initialTemplate) }
    var docMode by remember { mutableStateOf(initialDocMode) } // "INVOICE" or "ESTIMATE"
    var activeViewTab by remember { mutableIntStateOf(0) } // 0 = Live Sheet Preview, 1 = Customize Layout & Features

    // Available Accent Colors
    val accentColors = listOf(
        "#E65100" to "Orange Trades",
        "#0D47A1" to "Navy Executive",
        "#1B5E20" to "Forest Green",
        "#37474F" to "Slate Minimal",
        "#B71C1C" to "Ruby Bold"
    )

    fun parseColor(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            OrangePrimary
        }
    }

    val activeAccentColor = parseColor(template.accentColorHex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .testTag("smart_custom_template_sheet_dialog"),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Toolbar
                Surface(
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(activeAccentColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = activeAccentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "SMART TEMPLATE ENGINE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeAccentColor,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Custom Invoice & Estimate Sheets",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    onSaveTemplate(template)
                                    onShowNotice("Active ${template.name} template saved & applied!")
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = activeAccentColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp).testTag("save_active_template_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Apply As Default", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }
                }

                // Sub-Bar: Mode Selector (Invoice vs Estimate), Preset Chips, and Preview vs Edit View
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Doc Type Switcher
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(2.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (docMode == "INVOICE") activeAccentColor else Color.Transparent,
                                    modifier = Modifier.clickable { docMode = "INVOICE" }
                                ) {
                                    Text(
                                        text = "📄 Invoice Sheet",
                                        fontSize = 11.sp,
                                        fontWeight = if (docMode == "INVOICE") FontWeight.Bold else FontWeight.Normal,
                                        color = if (docMode == "INVOICE") Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (docMode == "ESTIMATE") activeAccentColor else Color.Transparent,
                                    modifier = Modifier.clickable { docMode = "ESTIMATE" }
                                ) {
                                    Text(
                                        text = "📋 Estimate & Proposal",
                                        fontSize = 11.sp,
                                        fontWeight = if (docMode == "ESTIMATE") FontWeight.Bold else FontWeight.Normal,
                                        color = if (docMode == "ESTIMATE") Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            // View Tab: Sheet Preview vs Layout Customizer
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(2.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (activeViewTab == 0) Navy700 else Color.Transparent,
                                    modifier = Modifier.clickable { activeViewTab = 0 }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            tint = if (activeViewTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Sheet Preview",
                                            fontSize = 11.sp,
                                            fontWeight = if (activeViewTab == 0) FontWeight.Bold else FontWeight.Normal,
                                            color = if (activeViewTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (activeViewTab == 1) Navy700 else Color.Transparent,
                                    modifier = Modifier.clickable { activeViewTab = 1 }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            tint = if (activeViewTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Customize Layout",
                                            fontSize = 11.sp,
                                            fontWeight = if (activeViewTab == 1) FontWeight.Bold else FontWeight.Normal,
                                            color = if (activeViewTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Template Presets Chips Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Smart Presets:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            SmartTemplatePresets.ALL_PRESETS.forEach { preset ->
                                val isSelected = template.id == preset.id
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) activeAccentColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                    modifier = Modifier.clickable {
                                        template = preset
                                    }
                                ) {
                                    Text(
                                        text = preset.name,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Main Content Body: Switch between Sheet Preview and Customizer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (activeViewTab == 0) {
                        // LIVE SHEET PREVIEW (Professional Document Representation)
                        LiveRenderedDocumentSheet(
                            template = template,
                            docMode = docMode,
                            accentColor = activeAccentColor,
                            onPrintOrExport = {
                                onShowNotice("Document exported to PDF! Saved to device storage.")
                            },
                            onShareLink = {
                                onShowNotice("Direct Stripe payment & proposal link copied to clipboard!")
                            }
                        )
                    } else {
                        // LAYOUT & FEATURES CUSTOMIZER
                        TemplateLayoutCustomizerPanel(
                            template = template,
                            accentColors = accentColors,
                            onTemplateChanged = { updated ->
                                template = updated
                            },
                            onResetDefaults = {
                                template = SmartTemplatePresets.CONTRACTOR_TRADES_PRO
                                onShowNotice("Reset to Contractor Trades Pro defaults.")
                            }
                        )
                    }
                }
            }
        }
    }
}

// RENDERED SMART DOCUMENT SHEET
@Composable
private fun LiveRenderedDocumentSheet(
    template: CustomDocumentTemplate,
    docMode: String, // "INVOICE" or "ESTIMATE"
    accentColor: Color,
    onPrintOrExport: () -> Unit,
    onShareLink: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val isInvoice = docMode == "INVOICE"
    val docNumber = if (isInvoice) "INV-2026-0894" else "EST-2026-0142"
    val docTitle = if (isInvoice) "TAX INVOICE" else "PROJECT ESTIMATE & PROPOSAL"
    val clientName = "Catalina Vista Properties LLC"
    val clientContact = "Attention: Marcus Holloway • (520) 881-4902"
    val projectAddress = "4200 E Sunrise Dr, Suite B, Tucson, AZ 85718"
    val issueDate = remember { SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date()) }
    val dueDate = "September 28, 2026"

    // Sample Itemized Rows
    val sampleItems = listOf(
        Triple("Master Electrical Service Upgrade (200A to 400A)", "Includes dual meter base, surge protector & copper grounding grid", 3400.0),
        Triple("Dedicated 240V Circuit Run & Commercial Subpanel", "80-ft conduit run through attic drop ceiling with NEMA 14-50", 1250.0),
        Triple("LED Recessed Architectural Luminaires (18 units)", "Zero-flicker commercial high-CRI dimmable drivers & rough-in trims", 980.0),
        Triple("City Inspection, Safety Permits & Utility Coordination", "Local jurisdiction engineering compliance & meter lock re-tag", 450.0)
    )

    val subtotal = sampleItems.sumOf { it.third }
    val taxRate = template.defaultTaxRatePercent / 100.0
    val taxAmount = if (template.showTaxLine) subtotal * taxRate else 0.0
    val grossTotal = subtotal + taxAmount
    val depositPercent = template.defaultDepositPercent / 100.0
    val depositAmount = if (template.showDepositDue) grossTotal * depositPercent else 0.0
    val balanceRemaining = grossTotal - depositAmount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9)) // Light blueprint canvas background
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Floating action buttons above document
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onShareLink,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Direct Link", fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onPrintOrExport,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export PDF Sheet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // WHITE PAPER DOCUMENT CARD
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rendered_paper_document")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Top Color Accent Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(accentColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // DOCUMENT HEADER: Company Info on Left, Document Title & Meta on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Company Info
                    Column(modifier = Modifier.weight(1f)) {
                        if (template.showLogoBadge) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(accentColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = template.companyName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        } else {
                            Text(
                                text = template.companyName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = template.companySlogan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${template.companyAddress} • ${template.companyPhone}",
                            fontSize = 10.sp,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = template.companyEmail,
                            fontSize = 10.sp,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (template.showLicenseBondBadge && template.licenseNumber.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = accentColor.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = template.licenseNumber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Document Badge & Meta
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor
                        ) {
                            Text(
                                text = docTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = docNumber,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF0F172A)
                        )

                        Text(
                            text = "Date: $issueDate",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = if (isInvoice) "Payment Due: $dueDate" else "Valid Through: 30 Days",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isInvoice) StatusAmber else Color(0xFF64748B)
                        )
                        if (template.taxRegistrationNumber.isNotBlank()) {
                            Text(
                                text = template.taxRegistrationNumber,
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(14.dp))

                // BILLED TO / PROJECT SCOPE LOCATION
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isInvoice) "BILLED TO:" else "PREPARED FOR:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = clientName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(text = clientContact, fontSize = 10.sp, color = Color(0xFF475569))
                        Text(text = projectAddress, fontSize = 10.sp, color = Color(0xFF475569))
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROJECT DESIGNATION:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Valencia Logistics Hub Expansion", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(text = "Division: Commercial Trades", fontSize = 10.sp, color = Color(0xFF475569))
                        Text(text = "Lead Tech: Alex Ramirez", fontSize = 10.sp, color = Color(0xFF475569))
                    }
                }

                // OPTIONAL SCOPE OF WORK NARRATIVE
                if (template.showScopeOfWork) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "SCOPE OF WORK & DELIVERABLES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Contractor shall furnish all labor, supervision, specialized tools, and commercial electrical components required to upgrade service entrance to 400-Amp dual meter capacity and wire interior high-bay circuits per NEC 2024 standards.",
                                fontSize = 10.sp,
                                color = Color(0xFF334155),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // ITEMIZED LINE ITEMS TABLE
                if (template.showItemizedBreakdown) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Table Header Row
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ITEM DESCRIPTION", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF475569), modifier = Modifier.weight(3f))
                            Text("QTY / RATE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF475569), textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
                            Text("AMOUNT", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    sampleItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(3f)) {
                                Text(item.first, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                                Text(item.second, fontSize = 9.sp, color = Color(0xFF64748B))
                            }
                            Text(
                                text = if (template.showUnitPrices) "1.0 @ ${currencyFormat.format(item.third)}" else "Lump Sum",
                                fontSize = 10.sp,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = currencyFormat.format(item.third),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1.2f)
                            )
                        }
                        if (index < sampleItems.size - 1) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // FINANCIAL TOTALS SUMMARY (Right aligned box)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Notes & Terms
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(
                            text = "PAYMENT TERMS & INSTRUCTIONS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = template.paymentTermsText,
                            fontSize = 10.sp,
                            color = Color(0xFF334155),
                            lineHeight = 14.sp
                        )

                        if (template.warrantyNotesText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "WARRANTY:",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                            Text(
                                text = template.warrantyNotesText,
                                fontSize = 9.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right Totals Breakdown
                    Column(
                        modifier = Modifier.weight(1.2f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(currencyFormat.format(subtotal), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                        }

                        if (template.showTaxLine) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sales Tax (${template.defaultTaxRatePercent}%):", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(currencyFormat.format(taxAmount), fontSize = 11.sp, color = Color(0xFF0F172A))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL AMOUNT:", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                            Text(
                                text = currencyFormat.format(grossTotal),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = accentColor
                            )
                        }

                        if (template.showDepositDue && depositAmount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Upfront Deposit (${template.defaultDepositPercent.toInt()}%):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                                Text(currencyFormat.format(depositAmount), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Balance on Signoff:", fontSize = 10.sp, color = Color(0xFF64748B))
                                Text(currencyFormat.format(balanceRemaining), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            }
                        }
                    }
                }

                // OPTIONAL STRIPE INSTANT CHECKOUT QR STUB
                if (template.showStripePaymentLink) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Navy900),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "⚡ STRIPE CONNECT INSTANT CHECKOUT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusGreen
                                    )
                                    Text(
                                        text = "Scan or tap to pay securely via Credit Card, Apple Pay, or Bank ACH",
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "https://pay.c520express.com/checkout/$docNumber",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = accentColor
                                    )
                                }
                            }

                            Button(
                                onClick = onShareLink,
                                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Pay Now", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // OPTIONAL CLIENT SIGNATURE LINE
                if (template.showClientSignatureLine) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(1.dp)
                                    .background(Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("Authorized Client Signature", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                            Text("I hereby accept the specifications & pricing above.", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        }

                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(1.dp)
                                    .background(Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("Date Approved", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                            Text("MM / DD / YYYY", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
        }
    }
}

// LAYOUT & FEATURES CUSTOMIZER PANEL
@Composable
private fun TemplateLayoutCustomizerPanel(
    template: CustomDocumentTemplate,
    accentColors: List<Pair<String, String>>,
    onTemplateChanged: (CustomDocumentTemplate) -> Unit,
    onResetDefaults: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section: Template Identity & Color Accent
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BRANDING & COLOR PALETTE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary,
                        letterSpacing = 0.5.sp
                    )

                    TextButton(onClick = onResetDefaults) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Defaults", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Color selector chips
                Text("Select Accent Theme Color:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accentColors.forEach { (hex, label) ->
                        val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { OrangePrimary }
                        val isSelected = template.accentColorHex.equals(hex, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = color.copy(alpha = if (isSelected) 1f else 0.2f),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else BorderStroke(1.dp, color),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onTemplateChanged(template.copy(accentColorHex = hex))
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label.split(" ").first(),
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Company Info & Credentials
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "COMPANY DETAILS & LICENSING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = template.companyName,
                    onValueChange = { onTemplateChanged(template.copy(companyName = it)) },
                    label = { Text("Company Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = template.companySlogan,
                    onValueChange = { onTemplateChanged(template.copy(companySlogan = it)) },
                    label = { Text("Company Tagline / Subtitle") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = template.companyPhone,
                        onValueChange = { onTemplateChanged(template.copy(companyPhone = it)) },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = template.companyEmail,
                        onValueChange = { onTemplateChanged(template.copy(companyEmail = it)) },
                        label = { Text("Billing Email") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = template.companyAddress,
                    onValueChange = { onTemplateChanged(template.copy(companyAddress = it)) },
                    label = { Text("Office Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = template.licenseNumber,
                    onValueChange = { onTemplateChanged(template.copy(licenseNumber = it)) },
                    label = { Text("Contractor License / DOT # / Bond Seal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Section: Modular Layout Feature Toggles
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "MODULAR SHEET LAYOUT SECTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LayoutToggleRow(
                    title = "Company Shield & Logo Badge",
                    desc = "Display verified security shield logo at the top left header",
                    checked = template.showLogoBadge,
                    onCheckedChange = { onTemplateChanged(template.copy(showLogoBadge = it)) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                LayoutToggleRow(
                    title = "Scope of Work Narrative",
                    desc = "Show dedicated project narrative and engineering specifications block",
                    checked = template.showScopeOfWork,
                    onCheckedChange = { onTemplateChanged(template.copy(showScopeOfWork = it)) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                LayoutToggleRow(
                    title = "Itemized Cost Breakdown Table",
                    desc = "Show individual itemized rows with quantities and line totals",
                    checked = template.showItemizedBreakdown,
                    onCheckedChange = { onTemplateChanged(template.copy(showItemizedBreakdown = it)) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                LayoutToggleRow(
                    title = "Unit Prices & Rates Column",
                    desc = "Show individual unit prices (or lump sum only)",
                    checked = template.showUnitPrices,
                    onCheckedChange = { onTemplateChanged(template.copy(showUnitPrices = it)) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                LayoutToggleRow(
                    title = "State Sales Tax Calculation",
                    desc = "Apply and display configurable sales tax calculation",
                    checked = template.showTaxLine,
                    onCheckedChange = { onTemplateChanged(template.copy(showTaxLine = it)) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                LayoutToggleRow(
                    title = "Upfront Escrow Deposit Due",
                    desc = "Display mobilization deposit requirement and balance remaining",
                    checked = template.showDepositDue,
                    onCheckedChange = { onTemplateChanged(template.copy(showDepositDue = it)) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                LayoutToggleRow(
                    title = "Stripe Connect Pay & QR Code Stub",
                    desc = "Include instant payment button and scan-to-pay QR voucher",
                    checked = template.showStripePaymentLink,
                    onCheckedChange = { onTemplateChanged(template.copy(showStripePaymentLink = it)) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                LayoutToggleRow(
                    title = "Client Acceptance & Signature Line",
                    desc = "Provide legally binding approval line for client sign-off",
                    checked = template.showClientSignatureLine,
                    onCheckedChange = { onTemplateChanged(template.copy(showClientSignatureLine = it)) }
                )
            }
        }

        // Section: Terms, Rates & Warranty Notes
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "TERMS, TAX RATES & WARRANTY CLAUSES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = template.defaultTaxRatePercent.toString(),
                        onValueChange = { str ->
                            str.toDoubleOrNull()?.let { num ->
                                onTemplateChanged(template.copy(defaultTaxRatePercent = num))
                            }
                        },
                        label = { Text("Tax Rate (%)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = template.defaultDepositPercent.toString(),
                        onValueChange = { str ->
                            str.toDoubleOrNull()?.let { num ->
                                onTemplateChanged(template.copy(defaultDepositPercent = num))
                            }
                        },
                        label = { Text("Deposit (%)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = template.paymentTermsText,
                    onValueChange = { onTemplateChanged(template.copy(paymentTermsText = it)) },
                    label = { Text("Payment Terms & Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = template.warrantyNotesText,
                    onValueChange = { onTemplateChanged(template.copy(warrantyNotesText = it)) },
                    label = { Text("Craftsmanship Warranty Clause") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun LayoutToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary, checkedTrackColor = OrangePrimary.copy(alpha = 0.4f))
        )
    }
}
