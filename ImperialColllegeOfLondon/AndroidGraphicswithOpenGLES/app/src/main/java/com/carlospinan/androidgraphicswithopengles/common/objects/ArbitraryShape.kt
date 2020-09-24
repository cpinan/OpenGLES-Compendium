@file:Suppress("UnnecessaryVariable")

package com.carlospinan.androidgraphicswithopengles.common.objects

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.androidgraphicswithopengles.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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

class ArbitraryShape(context: Context) : Primitive(
    context, R.raw.color_vertex_shader, R.raw.common_fragment_shader
) {

    private lateinit var sphereAVertex: FloatArray
    private lateinit var sphereAColor: FloatArray
    private lateinit var sphereAIndex: IntArray

    private lateinit var sphereBVertex: FloatArray
    private lateinit var sphereBColor: FloatArray
    private lateinit var sphereBIndex: IntArray

    private lateinit var ringVertex: FloatArray
    private lateinit var ringIndex: IntArray
    private lateinit var ringColor: FloatArray

    private val attributePositionHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_POSITION)
    }

    private val attributeColorHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_COLOR)
    }

    private val uniformMvpMatrix by lazy {
        GLES32.glGetUniformLocation(program, U_MVP_MATRIX)
    }

    private lateinit var sphereAVertexBuffer: FloatBuffer
    private lateinit var sphereAColorBuffer: FloatBuffer
    private lateinit var sphereAIndexBuffer: IntBuffer

    private lateinit var sphereBVertexBuffer: FloatBuffer
    private lateinit var sphereBColorBuffer: FloatBuffer
    private lateinit var sphereBIndexBuffer: IntBuffer

    private lateinit var ringVertexBuffer: FloatBuffer
    private lateinit var ringColorBuffer: FloatBuffer
    private lateinit var ringIndexBuffer: IntBuffer

    private var sphereAIndexCount = 0
    private var sphereBIndexCount = 0
    private var ringIndexCount = 0

    override fun setUp() {
        createSphere(2F, 30, 30)

        // SPHERE A
        sphereAVertexBuffer = ByteBuffer.allocateDirect(sphereAVertex.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereAVertex)
                position(0)
            }

        sphereAColorBuffer = ByteBuffer.allocateDirect(sphereAColor.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereAColor)
                position(0)
            }

        sphereAIndexBuffer = IntBuffer.allocate(sphereAIndex.size)
            .apply {
                put(sphereAIndex)
                position(0)
            }

        // SPHERE B
        sphereBVertexBuffer = ByteBuffer.allocateDirect(sphereBVertex.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereBVertex)
                position(0)
            }

        sphereBColorBuffer = ByteBuffer.allocateDirect(sphereBColor.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(sphereBColor)
                position(0)
            }

        sphereBIndexBuffer = IntBuffer.allocate(sphereBIndex.size)
            .apply {
                put(sphereBIndex)
                position(0)
            }

        // RING
        ringVertexBuffer = ByteBuffer.allocateDirect(ringVertex.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(ringVertex)
                position(0)
            }

        ringColorBuffer = ByteBuffer.allocateDirect(ringColor.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(ringColor)
                position(0)
            }

        ringIndexBuffer = IntBuffer.allocate(ringIndex.size)
            .apply {
                put(ringIndex)
                position(0)
            }
    }

    private fun createSphere(radius: Float, noLatitude: Int, noLongitude: Int) {
        // DYNAMIC SIZE
        val sphereAVertex = FloatArray(65535)
        val sphereAColor = FloatArray(65535)
        val sphereAIndex = IntArray(65535)

        val sphereBVertex = FloatArray(65535)
        val sphereBColor = FloatArray(65535)
        val sphereBIndex = IntArray(65535)

        val ringVertex = FloatArray(65535)
        val ringColor = FloatArray(65535)
        val ringIndex = IntArray(65535)

        var sphereAVertexIndex = 0
        var sphereAColorIndex = 0
        var sphereAIdx = 0

        var sphereBVertexIndex = 0
        var sphereBColorIndex = 0
        var sphereBIdx = 0

        var ringVertexIndex = 0
        var ringColorIndex = 0
        var ringIdx = 0

        val distance = 3F

        val colorIncreaseFactor = 1F / (noLatitude + 1)
        var pLength = (noLongitude + 1) * 3 * 3
        var pColorLength = (noLongitude + 1) * 4 * 3

        for (row in 0..noLatitude) {
            val theta = (row * Math.PI / noLatitude).toFloat()
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)
            var color = -0.5F

            for (col in 0..noLongitude) {
                val phi = (col * (2 * Math.PI) / noLongitude).toFloat()
                val sinPhi = sin(phi)
                val cosPhi = cos(phi)

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                sphereAVertex[sphereAVertexIndex++] = radius * x
                sphereAVertex[sphereAVertexIndex++] = radius * y + distance
                sphereAVertex[sphereAVertexIndex++] = radius * z

                sphereBVertex[sphereBVertexIndex++] = radius * x
                sphereBVertex[sphereBVertexIndex++] = radius * y - distance
                sphereBVertex[sphereBVertexIndex++] = radius * z

                sphereAColor[sphereAColorIndex++] = 1F
                sphereAColor[sphereAColorIndex++] = abs(color)
                sphereAColor[sphereAColorIndex++] = 1F
                sphereAColor[sphereAColorIndex++] = 1F

                sphereBColor[sphereBColorIndex++] = 1F
                sphereBColor[sphereBColorIndex++] = 1F
                sphereBColor[sphereBColorIndex++] = abs(color)
                sphereBColor[sphereBColorIndex++] = 1F

                if (row == 20) {
                    ringVertex[ringVertexIndex++] = (radius * x)
                    ringVertex[ringVertexIndex++] = (radius * y) + distance
                    ringVertex[ringVertexIndex++] = (radius * z)

                    ringColor[ringColorIndex++] = 1F
                    ringColor[ringColorIndex++] = abs(color)
                    ringColor[ringColorIndex++] = 0F
                    ringColor[ringColorIndex++] = 1F
                }

                if (row == 15) {
                    ringVertex[ringVertexIndex++] = (radius * x) / 2F
                    ringVertex[ringVertexIndex++] = (radius * y) / 2F + distance * 0.2F
                    ringVertex[ringVertexIndex++] = (radius * z) / 2F

                    ringColor[ringColorIndex++] = 1F
                    ringColor[ringColorIndex++] = abs(color)
                    ringColor[ringColorIndex++] = 0F
                    ringColor[ringColorIndex++] = 1F
                }

                if (row == 10) {
                    ringVertex[ringVertexIndex++] = (radius * x) / 2F
                    ringVertex[ringVertexIndex++] = (radius * y) / 2F - distance * 0.1F
                    ringVertex[ringVertexIndex++] = (radius * z) / 2F

                    ringColor[ringColorIndex++] = 0F
                    ringColor[ringColorIndex++] = 1F
                    ringColor[ringColorIndex++] = abs(color)
                    ringColor[ringColorIndex++] = 1F
                }

                if (row == 20) {
                    ringVertex[pLength++] = (radius * x)
                    ringVertex[pLength++] = (-radius * y) - distance
                    ringVertex[pLength++] = (radius * z)

                    ringColor[pColorLength++] = 0F
                    ringColor[pColorLength++] = 1F
                    ringColor[pColorLength++] = abs(color)
                    ringColor[pColorLength++] = 1F
                }

                color += colorIncreaseFactor
            }
        }

        // Index Buffer
        for (row in 0 until noLatitude) {

            for (col in 0 until noLongitude) {
                val p0 = row * (noLongitude + 1) + col
                val p1 = p0 + noLongitude + 1

                sphereAIndex[sphereAIdx++] = p0
                sphereAIndex[sphereAIdx++] = p1
                sphereAIndex[sphereAIdx++] = p0 + 1
                sphereAIndex[sphereAIdx++] = p1
                sphereAIndex[sphereAIdx++] = p1
                sphereAIndex[sphereAIdx++] = p1 + 1
                sphereAIndex[sphereAIdx++] = p0 + 1

                sphereBIndex[sphereBIdx++] = p0
                sphereBIndex[sphereBIdx++] = p1
                sphereBIndex[sphereBIdx++] = p0 + 1
                sphereBIndex[sphereBIdx++] = p1
                sphereBIndex[sphereBIdx++] = p1
                sphereBIndex[sphereBIdx++] = p1 + 1
                sphereBIndex[sphereBIdx++] = p0 + 1
            }

        }

        pLength = (noLongitude + 1)

        for (j in 0 until (pLength - 1)) {

            ringIndex[ringIdx++] = j
            ringIndex[ringIdx++] = j + pLength
            ringIndex[ringIdx++] = j + 1
            ringIndex[ringIdx++] = j + pLength + 1
            ringIndex[ringIdx++] = j + 1
            ringIndex[ringIdx++] = j + pLength

            ringIndex[ringIdx++] = j + pLength
            ringIndex[ringIdx++] = j + pLength * 2
            ringIndex[ringIdx++] = j + pLength + 1
            ringIndex[ringIdx++] = j + pLength * 2 + 1
            ringIndex[ringIdx++] = j + pLength + 1
            ringIndex[ringIdx++] = j + pLength * 2

            ringIndex[ringIdx++] = j + pLength * 3
            ringIndex[ringIdx++] = j
            ringIndex[ringIdx++] = j + 1
            ringIndex[ringIdx++] = j + 1
            ringIndex[ringIdx++] = j + pLength * 3 + 1
            ringIndex[ringIdx++] = j + pLength * 3

        }

        sphereAIndexCount = sphereAIdx
        sphereBIndexCount = sphereBIdx
        ringIndexCount = ringIdx

        this.sphereAVertex = sphereAVertex.copyOf(sphereAVertexIndex)
        this.sphereAColor = sphereAColor.copyOf(sphereAColorIndex)
        this.sphereAIndex = sphereAIndex.copyOf(sphereAIndexCount)

        this.sphereBVertex = sphereBVertex.copyOf(sphereAVertexIndex)
        this.sphereBColor = sphereBColor.copyOf(sphereAColorIndex)
        this.sphereBIndex = sphereBIndex.copyOf(sphereAIndexCount)

        this.ringVertex = ringVertex.copyOf(sphereAVertexIndex)
        this.ringColor = ringColor.copyOf(sphereAColorIndex)
        this.ringIndex = ringIndex.copyOf(sphereAIndexCount)

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

            // GLES32.GL_LINES
            // GLES32.GL_TRIANGLES

            // SPHERE A
            GLES32.glVertexAttribPointer(
                attributePositionHandle,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                sphereAVertexBuffer
            )

            GLES32.glVertexAttribPointer(
                attributeColorHandle,
                COLORS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                COLOR_STRIDE,
                sphereAColorBuffer
            )

            GLES32.glDrawElements(
                GLES32.GL_TRIANGLE_FAN,
                sphereAIndexCount,
                GLES32.GL_UNSIGNED_INT,
                sphereAIndexBuffer
            )

            // SPHERE B
            GLES32.glVertexAttribPointer(
                attributePositionHandle,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                sphereBVertexBuffer
            )

            GLES32.glVertexAttribPointer(
                attributeColorHandle,
                COLORS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                COLOR_STRIDE,
                sphereBColorBuffer
            )

            GLES32.glDrawElements(
                GLES32.GL_TRIANGLE_FAN,
                sphereBIndexCount,
                GLES32.GL_UNSIGNED_INT,
                sphereBIndexBuffer
            )

            // RING
            GLES32.glVertexAttribPointer(
                attributePositionHandle,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                ringVertexBuffer
            )

            GLES32.glVertexAttribPointer(
                attributeColorHandle,
                COLORS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                COLOR_STRIDE,
                ringColorBuffer
            )

            GLES32.glDrawElements(
                GLES32.GL_TRIANGLES,
                ringIndexCount,
                GLES32.GL_UNSIGNED_INT,
                ringIndexBuffer
            )

            GLES32.glDisableVertexAttribArray(attributePositionHandle)
            GLES32.glDisableVertexAttribArray(attributeColorHandle)

            GLES32.glUseProgram(0)
        }
    }

}