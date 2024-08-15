package com.bekircaglar.chatappbordo.presentation.chat.component

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatAppSearchBar() {

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = "",
        onValueChange ={},
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        },
        placeholder = {
            Text(text = "Search...")
        },




    )
    
    
}
