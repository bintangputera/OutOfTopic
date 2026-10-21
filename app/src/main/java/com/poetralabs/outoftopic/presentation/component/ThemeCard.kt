package com.poetralabs.outoftopic.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity
import com.poetralabs.outoftopic.core.theme.BiruLaut
import com.poetralabs.outoftopic.core.theme.GarisKartu
import com.poetralabs.outoftopic.core.theme.HijauDaun
import com.poetralabs.outoftopic.core.theme.MerahCabe
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Tinta
import com.poetralabs.outoftopic.core.theme.WajahKartu

private val cardSuits = listOf("\u2660\uFE0E", "\u2665\uFE0E", "\u2666\uFE0E", "\u2663\uFE0E")
private val cardColors = listOf(MerahCabe, BiruLaut, HijauDaun, Tinta)
private val cardTilts = listOf(-1.5f, 1.2f, 1.0f, -1.2f)

/** Kartu tema: suit dan warna bergilir per indeks, miring tipis seperti kartu di atas meja. */
@Composable
fun ThemeCard(
    theme: ThemeEntity,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = cardColors[index % cardColors.size]
    val suit = cardSuits[index % cardSuits.size]
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val tilt by animateFloatAsState(
        targetValue = if (pressed) 0f else cardTilts[index % cardTilts.size],
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "themeTilt"
    )
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(168.dp)
            .rotate(tilt)
            .shadow(
                elevation = if (pressed) 12.dp else 5.dp,
                shape = shape,
                ambientColor = Tinta,
                spotColor = Tinta
            )
            .clip(shape)
            .background(WajahKartu)
            .border(1.dp, GarisKartu, shape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp)
                .border(1.5.dp, color.copy(alpha = 0.55f), RoundedCornerShape(9.dp))
        )
        Text(
            text = suit,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 14.dp, top = 11.dp)
        )
        Text(
            text = suit,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 11.dp)
                .rotate(180f)
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = theme.displayName.uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = theme.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = OliveGray,
                maxLines = 3
            )
        }
    }
}
