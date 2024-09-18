package poruka.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import poruka.com.ui.screens.AddFriendScreen
import poruka.com.ui.screens.FriendsScreen
import poruka.com.ui.screens.LoginOrRegisterScreen
import poruka.com.ui.screens.LoginScreen
import poruka.com.ui.screens.UserCreationScreen
import poruka.com.ui.screens.UserHomeScreen
import poruka.com.ui.theme.PorukaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            PorukaTheme {
                // Nav stack to keep track of screen history
                val screenStack = remember {mutableStateListOf<Screen>(Screen.Select)}
                // Keeps track which screen the user is looking at, REMOVE LATER
                //var currentScreen by remember { mutableStateOf<Screen>(Screen.Select) }

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val currentScreen = screenStack.last()

                    // Function to handle screen navigation
                    fun navigateTo(screen: Screen) {
                        screenStack.add(screen)
                    }

                    // Function to go back to the previous screen
                    fun goBack() {
                        if (screenStack.size > 1) {
                            screenStack.removeLast()
                        }
                    }

                    // The when expression maps each Screen object to a specific Composable function, which renders the appropriate UI.
                    when (currentScreen) {
                        Screen.Select -> LoginOrRegisterScreen(
                            // We tell it if user clicks on the button assigned to onLoginClick it will then open up the LoginScreen screen
                            // It triggers a callback telling it switch the value to the appropriate screen the user wants to go to
                            //onLoginClick = { currentScreen =  Screen.Login},
                            onLoginClick = { navigateTo(Screen.Login) },
                            onRegisterClick = { navigateTo(Screen.Register)}

                        )
                        Screen.Login  -> LoginScreen(
                            onLoginSuccess = { navigateTo(Screen.Home) },
                            onBackClick = { goBack()}
                        )
                        Screen.Register -> UserCreationScreen(
                            onRegisterSuccess = { navigateTo(Screen.Home)},
                            onBackClick = { goBack()}
                        )
                        Screen.Home -> UserHomeScreen(
                            onFriendsClick = { navigateTo(Screen.Friends) },
                        )
                        Screen.Friends -> FriendsScreen(
                            onAddFriendClick = { navigateTo(Screen.AddFriend) },
                            onBackClick = { goBack()}
                        )
                        Screen.AddFriend -> AddFriendScreen(
                            onFriendAdded = { goBack() },
                            onBackClick = { goBack() }
                        )
                    }

                }
            }
        }
    }
    // Used to represent the different screens we got, sealed classes are popular for UI stuff in Kotlin, specially Android
    sealed class Screen {
        object Select : Screen()
        object Login : Screen()
        object Register : Screen()
        object Home : Screen()
        object Friends : Screen()
        object AddFriend : Screen()
    }
}




