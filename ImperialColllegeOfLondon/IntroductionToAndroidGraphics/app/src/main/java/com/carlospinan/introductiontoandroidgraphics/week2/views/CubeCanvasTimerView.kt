package com.carlospinan.introductiontoandroidgraphics.week2.views

import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.carlospinan.introductiontoandroidgraphics.common.*
import com.carlospinan.introductiontoandroidgraphics.common.objects.Cube
import java.util.*

/**
 * @author Carlos Piñan
 */
class CubeCanvasTimerView(context: Context) : View(context) {

    private val cube = Cube()

    init {
        var angle = 45F
        val timer = Timer()

        val timerTask = object : TimerTask() {
            override fun run() {

                with(cube) {

                    val matrix = cubeVertices
                        .translate(Vector3(2, 2, 2))
                        .scale(Vector3(40, 40, 40))
                        .rotate3DX(angle)
                        .rotate3DY(90F)
                        .rotate3DZ(25F)
                        .translate(Vector3(200, 200, 0))

                    angle += 10
                    angle %= 360

                    cube.updateMatrix(matrix)

                    postInvalidate()
                }

            }
        }

        timer.scheduleAtFixedRate(timerTask, 100L, 100L)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        cube.draw(canvas)
    }

}