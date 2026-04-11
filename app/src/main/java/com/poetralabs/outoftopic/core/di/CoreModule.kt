package com.poetralabs.outoftopic.core.di

import androidx.room.Room
import com.poetralabs.outoftopic.core.data.local.room.AppDatabase
import com.poetralabs.outoftopic.core.data.repository.QuestionRepositoryImpl
import com.poetralabs.outoftopic.core.domain.repository.QuestionRepository
import com.poetralabs.outoftopic.presentation.feedback.FeedbackViewModel
import com.poetralabs.outoftopic.presentation.question.theme.QuestionThemeViewModel
import com.poetralabs.outoftopic.presentation.question.QuestionViewModel
import com.poetralabs.outoftopic.presentation.sambungkata.SambungKataViewModel
import com.poetralabs.outoftopic.presentation.truthordare.TruthOrDareViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { FirebaseAnalytics.getInstance(androidContext()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "out_of_topic.db"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<AppDatabase>().questionDao() }
}

val networkModule = module {
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
}

val repositoryModule = module {
    single<QuestionRepository> { QuestionRepositoryImpl(androidContext(), get(), get()) }
}

val viewModelModule = module {
    viewModel { QuestionThemeViewModel(get(), get()) }
    viewModel { QuestionViewModel(get()) }
    viewModel { TruthOrDareViewModel() }
    viewModel { SambungKataViewModel() }
    viewModel { FeedbackViewModel() }
}

val appModule = listOf(databaseModule, networkModule, repositoryModule, viewModelModule)
