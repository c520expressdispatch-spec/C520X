package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed

/**
 * Embedded Google AI Studio Backend Hub.
 * Controls autonomous Gemini models, multimodal photo auditing, instant estimate triggers,
 * and system directives directly on device install.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoogleAiStudioHubCard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val aiConfig by viewModel.aiStudioConfig.collectAsState()
    val executionLogs by viewModel.aiStudioExecutionLogs.collectAsState()
    val sandboxResult by viewModel.aiStudioSandboxResult.collectAsState()
    val isRunning by viewModel.isAiStudioRunning.collectAsState()

    var customKeyInput by remember { mutableStateOf(aiConfig.customApiKey) }
    var showKeyText by remember { mutableStateOf(false) }
    var promptInput by remember { mutableStateOf("Generate Good/Better/Best estimate for 200A electrical service upgrade in Tucson 85718 with 80% contractor split.") }
    var directiveInput by remember { mutableStateOf(aiConfig.systemDirective) }
    var isEditingDirective by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .fillMaxWidth()
            .testTag("google_ai_studio_hub_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: AI Studio Engine Branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = StatusPurple.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = StatusPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Google AI Studio Backend Hub",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StatusGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "EMBEDDED NATIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Autonomous on-device intelligence for estimates, site audits & 80/20 escrow",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Model Selector Chips
            Text(
                text = "ACTIVE GEMINI MODEL ENGINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("gemini-3.5-flash", "Flash (Fast Dispatch & Estimates)"),
                    Pair("gemini-3.1-pro-preview", "Pro (Deep Code & Permits)"),
                    Pair("gemini-3.1-flash-lite-preview", "Flash Lite (<200ms Telemetry)")
                ).forEach { (modelKey, label) ->
                    FilterChip(
                        selected = aiConfig.selectedModel == modelKey,
                        onClick = { viewModel.updateAiStudioModel(modelKey) },
                        label = {
                            Column(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text(modelKey, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(label, fontSize = 9.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusPurple.copy(alpha = 0.2f),
                            selectedLabelColor = StatusPurple
                        ),
                        modifier = Modifier.testTag("model_chip_$modelKey")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Metrics & Telemetry Bar
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tokens Processed", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${aiConfig.totalTokensProcessed}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Active Engine", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(aiConfig.selectedModel.take(14), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusPurple)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Est. API Usage", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${"%.3f".format(aiConfig.monthlyApiCostEstimate)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. API Key & Security Vault Overrides
            Surface(
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder(),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Google AI Studio API Key (Master Secure Vault)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (aiConfig.customApiKey.isNotBlank()) "Custom Key Set" else "BuildConfig Default",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (aiConfig.customApiKey.isNotBlank()) StatusGreen else StatusBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your API key is used directly by the installed APK to execute Gemini operations with zero third-party middleware.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customKeyInput,
                            onValueChange = { customKeyInput = it },
                            placeholder = { Text("Paste AIzaSy... key to override", fontSize = 11.sp) },
                            visualTransformation = if (showKeyText) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showKeyText = !showKeyText }, modifier = Modifier.size(28.dp)) {
                                    Icon(
                                        imageVector = if (showKeyText) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Visibility",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_studio_api_key_input")
                        )
                        Button(
                            onClick = { viewModel.updateAiStudioApiKey(customKeyInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_ai_studio_key_btn")
                        ) {
                            Text("Save Key", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Autonomous Background Triggers ("Do All This On Install")
            Text(
                text = "AUTONOMOUS BACKGROUND TRIGGERS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AutonomousTriggerRow(
                    title = "Multimodal Site Photo Audit",
                    description = "Auto-analyzes technician camera photos for NEC 2026/OSHA compliance prior to escrow milestone release",
                    icon = Icons.Default.Engineering,
                    iconTint = OrangePrimary,
                    isChecked = aiConfig.autoAuditSitePhotos,
                    onToggle = { viewModel.toggleAiStudioFeature("autoAuditSitePhotos") },
                    testTag = "toggle_auto_audit_photos"
                )

                AutonomousTriggerRow(
                    title = "Instant Auto-Estimate Engine",
                    description = "Translates incoming homeowner & commercial inquiries into 3-tier Good/Better/Best estimates within 30s",
                    icon = Icons.Default.Bolt,
                    iconTint = StatusBlue,
                    isChecked = aiConfig.autoGenerateQuotes,
                    onToggle = { viewModel.toggleAiStudioFeature("autoGenerateQuotes") },
                    testTag = "toggle_auto_quotes"
                )

                AutonomousTriggerRow(
                    title = "Smart Geofence Dispatcher",
                    description = "Uses Play Services Location coordinates to assign urgent emergency calls to the nearest verified crew",
                    icon = Icons.Default.Speed,
                    iconTint = StatusGreen,
                    isChecked = aiConfig.autoDispatchClosestTech,
                    onToggle = { viewModel.toggleAiStudioFeature("autoDispatchClosestTech") },
                    testTag = "toggle_auto_dispatch"
                )

                AutonomousTriggerRow(
                    title = "80/20 Escrow Ledger Auditor",
                    description = "Verifies 80% contractor share vs 20% platform treasury before initiating Stripe Connect draws",
                    icon = Icons.Default.Security,
                    iconTint = StatusPurple,
                    isChecked = aiConfig.autoEscrowSplitValidation,
                    onToggle = { viewModel.toggleAiStudioFeature("autoEscrowSplitValidation") },
                    testTag = "toggle_auto_escrow"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Master System Directive Editor
            Surface(
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Master AI System Directives",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TextButton(onClick = { isEditingDirective = !isEditingDirective }) {
                            Text(if (isEditingDirective) "Done" else "Edit", fontSize = 11.sp)
                        }
                    }

                    if (isEditingDirective) {
                        OutlinedTextField(
                            value = directiveInput,
                            onValueChange = { directiveInput = it },
                            modifier = Modifier.fillMaxWidth().testTag("master_directive_input"),
                            minLines = 3,
                            maxLines = 5
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                viewModel.updateAiStudioDirective(directiveInput)
                                isEditingDirective = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusBlue),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("save_directive_btn")
                        ) {
                            Text("Deploy Directives to System", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = aiConfig.systemDirective,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Interactive Google AI Studio Testing Sandbox
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Navy900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Studio Live Console & Testing Sandbox",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = aiConfig.selectedModel,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AccentCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Buttons
                    Text("QUICK TEST PRESETS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Level 2 EV Charger Quote (85718)",
                            "Audit 200A Service Panel Photo",
                            "Emergency HVAC Outage Dispatch",
                            "Validate 80/20 Stripe Connect Split"
                        ).forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.1f),
                                modifier = Modifier.clickable {
                                    promptInput = preset
                                    viewModel.executeAiStudioSandboxPrompt(preset)
                                }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prompt Input
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("Enter prompt to test against Google AI Studio...", fontSize = 11.sp, color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().testTag("ai_studio_sandbox_input"),
                        minLines = 2,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Direct API Call • TLS 1.3 Encrypted",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Button(
                            onClick = { viewModel.executeAiStudioSandboxPrompt(promptInput) },
                            enabled = !isRunning && promptInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("run_ai_studio_sandbox_btn")
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Executing...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run in AI Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Sandbox Output Display
                    if (sandboxResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("CONSOLE RESPONSE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = sandboxResult ?: "",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Recent Autonomous Trigger Logs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT AUTONOMOUS EXECUTION STREAM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Text("${executionLogs.size} Events", fontSize = 11.sp, color = StatusBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                executionLogs.take(4).forEach { log ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth().testTag("ai_log_${log.id}")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = StatusGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = log.triggerType,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = log.timestamp,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${log.latencyMs}ms • ${log.modelUsed}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Prompt: ${log.promptSummary}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = log.outputSummary,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AutonomousTriggerRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isChecked: Boolean,
    onToggle: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(iconTint.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 14.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = iconTint,
                    checkedTrackColor = iconTint.copy(alpha = 0.3f)
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
