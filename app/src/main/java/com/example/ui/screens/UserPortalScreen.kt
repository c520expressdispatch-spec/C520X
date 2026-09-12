package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.UserRole
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun UserPortalScreen(viewModel: MainViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val contractorProfile by viewModel.contractorProfile.collectAsState()
    val ownerOpProfile by viewModel.ownerOperatorProfile.collectAsState()
    val clientProfile by viewModel.clientProfile.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("user_portal_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Portal Header Banner
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("user_portal_header")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (currentRole) {
                                    UserRole.CONTRACTOR -> Icons.Default.Handyman
                                    UserRole.OWNER_OPERATOR -> Icons.Default.LocalShipping
                                    UserRole.CLIENT -> Icons.Default.Business
                                    else -> Icons.Default.Security
                                },
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MY ADMIN & PROFILE SETTINGS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "VERIFIED PRO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (currentRole) {
                            UserRole.CONTRACTOR -> contractorProfile.businessName
                            UserRole.OWNER_OPERATOR -> ownerOpProfile.carrierName
                            UserRole.CLIENT -> clientProfile.companyName
                            else -> "My Organization Portal"
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (currentRole) {
                            UserRole.CONTRACTOR -> "Manage your contracting business, trade licenses, general liability insurance, 80% Stripe Connect payouts, and apprentice crew."
                            UserRole.OWNER_OPERATOR -> "Manage your trucking carrier MC#/DOT#, equipment specifications, 24-hr QuickPay factoring bank, and lane preferences."
                            UserRole.CLIENT -> "Manage your corporate billing profile, authorized signers, payment method on file, and milestone escrow funding."
                            else -> "Your private portal settings are isolated and securely maintained."
                        },
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        when (currentRole) {
            UserRole.CONTRACTOR, UserRole.LEAD_TECH -> {
                item {
                    ContractorAdminSection(
                        profile = contractorProfile,
                        onSave = { viewModel.updateContractorProfile(it) }
                    )
                }
            }
            UserRole.OWNER_OPERATOR, UserRole.DISPATCHER -> {
                item {
                    OwnerOperatorAdminSection(
                        profile = ownerOpProfile,
                        onSave = { viewModel.updateOwnerOperatorProfile(it) }
                    )
                }
            }
            UserRole.CLIENT -> {
                item {
                    ClientAdminSection(
                        profile = clientProfile,
                        onSave = { viewModel.updateClientProfile(it) }
                    )
                }
            }
            else -> {
                // Master Admin view also allows previewing contractor or driver portal
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        ContractorAdminSection(
                            profile = contractorProfile,
                            onSave = { viewModel.updateContractorProfile(it) }
                        )
                        OwnerOperatorAdminSection(
                            profile = ownerOpProfile,
                            onSave = { viewModel.updateOwnerOperatorProfile(it) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ContractorAdminSection(
    profile: com.example.ui.ContractorProfile,
    onSave: (com.example.ui.ContractorProfile) -> Unit
) {
    var businessName by remember(profile) { mutableStateOf(profile.businessName) }
    var contactName by remember(profile) { mutableStateOf(profile.contactName) }
    var tradeCategory by remember(profile) { mutableStateOf(profile.tradeCategory) }
    var licenseNumber by remember(profile) { mutableStateOf(profile.licenseNumber) }
    var liabilityInsurance by remember(profile) { mutableStateOf(profile.liabilityInsuranceCarrier) }
    var hourlyLaborRate by remember(profile) { mutableStateOf(profile.hourlyLaborRate.toString()) }
    var serviceRadius by remember(profile) { mutableStateOf(profile.serviceRadiusMiles.toString()) }
    var emergencyService by remember(profile) { mutableStateOf(profile.emergencyServiceAvailable) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth().testTag("contractor_admin_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Handyman, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tradesman Business Profile & Credentials", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = OrangePrimary.copy(alpha = 0.15f)
                ) {
                    Text("80% Take-Home Rate", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("Business / LLC Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("Lead Pro Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = tradeCategory,
                    onValueChange = { tradeCategory = it },
                    label = { Text("Primary Trade") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("Contractor License #") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = hourlyLaborRate,
                    onValueChange = { hourlyLaborRate = it },
                    label = { Text("Hourly Labor Rate ($/hr)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = liabilityInsurance,
                onValueChange = { liabilityInsurance = it },
                label = { Text("General Liability Insurance Carrier & Policy") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stripe Connect Payout Settings
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Stripe Connect Direct Payouts", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Text("ACTIVE & LINKED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Payout Destination: ${profile.stripePayoutAccount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Platform Split: 80% disbursed directly into this bank account upon client milestone approval.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("24/7 Emergency Dispatch Calls", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Receive high-margin urgent repair dispatches", fontSize = 10.sp, color = Color.Gray)
                }
                Switch(
                    checked = emergencyService,
                    onCheckedChange = { emergencyService = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    onSave(
                        profile.copy(
                            businessName = businessName,
                            contactName = contactName,
                            tradeCategory = tradeCategory,
                            licenseNumber = licenseNumber,
                            liabilityInsuranceCarrier = liabilityInsurance,
                            hourlyLaborRate = hourlyLaborRate.toDoubleOrNull() ?: profile.hourlyLaborRate,
                            serviceRadiusMiles = serviceRadius.toIntOrNull() ?: profile.serviceRadiusMiles,
                            emergencyServiceAvailable = emergencyService
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().testTag("save_contractor_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Contractor Settings", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OwnerOperatorAdminSection(
    profile: com.example.ui.OwnerOperatorProfile,
    onSave: (com.example.ui.OwnerOperatorProfile) -> Unit
) {
    var carrierName by remember(profile) { mutableStateOf(profile.carrierName) }
    var driverName by remember(profile) { mutableStateOf(profile.driverName) }
    var usdotNumber by remember(profile) { mutableStateOf(profile.usdotNumber) }
    var mcNumber by remember(profile) { mutableStateOf(profile.mcNumber) }
    var equipmentType by remember(profile) { mutableStateOf(profile.equipmentType) }
    var eldProvider by remember(profile) { mutableStateOf(profile.eldProvider) }
    var minRatePerMile by remember(profile) { mutableStateOf(profile.minRatePerMile.toString()) }
    var preferredLanes by remember(profile) { mutableStateOf(profile.preferredLanes) }
    var quickPayEnabled by remember(profile) { mutableStateOf(profile.quickPayEnabled) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth().testTag("owner_operator_admin_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Owner-Operator Carrier & Equipment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = StatusBlue.copy(alpha = 0.15f)
                ) {
                    Text("88-93% Gross Payout", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusBlue, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = carrierName,
                onValueChange = { carrierName = it },
                label = { Text("Carrier / Trucking Company Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = usdotNumber,
                    onValueChange = { usdotNumber = it },
                    label = { Text("USDOT #") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = mcNumber,
                    onValueChange = { mcNumber = it },
                    label = { Text("MC / FF #") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = equipmentType,
                    onValueChange = { equipmentType = it },
                    label = { Text("Equipment (Dry Van, Reefer, Flat)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = minRatePerMile,
                    onValueChange = { minRatePerMile = it },
                    label = { Text("Min $/Mile Filter") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = preferredLanes,
                onValueChange = { preferredLanes = it },
                label = { Text("Preferred Freight Lanes & Regions") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // QuickPay Factoring Settlement
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusBlue.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Direct Deposit / 24-Hr QuickPay", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Text("VERIFIED ACH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Bank Account: ${profile.quickPayBank}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Payout terms: Released within 24 hours of signed Delivery Bill of Lading (BOL).", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    onSave(
                        profile.copy(
                            carrierName = carrierName,
                            driverName = driverName,
                            usdotNumber = usdotNumber,
                            mcNumber = mcNumber,
                            equipmentType = equipmentType,
                            eldProvider = eldProvider,
                            preferredLanes = preferredLanes,
                            minRatePerMile = minRatePerMile.toDoubleOrNull() ?: profile.minRatePerMile,
                            quickPayEnabled = quickPayEnabled
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().testTag("save_owner_operator_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = StatusBlue)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Carrier & Truck Settings", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ClientAdminSection(
    profile: com.example.ui.ClientProfile,
    onSave: (com.example.ui.ClientProfile) -> Unit
) {
    var companyName by remember(profile) { mutableStateOf(profile.companyName) }
    var billingContact by remember(profile) { mutableStateOf(profile.billingContact) }
    var taxIdEin by remember(profile) { mutableStateOf(profile.taxIdEin) }
    var paymentMethod by remember(profile) { mutableStateOf(profile.paymentMethod) }
    var authorizedSigners by remember(profile) { mutableStateOf(profile.authorizedSigners) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth().testTag("client_admin_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Commercial Client Billing & Escrow Profile", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Corporate Entity Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = billingContact,
                    onValueChange = { billingContact = it },
                    label = { Text("Billing Contact") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = taxIdEin,
                    onValueChange = { taxIdEin = it },
                    label = { Text("Federal EIN / Tax ID") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = paymentMethod,
                onValueChange = { paymentMethod = it },
                label = { Text("Primary Payment Method on File") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = authorizedSigners,
                onValueChange = { authorizedSigners = it },
                label = { Text("Authorized Project Signers") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    onSave(
                        profile.copy(
                            companyName = companyName,
                            billingContact = billingContact,
                            taxIdEin = taxIdEin,
                            paymentMethod = paymentMethod,
                            authorizedSigners = authorizedSigners
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Client Profile", fontWeight = FontWeight.Bold)
            }
        }
    }
}
