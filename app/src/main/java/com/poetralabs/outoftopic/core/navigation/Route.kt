package com.poetralabs.outoftopic.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
data class QuestionRoute(val themeId: String)

