package com.poetralabs.outoftopic.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.poetralabs.outoftopic.core.data.local.entity.QuestionEntity
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM themes")
    fun getAllThemes(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM questions WHERE themeId = :themeId")
    fun getQuestionsByTheme(themeId: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThemes(themes: List<ThemeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT (SELECT COUNT(*) FROM themes) == 0")
    suspend fun isDatabaseEmpty(): Boolean

    @Query("DELETE FROM themes")
    suspend fun clearThemes()

    @Query("DELETE FROM questions")
    suspend fun clearQuestions()

}
