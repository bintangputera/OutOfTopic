package com.poetralabs.outoftopic.presentation.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poetralabs.outoftopic.R
import com.poetralabs.outoftopic.core.theme.BiruLaut
import com.poetralabs.outoftopic.core.theme.GarisKartu
import com.poetralabs.outoftopic.core.theme.HijauDaun
import com.poetralabs.outoftopic.core.theme.KuningAngkot
import com.poetralabs.outoftopic.core.theme.MerahCabe
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Tinta
import com.poetralabs.outoftopic.core.theme.WajahKartu

/**
 * Tiap menu tampil sebagai kartu remi: [rank] dan [suit] di pojok, warna cetak
 * sebagai aksen, dan sedikit miring seperti kartu yang ditebar di meja.
 */
enum class HomeMenu(
    val title: String,
    val desc: String,
    val rank: String,
    val suit: String,
    val color: Color,
    val badgeColor: Color,
    val tilt: Float,
    val icon: Int
) {
    TruthOrDare(
        title = "Truth or Dare",
        desc = "Jujur-jujuran atau terima tantangannya",
        rank = "A",
        suit = "\u2660\uFE0E",
        color = MerahCabe,
        badgeColor = KuningAngkot,
        tilt = 1.8f,
        icon = R.drawable.ic_tod,
    ),
    Question(
        title = "Random Question",
        desc = "Pertanyaan acak biar obrolan nggak mati",
        rank = "Q",
        suit = "\u2666\uFE0E",
        color = BiruLaut,
        badgeColor = KuningAngkot,
        tilt = -2.5f,
        icon = R.drawable.ic_question
    ),
    MiniGames(
        title = "Mini Games",
        desc = "Permainan kecil buat nunggu makanan datang",
        rank = "J",
        suit = "\u2663\uFE0E",
        color = HijauDaun,
        badgeColor = KuningAngkot,
        tilt = -1.2f,
        icon = R.drawable.ic_light_bulb
    )
}

@Composable
fun HomeMenuCard(
    menu: HomeMenu,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    // Saat ditekan kartu "terangkat" dan lurus, saat dilepas kembali miring.
    val tilt by animateFloatAsState(
        targetValue = if (pressed) 0f else menu.tilt,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardTilt"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(168.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .rotate(tilt)
            .shadow(
                elevation = if (pressed) 14.dp else 6.dp,
                shape = shape,
                ambientColor = Tinta,
                spotColor = Tinta
            )
            .clip(shape)
            .background(WajahKartu)
            .border(1.dp, GarisKartu, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Bingkai dalam, seperti garis cetak di kartu remi.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .border(1.5.dp, menu.color.copy(alpha = 0.55f), RoundedCornerShape(9.dp))
        )

        CornerIndex(
            menu = menu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 14.dp)
        )
        CornerIndex(
            menu = menu,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 14.dp)
                .rotate(180f)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 52.dp, end = 52.dp, top = 22.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = menu.title.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = menu.color
                )
                Text(
                    text = menu.desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = OliveGray
                )
            }
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(menu.badgeColor)
                    .border(2.dp, Tinta, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(menu.icon),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp)
                )
            }
        }
    }
}

@Composable
private fun CornerIndex(menu: HomeMenu, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = menu.rank,
            style = MaterialTheme.typography.headlineSmall,
            color = menu.color
        )
        Text(
            text = menu.suit,
            style = MaterialTheme.typography.titleMedium,
            color = menu.color
        )
    }
}

@Composable
@Preview(device = "id:pixel_4_xl", showSystemUi = true)
fun HomeMenuPreview() {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        HomeMenuCard(menu = HomeMenu.Question, onClick = {})
    }
}
