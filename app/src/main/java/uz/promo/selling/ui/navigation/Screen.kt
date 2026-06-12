package uz.promo.selling.ui.navigation

sealed class Screen(val route: String) {
    // No-arg routes
    data object Home : Screen("home")
    data object AddProduct : Screen("add")
    data object Saved : Screen("saved")
    data object Profile : Screen("profile")
    data object ProfileUpdate : Screen("profileUpdate")
    data object PasswordUpdate : Screen("passwordUpdate")
    data object Categories : Screen("categories")
    data object MyProducts : Screen("myProducts")
    data object Notifications : Screen("notifications")
    data object NotificationSettings : Screen("notificationSettings")
    data object Search : Screen("search_route")
    data object Login : Screen("loginScreen")
    data object Welcome : Screen("welcome_screen")
    data object Map : Screen("mapNavigationRoute")
    data object MapSearch : Screen("mapSearchRoute")
    data object CompleteProfile : Screen("completeProfile")
    data object Chat : Screen("chat")

    data class ChatDetail(val conversationId: Long) : Screen("chatDetail/$conversationId") {
        companion object {
            const val ROUTE = "chatDetail/{conversationId}"
        }
    }

    // Routes with arguments
    data class ProductDetails(val productId: Int) : Screen("productDetails/$productId") {
        companion object {
            const val ROUTE = "productDetails/{param}"
        }
    }

    data class FilterCategory(val categoryId: Int) : Screen("filterCategory/$categoryId") {
        companion object {
            const val ROUTE = "filterCategory/{param}"
        }
    }

    data class Verification(val email: String) : Screen("verification_screen/$email") {
        companion object {
            const val ROUTE = "verification_screen/{email}"
        }
    }

    data class ForgotPassword(val fromLoginPage: Boolean) : Screen("forgotPasswordRoute/$fromLoginPage") {
        companion object {
            const val ROUTE = "forgotPasswordRoute/{fromLoginPage}"
        }
    }

    data class ResetPassword(val email: String, val fromLoginPage: Boolean) : Screen("resetPasswordRoute/$email/$fromLoginPage") {
        companion object {
            const val ROUTE = "resetPasswordRoute/{email}/{fromLoginPage}"
        }
    }

    data class MapUserLocation(val latitude: String, val longitude: String) : Screen("mapUserScreen/$latitude/$longitude") {
        companion object {
            const val ROUTE = "mapUserScreen/{latitude}/{longitude}"
        }
    }
}
