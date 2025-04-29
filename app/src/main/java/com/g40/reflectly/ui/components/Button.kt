package com.g40.reflectly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReflectlyButton(
    onClick: () -> Unit,
    usePrimary: Boolean = true,
    modifier: Modifier, // ← allow external modifier
    content: @Composable () -> Unit
) {
    val backgroundColor = if (usePrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    val contentColor = if (usePrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground

    Button(
        onClick = onClick,
        modifier = modifier, // ← apply it here
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp
        ),
        border = BorderStroke(1.5.dp, contentColor),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
    ) {
        content()
    }
}
