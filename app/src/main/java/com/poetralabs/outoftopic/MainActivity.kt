package com.poetralabs.outoftopic

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.poetralabs.outoftopic.core.domain.repository.QuestionRepository
import com.poetralabs.outoftopic.core.navigation.HomeRoute
import com.poetralabs.outoftopic.core.navigation.navigationGraph
import com.poetralabs.outoftopic.core.theme.OutOfTopicTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val questionRepository: QuestionRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        lifecycleScope.launch {
            questionRepository.prepopulateDatabase()
        }

        setContent {
            val navController = rememberNavController()
            OutOfTopicTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        navigationGraph(navController)
                    }
                }
            }
        }
    }
}