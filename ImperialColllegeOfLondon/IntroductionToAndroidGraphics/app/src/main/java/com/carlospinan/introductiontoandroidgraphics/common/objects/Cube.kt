package com.carlospinan.introductiontoandroidgraphics.common.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.carlospinan.introductiontoandroidgraphics.common.Vector3
import com.carlospinan.introductiontoandroidgraphics.common.drawVectors

/**
 * @author Carlos Piñan
 */
class Cube(
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 5F
    }
) {

    val cubeVertices = arrayOf(
        Vector3(-1F, -1F, -1F), // 0 Bottom Left back
        Vector3(-1F, -1F, 1F), // 1 Bottom Left front
        Vector3(-1F, 1F, -1F), // 2 Top Left back
        Vector3(-1F, 1F, 1F), // 3 Top Left front
        Vector3(1F, -1F, -1F), // 4 Bottom Right back
        Vector3(1F, -1F, 1F), // 5 Bottom Right front
        Vector3(1F, 1F, -1F), // 6 Top Right back
        Vector3(1F, 1F, 1F) // 7 Top Right front
    )

    var baseCubeVertices = cubeVertices.clone()
        private set

    private val cubeDrawing = mutableListOf<Vector3>()

    fun draw(canvas: Canvas) {
        if (cubeDrawing.isEmpty() || cubeDrawing.size != cubeVertices.size)
            return

        canvas.drawVectors(
            cubeDrawing[0], cubeDrawing[1], paint
        )

        canvas.drawVectors(
            cubeDrawing[1], cubeDrawing[3], paint
        )

        canvas.drawVectors(
            cubeDrawing[3], cubeDrawing[2], paint
        )

        canvas.drawVectors(
            cubeDrawing[2], cubeDrawing[0], paint
        )

        canvas.drawVectors(
            cubeDrawing[4], cubeDrawing[5], paint
        )

        canvas.drawVectors(
            cubeDrawing[5], cubeDrawing[7], paint
        )

        canvas.drawVectors(
            cubeDrawing[7], cubeDrawing[6], paint
        )

        canvas.drawVectors(
            cubeDrawing[6], cubeDrawing[4], paint
        )

        canvas.drawVectors(
            cubeDrawing[0], cubeDrawing[4], paint
        )

        canvas.drawVectors(
            cubeDrawing[1], cubeDrawing[5], paint
        )

        canvas.drawVectors(
            cubeDrawing[2], cubeDrawing[6], paint
        )

        canvas.drawVectors(
            cubeDrawing[3], cubeDrawing[7], paint
        )
    }

    fun updateMatrix(matrix: Array<Vector3>) {
        cubeDrawing.clear()
        cubeDrawing.addAll(matrix)
    }

    fun setMatrix(matrix: Array<Vector3>) {
        baseCubeVertices = matrix.clone()
        updateMatrix(baseCubeVertices)
    }

}