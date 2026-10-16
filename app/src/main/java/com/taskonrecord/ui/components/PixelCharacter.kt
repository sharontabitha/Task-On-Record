package com.taskonrecord.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.taskonrecord.data.model.ResidentType
import com.taskonrecord.data.model.TaskStatus

/**
 * Renders a pixel-art character sprite on a Canvas.
 * Each character is drawn as a grid of colored squares (pixels).
 * Characters animate based on their task status.
 */
@Composable
fun PixelCharacter(
    residentType: ResidentType,
    status: TaskStatus,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    // Bounce/float animation for active characters
    val infiniteTransition = rememberInfiniteTransition(label = "char_anim")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = when (status) {
            TaskStatus.DONE -> 6f          // happy bounce
            TaskStatus.IN_PROGRESS -> 3f   // gentle float
            TaskStatus.OVERDUE -> 1f       // slow sad sway
            TaskStatus.PENDING -> 0f       // idle, no movement
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (status) {
                    TaskStatus.DONE -> 400
                    TaskStatus.IN_PROGRESS -> 700
                    TaskStatus.OVERDUE -> 1500
                    TaskStatus.PENDING -> 2000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    val pixels = remember(residentType, status) {
        getPixelMap(residentType, status)
    }

    Canvas(modifier = modifier.size(size)) {
        val gridSize = pixels.size
        val pixelSize = this.size.width / gridSize

        pixels.forEachIndexed { row, cols ->
            cols.forEachIndexed { col, color ->
                if (color != Color.Transparent) {
                    drawRect(
                        color = color,
                        topLeft = Offset(
                            x = col * pixelSize,
                            y = row * pixelSize - bobOffset
                        ),
                        size = Size(pixelSize - 0.5f, pixelSize - 0.5f)
                    )
                }
            }
        }
    }
}

// ── Transparent shorthand ────────────────────────────────────────
private val X = Color.Transparent

/**
 * Returns the pixel grid for each resident type & status.
 * Each array is rows × cols of Color values.
 * Grid is 12×12 pixels.
 */
fun getPixelMap(type: ResidentType, status: TaskStatus): Array<Array<Color>> {
    val statusTint = when (status) {
        TaskStatus.DONE -> Color(0xFFFFD700)      // gold overlay hint
        TaskStatus.OVERDUE -> Color(0xFFBDBDBD)   // gray-ish
        else -> null
    }

    return when (type) {
        ResidentType.PIG -> pigPixels(status)
        ResidentType.DOG -> dogPixels(status)
        ResidentType.CAT -> catPixels(status)
        ResidentType.MOUSE -> mousePixels(status)
        ResidentType.BOY -> boyPixels(status)
        ResidentType.GIRL -> girlPixels(status)
        ResidentType.BABY -> babyPixels(status)
        ResidentType.GRANNY -> grannyPixels(status)
        ResidentType.SUNFLOWER -> sunflowerPixels(status)
        ResidentType.ROSE -> rosePixels(status)
        ResidentType.TULIP -> tulipPixels(status)
        ResidentType.WHEAT -> wheatPixels(status)
        ResidentType.CARROT -> carrotPixels(status)
        ResidentType.CORN -> cornPixels(status)
    }
}

// ── Pixel color helpers ───────────────────────────────────────────
private val PINK_LIGHT = Color(0xFFFFC0CB)
private val PINK = Color(0xFFFF69B4)
private val PINK_DARK = Color(0xFFFF1493)
private val SKIN = Color(0xFFF4C494)
private val SKIN_DARK = Color(0xFFD4956A)
private val BLACK = Color(0xFF222222)
private val WHITE = Color(0xFFF5F5F5)
private val GRAY = Color(0xFF9E9E9E)
private val DARK_GRAY = Color(0xFF424242)
private val BROWN = Color(0xFF795548)
private val BROWN_LIGHT = Color(0xFFA1887F)
private val ORANGE = Color(0xFFFF9800)
private val YELLOW = Color(0xFFFFEB3B)
private val YELLOW_DARK = Color(0xFFF9A825)
private val GREEN = Color(0xFF4CAF50)
private val GREEN_DARK = Color(0xFF2E7D32)
private val GREEN_LIGHT = Color(0xFF81C784)
private val RED = Color(0xFFE53935)
private val RED_DARK = Color(0xFFB71C1C)
private val BLUE = Color(0xFF1E88E5)
private val BLUE_LIGHT = Color(0xFF64B5F6)
private val PURPLE = Color(0xFF9C27B0)
private val PURPLE_LIGHT = Color(0xFFCE93D8)

// ════════════════════════════════════════════════════
//  ANIMAL SPRITES (College residents)
// ════════════════════════════════════════════════════

private fun pigPixels(status: TaskStatus): Array<Array<Color>> {
    val body = if (status == TaskStatus.OVERDUE) GRAY else PINK_LIGHT
    val snout = if (status == TaskStatus.OVERDUE) DARK_GRAY else PINK
    val eye = if (status == TaskStatus.DONE) Color(0xFFFFD700) else BLACK
    return arrayOf(
        arrayOf(X, X, X, PINK, PINK, X, PINK, PINK, X, X, X, X),
        arrayOf(X, X, PINK, PINK, PINK, PINK, PINK, PINK, PINK, X, X, X),
        arrayOf(X, PINK, body, body, body, body, body, body, body, PINK, X, X),
        arrayOf(X, body, body, eye, body, body, body, eye, body, body, X, X),
        arrayOf(X, body, body, body, body, body, body, body, body, body, X, X),
        arrayOf(X, body, body, snout, snout, snout, snout, snout, body, body, X, X),
        arrayOf(X, X, body, body, body, body, body, body, body, X, X, X),
        arrayOf(X, X, body, body, body, body, body, body, body, X, X, X),
        arrayOf(X, body, body, X, body, body, body, body, X, body, X, X),
        arrayOf(X, body, X, X, body, X, X, body, X, body, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun dogPixels(status: TaskStatus): Array<Array<Color>> {
    val body = if (status == TaskStatus.OVERDUE) GRAY else Color(0xFFC68B59)
    val ear = if (status == TaskStatus.OVERDUE) DARK_GRAY else Color(0xFF8B5A2B)
    val snout = Color(0xFFE2A06E)
    val eye = if (status == TaskStatus.DONE) Color(0xFFFFD700) else BLACK
    return arrayOf(
        arrayOf(X, X, ear, ear, X, X, X, ear, ear, X, X, X),
        arrayOf(X, ear, body, body, ear, X, ear, body, body, ear, X, X),
        arrayOf(X, body, body, body, body, body, body, body, body, body, X, X),
        arrayOf(X, body, eye, body, body, body, body, eye, body, body, X, X),
        arrayOf(X, body, body, body, snout, snout, body, body, body, body, X, X),
        arrayOf(X, X, body, snout, snout, snout, snout, body, body, X, X, X),
        arrayOf(X, X, body, body, body, body, body, body, X, X, X, X),
        arrayOf(X, body, body, body, body, body, body, body, body, body, X, X),
        arrayOf(body, body, X, body, body, body, body, X, body, body, X, X),
        arrayOf(body, X, X, body, X, X, body, X, X, body, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun catPixels(status: TaskStatus): Array<Array<Color>> {
    val body = if (status == TaskStatus.OVERDUE) GRAY else Color(0xFFBDBDBD)
    val stripe = if (status == TaskStatus.OVERDUE) DARK_GRAY else DARK_GRAY
    val eye = if (status == TaskStatus.DONE) Color(0xFFFFD700) else Color(0xFF4CAF50)
    return arrayOf(
        arrayOf(X, X, body, X, X, X, X, X, body, X, X, X),
        arrayOf(X, body, body, body, X, X, body, body, body, X, X, X),
        arrayOf(body, body, body, body, body, body, body, body, body, body, X, X),
        arrayOf(body, body, eye, body, body, body, body, eye, body, body, X, X),
        arrayOf(body, body, body, body, body, body, body, body, body, body, X, X),
        arrayOf(X, body, body, body, body, body, body, body, body, X, X, X),
        arrayOf(X, stripe, body, body, body, body, body, body, stripe, X, X, X),
        arrayOf(X, X, body, body, body, body, body, body, X, X, X, X),
        arrayOf(X, body, body, X, body, body, X, body, body, X, X, X),
        arrayOf(body, body, X, X, body, X, body, X, X, body, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun mousePixels(status: TaskStatus): Array<Array<Color>> {
    val body = if (status == TaskStatus.OVERDUE) DARK_GRAY else GRAY
    val ear = PINK_LIGHT
    val eye = if (status == TaskStatus.DONE) Color(0xFFFFD700) else BLACK
    return arrayOf(
        arrayOf(X, body, X, X, X, X, X, X, X, body, X, X),
        arrayOf(body, body, X, X, X, X, X, X, X, body, body, X),
        arrayOf(X, body, body, body, body, body, body, body, body, body, X, X),
        arrayOf(X, X, body, eye, body, body, body, eye, body, X, X, X),
        arrayOf(X, X, body, body, body, body, body, body, body, X, X, X),
        arrayOf(X, X, body, body, PINK_LIGHT, body, body, body, X, X, X, X),
        arrayOf(X, X, X, body, body, body, body, X, X, X, X, X),
        arrayOf(X, X, body, body, body, body, body, body, X, X, X, X),
        arrayOf(X, body, body, X, body, body, X, body, body, X, X, X),
        arrayOf(X, body, X, X, body, X, body, X, X, body, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

// ════════════════════════════════════════════════════
//  HUMAN SPRITES (Home residents)
// ════════════════════════════════════════════════════

private fun boyPixels(status: TaskStatus): Array<Array<Color>> {
    val skin = if (status == TaskStatus.OVERDUE) GRAY else SKIN
    val hair = BROWN
    val shirt = if (status == TaskStatus.DONE) Color(0xFF4CAF50) else BLUE
    val pants = BLUE_LIGHT
    return arrayOf(
        arrayOf(X, X, X, hair, hair, hair, hair, hair, X, X, X, X),
        arrayOf(X, X, hair, skin, skin, skin, skin, skin, hair, X, X, X),
        arrayOf(X, X, skin, skin, BLACK, skin, BLACK, skin, skin, X, X, X),
        arrayOf(X, X, skin, skin, skin, skin, skin, skin, skin, X, X, X),
        arrayOf(X, X, X, skin, skin, SKIN_DARK, skin, skin, X, X, X, X),
        arrayOf(X, shirt, shirt, shirt, shirt, shirt, shirt, shirt, shirt, X, X, X),
        arrayOf(shirt, shirt, shirt, shirt, shirt, shirt, shirt, shirt, shirt, shirt, X, X),
        arrayOf(X, X, pants, pants, X, X, X, pants, pants, X, X, X),
        arrayOf(X, X, pants, pants, X, X, X, pants, pants, X, X, X),
        arrayOf(X, X, skin, skin, X, X, X, skin, skin, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun girlPixels(status: TaskStatus): Array<Array<Color>> {
    val skin = if (status == TaskStatus.OVERDUE) GRAY else SKIN
    val hair = Color(0xFFFFC107)
    val dress = if (status == TaskStatus.DONE) Color(0xFFE91E63) else PURPLE
    return arrayOf(
        arrayOf(X, X, hair, hair, hair, hair, hair, hair, hair, X, X, X),
        arrayOf(X, hair, hair, skin, skin, skin, skin, skin, hair, hair, X, X),
        arrayOf(X, hair, skin, skin, BLACK, skin, BLACK, skin, skin, hair, X, X),
        arrayOf(X, hair, skin, skin, skin, skin, skin, skin, skin, hair, X, X),
        arrayOf(X, hair, X, skin, SKIN_DARK, skin, skin, X, X, hair, X, X),
        arrayOf(X, dress, dress, dress, dress, dress, dress, dress, dress, X, X, X),
        arrayOf(dress, dress, dress, dress, dress, dress, dress, dress, dress, dress, X, X),
        arrayOf(dress, dress, dress, dress, dress, dress, dress, dress, dress, dress, X, X),
        arrayOf(X, dress, dress, skin, skin, X, skin, skin, dress, X, X, X),
        arrayOf(X, X, X, skin, skin, X, skin, skin, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun babyPixels(status: TaskStatus): Array<Array<Color>> {
    val skin = if (status == TaskStatus.OVERDUE) GRAY else SKIN
    val onesie = if (status == TaskStatus.DONE) Color(0xFFFFEB3B) else Color(0xFFE3F2FD)
    return arrayOf(
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, skin, skin, skin, skin, X, X, X, X, X),
        arrayOf(X, X, skin, skin, skin, skin, skin, skin, X, X, X, X),
        arrayOf(X, X, skin, BLACK, skin, skin, BLACK, skin, X, X, X, X),
        arrayOf(X, X, skin, skin, skin, skin, skin, skin, X, X, X, X),
        arrayOf(X, X, X, skin, onesie, onesie, skin, X, X, X, X, X),
        arrayOf(X, X, onesie, onesie, onesie, onesie, onesie, onesie, X, X, X, X),
        arrayOf(X, X, onesie, onesie, onesie, onesie, onesie, onesie, X, X, X, X),
        arrayOf(X, X, skin, onesie, X, X, onesie, skin, X, X, X, X),
        arrayOf(X, X, skin, X, X, X, X, skin, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun grannyPixels(status: TaskStatus): Array<Array<Color>> {
    val skin = if (status == TaskStatus.OVERDUE) DARK_GRAY else SKIN
    val hair = if (status == TaskStatus.OVERDUE) GRAY else WHITE
    val dress = if (status == TaskStatus.DONE) Color(0xFF9C27B0) else Color(0xFF607D8B)
    val glasses = Color(0xFF795548)
    return arrayOf(
        arrayOf(X, X, hair, hair, hair, hair, hair, hair, X, X, X, X),
        arrayOf(X, hair, hair, skin, skin, skin, skin, skin, hair, X, X, X),
        arrayOf(X, hair, skin, glasses, skin, skin, glasses, skin, skin, X, X, X),
        arrayOf(X, hair, skin, skin, skin, skin, skin, skin, skin, X, X, X),
        arrayOf(X, X, skin, skin, skin, skin, skin, skin, X, X, X, X),
        arrayOf(X, dress, dress, dress, dress, dress, dress, dress, dress, X, X, X),
        arrayOf(dress, dress, dress, dress, dress, dress, dress, dress, dress, dress, X, X),
        arrayOf(dress, dress, dress, dress, dress, dress, dress, dress, dress, dress, X, X),
        arrayOf(X, dress, skin, skin, X, X, skin, skin, dress, X, X, X),
        arrayOf(X, X, skin, skin, X, X, skin, skin, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

// ════════════════════════════════════════════════════
//  PLANT SPRITES (Garden residents)
// ════════════════════════════════════════════════════

private fun sunflowerPixels(status: TaskStatus): Array<Array<Color>> {
    val petal = if (status == TaskStatus.OVERDUE) GRAY else YELLOW
    val center = if (status == TaskStatus.OVERDUE) DARK_GRAY else BROWN
    val stem = if (status == TaskStatus.DONE) GREEN else GREEN_DARK
    val bloomSize = when (status) {
        TaskStatus.DONE -> 4
        TaskStatus.IN_PROGRESS -> 2
        else -> 1
    }
    return arrayOf(
        arrayOf(X, X, X, X, petal, petal, petal, X, X, X, X, X),
        arrayOf(X, X, X, petal, center, center, center, petal, X, X, X, X),
        arrayOf(X, petal, petal, center, center, center, center, center, petal, petal, X, X),
        arrayOf(X, petal, center, center, YELLOW_DARK, YELLOW_DARK, center, center, petal, X, X, X),
        arrayOf(X, petal, petal, center, center, center, center, center, petal, petal, X, X),
        arrayOf(X, X, X, petal, center, center, center, petal, X, X, X, X),
        arrayOf(X, X, X, X, petal, stem, petal, X, X, X, X, X),
        arrayOf(X, X, X, X, X, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, GREEN_LIGHT, stem, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, stem, X, X, X, X, X, X),
    )
}

private fun rosePixels(status: TaskStatus): Array<Array<Color>> {
    val bloom = if (status == TaskStatus.OVERDUE) DARK_GRAY else RED
    val inner = if (status == TaskStatus.OVERDUE) GRAY else RED_DARK
    val stem = GREEN_DARK
    val leaf = if (status == TaskStatus.DONE) GREEN else GREEN_DARK
    return arrayOf(
        arrayOf(X, X, X, bloom, bloom, bloom, bloom, X, X, X, X, X),
        arrayOf(X, X, bloom, bloom, inner, inner, bloom, bloom, X, X, X, X),
        arrayOf(X, bloom, bloom, inner, inner, inner, inner, bloom, bloom, X, X, X),
        arrayOf(X, bloom, inner, inner, inner, inner, inner, inner, bloom, X, X, X),
        arrayOf(X, bloom, bloom, inner, inner, inner, inner, bloom, bloom, X, X, X),
        arrayOf(X, X, bloom, bloom, bloom, bloom, bloom, bloom, X, X, X, X),
        arrayOf(X, X, X, X, stem, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, leaf, stem, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, X, stem, stem, leaf, X, X, X, X, X),
        arrayOf(X, X, X, X, stem, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, X, stem, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun tulipPixels(status: TaskStatus): Array<Array<Color>> {
    val petal = if (status == TaskStatus.OVERDUE) GRAY else PINK
    val inner = if (status == TaskStatus.OVERDUE) DARK_GRAY else PURPLE
    val stem = GREEN_DARK
    val leaf = if (status == TaskStatus.DONE) GREEN else GREEN_DARK
    return arrayOf(
        arrayOf(X, X, X, petal, petal, petal, petal, X, X, X, X, X),
        arrayOf(X, X, petal, petal, inner, inner, petal, petal, X, X, X, X),
        arrayOf(X, X, petal, inner, inner, inner, inner, petal, X, X, X, X),
        arrayOf(X, X, petal, petal, inner, inner, petal, petal, X, X, X, X),
        arrayOf(X, X, X, petal, petal, petal, petal, X, X, X, X, X),
        arrayOf(X, X, X, X, stem, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, X, stem, stem, X, X, X, X, X, X),
        arrayOf(X, X, leaf, leaf, stem, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, X, stem, stem, leaf, leaf, X, X, X, X),
        arrayOf(X, X, X, X, stem, stem, X, X, X, X, X, X),
        arrayOf(X, X, X, X, stem, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun wheatPixels(status: TaskStatus): Array<Array<Color>> {
    val grain = if (status == TaskStatus.OVERDUE) GRAY else YELLOW_DARK
    val stalk = if (status == TaskStatus.DONE) Color(0xFFF9A825) else Color(0xFF8BC34A)
    return arrayOf(
        arrayOf(X, X, grain, grain, grain, grain, grain, grain, X, X, X, X),
        arrayOf(X, grain, grain, grain, grain, grain, grain, grain, grain, X, X, X),
        arrayOf(grain, grain, grain, grain, grain, grain, grain, grain, grain, grain, X, X),
        arrayOf(X, grain, grain, grain, grain, grain, grain, grain, grain, X, X, X),
        arrayOf(X, X, grain, grain, grain, grain, grain, grain, X, X, X, X),
        arrayOf(X, X, X, stalk, stalk, stalk, stalk, X, X, X, X, X),
        arrayOf(X, X, X, stalk, stalk, stalk, stalk, X, X, X, X, X),
        arrayOf(X, stalk, X, stalk, stalk, stalk, stalk, X, stalk, X, X, X),
        arrayOf(X, stalk, stalk, stalk, stalk, stalk, stalk, stalk, stalk, X, X, X),
        arrayOf(X, X, stalk, stalk, stalk, stalk, stalk, stalk, X, X, X, X),
        arrayOf(X, X, X, stalk, stalk, stalk, stalk, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun carrotPixels(status: TaskStatus): Array<Array<Color>> {
    val top = if (status == TaskStatus.OVERDUE) DARK_GRAY else GREEN
    val body = if (status == TaskStatus.OVERDUE) GRAY else ORANGE
    val tip = if (status == TaskStatus.DONE) Color(0xFFF57F17) else Color(0xFFE65100)
    return arrayOf(
        arrayOf(X, X, top, X, top, X, top, X, X, X, X, X),
        arrayOf(X, X, top, top, top, top, top, X, X, X, X, X),
        arrayOf(X, X, X, top, top, top, X, X, X, X, X, X),
        arrayOf(X, X, X, body, body, body, X, X, X, X, X, X),
        arrayOf(X, X, body, body, body, body, body, X, X, X, X, X),
        arrayOf(X, body, body, body, body, body, body, body, X, X, X, X),
        arrayOf(X, body, body, body, body, body, body, body, X, X, X, X),
        arrayOf(X, X, body, body, body, body, body, X, X, X, X, X),
        arrayOf(X, X, X, body, body, body, X, X, X, X, X, X),
        arrayOf(X, X, X, X, tip, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}

private fun cornPixels(status: TaskStatus): Array<Array<Color>> {
    val husk = if (status == TaskStatus.OVERDUE) GRAY else GREEN
    val kernel = if (status == TaskStatus.OVERDUE) DARK_GRAY else YELLOW
    val stalk = if (status == TaskStatus.DONE) Color(0xFF8BC34A) else Color(0xFF558B2F)
    return arrayOf(
        arrayOf(X, X, X, husk, husk, husk, X, X, X, X, X, X),
        arrayOf(X, X, husk, kernel, kernel, kernel, husk, X, X, X, X, X),
        arrayOf(X, husk, kernel, kernel, kernel, kernel, kernel, husk, X, X, X, X),
        arrayOf(X, husk, kernel, kernel, kernel, kernel, kernel, husk, X, X, X, X),
        arrayOf(X, husk, kernel, kernel, kernel, kernel, kernel, husk, X, X, X, X),
        arrayOf(X, X, husk, kernel, kernel, kernel, husk, X, X, X, X, X),
        arrayOf(X, X, X, husk, husk, husk, X, X, X, X, X, X),
        arrayOf(X, X, X, stalk, stalk, stalk, X, X, X, X, X, X),
        arrayOf(X, X, stalk, stalk, stalk, stalk, stalk, X, X, X, X, X),
        arrayOf(X, X, X, stalk, stalk, stalk, X, X, X, X, X, X),
        arrayOf(X, X, X, X, stalk, X, X, X, X, X, X, X),
        arrayOf(X, X, X, X, X, X, X, X, X, X, X, X),
    )
}
