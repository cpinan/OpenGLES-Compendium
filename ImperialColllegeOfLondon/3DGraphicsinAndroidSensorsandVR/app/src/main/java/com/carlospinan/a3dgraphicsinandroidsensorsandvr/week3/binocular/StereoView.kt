package com.carlospinan.a3dgraphicsinandroidsensorsandvr.week3.binocular

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.R
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.FLOAT_SIZE
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects.Primitive
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

private const val DEPTH_Z = -5F

// Near clipping plane
private const val NEAR_Z = 1F

// Far clipping plane
private const val FAR_Z = 8F

// Screen projection plane
private const val SCREEN_Z = -10F

private const val INTRAOCULAR_DISTANCE = 0.8F

private const val FRUSTUM_SHIFT = -(INTRAOCULAR_DISTANCE / 2) * NEAR_Z / SCREEN_Z

private const val COORDS_PER_VERTEX = 3
private const val TEXTURE_PER_VERTEX = 2

private const val VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE
private const val TEXTURE_STRIDE = TEXTURE_PER_VERTEX * FLOAT_SIZE

private val plane2DVertex = floatArrayOf(
    // Front face
    -1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    -1.0f, 1.0f, 1.0f
)

private val plane2DIndex = intArrayOf(
    0, 1, 2, 0, 2, 3
)

private val plane2DTextureCoord = floatArrayOf(
    //front face
    0F, 0F,
    1F, 0F,
    1F, 1F,
    0F, 1F
)

/**
 * @author Carlos Piñan
 */
class StereoView(
    private val binocularWidth: Int,
    private val binocularHeight: Int,
    private val isLeft: Boolean,
    context: Context
) :
    Primitive(context, R.raw.binocular_vertex_shader, R.raw.binocular_fragment_shader) {

    private val attributeVertexPositionHandle by lazy {
        GLES32.glGetAttribLocation(program, "aVertexPosition")
    }

    private val attributeTextureCoordinateHandle by lazy {
        GLES32.glGetAttribLocation(program, "aTextureCoordinate")
    }

    private val uniformMVPMatrixHandle by lazy {
        GLES32.glGetUniformLocation(program, "uMVPMatrix")
    }

    private val uniformTextureSamplerHandle by lazy {
        GLES32.glGetUniformLocation(program, "uTextureSampler")
    }

    var width = 0
        private set

    var height = 0
        private set

    lateinit var frameBuffer: IntArray
        private set

    private lateinit var frameBufferTextureID: IntArray
    private lateinit var renderBuffer: IntArray

    // For drawing the object in the framebuffer
    lateinit var frameModelMatrix: FloatArray
        private set

    lateinit var frameViewMatrix: FloatArray
        private set

    lateinit var projectionMatrix: FloatArray
        private set

    // For drawing the framebuffer as a surface on the screen
    private lateinit var mvpMatrix: FloatArray
    private lateinit var modelMatrix: FloatArray
    private lateinit var viewMatrix: FloatArray
    private lateinit var displayProjectionMatrix: FloatArray

    // Utils
    // Screen aspect ratio
    private var aspect = 0F

    private var modelTranslation = 0F

    // Buffers
    private var vertexCount = 0
    private lateinit var vertexBuffer: FloatBuffer

    private lateinit var textureBuffer: FloatBuffer

    private lateinit var orderBuffer: IntBuffer

    init {
        setUp()
    }

    override fun setUp() {
        frameModelMatrix = FloatArray(16)
        frameViewMatrix = FloatArray(16)
        projectionMatrix = FloatArray(16)

        mvpMatrix = FloatArray(16)
        modelMatrix = FloatArray(16)
        viewMatrix = FloatArray(16)
        displayProjectionMatrix = FloatArray(16)

        vertexBuffer = ByteBuffer.allocateDirect(plane2DVertex.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(plane2DVertex)
                position(0)
                vertexCount = plane2DVertex.size / COORDS_PER_VERTEX
            }

        orderBuffer = IntBuffer.allocate(plane2DIndex.size)
            .apply {
                put(plane2DIndex)
                position(0)
            }

        textureBuffer = ByteBuffer.allocateDirect(plane2DTextureCoord.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(plane2DTextureCoord)
                position(0)
            }

        width = binocularWidth / 2
        height = binocularHeight
        aspect = width.toFloat() / height

        if (binocularWidth > binocularHeight) {
            val binocularRatio = binocularWidth.toFloat() / binocularHeight
            Matrix.orthoM(
                displayProjectionMatrix,
                0,
                -binocularRatio,
                binocularRatio,
                -1F,
                1F,
                -10F,
                200F
            )
        } else {
            val binocularRatio = binocularHeight.toFloat() / binocularWidth
            Matrix.orthoM(
                displayProjectionMatrix,
                0,
                -1F,
                1F,
                -binocularRatio,
                binocularRatio,
                -10F,
                200F
            )
        }

        Matrix.setLookAtM(
            viewMatrix,
            0,
            0F,
            0F,
            0.1f,
            // Looks at the origin
            0F,
            0F,
            0F,
            // Head is down (set to (0,1,0) to look from the top)
            0F,
            1F,
            0F
        )

        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.scaleM(
            modelMatrix,
            0,
            width.toFloat() / height,
            1F,
            1F
        )

        if (isLeft) {
            // Move to the left
            Matrix.translateM(modelMatrix, 0, -1F, 0F, 0F)
        } else {
            // Move to the right
            Matrix.translateM(modelMatrix, 0, 1F, 0F, 0F)
        }

        Matrix.multiplyMM(
            mvpMatrix,
            0,
            viewMatrix,
            0,
            modelMatrix,
            0
        )

        Matrix.multiplyMM(
            mvpMatrix,
            0,
            displayProjectionMatrix,
            0,
            mvpMatrix,
            0
        )

        // Setting up the matrices for drawing objects in the framebuffer
        if (isLeft) {
            Matrix.frustumM(
                projectionMatrix,
                0,
                FRUSTUM_SHIFT - aspect,
                FRUSTUM_SHIFT + aspect,
                -1F,
                1F,
                NEAR_Z,
                FAR_Z
            )
            modelTranslation = INTRAOCULAR_DISTANCE * 0.5F

            Matrix.setLookAtM(
                frameViewMatrix,
                0,
                -INTRAOCULAR_DISTANCE / 2F,
                0F,
                0.1F,
                // Looks at the screen
                0F,
                0F,
                SCREEN_Z,
                // Head is down (set to (0,1,0) to look from the top)
                0F,
                1F,
                0F
            )

        } else {
            Matrix.frustumM(
                projectionMatrix,
                0,
                -aspect - FRUSTUM_SHIFT,
                aspect - FRUSTUM_SHIFT,
                -1F,
                1F,
                NEAR_Z,
                FAR_Z
            )
            modelTranslation = -INTRAOCULAR_DISTANCE * 0.5F

            Matrix.setLookAtM(
                frameViewMatrix,
                0,
                INTRAOCULAR_DISTANCE / 2F,
                0F,
                0.1F,
                // Looks at the screen
                0F,
                0F,
                SCREEN_Z,
                // Head is down (set to (0,1,0) to look from the top)
                0F,
                1F,
                0F
            )
        }

        Matrix.setIdentityM(frameModelMatrix, 0)

        Matrix.translateM(
            frameModelMatrix,
            0,
            modelTranslation,
            0F,
            DEPTH_Z
        )

        // Frame buffer
        frameBuffer = IntArray(1)
        frameBufferTextureID = IntArray(2)
        renderBuffer = IntArray(1)

        createFrameBuffers()
    }

    fun getModelMatrix(rotateX: Float, rotateY: Float, rotateZ: Float): FloatArray {
        // Get the model matrix to draw the object onto the frame buffer
        val pModelMatrix = FloatArray(16) // model  matrix

        val mRotationMatrixX = FloatArray(16)

        val mRotationMatrixY = FloatArray(16)

        val mRotationMatrixZ = FloatArray(16)

        Matrix.setIdentityM(pModelMatrix, 0) // Set the model matrix to an identity matrix

        // Rotate around the y-axis
        Matrix.setRotateM(mRotationMatrixY, 0, rotateY, 0f, 1.0f, 0f)

        // Rotate around the x-axis
        Matrix.setRotateM(mRotationMatrixX, 0, rotateX, 1.0f, 0f, 0f)

        // Rotate around the x-axis
        Matrix.setRotateM(mRotationMatrixZ, 0, rotateZ, 0f, 0f, 1f)

        Matrix.multiplyMM(pModelMatrix, 0, frameModelMatrix, 0, mRotationMatrixY, 0)
        Matrix.multiplyMM(pModelMatrix, 0, pModelMatrix, 0, mRotationMatrixX, 0)
        Matrix.multiplyMM(pModelMatrix, 0, pModelMatrix, 0, mRotationMatrixZ, 0)

        return pModelMatrix
    }

    private fun initTexture(
        whichTexture: Int,
        textureID: Int,
        pixelFormat: Int,
        type: Int
    ) {
        // Activate the texture
        GLES32.glActiveTexture(whichTexture)

        // Bind the texture with the ID
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureID)

        // Set the min filter
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MIN_FILTER,
            GLES32.GL_NEAREST.toFloat()
        )

        // Set the mag filter
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MAG_FILTER,
            GLES32.GL_NEAREST.toFloat()
        )

        // Set the wrap for the edge s
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_S,
            GLES32.GL_CLAMP_TO_EDGE.toFloat()
        )

        // Set the wrap for the edge t
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_T,
            GLES32.GL_CLAMP_TO_EDGE.toFloat()
        )

        // Set the format to be RGBA
        GLES32.glTexImage2D(
            GLES32.GL_TEXTURE_2D,
            0,
            pixelFormat,
            width,
            height,
            0,
            pixelFormat,
            type,
            null
        )
    }

    private fun createFrameBuffers() {
        // Generate 2 texture objects
        GLES32.glGenTextures(1, frameBufferTextureID, 0)

        // Generate a framebuffer object
        GLES32.glGenFramebuffers(1, frameBuffer, 0)

        // Bind the framebuffer for drawing
        GLES32.glBindFramebuffer(GLES32.GL_DRAW_FRAMEBUFFER, frameBuffer[0])

        // Initialise texture (i.e. glActivateTextgure...glBindTexture...glTexImage2D....)
        initTexture(
            GLES32.GL_TEXTURE1,
            frameBufferTextureID[0],
            GLES32.GL_RGBA,
            GLES32.GL_UNSIGNED_BYTE
        )

        GLES32.glFramebufferTexture2D(
            GLES32.GL_FRAMEBUFFER,
            GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_TEXTURE_2D,
            frameBufferTextureID[0],
            0
        )

        GLES32.glGenRenderbuffers(1, renderBuffer, 0)

        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, renderBuffer[0])

        GLES32.glRenderbufferStorage(
            GLES32.GL_RENDERBUFFER,
            GLES32.GL_DEPTH_COMPONENT24,
            width,
            height
        )

        GLES32.glFramebufferRenderbuffer(
            GLES32.GL_FRAMEBUFFER,
            GLES32.GL_DEPTH_ATTACHMENT,
            GLES32.GL_RENDERBUFFER,
            renderBuffer[0]
        )

        val status = GLES32.glCheckFramebufferStatus(GLES32.GL_FRAMEBUFFER)
        if (status != GLES32.GL_FRAMEBUFFER_COMPLETE) {
            Log.d("framebuffer", "Error in creating framebuffer")
        }

        // Unbind the texture
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)

        // Unbind the framebuffer
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
    }

    override fun draw(modelViewProjectionMatrix: FloatArray) {
    }

    fun render(zoom: Float) {
        GLES32.glUseProgram(program)

        GLES32.glUniformMatrix4fv(
            uniformMVPMatrixHandle,
            1,
            false,
            mvpMatrix,
            0
        )

        // Set the active texture to unit 0
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)

        // Bind the texture to this unit
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, frameBufferTextureID[0])

        // Tell the uniform sampler to use this texture i
        GLES32.glUniform1i(uniformTextureSamplerHandle, 1)

        GLES32.glEnableVertexAttribArray(attributeVertexPositionHandle)
        GLES32.glEnableVertexAttribArray(attributeTextureCoordinateHandle)

        GLES32.glVertexAttribPointer(
            attributeTextureCoordinateHandle,
            TEXTURE_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            TEXTURE_STRIDE,
            textureBuffer
        )

        GLES32.glVertexAttribPointer(
            attributeVertexPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            vertexBuffer
        )

        // Draw 2D Plane
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            plane2DIndex.size,
            GLES32.GL_UNSIGNED_INT,
            orderBuffer
        )

        GLES32.glDisableVertexAttribArray(attributeVertexPositionHandle)
        GLES32.glDisableVertexAttribArray(attributeTextureCoordinateHandle)

        GLES32.glUseProgram(0)
    }

}