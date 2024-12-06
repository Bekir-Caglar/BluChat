@file:OptIn(ExperimentalMaterial3Api::class)

package com.bekircaglar.bluchat.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bekircaglar.bluchat.presentation.chat.component.SearchTextField

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
    containerColor: Color = MaterialTheme.colorScheme.background,
    titleColor: Color = MaterialTheme.colorScheme.primary,
    searchIcon :Boolean = true,
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    TopAppBar(
        title = {
            if (!isSearchActive)
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
                        tint = titleColor,
                    )
                }
            }

        },
        actions = {
            if (searchIcon) {
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn() + expandHorizontally(
                        expandFrom = Alignment.Start,
                        clip = false
                    ),
                    exit = shrinkHorizontally(
                        shrinkTowards = Alignment.Start,
                        clip = false
                    ) + fadeOut()
                ) {
                    SearchTextField(
                        query = searchText,
                        onQueryChange = { searchText = it },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
                IconButton(onClick = { isSearchActive = !isSearchActive }) {
                    Icon(
                        Icons.Default.Search, contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (actionIcon != null) {
                IconButton(onClick = { onActionIconClicked() }) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        tint = titleColor,
                    )

                }
            }
            if (actionIcon2 != null) {
                IconButton(onClick = { onActionIcon2Clicked() }) {
                    Icon(
                        imageVector = actionIcon2,
                        contentDescription = null,
                        tint = titleColor,
                    )
                }
            }
        },
    )

}
