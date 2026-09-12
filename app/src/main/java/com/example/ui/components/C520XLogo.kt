package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CanyonCobalt
import com.example.ui.theme.CanyonOrange
import com.example.ui.theme.CanyonOrangeLight
import com.example.ui.theme.TextWhite

@Composable
fun C520XBrandBadge(
    modifier: Modifier = Modifier,
    height: Dp = 36.dp
) {
    Row(
        modifier = modifier
            .background(Color(0xFF061022), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(height = height, width = height * 1.3f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(height = height, width = height * 1.3f)) {
                val w = size.width
                val h = size.height

                val swooshPath = Path().apply {
                    moveTo(0f, h * 0.75f)
                    cubicTo(w * 0.3f, h * 0.9f, w * 0.6f, h * 0.7f, w, h * 0.25f)
                    cubicTo(w * 0.7f, h * 0.55f, w * 0.35f, h * 0.65f, 0f, h * 0.75f)
                    close()
                }
                drawPath(
                    path = swooshPath,
                    brush = Brush.linearGradient(
                        colors = listOf(CanyonOrange, CanyonOrangeLight, Color(0xFFFFB300)),
                        start = Offset(0f, h),
                        end = Offset(w, 0f)
                    )
                )

                val blueArc = Path().apply {
                    moveTo(w * 0.1f, h * 0.65f)
                    cubicTo(w * 0.05f, h * 0.2f, w * 0.35f, h * 0.15f, w * 0.55f, h * 0.2f)
                    cubicTo(w * 0.35f, h * 0.28f, w * 0.18f, h * 0.35f, w * 0.1f, h * 0.65f)
                    close()
                }
                drawPath(
                    path = blueArc,
                    brush = Brush.linearGradient(
                        colors = listOf(CanyonCobalt, Color(0xFF3884F7)),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                )
            }
            Text(
                text = "C520X",
                color = TextWhite,
                fontSize = (height.value * 0.42).sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-0.5).sp
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Column {
            Text(
                text = "CANYON 520 EXPRESS",
                color = CanyonOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = "DISPATCH COMMAND CENTER",
                color = Color(0xFF94A3B8),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
