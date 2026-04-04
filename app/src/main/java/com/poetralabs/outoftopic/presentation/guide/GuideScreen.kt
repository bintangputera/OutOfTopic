package com.poetralabs.outoftopic.presentation.guide

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.poetralabs.outoftopic.R
import com.poetralabs.outoftopic.core.theme.BackgroundWhite
import com.poetralabs.outoftopic.core.theme.DarkOrange
import com.poetralabs.outoftopic.core.theme.DarkTaro
import com.poetralabs.outoftopic.core.theme.LightOrange
import com.poetralabs.outoftopic.core.theme.Taro
import kotlinx.coroutines.launch

private data class GuideStep(val number: Int, val text: String)

private data class GuidePage(
    val title: String,
    val subtitle: String,
    val description: String,
    val steps: List<GuideStep>,
    val icon: Int,
    val bgColor: Color,
    val accentColor: Color,
)

private val guidePages = listOf(
    GuidePage(
        title = "Truth or Dare",
        subtitle = "Cara Bermain",
        description = "Tantang teman-temanmu dengan game klasik yang seru! Pilih jujur atau berani terima tantangan.",
        steps = listOf(
            GuideStep(1, "Buka menu Truth or Dare dari halaman utama"),
            GuideStep(2, "Tekan tombol PUTAR untuk memutar slot"),
            GuideStep(3, "Slot akan berhenti di salah satu pertanyaan Truth atau tantangan Dare"),
            GuideStep(4, "Pemain yang ditunjuk harus menjawab jujur atau menjalankan tantangan"),
            GuideStep(5, "Putar lagi untuk giliran berikutnya!"),
        ),
        icon = R.drawable.ic_tod,
        bgColor = Taro,
        accentColor = DarkTaro,
    ),
    GuidePage(
        title = "Random Question",
        subtitle = "Cara Bermain",
        description = "Bikin obrolan makin seru! Pilih tema yang kamu suka dan dapatkan pertanyaan random yang mengasyikkan.",
        steps = listOf(
            GuideStep(1, "Buka menu Random Question dari halaman utama"),
            GuideStep(
                2,
                "Pilih tema yang sesuai suasana, misalnya: Kenalan, Bucin, atau Masa Kecil"
            ),
            GuideStep(3, "Geser kartu ke kiri atau kanan untuk berpindah pertanyaan"),
            GuideStep(4, "Baca pertanyaan keras-keras dan jawab bersama teman"),
            GuideStep(5, "Geser terus untuk pertanyaan berikutnya yang makin seru!"),
        ),
        icon = R.drawable.ic_question,
        bgColor = LightOrange,
        accentColor = DarkOrange,
    ),
)

@Composable
fun GuideScreen(
    navController: NavController, startPage: Int = 0
) {
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { guidePages.size })
    val scope = rememberCoroutineScope()

    val currentPage = guidePages[pagerState.currentPage]
    val isLastPage = pagerState.currentPage == guidePages.size - 1

    val bgColor by animateColorAsState(
        targetValue = currentPage.bgColor,
        animationSpec = tween(400),
        label = "bgColor"
    )

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "arrowBack",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Panduan", color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                GuidePage(page = guidePages[page], bgColor = bgColor)
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(guidePages.size) { index ->
                        val isSelected = index == pagerState.currentPage
                        val dotColor by animateColorAsState(
                            targetValue = if (isSelected) currentPage.bgColor else Color.LightGray,
                            animationSpec = tween(300),
                            label = "dot"
                        )
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }

                // Action button
                Button(
                    onClick = {
                        if (isLastPage) {
                            navController.popBackStack()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = currentPage.bgColor)
                ) {
                    Text(
                        text = if (isLastPage) "Ayo Mulai!" else "Selanjutnya",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun GuidePage(page: GuidePage, bgColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title block
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = bgColor
            )
            Text(
                text = page.title,
                style = MaterialTheme.typography.displaySmall,
                color = Color.Black
            )
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }

        // Steps
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            page.steps.forEach { step ->
                GuideStepRow(step = step, accentColor = bgColor)
            }
        }
    }
}

@Composable
private fun GuideStepRow(step: GuideStep, accentColor: Color) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.number.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = step.text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 4.dp),
            lineHeight = 20.sp
        )
    }
}

@Preview(device = "id:pixel_4_xl", showSystemUi = true)
@Composable
private fun GuideScreenPreview() {
}