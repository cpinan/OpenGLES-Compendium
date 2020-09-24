package com.carlospinan.a3dgraphicsinandroidsensorsandvr.week3.binocular

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.letters.LetterA
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.letters.LetterS
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.views.BaseGLRenderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author Carlos Piñan
 */
class BinocularGLRenderer(
    private val context: Context
) : BaseGLRenderer(context) {

    private val modelViewProjectionMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    var angleX = 0F
    var angleY = 0F
    var angleZ = 0F

    var mZoom = 1F

    var yaw = 0F
    var pitch = 0F
    var roll = 0F

    private var viewPortWidth = 0
    private var viewPortHeight = 0

    private lateinit var leftView: StereoView
    private lateinit var rightView: StereoView

    private lateinit var letterA: LetterA
    private lateinit var letterS: LetterS

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        letterA = LetterA(context)
        letterS = LetterS(context)
        mZoom = 1F
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)

        //val aspectRatio = width.toFloat() / height.toFloat()

        /*
        Matrix.frustumM(
            projectionMatrix,
            0,
            -aspectRatio,
            aspectRatio,
            -1F,
            1F,
            1F,
            8F
        )
         */

        if (width > height) {
            val ratio = width.toFloat() / height
            Matrix.orthoM(
                projectionMatrix, 0, -ratio, ratio, -1F, 1F, -10F, 200F
            )
        } else {
            val ratio = height.toFloat() / width
            Matrix.orthoM(
                projectionMatrix, 0, -1F, 1F, -ratio, ratio, -10F, 200F
            )
        }

        viewPortWidth = width
        viewPortHeight = height

        leftView = StereoView(width, height, true, context)
        rightView = StereoView(width, height, false, context)
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

        val rotationMatrixY = FloatArray(16)
        val rotationMatrixX = FloatArray(16)

        // Initialize
        Matrix.setIdentityM(modelViewProjectionMatrix, 0)
        Matrix.setIdentityM(modelViewMatrix, 0)
        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.setRotateM(
            rotationMatrixY,
            0,
            pitch,
            0F,
            1F,
            0F
        )

        Matrix.setRotateM(
            rotationMatrixX,
            0,
            yaw,
            1F,
            0F,
            0F
        )

        // Set the camera position (View matrix)
        Matrix.setLookAtM(
            viewMatrix,
            0,
            // Camera is at (0,0,1)
            0F,
            0F,
            1F,
            // Looks at the origin
            0F,
            0F,
            0F,
            // Head is down (set to (0,1,0) to look from the top)
            0F,
            1F,
            0F
        )

        Matrix.translateM(
            modelMatrix,
            0,
            0F,
            0F,
            -5F + mZoom
        )

        Log.d("mZoom", "-XF + $mZoom")

        Matrix.multiplyMM(
            modelMatrix,
            0,
            modelMatrix,
            0,
            rotationMatrixY,
            0
        )

        Matrix.multiplyMM(
            modelMatrix,
            0,
            modelMatrix,
            0,
            rotationMatrixX,
            0
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

        // Draw the frame buffer
        GLES32.glViewport(0, 0, viewPortWidth, viewPortHeight)
        Matrix.setIdentityM(modelMatrix, 0)

        // Draw the Left view
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, leftView.frameBuffer[0])
        GLES32.glViewport(0, 0, leftView.width, leftView.height)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        val leftRotationMatrix = leftView.getModelMatrix(yaw, pitch, roll)

        Matrix.multiplyMM(
            modelViewMatrix,
            0,
            leftView.frameViewMatrix,
            0,
            leftRotationMatrix,
            0
        )

        Matrix.multiplyMM(
            modelViewProjectionMatrix,
            0,
            leftView.projectionMatrix,
            0,
            modelViewMatrix,
            0
        )

        letterA.draw(modelViewProjectionMatrix)
        letterS.draw(modelViewProjectionMatrix)

        // Render onto the screen
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)

        // Draw the Right view
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, rightView.frameBuffer[0])
        GLES32.glViewport(0, 0, rightView.width, rightView.height)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        val rightRotationMatrix = rightView.getModelMatrix(yaw, pitch, roll)

        Matrix.multiplyMM(
            modelViewMatrix,
            0,
            rightView.frameViewMatrix,
            0,
            rightRotationMatrix,
            0
        )

        Matrix.multiplyMM(
            modelViewProjectionMatrix,
            0,
            rightView.projectionMatrix,
            0,
            modelViewMatrix,
            0
        )

        letterA.draw(modelViewProjectionMatrix)
        letterS.draw(modelViewProjectionMatrix)

        // Render onto the screen
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)

        // Draw the framebuffer
        GLES32.glViewport(0, 0, viewPortWidth, viewPortHeight)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        leftView.render(mZoom)
        rightView.render(mZoom)
    }

}