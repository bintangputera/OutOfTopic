package com.poetralabs.outoftopic.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.poetralabs.outoftopic.presentation.about.AboutScreen
import com.poetralabs.outoftopic.presentation.feedback.FeedbackScreen
import com.poetralabs.outoftopic.presentation.guide.GuideScreen
import com.poetralabs.outoftopic.presentation.home.HomeScreen
import com.poetralabs.outoftopic.presentation.question.QuestionScreen
import com.poetralabs.outoftopic.presentation.question.theme.QuestionThemeScreen
import com.poetralabs.outoftopic.presentation.minigames.MiniGamesScreen
import com.poetralabs.outoftopic.presentation.sambungkata.SambungKataScreen
import com.poetralabs.outoftopic.presentation.truthordare.TruthOrDareScreen

fun NavGraphBuilder.navigationGraph(navController: NavController) {
    composable<HomeRoute> {
        HomeScreen(navController = navController)
    }
    composable<GuideRoute> {
        val args = it.toRoute<GuideRoute>()
        GuideScreen(
            navController = navController,
            startPage = args.startPage
        )
    }
    composable<TruthOrDareRoute> {
        TruthOrDareScreen(
            onBack = { navController.popBackStack() },
            onGuide = { navController.navigate(GuideRoute(startPage = 0)) }
        )
    }
    composable<QuestionThemeRoute> {
        QuestionThemeScreen(
            onBack = { navController.popBackStack() },
            onGuide = { navController.navigate(GuideRoute(startPage = 1)) },
            onThemeClick = { theme ->
                navController.navigate(QuestionRoute(theme.id, theme.displayName))
            }
        )
    }
    composable<QuestionRoute> {
        val args = it.toRoute<QuestionRoute>()
        QuestionScreen(
            navController = navController,
            themeId = args.themeId,
            themeName = args.themeName
        )
    }
    composable<MiniGamesRoute> {
        MiniGamesScreen(
            onBack = { navController.popBackStack() },
            onSambungKata = { navController.navigate(SambungKataRoute) }
        )
    }
    composable<SambungKataRoute> {
        SambungKataScreen(
            onBack = { navController.popBackStack() }
        )
    }
    composable<FeedbackRoute> {
        FeedbackScreen(
            onBack = { navController.popBackStack() }
        )
    }
    composable<AboutRoute> {
        AboutScreen(
            onBack = { navController.popBackStack() },
            onFeedback = { navController.navigate(FeedbackRoute) }
        )
    }
}