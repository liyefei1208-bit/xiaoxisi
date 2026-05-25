package com.xiaoxisi.automation.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuideOverlayView @Inject constructor(
    @ApplicationContext private val context: Context
) : View(context) {

    private var guideText: String = ""
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#FF6B35")
    }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#33FF6B35")
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 36f
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 2f, Color.BLACK)
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setGuideText(text: String) {
        guideText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val maxRadius = width.coerceAtMost(height) / 2f - 10f

        val gradient = RadialGradient(
            cx, cy, maxRadius,
            intArrayOf(Color.parseColor("#44FF6B35"), Color.parseColor("#00FF6B35")),
            null,
            Shader.TileMode.CLAMP
        )
        circlePaint.shader = gradient

        canvas.drawCircle(cx, cy, 60f, fillPaint)
        canvas.drawCircle(cx, cy, 60f, circlePaint)

        val pulseRadius = 60f + (System.currentTimeMillis() % 1000) / 1000f * 40f
        canvas.drawCircle(cx, cy, pulseRadius, circlePaint)

        if (guideText.isNotBlank()) {
            canvas.drawText(guideText, cx, cy + 120f, textPaint)
        }
    }
}
