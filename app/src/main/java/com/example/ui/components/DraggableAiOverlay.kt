package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DraggableAiOverlay(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    val animatedOffset by animateOffsetAsState(targetValue = offset, label = "ai_overlay_drag")

    val isAiStreamingActive by viewModel.isAiStreamingActive.collectAsState()
    val isAiModalOpen by viewModel.isAiModalOpen.collectAsState()
    val isMicMuted by viewModel.isWebRtcMicMuted.collectAsState()
    val isCameraOn by viewModel.isWebRtcCameraEnabled.collectAsState()
    val isScreenShare by viewModel.isWebRtcScreenShare.collectAsState()
    val latencyMs by viewModel.webRtcLatencyMs.collectAsState()
    val transcriptMessages by viewModel.aiTranscriptMessages.collectAsState()

    // Pulsating animation for the electric lightning bolt
    val infiniteTransition = rememberInfiniteTransition(label = "lightning_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isAiStreamingActive) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val tooltipState = rememberTooltipState()

    // Floating Draggable Live SVG Lightning Bolt
    Box(
        modifier = modifier
            .offset { IntOffset(animatedOffset.x.roundToInt(), animatedOffset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset += dragAmount
                }
            }
    ) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip(
                    shape = RoundedCornerShape(8.dp),
                    containerColor = Navy900,
                    contentColor = Color.White
                ) {
                    Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                        Text(
                            text = "⚡ C520X Live AI Dispatch Copilot",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                        Text(
                            text = if (isAiStreamingActive) "WebRTC Live Voice & Vision Active • Tap to open" else "Tap to open AI Dispatch Orchestrator",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            },
            state = tooltipState
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clickable { viewModel.openAiModal() }
                    .testTag("draggable_lightning_ai_btn"),
                contentAlignment = Alignment.Center
            ) {
                // Electric energetic aura ring (no solid card or box)
                Box(
                    modifier = Modifier
                        .size(if (isAiStreamingActive) 54.dp else 44.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    OrangePrimary.copy(alpha = if (isAiStreamingActive) 0.55f else 0.25f),
                                    Color(0xFFFFB703).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Pure SVG Live Lightning Bolt
                Image(
                    painter = painterResource(id = R.drawable.ic_live_lightning),
                    contentDescription = "C520X Live Lightning SVG",
                    modifier = Modifier
                        .size(46.dp)
                        .scale(pulseScale)
                )

                // Miniature LIVE pill indicator
                if (isAiStreamingActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(6.dp))
                            .background(StatusGreen)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

    // Fully Functional Live Multimodal WebRTC & AI Dispatch Orchestrator Modal
    if (isAiModalOpen) {
        Dialog(onDismissRequest = { viewModel.closeAiModal() }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Navy900,
                border = BorderStroke(1.5.dp, OrangePrimary),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .testTag("webrtc_ai_orchestrator_modal")
            ) {
                var promptInput by remember { mutableStateOf("") }

                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_live_lightning),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "C520X MULTIMODAL AI",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OrangePrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isAiStreamingActive) StatusGreen else StatusRed)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAiStreamingActive) "WebRTC Streaming • ${latencyMs}ms" else "Streaming Paused",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isAiStreamingActive) StatusGreen else Color.Gray
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { viewModel.closeAiModal() },
                            modifier = Modifier.size(32.dp).testTag("close_ai_modal_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live WebRTC Audio/Vision Status & Frequency Waveform
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy700),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "WebRTC PEER: c520x-stream-us-east1",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = "CODEC: OPUS / VP9",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Simulated Animated Audio Waveform Bars
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val heights = listOf(8, 18, 26, 14, 22, 28, 16, 24, 12, 20, 26, 14, 18, 10)
                                heights.forEachIndexed { index, baseHeight ->
                                    val barHeight = if (isAiStreamingActive && !isMicMuted) {
                                        val animFactor = if ((index % 2) == 0) pulseScale else (2.0f - pulseScale)
                                        (baseHeight * animFactor).coerceIn(4f, 28f).dp
                                    } else {
                                        4.dp
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                if (isAiStreamingActive && !isMicMuted) OrangePrimary else Color.DarkGray
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Multimodal Controls Row (Mic, Camera, Screen Share, Live Toggle)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Mic Toggle
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (!isMicMuted) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, if (!isMicMuted) StatusGreen else StatusRed),
                                        modifier = Modifier.clickable { viewModel.toggleWebRtcMic() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (!isMicMuted) Icons.Default.Mic else Icons.Default.MicOff,
                                                contentDescription = "Mic",
                                                tint = if (!isMicMuted) StatusGreen else StatusRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (!isMicMuted) "Mic ON" else "Muted",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (!isMicMuted) StatusGreen else StatusRed
                                            )
                                        }
                                    }

                                    // Camera Toggle
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCameraOn) OrangePrimary.copy(alpha = 0.15f) else Color.DarkGray.copy(alpha = 0.3f),
                                        border = BorderStroke(1.dp, if (isCameraOn) OrangePrimary else Color.Gray),
                                        modifier = Modifier.clickable { viewModel.toggleWebRtcCamera() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                                contentDescription = "Camera",
                                                tint = if (isCameraOn) OrangePrimary else Color.LightGray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isCameraOn) "Vision Active" else "Cam Off",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCameraOn) OrangePrimary else Color.LightGray
                                            )
                                        }
                                    }

                                    // Screen Share Toggle
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isScreenShare) Color(0xFF2196F3).copy(alpha = 0.15f) else Color.DarkGray.copy(alpha = 0.3f),
                                        border = BorderStroke(1.dp, if (isScreenShare) Color(0xFF2196F3) else Color.Gray),
                                        modifier = Modifier.clickable { viewModel.toggleWebRtcScreenShare() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isScreenShare) Icons.Default.ScreenShare else Icons.Default.StopScreenShare,
                                                contentDescription = "Screen",
                                                tint = if (isScreenShare) Color(0xFF2196F3) else Color.LightGray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                // Stream Play/Pause
                                Button(
                                    onClick = { viewModel.toggleAiStreaming() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isAiStreamingActive) Color(0xFFD32F2F) else OrangePrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isAiStreamingActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAiStreamingActive) "Stop Live" else "Go Live",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick AI Prompt Chips
                    Text(
                        text = "DISPATCH & FLEET ORCHESTRATION CHIPS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val promptChips = listOf(
                            "⚡ Optimize Today's Dispatch",
                            "💰 Verify 80/20 Contractor Escrow",
                            "🚛 Scan Reefer Freight Loads",
                            "🔄 Auto-Update All Users & Sync"
                        )
                        promptChips.forEach { chipText ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable { viewModel.sendAiPrompt(chipText) }
                            ) {
                                Text(
                                    text = chipText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Conversation / Multimodal Output Feed
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Navy700.copy(alpha = 0.5f))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transcriptMessages) { (sender, text) ->
                            val isAi = sender.contains("Gemini", ignoreCase = true)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isAi) Navy900 else OrangePrimary.copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, if (isAi) OrangePrimary.copy(alpha = 0.5f) else Color.Transparent),
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = sender,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAi) OrangePrimary else Color(0xFF64B5F6)
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = text,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text / Voice Prompt Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("Speak or type dispatch instruction...", fontSize = 12.sp, color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    viewModel.sendAiPrompt(promptInput)
                                    promptInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(OrangePrimary)
                                .testTag("send_ai_prompt_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
