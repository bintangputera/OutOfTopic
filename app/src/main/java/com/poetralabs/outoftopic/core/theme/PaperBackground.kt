package com.poetralabs.outoftopic.core.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Latar kertas kraft: warna dasar, bintik serat halus, dan sedikit gelap di tepi
 * supaya terasa seperti permukaan meja, bukan bidang warna rata.
 */
fun Modifier.paperBackground(): Modifier = drawWithCache {
    val random = Random(7)
    val density = this.density
    val specks = List(320) {
        Triple(
            Offset(random.nextFloat() * size.width, random.nextFloat() * size.height),
            (0.5f + random.nextFloat() * 1.1f) * density,
            0.04f + random.nextFloat() * 0.07f
        )
    }
    val vignette = Brush.radialGradient(
        colors = listOf(Color.Transparent, Tinta.copy(alpha = 0.10f)),
        center = Offset(size.width / 2f, size.height / 2f),
        radius = maxOf(size.width, size.height) * 0.75f
    )
    onDrawBehind {
        drawRect(Kertas)
        specks.forEach { (center, radius, alpha) ->
            drawCircle(color = Tinta.copy(alpha = alpha), radius = radius, center = center)
        }
        drawRect(vignette)
    }
}
