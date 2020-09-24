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
    // Front
    -2F, 2F, 1F,//0
    -1F, 2F, 1F,//1
    0F, 0F, 1F,//2
    1F, 2F, 1F,//3
    2F, 2F, 1F,//4
    0F, -2F, 1F,//5
    // Back
    -2F, 2F, -1F,//6
    -1F, 2F, -1F,//7
    0F, 0F, -1F,//8
    1F, 2F, -1F,//9
    2F, 2F, -1F,//10
    0F, -2F, -1F//11
)

private val indices = intArrayOf(
    // Front
    0, 1, 5, 1, 2, 5,
    2, 3, 5, 3, 4, 5,
    // Back
    6, 7, 11, 7, 8, 11,
    8, 9, 11, 9, 10, 11,
    // Top
    0, 1, 6, 1, 6, 7,
    3, 4, 9, 4, 9, 10,
    // Left
    1, 7, 2, 7, 2, 8,
    4, 5, 10, 10, 11, 5,
    // Right
    0, 6, 5, 6, 5, 11,
    3, 9, 2, 9, 8, 2
)

private val colors = floatArrayOf(
    0.0F, 0.0F, 1.0F, 1.0F,//0
    0.0F, 0.0F, 1.0F, 1.0F,//1
    0.0F, 0.0F, 1.0F, 1.0F,//2
    0.0F, 0.0F, 1.0F, 1.0F,//3
    0.0F, 0.0F, 1.0F, 1.0F,//4
    0.0F, 0.0F, 1.0F, 1.0F,//5
    0.0F, 1.0F, 1.0F, 1.0F,//6
    0.0F, 1.0F, 1.0F, 1.0F,//7
    0.0F, 1.0F, 1.0F, 1.0F,//8
    0.0F, 1.0F, 1.0F, 1.0F,//9
    0.0F, 1.0F, 1.0F, 1.0F,//10
    0.0F, 1.0F, 1.0F, 1.0F//11
)

class LetterV(
    context: Context
) : Primitive(context, R.raw.color_vertex_shader, R.raw.common_fragment_shader) {

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