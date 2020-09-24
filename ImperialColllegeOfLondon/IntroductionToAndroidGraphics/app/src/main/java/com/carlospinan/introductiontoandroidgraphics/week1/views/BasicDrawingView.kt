package com.carlospinan.introductiontoandroidgraphics.week1.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/**
 * @author Carlos Piñan
 */
class BasicDrawingView(
    context: Context
) : View(context) {

    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 5F
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        canvas.drawRect(
            10f,
            30F,
            200F,
            200F,
            redPaint
        )
    }

}