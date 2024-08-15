package com.bekircaglar.chatappbordo.presentation.chat.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChatAppFAB(
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer, // FAB butonunun arka plan rengi
    contentColor: Color =MaterialTheme.colorScheme.onSecondaryContainer,
    size: Dp = 56.dp,
    cornerRadius: Dp = 12.dp
) {
    FloatingActionButton(
        onClick = { /* Butona tıklanınca yapılacak işlemler */ },
        shape = RoundedCornerShape(cornerRadius), // Karemsi şekil için köşe yuvarlama
        containerColor = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier.size(size)
    ) {
        Icon(
            imageVector = Icons.Default.Add, // + simgesi
            contentDescription = "Add",
            modifier = Modifier.size(size / 2), // İkon boyutu (buton boyutunun yarısı)
            tint = contentColor
        )
    }
}