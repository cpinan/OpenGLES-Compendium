package com.carlospinan.androidgraphicswithopengles.common.views

import android.content.Context
import android.opengl.GLSurfaceView
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLRenderer

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
        additionalSetup()
    }

    abstract fun setupRenderer(): BaseGLRenderer

    protected open fun additionalSetup() {
        // renderMode = RENDERMODE_WHEN_DIRTY
    }

}