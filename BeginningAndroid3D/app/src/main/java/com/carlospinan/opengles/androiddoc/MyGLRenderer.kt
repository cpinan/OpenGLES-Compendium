package com.carlospinan.opengles.androiddoc

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import com.carlospinan.opengles.androiddoc.objects.Cube
import com.carlospinan.opengles.androiddoc.objects.Square
import com.carlospinan.opengles.androiddoc.objects.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author Carlos Piñan
 * https://developer.android.com/guide/topics/graphics/opengl#coordinate-mapping
 */
class MyGLRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    private lateinit var triangle: Triangle
    private lateinit var square: Square
    private lateinit var cube: Cube

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)

    private val projectionMatrix = FloatArray(16)

    private val viewMatrix = FloatArray(16)

    private val rotationMatrix = FloatArray(16)

    @Volatile
    var angle = 0F

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Called when an new surface has been created
        // Create OpenGL resources here

        // Set the background frame color
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        triangle = Triangle()
        square = Square()
        cube = Cube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Called when new GL Surface has been created or changes size

        // Set the OpenglES camera viewport here
        GLES32.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(
            projectionMatrix,
            0,
            -ratio,
            ratio,
            -1f,
            1f,
            3f,
            10f
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        // Put code to draw 3d objects to screen here

        // https://stackoverflow.com/questions/5717654/glulookat-explanation
        // Set the camera position (View matrix)
        Matrix.setLookAtM(
            viewMatrix,
            0,
            0F,
            0F,
            -3F,
            0F,
            0F,
            0F,
            0F,
            1F,
            0F
        )

        // Calculate the projection and view transformation
        Matrix.multiplyMM(
            vPMatrix,
            0,
            projectionMatrix,
            0,
            viewMatrix,
            0
        )

        // Redraw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        val translatedMatrix = FloatArray(16)
        Matrix.translateM(
            translatedMatrix,
            0,
            vPMatrix,
            0,
            1f,
            0f,
            0f
        )

        // Create a rotation transformation for the triangle
        //val time = SystemClock.uptimeMillis() % 4000L
        //val angle = 0.090f * time.toInt()
        //Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        // val scratch = FloatArray(16)
        // Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        // Draw the shape
        // triangle.draw(scratch)

        val time = SystemClock.uptimeMillis() % 4000L
        val newAngle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, newAngle, 0f, 1f, -1.0f)

        val scratch = FloatArray(16)
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        cube.draw(scratch)
    }

}