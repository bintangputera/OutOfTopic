package com.poetralabs.outoftopic.presentation.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poetralabs.outoftopic.core.theme.AnthropicNearBlack
import com.poetralabs.outoftopic.core.theme.Ivory
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Parchment
import com.poetralabs.outoftopic.core.theme.RingWarm
import com.poetralabs.outoftopic.core.theme.StoneGray
import com.poetralabs.outoftopic.core.theme.TerracottaBrand
import com.poetralabs.outoftopic.core.theme.WarmSand
import org.koin.androidx.compose.koinViewModel

private val feedbackCategories = listOf(
    "Truth or Dare",
    "Random Question",
    "Mini Games",
    "Desain aplikasi",
    "Kemudahan penggunaan"
)

private val ratingEmojis = listOf("1", "2", "3", "4", "5")

@Composable
fun FeedbackScreen(
    onBack: () -> Unit = {},
    viewModel: FeedbackViewModel = koinViewModel()
) {
    var rating by remember { mutableIntStateOf(0) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var comment by remember { mutableStateOf("") }

    val submitState by viewModel.submitState.collectAsState()

    if (submitState is FeedbackSubmitState.Success) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetState()
                onBack()
            },
            containerColor = Ivory,
            title = {
                Text(
                    "Terima kasih!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AnthropicNearBlack
                )
            },
            text = {
                Text(
                    "Feedback kamu sudah diterima. Kami akan terus membaik!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OliveGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetState()
                        onBack()
                    }
                ) {
                    Text(
                        "OK",
                        color = TerracottaBrand,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = Parchment,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AnthropicNearBlack
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Share Your Feedback",
                style = MaterialTheme.typography.headlineMedium,
                color = AnthropicNearBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Masukan kamu membantu kami membuat Out of Topic jadi lebih seru!",
                style = MaterialTheme.typography.bodyMedium,
                color = OliveGray
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Rating
            Text(
                text = "Seberapa puas kamu?",
                style = MaterialTheme.typography.headlineSmall,
                color = AnthropicNearBlack
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ratingEmojis.forEachIndexed { index, label ->
                    val ratingValue = index + 1
                    val isSelected = rating == ratingValue
                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .clickable { rating = ratingValue },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) TerracottaBrand.copy(alpha = 0.12f) else Color.Transparent,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) TerracottaBrand else RingWarm
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (isSelected) TerracottaBrand else OliveGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Categories
            Text(
                text = "Apa yang kamu suka?",
                style = MaterialTheme.typography.headlineSmall,
                color = AnthropicNearBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            feedbackCategories.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    row.forEach { category ->
                        val isChecked = category in selectedCategories
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedCategories = if (isChecked) {
                                        selectedCategories - category
                                    } else {
                                        selectedCategories + category
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    selectedCategories = if (checked) {
                                        selectedCategories + category
                                    } else {
                                        selectedCategories - category
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = TerracottaBrand,
                                    checkmarkColor = Ivory,
                                    uncheckedColor = StoneGray
                                )
                            )
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AnthropicNearBlack
                            )
                        }
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Comment
            Text(
                text = "Komentar tambahan (opsional)",
                style = MaterialTheme.typography.headlineSmall,
                color = AnthropicNearBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = {
                    Text(
                        text = "Ceritakan pengalaman atau saranmu di sini...",
                        color = StoneGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TerracottaBrand,
                    unfocusedBorderColor = RingWarm,
                    focusedTextColor = AnthropicNearBlack
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (submitState is FeedbackSubmitState.Error) {
                Text(
                    text = "Gagal mengirim feedback. Coba lagi.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.submitFeedback(
                        rating = rating,
                        categories = selectedCategories.toList(),
                        comment = comment
                    )
                },
                enabled = rating > 0 && submitState !is FeedbackSubmitState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TerracottaBrand,
                    disabledContainerColor = TerracottaBrand.copy(alpha = 0.4f)
                )
            ) {
                if (submitState is FeedbackSubmitState.Loading) {
                    CircularProgressIndicator(
                        color = Ivory,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Submit Feedback",
                        color = Ivory,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
