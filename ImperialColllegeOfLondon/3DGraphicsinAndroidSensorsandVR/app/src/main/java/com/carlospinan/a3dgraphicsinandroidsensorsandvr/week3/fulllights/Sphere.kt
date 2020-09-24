package com.carlospinan.a3dgraphicsinandroidsensorsandvr.week3.fulllights

import android.content.Context
import android.opengl.GLES32
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.R
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.checkGLError
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.FLOAT_SIZE
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.Primitive
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author Carlos Piñan
 */
private const val COORDS_PER_VERTEX = 3
private const val COLORS_PER_VERTEX = 4
private const val TEXTURE_PER_VERTEX = 2

private const val A_VERTEX_POSITION = "aVertexPosition"
private const val A_VERTEX_COLOR = "aVertexColor"
private const val U_MVP_MATRIX = "uMVPMatrix"
private const val U_POINT_LIGHTING_LOCATION = "uPointLightingLocation"
private const val A_VERTEX_NORMAL = "aVertexNormal"
private const val A_TEXTURE_COORDINATE = "aTextureCoordinate"
private const val U_DIFFUSE_COLOR = "uDiffuseColor"
private const val U_POINT_LIGHTING_COLOR = "uPointLightingColor"
private const val U_AMBIENT_COLOR = "uAmbientColor"
private const val U_DIFFUSE_LIGHT_LOCATION = "uDiffuseLightLocation"
private const val U_ATTENUATION = "uAttenuation"
private const val U_SPECULAR_COLOR = "uSpecularColor"
private const val U_SPECULAR_LIGHT_LOCATION = "uSpecularLightLocation"
private const val U_MATERIAL_SHININESS = "uMaterialShininess"
private const val U_TEXTURE_SAMPLER = "uTextureSampler"

private const val VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE
private const val COLOR_STRIDE = COLORS_PER_VERTEX * FLOAT_SIZE
private const val TEXTURE_STRIDE = TEXTURE_PER_VERTEX * FLOAT_SIZE

class Sphere(context: Context) : Primitive(
    context, R.raw.full_light_vertex_shader, R.raw.full_light_fragment_shader
) {

    private lateinit var vertex: FloatArray
    private lateinit var colors: FloatArray
    private lateinit var indices: IntArray
    private lateinit var normal: FloatArray
    private lateinit var textureCoordData: FloatArray

    private val uniformDiffuseColorHandle by lazy {
        GLES32.glGetUniformLocation(program, U_DIFFUSE_COLOR)
    }

    private val attributeTextureCoordHandle by lazy {
        GLES32.glGetAttribLocation(program, A_TEXTURE_COORDINATE)
    }

    private val attributeNormalHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_NORMAL)
    }

    private val attributePositionHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_POSITION)
    }

    private val attributeColorHandle by lazy {
        GLES32.glGetAttribLocation(program, A_VERTEX_COLOR)
    }

    private val uniformMvpMatrixHandle by lazy {
        GLES32.glGetUniformLocation(program, U_MVP_MATRIX)
    }

    private val uniformPointLightingLocationHandle by lazy {
        GLES32.glGetUniformLocation(program, U_POINT_LIGHTING_LOCATION)
    }

    private val uniformPointLightingColorHandle by lazy {
        GLES32.glGetUniformLocation(program, U_POINT_LIGHTING_COLOR)
    }

    private val uniformAmbientColorHandle by lazy {
        GLES32.glGetUniformLocation(program, U_AMBIENT_COLOR)
    }

    private val uniformDiffuseLightLocationHandle by lazy {
        GLES32.glGetUniformLocation(program, U_DIFFUSE_LIGHT_LOCATION)
    }

    private val uniformAttenuationHandle by lazy {
        GLES32.glGetUniformLocation(program, U_ATTENUATION)
    }

    private val uniformSpecularColorHandle by lazy {
        GLES32.glGetUniformLocation(program, U_SPECULAR_COLOR)
    }

    private val uniformSpecularLightLocationHandle by lazy {
        GLES32.glGetUniformLocation(program, U_SPECULAR_LIGHT_LOCATION)
    }

    private val uniformMaterialShininessHandle by lazy {
        GLES32.glGetUniformLocation(program, U_MATERIAL_SHININESS)
    }

    private val uniformTextureSamplerHandle by lazy {
        GLES32.glGetUniformLocation(program, U_TEXTURE_SAMPLER)
    }

    private var vertexCount = 0
    private lateinit var vertexBuffer: FloatBuffer

    private var colorCount = 0
    private lateinit var colorBuffer: FloatBuffer

    private lateinit var orderBuffer: IntBuffer

    private var normalCount = 0
    private lateinit var normalBuffer: FloatBuffer

    private lateinit var textureBuffer: FloatBuffer

    private lateinit var lightLocation: FloatArray
    private lateinit var diffuseLightLocation: FloatArray
    private lateinit var lightAttenuation: FloatArray
    private lateinit var diffuseLightColor: FloatArray
    private lateinit var specularHighlightColor: FloatArray
    private lateinit var specularLightLocation: FloatArray

    private var materialShininess = 10F
    private var textureIdHandle = 0

    init {
        setUp()
    }

    override fun setUp() {
        createSphere(2F, 50, 50)

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

        normalBuffer = ByteBuffer.allocateDirect(normal.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(normal)
                position(0)
                normalCount = normal.size / COLORS_PER_VERTEX
            }

        textureBuffer = ByteBuffer.allocateDirect(textureCoordData.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(textureCoordData)
                position(0)
            }

        // INIT
        lightLocation = FloatArray(3)
        diffuseLightLocation = FloatArray(3)
        lightAttenuation = FloatArray(3)
        diffuseLightColor = FloatArray(4)
        specularHighlightColor = FloatArray(4)
        specularLightLocation = FloatArray(3)

        lightLocation[0] = 10F
        lightLocation[1] = 10F
        lightLocation[2] = 10F

        diffuseLightLocation[0] = 2f
        diffuseLightLocation[1] = 0.2f
        diffuseLightLocation[2] = 2f

        specularHighlightColor[0] = 1F
        specularHighlightColor[1] = 1F
        specularHighlightColor[2] = 1F
        specularHighlightColor[3] = 1F

        specularLightLocation[0] = -7F
        specularLightLocation[1] = -4F
        specularLightLocation[2] = 2F

        diffuseLightColor[0] = 1F
        diffuseLightColor[1] = 1F
        diffuseLightColor[2] = 1F
        diffuseLightColor[3] = 1F

        lightAttenuation[0] = 1F
        lightAttenuation[1] = 0.14F
        lightAttenuation[2] = 0.07F

        textureIdHandle = loadTexture(R.drawable.world)
    }

    private fun createSphere(radius: Float, noLatitude: Int, noLongitude: Int) {
        val vertex = FloatArray(65535)
        val colors = FloatArray(65535)
        val indices = IntArray(65535)
        val normal = FloatArray(65535)
        val textureCoordData = FloatArray(65535)

        // CREATE SPHERE
        var vertexIndex = 0
        var colorIndex = 0
        var normalIndex = 0
        var textureIndex = 0
        var index = 0
        val distance = 0F

        for (row in 0..noLatitude) {
            val theta = (row * Math.PI / noLatitude).toFloat()
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)

            for (col in 0..noLongitude) {
                val phi = (col * (2 * Math.PI) / noLongitude).toFloat()

                val sinPhi = sin(phi)
                val cosPhi = cos(phi)

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                normal[normalIndex++] = x
                normal[normalIndex++] = y
                normal[normalIndex++] = z

                vertex[vertexIndex++] = radius * x
                vertex[vertexIndex++] = radius * y + distance
                vertex[vertexIndex++] = radius * z

                colors[colorIndex++] = 1F
                colors[colorIndex++] = 0F
                colors[colorIndex++] = 0F
                colors[colorIndex++] = 1F

                val u = col.toFloat() / noLongitude
                val v = row.toFloat() / noLatitude

                textureCoordData[textureIndex++] = u
                textureCoordData[textureIndex++] = v
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

        this.vertex = vertex.copyOf(vertexIndex)
        this.colors = colors.copyOf(colorIndex)
        this.indices = indices.copyOf(index)
        this.normal = normal.copyOf(normalIndex)
        this.textureCoordData = textureCoordData.copyOf(textureIndex)
    }

    override fun draw(modelViewProjectionMatrix: FloatArray) {
        with(program) {
            GLES32.glUseProgram(this)

            GLES32.glEnableVertexAttribArray(attributePositionHandle)
            checkGLError("glEnableVertexAttribArray - attributePositionHandle")

            GLES32.glEnableVertexAttribArray(attributeColorHandle)
            checkGLError("glEnableVertexAttribArray - attributeColorHandle")

            GLES32.glEnableVertexAttribArray(attributeNormalHandle)
            checkGLError("glEnableVertexAttribArray - attributeNormalHandle")

            GLES32.glEnableVertexAttribArray(attributeTextureCoordHandle)
            checkGLError("glEnableVertexAttribArray - attributeTextureCoordHandle")

            GLES32.glUniformMatrix4fv(
                uniformMvpMatrixHandle,
                1,
                false,
                modelViewProjectionMatrix,
                0
            )
            checkGLError("glUniformMatrix4fv - uniformMvpMatrixHandle")

            GLES32.glUniform3fv(
                uniformPointLightingLocationHandle,
                1,
                lightLocation,
                0
            )
            checkGLError("glUniform3fv - uniformPointLightingLocationHandle")

            GLES32.glUniform3fv(
                uniformDiffuseLightLocationHandle,
                1,
                diffuseLightLocation,
                0
            )
            checkGLError("glUniform3fv - uniformDiffuseLightLocationHandle")

            GLES32.glUniform4fv(
                uniformDiffuseColorHandle,
                1,
                diffuseLightColor,
                0
            )
            checkGLError("glUniform4fv - uniformDiffuseColorHandle")

            GLES32.glUniform3fv(
                uniformAttenuationHandle,
                1,
                lightAttenuation,
                0
            )
            checkGLError("glUniform3fv - uniformAttenuationHandle")

            GLES32.glUniform3f(
                uniformPointLightingColorHandle,
                0.3f,
                0.3f,
                0.3f
            )
            checkGLError("glUniform3f - uniformPointLightingColorHandle")

            GLES32.glUniform3f(
                uniformAmbientColorHandle,
                0.6f,
                0.6f,
                0.6f
            )
            checkGLError("glUniform3f - uniformAmbientColorHandle")

            GLES32.glUniform4fv(
                uniformSpecularColorHandle,
                1,
                specularHighlightColor,
                0
            )
            checkGLError("glUniform4fv - uniformSpecularColorHandle")

            GLES32.glUniform1f(
                uniformMaterialShininessHandle,
                materialShininess
            )
            checkGLError("glUniform1f - uniformMaterialShininessHandle")

            GLES32.glUniform3fv(
                uniformSpecularLightLocationHandle,
                1,
                specularLightLocation,
                0
            )
            checkGLError("glUniform3fv - uniformSpecularLightLocationHandle")

            // TEXTURE
            // Set the active texture to unit 0
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            checkGLError("glActiveTexture - GL_TEXTURE0")

            // Bind the texture to this unit
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureIdHandle)
            checkGLError("glBindTexture - $textureIdHandle")

            // Tell the uniform sampler to use this texture i
            GLES32.glUniform1i(uniformTextureSamplerHandle, 0)
            checkGLError("glUniform1i - $uniformTextureSamplerHandle")

            // VERTEX SET
            GLES32.glVertexAttribPointer(
                attributeTextureCoordHandle,
                TEXTURE_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                TEXTURE_STRIDE,
                textureBuffer
            )
            checkGLError("glVertexAttribPointer - attributeTextureCoordHandle")

            GLES32.glVertexAttribPointer(
                attributePositionHandle,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                vertexBuffer
            )
            checkGLError("glVertexAttribPointer - attributePositionHandle")

            GLES32.glVertexAttribPointer(
                attributeNormalHandle,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                normalBuffer
            )
            checkGLError("glVertexAttribPointer - attributeNormalHandle")

            GLES32.glVertexAttribPointer(
                attributeColorHandle,
                COLORS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                COLOR_STRIDE,
                colorBuffer
            )
            checkGLError("glVertexAttribPointer - attributeColorHandle")

            GLES32.glDrawElements(
                GLES32.GL_TRIANGLE_FAN,
                indices.size,
                GLES32.GL_UNSIGNED_INT,
                orderBuffer
            )
            checkGLError("glDrawElements - GL_TRIANGLE_FAN")

            GLES32.glDisableVertexAttribArray(attributePositionHandle)
            checkGLError("glDisableVertexAttribArray - attributePositionHandle")

            GLES32.glDisableVertexAttribArray(attributeColorHandle)
            checkGLError("glDisableVertexAttribArray - attributeColorHandle")

            GLES32.glDisableVertexAttribArray(attributeNormalHandle)
            checkGLError("glDisableVertexAttribArray - attributeNormalHandle")

            GLES32.glDisableVertexAttribArray(attributeTextureCoordHandle)
            checkGLError("glDisableVertexAttribArray - attributeTextureCoordHandle")

            GLES32.glUseProgram(0)

            checkGLError("Print Error")
        }
    }

}