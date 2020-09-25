package com.carlospinan.opengles.common

import android.opengl.Matrix
import kotlin.math.abs

/**
 * @author Carlos Piñan
 */
class Camera(
    var eye: Vector3,
    private var center: Vector3,
    private var up: Vector3,
    private var projectionLeft: Float,
    private var projectionRight: Float,
    private var projectionBottom: Float,
    private var projectionTop: Float,
    private var projectionNear: Float,
    private var projectionFar: Float
) {

    private val orientation = Orientation()
    val projectionMatrix = FloatArray(16)
    val viewMatrix = FloatArray(16)

    init {
        setCameraProjection()

        // Set orientation
        orientation.forward.set(center)
        orientation.up.set(up)
        orientation.position.set(eye)

        // Calculate Right Local Vector
        val cameraRight = Vector3.crossProduct(center, up)
        cameraRight.normalize()
        orientation.right.set(cameraRight)
    }

    private fun setCameraProjection() {
        Matrix.frustumM(
            projectionMatrix,
            0,
            projectionLeft,
            projectionRight,
            projectionBottom,
            projectionTop,
            projectionNear,
            projectionFar
        )
    }

    private fun setCameraView() {
        // public static void setLookAtM (float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
        // Create Matrix
        Matrix.setLookAtM(
            viewMatrix,
            0,
            eye.x, eye.y, eye.z,
            center.x, center.y, center.z,
            up.x, up.y, up.z
        )
    }

    fun cameraViewportWidth(): Float {
        return abs(projectionLeft - projectionRight)
    }

    fun cameraViewportHeight(): Float {
        return abs(projectionBottom - projectionTop)
    }

    fun cameraViewportDepth(): Float {
        return abs(projectionFar - projectionNear)
    }

    private fun calculateLookAtVector() {
        // LookatVector = m_Projfar * FowardCameraUnitVecWorldCoords
        center.set(orientation.getForwardWorldCoordinates())

        // m_Center.Multiply(m_Projfar);
        center.scale(5f)

        center += orientation.position
    }

    private fun calculateUpVector() {
        up.set(orientation.getUpWorldCoordinates())
    }

    private fun calculatePosition() {
        eye.set(orientation.position)
    }

    fun updateCamera() {
        calculateLookAtVector()
        calculateUpVector()
        calculatePosition()

        setCameraView()
    }

}