package com.poetralabs.outoftopic.presentation.truthordare

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    onGuide: () -> Unit = {},
    viewModel: TruthOrDareViewModel = koinViewModel()
) {
    var selectedMode by remember { mutableStateOf<TodMode?>(null) }
    var showAddPlayerDialog by remember { mutableStateOf(false) }

    val currentQuestion by viewModel.currentQuestion.collectAsStateWithLifecycle()
    val isRolling by viewModel.isRolling.collectAsStateWithLifecycle()
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val players by viewModel.players.collectAsStateWithLifecycle()
    val currentPlayerIndex by viewModel.currentPlayerIndex.collectAsStateWithLifecycle()
    val scores by viewModel.scores.collectAsStateWithLifecycle()

    val isQuestionShowing = currentQuestion != null && !isRolling

    if (showAddPlayerDialog) {
        AddPlayerDialog(
            onDismiss = { showAddPlayerDialog = false },
            onAdd = { name ->
                viewModel.addPlayer(name)
                showAddPlayerDialog = false
            }
        )
    }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.Black
                    )
                }
                androidx.compose.material3.IconButton(onClick = onGuide) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(24.dp)) {
                if (isQuestionShowing) {
                    when (currentMode) {
                        TodMode.DARE -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.onTurnComplete(scored = true) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    )
                                ) {
                                    Text(
                                        text = "Selesai! ✓",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                OutlinedButton(
                                    onClick = { viewModel.onTurnComplete(scored = false) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE53935)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFE53935)
                                    )
                                ) {
                                    Text(
                                        text = "Lewati ✗",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        TodMode.TRUTH -> {
                            Button(
                                onClick = { viewModel.onTurnComplete(scored = false) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Taro)
                            ) {
                                Text(
                                    text = "Lanjut →",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                            }
                        }

                        null -> {}
                    }
                } else {
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
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Text(
                text = "Pilih tantangan atau jujur-jujuran",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayerSection(
                players = players,
                currentPlayerIndex = currentPlayerIndex,
                scores = scores,
                onAddPlayer = { showAddPlayerDialog = true },
                onRemovePlayer = { viewModel.removePlayer(it) }
            )

            if (players.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                val currentPlayer = players[currentPlayerIndex]
                val activeColor = when (currentMode ?: selectedMode) {
                    TodMode.TRUTH -> Taro
                    TodMode.DARE -> LightOrange
                    null -> Color.Gray
                }
                Text(
                    text = when {
                        isRolling -> "Giliran $currentPlayer..."
                        isQuestionShowing -> "Giliran $currentPlayer"
                        else -> "Giliran $currentPlayer — pilih tantangan!"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = activeColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(if (players.isEmpty()) 24.dp else 12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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

@Composable
private fun PlayerSection(
    players: List<String>,
    currentPlayerIndex: Int,
    scores: Map<String, Int>,
    onAddPlayer: () -> Unit,
    onRemovePlayer: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        players.forEachIndexed { index, name ->
            val isCurrentPlayer = index == currentPlayerIndex
            val score = scores[name] ?: 0
            InputChip(
                selected = isCurrentPlayer,
                onClick = {},
                label = {
                    Text(
                        text = if (score > 0) "$name • $score" else name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                trailingIcon = {
                    Text(
                        text = "×",
                        modifier = Modifier.clickable { onRemovePlayer(index) },
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isCurrentPlayer) Color.White else Color.Black
                    )
                },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = Taro,
                    selectedLabelColor = Color.White,
                    labelColor = Color.Black
                )
            )
        }
        SuggestionChip(
            onClick = onAddPlayer,
            label = {
                Text(
                    "+ Pemain",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }
        )
    }
}

@Composable
private fun AddPlayerDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                "Tambah Pemain",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(
                        "Nama pemain",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    focusedTextColor = Color.Black,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (name.isNotBlank()) onAdd(name)
                })
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onAdd(name) }) {
                Text("Tambah", style = MaterialTheme.typography.titleMedium, color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", style = MaterialTheme.typography.titleMedium, color = Color.Black)
            }
        }
    )
}
