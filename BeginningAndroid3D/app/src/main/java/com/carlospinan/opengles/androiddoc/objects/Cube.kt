package com.carlospinan.opengles.androiddoc.objects

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * @author Carlos Piñan
 */
private val cubeCoords = floatArrayOf(
    -0.5f, 0.5f, 0.5f,
    -0.5f, -0.5f, 0.5f,
    0.5f, -0.5f, 0.5f,
    0.5f, 0.5f, 0.5f,

    -0.5f, 0.5f, -0.5f,
    -0.5f, -0.5f, -0.5f,
    0.5f, -0.5f, -0.5f,
    0.5f, 0.5f, -0.5f
)

class Cube {

    private val drawOrder = shortArrayOf(
        0, 3, 1, 3, 2, 1,    // Front panel
        4, 7, 5, 7, 6, 5,   // Back panel
        4, 0, 5, 0, 1, 5,    // Side
        7, 3, 6, 3, 2, 6,    // Side
        4, 7, 0, 7, 3, 0,    // Top
        5, 6, 1, 6, 2, 1    // Bottom
    )

    private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(cubeCoords.size * 4)
        .run {
            order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply {
                    put(cubeCoords)
                    position(0)
                }
        }

    private val drawListBuffer: ShortBuffer = ByteBuffer.allocateDirect(drawOrder.size * 2)
        .run {
            order(ByteOrder.nativeOrder())
                .asShortBuffer().apply {
                    put(drawOrder)
                    position(0)
                }
        }

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

    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    private val color = floatArrayOf(1f, 1f, 1f, 1.0f)

    init {
        val vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        program = GLES32.glCreateProgram().also {

            // add the vertex shader to program
            GLES32.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES32.glAttachShader(it, fragmentShader)

            /*
                // Bind attributes
                GLES32.glBindAttribLocation(programHandle, 0, "a_Position");
                GLES32.glBindAttribLocation(programHandle, 1, "a_Color");
             */

            // creates OpenGL ES program executables
            GLES32.glLinkProgram(it)

            // Get the link status.
            val linkStatus = IntArray(1)
            GLES32.glGetProgramiv(it, GLES32.GL_LINK_STATUS, linkStatus, 0)

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                GLES32.glDeleteProgram(it)
            }
        }
    }

    fun draw(viewProjectionMatrix: FloatArray) {
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

        // GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            drawListBuffer.capacity(),
            GLES32.GL_UNSIGNED_SHORT,
            drawListBuffer
        )

        // Disable vertex array
        GLES32.glDisableVertexAttribArray(positionHandle)

        GLES32.glUseProgram(0)
    }

}