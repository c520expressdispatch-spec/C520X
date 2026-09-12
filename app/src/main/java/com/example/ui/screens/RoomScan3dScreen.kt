package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropRotate
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.RoomScanEntity
import com.example.data.TradePersona
import com.example.ui.MainViewModel
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import java.util.Locale

enum class Room3dViewTab(val label: String) {
    SCANNER("AR Room Scan"),
    FLOOR_PLAN_3D("3D Floor Plan"),
    RENDERS("Photorealistic Renders")
}

@Composable
fun RoomScan3dScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(Room3dViewTab.SCANNER) }
    val roomScans by viewModel.roomScans.collectAsState()
    val activeTradePersona by viewModel.selectedTradePersona.collectAsState()

    var activeScanIndex by remember { mutableIntStateOf(0) }
    val currentScan = roomScans.getOrNull(activeScanIndex) ?: roomScans.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("room_scan_3d_screen")
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Top Header Banner with Trade Persona Customization
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "3D Tools",
                            tint = OrangePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "3D DESIGN & ROOM SCAN STUDIO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "c520x LiDAR Room Scanner & Photorealistic Renders",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AccentCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = activeTradePersona.label.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Selector Row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = OrangePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .testTag("room_studio_tabs")
        ) {
            Room3dViewTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = tab.label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = when (tab) {
                                Room3dViewTab.SCANNER -> Icons.Default.CameraAlt
                                Room3dViewTab.FLOOR_PLAN_3D -> Icons.Default.Layers
                                Room3dViewTab.RENDERS -> Icons.Default.ViewInAr
                            },
                            contentDescription = tab.label,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Screen Body Switcher
        when (selectedTab) {
            Room3dViewTab.SCANNER -> ArRoomScannerView(
                viewModel = viewModel,
                onScanCompleted = {
                    selectedTab = Room3dViewTab.FLOOR_PLAN_3D
                }
            )
            Room3dViewTab.FLOOR_PLAN_3D -> FloorPlan3dView(
                currentScan = currentScan,
                allScans = roomScans,
                onSelectScan = { index -> activeScanIndex = index },
                onNavigateToRenders = { selectedTab = Room3dViewTab.RENDERS }
            )
            Room3dViewTab.RENDERS -> PhotorealisticRendersView(
                currentScan = currentScan,
                tradePersona = activeTradePersona,
                viewModel = viewModel
            )
        }
    }
}

// TAB 1: AR ROOM SCANNER & SNAP PHOTOS DURING ROOM SCANS
@Composable
fun ArRoomScannerView(
    viewModel: MainViewModel,
    onScanCompleted: () -> Unit
) {
    var roomName by remember { mutableStateOf("Kitchen & Dining Renovation") }
    var lengthFt by remember { mutableFloatStateOf(18.5f) }
    var widthFt by remember { mutableFloatStateOf(14.0f) }
    var ceilingFt by remember { mutableFloatStateOf(9.5f) }

    // Snapped photos list during scan
    var snappedPhotos by remember {
        mutableStateOf(
            listOf(
                "North Wall Plumbing & Supply Stacks",
                "Island Center Load-Bearing Post",
                "East Window Structural Header",
                "Main Electrical Subpanel Drop"
            )
        )
    }

    var showSnapPhotoDialog by remember { mutableStateOf(false) }
    var newPhotoCaption by remember { mutableStateOf("") }
    var isCalibrated by remember { mutableStateOf(true) }
    var shutterFlash by remember { mutableStateOf(false) }

    val calculatedArea = lengthFt * widthFt

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val gridPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "grid_pulse"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ar_scanner_view"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Simulated AR LiDAR Viewfinder Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .border(2.dp, if (isCalibrated) StatusGreen.copy(alpha = 0.8f) else OrangePrimary, RoundedCornerShape(16.dp))
                    .testTag("ar_viewfinder")
            ) {
                // Background grid & LiDAR mesh animation
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Draw LiDAR perspective grid lines
                    val step = 32.dp.toPx()
                    for (x in 0..(w / step).toInt()) {
                        drawLine(
                            color = AccentCyan.copy(alpha = gridPulse * 0.4f),
                            start = Offset(x * step, 0f),
                            end = Offset(x * step, h),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0..(h / step).toInt()) {
                        drawLine(
                            color = AccentCyan.copy(alpha = gridPulse * 0.4f),
                            start = Offset(0f, y * step),
                            end = Offset(w, y * step),
                            strokeWidth = 1f
                        )
                    }

                    // Draw 3D Room Box wireframe in camera space
                    val cx = w / 2f
                    val cy = h / 2f
                    val boxW = w * 0.65f
                    val boxH = h * 0.6f

                    // Floor rectangle
                    drawRect(
                        color = OrangePrimary.copy(alpha = 0.08f),
                        topLeft = Offset(cx - boxW / 2, cy - boxH / 4),
                        size = Size(boxW, boxH * 0.7f)
                    )

                    // Corner crosshairs
                    val corners = listOf(
                        Offset(cx - boxW / 2, cy - boxH / 4),
                        Offset(cx + boxW / 2, cy - boxH / 4),
                        Offset(cx - boxW / 2, cy + boxH * 0.45f),
                        Offset(cx + boxW / 2, cy + boxH * 0.45f)
                    )
                    corners.forEach { pt ->
                        drawCircle(color = StatusGreen, radius = 6.dp.toPx(), center = pt)
                        drawLine(color = Color.White, start = Offset(pt.x - 12, pt.y), end = Offset(pt.x + 12, pt.y), strokeWidth = 2f)
                        drawLine(color = Color.White, start = Offset(pt.x, pt.y - 12), end = Offset(pt.x, pt.y + 12), strokeWidth = 2f)
                    }

                    // Center reticle
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 28.dp.toPx(),
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    )
                    drawCircle(color = OrangePrimary, radius = 3.dp.toPx(), center = Offset(cx, cy))
                }

                // Top HUD telemetry
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LiDAR 4/4 CORNERS LOCKED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "${lengthFt} ft × ${widthFt} ft | ${calculatedArea.toInt()} SQ FT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Center shutter flash feedback
                if (shutterFlash) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.7f))
                    )
                }

                // Bottom HUD: Snapped Photos count & Camera Shutter button
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${snappedPhotos.size} Photos Snapped",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    // CAMERA SHUTTER BUTTON: Snap Photos During Room Scans
                    Button(
                        onClick = {
                            shutterFlash = true
                            showSnapPhotoDialog = true
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("snap_photo_shutter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Snap Photo During Scan",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Snapped Photos Reel during Room Scan
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Camera, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SNAPPED SCAN PHOTOS (${snappedPhotos.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 0.5.sp
                            )
                        }

                        TextButton(
                            onClick = { showSnapPhotoDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("quick_snap_photo_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Snap Another Angle", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (snappedPhotos.isEmpty()) {
                        Text(
                            text = "No photos snapped yet. Tap the shutter to capture plumbing, structural posts, or electrical boxes during scan.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(snappedPhotos) { photoTag ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Navy800,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                                    modifier = Modifier.width(140.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(55.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF1E293B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CameraAlt,
                                                contentDescription = null,
                                                tint = AccentCyan,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Text(
                                                text = "ATTACHED",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = photoTag,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dimensional Calibration & Presets
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROOM DIMENSIONS & GEOMETRY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "${calculatedArea.toInt()} SQ FT",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DimensionBadge("Length", "${lengthFt} ft", Modifier.weight(1f))
                        DimensionBadge("Width", "${widthFt} ft", Modifier.weight(1f))
                        DimensionBadge("Ceiling", "${ceilingFt} ft", Modifier.weight(1f))
                        DimensionBadge("Perimeter", "${((lengthFt + widthFt) * 2).toInt()} ft", Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset Room Buttons
                    Text(
                        text = "Quick Room Templates:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("Kitchen", 18.5f, 14.0f),
                            Triple("Master Bath", 12.0f, 10.5f),
                            Triple("Great Room", 24.0f, 18.0f),
                            Triple("Office", 14.0f, 12.0f)
                        ).forEach { (label, l, w) ->
                            val isSel = lengthFt == l && widthFt == w
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    lengthFt = l
                                    widthFt = w
                                    roomName = "$label Renovation"
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OrangePrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = OrangePrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Complete Room Scan and Generate 3D Floor Plan
                    Button(
                        onClick = {
                            viewModel.saveNewRoomScan(
                                roomName = roomName,
                                length = lengthFt.toDouble(),
                                width = widthFt.toDouble(),
                                ceiling = ceilingFt.toDouble(),
                                snappedPhotos = snappedPhotos
                            )
                            onScanCompleted()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("build_3d_floor_plan_button")
                    ) {
                        Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lock Scan & Build 3D Floor Plan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Dialog to tag snapped photo
    if (showSnapPhotoDialog) {
        Dialog(onDismissRequest = {
            shutterFlash = false
            showSnapPhotoDialog = false
        }) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("snap_photo_dialog")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Photo Snapped!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = {
                            shutterFlash = false
                            showSnapPhotoDialog = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tag structural feature or trade fixture captured during this scan angle:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val suggestions = listOf(
                        "Plumbing Supply & Waste Stacks",
                        "Load-Bearing Header / Post",
                        "Electrical Box & Circuit Drop",
                        "HVAC Supply Register / Return",
                        "Countertop Bay Alignment",
                        "Floor Joist Moisture Check"
                    )

                    suggestions.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    snappedPhotos = snappedPhotos + tag
                                    viewModel.showToastNotice("Photo saved: $tag")
                                    shutterFlash = false
                                    showSnapPhotoDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(tag, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DimensionBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// TAB 2: INTERACTIVE 3D FLOOR PLAN STUDIO
@Composable
fun FloorPlan3dView(
    currentScan: RoomScanEntity?,
    allScans: List<RoomScanEntity>,
    onSelectScan: (Int) -> Unit,
    onNavigateToRenders: () -> Unit
) {
    var viewMode by remember { mutableStateOf("2D_BLUEPRINT") } // 2D_BLUEPRINT, 3D_ISOMETRIC, MEP_SYSTEMS
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    val scan = currentScan ?: RoomScanEntity(
        roomName = "Sample Master Suite",
        clientName = "Valued Client",
        lengthFeet = 18.5,
        widthFeet = 14.0,
        ceilingHeightFeet = 9.5,
        areaSqFt = 259.0,
        scanDate = "Today"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("floor_plan_3d_view"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Room Scan Selector if multiple exist
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allScans.size) { idx ->
                    val s = allScans[idx]
                    val isSel = s.id == scan.id
                    FilterChip(
                        selected = isSel,
                        onClick = { onSelectScan(idx) },
                        label = { Text(s.roomName.take(24), fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // View Mode Controls: 2D Blueprint, 3D Isometric, MEP Systems
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        "2D_BLUEPRINT" to "2D Blueprint",
                        "3D_ISOMETRIC" to "3D Isometric",
                        "MEP_SYSTEMS" to "MEP Trades"
                    ).forEach { (mode, label) ->
                        val isSel = viewMode == mode
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clickable { viewMode = mode }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Rotate angle button
                IconButton(
                    onClick = { rotationAngle = (rotationAngle + 90f) % 360f },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.CropRotate, contentDescription = "Rotate 90", tint = OrangePrimary, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Canvas: Interactive Floor Plan Render
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (viewMode == "2D_BLUEPRINT") Color(0xFF0F172A) else Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .testTag("interactive_canvas_floorplan")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures { _, dragAmount ->
                                    rotationAngle = (rotationAngle + dragAmount.x * 0.2f) % 360f
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height
                        val cx = w / 2f
                        val cy = h / 2f

                        // Draw Grid lines
                        val gridSpacing = 24.dp.toPx()
                        for (x in 0..(w / gridSpacing).toInt()) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.05f),
                                start = Offset(x * gridSpacing, 0f),
                                end = Offset(x * gridSpacing, h),
                                strokeWidth = 1f
                            )
                        }
                        for (y in 0..(h / gridSpacing).toInt()) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.05f),
                                start = Offset(0f, y * gridSpacing),
                                end = Offset(w, y * gridSpacing),
                                strokeWidth = 1f
                            )
                        }

                        val roomW = w * 0.65f
                        val roomH = h * 0.55f

                        if (viewMode == "2D_BLUEPRINT") {
                            // 2D Architectural CAD Blueprint
                            val left = cx - roomW / 2
                            val top = cy - roomH / 2

                            // Outer Walls (Double line for wall thickness)
                            drawRect(
                                color = Color(0xFF1E293B),
                                topLeft = Offset(left, top),
                                size = Size(roomW, roomH)
                            )
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(left, top),
                                size = Size(roomW, roomH),
                                style = Stroke(width = 6.dp.toPx())
                            )
                            drawRect(
                                color = AccentCyan,
                                topLeft = Offset(left + 6.dp.toPx(), top + 6.dp.toPx()),
                                size = Size(roomW - 12.dp.toPx(), roomH - 12.dp.toPx()),
                                style = Stroke(width = 1.dp.toPx())
                            )

                            // Interior Fixtures: Kitchen Island / Counters
                            drawRect(
                                color = OrangePrimary.copy(alpha = 0.3f),
                                topLeft = Offset(cx - roomW * 0.25f, cy - roomH * 0.15f),
                                size = Size(roomW * 0.5f, roomH * 0.3f)
                            )
                            drawRect(
                                color = OrangePrimary,
                                topLeft = Offset(cx - roomW * 0.25f, cy - roomH * 0.15f),
                                size = Size(roomW * 0.5f, roomH * 0.3f),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Door swing arc
                            val doorArcPath = Path().apply {
                                moveTo(left + roomW * 0.2f, top + roomH)
                                quadraticTo(left + roomW * 0.35f, top + roomH - 30.dp.toPx(), left + roomW * 0.4f, top + roomH)
                            }
                            drawPath(
                                path = doorArcPath,
                                color = AccentCyan,
                                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                            )

                            // Dimension ticks
                            drawLine(
                                color = StatusAmber,
                                start = Offset(left, top - 15.dp.toPx()),
                                end = Offset(left + roomW, top - 15.dp.toPx()),
                                strokeWidth = 2.dp.toPx()
                            )
                        } else if (viewMode == "3D_ISOMETRIC") {
                            // 3D Isometric Projection
                            val isoPath = Path().apply {
                                moveTo(cx, cy - roomH * 0.4f)
                                lineTo(cx + roomW * 0.45f, cy - roomH * 0.15f)
                                lineTo(cx, cy + roomH * 0.35f)
                                lineTo(cx - roomW * 0.45f, cy - roomH * 0.15f)
                                close()
                            }
                            // Floor texture
                            drawPath(isoPath, color = Color(0xFF334155))

                            // 3D Extruded Walls
                            val wallLeft = Path().apply {
                                moveTo(cx - roomW * 0.45f, cy - roomH * 0.15f)
                                lineTo(cx - roomW * 0.45f, cy - roomH * 0.45f)
                                lineTo(cx, cy - roomH * 0.7f)
                                lineTo(cx, cy - roomH * 0.4f)
                                close()
                            }
                            drawPath(wallLeft, color = Color(0xFF475569))

                            val wallRight = Path().apply {
                                moveTo(cx, cy - roomH * 0.4f)
                                lineTo(cx, cy - roomH * 0.7f)
                                lineTo(cx + roomW * 0.45f, cy - roomH * 0.45f)
                                lineTo(cx + roomW * 0.45f, cy - roomH * 0.15f)
                                close()
                            }
                            drawPath(wallRight, color = Color(0xFF64748B))

                            // 3D Island Block
                            drawCircle(color = OrangePrimary.copy(alpha = 0.7f), radius = 18.dp.toPx(), center = Offset(cx, cy))
                        } else {
                            // MEP Systems Overlay (Mechanical, Electrical, Plumbing)
                            val left = cx - roomW / 2
                            val top = cy - roomH / 2

                            drawRect(
                                color = Color(0xFF0F172A),
                                topLeft = Offset(left, top),
                                size = Size(roomW, roomH)
                            )
                            drawRect(
                                color = Color.Gray,
                                topLeft = Offset(left, top),
                                size = Size(roomW, roomH),
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Blue Plumbing Lines
                            drawLine(
                                color = Color(0xFF38BDF8),
                                start = Offset(left + 20, top + 40),
                                end = Offset(cx, top + 40),
                                strokeWidth = 4.dp.toPx()
                            )
                            drawLine(
                                color = Color(0xFF38BDF8),
                                start = Offset(cx, top + 40),
                                end = Offset(cx, cy),
                                strokeWidth = 4.dp.toPx()
                            )

                            // Red Hot Water
                            drawLine(
                                color = Color(0xFFF87171),
                                start = Offset(left + 20, top + 55),
                                end = Offset(cx - 10, top + 55),
                                strokeWidth = 3.dp.toPx()
                            )

                            // Amber Electrical Home Run
                            drawLine(
                                color = StatusAmber,
                                start = Offset(left + roomW - 20, top + 20),
                                end = Offset(cx + 40, cy),
                                strokeWidth = 3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                            )

                            // Circuit Outlets
                            listOf(
                                Offset(left + 30, top + 5),
                                Offset(left + roomW - 30, top + 5),
                                Offset(cx, top + roomH - 5)
                            ).forEach { pt ->
                                drawCircle(color = StatusAmber, radius = 5.dp.toPx(), center = pt)
                            }
                        }
                    }

                    // Mode telemetry overlay
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = when (viewMode) {
                                "2D_BLUEPRINT" -> "2D ARCHITECTURAL CAD • 1/4\" = 1'0\""
                                "3D_ISOMETRIC" -> "3D ISOMETRIC MESH • DRAG TO ROTATE"
                                else -> "MEP OVERLAY • BLUE=PLUMBING, AMBER=20A AFCI"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Room Specifications & Snapped Photos Count
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = scan.roomName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Client: ${scan.clientName} • Scanned ${scan.scanDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DimensionBadge("Dimensions", "${scan.lengthFeet} × ${scan.widthFeet} ft", Modifier.weight(1.2f))
                        DimensionBadge("Total Area", "${scan.areaSqFt.toInt()} sq ft", Modifier.weight(1f))
                        DimensionBadge("Ceiling", "${scan.ceilingHeightFeet} ft", Modifier.weight(0.8f))
                        DimensionBadge("Photos", "${scan.photoCount} snapped", Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Structural Reference Points: ${scan.snappedPhotos}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Next CTA: View Photorealistic Renders
                    Button(
                        onClick = onNavigateToRenders,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("view_photorealistic_renders_cta")
                    ) {
                        Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Showcase Photorealistic Renders", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

// TAB 3: PHOTOREALISTIC RENDERS GALLERY & BEFORE / AFTER
@Composable
fun PhotorealisticRendersView(
    currentScan: RoomScanEntity?,
    tradePersona: TradePersona,
    viewModel: MainViewModel
) {
    var selectedAngle by remember { mutableStateOf("Angle 1: Warm Sunset Lighting (Waterfall Quartz Island)") }
    var showBeforeCondition by remember { mutableStateOf(false) }

    val renderAngles = listOf(
        "Angle 1: Warm Sunset Lighting (Waterfall Quartz Island)",
        "Angle 2: Daylight Morning Mood (Custom Cabinetry & Millwork)",
        "Angle 3: Eye-Level Living Space & Dining Flow",
        "Angle 4: Macro Texture Detail (Calacatta Gold & Matte Black Brassware)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("photorealistic_renders_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Angle Selector Chips
        item {
            Text(
                text = "SELECT CAMERA PERSPECTIVE & LIGHTING:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(renderAngles) { angle ->
                    val isSel = selectedAngle == angle
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedAngle = angle },
                        label = { Text(angle.take(28) + "...", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Hero Photorealistic Render Canvas Showcase
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .testTag("hero_photorealistic_render_card")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Rich architectural Canvas painting simulating high-end interior 3D render
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        if (showBeforeCondition) {
                            // BEFORE: Demolished Jobsite with bare framing studs
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF27272A), Color(0xFF18181B))
                                )
                            )
                            // Draw 2x4 framing studs
                            val studSpacing = 28.dp.toPx()
                            for (x in 0..(w / studSpacing).toInt()) {
                                drawRect(
                                    color = Color(0xFF78350F),
                                    topLeft = Offset(x * studSpacing, 0f),
                                    size = Size(8.dp.toPx(), h)
                                )
                            }
                            // Exposed wire cables
                            drawLine(
                                color = StatusAmber,
                                start = Offset(0f, h * 0.4f),
                                end = Offset(w, h * 0.45f),
                                strokeWidth = 3.dp.toPx()
                            )
                        } else {
                            // AFTER: Photorealistic Luxury 3D Render
                            // Ambient Wall Gradient
                            val isSunset = selectedAngle.contains("Sunset")
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = if (isSunset) {
                                        listOf(Color(0xFF451A03), Color(0xFF1E1B4B), Color(0xFF0F172A))
                                    } else {
                                        listOf(Color(0xFFF1F5F9), Color(0xFFCBD5E1), Color(0xFF64748B))
                                    }
                                )
                            )

                            // Hardwood Chevron Floor
                            val floorTop = h * 0.55f
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF78350F), Color(0xFF451A03))
                                ),
                                topLeft = Offset(0f, floorTop),
                                size = Size(w, h - floorTop)
                            )
                            // Floor plank perspective lines
                            for (i in 0..12) {
                                val fx = (w / 12) * i
                                drawLine(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    start = Offset(fx, floorTop),
                                    end = Offset(fx * 1.3f - w * 0.15f, h),
                                    strokeWidth = 2f
                                )
                            }

                            // Luxury Waterfall Quartz Kitchen Island
                            val islandW = w * 0.7f
                            val islandH = h * 0.28f
                            val islandLeft = w * 0.15f
                            val islandTop = h * 0.48f

                            // Drop shadow
                            drawOval(
                                color = Color.Black.copy(alpha = 0.5f),
                                topLeft = Offset(islandLeft - 10, islandTop + islandH - 10),
                                size = Size(islandW + 20, 24.dp.toPx())
                            )

                            // Waterfall Quartz Face
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFAFAFA), Color(0xFFE4E4E7), Color(0xFFF4F4F5))
                                ),
                                topLeft = Offset(islandLeft, islandTop),
                                size = Size(islandW, islandH)
                            )

                            // Subtle marble veining
                            val veinPath = Path().apply {
                                moveTo(islandLeft + 20, islandTop + 10)
                                cubicTo(islandLeft + islandW * 0.3f, islandTop + 40, islandLeft + islandW * 0.6f, islandTop + 15, islandLeft + islandW - 30, islandTop + islandH - 20)
                            }
                            drawPath(
                                path = veinPath,
                                color = Color(0xFFD97706).copy(alpha = 0.35f),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Under-counter LED glow
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(StatusAmber.copy(alpha = 0.6f), Color.Transparent)
                                ),
                                topLeft = Offset(islandLeft, islandTop + islandH - 12),
                                size = Size(islandW, 16.dp.toPx())
                            )

                            // Pendant light fixtures
                            listOf(w * 0.35f, w * 0.65f).forEach { px ->
                                drawLine(color = Color(0xFFE2E8F0), start = Offset(px, 0f), end = Offset(px, h * 0.25f), strokeWidth = 2f)
                                drawCircle(color = StatusAmber, radius = 12.dp.toPx(), center = Offset(px, h * 0.25f))
                                drawCircle(color = Color.White, radius = 6.dp.toPx(), center = Offset(px, h * 0.25f))
                            }
                        }
                    }

                    // Before vs After Toggle Chip in Viewfinder
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clickable { showBeforeCondition = !showBeforeCondition }
                            .testTag("before_after_toggle_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (showBeforeCondition) Icons.Default.Tune else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (showBeforeCondition) StatusAmber else StatusGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showBeforeCondition) "SHOWING: BEFORE DEMO" else "SHOWING: 3D DREAM OUTCOME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Camera perspective label bottom overlay
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedAngle,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Before & After Interactive Slider Control
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CLIENT VISUALIZATION CONTROLS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = if (showBeforeCondition) "Demo Condition" else "Photorealistic Finish",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showBeforeCondition) StatusAmber else StatusGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showBeforeCondition = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showBeforeCondition) StatusAmber else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Text("Before (Demo)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (showBeforeCondition) Color.Black else MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = { showBeforeCondition = false },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!showBeforeCondition) StatusGreen else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Text("After (3D Render)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (!showBeforeCondition) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Client Presentation Package Export CTA
                    OutlinedButton(
                        onClick = {
                            viewModel.showToastNotice("Client 3D Render Presentation Link generated and copied!")
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("share_3d_render_package_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share 3D Visuals with Client", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
