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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import poruka.com.ui.theme.PorukaTheme
import poruka.data.AuthRepository

@Composable
fun EditProfileScreen(
    onSaveChanges: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPassword by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var currentEmail by remember { mutableStateOf("") }
    var currentUsername by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isEmailVerified by remember { mutableStateOf(false) }

    val isInPreview = LocalInspectionMode.current

    // Coroutine scope for handling async operations
    val scope = rememberCoroutineScope()
    val authRepository = AuthRepository()


    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        currentEmail = user?.email.orEmpty()

        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
            currentUsername = userDocument.getString("userName").orEmpty()
        }

        isEmailVerified = authRepository.isEmailVerified() // Check if email is verified
    }

    Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            TextButton(onClick = onBackClick) {
                Text(text = "← Back", color = MaterialTheme.colorScheme.primary)
            }
            Text(text = "Edit Profile", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Display current email and username
            Text(text = "Current Email: $currentEmail", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Current Username: $currentUsername", style = MaterialTheme.typography.bodyMedium)

            // Input fields for username, email, and new password
            TextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("New Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                label = { Text("New Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Field to confirm current password
            TextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email verification status
            Text(text = "Email Verified: ${if (isEmailVerified) "Yes" else "No"}")

            // Send verification email button
            Button(
                onClick = {
                    scope.launch {
                        val result = authRepository.sendEmailVerification()
                        errorMessage = result.exceptionOrNull()?.message ?: "Verification email sent!"
                    }
                },
                enabled = !isEmailVerified,  // Disable if email is verified
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Send Verification Email")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Changes Button
            Button(
                onClick = {
                    scope.launch {
                        val result = authRepository.updateUserDetails(
                            currentPassword = currentPassword,
                            newUsername = newUsername,
                            newEmail = newEmail,
                            newPassword = newPassword
                        )

                        if (result.isSuccess) {
                            // Reload user data after saving changes
                            authRepository.reloadUser().onSuccess {
                                // Refresh user info on screen
                                val updatedUser = FirebaseAuth.getInstance().currentUser
                                currentEmail = updatedUser?.email.orEmpty()
                                isEmailVerified = updatedUser?.isEmailVerified ?: false

                                val userId = authRepository.getCurrentUserId()
                                if (userId != null) {
                                    val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                                    currentUsername = userDocument.getString("userName").orEmpty()
                                }

                                onSaveChanges()
                            }.onFailure {
                                errorMessage = it.message
                            }
                        } else {
                            errorMessage = result.exceptionOrNull()?.message
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Changes")
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun EditProfileScreenPreview() {
    PorukaTheme {
        EditProfileScreen(onSaveChanges = {}, onBackClick = {})
    }
}