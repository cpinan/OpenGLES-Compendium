package com.carlospinan.opengles.androiddoc

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

/**
 * @author Carlos Piñan
 * @ref https://github.com/JimSeker/opengl
 */
private const val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f

class MyGLSurfaceView(
    context: Context
) : GLSurfaceView(context) {

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private var renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(3)
        //fix for error No Config chosen, but I don't know what this does.
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)

        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        // Render the view only when there is a change in the drawing data
        // renderMode = RENDERMODE_WHEN_DIRTY
        // renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx: Float = x - previousX
                var dy: Float = y - previousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }

                renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }
        previousX = x
        previousY = y

        return true
    }

}