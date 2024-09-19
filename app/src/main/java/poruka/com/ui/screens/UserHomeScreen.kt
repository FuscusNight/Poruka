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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import poruka.com.ui.theme.PorukaTheme
import poruka.data.AuthRepository


@Composable
fun UserHomeScreen(onFriendsClick: () -> Unit,
                   modifier: Modifier = Modifier) {

    val isInPreview = LocalInspectionMode.current
    val authRepository = if (!isInPreview) AuthRepository() else null

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            // Top Bar with "Home" title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(text = "Home", style = MaterialTheme.typography.titleLarge)

            }

            // Central content: welcome message and button
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Welcome to the Home Screen My Dude!")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onFriendsClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B57DC),
                        contentColor = Color.White
                    )
                ) {
                    Text("Go to Friends List")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun UserHomeScreenPreview() {
    PorukaTheme {
        UserHomeScreen(onFriendsClick = {})
    }
}