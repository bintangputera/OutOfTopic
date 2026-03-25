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

    private val _currentQuestion = MutableStateFlow<String?>(null)
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _isRolling = MutableStateFlow(false)
    val isRolling = _isRolling.asStateFlow()

    fun rollQuestion(mode: TodMode) {
        val questions = if (mode == TodMode.TRUTH) truthQuestions else dareQuestions
        val target = questions.random()

        viewModelScope.launch {
            _isRolling.value = true

            // Fast phase
            repeat(10) {
                _currentQuestion.value = questions.random()
                delay(70)
            }
            // Medium phase
            repeat(5) {
                _currentQuestion.value = questions.random()
                delay(140)
            }
            // Slow phase
            repeat(3) {
                _currentQuestion.value = questions.random()
                delay(280)
            }
            // Land on the final question
            _currentQuestion.value = target
            _isRolling.value = false
        }
    }
}
