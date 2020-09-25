package com.carlospinan.opengles.androiddoc.objects

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author Carlos Piñan
 */
// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3

private var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.622008459f, 0.0f,      // top
    -0.5f, -0.311004243f, 0.0f,    // bottom left
    0.5f, -0.311004243f, 0.0f      // bottom right
)

class Triangle {

    /*
    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

     */
    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private var program: Int

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        val vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        program = GLES32.glCreateProgram().also {

            // add the vertex shader to program
            GLES32.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES32.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES32.glLinkProgram(it)
        }
    }

    // Set color with red, green, blue and alpha (opacity) values
    private val color = floatArrayOf(1f, 1f, 1f, 1.0f)

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    fun draw(viewProjectionMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        GLES32.glUseProgram(program)

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES32.glGetUniformLocation(program, "uMVPMatrix")
        GLES32.glUniformMatrix4fv(
            vPMatrixHandle,
            1,
            false,
            viewProjectionMatrix,
            0
        )

        // get handle to vertex shader's vPosition member
        positionHandle = GLES32.glGetAttribLocation(program, "vPosition")

        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(positionHandle)

        // Prepare the triangle coordinate data
        GLES32.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        // get handle to fragment shader's vColor member
        mColorHandle = GLES32.glGetUniformLocation(program, "vColor")
        GLES32.glUniform4fv(mColorHandle, 1, color, 0)

        // Draw the triangle
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)

        // Disable vertex array
        GLES32.glDisableVertexAttribArray(positionHandle)

        GLES32.glUseProgram(0)
    }

    fun draw() {
        // Add program to OpenGL ES environment
        GLES32.glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES32.glGetAttribLocation(program, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES32.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES32.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES32.glGetUniformLocation(program, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES32.glUniform4fv(colorHandle, 1, color, 0)
            }

            // Draw the triangle
            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES32.glDisableVertexAttribArray(it)
        }
    }
}