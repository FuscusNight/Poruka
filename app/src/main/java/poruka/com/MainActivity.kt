package poruka.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
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
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Select) }
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    when (currentScreen) {
                        Screen.Select -> LoginOrRegisterScreen(
                            onLoginClick = { currentScreen =  Screen.Login},
                            onRegisterClick = { currentScreen = Screen.Register}

                        )
                        Screen.Login  -> LoginScreen(
                            onLoginSuccess = { currentScreen = Screen.Home}
                        )
                        Screen.Register -> UserCreationScreen(
                            onRegisterSuccess = { currentScreen = Screen.Home}
                        )
                        Screen.Home -> UserHomeScreen()
                    }

                }
            }
        }
    }

    sealed class Screen {
        object Select : Screen()
        object Login : Screen()
        object Register : Screen()
        object Home : Screen()
    }
}




