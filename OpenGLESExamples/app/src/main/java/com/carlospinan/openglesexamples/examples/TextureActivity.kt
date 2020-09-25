package com.carlospinan.openglesexamples.examples

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.carlospinan.openglesexamples.R
import com.carlospinan.openglesexamples.common.BYTES_PER_FLOAT
import com.carlospinan.openglesexamples.common.Object3D
import com.carlospinan.openglesexamples.common.checkGLError
import com.carlospinan.openglesexamples.common.views.BaseGLRenderer
import com.carlospinan.openglesexamples.common.views.BaseGLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author Carlos Piñan
 */
class TextureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextureSurfaceView())
    }

    inner class TextureSurfaceView : BaseGLSurfaceView(this) {

        override fun setupRenderer(): BaseGLRenderer {
            return TextureRenderer()
        }

        init {
            renderMode = RENDERMODE_CONTINUOUSLY
        }

    }

    inner class TextureRenderer : BaseGLRenderer(this) {

        // Used to project into 2D screen. For shader program
        private val modelViewProjectionMatrix = FloatArray(16)

        // Basic frustum. Check matrix projection
        private val projectionMatrix = FloatArray(16)

        // For camera view. SetLookAt
        private val viewMatrix = FloatArray(16)

        // To allocate the result between viewMatrix * modelMatrix
        private val modelViewMatrix = FloatArray(16)

        /**
         * Store the model matrix.
         * This matrix is used to move models from object space (where each model can be thought
         * of being located at the center of the universe) to world space.
         */
        private val modelMatrix = FloatArray(16)

        private lateinit var cube: Cube

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            cube = Cube(context)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES32.glViewport(0, 0, width, height)

            val ratio = width.toFloat() / height

            Matrix.frustumM(
                projectionMatrix,
                0,
                -ratio,
                ratio,
                -1F,
                1F,
                1F,
                10F
            )
        }

        override fun onDrawFrame(gl: GL10?) {
            // Do a complete rotation every 10 seconds.
            val time = SystemClock.uptimeMillis() % 10000L
            val angleInDegrees = 360.0f / 10000.0f * time

            GLES32.glClearColor(1F, 1F, 1F, 1F)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

            GLES32.glClearDepthf(1F)
            GLES32.glEnable(GLES32.GL_DEPTH_TEST)
            GLES32.glDepthFunc(GLES32.GL_LEQUAL)

            setupCamera()

            Matrix.setIdentityM(modelViewProjectionMatrix, 0)
            Matrix.setIdentityM(modelViewMatrix, 0)
            Matrix.setIdentityM(modelMatrix, 0)

            // MODEL MATRIX CALCULATION
            Matrix.translateM(
                modelMatrix,
                0,
                0F,
                0F,
                -1F
            )

            val rotationYMatrix = FloatArray(16)
            Matrix.setRotateM(
                rotationYMatrix,
                0,
                angleInDegrees,
                0F, 1F, 0F
            )

            val rotationXMatrix = FloatArray(16)
            Matrix.setRotateM(
                rotationXMatrix,
                0,
                angleInDegrees,
                1F, 0F, 0F
            )

            Matrix.multiplyMM(
                modelMatrix,
                0,
                modelMatrix,
                0,
                rotationYMatrix,
                0
            )

            Matrix.multiplyMM(
                modelMatrix,
                0,
                modelMatrix,
                0,
                rotationXMatrix,
                0
            )

            // MODEL VIEW MATRIX
            Matrix.multiplyMM(
                modelViewMatrix,
                0,
                viewMatrix,
                0,
                modelMatrix,
                0
            )

            // FINAL MATRIX FOR PROJECTION
            Matrix.multiplyMM(
                modelViewProjectionMatrix, // productMatrix
                0,
                projectionMatrix,
                0,
                modelViewMatrix,
                0
            )

            cube.render(modelViewProjectionMatrix, modelViewMatrix)


        }

        private fun setupCamera() {
            // Position the eye behind the origin.
//            val eyeX = 0.0F
//            val eyeY = 0F
//            val eyeZ = -4F
            val eyeX = 0F
            val eyeY = 0F
            val eyeZ = 4F

            // We are looking toward the distance
            val lookX = 0.0F
            val lookY = 0.0F
            val lookZ = 0F

            // Set our up vector. This is where our head would be pointing were we holding the camera.
            val upX = 0.0F
            val upY = 1.0F
            val upZ = 0.0F

            Matrix.setLookAtM(
                viewMatrix,
                0,
                eyeX,
                eyeY,
                eyeZ,
                lookX,
                lookY,
                lookZ,
                upX,
                upY,
                upZ
            )
        }

    }

}

class Cube(
    context: Context
) : Object3D(context, R.raw.basic_texture_vertex_shader, R.raw.basic_texture_fragment_shader) {

    private val vertex = floatArrayOf(
        // Front Face
        -1F, -1F, 1F,
        1F, -1F, 1F,
        1F, 1F, 1F,
        -1F, 1F, 1F,

        // Back Face
        -1F, -1F, -1F,
        -1F, 1F, -1F,
        1F, 1F, -1F,
        1F, -1F, -1F,

        // Top Face
        -1F, 1F, -1F,
        -1F, 1F, 1F,
        1F, 1F, 1F,
        1F, 1F, -1F,

        // Bottom Face
        -1F, -1F, -1F,
        1F, -1F, -1F,
        1F, -1F, 1F,
        -1F, -1F, 1F,

        // Right
        1F, -1F, -1F,
        1F, 1F, -1F,
        1F, 1F, 1F,
        1F, -1F, 1F,

        // Left Face
        -1F, -1F, -1F,
        -1F, -1F, 1F,
        -1F, 1F, 1F,
        -1F, 1F, -1F
    )

    private val indices = intArrayOf(
        0, 1, 2, 0, 2, 3, // front face
        4, 5, 6, 4, 6, 7, // back face
        8, 9, 10, 8, 10, 11, // top face
        12, 13, 14, 12, 14, 15, // bottom face
        16, 17, 18, 16, 18, 19, // right face
        20, 21, 22, 20, 22, 23 // left face
    )

    private val colors = floatArrayOf(
        // Front Face
        0F, 0F, 1F, 1F,
        0F, 0F, 1F, 1F,
        0F, 0F, 1F, 1F,
        0F, 0F, 1F, 1F,
        // Back Face
        0F, 1F, 0F, 1F,
        0F, 1F, 0F, 1F,
        0F, 1F, 0F, 1F,
        0F, 1F, 0F, 1F,
        // Top Face
        1F, 0F, 0F, 1F,
        1F, 0F, 0F, 1F,
        1F, 0F, 0F, 1F,
        1F, 0F, 0F, 1F,
        // Bottom Face
        0F, 1F, 1F, 1F,
        0F, 1F, 1F, 1F,
        0F, 1F, 1F, 1F,
        0F, 1F, 1F, 1F,
        // Right Face
        1F, 1F, 0F, 1F,
        1F, 1F, 0F, 1F,
        1F, 1F, 0F, 1F,
        1F, 1F, 0F, 1F,
        // Left Face
        1F, 0F, 1F, 1F,
        1F, 0F, 1F, 1F,
        1F, 0F, 1F, 1F,
        1F, 0F, 1F, 1F
    )

    // S, T (or X, Y)
    // Texture coordinate data.
    // Because images have a Y axis pointing downward (values increase as you move down the image) while
    // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
    // What's more is that the texture coordinates are the same for every face.
    private val textureCoord = floatArrayOf(
        // Front face
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,

        // Right face
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,

        // Back face
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,

        // Left face
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,

        // Top face
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,

        // Bottom face
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    private val uModelViewProjectionMatrixHandle by lazy {
        GLES32.glGetUniformLocation(program, "uModelViewProjectionMatrix")
    }

    private val uModelViewMatrixHandle by lazy {
        GLES32.glGetUniformLocation(program, "uModelViewMatrix")
    }

    private val aVertexPositionHandle by lazy {
        GLES32.glGetAttribLocation(program, "aVertexPosition")
    }

    private val aVertexColorHandle by lazy {
        GLES32.glGetAttribLocation(program, "aVertexColor")
    }

    private val aTextureCoordinateHandle by lazy {
        GLES32.glGetAttribLocation(program, "aTextureCoordinate")
    }

    private val uTextureHandle by lazy {
        GLES32.glGetUniformLocation(program, "uTexture")
    }

    private val coordsPerVertex = 3
    private val colorsPerVertex = 4
    private val texturesPerVertex = 2

    private val coordStride = coordsPerVertex * BYTES_PER_FLOAT
    private val colorStride = colorsPerVertex * BYTES_PER_FLOAT
    private val textureStride = texturesPerVertex * BYTES_PER_FLOAT

    private var vertexBuffer: FloatBuffer
    private var colorBuffer: FloatBuffer
    private var indexBuffer: IntBuffer
    private var textureBuffer: FloatBuffer

    private var textureId = 0

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertex.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertex)
                position(0)
            }

        colorBuffer = ByteBuffer.allocateDirect(colors.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(colors)
                position(0)
            }

        textureBuffer = ByteBuffer.allocateDirect(textureCoord.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(textureCoord)
                position(0)
            }

        indexBuffer = IntBuffer.allocate(indices.size)
            .apply {
                put(indices)
                position(0)
            }

        textureId = loadTexture(R.drawable.bumpy_bricks_public_domain)
    }

    override fun draw(modelViewProjectionMatrix: FloatArray) {}

    fun render(mvpMatrix: FloatArray, mvMatrix: FloatArray) {

        GLES32.glUseProgram(program)
        checkGLError("glUseProgram $program")

        GLES32.glEnableVertexAttribArray(aVertexPositionHandle)
        checkGLError("glEnableVertexAttribArray $aVertexPositionHandle")

        GLES32.glEnableVertexAttribArray(aVertexColorHandle)
        checkGLError("glEnableVertexAttribArray $aVertexColorHandle")

        GLES32.glEnableVertexAttribArray(aTextureCoordinateHandle)
        checkGLError("glEnableVertexAttribArray $aTextureCoordinateHandle")

        GLES32.glUniformMatrix4fv(
            uModelViewProjectionMatrixHandle,
            1,
            false,
            mvpMatrix,
            0
        )
        checkGLError("glUniformMatrix4fv $uModelViewProjectionMatrixHandle")

        GLES32.glUniformMatrix4fv(
            uModelViewMatrixHandle,
            1,
            false,
            mvMatrix,
            0
        )
        checkGLError("glUniformMatrix4fv $uModelViewMatrixHandle")

        GLES32.glVertexAttribPointer(
            aVertexPositionHandle,
            coordsPerVertex,
            GLES32.GL_FLOAT,
            false,
            coordStride,
            vertexBuffer
        )
        checkGLError("glVertexAttribPointer $aVertexPositionHandle")

        GLES32.glVertexAttribPointer(
            aVertexColorHandle,
            colorsPerVertex,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
        checkGLError("glVertexAttribPointer $aVertexColorHandle")

        GLES32.glVertexAttribPointer(
            aTextureCoordinateHandle,
            texturesPerVertex,
            GLES32.GL_FLOAT,
            false,
            textureStride,
            textureBuffer
        )
        checkGLError("glVertexAttribPointer $aTextureCoordinateHandle")

        /**
         * Set the active texture unit.
         * Bind a texture to this unit.
         * Assign this unit to a texture uniform in the fragment shader.
         */

        // Activate texture
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)

        // Bind the texture to this unit.
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureId)

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES32.glUniform1i(uTextureHandle, 0)

        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            indices.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )

        GLES32.glDisableVertexAttribArray(aVertexPositionHandle)
        GLES32.glDisableVertexAttribArray(aVertexColorHandle)
        GLES32.glDisableVertexAttribArray(aTextureCoordinateHandle)

        GLES32.glUseProgram(0)

    }

}