package com.carlospinan.a3dgraphicsinandroidsensorsandvr.week2.pyramid

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.R
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.FLOAT_SIZE
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.Primitive
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

private const val COORDS_PER_VERTEX = 3
private const val TEXTURE_PER_VERTEX = 2

private const val A_VERTEX_POSITION = "aVertexPosition"
private const val A_TEXTURE_COORDINATE = "aTextureCoordinate"
private const val U_MVP_MATRIX = "uMVPMatrix"
private const val U_TEXTURE_SAMPLER = "uTextureSampler"

private const val VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE
private const val TEXTURE_STRIDE = TEXTURE_PER_VERTEX * FLOAT_SIZE

private val vertex = floatArrayOf(
    //front face
    0.0f, 1.0f, 0.0f,
    -1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, 1.0f,
    //right face
    0.0f, 1.0f, 0.0f,
    1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, -1.0f,
    //back face
    0.0f, 1.0f, 0.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f, -1.0f,
    //left face
    0.0f, 1.0f, 0.0f,
    -1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f, 1.0f
)

private val textureCoordinate = floatArrayOf(
    //front face
    0.5f, 0f,
    0f, 1f,
    1f, 1f,
    //right face
    0.5f, 0f,
    0f, 1f,
    1f, 1f,
    //back face
    0.5f, 0f,
    0f, 1f,
    1f, 1f,
    //left face
    0.5f, 0f,
    1f, 1f,
    0f, 1f
)

/**
 * @author Carlos Piñan
 */
class PyramidWithTexture(context: Context) :
    Primitive(context, R.raw.pyramid_vertex_shader, R.raw.pyramid_fragment_shader) {

    private val attributePositionHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_POSITION)
    }

    private val attributeTextureCoordinateHandle by lazy {
        GLES32.glGetAttribLocation(program, A_TEXTURE_COORDINATE)
    }

    private val uniformMvpMatrixHandle by lazy {
        GLES32.glGetUniformLocation(program, U_MVP_MATRIX)
    }

    private val uniformTextureSamplerHandle by lazy {
        GLES32.glGetUniformLocation(program, U_TEXTURE_SAMPLER)
    }

    private var vertexCount = 0
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    private var textureId = 0

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

        textureBuffer = ByteBuffer.allocateDirect(textureCoordinate.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(textureCoordinate)
                position(0)
            }

        textureId = loadTexture(R.drawable.angkor)
    }

    override fun draw(modelViewProjectionMatrix: FloatArray) {
        with(program) {
            GLES32.glUseProgram(this)

            GLES32.glEnableVertexAttribArray(attributePositionHandle)
            GLES32.glEnableVertexAttribArray(attributeTextureCoordinateHandle)

            GLES32.glUniformMatrix4fv(
                uniformMvpMatrixHandle,
                1,
                false,
                modelViewProjectionMatrix,
                0
            )

            // ENABLE TEXTURE
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureId)
            GLES32.glUniform1i(uniformTextureSamplerHandle, 0)

            GLES32.glVertexAttribPointer(
                attributePositionHandle,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                vertexBuffer
            )

            GLES32.glVertexAttribPointer(
                attributeTextureCoordinateHandle,
                TEXTURE_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                TEXTURE_STRIDE,
                textureBuffer
            )

            GLES32.glDrawArrays(
                GLES32.GL_TRIANGLES,
                0,
                vertexCount
            )

            GLES32.glDisableVertexAttribArray(attributePositionHandle)
            GLES32.glDisableVertexAttribArray(attributeTextureCoordinateHandle)

            GLES32.glUseProgram(0)
        }
    }

}