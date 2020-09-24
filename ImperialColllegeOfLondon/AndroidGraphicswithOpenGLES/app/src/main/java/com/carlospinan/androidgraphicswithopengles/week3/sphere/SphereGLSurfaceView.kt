package com.carlospinan.androidgraphicswithopengles.week3.sphere

import android.content.Context
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLRenderer
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLSurfaceView
import java.util.*

/**
 * @author Carlos Piñan
 */
class SphereGLSurfaceView(
    context: Context
) : BaseGLSurfaceView(context) {

    override fun setupRenderer(): BaseGLRenderer {
        return SphereGLRenderer(context)
    }

    override fun additionalSetup() {
        renderMode = RENDERMODE_WHEN_DIRTY

        val sphereRenderer = renderer as SphereGLRenderer

        val timer = Timer()
        val task = object : TimerTask() {
            var angleX = 0F
            var lightX = 0F
            var lightY = 0F
            var lightZ = 0F
            var direction = true

            override fun run() {
                sphereRenderer.angleX = angleX

                angleX += 1F
                angleX %= 360F

                if (direction) {
                    lightX += 0.1F
                    lightY += 0.1F
                    if (lightX >= 10F)
                        direction = false
                } else {
                    lightX -= 0.1F
                    lightY -= 0.1F
                    if (lightX <= -10F)
                        direction = true
                }

                sphereRenderer.updateLightLocation(lightX, lightY, lightZ)

                requestRender() // Ask the render to update

            }

        }
        timer.scheduleAtFixedRate(task, 0L, 10L)
    }

}