package com.carlospinan.androidgraphicswithopengles.common.objects.letters

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.androidgraphicswithopengles.R
import com.carlospinan.androidgraphicswithopengles.common.objects.FLOAT_SIZE
import com.carlospinan.androidgraphicswithopengles.common.objects.Primitive
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.pow

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

private val controlPointsP = floatArrayOf(
    2F, 0F, 3.2F, 0F, 4F, 0.8F, 2.8F, 1.3F, 2F, 1.5F, 2F, 2F, 3.2F, 2F
)

private val controlPointsQ = floatArrayOf(
    2F, 0.2F, 2.2F, 0.2F, 3.6F, 0.4F, 2.8F, 1F, 1.5F, 1.5F, 1.6F, 2.2F, 3.2F, 2.2F
)

class LetterS(
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

    private var indexCount = 0
    private lateinit var orderBuffer: IntBuffer

    override fun setUp() {
        val vertex = FloatArray(65535)
        val colors = FloatArray(65535)
        val indices = IntArray(65535)

        // START TO CREATE THE CURVE

        var centroidX = 0F
        var centroidY = 0F
        val z = 0.2F

        val noSegments = (controlPointsP.size / 2) / 3

        for (i in controlPointsP.indices step 2) {
            centroidX += controlPointsP[i]
            centroidY += controlPointsP[i + 1]
        }
        centroidX /= (controlPointsP.size / 2)
        centroidY /= (controlPointsP.size / 2)

        var vertexP = 0
        var colorIndex = 0
        var p = 0

        for (segments in 0 until noSegments) {
            // Bezier Curve
            var t = 0F
            while (t < 1F) {
                val x = (1.0 - t).pow(3.0).toFloat() * controlPointsP[p] +
                        controlPointsP[p + 2] * 3 * t * (1.0 - t).pow(2.0).toFloat() +
                        controlPointsP[p + 4] * 3 * t * t * (1 - t) +
                        controlPointsP[p + 6] * t.toDouble().pow(3.0).toFloat()

                val y = (1.0 - t).pow(3.0).toFloat() * controlPointsP[p + 1] +
                        controlPointsP[p + 3] * 3 * t * (1.0 - t).pow(2.0).toFloat() +
                        controlPointsP[p + 5] * 3 * t * t * (1 - t) +
                        controlPointsP[p + 7] * t.toDouble().pow(3.0).toFloat()

                vertex[vertexP++] = x - centroidX
                vertex[vertexP++] = y - centroidY
                vertex[vertexP++] = z

                colors[colorIndex++] = 1F
                colors[colorIndex++] = 1F
                colors[colorIndex++] = 0F
                colors[colorIndex++] = 1F

                t += 0.1F
            }
            p += 6
        }

        var q = 0
        var vertexQ = vertexP

        for (segments in 0 until noSegments) {
            // Bezier Curve
            var t = 0F
            while (t < 1F) {
                val x = (1.0 - t).pow(3.0).toFloat() * controlPointsQ[q] +
                        controlPointsQ[q + 2] * 3 * t * (1.0 - t).pow(2.0).toFloat() +
                        controlPointsQ[q + 4] * 3 * t * t * (1 - t) +
                        controlPointsQ[q + 6] * t.toDouble().pow(3.0).toFloat()

                val y = (1.0 - t).pow(3.0).toFloat() * controlPointsQ[q + 1] +
                        controlPointsQ[q + 3] * 3 * t * (1.0 - t).pow(2.0).toFloat() +
                        controlPointsQ[q + 5] * 3 * t * t * (1 - t) +
                        controlPointsQ[q + 7] * t.toDouble().pow(3.0).toFloat()

                vertex[vertexQ++] = x - centroidX
                vertex[vertexQ++] = y - centroidY
                vertex[vertexQ++] = z

                colors[colorIndex++] = 1F
                colors[colorIndex++] = 1F
                colors[colorIndex++] = 0F
                colors[colorIndex++] = 1F

                t += 0.1F
            }
            q += 6
        }

        // Front
        var index = 0

        var v0 = 0
        var v1 = 1
        var v2 = vertexP / 3
        var v3 = vertexP / 3 + 1
        var noVertices = vertexQ

        while (v3 < noVertices / 3) {
            indices[index++] = v0
            indices[index++] = v1
            indices[index++] = v2

            indices[index++] = v1
            indices[index++] = v2
            indices[index++] = v3

            v0++
            v1++
            v2++
            v3++
        }

        var k = noVertices
        var kIndex = 0
        while (kIndex < noVertices) {
            vertex[k++] = vertex[kIndex++] - 0.2F
            vertex[k++] = vertex[kIndex++] + 0.2F
            vertex[k++] = vertex[kIndex++] - 0.2F

            colors[colorIndex++] = 1F
            colors[colorIndex++] = 0F
            colors[colorIndex++] = 0F
            colors[colorIndex++] = 1F
        }

        // Back
        noVertices = k
        v0 = vertexQ / 3
        v1 = vertexQ / 3 + 1
        v2 = (vertexQ + vertexP) / 3
        v3 = (vertexQ + vertexP) / 3 + 1

        while (v3 < noVertices / 3) {
            indices[index++] = v0
            indices[index++] = v1
            indices[index++] = v2

            indices[index++] = v1
            indices[index++] = v2
            indices[index++] = v3

            v0++
            v1++
            v2++
            v3++
        }

        // Bottom
        v0 = 0
        v1 = 1
        v2 = vertexQ / 3
        v3 = vertexQ / 3 + 1

        while (v3 < (vertexP + vertexQ) / 3) {
            indices[index++] = v0
            indices[index++] = v1
            indices[index++] = v2

            indices[index++] = v1
            indices[index++] = v2
            indices[index++] = v3

            v0++
            v1++
            v2++
            v3++
        }

        // Top
        v0 = vertexP / 3
        v1 = vertexP / 3 + 1
        v2 = (vertexQ + vertexP) / 3
        v3 = (vertexQ + vertexP) / 3 + 1

        while (v3 < noVertices / 3) {
            indices[index++] = v0
            indices[index++] = v1
            indices[index++] = v2

            indices[index++] = v1
            indices[index++] = v2
            indices[index++] = v3

            v0++
            v1++
            v2++
            v3++
        }
        // END CURVE CREATION

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
            indexCount = indices.size
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
                indexCount,
                GLES32.GL_UNSIGNED_INT,
                orderBuffer
            )

            GLES32.glDisableVertexAttribArray(attributePositionHandle)
            GLES32.glDisableVertexAttribArray(attributeColorHandle)

            GLES32.glUseProgram(0)
        }
    }

}