
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.don.bottomappbar.NavItems


@Composable
fun rememberNiaAppState(
    navController: NavHostController = rememberNavController(),
): ApplicationState {
    return remember(
        navController,
    ) {
        ApplicationState(
            navController,
        )
    }
}


@Stable
class ApplicationState(
    val navController: NavHostController,
){
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: NavItems?
        @Composable get() = when (currentDestination?.route) {
            "home" -> NavItems.Home
            "chat" -> NavItems.Chat
            "settings" -> NavItems.Settings
            else -> null
        }

}

val currentDestination: (String) -> NavItems? = { destination: String ->
    when (destination) {
        "home" -> NavItems.Home
        "chat" -> NavItems.Chat
        "settings" -> NavItems.Settings
        else -> null
    }
}
