package com.bekircaglar.bluchat.presentation.bottomappbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.ui.theme.ChatAppBordoTheme


@Composable
fun ChatAppBottomAppBar(navController: NavController? = null) {
    val items = remember {
        listOf(
            Screens.ContactScreen,
            Screens.ChatListScreen,
            Screens.ProfileScreen,
        )
    }
    val currentRoute = navController?.currentBackStackEntryAsState()?.value?.destination?.route

    BottomAppBar(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            .clip(shape = MaterialTheme.shapes.medium),
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController?.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },

                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount != 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Text(text = item.badgeCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon!!),
                                contentDescription = item.route,
                                modifier = Modifier.size(30.dp),
                                tint = if (currentRoute == item.route) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                                }
                            )
                        }
                    },
                    colors = NavigationBarItemColors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                        selectedIndicatorColor = MaterialTheme.colorScheme.background,
                        disabledIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun ChatAppBottomAppBarPreview() {
    ChatAppBordoTheme {
        ChatAppBottomAppBar()
    }
}
