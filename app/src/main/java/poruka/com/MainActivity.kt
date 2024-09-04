package poruka.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import poruka.com.ui.screens.HomeScreen
import poruka.com.ui.theme.PorukaTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            PorukaTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomeScreen("Zmaj")

                }
            }
        }
    }
}
/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    PorukaTheme {
        HomeScreen("Android")
    }
}
*/


