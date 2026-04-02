package com.skilltok.app

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {
    val Primary = Color(0xFF6366F1)
    val Secondary = Color(0xFF8B5CF6)
    val PrimaryGradient = Brush.linearGradient(
        colors = listOf(Primary, Secondary)
    )
}
