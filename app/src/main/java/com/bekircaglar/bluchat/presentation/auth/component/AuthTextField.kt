package com.bekircaglar.bluchat.presentation.auth.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.bekircaglar.bluchat.R

@Composable
fun AuthTextField(
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    title: String? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    supportedTextList: List<Pair<String, Boolean>> = emptyList(),
    ) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        if (title != null) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        TextField(
            value = value,
            onValueChange = { onValueChange(it) },
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon, contentDescription = null
                    )
                }
            },
            trailingIcon = {
                if (keyboardType == KeyboardType.Password) {
                    val image = if (passwordVisible) painterResource(R.drawable.ic_visible) else painterResource(R.drawable.ic_invisible)
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                    ){
                        Icon(
                            painter = image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
            },
            maxLines = 1,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (keyboardType == KeyboardType.Password && !passwordVisible) PasswordVisualTransformation() else visualTransformation,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = hint, fontSize = 14.sp)
            },
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .shadow(2.dp, shape = ShapeDefaults.Medium)
                .background(Color.White)
                .clip(shape = ShapeDefaults.Medium)

        )
        supportedTextList.forEach { (text, isValid) ->
            Text(
                text = " * $text",
                color = if (isValid) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun AuthTextFieldPreview() {
    AuthTextField(
        hint = "Email",
        value = "",
        onValueChange = {},
        leadingIcon = Icons.Default.Email,
        title = "Email",
        modifier = Modifier.border(1.dp, Color.Red, MaterialTheme.shapes.medium),
        supportedTextList = listOf(
            "8 characters" to false,
            "Minimum one number" to true
        )
    )
}