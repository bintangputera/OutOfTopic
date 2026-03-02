package com.poetralabs.outoftopic.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.poetralabs.outoftopic.presentation.home.HomeScreen
import com.poetralabs.outoftopic.presentation.question.QuestionScreen

fun NavGraphBuilder.navigationGraph(navController: NavController) {
    composable<HomeRoute> {
        HomeScreen(onThemeClick = { themeId ->
            navController.navigate(QuestionRoute(themeId))
        })
    }
    composable<QuestionRoute> {
        val args = it.toRoute<QuestionRoute>()
        QuestionScreen(
            themeId = args.themeId,
            onBackClick = { navController.popBackStack() }
        )
    }
}