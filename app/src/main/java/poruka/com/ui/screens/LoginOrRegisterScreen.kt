package poruka.com.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import poruka.com.ui.theme.PorukaTheme

@Composable
fun LoginOrRegisterScreen(
    /**
     * Units are special types that represent absence of a result, so similiar to void
     * Meaning the function still does something, like updating UI but returns no meaningful value
     *
     * onLoginClick and onRegisterClick are callbacks, They don't return any value but perform actions when the button is clicked
     */
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Button(
                onClick = onLoginClick, // This executes the function passed in as the callback when the button is clicked
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B57DC),
                    contentColor = Color.White
                )
            ) {
                Text("Log In")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF57DC8B),
                    contentColor = Color.White
                )
            ) {
                Text("Register")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun LoginOrRegisterScreenPreview() {
    PorukaTheme {
        LoginOrRegisterScreen(onLoginClick = {}, onRegisterClick = {})
    }
}