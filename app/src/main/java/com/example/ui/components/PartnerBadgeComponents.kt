package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PartnerType
import com.example.data.models.TradeCategory
import com.example.ui.theme.TextWhite

@Composable
fun PartnerBadge(
    partner: PartnerType,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Surface(
        modifier = modifier,
        color = partner.badgeColor.copy(alpha = 0.18f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, partner.badgeColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(partner.badgeColor, CircleShape)
            )
            if (!compact) {
                Text(
                    text = " ${partner.displayName}",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = " ${partner.brandTag}",
                    color = TextWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getTradeIcon(trade: TradeCategory): ImageVector {
    return when (trade) {
        TradeCategory.REMODELING -> Icons.Default.Kitchen
        TradeCategory.PLUMBING -> Icons.Default.Plumbing
        TradeCategory.ELECTRICAL -> Icons.Default.Bolt
        TradeCategory.ROOFING -> Icons.Default.Roofing
        TradeCategory.PAINTING -> Icons.Default.FormatPaint
        TradeCategory.HVAC -> Icons.Default.AcUnit
        TradeCategory.FLOORING -> Icons.Default.GridOn
        TradeCategory.CARPENTRY -> Icons.Default.Handyman
        TradeCategory.LANDSCAPING -> Icons.Default.Park
        TradeCategory.HANDYMAN -> Icons.Default.Build
        TradeCategory.CONCRETE -> Icons.Default.Foundation
        TradeCategory.CUSTOM -> Icons.Default.Architecture
    }
}
