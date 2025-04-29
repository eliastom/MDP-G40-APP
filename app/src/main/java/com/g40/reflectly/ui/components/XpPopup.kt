package com.g40.reflectly.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun XpPopup(
    visible: Boolean,
    xpAmount: Int,
    modifier: Modifier = Modifier
) {
    if (visible) {
        val offsetAnim by animateFloatAsState(
            targetValue = -50f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
            label = "XpPopupOffset"
        )

        Box(
            modifier = modifier.padding(top = 100.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "+$xpAmount XP",
                color = Color(0xFFA7C7E7),
                fontSize = 24.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.offset { IntOffset(0, offsetAnim.toInt()) }
            )
        }
    }
}
