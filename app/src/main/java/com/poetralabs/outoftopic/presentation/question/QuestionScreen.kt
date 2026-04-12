package com.poetralabs.outoftopic.presentation.question

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.poetralabs.outoftopic.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.poetralabs.outoftopic.core.theme.AnthropicNearBlack
import com.poetralabs.outoftopic.core.theme.Ivory
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.Parchment
import com.poetralabs.outoftopic.core.theme.TerracottaBrand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.absoluteValue

@Composable
fun QuestionScreen(
    navController: NavController,
    themeId: String,
    themeName: String,
    viewModel: QuestionViewModel = koinViewModel()
) {

    val questions by viewModel.question.collectAsStateWithLifecycle()
    val isFinished by viewModel.isFinished.collectAsStateWithLifecycle()
    val totalQuestions by viewModel.totalQuestions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val previousQuestion by viewModel.previousQuestion.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getAllQuestion(themeId = themeId)
    }

    Scaffold(
        containerColor = Parchment,
        topBar = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "arrowBack",
                    tint = AnthropicNearBlack,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                if (!isFinished) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = themeName, color = AnthropicNearBlack,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isFinished) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Beres!",
                        style = MaterialTheme.typography.displaySmall,
                        color = AnthropicNearBlack,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Mau lanjut ke tema lain?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OliveGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AnthropicNearBlack)
                    ) {
                        Text(
                            "Kembali ke pilihan tema",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            } else if (questions.isNotEmpty()) {
                val density = LocalDensity.current
                val configuration = LocalConfiguration.current
                val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                val swipeThreshold = screenWidthPx * 0.35f
                val velocityThreshold = 1200f
                val minFlingDisplacement = screenWidthPx * 0.12f

                val dragOffset = remember(questions[0]) { Animatable(0f) }
                val velocityTracker = remember { VelocityTracker() }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Progress indicator
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currentIndex / $totalQuestions",
                            color = OliveGray,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Behind card
                        val swipingToPrev = dragOffset.value > 0f
                        val behindData =
                            if (swipingToPrev) previousQuestion else questions.getOrNull(1)
                        val progress =
                            (dragOffset.value.absoluteValue / screenWidthPx).coerceIn(0f, 1f)
                        val behindScale = 0.92f + progress * 0.08f

                        if (behindData != null) Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = behindScale
                                    scaleY = behindScale
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .fillMaxHeight(0.75f),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(behindData.color)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = behindData.question?.question ?: "",
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        // Front card
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationX = dragOffset.value
                                    rotationZ = (dragOffset.value / screenWidthPx) * 12f
                                }
                                .pointerInput(questions[0]) {
                                    detectHorizontalDragGestures(
                                        onDragStart = {
                                            velocityTracker.resetTracking()
                                        },
                                        onHorizontalDrag = { change, dragAmount ->
                                            change.consume()
                                            velocityTracker.addPosition(
                                                change.uptimeMillis,
                                                change.position
                                            )
                                            scope.launch {
                                                dragOffset.snapTo(dragOffset.value + dragAmount)
                                            }
                                        },
                                        onDragEnd = {
                                            val velocity = velocityTracker.calculateVelocity().x
                                            scope.launch {
                                                when {
                                                    dragOffset.value < -swipeThreshold ||
                                                            (velocity < -velocityThreshold && dragOffset.value < -minFlingDisplacement) -> {
                                                        dragOffset.animateTo(
                                                            -screenWidthPx * 1.4f,
                                                            tween(durationMillis = 280)
                                                        )
                                                        viewModel.nextQuestion()
                                                    }

                                                    ((dragOffset.value > swipeThreshold) ||
                                                            (velocity > velocityThreshold && dragOffset.value > minFlingDisplacement)) && previousQuestion != null -> {
                                                        dragOffset.animateTo(
                                                            screenWidthPx * 1.4f,
                                                            tween(durationMillis = 280)
                                                        )
                                                        viewModel.goToPreviousQuestion()
                                                    }

                                                    else -> {
                                                        dragOffset.animateTo(
                                                            0f,
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                                stiffness = Spring.StiffnessMedium
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        onDragCancel = {
                                            scope.launch {
                                                dragOffset.animateTo(
                                                    0f,
                                                    spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                                )
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .fillMaxHeight(0.75f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = questions.getOrNull(0)?.let { Color(it.color) }
                                        ?: Color.Transparent
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp)
                                ) {
                                    Text(
                                        text = questions.getOrNull(0)?.question?.question ?: "",
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                    Button(
                                        onClick = {
                                            val questionText =
                                                questions.getOrNull(0)?.question?.question
                                                    ?: return@Button
                                            scope.launch {
                                                shareQuestionToInstagram(context, questionText)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .align(Alignment.BottomCenter),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.25f),
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text(
                                            text = "Share ke Instagram",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Swipe hint
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Geser untuk melihat pertanyaan selanjutnya",
                            color = OliveGray,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private suspend fun shareQuestionToInstagram(context: Context, questionText: String) {
    val template = withContext(Dispatchers.Default) { createStoryTemplate(context, questionText) }
    val cachePath = File(context.cacheDir, "shared_images").also { it.mkdirs() }
    val file = File(cachePath, "question_card.png")
    withContext(Dispatchers.IO) {
        FileOutputStream(file).use { out ->
            template.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val storyIntent = Intent("com.instagram.share.ADD_TO_STORY").apply {
        setDataAndType(uri, "image/png")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    @Suppress("DEPRECATION")
    if (context.packageManager.resolveActivity(storyIntent, 0) != null) {
        context.grantUriPermission(
            "com.instagram.android",
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        context.startActivity(storyIntent)
    } else {
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(fallback, "Bagikan pertanyaan"))
    }
}

/**
 * Generates a 1080x1920 Instagram Story image with warm Anthropic-inspired styling.
 */
private fun createStoryTemplate(context: Context, questionText: String): Bitmap {
    val W = 1080
    val H = 1920
    val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    // Parchment background
    canvas.drawColor(0xFFF5F4ED.toInt())

    val playfairBold = ResourcesCompat.getFont(context, R.font.playfair_display_bold)
    val playfairMedium = ResourcesCompat.getFont(context, R.font.playfair_display_medium)
    val playfairReg = ResourcesCompat.getFont(context, R.font.playfair_display_regular)

    // App logo — serif style
    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF141413.toInt()
        textSize = W * 0.12f
        typeface = playfairMedium ?: Typeface.create("serif", Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("Out of", W / 2f, H * 0.135f, titlePaint)
    canvas.drawText("Topic", W / 2f, H * 0.200f, titlePaint)

    val taglinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF5E5D59.toInt()
        textSize = W * 0.034f
        typeface = Typeface.create(playfairReg ?: Typeface.create("serif", Typeface.NORMAL), Typeface.ITALIC)
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(
        "\u201CBukan gak nyambung, cuma Out Of Topic.\u201D",
        W / 2f,
        H * 0.240f,
        taglinePaint
    )

    // Question card with terracotta border
    val cardL = W * 0.074f
    val cardR = W * 0.926f
    val cardT = H * 0.285f
    val cardB = H * 0.620f
    val cardRadius = 40f

    val cardBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFC96442.toInt() // Terracotta
        style = Paint.Style.STROKE
        strokeWidth = 7f
    }
    canvas.drawRoundRect(RectF(cardL, cardT, cardR, cardB), cardRadius, cardRadius, cardBorderPaint)

    // Question text
    val qPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF141413.toInt()
        textSize = W * 0.053f
        typeface = Typeface.create(playfairMedium ?: Typeface.create("serif", Typeface.NORMAL), Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
    }
    drawCenteredMultilineText(
        canvas = canvas,
        text = questionText,
        paint = qPaint,
        centerX = W / 2f,
        areaTop = cardT + H * 0.018f,
        areaBottom = cardT + (cardB - cardT) * 0.54f,
        maxWidth = (cardR - cardL) - W * 0.18f
    )

    // "Jawaban Kamu:" label
    val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF5E5D59.toInt()
        textSize = W * 0.036f
        typeface = playfairReg ?: Typeface.create("serif", Typeface.NORMAL)
        textAlign = Paint.Align.LEFT
    }
    val innerPad = W * 0.065f
    val labelY = cardT + (cardB - cardT) * 0.655f
    canvas.drawText("Jawaban Kamu:", cardL + innerPad, labelY, labelPaint)

    // Empty answer box
    val answerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFE8E6DC.toInt() // BorderWarm
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    canvas.drawRoundRect(
        RectF(cardL + innerPad, labelY + H * 0.008f, cardR - innerPad, cardB - H * 0.022f),
        22f, 22f, answerPaint
    )

    // Bottom branding
    val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF141413.toInt()
        textSize = W * 0.058f
        typeface = Typeface.create(playfairMedium ?: Typeface.create("serif", Typeface.NORMAL), Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("OutOfTopic", W / 2f, H * 0.856f, brandPaint)

    // Google Play badge
    runCatching {
        BitmapFactory.decodeResource(context.resources, R.drawable.google_play_badge)
            ?.let { badge ->
                val targetW = W * 0.37f
                val targetH = badge.height * (targetW / badge.width)
                val left = (W - targetW) / 2f
                val top = H * 0.872f
                canvas.drawBitmap(
                    badge,
                    null,
                    RectF(left, top, left + targetW, top + targetH),
                    null
                )
                badge.recycle()
            }
    }

    return bmp
}

private fun drawCenteredMultilineText(
    canvas: android.graphics.Canvas,
    text: String,
    paint: Paint,
    centerX: Float,
    areaTop: Float,
    areaBottom: Float,
    maxWidth: Float
) {
    val lines = mutableListOf<String>()
    var current = ""
    for (word in text.split(" ")) {
        val test = if (current.isEmpty()) word else "$current $word"
        if (paint.measureText(test) > maxWidth && current.isNotEmpty()) {
            lines.add(current)
            current = word
        } else {
            current = test
        }
    }
    if (current.isNotEmpty()) lines.add(current)

    val fm = paint.fontMetrics
    val lineH = paint.fontSpacing
    val blockH = lines.size * lineH
    val firstBaseline = (areaTop + areaBottom) / 2f - blockH / 2f - fm.ascent

    lines.forEachIndexed { i, line ->
        canvas.drawText(line, centerX, firstBaseline + i * lineH, paint)
    }
}
