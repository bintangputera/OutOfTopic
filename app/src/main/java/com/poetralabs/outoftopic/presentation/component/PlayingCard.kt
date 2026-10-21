package com.poetralabs.outoftopic.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.poetralabs.outoftopic.core.theme.GarisKartu
import com.poetralabs.outoftopic.core.theme.Tinta
import com.poetralabs.outoftopic.core.theme.WajahKartu

/**
 * Kartu remi: muka krem dengan [rank] dan [suit] di dua pojok, atau punggung
 * kartu bermotif garis silang kalau [faceDown]. [content] diletakkan di tengah.
 */
@Composable
fun PlayingCard(
    color: Color,
    rank: String,
    suit: String,
    modifier: Modifier = Modifier,
    tilt: Float = 0f,
    faceDown: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val innerShape = RoundedCornerShape(11.dp)

    Box(
        modifier = modifier
            .rotate(tilt)
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = Tinta,
                spotColor = Tinta
            )
            .clip(shape)
            .background(if (faceDown) color else WajahKartu)
            .border(1.dp, GarisKartu, shape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .clip(innerShape)
                .then(if (faceDown) Modifier.drawBehind { drawCrosshatch(WajahKartu.copy(alpha = 0.22f)) } else Modifier)
                .border(
                    width = 2.dp,
                    color = if (faceDown) WajahKartu.copy(alpha = 0.8f) else color.copy(alpha = 0.55f),
                    shape = innerShape
                )
        )

        if (!faceDown) {
            CardCornerIndex(
                rank = rank,
                suit = suit,
                color = color,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 18.dp)
            )
            CardCornerIndex(
                rank = rank,
                suit = suit,
                color = color,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 18.dp)
                    .rotate(180f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp, vertical = 44.dp),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
internal fun CardCornerIndex(
    rank: String,
    suit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = rank, style = MaterialTheme.typography.headlineMedium, color = color)
        Text(text = suit, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCrosshatch(color: Color) {
    val step = 16.dp.toPx()
    val stroke = 1.dp.toPx()
    var x = -size.height
    while (x < size.width) {
        drawLine(color, Offset(x, 0f), Offset(x + size.height, size.height), stroke)
        drawLine(color, Offset(x + size.height, 0f), Offset(x, size.height), stroke)
        x += step
    }
}
