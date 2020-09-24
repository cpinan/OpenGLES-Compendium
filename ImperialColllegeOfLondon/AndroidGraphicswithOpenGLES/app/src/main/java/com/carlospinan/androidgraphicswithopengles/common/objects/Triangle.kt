package com.carlospinan.androidgraphicswithopengles.common.objects

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.androidgraphicswithopengles.common.checkGLError
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author Carlos Piñan
 */
val triangleVertex = floatArrayOf(
    -1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, 1.0f,
    0.0f, 1.0f, 1.0f
)

private const val COORDS_PER_VERTEX = 3
private const val VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE

private const val A_VERTEX_POSITION = "aVertexPosition"
private const val U_MVP_MATRIX = "uMVPMatrix"

class Triangle(
    context: Context,
    vertexShaderResourceId: Int,
    fragmentShaderResourceId: Int
) : Primitive(context, vertexShaderResourceId, fragmentShaderResourceId) {

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(triangleVertex.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(triangleVertex)
                position(0)
            }

    private val vertexCount = triangleVertex.size / COORDS_PER_VERTEX

    private var positionHandle = 0
    private var mvpMatrixHandle = 0

    override fun setUp() {
        checkGLError("Triangle.kt setup had a problem.")
    }

    override fun draw(mvpMatrix: FloatArray) {
        with(program) {
            GLES32.glUseProgram(this)
            checkGLError("glUserProgram in Triangle.kt")

            positionHandle = GLES32.glGetAttribLocation(this, A_VERTEX_POSITION)
            checkGLError("positionHandle = $positionHandle in Triangle.kt")

            // Enable a handle to the triangle vertices
            GLES32.glEnableVertexAttribArray(positionHandle)
            checkGLError("glEnableVertexAttribArray in Triangle.kt")

            mvpMatrixHandle = GLES32.glGetUniformLocation(this, U_MVP_MATRIX)
            checkGLError("mvpMatrixHandle = $mvpMatrixHandle in Triangle.kt")

            GLES32.glUniformMatrix4fv(
                mvpMatrixHandle,
                1,
                false,
                mvpMatrix,
                0
            )
            checkGLError("glUniformMatrix4fv in Triangle.kt")

            GLES32.glVertexAttribPointer(
                positionHandle,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                vertexBuffer
            )
            checkGLError("glVertexAttribPointer in Triangle.kt")

            GLES32.glDrawArrays(
                GLES32.GL_TRIANGLES,
                0,
                vertexCount
            )
            checkGLError("glDrawArrays in Triangle.kt")

            GLES32.glDisableVertexAttribArray(positionHandle)
            checkGLError("glDisableVertexAttribArray in Triangle.kt")

            GLES32.glUseProgram(0)
        }
    }

}