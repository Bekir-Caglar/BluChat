package com.bekircaglar.chatappbordo.presentation.auth.component

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bekircaglar.chatappbordo.R

@Composable
fun AuthTextField(hint:@Composable ()->Unit,value: String, onValueChange: (String) -> Unit,leadingIcon: ImageVector? = null,keyboardType: KeyboardType = KeyboardType.Text,title:String? = null) {

    Column {
        if (title != null) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary
            )
    }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        TextField(value = value,
            onValueChange = { onValueChange(value)},
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon, contentDescription = null
                    )
                }
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { hint() },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .clip(shape = ShapeDefaults.Medium)
        )
    }

}

@Preview
@Composable
fun AuthTextFieldPreview() {

    AuthTextField(hint = { Text(text = "Email") }, value = "", onValueChange = {}, leadingIcon = Icons.Default.Email, title = "Email")
}

