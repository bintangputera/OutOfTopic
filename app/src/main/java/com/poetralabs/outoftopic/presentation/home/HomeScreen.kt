package com.poetralabs.outoftopic.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.poetralabs.outoftopic.R
import com.poetralabs.outoftopic.presentation.component.ThemeCard
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onThemeClick: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val themes by viewModel.themes.collectAsState()

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painterResource(R.drawable.group_1), contentDescription = null, modifier = Modifier.size(192.dp))
                    Text(
                        "Satu tap. Satu pertanyaan. Bisa jadi panjang.",
                        color = Color.White,
                    )
                    Text(
                        "Pilih tema dan biarkan obrolan mengalir.",
                        color = Color.White,
                    )
                }
            }
            items(themes) { theme ->
                ThemeCard(
                    theme = theme,
                    onClick = { 
                        viewModel.logThemeSelection(theme.id)
                        onThemeClick(theme.id) 
                    }
                )
            }
        }
    }
}