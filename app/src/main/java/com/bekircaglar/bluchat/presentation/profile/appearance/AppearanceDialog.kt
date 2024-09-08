package com.bekircaglar.bluchat.presentation.profile.appearance

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bekircaglar.bluchat.R

@Composable
fun AppearanceDialog(
    onDismissRequest: () -> Unit,
    onThemeChange: (Boolean) -> Unit,
    darkTheme: Boolean
) {
    var isChecked by remember { mutableStateOf(darkTheme) }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Change Theme",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom =16.dp)
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(if (isChecked) Color.Gray else Color.Yellow)
                        .clickable {
                            isChecked = !isChecked
                            onThemeChange(isChecked)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(isChecked) { checked ->
                        if (checked) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_moon),
                                contentDescription = "Dark Theme",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_sun2),
                                contentDescription = "Light Theme",
                                tint = Color.Black,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun AppearanceDialogPreview() {
    AppearanceDialog(
        onDismissRequest = {},
        onThemeChange = {},
        darkTheme = true
    )
}