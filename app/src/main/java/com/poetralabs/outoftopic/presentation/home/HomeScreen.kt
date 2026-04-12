package com.poetralabs.outoftopic.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.poetralabs.outoftopic.core.navigation.AboutRoute
import com.poetralabs.outoftopic.core.navigation.MiniGamesRoute
import com.poetralabs.outoftopic.core.navigation.QuestionThemeRoute
import com.poetralabs.outoftopic.core.navigation.TruthOrDareRoute
import com.poetralabs.outoftopic.core.theme.AnthropicNearBlack
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Parchment
import com.poetralabs.outoftopic.presentation.component.HomeMenu
import com.poetralabs.outoftopic.presentation.component.HomeMenuCard

@Composable
fun HomeScreen(
    navController: NavController
) {
    Scaffold(
        containerColor = Parchment
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "OUT OF\nTOPIC",
                    style = MaterialTheme.typography.displayLarge,
                    color = AnthropicNearBlack,
                    textAlign = TextAlign.Center
                )
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { navController.navigate(AboutRoute) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Lainnya",
                        tint = OliveGray
                    )
                }
            }
            Text(
                text = "Pilih aktivitas yang kamu mau,\nbiar tongkrongan makin asik",
                color = OliveGray,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            HomeMenuCard(
                menu = HomeMenu.TruthOrDare,
                onClick = { navController.navigate(TruthOrDareRoute) }
            )
            HomeMenuCard(
                menu = HomeMenu.Question,
                onClick = { navController.navigate(QuestionThemeRoute) }
            )
            HomeMenuCard(
                menu = HomeMenu.MiniGames,
                onClick = { navController.navigate(MiniGamesRoute) }
            )
        }
    }
}
