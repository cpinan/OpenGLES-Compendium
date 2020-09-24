package com.carlospinan.a3dgraphicsinandroidsensorsandvr.week2.pyramid

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.views.BaseGLRenderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author Carlos Piñan
 */
class TouchPyramidGLRenderer(
    private val context: Context
) : BaseGLRenderer(context) {

    private lateinit var pyramidWithTexture: PyramidWithTexture

    private val modelViewProjectionMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    var angleX = 0F
    var angleY = 0F

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES32.glCullFace(GLES32.GL_BACK) // Cull the back faces
        // GLES32.glCullFace(GLES32.GL_FRONT) // Cull the front faces
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        pyramidWithTexture = PyramidWithTexture(context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)

        val aspectRatio = width.toFloat() / height.toFloat()

        Matrix.frustumM(
            projectionMatrix,
            0,
            -aspectRatio,
            aspectRatio,
            -1F,
            1F,
            1F,
            10F
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClearColor(0F, 0F, 0F, 1F)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // Set up the depth buffer
        GLES32.glClearDepthf(1F)

        // Enable depth test (so, it will not look through the surfaces)
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)

        // Indicate what type of depth test
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // Initialize
        Matrix.setIdentityM(modelViewProjectionMatrix, 0)
        Matrix.setIdentityM(modelViewMatrix, 0)
        Matrix.setIdentityM(modelMatrix, 0)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(
            viewMatrix,
            0,
            0F,
            0F,
            1F,
            0F,
            0F,
            0F,
            0F,
            1F,
            0F
        )

        Matrix.translateM(
            modelMatrix,
            0,
            0F,
            0F,
            -5F
        )

        Matrix.multiplyMM(
            modelViewMatrix,
            0,
            viewMatrix,
            0,
            modelMatrix,
            0
        )

        Matrix.multiplyMM(
            modelViewProjectionMatrix,
            0,
            projectionMatrix,
            0,
            modelViewMatrix,
            0
        )

        val rotationXMatrix = FloatArray(16)
        val rotationYMatrix = FloatArray(16)

        // ORDER Y - X - Z
        Matrix.setRotateM(
            rotationYMatrix,
            0,
            angleY,
            0F, 1F, 0F
        )

        Matrix.setRotateM(
            rotationXMatrix,
            0,
            angleX,
            1F, 0F, 0F
        )

        Matrix.multiplyMM(
            modelViewProjectionMatrix,
            0,
            modelViewProjectionMatrix,
            0,
            rotationYMatrix,
            0
        )

        Matrix.multiplyMM(
            modelViewProjectionMatrix,
            0,
            modelViewProjectionMatrix,
            0,
            rotationXMatrix,
            0
        )

        pyramidWithTexture.draw(modelViewProjectionMatrix)
    }

}