package com.bekircaglar.bluchat.presentation.chat.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchTextField(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search",
    width: Int = 250,
    height: Int = 60
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = modifier
            .width(width.dp)
            .height(height = height.dp)
            .clip(MaterialTheme.shapes.medium),
        placeholder = { Text(placeholderText, style = MaterialTheme.typography.bodySmall) },
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}