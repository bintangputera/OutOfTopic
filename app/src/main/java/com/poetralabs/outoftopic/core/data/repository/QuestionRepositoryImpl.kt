package com.poetralabs.outoftopic.core.data.repository

import android.content.Context
import com.poetralabs.outoftopic.core.data.local.entity.QuestionEntity
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity
import com.poetralabs.outoftopic.core.data.local.room.QuestionDao
import com.poetralabs.outoftopic.core.data.remote.response.QuestionResponse
import com.poetralabs.outoftopic.core.domain.repository.QuestionRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class QuestionRepositoryImpl(
    private val context: Context,
    private val questionDao: QuestionDao,
    private val httpClient: HttpClient
) : QuestionRepository {

    override fun getAllThemes(): Flow<List<ThemeEntity>> {
        return questionDao.getAllThemes()
    }

    override fun getQuestionByTheme(themeId: String): Flow<List<QuestionEntity>> {
        return questionDao.getQuestionsByTheme(themeId)
    }

    override suspend fun fetchQuestionsFromRemote() {
        withContext(Dispatchers.IO) {
            try {
                val response: QuestionResponse = httpClient.get("https://outoftopic-8df32.web.app/question.json").body()
                
                val themes = response.themes.map { 
                    ThemeEntity(id = it.id, displayName = it.displayName, description = it.description)
                }
                
                val questions = response.themes.flatMap { theme ->
                    theme.questions.map { question ->
                        QuestionEntity(themeId = theme.id, question = question)
                    }
                }

                questionDao.clearThemes()
                questionDao.clearQuestions()
                questionDao.insertThemes(themes)
                questionDao.insertQuestions(questions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchQuestionsFromLocal() {
        val jsonString = context.assets.open("question.json").bufferedReader().use { it.readText() }
        val response = Json.decodeFromString<QuestionResponse>(jsonString)

        val themes = response.themes.map {
            ThemeEntity(id = it.id, displayName = it.displayName, description = it.description)
        }

        val questions = response.themes.flatMap { theme ->
            theme.questions.map { question ->
                QuestionEntity(themeId = theme.id, question = question)
            }
        }

        questionDao.clearThemes()
        questionDao.clearQuestions()
        questionDao.insertThemes(themes)
        questionDao.insertQuestions(questions)
    }

    override suspend fun prepopulateDatabase() {
        withContext(Dispatchers.IO) {
            fetchQuestionsFromRemote()
        }
    }
}
