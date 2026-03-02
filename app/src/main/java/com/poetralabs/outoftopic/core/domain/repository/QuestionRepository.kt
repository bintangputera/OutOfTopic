package com.poetralabs.outoftopic.core.domain.repository

import com.poetralabs.outoftopic.core.data.local.entity.QuestionEntity
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun prepopulateDatabase()
    fun getAllThemes(): Flow<List<ThemeEntity>>
    fun getQuestionByTheme(themeId: String): Flow<List<QuestionEntity>>
    suspend fun fetchQuestionsFromRemote()
}
