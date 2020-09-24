package com.carlospinan.androidgraphicswithopengles.common.objects

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.androidgraphicswithopengles.R
import com.carlospinan.androidgraphicswithopengles.common.checkGLError
import com.carlospinan.androidgraphicswithopengles.common.degreesToRadians
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author Carlos Piñan
 */
class Circle(
    context: Context
) : Primitive(context, R.raw.circle_vertex_shader, R.raw.color_fragment_shader) {

    private var vertexCount = 0
    private lateinit var vertexBuffer: FloatBuffer

    private val positionHandle by lazy {
        GLES32.glGetAttribLocation(program, "aVertexPosition")
    }

    private val colorHandle by lazy {
        GLES32.glGetUniformLocation(program, "uColor")
    }

    private val mvpHandle by lazy {
        GLES32.glGetUniformLocation(program, "uMVPMatrix")
    }

    override fun setUp() {
        val radius = 2F
        val circleVertex = arrayListOf(0F, 0F, 1F)

        val increaseFactor = 1
        for (i in 0..360 step increaseFactor) {
            val radians = degreesToRadians(i.toFloat())
            val x = cos(radians) * radius
            val y = sin(radians) * radius
            circleVertex.addAll(arrayOf(x, y, 1F))
        }

        vertexBuffer = ByteBuffer
            .allocateDirect(circleVertex.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(circleVertex.toFloatArray())
                position(0)
                vertexCount = circleVertex.size / 3
            }
    }

    override fun draw(modelViewProjectionMatrix: FloatArray) {
        with(program) {
            GLES32.glUseProgram(this)

            GLES32.glEnableVertexAttribArray(positionHandle)

            GLES32.glUniformMatrix4fv(
                mvpHandle,
                1,
                false,
                modelViewProjectionMatrix,
                0
            )
            checkGLError("glUniformMatrix4fv in Circle.kt")

            GLES32.glVertexAttribPointer(
                positionHandle,
                3,
                GLES32.GL_FLOAT,
                false,
                3 * FLOAT_SIZE,
                vertexBuffer
            )
            checkGLError("glVertexAttribPointer in Circle.kt")

            GLES32.glUniform4fv(
                colorHandle,
                1,
                floatArrayOf(1F, 0F, 0F, 1F),
                0
            )

            GLES32.glDrawArrays(
                GLES32.GL_TRIANGLE_FAN,
                0,
                vertexCount
            )
            checkGLError("glDrawArrays in Circle.kt")

            GLES32.glUniform4fv(
                colorHandle,
                1,
                floatArrayOf(0F, 1F, 0F, 1F),
                0
            )

            // Draw the circle contour
            // GLES32.glLineWidth(30F)
            // GL_LINE_LOOP would be better
            GLES32.glDrawArrays(
                GLES32.GL_POINTS,
                1,
                vertexCount - 1
            )

            GLES32.glDisableVertexAttribArray(positionHandle)
            checkGLError("glDisableVertexAttribArray in Circle.kt")

            GLES32.glUseProgram(0)
        }
    }

}