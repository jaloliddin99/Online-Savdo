package org.don.onlineTrade.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AreaChart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.navigation.categoriesNavigationRoute
import org.don.onlineTrade.ui.navigation.regionsNavigationRoute

sealed class NavItems(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    var screenRoute:String,
    @StringRes val titleRes: Int
){

    object Home: NavItems(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        hasNews = false,
        badgeCount = null,
        "home",
        titleRes = R.string.top_home
    )
    object Chat: NavItems(
        title = "Chat",
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email,
        hasNews = false,
        badgeCount = 45,
        "chat",
        titleRes = R.string.chat_page
    )
    object Saved: NavItems(
        title = "Saved",
        selectedIcon = Icons.Filled.Save,
        unselectedIcon = Icons.Outlined.Save,
        hasNews = true,
        badgeCount = null,
        "saved",
        titleRes = R.string.saved_page
    )

    object Profile: NavItems(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        hasNews = true,
        badgeCount = null,
        "profile",
        titleRes = R.string.profile
    )

    object Categories: NavItems(
        title = "Categories",
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category,
        hasNews = true,
        badgeCount = null,
        categoriesNavigationRoute,
        titleRes = R.string.profile
    )

    object Regions: NavItems(
        title = "Regions",
        selectedIcon = Icons.Filled.AreaChart,
        unselectedIcon = Icons.Outlined.AreaChart,
        hasNews = true,
        badgeCount = null,
        regionsNavigationRoute,
        titleRes = R.string.regions
    )
}
