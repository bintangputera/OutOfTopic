package com.poetralabs.outoftopic.presentation.sambungkata

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poetralabs.outoftopic.core.theme.AnthropicNearBlack
import com.poetralabs.outoftopic.core.theme.BorderCream
import com.poetralabs.outoftopic.core.theme.DarkGameColor
import com.poetralabs.outoftopic.core.theme.ErrorCrimson
import com.poetralabs.outoftopic.core.theme.GameColor
import com.poetralabs.outoftopic.core.theme.Ivory
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Parchment
import com.poetralabs.outoftopic.core.theme.RingWarm
import com.poetralabs.outoftopic.core.theme.StoneGray
import com.poetralabs.outoftopic.core.theme.TerracottaBrand
import com.poetralabs.outoftopic.core.theme.WarmSand
import org.koin.androidx.compose.koinViewModel

@Composable
fun SambungKataScreen(
    onBack: () -> Unit = {},
    viewModel: SambungKataViewModel = koinViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    val activePlayers by viewModel.activePlayers.collectAsStateWithLifecycle()
    val eliminatedPlayers by viewModel.eliminatedPlayers.collectAsStateWithLifecycle()
    val currentPlayerIndex by viewModel.currentPlayerIndex.collectAsStateWithLifecycle()
    val wordChain by viewModel.wordChain.collectAsStateWithLifecycle()
    val lastWord by viewModel.lastWord.collectAsStateWithLifecycle()
    val nextLetter by viewModel.nextLetter.collectAsStateWithLifecycle()
    val timeLeft by viewModel.timeLeft.collectAsStateWithLifecycle()
    val gamePhase by viewModel.gamePhase.collectAsStateWithLifecycle()
    val winner by viewModel.winner.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val justEliminated by viewModel.justEliminated.collectAsStateWithLifecycle()

    var inputWord by remember { mutableStateOf("") }
    var showAddPlayerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentPlayerIndex, gamePhase) {
        inputWord = ""
    }

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
        containerColor = Parchment,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.resetGame()
                    onBack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AnthropicNearBlack
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .imePadding()
            ) {
                when (gamePhase) {
                    GamePhase.SETUP -> {
                        Button(
                            onClick = { viewModel.startGame() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = players.size >= 2,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GameColor,
                                disabledContainerColor = WarmSand
                            )
                        ) {
                            Text(
                                text = if (players.size < 2) "Tambah min. 2 pemain" else "Mulai Permainan!",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (players.size >= 2) Ivory else StoneGray
                            )
                        }
                    }

                    GamePhase.PLAYING -> {
                        AnimatedVisibility(visible = error != null) {
                            Text(
                                text = error ?: "",
                                color = ErrorCrimson,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        OutlinedTextField(
                            value = inputWord,
                            onValueChange = {
                                inputWord = it
                                viewModel.clearError()
                            },
                            placeholder = {
                                Text(
                                    "Ketik kata yang dimulai '${nextLetter ?: "?"}'...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = StoneGray
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GameColor,
                                unfocusedBorderColor = RingWarm,
                                focusedTextColor = AnthropicNearBlack,
                                unfocusedTextColor = AnthropicNearBlack,
                                cursorColor = GameColor
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                capitalization = KeyboardCapitalization.Characters
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                viewModel.submitWord(inputWord)
                            })
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.submitWord(inputWord) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GameColor)
                        ) {
                            Text(
                                text = "Kirim",
                                style = MaterialTheme.typography.titleMedium,
                                color = Ivory
                            )
                        }
                    }

                    GamePhase.GAME_OVER -> {
                        Button(
                            onClick = { viewModel.resetGame() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GameColor)
                        ) {
                            Text(
                                text = "Main Lagi",
                                style = MaterialTheme.typography.titleMedium,
                                color = Ivory
                            )
                        }
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
                text = "Sambung Kata",
                style = MaterialTheme.typography.headlineMedium,
                color = AnthropicNearBlack
            )
            Text(
                text = "Sambung kata dari huruf terakhir!",
                style = MaterialTheme.typography.bodyMedium,
                color = OliveGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (gamePhase) {
                GamePhase.SETUP -> {
                    SetupPlayerSection(
                        players = players,
                        onAddPlayer = { showAddPlayerDialog = true },
                        onRemovePlayer = { viewModel.removePlayer(it) }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (players.isEmpty()) "Tambah pemain untuk mulai!"
                                else if (players.size == 1) "Butuh 1 pemain lagi!"
                                else "Siap! Tekan mulai.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = OliveGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                GamePhase.PLAYING -> {
                    val currentPlayer = activePlayers.getOrNull(currentPlayerIndex) ?: ""

                    ActivePlayerSection(
                        activePlayers = activePlayers,
                        eliminatedPlayers = eliminatedPlayers,
                        currentPlayerIndex = currentPlayerIndex
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedVisibility(
                        visible = justEliminated != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ErrorCrimson.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "$justEliminated kehabisan waktu!",
                                style = MaterialTheme.typography.bodySmall,
                                color = ErrorCrimson,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Giliran $currentPlayer",
                        style = MaterialTheme.typography.titleMedium,
                        color = GameColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer circle
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { timeLeft.toFloat() / SambungKataViewModel.TURN_DURATION },
                            modifier = Modifier.size(120.dp),
                            strokeWidth = 8.dp,
                            color = if (timeLeft <= 3) ErrorCrimson else GameColor,
                            trackColor = WarmSand,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "huruf",
                                style = MaterialTheme.typography.labelMedium,
                                color = StoneGray
                            )
                            Text(
                                text = nextLetter?.toString() ?: "?",
                                style = MaterialTheme.typography.displayLarge,
                                color = if (timeLeft <= 3) ErrorCrimson else DarkGameColor
                            )
                            Text(
                                text = "$timeLeft",
                                style = MaterialTheme.typography.labelMedium,
                                color = StoneGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Kata sebelumnya:",
                        style = MaterialTheme.typography.bodySmall,
                        color = StoneGray
                    )
                    Text(
                        text = lastWord ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AnthropicNearBlack
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    WordChainRow(wordChain = wordChain)
                }

                GamePhase.GAME_OVER -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = winner ?: "",
                                style = MaterialTheme.typography.displaySmall,
                                color = DarkGameColor
                            )
                            Text(
                                text = "Menang!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = AnthropicNearBlack
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "${wordChain.size} kata berhasil dibuat",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OliveGray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            WordChainRow(wordChain = wordChain)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WordChainRow(wordChain: List<String>) {
    val listState = rememberLazyListState()
    LaunchedEffect(wordChain.size) {
        if (wordChain.isNotEmpty()) {
            listState.animateScrollToItem(wordChain.lastIndex)
        }
    }
    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(wordChain) { word ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GameColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.labelLarge,
                    color = DarkGameColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SetupPlayerSection(
    players: List<String>,
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
            InputChip(
                selected = false,
                onClick = {},
                label = {
                    Text(name, style = MaterialTheme.typography.labelLarge)
                },
                trailingIcon = {
                    Text(
                        text = "x",
                        modifier = Modifier.clickable { onRemovePlayer(index) },
                        style = MaterialTheme.typography.labelLarge,
                        color = AnthropicNearBlack
                    )
                },
                colors = InputChipDefaults.inputChipColors(labelColor = AnthropicNearBlack)
            )
        }
        SuggestionChip(
            onClick = onAddPlayer,
            label = {
                Text("+ Pemain", style = MaterialTheme.typography.labelLarge, color = AnthropicNearBlack)
            }
        )
    }
}

@Composable
private fun ActivePlayerSection(
    activePlayers: List<String>,
    eliminatedPlayers: List<String>,
    currentPlayerIndex: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        activePlayers.forEachIndexed { index, name ->
            val isCurrent = index == currentPlayerIndex
            InputChip(
                selected = isCurrent,
                onClick = {},
                label = {
                    Text(name, style = MaterialTheme.typography.labelLarge)
                },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = GameColor,
                    selectedLabelColor = Ivory,
                    labelColor = AnthropicNearBlack
                )
            )
        }
        eliminatedPlayers.forEach { name ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = WarmSand
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge.copy(
                        textDecoration = TextDecoration.LineThrough
                    ),
                    color = StoneGray,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
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
        containerColor = Ivory,
        title = {
            Text(
                "Tambah Pemain",
                style = MaterialTheme.typography.headlineSmall,
                color = AnthropicNearBlack
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text("Nama pemain", style = MaterialTheme.typography.bodyMedium, color = OliveGray)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GameColor,
                    focusedTextColor = AnthropicNearBlack,
                    cursorColor = GameColor
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (name.isNotBlank()) onAdd(name.trim())
                })
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onAdd(name.trim()) }) {
                Text("Tambah", style = MaterialTheme.typography.titleMedium, color = GameColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", style = MaterialTheme.typography.titleMedium, color = StoneGray)
            }
        }
    )
}
