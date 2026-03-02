package com.poetralabs.outoftopic.presentation.question

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionScreen(
    themeId: String,
    onBackClick: () -> Unit,
    viewModel: QuestionViewModel = koinViewModel()
) {

    val questions by viewModel.question.collectAsStateWithLifecycle()
    val isFinished by viewModel.isFinished.collectAsStateWithLifecycle()
    val totalQuestions by viewModel.totalQuestions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getAllQuestion(themeId = themeId)
    }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isFinished) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "All Done!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You've gone through all the questions in this theme.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { viewModel.restart(themeId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text("Restart Theme")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Back to Themes")
                    }
                }
            } else if (questions.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { 2 })

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.settledPage }.collect { settledPage ->
                        if (settledPage == 1) {
                            viewModel.nextQuestion()
                            if (!isFinished) {
                                pagerState.scrollToPage(0)
                            }
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Progress Indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currentIndex / $totalQuestions",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                        ) { page ->

                            val pageOffset = (
                                    (pagerState.currentPage - page) +
                                            pagerState.currentPageOffsetFraction
                                    )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        if (page == 0) {
                                            val absOffset = pageOffset.absoluteValue
                                            val scale = 1f - (absOffset * 0.1f)
                                            scaleX = scale
                                            scaleY = scale
                                            rotationZ = pageOffset * 8f
                                        } else {
                                            scaleX = 0.9f
                                            scaleY = 0.9f
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 32.dp)
                                        .fillMaxHeight(0.75f),
                                    elevation = CardDefaults.cardElevation(if (page == 0) 12.dp else 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = questions.getOrNull(page)?.let { Color(it.color) } ?: Color.Transparent
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = questions.getOrNull(page)?.question?.question ?: "Finish",
                                            style = MaterialTheme.typography.headlineMedium,
                                            textAlign = TextAlign.Center,
                                            color = if (questions.getOrNull(page) != null) Color.White else Color.White.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Instruction
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Swipe left for next question",
                            color = Color.White.copy(alpha = 0.3f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}