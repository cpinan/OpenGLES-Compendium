package com.carlospinan.androidgraphicswithopengles.common.objects

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.androidgraphicswithopengles.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

private const val MAX_NO_VERTICES = 65536

private const val COORDS_PER_VERTEX = 3
private const val COLORS_PER_VERTEX = 4

private const val VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE
private const val COLOR_STRIDE = COLORS_PER_VERTEX * FLOAT_SIZE

private val INNER_COLOR = floatArrayOf(1F, 0F, 0F, 1F)
private val OUTER_COLOR = floatArrayOf(1F, 0F, 1F, 0F)

private const val CIRCLE_LINE_WIDTH = 0.3F
private const val CIRCLE_RADIUS = 1F
private const val CIRCLE_RESOLUTION = 5

/**
 * @author Carlos Piñan
 */
class HalfCone(context: Context) : Primitive(
    context, R.raw.color_vertex_shader, R.raw.common_fragment_shader
) {

    private val aVertexPositionHandle by lazy {
        GLES32.glGetAttribLocation(program, "aVertexPosition")
    }

    private val aVertexColorHandle by lazy {
        GLES32.glGetAttribLocation(program, "aVertexColor")
    }

    private val uMVPMatrixHandle by lazy {
        GLES32.glGetUniformLocation(program, "uMVPMatrix")
    }

    private lateinit var circleVertices: FloatArray
    private lateinit var circleColor: FloatArray

    private var vertexCount = 0
    private lateinit var vertexBuffer: FloatBuffer

    private lateinit var colorBuffer: FloatBuffer

    override fun setUp() {
        initCircleVertices()

        vertexBuffer = ByteBuffer.allocateDirect(circleVertices.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(circleVertices)
                position(0)
                vertexCount = circleVertices.size / COORDS_PER_VERTEX
            }


        colorBuffer = ByteBuffer.allocateDirect(circleColor.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(circleColor)
                position(0)
            }
    }

    private fun initCircleVertices() {
        circleVertices = FloatArray(MAX_NO_VERTICES * COORDS_PER_VERTEX)
        circleColor = FloatArray(MAX_NO_VERTICES * COLORS_PER_VERTEX)

        var circleIndex = 0

        // Hollow circle
        val innerDepth = 0F
        val outerDepth = 1F

        val xOffset = 0F
        val yOffset = 0F

        // First Quadrant
        for (i in 0 until 90 step CIRCLE_RESOLUTION) {
            circleIndex = initCircleQuadrant(
                circleIndex,
                CIRCLE_RADIUS,
                i.toFloat(),
                1F,
                1F,
                CIRCLE_LINE_WIDTH,
                xOffset,
                yOffset,
                innerDepth,
                outerDepth
            )
        }

        // Fourth Quadrant
        for (i in 90 until 0 step CIRCLE_RESOLUTION) {
            circleIndex = initCircleQuadrant(
                circleIndex,
                CIRCLE_RADIUS,
                i.toFloat(),
                -1F,
                1F,
                CIRCLE_LINE_WIDTH,
                xOffset,
                yOffset,
                innerDepth,
                outerDepth
            )
        }

        // Third Quadrant
        for (i in 0 until 90 step CIRCLE_RESOLUTION) {
            circleIndex = initCircleQuadrant(
                circleIndex,
                CIRCLE_RADIUS,
                i.toFloat(),
                -1F,
                -1F,
                CIRCLE_LINE_WIDTH,
                xOffset,
                yOffset,
                innerDepth,
                outerDepth
            )
        }

        // Second Quadrant
        for (i in 90 until 0 step CIRCLE_RESOLUTION) {
            circleIndex = initCircleQuadrant(
                circleIndex,
                CIRCLE_RADIUS,
                i.toFloat(),
                1F,
                -1F,
                CIRCLE_LINE_WIDTH,
                xOffset,
                yOffset,
                innerDepth,
                outerDepth
            )
        }
    }

    private fun initCircleQuadrant(
        vertexIndex: Int,
        circleRadius: Float,
        angleIndex: Float,
        quadrantX: Float,
        quadrantY: Float,
        lineWidth: Float,
        xOffset: Float,
        yOffset: Float,
        innerDepth: Float,
        outerDepth: Float
    ): Int {
        var index = vertexIndex

        // First triangle strip to draw the thick line
        var angle = (angleIndex / 180F) * Math.PI.toFloat()
        var x = circleRadius * cos(angle)
        var y = circleRadius * sin(angle)

        // STEP 1
        var colorCircleIndex = index / 3 * 4
        circleVertices[index++] = x * quadrantX + xOffset
        circleVertices[index++] = y * quadrantY + yOffset
        circleVertices[index++] = outerDepth

        // STEP 2
        colorCircleIndex = setColorBuffer(colorCircleIndex, OUTER_COLOR)
        x = (circleRadius - lineWidth) * cos(angle)
        y = (circleRadius - lineWidth) * sin(angle)

        circleVertices[index++] = x * quadrantX + xOffset
        circleVertices[index++] = y * quadrantY + yOffset
        circleVertices[index++] = innerDepth

        // STEP 3
        colorCircleIndex = setColorBuffer(colorCircleIndex, INNER_COLOR)
        angle = ((angleIndex + CIRCLE_RESOLUTION) / 180F) * Math.PI.toFloat()
        x = circleRadius * cos(angle)
        y = circleRadius * sin(angle)

        circleVertices[index++] = x * quadrantX + xOffset
        circleVertices[index++] = y * quadrantY + yOffset
        circleVertices[index++] = outerDepth

        // STEP 4
        colorCircleIndex = setColorBuffer(colorCircleIndex, OUTER_COLOR)

        // Second triangle to complete the strip
        circleVertices[index++] = x * quadrantX + xOffset
        circleVertices[index++] = y * quadrantY + yOffset
        circleVertices[index++] = outerDepth

        // STEP 5
        colorCircleIndex = setColorBuffer(colorCircleIndex, OUTER_COLOR)
        angle = ((angleIndex + CIRCLE_RESOLUTION) / 180F) * Math.PI.toFloat()
        x = (circleRadius - lineWidth) * cos(angle)
        y = (circleRadius - lineWidth) * sin(angle)

        circleVertices[index++] = x * quadrantX + xOffset
        circleVertices[index++] = y * quadrantY + yOffset
        circleVertices[index++] = innerDepth

        // STEP 6
        colorCircleIndex = setColorBuffer(colorCircleIndex, INNER_COLOR)
        angle = (angleIndex / 180F) * Math.PI.toFloat()
        x = (circleRadius - lineWidth) * cos(angle)
        y = (circleRadius - lineWidth) * sin(angle)

        circleVertices[index++] = x * quadrantX + xOffset
        circleVertices[index++] = y * quadrantY + yOffset
        circleVertices[index++] = innerDepth

        // FINAL
        setColorBuffer(colorCircleIndex, INNER_COLOR)

        return index
    }

    private fun setColorBuffer(currentIndex: Int, colorArray: FloatArray): Int {
        var index = currentIndex

        circleColor[index++] = colorArray[0]
        circleColor[index++] = colorArray[1]
        circleColor[index++] = colorArray[2]
        circleColor[index++] = colorArray[3]

        return index
    }

    override fun draw(modelViewProjectionMatrix: FloatArray) {
        GLES32.glUseProgram(program)

        GLES32.glUniformMatrix4fv(
            uMVPMatrixHandle,
            1,
            false,
            modelViewProjectionMatrix,
            0
        )

        GLES32.glEnableVertexAttribArray(aVertexColorHandle)
        GLES32.glEnableVertexAttribArray(aVertexPositionHandle)

        GLES32.glVertexAttribPointer(
            aVertexColorHandle,
            COLORS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            COLOR_STRIDE,
            colorBuffer
        )

        GLES32.glVertexAttribPointer(
            aVertexPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            vertexBuffer
        )

        GLES32.glDrawArrays(
            GLES32.GL_TRIANGLES,
            0,
            vertexCount
        )

        GLES32.glDisableVertexAttribArray(aVertexColorHandle)
        GLES32.glDisableVertexAttribArray(aVertexPositionHandle)


        GLES32.glUseProgram(0)
    }

}
