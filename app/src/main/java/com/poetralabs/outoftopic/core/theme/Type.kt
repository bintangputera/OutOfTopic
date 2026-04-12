package com.poetralabs.outoftopic.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.poetralabs.outoftopic.R

/**
 * Serif family — used for all headlines (weight 500 only).
 * Substitute for Anthropic Serif; PlayfairDisplay is a high-quality editorial serif.
 */
val SerifFamily = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_medium, FontWeight.Medium),
    Font(R.font.playfair_display_semi_bold, FontWeight.SemiBold),
    Font(R.font.playfair_display_bold, FontWeight.Bold)
)

/**
 * Sans family — used for body text, labels, navigation, and all UI elements.
 * Substitute for Anthropic Sans; system default provides a clean sans-serif.
 */
val SansFamily = FontFamily.Default

val BebasNeue = FontFamily(
    Font(R.font.bebasneue_regular, weight = FontWeight.Normal)
)

/**
 * Typography scale inspired by Claude (Anthropic) design system.
 *
 * Principles:
 * - Serif for authority (headlines): all at weight 500 (Medium), single consistent voice
 * - Sans for utility (body/UI): quiet, efficient functional text
 * - Relaxed body line-height (~1.60) for a literary reading experience
 * - Tight-but-comfortable heading line-heights (1.10–1.30)
 */
val Typography = Typography(
    // ── Serif Headlines ──────────────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily = BebasNeue,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,
        lineHeight = 43.sp,       // ~1.20
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 42.sp,       // ~1.30
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 31.sp,       // ~1.10
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 29.sp,       // ~1.20
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 24.sp,       // ~1.20
        letterSpacing = 0.sp
    ),
    // ── Serif Body ───────────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 27.sp,       // ~1.60
        letterSpacing = 0.sp
    ),
    // ── Sans Titles & Navigation ─────────────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 32.sp,       // ~1.60
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,       // ~1.50
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp,       // ~1.40
        letterSpacing = 0.sp
    ),
    // ── Sans Body ────────────────────────────────────────────────────────────
    bodyMedium = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,       // ~1.60
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,       // ~1.43
        letterSpacing = 0.sp
    ),
    // ── Sans Labels ──────────────────────────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,       // ~1.25
        letterSpacing = 0.12.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,       // ~1.33
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 16.sp,       // ~1.60
        letterSpacing = 0.1.sp
    )
)