package com.carlospinan.opengles.gLHelloWorld

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * @author Carlos Piñan
 * @ref https://github.com/JimSeker/opengl
 */
class MyGLSurfaceView(
    context: Context
) : GLSurfaceView(context) {

    init {
        setEGLContextClientVersion(3)
        //fix for error No Config chosen, but I don't know what this does.
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)

        setRenderer(MyGLRenderer(context))
        // Render the view only when there is a change in the drawing data
        // renderMode = RENDERMODE_WHEN_DIRTY
        renderMode = RENDERMODE_CONTINUOUSLY
    }

}