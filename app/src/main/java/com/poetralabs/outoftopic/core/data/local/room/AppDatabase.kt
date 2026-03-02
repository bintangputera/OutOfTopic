package com.poetralabs.outoftopic.core.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.poetralabs.outoftopic.core.data.local.entity.QuestionEntity
import com.poetralabs.outoftopic.core.data.local.entity.ThemeEntity

@Database(entities = [QuestionEntity::class, ThemeEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}
