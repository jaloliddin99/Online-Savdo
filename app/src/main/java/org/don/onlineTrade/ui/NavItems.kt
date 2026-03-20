package org.don.onlineTrade.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.ui.graphics.vector.ImageVector
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.navigation.Screen

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
        Screen.Home.route,
        titleRes = R.string.top_home
    )
    object AddProduct: NavItems(
        title = "Add",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        hasNews = false,
        badgeCount = null,
        Screen.AddProduct.route,
        titleRes = R.string.chat_page
    )
    object Saved: NavItems(
        title = "Saved",
        selectedIcon = Icons.Filled.Save,
        unselectedIcon = Icons.Outlined.Save,
        hasNews = true,
        badgeCount = null,
        Screen.Saved.route,
        titleRes = R.string.saved_page
    )

    object Profile: NavItems(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        hasNews = true,
        badgeCount = null,
        Screen.Profile.route,
        titleRes = R.string.profile
    )


    object ProfileUpdate: NavItems(
        title = "Profile Update",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        hasNews = true,
        badgeCount = null,
        Screen.ProfileUpdate.route,
        titleRes = R.string.update_profile
    )

    object PasswordUpdate: NavItems(
        title = "Password Update",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        hasNews = true,
        badgeCount = null,
        Screen.PasswordUpdate.route,
        titleRes = R.string.password_update
    )
    object Categories: NavItems(
        title = "Categories",
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category,
        hasNews = true,
        badgeCount = null,
        Screen.Categories.route,
        titleRes = R.string.select_category_without
    )

    object MyPosts: NavItems(
        title = "My Posts",
        selectedIcon = Icons.Filled.Inventory2,
        unselectedIcon = Icons.Outlined.Inventory2,
        hasNews = false,
        badgeCount = null,
        Screen.MyProducts.route,
        titleRes = R.string.my_orders
    )


    object Notifications: NavItems(
        title = "Notifications",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
        hasNews = true,
        badgeCount = null,
        screenRoute = Screen.Notifications.route,
        titleRes = R.string.notifications
    )

    object FilterCategories: NavItems(
        title = "Product Categories",
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category,
        hasNews = true,
        badgeCount = null,
        screenRoute = Screen.FilterCategory.ROUTE,
        titleRes = R.string.categories
    )
}
