package com.carlospinan.opengles.gLHelloWorld

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import com.carlospinan.opengles.R
import com.carlospinan.opengles.common.*
import com.carlospinan.opengles.common.objects.Cube
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author Carlos Piñan
 */
class MyGLRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    private lateinit var pointLight: PointLight
    private lateinit var cube: Cube
    private lateinit var camera: Camera

    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0

    private val cubePositionDelta = Vector3(0f, 0f, 0.1f)
    private val cubeScaleDelta = Vector3(1f, 1f, 0.1f)
    private val cubeTranslateXDelta = Vector3(0.1f, 0f, 0f)

    private fun setupLights() {
        val lightPosition = Vector3(0f, 125f, 125f)
        // val lightPosition = Vector3(125f, 0f, 0f)
        // val lightPosition = Vector3(-125f, 0f, 0f)
        //val lightPosition = Vector3(0f, 125f, 0f)
        // val lightPosition = Vector3(0f, -125f, 0f)
        val ambientColor = Vector3.zero()
        val diffuseColor = Vector3(1f, 1f, 1f)
        val specularColor = Vector3(1f, 1f, 1f)

        pointLight.position.set(lightPosition)
        pointLight.ambientLight.set(ambientColor)
        pointLight.diffuseLight.set(diffuseColor)
        pointLight.specularLight.set(specularColor)
    }

    private fun createCube() {
        // Create Cube Shader
        val shader = Shader(
            context,
            R.raw.vsonelight,
            R.raw.fsonelight
        )

        val mesh = Mesh(
            8,
            0,
            3,
            5,
            Cube.cubeData,
            Cube.cubeDrawOrder
        )

        val material = Material()
        val texture = Texture(context, R.drawable.ic_launcher)

        val cubeTexture = arrayOf(texture)

        cube = Cube(
            mesh,
            cubeTexture,
            material,
            shader
        )

        // Set Initial Position and Orientation
        val axis = Vector3(0f, 1f, 0f)
        val position = Vector3(0.0f, 0.0f, 0.0f)
        val scale = Vector3(1.0f, 1.0f, 1.0f)

        cube.orientation.position.set(position)
        cube.orientation.rotationAxis.set(axis)
        cube.orientation.scale.set(scale)
        // cube.orientation.addRotation(45f)
    }

    private fun setupCamera() {
        // Set Camera View
        val eye = Vector3(0f, 0f, 8f)
        val center = Vector3(0f, 0f, -1f)
        val up = Vector3(0f, 1f, 0f)

        val ratio = viewportWidth.toFloat() / viewportHeight

        val projectionLeft = -ratio

        val projectionBottom = -1f
        val projectionTop = 1f

        val projectionNear = 3f
        val projectionFar = 50f // 100f

        camera = Camera(
            eye,
            center,
            up,
            projectionLeft,
            ratio,
            projectionBottom,
            projectionTop,
            projectionNear,
            projectionFar
        )

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Called when an new surface has been created
        // Create OpenGL resources here
        pointLight = PointLight()
        setupLights()
        createCube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Called when new GL Surface has been created or changes size
        // Set the OpenglES camera viewport here
        GLES32.glViewport(0, 0, width, height)
        viewportWidth = width
        viewportHeight = height
        setupCamera()
    }

    override fun onDrawFrame(gl: GL10?) {
        // Put code to draw 3d objects to screen here
        GLES32.glClearColor(1f, 0f, 0f, 1f)
        GLES32.glClear(
            GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT
        )

        camera.updateCamera()

        // Set Position
        var currentCubePosition = cube.orientation.position
        if (currentCubePosition.x < -1 || currentCubePosition.x > 1) {
            cubeTranslateXDelta.negate()
        }
        // cube.orientation.position.set(currentCubePosition.add(cubeTranslateXDelta))

        // Add rotation to the cube
        cube.orientation.rotationAxis = Vector3(0f, 1f, 0f)
        cube.orientation.addRotation(1f)

        // Add translation to the cube
        currentCubePosition = cube.orientation.position
        if (currentCubePosition.z > 4f || currentCubePosition.z < -4) {
            cubePositionDelta.negate()
        }
        val newCubePosition = currentCubePosition.add(cubePositionDelta)
        // Log.d("POSITION", "$newCubePosition")
        // cube.orientation.position.set(newCubePosition)

        // Set scale
        // cube.orientation.scale.set(cubeScaleDelta)


        cube.drawObject(camera, pointLight)
    }

}