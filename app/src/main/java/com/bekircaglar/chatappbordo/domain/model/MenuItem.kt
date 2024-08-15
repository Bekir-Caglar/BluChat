package com.bekircaglar.chatappbordo.domain.model

import androidx.compose.ui.graphics.painter.Painter

class MenuItem(
    val icon: Painter,
    val title: String,
    val onClick: () -> Unit
) {
    fun onClick() {
        if (title == "Account") {

        }
    }
}