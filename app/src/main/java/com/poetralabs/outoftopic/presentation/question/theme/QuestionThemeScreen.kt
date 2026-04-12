package com.poetralabs.outoftopic.presentation.question.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity
import com.poetralabs.outoftopic.core.theme.AnthropicNearBlack
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Parchment
import com.poetralabs.outoftopic.presentation.component.ThemeCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuestionThemeScreen(
    onBack: () -> Unit,
    onGuide: () -> Unit = {},
    onThemeClick: (ThemeEntity) -> Unit,
    viewModel: QuestionThemeViewModel = koinViewModel()
) {
    val themes by viewModel.themes.collectAsState()

    Scaffold(
        containerColor = Parchment,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AnthropicNearBlack
                    )
                }
                IconButton(onClick = onGuide) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = AnthropicNearBlack
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pertanyaan Random",
                style = MaterialTheme.typography.headlineMedium,
                color = AnthropicNearBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bikin obrolan makin seru dengan pertanyaan random.",
                style = MaterialTheme.typography.bodyMedium,
                color = OliveGray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(themes) { index, theme ->
                    ThemeCard(
                        theme = theme,
                        index = index,
                        onClick = {
                            viewModel.logThemeSelection(theme.id)
                            onThemeClick(theme)
                        }
                    )
                }
            }
        }
    }
}
