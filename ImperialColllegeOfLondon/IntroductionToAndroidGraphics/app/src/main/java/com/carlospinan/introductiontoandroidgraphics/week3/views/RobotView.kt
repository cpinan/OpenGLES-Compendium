package com.carlospinan.introductiontoandroidgraphics.week3.views

import android.content.Context
import android.graphics.Canvas
import android.view.View
import java.util.*

/**
 * @author Carlos Piñan
 */

private const val MIN_ANGLE = -45F
private const val MAX_ANGLE = 45F

class RobotView(context: Context) : View(context) {

    private val robot = Robot()

    init {
        var angle = MIN_ANGLE
        var rotateDirection = 1

        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                if (angle == MAX_ANGLE && rotateDirection == 1) {
                    rotateDirection = -1
                } else if (angle == MIN_ANGLE && rotateDirection == -1) {
                    rotateDirection = 1
                }

                angle += 1F * rotateDirection

                robot.rotate(0F, angle, 0F)
                postInvalidate()
            }

        }
        timer.scheduleAtFixedRate(timerTask, 100L, 100L)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        robot.draw(canvas)
    }

}