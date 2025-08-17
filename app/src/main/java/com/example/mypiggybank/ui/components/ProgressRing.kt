package com.example.mypiggybank.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressRing(progress: Float) {
    CircularProgressIndicator(
        progress = { progress },
        strokeWidth = 8.dp,
        modifier = Modifier.size(120.dp)
    )
}
