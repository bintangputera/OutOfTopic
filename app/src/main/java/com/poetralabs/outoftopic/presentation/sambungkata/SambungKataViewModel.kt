package com.poetralabs.outoftopic.presentation.sambungkata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GamePhase { SETUP, PLAYING, GAME_OVER }

class SambungKataViewModel : ViewModel() {

    companion object {
        const val TURN_DURATION = 10
        private val STARTER_WORDS = listOf(
            "APEL", "BUKU", "KURSI", "DONAT", "EMBER",
            "FOTO", "GAJAH", "HARIMAU", "IKAN", "JAMBU",
            "KUDA", "LANGIT", "MEJA", "NANAS", "OBAT",
            "PAGI", "RAMBUT", "SABUN", "TANAH", "UDARA"
        )
    }

    private val _players = MutableStateFlow<List<String>>(emptyList())
    val players = _players.asStateFlow()

    private val _activePlayers = MutableStateFlow<List<String>>(emptyList())
    val activePlayers = _activePlayers.asStateFlow()

    private val _eliminatedPlayers = MutableStateFlow<List<String>>(emptyList())
    val eliminatedPlayers = _eliminatedPlayers.asStateFlow()

    private val _currentPlayerIndex = MutableStateFlow(0)
    val currentPlayerIndex = _currentPlayerIndex.asStateFlow()

    private val _wordChain = MutableStateFlow<List<String>>(emptyList())
    val wordChain = _wordChain.asStateFlow()

    private val _lastWord = MutableStateFlow<String?>(null)
    val lastWord = _lastWord.asStateFlow()

    private val _nextLetter = MutableStateFlow<Char?>(null)
    val nextLetter = _nextLetter.asStateFlow()

    private val _timeLeft = MutableStateFlow(TURN_DURATION)
    val timeLeft = _timeLeft.asStateFlow()

    private val _gamePhase = MutableStateFlow(GamePhase.SETUP)
    val gamePhase = _gamePhase.asStateFlow()

    private val _winner = MutableStateFlow<String?>(null)
    val winner = _winner.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _justEliminated = MutableStateFlow<String?>(null)
    val justEliminated = _justEliminated.asStateFlow()

    private var timerJob: Job? = null

    fun addPlayer(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || _gamePhase.value != GamePhase.SETUP) return
        _players.value = _players.value + trimmed
    }

    fun removePlayer(index: Int) {
        if (_gamePhase.value != GamePhase.SETUP) return
        _players.value = _players.value.toMutableList().also { it.removeAt(index) }
    }

    fun startGame() {
        if (_players.value.size < 2) return
        val starterWord = STARTER_WORDS.random()
        _activePlayers.value = _players.value.shuffled()
        _eliminatedPlayers.value = emptyList()
        _currentPlayerIndex.value = 0
        _wordChain.value = listOf(starterWord)
        _lastWord.value = starterWord
        _nextLetter.value = starterWord.last().uppercaseChar()
        _error.value = null
        _justEliminated.value = null
        _gamePhase.value = GamePhase.PLAYING
        startTimer()
    }

    fun submitWord(word: String) {
        val trimmed = word.trim().uppercase()
        val requiredLetter = _nextLetter.value ?: return

        if (trimmed.isEmpty()) {
            _error.value = "Kata tidak boleh kosong!"
            return
        }
        if (trimmed.first() != requiredLetter) {
            _error.value = "Harus dimulai huruf '$requiredLetter'!"
            return
        }
        if (_wordChain.value.contains(trimmed)) {
            _error.value = "'$trimmed' sudah dipakai!"
            return
        }

        _error.value = null
        _justEliminated.value = null
        _wordChain.value = _wordChain.value + trimmed
        _lastWord.value = trimmed
        _nextLetter.value = trimmed.last().uppercaseChar()
        advanceTurn()
    }

    fun clearError() {
        _error.value = null
    }

    private fun advanceTurn() {
        timerJob?.cancel()
        val active = _activePlayers.value
        if (active.size <= 1) {
            _gamePhase.value = GamePhase.GAME_OVER
            _winner.value = active.firstOrNull()
            return
        }
        _currentPlayerIndex.value = (_currentPlayerIndex.value + 1) % active.size
        startTimer()
    }

    private fun eliminateCurrentPlayer() {
        timerJob?.cancel()
        val active = _activePlayers.value.toMutableList()
        val idx = _currentPlayerIndex.value
        val eliminated = active.removeAt(idx)
        _justEliminated.value = eliminated
        _eliminatedPlayers.value = _eliminatedPlayers.value + eliminated
        _error.value = null

        if (active.size <= 1) {
            _activePlayers.value = active
            _gamePhase.value = GamePhase.GAME_OVER
            _winner.value = active.firstOrNull()
            return
        }

        _activePlayers.value = active
        _currentPlayerIndex.value = idx % active.size
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        _timeLeft.value = TURN_DURATION
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value--
            }
            eliminateCurrentPlayer()
        }
    }

    fun resetGame() {
        timerJob?.cancel()
        _wordChain.value = emptyList()
        _lastWord.value = null
        _nextLetter.value = null
        _activePlayers.value = emptyList()
        _eliminatedPlayers.value = emptyList()
        _currentPlayerIndex.value = 0
        _error.value = null
        _justEliminated.value = null
        _gamePhase.value = GamePhase.SETUP
        _winner.value = null
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
