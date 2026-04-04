package com.poetralabs.outoftopic.presentation.truthordare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TruthOrDareViewModel : ViewModel() {

    private val truthQuestions = listOf(
        "Apa hal paling memalukan yang pernah kamu lakukan?",
        "Kapan terakhir kamu berbohong dan kenapa?",
        "Siapa yang paling kamu suka di ruangan ini?",
        "Apa rahasia terbesar yang belum pernah kamu ceritakan ke siapapun?",
        "Pernahkah kamu pura-pura sakit untuk tidak masuk kerja/sekolah?",
        "Apa hal yang paling kamu sesali dalam hidupmu?",
        "Pernahkah kamu mencuri sesuatu? Apa itu?",
        "Siapa orang yang paling sering kamu gosipkan?",
        "Apa hal tergila yang pernah kamu lakukan karena suka seseorang?",
        "Jika bisa menghapus satu kenangan, kenangan apa itu?",
        "Pernahkah kamu mengintip HP orang lain tanpa izin?",
        "Apa hal yang paling tidak kamu sukai dari diri sendiri?",
        "Pernahkah kamu pura-pura tidak melihat seseorang di jalan agar tidak menyapa?",
        "Apa mimpi terliar yang pernah kamu alami?",
        "Siapa yang pertama kali kamu hubungi saat ada masalah besar?"
    )

    private val dareQuestions = listOf(
        "Nyanyikan bagian chorus dari lagu favoritmu sekarang!",
        "Lakukan 10 push-up di depan semua orang.",
        "Ceritakan lelucon terburukmu dan pastikan semua orang tertawa.",
        "Minum satu gelas air penuh tanpa berhenti.",
        "Kirim pesan 'Aku kangen kamu' ke kontak pertama di HPmu.",
        "Tiru gaya berjalan seseorang di ruangan ini.",
        "Berdiri di atas satu kaki selama 1 menit.",
        "Berbicara dengan aksen asing selama 3 giliran berikutnya.",
        "Posting foto selfie paling aneh di story-mu sekarang.",
        "Ceritakan hal paling konyol yang pernah kamu lakukan.",
        "Lakukan tarian selama 30 detik tanpa musik.",
        "Telepon seseorang dan katakan 'Aku punya berita penting' lalu diam.",
        "Makan sesuatu yang ada di sekitarmu dengan mata tertutup.",
        "Tulis status WA yang memalukan dan biarkan selama 5 menit.",
        "Tiru suara 3 hewan berbeda berturut-turut."
    )

    // Feature 1: no-repeat — tracks which indices have been shown this round
    private val usedTruth = mutableSetOf<Int>()
    private val usedDare = mutableSetOf<Int>()

    private val _currentQuestion = MutableStateFlow<String?>(null)
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _isRolling = MutableStateFlow(false)
    val isRolling = _isRolling.asStateFlow()

    // The mode of the question currently on screen (null when idle)
    private val _currentMode = MutableStateFlow<TodMode?>(null)
    val currentMode = _currentMode.asStateFlow()

    // Feature 2: optional player turn tracker
    private val _players = MutableStateFlow<List<String>>(emptyList())
    val players = _players.asStateFlow()

    private val _currentPlayerIndex = MutableStateFlow(0)
    val currentPlayerIndex = _currentPlayerIndex.asStateFlow()

    // Shuffled queue of remaining player indices for the current round.
    // Empty until the first roll; refilled with a new shuffle each round.
    private val _turnQueue = ArrayDeque<Int>()
    private var gameStarted = false

    // Feature 3: score tally — playerName -> points
    private val _scores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val scores = _scores.asStateFlow()

    private fun startNewRound() {
        val shuffled = _players.value.indices.shuffled()
        _currentPlayerIndex.value = shuffled.first()
        _turnQueue.clear()
        _turnQueue.addAll(shuffled.drop(1))
    }

    fun rollQuestion(mode: TodMode) {
        // On the very first roll, randomize who goes first (spinning bottle)
        if (!gameStarted && _players.value.isNotEmpty()) {
            startNewRound()
            gameStarted = true
        }

        val questions = if (mode == TodMode.TRUTH) truthQuestions else dareQuestions
        val usedSet = if (mode == TodMode.TRUTH) usedTruth else usedDare

        if (usedSet.size >= questions.size) usedSet.clear()
        val availableIndices = questions.indices.filter { it !in usedSet }
        val targetIndex = availableIndices.random()
        usedSet.add(targetIndex)
        val target = questions[targetIndex]
        _currentMode.value = mode

        viewModelScope.launch {
            _isRolling.value = true
            repeat(10) {
                _currentQuestion.value = questions.random()
                delay(70)
            }
            repeat(5) {
                _currentQuestion.value = questions.random()
                delay(140)
            }
            repeat(3) {
                _currentQuestion.value = questions.random()
                delay(280)
            }
            _currentQuestion.value = target
            _isRolling.value = false
        }
    }

    fun addPlayer(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        _players.value = _players.value + trimmed
        if (_scores.value[trimmed] == null) {
            _scores.value = _scores.value + (trimmed to 0)
        }
        // Reset so the next roll re-randomizes with the updated roster
        resetTurnState()
    }

    fun removePlayer(index: Int) {
        val name = _players.value.getOrNull(index) ?: return
        _players.value = _players.value.toMutableList().also { it.removeAt(index) }
        _scores.value = _scores.value - name
        resetTurnState()
    }

    // Called when the current turn ends. scored=true awards a point (dare completed).
    fun onTurnComplete(scored: Boolean) {
        if (scored && _players.value.isNotEmpty()) {
            val playerName = _players.value[_currentPlayerIndex.value]
            _scores.value = _scores.value.toMutableMap().also {
                it[playerName] = (it[playerName] ?: 0) + 1
            }
        }
        if (_players.value.isNotEmpty()) {
            if (_turnQueue.isEmpty()) {
                // All players had a turn — start a new shuffled round
                startNewRound()
            } else {
                _currentPlayerIndex.value = _turnQueue.removeFirst()
            }
        }
        _currentQuestion.value = null
        _currentMode.value = null
    }

    private fun resetTurnState() {
        _turnQueue.clear()
        gameStarted = false
        _currentPlayerIndex.value = 0
    }
}
