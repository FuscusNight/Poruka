package poruka.com.ui.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import poruka.com.ui.theme.PorukaTheme
import poruka.data.AuthRepository

@Composable
fun ChatScreen(
    friendId: String,
    friendName: String,
    friendProfilePictureUrl: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val isInPreview = LocalInspectionMode.current
    val authRepository = if (!isInPreview) AuthRepository() else null
    var messages by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }
    var friendName by remember { mutableStateOf("Unknown") }  // Default values
    var friendProfilePictureUrl by remember { mutableStateOf("") }
    var currentUserProfilePictureUrl by remember { mutableStateOf<String?>(null) }
    val defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/poruka-d701c.appspot.com/o/DefaultProfileJPG.jpg?alt=media&token=3999e5c2-04ef-426d-a9f4-17ad605cf1e1"

    // Fetch the friend's details and current user's profile picture URL
    LaunchedEffect(friendId) {
        if (!isInPreview && authRepository != null) {
            // Fetch friend's details
            val friendDetails = authRepository.getFriendDetails(friendId)
            friendName = friendDetails["userName"] ?: "Unknown"
            friendProfilePictureUrl = friendDetails["profilePictureUrl"] ?: ""

            // Fetch current user's profile picture URL
            currentUserProfilePictureUrl = authRepository.getCurrentUserProfilePictureUrl()

            // Handle real-time updates with listener registration
            val listenerRegistration = authRepository.getChatMessagesRealTime(friendId) { result ->
                if (result.isSuccess) {
                    messages = result.getOrDefault(emptyList())
                }
            }
        } else {
            // Mock data for preview
            messages = listOf(
                mapOf("senderId" to "1", "content" to "Hello!", "timestamp" to "12:34"),
                mapOf("senderId" to "2", "content" to "Hi there!", "timestamp" to "12:35"),
                mapOf("senderId" to "1", "content" to "How's it going?", "timestamp" to "12:36")
            )
            currentUserProfilePictureUrl = defaultProfileImageUrl
        }
    }


    // Handle real-time updates with listener registration
    DisposableEffect(friendId) {
        val listenerRegistration = authRepository?.getChatMessagesRealTime(friendId) { result ->
            if (result.isSuccess) {
                messages = result.getOrDefault(emptyList())
            }
        }

        // Clean up the listener when the screen is removed
        onDispose {
            listenerRegistration?.remove()
        }
    }

    Surface(modifier = Modifier.fillMaxSize().imePadding()) {
        Column {
            // Top bar with a back button
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
                Text(text = "$friendName Chat", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }

            // Display chat messages
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                messages.forEach { message ->
                    val isCurrentUser = message["senderId"] != friendId // Check if the sender is the current user
                    ChatBubble(
                        messageContent = message["content"] ?: "",
                        timestamp = message["timestamp"] ?: "",
                        senderProfileUrl = if (message["senderId"] == friendId) friendProfilePictureUrl else currentUserProfilePictureUrl ?: "",
                        isCurrentUser = isCurrentUser
                    )
                }
            }


            // TextField for typing new message
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Type your message...") }
                )
                Button(onClick = {
                    scope.launch {
                        if (authRepository != null) {
                            authRepository.sendMessage(friendId, newMessage)
                        }
                        newMessage = "" // Clear after sending
                    }
                }) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun ChatBubble(messageContent: String, timestamp: String, senderProfileUrl: String, isCurrentUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start, // Align based on sender
        verticalAlignment = Alignment.Top
    ) {
        if (!isCurrentUser) {
            // Profile Picture with fallback to placeholder
            if (senderProfileUrl.isNotEmpty()) {
                // Load the profile picture using Coil
                Image(
                    painter = rememberAsyncImagePainter(senderProfileUrl),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                // Fallback to gray circle if no profile picture URL is available
                Canvas(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                ) {
                    drawCircle(color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Message Content
        Column {
            // Display message content
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (isCurrentUser) Color(0xFFDFF7C8) else Color(0xFFE0E0E0),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = messageContent, modifier = Modifier.padding(8.dp))
            }

            // Timestamp below the message
            Text(text = timestamp, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // Profile Picture on the right if it's the user
            Image(
                painter = rememberAsyncImagePainter(senderProfileUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }
    }
}

fun formatTimestamp(date: java.util.Date): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
    return formatter.format(date)
}


@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun PreviewChatScreen() {
    PorukaTheme {
        ChatScreen(
            friendId = "1",
            friendName = "John Doe",
            friendProfilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/poruka-d701c.appspot.com/o/DefaultProfileJPG.jpg?alt=media&token=3999e5c2-04ef-426d-a9f4-17ad605cf1e1",
            onBackClick = {}
        )
    }
}


