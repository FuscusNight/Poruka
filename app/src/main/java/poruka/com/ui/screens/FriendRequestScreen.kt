package poruka.com.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import poruka.com.ui.theme.PorukaTheme
import poruka.data.AuthRepository

@Composable
fun FriendRequestScreen(
    onBackClick: () -> Unit,
    onFriendAccepted: () -> Unit,
    onFriendRejected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInPreview = LocalInspectionMode.current
    val authRepository = if (!isInPreview) AuthRepository() else null
    val scope = rememberCoroutineScope()
    var friendRequests by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun fetchFriendRequests() {
        scope.launch {
            val result = authRepository?.getFriendRequest()
            if (result?.isSuccess == true) {
                friendRequests = result.getOrDefault(emptyList())
            } else {
                errorMessage = result?.exceptionOrNull()?.message
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isInPreview && authRepository != null)
        fetchFriendRequests()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            // Top Bar with back button and title
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
                Text(text = "Friend Requests", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }

            // Display Friend Requests
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (friendRequests.isNotEmpty()) {
                    friendRequests.forEach { request ->
                        val senderId = request["senderId"] ?: "Unknown"
                        val senderName = request["senderName"] ?: "Unknown Name"

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            // Profile Picture Placeholder
                            Canvas(
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(8.dp)
                            ) {
                                drawCircle(color = Color.Gray)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Friend Information
                            Column {
                                Text(
                                    text = senderName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Accept and Reject Buttons
                            Row {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            // Goes into authRep, access the acceptFriendReqeust functions, feeds it the sender ID argument
                                            val result = authRepository?.acceptFriendRequest(senderId)
                                            if (result?.isSuccess == true) {
                                                //fetchFriendRequests()
                                                // Remove the friend request locally without refetching the whole list
                                                friendRequests = friendRequests.filter { it["senderId"] != senderId }

                                                onFriendAccepted()
                                            } else {
                                                errorMessage = result?.exceptionOrNull()?.message
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text("Yes")
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            val result = authRepository?.rejectFriendRequest(senderId)
                                            if (result?.isSuccess == true) {
                                                // Remove the friend request locally without refetching the whole list
                                                friendRequests = friendRequests.filter { it["senderId"] != senderId }

                                                onFriendRejected()
                                            } else {
                                                errorMessage = result?.exceptionOrNull()?.message
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("No")
                                }
                            }
                        }
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

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun FriendRequestScreenPreview() {
    PorukaTheme {
        FriendRequestScreen( onBackClick = {}, onFriendAccepted = {}, onFriendRejected = {})
    }
}

