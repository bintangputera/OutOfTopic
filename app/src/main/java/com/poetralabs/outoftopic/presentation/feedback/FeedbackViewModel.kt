package com.poetralabs.outoftopic.presentation.feedback

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class FeedbackSubmitState {
    object Idle : FeedbackSubmitState()
    object Loading : FeedbackSubmitState()
    object Success : FeedbackSubmitState()
    data class Error(val message: String) : FeedbackSubmitState()
}

class FeedbackViewModel : ViewModel() {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _submitState = MutableStateFlow<FeedbackSubmitState>(FeedbackSubmitState.Idle)
    val submitState: StateFlow<FeedbackSubmitState> = _submitState.asStateFlow()

    fun submitFeedback(rating: Int, categories: List<String>, comment: String) {
        _submitState.value = FeedbackSubmitState.Loading
        val data = hashMapOf(
            "rating" to rating,
            "categories" to categories,
            "comment" to comment,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("feedbacks")
            .add(data)
            .addOnSuccessListener { _submitState.value = FeedbackSubmitState.Success }
            .addOnFailureListener { e ->
                _submitState.value = FeedbackSubmitState.Error(e.message ?: "Unknown error")
            }
    }

    fun resetState() {
        _submitState.value = FeedbackSubmitState.Idle
    }
}
