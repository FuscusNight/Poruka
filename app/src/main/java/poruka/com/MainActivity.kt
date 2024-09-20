package poruka.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import poruka.com.ui.screens.AddFriendScreen
import poruka.com.ui.screens.FriendRequestScreen
import poruka.com.ui.screens.FriendsScreen
import poruka.com.ui.screens.LoginOrRegisterScreen
import poruka.com.ui.screens.LoginScreen
import poruka.com.ui.screens.UserCreationScreen
import poruka.com.ui.screens.UserHomeScreen
import poruka.com.ui.theme.PorukaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Sets up a custom theme i made in my resources folders, for the start up screen
        setTheme(R.style.Theme_Poruka)
        super.onCreate(savedInstanceState)
        // Starts firebase for the whole application
        FirebaseApp.initializeApp(this)

        setContent {
            PorukaTheme {
                //Defines area of screen where content is drawn
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Initialize NavController which will handle all the navigation between screens
                    val navController = rememberNavController()
                    // Start the AppNavHost, passing the navController to it
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    /**
     * NavHost is the container that manages navigation between different screens (composables).
     * The `startDestination` defines the initial screen when the app is opened.
     */
    NavHost(navController = navController, startDestination = "select") {
        composable("select") {
            // Define each screen with a route and the corresponding Composable function
            LoginOrRegisterScreen(
                onLoginClick = { navController.navigate("login") }, // Navigates to Login screen
                onRegisterClick = { navController.navigate("register") } // Navigates to Register screen
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onBackClick = { navController.popBackStack() } // Removes current screen from the backstack
            )
        }
        composable("register") {
            UserCreationScreen(
                onRegisterSuccess = { navController.navigate("home") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("home") {
            UserHomeScreen(
                onFriendsClick = { navController.navigate("friends") }
            )
        }
        composable("friends") {
            FriendsScreen(
                onAddFriendClick = { navController.navigate("add_friend") },
                onViewFriendRequestsClick = { navController.navigate("friend_requests") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("add_friend") {
            AddFriendScreen(
                onFriendAdded = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("friend_requests") {
            FriendRequestScreen(
                onBackClick = { navController.popBackStack() },
                // TO DO: Change this later so it stays on the FriendRequestScreen
                onFriendAccepted = { navController.popBackStack() },
                onFriendRejected = { navController.popBackStack() }
            )
        }
    }
}




