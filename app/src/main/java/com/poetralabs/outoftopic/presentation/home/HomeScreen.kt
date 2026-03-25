package com.poetralabs.outoftopic.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.poetralabs.outoftopic.core.navigation.QuestionThemeRoute
import com.poetralabs.outoftopic.core.navigation.TruthOrDareRoute
import com.poetralabs.outoftopic.core.theme.BackgroundWhite
import com.poetralabs.outoftopic.presentation.component.HomeMenu
import com.poetralabs.outoftopic.presentation.component.HomeMenuCard

@Composable
fun HomeScreen(
    navController: NavController
) {
    Scaffold(
        containerColor = BackgroundWhite
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "OUT OF\nTOPIC",
                style = MaterialTheme.typography.displayLarge,
                color = Color.Black
            )
            Text(
                text = "Pilih aktivitas yang kamu mau,\n" +
                        "biar tongkrongan makin asik",
                 color = Color.Black,
                textAlign = TextAlign.Center
            )
            HomeMenuCard(
                menu = HomeMenu.TruthOrDare,
                onClick = { navController.navigate(TruthOrDareRoute) }
            )
            HomeMenuCard(
                menu = HomeMenu.Question,
                onClick = {
                    navController.navigate(QuestionThemeRoute)
                }
            )
        }
    }
}