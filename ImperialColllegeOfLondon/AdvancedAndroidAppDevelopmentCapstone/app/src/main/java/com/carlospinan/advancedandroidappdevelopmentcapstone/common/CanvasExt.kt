package com.carlospinan.advancedandroidappdevelopmentcapstone.common

import android.graphics.Canvas
import android.graphics.Paint

/**
 * @author Carlos Piñan
 */
fun Canvas.drawVectors(v1: Vector3, v2: Vector3, paint: Paint) {
    drawLine(
        v1.x,
        v1.y,
        v2.x,
        v2.y,
        paint
    )
}