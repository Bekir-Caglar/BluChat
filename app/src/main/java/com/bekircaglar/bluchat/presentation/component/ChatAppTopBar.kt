@file:OptIn(ExperimentalMaterial3Api::class)

package com.bekircaglar.bluchat.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ChatAppTopBar(
    title: @Composable () -> Unit,
    navigationIcon: ImageVector? = null,
    onNavigateIconClicked: () -> Unit? = {},
    actionIcon: ImageVector? = null,
    actionIcon2: ImageVector? = null,
    onActionIconClicked: () -> Unit? = {},
    onActionIcon2Clicked: () -> Unit? = {},
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    titleColor: Color = MaterialTheme.colorScheme.onSecondary,
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
                IconButton(onClick = { onNavigateIconClicked() }) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                    )
                }
            }

        },
        actions = {
            if (actionIcon != null) {
                IconButton(onClick = { onActionIconClicked() }) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                    )

                }
            }
            if (actionIcon2 != null) {
                IconButton(onClick = { onActionIcon2Clicked() }) {
                    Icon(
                        imageVector = actionIcon2,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                    )
                }
            }
        },
    )

}
