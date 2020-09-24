package com.carlospinan.introductiontoandroidgraphics.week2.views

import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.carlospinan.introductiontoandroidgraphics.common.Vector3
import com.carlospinan.introductiontoandroidgraphics.common.objects.Cube
import com.carlospinan.introductiontoandroidgraphics.common.scale
import com.carlospinan.introductiontoandroidgraphics.common.translate
import java.util.*

/**
 * @author Carlos Piñan
 */
class CubeCanvasTimer3View(context: Context) : View(context) {

    private val cube = Cube()

    init {

        val timer = Timer()

        var speed = 10F
        var positionX = 0F
        var goRight = true

        val timerTask = object : TimerTask() {
            override fun run() {
                if (positionX + 80 >= width && goRight) {
                    goRight = false
                } else if (positionX <= 0 && !goRight) {
                    goRight = true
                }

                with(cube) {
                    if (goRight) {
                        positionX += speed
                    } else {
                        positionX -= speed
                    }

                    val newTranslateVector = Vector3(positionX, 0F, 0F)

                    val matrix = cubeVertices
                        .translate(Vector3(2, 2, 2))
                        .scale(Vector3(40, 40, 40))
                        .translate(newTranslateVector)

                    updateMatrix(matrix)

                    postInvalidate()
                }

            }
        }

        timer.scheduleAtFixedRate(timerTask, 100L, 50L)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        cube.draw(canvas)
    }

}