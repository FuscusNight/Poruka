package poruka.com.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import poruka.com.ui.theme.PorukaTheme
import poruka.data.AuthRepository


@Composable
fun FriendsScreen(
    onAddFriendClick: () -> Unit,
    onBackClick: () -> Unit ,
    modifier: Modifier = Modifier
) {
    val authRepository = AuthRepository()
    val scope = rememberCoroutineScope()
    var friends by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            val result = authRepository.getFriends()
            if (result.isSuccess) {
                friends = result.getOrDefault(emptyList())
            } else {
                errorMessage = result.exceptionOrNull()?.message
            }
        }
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
                if (friends.isNotEmpty()) {
                    friends.forEach { friend ->
                        Text(text = "Friend: ${friend["userName"]}, Email: ${friend["email"]}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else if (errorMessage != null) {
                    Text(text = "Error: $errorMessage")
                } else {
                    Text(text = "No Friends :(")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddFriendClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B57DC),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Add Friend")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun FriendsScreenPreview() {
    PorukaTheme {
        FriendsScreen(onAddFriendClick = {}, onBackClick = {})
    }
}