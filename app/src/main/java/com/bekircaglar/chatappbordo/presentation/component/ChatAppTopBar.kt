@file:OptIn(ExperimentalMaterial3Api::class)

package com.bekircaglar.chatappbordo.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialogDefaults.titleContentColor
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.w3c.dom.Text

@Composable

fun ChatAppTopBar(
    title: @Composable () -> Unit,
    navigationIcon: ImageVector? = null,
    onNavigateIconClicked: () -> Unit? = {},
    actionIcon: ImageVector? = null,
    onActionIconClicked: () -> Unit? = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    TopAppBar(
        title = {
            title()
        },
        colors = TopAppBarColors(
            containerColor = containerColor,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            scrolledContainerColor = containerColor,
        ),
        navigationIcon = {
            if (navigationIcon != null) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                        onNavigateIconClicked()
                    }
                )
            }

        },
        actions = {
            if (actionIcon != null) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                        onActionIconClicked()
                    }
                )
            }
        },
        windowInsets = WindowInsets(
            top = 16.dp,
            left = 8.dp,
            right = 8.dp,
            ),


        )

}
