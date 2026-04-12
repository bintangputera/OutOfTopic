package com.poetralabs.outoftopic.presentation.about

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.poetralabs.outoftopic.core.theme.AnthropicNearBlack
import com.poetralabs.outoftopic.core.theme.BorderCream
import com.poetralabs.outoftopic.core.theme.Ivory
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Parchment
import com.poetralabs.outoftopic.core.theme.StoneGray
import com.poetralabs.outoftopic.core.theme.TerracottaBrand

@Composable
fun AboutScreen(
    onBack: () -> Unit = {},
    onFeedback: () -> Unit = {}
) {
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AlertDialog(
            containerColor = Ivory,
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    "Out of Topic",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AnthropicNearBlack
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Versi 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OliveGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Aplikasi seru untuk meramaikan tongkrongan kamu — Truth or Dare, Random Question, dan Mini Games.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AnthropicNearBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "\u00A9 2024 Poetra Labs",
                        style = MaterialTheme.typography.bodySmall,
                        color = StoneGray
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(
                        "Tutup",
                        color = TerracottaBrand,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        )
    }

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
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Ivory),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth(),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(BorderCream)
                )
            ) {
                AboutMenuItem(
                    title = "Beri Feedback",
                    subtitle = "Bagikan pengalamanmu menggunakan Out of Topic",
                    onClick = onFeedback
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BorderCream
                )
                AboutMenuItem(
                    title = "Tentang Aplikasi",
                    subtitle = "Versi & info aplikasi",
                    onClick = { showAboutDialog = true }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Out of Topic",
                style = MaterialTheme.typography.headlineSmall,
                color = AnthropicNearBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Versi 1.0.0 \u00B7 \u00A9 2026 Poetra Labs",
                style = MaterialTheme.typography.bodySmall,
                color = StoneGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun AboutMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = AnthropicNearBlack
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OliveGray
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = StoneGray,
            modifier = Modifier.size(20.dp)
        )
    }
}
