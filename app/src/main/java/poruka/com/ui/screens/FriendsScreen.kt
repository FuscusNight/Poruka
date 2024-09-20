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
fun FriendsScreen(
    onAddFriendClick: () -> Unit,
    onViewFriendRequestsClick: () -> Unit,
    onBackClick: () -> Unit ,
    modifier: Modifier = Modifier
) {
    val isInPreview = LocalInspectionMode.current // Check if we're in preview mode
    val authRepository = if (!isInPreview) AuthRepository() else null
    val scope = rememberCoroutineScope()
    var friends by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pendingRequestsCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        if (!isInPreview && authRepository != null) {
            // async way for kotlin to performs tasks without blocking anything
            scope.launch {
                val result = authRepository.getFriends()
                if (result.isSuccess) {
                    friends = result.getOrDefault(emptyList())
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                }

                // Fetch pending friend requests count
                val requestsResult = authRepository.getFriendRequest()
                if (requestsResult.isSuccess) {
                    pendingRequestsCount = requestsResult.getOrDefault(emptyList()).size
                }
            }
        } else {
            // Mock data for preview
            friends = listOf(
                mapOf("userName" to "John Doe", "email" to "john.doe@example.com"),
                mapOf("userName" to "Jane Smith", "email" to "jane.smith@example.com")
            )
            pendingRequestsCount = 1 // Mock pending requests count for preview
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            // Top Bar with back button and "Friends List" title
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
                Text(text = "Friends List", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Row {
                    // Friend Request Button with a placeholder number
                    Button(
                        onClick = onViewFriendRequestsClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF57DC8B),
                            contentColor = Color.White
                        )
                    ) {
                        Text("$pendingRequestsCount Pending")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAddFriendClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B57DC),
                            contentColor = Color.White
                        )
                    ) {
                        Text("+")
                    }
                }
            }

            // Display Friends List
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (friends.isNotEmpty()) {
                    friends.forEach { friend ->
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
                                    text = friend["userName"] ?: "Unknown",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Placeholder message", // Temporary placeholder for the last message
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Timestamp Placeholder
                            Text(
                                text = "12:34", // Placeholder timestamp
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                } else if (errorMessage != null) {
                    Text(text = "Error: $errorMessage")
                } else {
                    Text(text = "No Friends :(")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun FriendsScreenPreview() {
    PorukaTheme {
        FriendsScreen(onAddFriendClick = {}, onBackClick = {}, onViewFriendRequestsClick = {})
    }
}