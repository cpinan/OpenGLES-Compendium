package com.carlospinan.openglesexamples.common.views

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * @author Carlos Piñan
 */
abstract class BaseGLSurfaceView(
    context: Context
) : GLSurfaceView(context) {

    protected var renderer: BaseGLRenderer
        private set

    init {
        setEGLContextClientVersion(3)

        renderer = setupRenderer()
        setRenderer(renderer)
    }

    abstract fun setupRenderer(): BaseGLRenderer

}