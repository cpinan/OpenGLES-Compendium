package com.carlospinan.opengles.chapter3

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author Carlos Piñan
 */
class MyGLRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Called when an new surface has been created
        // Create OpenGL resources here
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Called when new GL Surface has been created or changes size
        // Set the OpenglES camera viewport here
    }

    override fun onDrawFrame(gl: GL10?) {
        // Put code to draw 3d objects to screen here
    }

}