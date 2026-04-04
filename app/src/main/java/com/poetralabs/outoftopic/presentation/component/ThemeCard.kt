package com.poetralabs.outoftopic.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity

private val themeCardColors = listOf(
    Color(0xFF797DFF), // Taro
    Color(0xFFFFAE43), // LightOrange
    Color(0xFFFF6B6B), // Coral
    Color(0xFF4ECDC4), // Teal
    Color(0xFFFF8CC8), // Pink
    Color(0xFF6BCB77), // Green
    Color(0xFFFFD93D), // Yellow
    Color(0xFF845EC2), // Purple
)

@Composable
fun ThemeCard(
    theme: ThemeEntity,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = themeCardColors[index % themeCardColors.size]
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = theme.description,
                    style = MaterialTheme.typography.titleMedium.copy(
                        lineHeight = 18.sp, fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}