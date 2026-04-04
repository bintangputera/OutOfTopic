package com.poetralabs.outoftopic.presentation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poetralabs.outoftopic.core.data.local.entity.QuestionEntity
import com.poetralabs.outoftopic.core.domain.repository.QuestionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class QuestionViewModel(
    private val questionRepository: QuestionRepository
): ViewModel() {

    private val _question: MutableStateFlow<List<QuestionWithColor>> = MutableStateFlow(emptyList())
    val question = _question.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions = _totalQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    private var _allQuestions: List<QuestionEntity> = emptyList()
    private var _questionJob: Job? = null

    private val _historyStack: MutableList<QuestionWithColor> = mutableListOf()
    private val _previousQuestion = MutableStateFlow<QuestionWithColor?>(null)
    val previousQuestion = _previousQuestion.asStateFlow()

    private val colors = listOf(
        0xFFE57373, 0xFFF06292, 0xFFBA68C8, 0xFF9575CD,
        0xFF7986CB, 0xFF64B5F6, 0xFF4FC3F7, 0xFF4DD0E1,
        0xFF4DB6AC, 0xFF81C784, 0xFFAED581, 0xFFFFD54F,
        0xFFFFB74D, 0xFFFF8A65
    )

    fun getAllQuestion(themeId: String) {
        _questionJob?.cancel()
        _questionJob = questionRepository.getQuestionByTheme(themeId)
            .onEach {
                if (_question.value.isEmpty() && !_isFinished.value && it.isNotEmpty()) {
                    _allQuestions = it.shuffled()
                    _totalQuestions.value = it.size
                    _currentQuestionIndex.value = 1

                    val initialList = mutableListOf<QuestionWithColor>()
                    initialList.add(QuestionWithColor(_allQuestions.first(), colors.random()))
                    _allQuestions = _allQuestions.drop(1)

                    if (_allQuestions.isNotEmpty()) {
                        initialList.add(QuestionWithColor(_allQuestions.first(), colors.random()))
                        _allQuestions = _allQuestions.drop(1)
                    }

                    _question.value = initialList
                }
            }
            .launchIn(viewModelScope)
    }

    fun nextQuestion() {
        val currentList = _question.value.toMutableList()
        if (currentList.isNotEmpty()) {
            _historyStack.add(currentList[0])
            _previousQuestion.value = currentList[0]
            currentList.removeAt(0)
            if (_allQuestions.isNotEmpty()) {
                currentList.add(QuestionWithColor(_allQuestions.first(), colors.random()))
                _allQuestions = _allQuestions.drop(1)
            }
            
            if (currentList.isNotEmpty()) {
                _question.value = currentList
                _currentQuestionIndex.value++
            } else {
                _question.value = emptyList()
                _isFinished.value = true
            }
        }
    }

    fun goToPreviousQuestion() {
        if (_historyStack.isEmpty()) return
        val prev = _historyStack.removeAt(_historyStack.lastIndex)
        val currentList = _question.value
        val current = currentList.firstOrNull() ?: return
        val oldNext = currentList.getOrNull(1)
        if (oldNext != null) {
            _allQuestions = listOf(oldNext.question) + _allQuestions
        }
        _question.value = listOf(prev, current)
        _previousQuestion.value = _historyStack.lastOrNull()
        _currentQuestionIndex.value--
    }

    fun restart(themeId: String) {
        _isFinished.value = false
        _question.value = emptyList()
        _currentQuestionIndex.value = 0
        _historyStack.clear()
        _previousQuestion.value = null
        getAllQuestion(themeId)
    }
}

data class QuestionWithColor(
    val question: QuestionEntity,
    val color: Long
)