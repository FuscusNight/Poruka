package poruka.com.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
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
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isEmailVerified by remember { mutableStateOf(false) }
    // Retrieves the current Android Context, allowing the composable to interact with Android system resources
    // (e.g., show Toasts, access resources, start activities).
    val context = LocalContext.current

    val isInPreview = LocalInspectionMode.current
    val authRepository = if (!isInPreview) AuthRepository() else null

    // Coroutine scope for handling async operations
    val scope = rememberCoroutineScope()

    /** Launches an image picker to allow the user to select an image, and handles the result
    * (in this case, the Uri of the selected image) when the user makes a selection.
    * This also persists across recompositions.
    * Which allows the user to pick an image without reinitializing the launcher on each recomposition.
    */
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && authRepository != null) {
            scope.launch {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    val result = authRepository.uploadProfilePicture(userId, uri, context)
                    result.onSuccess {
                        profileImageUrl = it
                        Toast.makeText(context, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
                    }.onFailure { error ->
                        errorMessage = "Failed to upload image: ${error.message}"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                } else {
                    errorMessage = "Error: User ID not found."
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            errorMessage = "No image selected."
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    // Triggers the code inside the block to run only once when the composable enters the composition.
    // In this case, it fetches the current user data (email, username, profile image) asynchronously.
    // Using Unit as a key ensures that the block will only run once
    LaunchedEffect(Unit) {
        if (!isInPreview && authRepository != null) {
            val user = FirebaseAuth.getInstance().currentUser
            currentEmail = user?.email.orEmpty()

            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                currentUsername = userDocument.getString("userName").orEmpty()
                profileImageUrl = userDocument.getString("profilePictureUrl") ?: "https://firebasestorage.googleapis.com/v0/b/poruka-d701c.appspot.com/o/DefaultProfileJPG.jpg?alt=media&token=3999e5c2-04ef-426d-a9f4-17ad605cf1e1"
            }

            isEmailVerified = authRepository.isEmailVerified() // Check if email is verified
        } else {
            // Mock data for preview
            currentEmail = "user@example.com"
            currentUsername = "ExampleUser"
            profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/poruka-d701c.appspot.com/o/DefaultProfileJPG.jpg?alt=media&token=3999e5c2-04ef-426d-a9f4-17ad605cf1e1"
            isEmailVerified = true
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF4641D3))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBackClick) {
                    Text(text = "← Back", color = Color.White)
                }
                Text(text = "Edit Profile", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Display current profile image
                if (profileImageUrl != null) {
                    Image(
                        // Asynchronously loads the image from the provided URL (profileImageUrl) using Coil's painter,
                        // caches the result, and automatically recomposes the UI once the image is ready.
                        painter = rememberAsyncImagePainter(model = profileImageUrl),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(128.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Show default image
                    Image(
                        painter = rememberAsyncImagePainter(model = "your_default_image_url"),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(128.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button to upload profile image
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4641D3))
                ) {
                    Text("Upload Profile Picture")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display current email and username
                Text(
                    text = "Current Email: $currentEmail",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Current Username: $currentUsername",
                    style = MaterialTheme.typography.bodyMedium
                )

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
                if (!isInPreview) {
                    Button(
                        onClick = {
                            scope.launch {
                                val result = authRepository?.sendEmailVerification()
                                errorMessage = result?.exceptionOrNull()?.message ?: "Verification email sent!"
                            }
                        },
                        enabled = !isEmailVerified,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4641D3))
                    ) {
                        Text("Send Verification Email")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Save Changes Button
                Button(
                    onClick = {
                        scope.launch {
                            val result = authRepository?.updateUserDetails(
                                currentPassword = currentPassword,
                                newUsername = newUsername,
                                newEmail = newEmail,
                                newPassword = newPassword
                            )

                            if (result?.isSuccess == true) {
                                // Reload user data after saving changes
                                authRepository.reloadUser().onSuccess {
                                    // Refresh user info on screen
                                    val updatedUser = FirebaseAuth.getInstance().currentUser
                                    currentEmail = updatedUser?.email.orEmpty()
                                    isEmailVerified = updatedUser?.isEmailVerified ?: false

                                    val userId = authRepository.getCurrentUserId()
                                    if (userId != null) {
                                        val userDocument =
                                            FirebaseFirestore.getInstance().collection("users")
                                                .document(userId).get().await()
                                        currentUsername =
                                            userDocument.getString("userName").orEmpty()
                                    }

                                    onSaveChanges()
                                }.onFailure {
                                    errorMessage = it.message
                                }
                            } else {
                                errorMessage = result?.exceptionOrNull()?.message
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4641D3))
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
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun EditProfileScreenPreview() {
    PorukaTheme {
        EditProfileScreen(onSaveChanges = {}, onBackClick = {})
    }
}