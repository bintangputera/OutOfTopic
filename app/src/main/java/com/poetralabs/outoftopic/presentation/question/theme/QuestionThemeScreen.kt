package com.poetralabs.outoftopic.presentation.question.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity
import com.poetralabs.outoftopic.core.theme.BackgroundWhite
import com.poetralabs.outoftopic.presentation.component.ThemeCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuestionThemeScreen(
    onBack: () -> Unit,
    onThemeClick: (ThemeEntity) -> Unit,
    viewModel: QuestionThemeViewModel = koinViewModel()
) {
    val themes by viewModel.themes.collectAsState()

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "arrowBack",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pertanyaan Random",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
            Text(
                text = "Bikin obrolan makin seru dengan pertanyaan random.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(themes) { theme ->
                    ThemeCard(
                        theme = theme,
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