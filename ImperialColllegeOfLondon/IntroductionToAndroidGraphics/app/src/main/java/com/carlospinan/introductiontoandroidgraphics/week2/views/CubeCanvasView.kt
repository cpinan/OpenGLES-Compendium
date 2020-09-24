package com.carlospinan.introductiontoandroidgraphics.week2.views

import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.carlospinan.introductiontoandroidgraphics.common.*
import com.carlospinan.introductiontoandroidgraphics.common.objects.Cube

/**
 * @author Carlos Piñan
 */
class CubeCanvasView(context: Context) : View(context) {

    private val cube = Cube().apply {
        val matrix = cubeVertices
            .translate(Vector3(2, 2, 1))
            .scale(Vector3(40, 40, 40))
            .rotate3DY(45F)
            .rotate3DX(45F)

        updateMatrix(matrix)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        cube.draw(canvas)
    }

}