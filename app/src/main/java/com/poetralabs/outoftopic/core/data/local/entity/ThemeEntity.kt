package com.poetralabs.outoftopic.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "themes")
data class ThemeEntity(
    @PrimaryKey
    val id: String,
    val displayName: String,
    val description: String
)