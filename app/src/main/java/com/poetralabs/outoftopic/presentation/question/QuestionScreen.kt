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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.poetralabs.outoftopic.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.poetralabs.outoftopic.core.theme.BackgroundWhite
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
        containerColor = BackgroundWhite,
        topBar = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "arrowBack",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                if (!isFinished) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = themeName, color = Color.Black,
                        style = MaterialTheme.typography.titleLarge,
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
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Mau lanjut ke tema lain?",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Text(
                            "Kembali ke pilihan tema",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
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

                // Resets to 0 automatically whenever the front question changes
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
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Behind card — scales up as the drag progresses
                        val swipingToPrev = dragOffset.value > 0f
                        val behindData =
                            if (swipingToPrev) previousQuestion else questions.getOrNull(1)
                        val progress =
                            (dragOffset.value.absoluteValue / screenWidthPx).coerceIn(0f, 1f)
                        val behindScale = 0.92f + progress * 0.08f

                        // Only show the behind card when there is actually something behind
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

                        // Front card — draggable, translates and rotates with the drag
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
                                                        // Fly off left → next question
                                                        dragOffset.animateTo(
                                                            -screenWidthPx * 1.4f,
                                                            tween(durationMillis = 280)
                                                        )
                                                        viewModel.nextQuestion()
                                                    }

                                                    ((dragOffset.value > swipeThreshold) ||
                                                            (velocity > velocityThreshold && dragOffset.value > minFlingDisplacement)) && previousQuestion != null -> {
                                                        // Fly off right → previous question
                                                        dragOffset.animateTo(
                                                            screenWidthPx * 1.4f,
                                                            tween(durationMillis = 280)
                                                        )
                                                        viewModel.goToPreviousQuestion()
                                                    }

                                                    else -> {
                                                        // Not far enough — spring back
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
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.25f),
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text(
                                            text = "Share ke Instagram",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
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
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
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
        // Instagram not installed — fall back to the system share sheet
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(fallback, "Bagikan pertanyaan"))
    }
}

/**
 * Generates a 1080×1920 Instagram Story image from scratch matching the branded template:
 *  - App logo + tagline at top
 *  - Question card (bordered rounded rect) in the middle with an empty "Jawaban Kamu:" answer box
 *  - "OutOfTopic" label + Google Play badge at the bottom
 */
private fun createStoryTemplate(context: Context, questionText: String): Bitmap {
    val W = 1080
    val H = 1920
    val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    canvas.drawColor(android.graphics.Color.WHITE)

    val bebasNeue = ResourcesCompat.getFont(context, R.font.bebasneue_regular)
    val playfairBold = ResourcesCompat.getFont(context, R.font.playfair_display_bold)
    val playfairReg = ResourcesCompat.getFont(context, R.font.playfair_display_regular)

    // ── App logo ─────────────────────────────────────────────────────────────
    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        textSize = W * 0.185f
        typeface = bebasNeue ?: Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("OUT OF", W / 2f, H * 0.135f, titlePaint)
    canvas.drawText("TOPIC", W / 2f, H * 0.215f, titlePaint)

    val taglinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        textSize = W * 0.034f
        typeface = Typeface.create(playfairReg ?: Typeface.DEFAULT, Typeface.ITALIC)
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(
        "\u201CBukan gak nyambung, cuma Out Of Topic.\u201D",
        W / 2f,
        H * 0.253f,
        taglinePaint
    )

    // ── Question card ─────────────────────────────────────────────────────────
    val cardL = W * 0.074f
    val cardR = W * 0.926f
    val cardT = H * 0.285f
    val cardB = H * 0.620f
    val cardRadius = 50f

    val cardBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 7f
    }
    canvas.drawRoundRect(RectF(cardL, cardT, cardR, cardB), cardRadius, cardRadius, cardBorderPaint)

    // Question text — bold, centered, word-wrapped in upper portion of card
    val qPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        textSize = W * 0.053f
        typeface = Typeface.create(playfairBold ?: Typeface.DEFAULT_BOLD, Typeface.BOLD)
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
        color = android.graphics.Color.BLACK
        textSize = W * 0.036f
        typeface = playfairReg ?: Typeface.DEFAULT
        textAlign = Paint.Align.LEFT
    }
    val innerPad = W * 0.065f
    val labelY = cardT + (cardB - cardT) * 0.655f
    canvas.drawText("Jawaban Kamu:", cardL + innerPad, labelY, labelPaint)

    // Empty answer box
    val answerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    canvas.drawRoundRect(
        RectF(cardL + innerPad, labelY + H * 0.008f, cardR - innerPad, cardB - H * 0.022f),
        22f, 22f, answerPaint
    )

    // ── Bottom branding ───────────────────────────────────────────────────────
    val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        textSize = W * 0.058f
        typeface = Typeface.create(playfairBold ?: Typeface.DEFAULT_BOLD, Typeface.BOLD)
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
    // Word-wrap
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

    // Vertically center the text block inside the area
    val fm = paint.fontMetrics
    val lineH = paint.fontSpacing
    val blockH = lines.size * lineH
    val firstBaseline = (areaTop + areaBottom) / 2f - blockH / 2f - fm.ascent

    lines.forEachIndexed { i, line ->
        canvas.drawText(line, centerX, firstBaseline + i * lineH, paint)
    }
}