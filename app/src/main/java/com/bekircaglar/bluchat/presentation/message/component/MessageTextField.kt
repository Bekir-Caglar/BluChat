package com.bekircaglar.bluchat.presentation.message.component

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp


@Composable
fun MessageTextField(
    onSend : (String) -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = ""
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        keyboardOptions = KeyboardOptions.Default.copy(

            imeAction = ImeAction.Send
        ),
        keyboardActions = KeyboardActions(
            onSend = {
                onSend(searchText)
            }
        ),
        modifier = modifier
            .width(250.dp)
            .clip(MaterialTheme.shapes.medium),
        placeholder = { Text(placeholderText, color = MaterialTheme.colorScheme.onSurface) },
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )

}