package com.poetralabs.outoftopic.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity
import com.poetralabs.outoftopic.core.domain.repository.QuestionRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val questionRepository: QuestionRepository,
    private val analytics: FirebaseAnalytics
): ViewModel() {
    
    private val _themes = MutableStateFlow<List<ThemeEntity>>(emptyList())
    val themes = _themes.asStateFlow()
    
    init {
        getAllThemes()
    }

    private fun getAllThemes() {
        questionRepository.getAllThemes()
            .onEach { _themes.value = it }
            .launchIn(viewModelScope)
    }

    fun logThemeSelection(themeId: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, themeId)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "theme")
        }
    }
}