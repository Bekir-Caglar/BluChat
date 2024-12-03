import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.bluchat.navigation.Screens

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
            .background(Color.Transparent)
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.6f
                    ),
                    animationSpec = tween(durationMillis = 300),
                    label = ""
                )
                NavigationBarItem(
                    selected = isSelected,
                    label = {
                        item.label?.let {
                            Text(
                                text = it,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    },
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
                            AnimatedContent(
                                targetState = item.route == currentRoute,
                                transitionSpec ={
                                    scaleIn(animationSpec = tween(300)) togetherWith scaleOut(
                                        animationSpec = tween(300)
                                    )
                                },
                                label = "Icon Animation"
                                ) {
                                when (it) {
                                    true -> {
                                        Icon(
                                            painter = painterResource(id = item.selectedIcon!!),
                                            contentDescription = item.label,
                                            tint = iconColor
                                        )
                                    }
                                    false -> {
                                        Icon(
                                            painter = painterResource(id = item.unSelectedIcon!!),
                                            contentDescription = item.label,
                                            tint = iconColor
                                        )
                                    }
                                }
                            }
                        }
                    },
                    colors = NavigationBarItemColors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = 0.6f
                        ),
                        unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = 0.6f
                        ),
                        selectedIndicatorColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                        disabledIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                )
            }
        }
    }
    HorizontalDivider(
        modifier =
        Modifier
            .height(0.2.dp)
            .shadow(elevation = 0.2.dp)
    )
}