package com.poetralabs.outoftopic.presentation.truthordare

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poetralabs.outoftopic.core.theme.BackgroundWhite
import com.poetralabs.outoftopic.core.theme.LightOrange
import com.poetralabs.outoftopic.core.theme.Taro
import org.koin.androidx.compose.koinViewModel

enum class TodMode { TRUTH, DARE }

@Composable
fun TruthOrDareScreen(
    onBack: () -> Unit = {},
    viewModel: TruthOrDareViewModel = koinViewModel()
) {
    var selectedMode by remember { mutableStateOf<TodMode?>(null) }

    val currentQuestion by viewModel.currentQuestion.collectAsStateWithLifecycle()
    val isRolling by viewModel.isRolling.collectAsStateWithLifecycle()

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
        },
        bottomBar = {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val truthSelected = selectedMode == TodMode.TRUTH
                    Button(
                        onClick = { selectedMode = TodMode.TRUTH },
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (truthSelected) Taro else Color.White,
                            contentColor = if (truthSelected) Color.White else Taro
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (truthSelected) 0.dp else 2.dp
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🤔", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "TRUTH",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    val dareSelected = selectedMode == TodMode.DARE
                    Button(
                        onClick = { selectedMode = TodMode.DARE },
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (dareSelected) LightOrange else Color.White,
                            contentColor = if (dareSelected) Color.White else LightOrange
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (dareSelected) 0.dp else 2.dp
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔥", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "DARE",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { selectedMode?.let { viewModel.rollQuestion(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = selectedMode != null && !isRolling,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedMode) {
                            TodMode.TRUTH -> Taro
                            TodMode.DARE -> LightOrange
                            null -> Color.LightGray
                        },
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = if (isRolling) "Rolling..." else when (selectedMode) {
                            TodMode.TRUTH -> "Mulai Truth!"
                            TodMode.DARE -> "Mulai Dare!"
                            null -> "Pilih dulu"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Truth or Dare",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )

            Text(
                text = "Pilih tantangan atau jujur-jujuran",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentQuestion == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🎲",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Pilih Truth atau Dare\nlalu tekan mulai!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Rolling slot-machine animation
                        AnimatedContent(
                            targetState = currentQuestion,
                            transitionSpec = {
                                slideInVertically { height -> height } togetherWith
                                        slideOutVertically { height -> -height }
                            },
                            label = "rolling_question"
                        ) { question ->
                            Text(
                                text = question ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
