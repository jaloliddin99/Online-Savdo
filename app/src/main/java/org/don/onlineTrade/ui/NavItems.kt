package org.don.onlineTrade.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.AreaChart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Details
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.ui.graphics.vector.ImageVector
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.navigation.categoriesNavigationRoute
import org.don.onlineTrade.ui.navigation.districtsNavigationRoute
import org.don.onlineTrade.ui.navigation.filterCategoryNavigationRoute
import org.don.onlineTrade.ui.navigation.myProductsNavigationRoute
import org.don.onlineTrade.ui.navigation.notificationsNavigationRoute
import org.don.onlineTrade.ui.navigation.pDetailsNavigationRoute
import org.don.onlineTrade.ui.navigation.profileUpdateNavigationRoute
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
    object AddProduct: NavItems(
        title = "Add",
        selectedIcon = Icons.Filled.LibraryAdd,
        unselectedIcon = Icons.Outlined.LibraryAdd,
        hasNews = false,
        badgeCount = 45,
        "add",
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


    object ProfileUpdate: NavItems(
        title = "Profile Update",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        hasNews = true,
        badgeCount = null,
        profileUpdateNavigationRoute,
        titleRes = R.string.update_profile
    )
    object Categories: NavItems(
        title = "Categories",
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category,
        hasNews = true,
        badgeCount = null,
        categoriesNavigationRoute,
        titleRes = R.string.select_category_without
    )

    object MyPosts: NavItems(
        title = "My Posts",
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category,
        hasNews = true,
        badgeCount = null,
        myProductsNavigationRoute,
        titleRes = R.string.my_orders
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
    object District: NavItems(
        title = "Districts",
        selectedIcon = Icons.Filled.AreaChart,
        unselectedIcon = Icons.Outlined.AreaChart,
        hasNews = true,
        badgeCount = null,
        districtsNavigationRoute,
        titleRes = R.string.districts
    )
    object ProductDetails: NavItems(
        title = "Product Details",
        selectedIcon = Icons.Filled.Details,
        unselectedIcon = Icons.Outlined.Details,
        hasNews = true,
        badgeCount = null,
        screenRoute = pDetailsNavigationRoute,
        titleRes = R.string.details
    )

    object Notifications: NavItems(
        title = "Notifications",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
        hasNews = true,
        badgeCount = null,
        screenRoute = notificationsNavigationRoute,
        titleRes = R.string.notifications
    )

    object FilterCategories: NavItems(
        title = "Product Categories",
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category,
        hasNews = true,
        badgeCount = null,
        screenRoute = filterCategoryNavigationRoute,
        titleRes = R.string.categories
    )
}
