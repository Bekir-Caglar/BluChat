package com.bekircaglar.bluchat.domain.model

import androidx.compose.ui.graphics.painter.Painter

class MenuItem(
    val icon: Painter,
    val title: String,
    val onClick: () -> Unit
) {
    fun onClick() {
        onClick()
    }
}