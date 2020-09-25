package com.carlospinan.opengles.common

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author Carlos Piñan
 */
const val FLOAT_SIZE_BYTES = 4

class Mesh(
    coordsPerVertex: Int,
    private val meshVerticesDataPosOffset: Int,
    private val meshVerticesUVOffset: Int,
    private val meshVerticesNormalOffset: Int,
    vertices: FloatArray,
    drawOrder: ShortArray
) {

    private val meshVerticesDataStrideBytes = FLOAT_SIZE_BYTES * coordsPerVertex
    private val meshHasUV = meshVerticesUVOffset >= 0
    private val meshHasNormals = meshVerticesNormalOffset >= 0

    private val vertexBuffer = ByteBuffer
        .allocateDirect(vertices.size * FLOAT_SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer()
                .apply {
                    if (vertices.isNotEmpty()) {
                        put(vertices)
                        position(0)
                        vertexCount = vertices.size / coordsPerVertex
                    }
                }
        }
    private var vertexCount = 0

    // Initialize DrawList Buffer
    private val drawListBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private fun setUpMeshArray(positionHandle: Int, textureHandle: Int, normalHandle: Int) {
        //glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset)
        //glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr)

        // Set up stream to position variable in shader
        vertexBuffer.position(meshVerticesDataPosOffset)
        GLES32.glVertexAttribPointer(
            positionHandle,
            3,
            GLES32.GL_FLOAT,
            false,
            meshVerticesDataStrideBytes,
            vertexBuffer
        )
        GLES32.glEnableVertexAttribArray(positionHandle)

        if (meshHasUV) {
            // Set up Vertex Texture Data stream to shader
            vertexBuffer.position(meshVerticesUVOffset)
            GLES32.glVertexAttribPointer(
                textureHandle,
                2,
                GLES32.GL_FLOAT,
                false,
                meshVerticesDataStrideBytes,
                vertexBuffer
            )
            GLES32.glEnableVertexAttribArray(textureHandle)
        }

        if (meshHasNormals) {
            // Set up Vertex Texture Data stream to shader
            vertexBuffer.position(meshVerticesNormalOffset)
            GLES32.glVertexAttribPointer(
                normalHandle,
                3,
                GLES32.GL_FLOAT,
                false,
                meshVerticesDataStrideBytes,
                vertexBuffer
            )
            GLES32.glEnableVertexAttribArray(normalHandle)
        }
    }

    fun drawMesh(positionHandle: Int, textureHandle: Int, normalHandle: Int) {
        setUpMeshArray(positionHandle, textureHandle, normalHandle)

        //glDrawElements (int mode, int count, int type, int offset)
        //glDrawElements (int mode, int count, int type, Buffer indices)
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            drawListBuffer.capacity(),
            GLES32.GL_UNSIGNED_SHORT,
            drawListBuffer
        )

        // Disable vertex array
        GLES32.glDisableVertexAttribArray(positionHandle)
        GLCommon.checkGLError("glDisableVertexAttribArray ERROR - PosHandle")

        if (meshHasUV) {
            GLES32.glDisableVertexAttribArray(textureHandle)
            GLCommon.checkGLError("glDisableVertexAttribArray ERROR - TexHandle")
        }

        if (meshHasNormals) {
            GLES32.glDisableVertexAttribArray(normalHandle)
            GLCommon.checkGLError("glDisableVertexAttribArray ERROR - NormalHandle")
        }
    }

}