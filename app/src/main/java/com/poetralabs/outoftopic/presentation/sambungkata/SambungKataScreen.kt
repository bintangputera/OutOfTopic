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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poetralabs.outoftopic.core.theme.BackgroundWhite
import com.poetralabs.outoftopic.core.theme.DarkMintGreen
import com.poetralabs.outoftopic.core.theme.MintGreen
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

    // Clear input on turn advance
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
        containerColor = BackgroundWhite,
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
                        tint = Color.Black
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
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = players.size >= 2,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MintGreen,
                                disabledContainerColor = Color.LightGray
                            )
                        ) {
                            Text(
                                text = if (players.size < 2) "Tambah min. 2 pemain" else "Mulai Permainan!",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    }

                    GamePhase.PLAYING -> {
                        AnimatedVisibility(visible = error != null) {
                            Text(
                                text = error ?: "",
                                color = Color(0xFFE53935),
                                style = MaterialTheme.typography.titleMedium,
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
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MintGreen,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = MintGreen
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
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                        ) {
                            Text(
                                text = "Kirim →",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    }

                    GamePhase.GAME_OVER -> {
                        Button(
                            onClick = { viewModel.resetGame() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                        ) {
                            Text(
                                text = "Main Lagi",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
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
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Text(
                text = "Sambung kata dari huruf terakhir!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
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
                            Text(text = "🔗", style = MaterialTheme.typography.displayLarge)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (players.isEmpty()) "Tambah pemain untuk mulai!"
                                else if (players.size == 1) "Butuh 1 pemain lagi!"
                                else "Siap! Tekan mulai.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
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
                            color = Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = "❌ $justEliminated kehabisan waktu!",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFE53935),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Giliran $currentPlayer",
                        style = MaterialTheme.typography.titleMedium,
                        color = MintGreen,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer circle with next letter
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { timeLeft.toFloat() / SambungKataViewModel.TURN_DURATION },
                            modifier = Modifier.size(120.dp),
                            strokeWidth = 8.dp,
                            color = if (timeLeft <= 3) Color(0xFFE53935) else MintGreen,
                            trackColor = Color(0xFFE0E0E0),
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "huruf",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = nextLetter?.toString() ?: "?",
                                style = MaterialTheme.typography.displayLarge,
                                color = if (timeLeft <= 3) Color(0xFFE53935) else DarkMintGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$timeLeft",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Kata sebelumnya:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = lastWord ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Word chain history
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
                            Text(text = "🏆", style = MaterialTheme.typography.displayLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = winner ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                color = DarkMintGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Menang!",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "${wordChain.size} kata berhasil dibuat",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
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
                shape = RoundedCornerShape(20.dp),
                color = MintGreen.copy(alpha = 0.15f)
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkMintGreen,
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
                    Text(name, style = MaterialTheme.typography.titleMedium)
                },
                trailingIcon = {
                    Text(
                        text = "×",
                        modifier = Modifier.clickable { onRemovePlayer(index) },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                },
                colors = InputChipDefaults.inputChipColors(labelColor = Color.Black)
            )
        }
        SuggestionChip(
            onClick = onAddPlayer,
            label = {
                Text("+ Pemain", style = MaterialTheme.typography.titleMedium, color = Color.Black)
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
                    Text(name, style = MaterialTheme.typography.titleMedium)
                },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = MintGreen,
                    selectedLabelColor = Color.White,
                    labelColor = Color.Black
                )
            )
        }
        eliminatedPlayers.forEach { name ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = TextDecoration.LineThrough
                    ),
                    color = Color.LightGray,
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
                    Text("Nama pemain", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintGreen,
                    focusedTextColor = Color.Black,
                    cursorColor = MintGreen
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (name.isNotBlank()) onAdd(name.trim())
                })
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onAdd(name.trim()) }) {
                Text("Tambah", style = MaterialTheme.typography.titleMedium, color = MintGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            }
        }
    )
}
