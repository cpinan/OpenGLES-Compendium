package com.carlospinan.opengles.common

import android.opengl.Matrix

/**
 * @author Carlos Piñan
 */
class Orientation {

    var right = Vector3(1.0f, 0.0f, 0.0f)
    var up = Vector3(0.0f, 1.0f, 0.0f)
    var forward = Vector3(0.0f, 0.0f, 1.0f)
    var position = Vector3(0.0f, 0.0f, 0.0f)
    var scale = Vector3(1.0f, 1.0f, 1.0f)

    var rotationAngle = 0F
    var rotationAxis = Vector3(0.0f, 1.0f, 0.0f)

    var orientationMatrix = FloatArray(16)
        private set
    private var rotationMatrix = FloatArray(16)
    private var positionMatrix = FloatArray(16)
    private var scaleMatrix = FloatArray(16)
    private var temporalMatrix = FloatArray(16)

    init {
        Matrix.setIdentityM(orientationMatrix, 0)
        Matrix.setIdentityM(rotationMatrix, 0)
    }

    fun getUpWorldCoordinates(): Vector3 {
        val upWorld = FloatArray(4)
        val upLocal = floatArrayOf(up.x, up.y, up.z, 1f)

        //void multiplyMV (float[] resultVec, int resultVecOffset, float[] lhsMat, int lhsMatOffset, float[] rhsVec, int rhsVecOffset)
        Matrix.multiplyMV(upWorld, 0, rotationMatrix, 0, upLocal, 0)

        val resultVector = Vector3(upWorld[0], upWorld[1], upWorld[2])
        resultVector.normalize()
        return resultVector
    }

    fun getRightWorldCoordinates(): Vector3 {
        val rightWorld = FloatArray(4)
        val rightLocal = floatArrayOf(right.x, right.y, right.z, 1f)

        //void multiplyMV (float[] resultVec, int resultVecOffset, float[] lhsMat, int lhsMatOffset, float[] rhsVec, int rhsVecOffset)
        Matrix.multiplyMV(rightWorld, 0, rotationMatrix, 0, rightLocal, 0)

        val resultVector = Vector3(rightWorld[0], rightWorld[1], rightWorld[2])
        resultVector.normalize()
        return resultVector
    }

    fun getForwardWorldCoordinates(): Vector3 {
        val forwardWorld = FloatArray(4)
        val forwardLocal = floatArrayOf(forward.x, forward.y, forward.z, 1f)

        //void multiplyMV (float[] resultVec, int resultVecOffset, float[] lhsMat, int lhsMatOffset, float[] rhsVec, int rhsVecOffset)
        Matrix.multiplyMV(forwardWorld, 0, rotationMatrix, 0, forwardLocal, 0)

        val resultVector = Vector3(forwardWorld[0], forwardWorld[1], forwardWorld[2])
        resultVector.normalize()
        return resultVector
    }

    private fun updatePositionMatrix(currentPosition: Vector3) {
        Matrix.setIdentityM(positionMatrix, 0)
        Matrix.translateM(
            positionMatrix,
            0,
            currentPosition.x,
            currentPosition.y,
            currentPosition.z
        )
    }

    fun updateRotationMatrix(angle: Float, axis: Vector3) {
        Matrix.setIdentityM(rotationMatrix, 0)
        Matrix.setRotateM(
            rotationMatrix,
            0,
            angle,
            axis.x,
            axis.y,
            axis.z
        )
    }

    private fun updateScaleMatrix(scale: Vector3) {
        Matrix.setIdentityM(scaleMatrix, 0)
        Matrix.scaleM(
            scaleMatrix,
            0,
            scale.x,
            scale.y,
            scale.z
        )
    }

    fun addRotation(rotation: Float) {
        rotationAngle += rotation

        //rotateM(float[] m, int mOffset, float a, float x, float y, float z)
        //Rotates matrix m in place by angle a (in degrees) around the axis (x, y, z)
        Matrix.rotateM(
            rotationMatrix,
            0,
            rotation,
            rotationAxis.x,
            rotationAxis.y,
            rotationAxis.z
        )
    }

    fun updateOrientation() {
        // Build Translation Matrix
        updatePositionMatrix(position)

        // Build Scale Matrix
        updateScaleMatrix(scale)

        // Then Rotate object around Axis then translate
        Matrix.multiplyMM(
            temporalMatrix,
            0,
            positionMatrix,
            0,
            rotationMatrix,
            0
        )

        // Scale Object first
        Matrix.multiplyMM(
            orientationMatrix,
            0,
            temporalMatrix,
            0,
            scaleMatrix,
            0
        )
    }

}