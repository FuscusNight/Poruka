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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope // a coroutine allows an app to perform tasks that might take some time (like fetching data from the internet) without freezing the screen or making the app unresponsive.
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import poruka.com.ui.theme.PorukaTheme
import poruka.data.AuthRepository



@Composable
fun HomeScreen(name: String, modifier: Modifier = Modifier) {
    /**
     * Remember is a function to store values in memory of a composable function, even when redrawing UI
     * mutableStateOf can be read and modified
     * "by" using "by" keyword, you are stating that this getter/getter&setter is provided elsewhere  (https://stackoverflow.com/questions/38250022/what-does-by-keyword-do-in-kotlin)
     */
    var emailField by remember { mutableStateOf("") }
    var nameField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    val authRepository = AuthRepository()
    val scope = rememberCoroutineScope()


    Surface (modifier = Modifier.fillMaxSize()){
        // https://developer.android.com/develop/ui/compose/layouts/basics
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            TextField(
                value = emailField,
                onValueChange = { emailField = it},
                label = { Text("E-Mail")},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = nameField,
                onValueChange = { nameField = it},
                label = { Text("User Name")},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = passwordField,
                onValueChange = { passwordField = it},
                label = { Text("Password")},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button (
                onClick = {
                    // Since registerUser is a suspend function ie asynchronous , gotta call it inside a coroutine such as rememberCoroutineScope
                    scope.launch { //  statement starts a new coroutine within the coroutine scope that was remembered using rememberCoroutineScope
                        val registerResult = authRepository.registerUser(emailField, passwordField, nameField )

                        result = if (registerResult.isSuccess) {
                            "User Register Succesfully!"
                        } else {
                            "Registration Failed: ${registerResult.exceptionOrNull()?.message}"
                        }
                    }
                    result =  "Inputs are: $passwordField, $emailField, $nameField"
                },
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B57DC),
                    contentColor = Color.White
                )
            ) {
                Text("Submit")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = result)

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    PorukaTheme {
        HomeScreen("Android")
    }
}