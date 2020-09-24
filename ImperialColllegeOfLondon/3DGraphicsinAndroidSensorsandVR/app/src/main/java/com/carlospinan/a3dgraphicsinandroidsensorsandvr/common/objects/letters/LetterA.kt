package com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.letters

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.R
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.FLOAT_SIZE
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.Primitive
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * @author Carlos Piñan
 */

private const val COORDS_PER_VERTEX = 3
private const val COLORS_PER_VERTEX = 4

private const val A_VERTEX_POSITION = "aVertexPosition"
private const val A_VERTEX_COLOR = "aVertexColor"
private const val U_MVP_MATRIX = "uMVPMatrix"

private const val VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE
private const val COLOR_STRIDE = COLORS_PER_VERTEX * FLOAT_SIZE

private val vertex = floatArrayOf(
    -0.2f, 1f, -0.3f,//0
    -0.2f, 1f, 0.3f,//1
    0.2f, 1f, -0.3f,//2
    0.2f, 1f, 0.3f,//3
    -1f, -1f, -0.5f,//4
    -1f, -1f, 0.5f,//5
    -0.6f, -1f, -0.5f,//6
    -0.6f, -1f, 0.5f,//7
    0.6f, -1f, 0.5f,//8
    0.6f, -1f, -0.5f,//9
    1f, -1f, 0.5f,//10
    1f, -1f, -0.5f,//11
    0f, 0.8f, 0.3f,//12
    0f, 0.8f, -0.3f,//13
    0.25f, 0.1f, 0.382f,//14
    0.25f, 0.1f, -0.382f,//15
    -0.25f, 0.1f, 0.382f,//16
    -0.25f, 0.1f, -0.382f,//17
    0.32f, -0.1f, 0.41f,//18
    0.32f, -0.1f, -0.41f,//19
    -0.32f, -0.1f, 0.41f,//20
    -0.32f, -0.1f, -0.41f//21
)

private val indices = intArrayOf(
    1, 0, 2, 1, 3, 2,//top
    4, 0, 5, 5, 1, 0,//left
    4, 5, 6, 6, 7, 5,//left bottom
    1, 5, 7, 7, 3, 1,//left front
    4, 0, 6, 2, 6, 0,//left back
    3, 10, 11, 11, 3, 2,//right
    8, 9, 10, 10, 11, 9,//right bottom
    10, 3, 8, 8, 3, 1,//right front
    2, 11, 9, 2, 9, 0,//right back
    6, 12, 13, 7, 6, 12,//left inner
    9, 8, 12, 9, 13, 12,//right inner
    14, 15, 16, 15, 17, 16,//inner top
    19, 18, 20, 20, 21, 19,//inner bottom
    18, 14, 20, 16, 20, 14,//inner front
    15, 19, 21, 21, 17, 15//inner back
)

private val colors = floatArrayOf(
    0.0f, 0.0f, 1.0f, 1.0f,//0
    0.0f, 0.0f, 1.0f, 1.0f,//1
    0.0f, 0.0f, 1.0f, 1.0f,//2
    0.0f, 0.0f, 1.0f, 1.0f,//3
    0.0f, 1.0f, 0.0f, 1.0f,//4
    0.0f, 1.0f, 0.0f, 1.0f,//5
    0.0f, 1.0f, 0.0f, 1.0f,//6
    0.0f, 1.0f, 0.0f, 1.0f,//7
    0.0f, 1.0f, 0.0f, 1.0f,//8
    0.0f, 1.0f, 0.0f, 1.0f,//9
    0.0f, 1.0f, 0.0f, 1.0f,//10
    0.0f, 1.0f, 0.0f, 1.0f,//11
    0.0f, 0.0f, 1.0f, 1.0f,//12
    0.0f, 0.0f, 1.0f, 1.0f,//13
    0.0f, 0.0f, 1.0f, 1.0f,//14
    0.0f, 0.0f, 1.0f, 1.0f,//15
    0.0f, 0.0f, 1.0f, 1.0f,//16
    0.0f, 0.0f, 1.0f, 1.0f,//17
    0.0f, 1.0f, 0.0f, 1.0f,//18
    0.0f, 1.0f, 0.0f, 1.0f,//19
    0.0f, 1.0f, 0.0f, 1.0f,//20
    0.0f, 1.0f, 0.0f, 1.0f,//21
)

class LetterA(
    context: Context
) : Primitive(context, R.raw.color_vertex_shader, R.raw.letter_fragment_shader) {

    private val attributePositionHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_POSITION)
    }

    private val attributeColorHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_COLOR)
    }

    private val uniformMvpMatrix by lazy {
        GLES32.glGetUniformLocation(program, U_MVP_MATRIX)
    }

    private var vertexCount = 0
    private lateinit var vertexBuffer: FloatBuffer

    private var colorCount = 0
    private lateinit var colorBuffer: FloatBuffer

    private lateinit var orderBuffer: IntBuffer

    init {
        setUp()
    }

    override fun setUp() {
        vertexBuffer = ByteBuffer.allocateDirect(vertex.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertex)
                position(0)
                vertexCount = vertex.size / COORDS_PER_VERTEX
            }

        colorBuffer = ByteBuffer.allocateDirect(colors.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(colors)
                position(0)
                colorCount = colors.size / COLORS_PER_VERTEX
            }

        orderBuffer = IntBuffer.allocate(indices.size).apply {
            put(indices)
            position(0)
        }
    }

    override fun draw(modelViewProjectionMatrix: FloatArray) {
        with(program) {
            GLES32.glUseProgram(this)

            GLES32.glEnableVertexAttribArray(attributePositionHandle)
            GLES32.glEnableVertexAttribArray(attributeColorHandle)

            GLES32.glUniformMatrix4fv(
                uniformMvpMatrix,
                1,
                false,
                modelViewProjectionMatrix,
                0
            )

            GLES32.glVertexAttribPointer(
                attributePositionHandle,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                vertexBuffer
            )

            GLES32.glVertexAttribPointer(
                attributeColorHandle,
                COLORS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                COLOR_STRIDE,
                colorBuffer
            )

            GLES32.glDrawElements(
                GLES32.GL_TRIANGLES,
                indices.size,
                GLES32.GL_UNSIGNED_INT,
                orderBuffer
            )

            GLES32.glDisableVertexAttribArray(attributePositionHandle)
            GLES32.glDisableVertexAttribArray(attributeColorHandle)

            GLES32.glUseProgram(0)
        }
    }

}