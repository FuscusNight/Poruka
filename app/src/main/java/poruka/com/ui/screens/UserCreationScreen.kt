package poruka.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import poruka.com.ui.theme.PorukaTheme
import poruka.data.AuthRepository


@Composable
fun UserCreationScreen(onRegisterSuccess: () -> Unit,
                       onBackClick: () -> Unit ,
                       modifier: Modifier = Modifier) {
    /**
     * Remember is a function to store values in memory of a composable function, even when redrawing UI
     * mutableStateOf can be read and modified
     * by using "by" keyword, we are stating that this getter/getter&setter is provided elsewhere  (https://stackoverflow.com/questions/38250022/what-does-by-keyword-do-in-kotlin)
     */
    var emailField by remember { mutableStateOf("") }
    var nameField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    val isInPreview = LocalInspectionMode.current
    val authRepository = if (!isInPreview) AuthRepository() else null
    val scope = rememberCoroutineScope()


    // https://developer.android.com/develop/ui/compose/layouts/basics
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            // Top Bar with back button and "Register" title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBackClick) {
                    Text(text = "← Back", color = Color.Black)
                }
                Text(text = "Register", style = MaterialTheme.typography.titleLarge)
            }

            // Central content: input fields and submit button
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextField(
                    value = emailField,
                    onValueChange = { emailField = it },
                    label = { Text("E-Mail") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = nameField,
                    onValueChange = { nameField = it },
                    label = { Text("User Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = passwordField,
                    onValueChange = { passwordField = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch {
                            val registerResult =
                                authRepository?.registerUser(emailField, passwordField, nameField)

                            if (registerResult?.isSuccess == true) {
                                result = "User Registered Successfully!"
                                onRegisterSuccess()
                            } else {
                                result =
                                    "Registration Failed: ${registerResult?.exceptionOrNull()?.message}"
                            }
                        }
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
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun UserCreationScreenPreview() {
    PorukaTheme {
        UserCreationScreen(onRegisterSuccess = {}, onBackClick = {})
    }
}