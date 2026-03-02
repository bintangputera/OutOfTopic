package com.poetralabs.outoftopic.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    @SerialName("themes")
    val themes: List<ThemeResponse> = emptyList()
)

@Serializable
data class ThemeResponse(
    @SerialName("id")
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("description")
    val description: String,
    @SerialName("questions")
    val questions: List<String> = emptyList()
)
