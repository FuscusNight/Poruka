package poruka.com.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import poruka.data.AuthRepository

@Composable
fun FriendRequestScreen(
    onBackClick: () -> Unit,
    onFriendAccepted: () -> Unit,
    onFriendRejected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authRepository = AuthRepository()
    val scope = rememberCoroutineScope()
    var friendRequests by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun fetchFriendRequests() {
        scope.launch {
            val result = authRepository.getFriendRequest()
            if (result.isSuccess) {
                friendRequests = result.getOrDefault(emptyList())
            } else {
                errorMessage = result.exceptionOrNull()?.message
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchFriendRequests()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Back button at the top-left corner
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            ) {
                Text(text = "← Back", color = Color.Black)
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                if (friendRequests.isNotEmpty()) {
                    friendRequests.forEach { request ->
                        val senderId = request["senderId"] ?: "Unknown"
                        val senderName = request["senderName"] ?: "Unknown Name"
                        Text(text = "Friend request from: $senderId")
                        Row {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val result = authRepository.acceptFriendRequest(senderId)
                                        if (result.isSuccess) {
                                            fetchFriendRequests()
                                            onFriendAccepted()
                                        } else {
                                            errorMessage = result.exceptionOrNull()?.message
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Accept")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    scope.launch {
                                        val result = authRepository.rejectFriendRequest(senderId)
                                        if (result.isSuccess) {
                                            fetchFriendRequests()
                                            onFriendRejected()
                                        } else {
                                            errorMessage = result.exceptionOrNull()?.message
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Reject")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else if (errorMessage != null) {
                    Text(text = "Error: $errorMessage")
                } else {
                    Text(text = "No pending friend requests")
                }
            }
        }
    }
}