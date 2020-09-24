package com.carlospinan.a3dgraphicsinandroidsensorsandvr.week1.light

import android.content.Context
import android.view.MotionEvent
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.views.BaseGLRenderer
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.views.BaseGLSurfaceView

/**
 * @author Carlos Piñan
 */

// Scale factor for the touch motions
private const val TOUCH_SCALE_FACTOR = 180.0f / 320

// Zoom factor
// private const val TOUCH_ZOOM_FACTOR = 1.0f / 320

class LightCubeGLSurfaceView(
    context: Context
) : BaseGLSurfaceView(context) {

    private var previousTouchX = 0F
    private var previousTouchY = 0F
    private lateinit var customRenderer: LightCubeGLRenderer

    override fun setupRenderer(): BaseGLRenderer {
        return LightCubeGLRenderer(context)
    }

    override fun additionalSetup() {
        super.additionalSetup()
        renderMode = RENDERMODE_WHEN_DIRTY
        customRenderer = renderer as LightCubeGLRenderer
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                var dX = x - previousTouchX
                var dY = y - previousTouchY

                if (y > height / 2) {
                    dX *= -1
                }
                if (x < width / 2) {
                    dY *= -1
                }

                customRenderer.angleY += dX * TOUCH_SCALE_FACTOR
                customRenderer.angleX += dY * TOUCH_SCALE_FACTOR

                requestRender()

            }
        }

        previousTouchX = x
        previousTouchY = y
        return true
    }

}