package com.carlospinan.opengles.common

import android.opengl.GLES32
import android.opengl.Matrix
import android.os.SystemClock

/**
 * @author Carlos Piñan
 */
open class Object3d(
    private val mesh: Mesh?,
    private val textures: Array<Texture>,
    private val material: Material?,
    private val shader: Shader
) {

    var orientation = Orientation()

    private val mvpMatrix = FloatArray(16)
    private var modelMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val normalMatrix = FloatArray(16)
    private val normalMatrixInvert = FloatArray(16)

    private var activeTexture = if (textures.isEmpty()) -1 else 0
    private var animateTextures = false
    private var startTextureAnimationIndex = 0
    private var stopTextureAnimationIndex = 0
    private var textureAnimationDelay = 0F // In Seconds
    private var targetTime = 0F
    private var counter = 0F

    private var positionHandle = 0
    private var textureHandle = 0
    private var normalHandle = 0

    fun getTexture(textureNumber: Int) = textures[textureNumber % textures.size]

    fun setTexture(textureNumber: Int) {
        activeTexture = textureNumber % textures.size
    }

    private fun activateTexture() {
        if (activeTexture >= 0) {
            Texture.setActiveTextureUnit(GLES32.GL_TEXTURE0)
            GLCommon.checkGLError("glActiveTexture - SetActiveTexture Unit Failed")

            if (animateTextures && (counter >= targetTime)) {
                activeTexture++
                if (activeTexture > stopTextureAnimationIndex) {
                    activeTexture = startTextureAnimationIndex
                }
                targetTime = counter + textureAnimationDelay
            }
            counter = SystemClock.uptimeMillis() * 1 / 1000F // Convert to seconds

            // Activates texture for this object
            textures[activeTexture].activateTexture()
        }
    }

    fun drawObject(camera: Camera, light: PointLight) {
        drawObject(
            camera,
            light,
            orientation.position,
            orientation.rotationAxis,
            orientation.scale
        )
    }

    private fun drawObject(
        camera: Camera,
        light: PointLight,
        position: Vector3,
        rotationAxis: Vector3,
        scale: Vector3
    ) {
        // Activate and set up the Shader and Draw Object's mesh

        // Generate Needed Matrices for Object
        generateMatrices(
            camera,
            position,
            rotationAxis,
            scale
        )

        // Add program to OpenGL environment
        shader.activateShader()

        // Get Vertex Attribute Info in preparation for drawing the mesh
        getVertexAttribInfo()

        // Sets up the lighting parameters for this object
        setLighting(camera, light)

        // Apply the projection and view transformation matrix to the shader
        shader.setShaderVariableValueFloatMatrix4Array(
            "uMVPMatrix",
            1,
            false,
            mvpMatrix,
            0
        )

        // Activates texture for this object
        activateTexture()

        // Enable Hidden surface removal
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        mesh?.drawMesh(
            positionHandle,
            textureHandle,
            normalHandle
        )

    }

    private fun setLighting(camera: Camera, light: PointLight) {
        val ambientColor = light.ambientLight
        val diffuseColor = light.diffuseLight
        val specularColor = light.specularLight
        val specularShininess = light.specularShininess

        val eyePos = camera.eye
        shader.setShaderUniformVariableValue("uLightAmbient", ambientColor)
        shader.setShaderUniformVariableValue("uLightDiffuse", diffuseColor)
        shader.setShaderUniformVariableValue("uLightSpecular", specularColor)
        shader.setShaderUniformVariableValue("uLightShininess", specularShininess)
        shader.setShaderUniformVariableValue("uWorldLightPos", light.position)
        shader.setShaderUniformVariableValue("uEyePosition", eyePos)

        shader.setShaderVariableValueFloatMatrix4Array(
            "NormalMatrix", 1, false, normalMatrix, 0
        )

        shader.setShaderVariableValueFloatMatrix4Array(
            "uModelMatrix", 1, false, modelMatrix, 0
        )

        shader.setShaderVariableValueFloatMatrix4Array(
            "uViewMatrix",
            1,
            false,
            camera.viewMatrix,
            0
        )

        shader.setShaderVariableValueFloatMatrix4Array(
            "uModelViewMatrix",
            1,
            false,
            modelViewMatrix,
            0
        )

        // Set object lighting Material Properties in shader
        if (material != null) {
            shader.setShaderUniformVariableValue("uMatEmissive", material.emissive)
            shader.setShaderUniformVariableValue("uMatAmbient", material.ambient)
            shader.setShaderUniformVariableValue("uMatDiffuse", material.diffuse)
            shader.setShaderUniformVariableValue("uMatSpecular", material.specular)
            shader.setShaderUniformVariableValue("uMatAlpha", material.alpha)
        }

    }

    private fun generateMatrices(
        camera: Camera,
        position: Vector3,
        rotationAxis: Vector3,
        scale: Vector3
    ) {
        // Initialize Model Matrix
        Matrix.setIdentityM(modelMatrix, 0)

        orientation.position.set(position)
        orientation.rotationAxis.set(rotationAxis)
        orientation.scale.set(scale)

        orientation.updateOrientation()
        modelMatrix = orientation.orientationMatrix

        // Create Model View Matrix
        Matrix.multiplyMM(
            modelViewMatrix,
            0,
            camera.viewMatrix,
            0,
            modelMatrix,
            0
        )

        // Create Normal Matrix for lighting
        Matrix.multiplyMM(
            normalMatrix,
            0,
            camera.viewMatrix,
            0,
            modelMatrix,
            0
        )

        Matrix.invertM(
            normalMatrixInvert,
            0,
            normalMatrix,
            0
        )

        Matrix.transposeM(
            normalMatrix,
            0,
            normalMatrixInvert,
            0
        )

        // Create Model View Projection Matrix
        Matrix.multiplyMM(
            mvpMatrix,
            0,
            camera.projectionMatrix,
            0,
            modelViewMatrix,
            0
        )

    }

    private fun getVertexAttribInfo() {
        // Set up Vertex Position Data
        positionHandle = shader.getShaderVertexAttributeVariableLocation("aPosition")
        GLCommon.checkGLError("glGetAttribLocation - aPosition")

        // Set up Vertex Texture Data
        textureHandle = shader.getShaderVertexAttributeVariableLocation("aTextureCoord")
        GLCommon.checkGLError("glGetAttribLocation - aTextureCoord")

        // Get Handle to Normal Vertex Attribute Variable
        normalHandle = shader.getShaderVertexAttributeVariableLocation("aNormal")
        GLCommon.checkGLError("glGetAttribLocation - aNormal")
    }

}