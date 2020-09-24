package com.carlospinan.openglesexamples.common.views

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author Carlos Piñan
 */
abstract class BaseGLRenderer(
    protected val context: Context
) : GLSurfaceView.Renderer {

    abstract override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?)

    abstract override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int)

    abstract override fun onDrawFrame(gl: GL10?)

}