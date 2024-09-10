package poruka.com.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import poruka.com.ui.theme.PorukaTheme


@Composable
fun UserHomeScreen(modifier: Modifier = Modifier) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text("Welcome to the Home Screen My Dude!")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun UserHomeScreenPreview() {
    PorukaTheme {
        UserHomeScreen()
    }
}