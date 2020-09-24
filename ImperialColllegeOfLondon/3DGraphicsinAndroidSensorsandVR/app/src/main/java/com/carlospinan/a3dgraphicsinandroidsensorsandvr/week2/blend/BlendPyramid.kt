package com.carlospinan.a3dgraphicsinandroidsensorsandvr.week2.blend

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.R
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.FLOAT_SIZE
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.Primitive
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

private const val COORDS_PER_VERTEX = 3
private const val COLORS_PER_VERTEX = 4

private const val A_VERTEX_POSITION = "aVertexPosition"
private const val A_VERTEX_COLOR = "aVertexColor"
private const val U_MVP_MATRIX = "uMVPMatrix"

private const val VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE
private const val COLOR_STRIDE = COLORS_PER_VERTEX * FLOAT_SIZE

private val vertex = floatArrayOf(
    //front face
    0.0f, 2.0f, 0.0f,
    -1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, 1.0f,
    //right face
    0.0f, 2.0f, 0.0f,
    1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, -1.0f,
    //back face
    0.0f, 2.0f, 0.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f, -1.0f,
    //left face
    0.0f, 2.0f, 0.0f,
    -1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f, 1.0f
)

private val colors = floatArrayOf(
    //front face
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    //right face
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    //back face
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    //left face
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f
)

/**
 * @author Carlos Piñan
 */
class BlendPyramid(context: Context) :
    Primitive(context, R.raw.color_vertex_shader, R.raw.common_fragment_shader) {

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

            GLES32.glDrawArrays(
                GLES32.GL_TRIANGLES,
                0,
                vertexCount
            )

            GLES32.glDisableVertexAttribArray(attributePositionHandle)
            GLES32.glDisableVertexAttribArray(attributeColorHandle)

            GLES32.glUseProgram(0)
        }
    }

}