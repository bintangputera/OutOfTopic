package com.poetralabs.outoftopic.presentation.minigames

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.poetralabs.outoftopic.core.theme.AnthropicNearBlack
import com.poetralabs.outoftopic.core.theme.GameColor
import com.poetralabs.outoftopic.core.theme.Ivory
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Parchment
import com.poetralabs.outoftopic.core.theme.WarmSilver

data class MiniGameItem(
    val title: String,
    val description: String,
    val bgColor: Color,
    val textColor: Color = Ivory,
    val onClick: () -> Unit
)

@Composable
fun MiniGamesScreen(
    onBack: () -> Unit = {},
    onSambungKata: () -> Unit = {}
) {
    val games = listOf(
        MiniGameItem(
            title = "Sambung Kata",
            description = "Sambung kata dari huruf terakhir. Kehabisan waktu atau salah huruf? Out!",
            bgColor = GameColor,
            onClick = onSambungKata
        )
    )

    Scaffold(
        containerColor = Parchment,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AnthropicNearBlack
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mini Games",
                style = MaterialTheme.typography.headlineMedium,
                color = AnthropicNearBlack,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Pilih game buat ramaikan tongkrongan!",
                style = MaterialTheme.typography.bodyMedium,
                color = OliveGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            games.forEach { game ->
                MiniGameCard(game = game)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MiniGameCard(game: MiniGameItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { game.onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = game.bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = game.textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = game.textColor.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = game.textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
