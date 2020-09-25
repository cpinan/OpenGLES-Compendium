package com.carlospinan.openglesexamples.examples

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.core.math.MathUtils
import com.carlospinan.openglesexamples.R
import com.carlospinan.openglesexamples.common.BYTES_PER_FLOAT
import com.carlospinan.openglesexamples.common.ObjParser
import com.carlospinan.openglesexamples.common.Object3D
import com.carlospinan.openglesexamples.common.parseObjFile
import com.carlospinan.openglesexamples.common.views.BaseGLRenderer
import com.carlospinan.openglesexamples.common.views.BaseGLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

/**
 * @author Carlos Piñan
 */

val menuElements = arrayListOf(
    Pair(R.string.shape_cube, R.raw.cube),
    Pair(R.string.shape_cylinder, R.raw.cylinder),
    Pair(R.string.shape_ico_sphere, R.raw.icosphere),
    Pair(R.string.shape_monkey, R.raw.monkey),
    Pair(R.string.shape_plane, R.raw.plane),
    Pair(R.string.shape_sphere, R.raw.sphere),
    Pair(R.string.shape_torus, R.raw.torus),
    Pair(R.string.shape_tank, R.raw.tank),
    Pair(R.string.shape_dragon, R.raw.dragon_sample)
)

private var selectedResource = menuElements[0].second
private const val ZOOM_FACTOR = 5F

class OBJActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(OBJSurfaceView())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        for (element in menuElements) {
            menu.add(element.first)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        for (element in menuElements) {
            if (item.title == getString(element.first) && element.second != selectedResource) {
                selectedResource = element.second
                recreate()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // View
    inner class OBJSurfaceView : BaseGLSurfaceView(this) {

        private val onScaleGestureListener = object :
            ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // scaleFactor > 1 then Zooming Out
                // else then Zooming In
                objectRender.zoom = detector.scaleFactor
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                return true
            }

        }

        private var objectRender: OBJRenderer = renderer as OBJRenderer

        private val scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(
            this@OBJActivity, onScaleGestureListener
        )

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val y = event.y
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (y < height / 2) {
                    objectRender.zoom += ZOOM_FACTOR
                } else {
                    objectRender.zoom -= ZOOM_FACTOR
                }
            }
            scaleGestureDetector.onTouchEvent(event)
            return true
        }

        override fun setupRenderer(): BaseGLRenderer = OBJRenderer()

    }

    // Renderer
    inner class OBJRenderer : BaseGLRenderer(this) {

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

        var zoom = 1F

        private lateinit var customObject: CustomObject

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            val objParser = parseObjFile(context, selectedResource)
            Log.d("objParser", "${objParser.id} ; ${objParser.name}")
            customObject = CustomObject(context, objParser)
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
                1000F
            )
        }

        override fun onDrawFrame(gl: GL10?) {
            // Do a complete rotation every 10 seconds.
            val time = SystemClock.uptimeMillis() % 10000L
            val angleInDegrees = 360.0f / 10000.0f * time

            GLES32.glClearColor(0F, 0F, 0F, 1F)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

            GLES32.glClearDepthf(1F)
            GLES32.glEnable(GLES32.GL_DEPTH_TEST)
            GLES32.glDepthFunc(GLES32.GL_LEQUAL)

            /*
            // Use culling to remove back faces.
            GLES32.glEnable(GLES20.GL_CULL_FACE);
             */

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
                -5F + zoom
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

            customObject.draw(modelViewProjectionMatrix)

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

class CustomObject(
    context: Context,
    private val objectParsed: ObjParser
) :
    Object3D(context, R.raw.vertex_with_color_shader, R.raw.fragment_color_shader) {

    private val aVertexPositionHandle by lazy {
        GLES32.glGetAttribLocation(program, "aVertexPosition")
    }

    private val aVertexColorHandle by lazy {
        GLES32.glGetAttribLocation(program, "aVertexColor")
    }

    private val uMVPMatrixHandle by lazy {
        GLES32.glGetUniformLocation(program, "uMVPMatrix")
    }

    private val coordsPerVertex = 3
    private val vertexStride = BYTES_PER_FLOAT * coordsPerVertex

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(objectParsed.vertex.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(objectParsed.vertex)
                position(0)
            }

    private val colorBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(objectParsed.vertex.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                var colorIndex = 0
                val colors = FloatArray(objectParsed.vertex.size)
                for (i in 0..(colors.size - coordsPerVertex) step coordsPerVertex) {
                    colors[colorIndex++] = MathUtils.clamp(Random.nextFloat(), 0.2F, 1F)
                    colors[colorIndex++] = MathUtils.clamp(Random.nextFloat(), 0.2F, 0.5F)
                    colors[colorIndex++] = MathUtils.clamp(Random.nextFloat(), 0.2F, 0.5F)
                }
                put(colors)
                position(0)
            }

    private val indexBuffer: IntBuffer =
        IntBuffer.allocate(objectParsed.order.size)
            .apply {
                put(objectParsed.order)
                position(0)
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

        GLES32.glEnableVertexAttribArray(aVertexPositionHandle)
        GLES32.glEnableVertexAttribArray(aVertexColorHandle)

        GLES32.glVertexAttribPointer(
            aVertexPositionHandle,
            coordsPerVertex,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES32.glVertexAttribPointer(
            aVertexColorHandle,
            coordsPerVertex,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )

        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            objectParsed.order.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )

        GLES32.glDisableVertexAttribArray(aVertexPositionHandle)
        GLES32.glDisableVertexAttribArray(aVertexColorHandle)

        GLES32.glUseProgram(0)
    }

}