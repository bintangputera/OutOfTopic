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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
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
import com.poetralabs.outoftopic.core.theme.AnthropicNearBlack
import com.poetralabs.outoftopic.core.theme.Ivory
import com.poetralabs.outoftopic.core.theme.OliveGray
import com.poetralabs.outoftopic.core.theme.paperBackground
import com.poetralabs.outoftopic.core.theme.SerifFamily
import com.poetralabs.outoftopic.core.theme.WajahKartu
import com.poetralabs.outoftopic.presentation.component.PlayingCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.absoluteValue

private val cardSuits = listOf("\u2660\uFE0E", "\u2665\uFE0E", "\u2666\uFE0E", "\u2663\uFE0E")

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
        containerColor = Color.Transparent,
        modifier = Modifier.paperBackground(),
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
                            // Same face the card will have once it becomes the front card,
                            // so nothing changes visually at the moment the swipe completes.
                            QuestionCardFace(
                                data = behindData,
                                number = if (swipingToPrev) currentIndex - 1 else currentIndex + 1,
                                onShare = {}
                            )
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
                            QuestionCardFace(
                                data = questions[0],
                                number = currentIndex,
                                onShare = {
                                    val questionText = questions[0].question.question
                                    scope.launch {
                                        shareQuestionToInstagram(context, questionText)
                                    }
                                }
                            )
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

@Composable
private fun QuestionCardFace(
    data: QuestionWithColor,
    number: Int,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(data.color)
    PlayingCard(
        color = color,
        rank = number.toString(),
        suit = cardSuits[number.mod(cardSuits.size)],
        tilt = -1f,
        modifier = modifier
            .fillMaxWidth(0.86f)
            .aspectRatio(0.72f)
    ) {
        Text(
            text = data.question.question,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = SerifFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            textAlign = TextAlign.Center,
            color = AnthropicNearBlack
        )
        OutlinedButton(
            onClick = onShare,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(44.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, color),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
        ) {
            Text(
                text = "Share ke Instagram",
                style = MaterialTheme.typography.titleMedium
            )
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
 * Generates a 1080x1920 Instagram Story image: a tilted playing card on kraft paper,
 * with the same print palette and Bebas/Playfair type as the app.
 */
private fun createStoryTemplate(context: Context, questionText: String): Bitmap {
    val W = 1080
    val H = 1920
    val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)

    val ink = 0xFF2B211B.toInt()
    val red = 0xFFD4412F.toInt()
    val cardFace = 0xFFF8EFD8.toInt()
    val cardLine = 0xFFD9C79F.toInt()
    val bodyGray = 0xFF6B5B4D.toInt()

    val bebas = ResourcesCompat.getFont(context, R.font.bebasneue_regular)
        ?: Typeface.create("sans-serif-condensed", Typeface.BOLD)
    val playfairMedium = ResourcesCompat.getFont(context, R.font.playfair_display_medium)
        ?: Typeface.create("serif", Typeface.NORMAL)
    val playfairReg = ResourcesCompat.getFont(context, R.font.playfair_display_regular)
        ?: Typeface.create("serif", Typeface.NORMAL)

    // Kraft paper: base color, fiber specks, and a darker edge.
    canvas.drawColor(0xFFEADDBF.toInt())
    val random = kotlin.random.Random(7)
    val speckPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    repeat(700) {
        speckPaint.color = ink
        speckPaint.alpha = (10 + random.nextInt(18))
        canvas.drawCircle(
            random.nextFloat() * W,
            random.nextFloat() * H,
            1.2f + random.nextFloat() * 2.4f,
            speckPaint
        )
    }
    val vignette = Paint().apply {
        shader = android.graphics.RadialGradient(
            W / 2f, H / 2f, H * 0.75f,
            intArrayOf(0x00000000, 0x1A2B211B),
            floatArrayOf(0.55f, 1f),
            android.graphics.Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, W.toFloat(), H.toFloat(), vignette)

    // Title: printed twice with a small offset, like a misregistered offset print.
    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = W * 0.17f
        typeface = bebas
        textAlign = Paint.Align.CENTER
    }
    listOf(red to 7f, ink to 0f).forEach { (color, offset) ->
        titlePaint.color = color
        canvas.drawText("OUT OF", W / 2f + offset, H * 0.118f + offset, titlePaint)
        canvas.drawText("TOPIC", W / 2f + offset, H * 0.205f + offset, titlePaint)
    }

    val taglinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = bodyGray
        textSize = W * 0.036f
        typeface = Typeface.create(playfairReg, Typeface.ITALIC)
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(
        "\u201CBukan gak nyambung, cuma Out Of Topic.\u201D",
        W / 2f,
        H * 0.250f,
        taglinePaint
    )

    // Playing card, tilted slightly around its center.
    val cardL = W * 0.10f
    val cardR = W * 0.90f
    val cardT = H * 0.295f
    val cardB = H * 0.735f
    val cardRect = RectF(cardL, cardT, cardR, cardB)
    canvas.save()
    canvas.rotate(-2.2f, W / 2f, (cardT + cardB) / 2f)

    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = cardFace
        setShadowLayer(36f, 0f, 16f, 0x552B211B)
    }
    canvas.drawRoundRect(cardRect, 56f, 56f, shadowPaint)
    val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = cardFace }
    canvas.drawRoundRect(cardRect, 56f, 56f, facePaint)
    val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = cardLine
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    canvas.drawRoundRect(cardRect, 56f, 56f, edgePaint)
    val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = red
        alpha = 140
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }
    val inset = 30f
    canvas.drawRoundRect(
        RectF(cardL + inset, cardT + inset, cardR - inset, cardB - inset),
        38f, 38f, framePaint
    )

    // Corner index: rank and suit top-left, mirrored bottom-right.
    val rankPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = red
        textSize = 92f
        typeface = bebas
        textAlign = Paint.Align.CENTER
    }
    val suitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = red
        textSize = 72f
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.CENTER
    }
    val idxX = cardL + 98f
    val idxY = cardT + 150f
    canvas.drawText("Q", idxX, idxY, rankPaint)
    canvas.drawText("\u2666\uFE0E", idxX, idxY + 78f, suitPaint)
    canvas.save()
    canvas.rotate(180f, W / 2f, (cardT + cardB) / 2f)
    canvas.drawText("Q", idxX, idxY, rankPaint)
    canvas.drawText("♦︎", idxX, idxY + 78f, suitPaint)
    canvas.restore()

    // Question text
    val qPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ink
        textSize = W * 0.056f
        typeface = playfairMedium
        textAlign = Paint.Align.CENTER
    }
    drawCenteredMultilineText(
        canvas = canvas,
        text = questionText,
        paint = qPaint,
        centerX = W / 2f,
        areaTop = cardT + H * 0.045f,
        areaBottom = cardT + (cardB - cardT) * 0.58f,
        maxWidth = (cardR - cardL) - W * 0.26f
    )

    // "Jawaban Kamu:" label and empty answer box
    val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = bodyGray
        textSize = W * 0.038f
        typeface = Typeface.create(playfairReg, Typeface.ITALIC)
        textAlign = Paint.Align.LEFT
    }
    val innerPad = W * 0.115f
    val labelY = cardT + (cardB - cardT) * 0.665f
    canvas.drawText("Jawaban kamu:", cardL + innerPad, labelY, labelPaint)
    val answerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ink
        alpha = 90
        style = Paint.Style.STROKE
        strokeWidth = 4f
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(18f, 12f), 0f)
    }
    canvas.drawRoundRect(
        RectF(cardL + innerPad, labelY + H * 0.012f, cardR - innerPad, cardB - H * 0.095f),
        26f, 26f, answerPaint
    )
    canvas.restore()

    // Bottom branding
    val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ink
        textSize = W * 0.085f
        typeface = bebas
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("OUT OF TOPIC", W / 2f, H * 0.846f, brandPaint)

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
