package poruka.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import poruka.com.ui.AppNavHost
import poruka.com.ui.theme.PorukaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Sets up a custom theme i made in my resources folders, for the start up screen
        setTheme(R.style.Theme_Poruka)
        super.onCreate(savedInstanceState)
        // Starts firebase for the whole application
        FirebaseApp.initializeApp(this)

        /* Initialize Firebase App Check with Play Integrity
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
           */
        setContent {
            PorukaTheme {
                //Defines area of screen where content is drawn
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Initialize NavController which will handle all the navigation between screens
                    val navController = rememberNavController()
                    // Start the AppNavHost, passing the navController to it
                    AppNavHost(navController = navController) // This calls the AppNavHost function navigation logic situated in Navigation.kt
                }
            }
        }
    }
}







