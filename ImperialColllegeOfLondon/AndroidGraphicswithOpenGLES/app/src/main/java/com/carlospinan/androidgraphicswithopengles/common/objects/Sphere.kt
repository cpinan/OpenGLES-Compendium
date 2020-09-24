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
private const val U_POINT_LIGHTING = "uPointLightingLocation"

private const val VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE
private const val COLOR_STRIDE = COLORS_PER_VERTEX * FLOAT_SIZE

class Sphere(context: Context) : Primitive(
    context, R.raw.sphere_vertex_shader, R.raw.sphere_fragment_shader
) {

    private lateinit var vertex: FloatArray
    private lateinit var colors: FloatArray
    private lateinit var indices: IntArray

    private val attributePositionHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_POSITION)
    }

    private val attributeColorHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_COLOR)
    }

    private val uniformMvpMatrix by lazy {
        GLES32.glGetUniformLocation(program, U_MVP_MATRIX)
    }

    private val pointLightingHandle by lazy {
        GLES32.glGetUniformLocation(program, U_POINT_LIGHTING)
    }

    private var vertexCount = 0
    private lateinit var vertexBuffer: FloatBuffer

    private var colorCount = 0
    private lateinit var colorBuffer: FloatBuffer

    private lateinit var orderBuffer: IntBuffer

    private val lightLocation = floatArrayOf(2F, 2F, 0F)

    override fun setUp() {
        createSphere(2F, 30, 30)

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

    private fun createSphere(radius: Float, noLatitude: Int, noLongitude: Int) {
        vertex = FloatArray(65535)
        colors = FloatArray(65535)
        indices = IntArray(65535)

        // CREATE SPHERE
        var vertexIndex = 0
        var colorIndex = 0
        var index = 0
        val distance = 0F
        val colorIncreaseFactor = 1F / (noLatitude + 1)

        for (row in 0..noLatitude) {
            val theta = (row * Math.PI / noLatitude).toFloat()
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)
            var color = 0.5F

            for (col in 0..noLongitude) {
                val phi = (col * (2 * Math.PI) / noLongitude).toFloat()
                val sinPhi = sin(phi)
                val cosPhi = cos(phi)

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                vertex[vertexIndex++] = radius * x
                vertex[vertexIndex++] = radius * y + distance
                vertex[vertexIndex++] = radius * z

                colors[colorIndex++] = 1F
                colors[colorIndex++] = abs(color)
                colors[colorIndex++] = 1F
                colors[colorIndex++] = 1F

                color += colorIncreaseFactor
            }
        }

        // Index Buffer
        for (row in 0 until noLatitude) {

            for (col in 0 until noLongitude) {
                val p0 = row * (noLongitude + 1) + col
                val p1 = p0 + noLongitude + 1

                indices[index++] = p0
                indices[index++] = p1
                indices[index++] = p0 + 1
                indices[index++] = p1
                indices[index++] = p1
                indices[index++] = p1 + 1
                indices[index++] = p0 + 1
            }

        }
        // END SPHERE
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

            GLES32.glUniform3fv(
                pointLightingHandle,
                1,
                lightLocation,
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
                GLES32.GL_TRIANGLE_FAN,
                indices.size,
                GLES32.GL_UNSIGNED_INT,
                orderBuffer
            )

            GLES32.glDisableVertexAttribArray(attributePositionHandle)
            GLES32.glDisableVertexAttribArray(attributeColorHandle)

            GLES32.glUseProgram(0)
        }
    }

    fun updateLight(lightSphere: FloatArray) {
        lightLocation[0] = lightSphere[0]
        lightLocation[1] = lightSphere[1]
        lightLocation[2] = lightSphere[2]
    }

}