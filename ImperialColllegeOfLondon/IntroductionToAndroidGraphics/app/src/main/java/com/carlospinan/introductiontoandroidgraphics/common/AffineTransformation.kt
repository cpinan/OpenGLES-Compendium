package com.carlospinan.introductiontoandroidgraphics.common

import kotlin.math.cos
import kotlin.math.sin

/**
 * @author Carlos Piñan
 */
object AffineTransformation {

    private inline fun degreesToRadians(degrees: Float) = (degrees * Math.PI / 180F).toFloat()

    private inline fun radiansToDegrees(radians: Float) = (radians * 180F / Math.PI).toFloat()

    private fun vectorMatrixMultiplication(vector: Vector3, matrix: Array<FloatArray>): Vector3 {
        val x = matrix[0][0] * vector.x +
                matrix[0][1] * vector.y +
                matrix[0][2] * vector.z +
                matrix[0][3]

        val y = matrix[1][0] * vector.x +
                matrix[1][1] * vector.y +
                matrix[1][2] * vector.z +
                matrix[1][3]

        val z = matrix[2][0] * vector.x +
                matrix[2][1] * vector.y +
                matrix[2][2] * vector.z +
                matrix[2][3]

        val w = matrix[3][0] * vector.x +
                matrix[3][1] * vector.y +
                matrix[3][2] * vector.z +
                matrix[3][3]

        return Vector3(x, y, z).normalizeWithW()
    }

    private fun affineVectorList(
        vectorList: Array<Vector3>,
        matrix: Array<FloatArray>
    ): Array<Vector3> {
        val newVectorList = mutableListOf<Vector3>()
        for (vector in vectorList) {
            newVectorList.add(
                vectorMatrixMultiplication(vector, matrix)
            )
        }
        return newVectorList.toTypedArray()
    }

    private fun m4x4(): Array<FloatArray> {
        return arrayOf(
            floatArrayOf(0F, 0F, 0F, 0F),
            floatArrayOf(0F, 0F, 0F, 0F),
            floatArrayOf(0F, 0F, 0F, 0F),
            floatArrayOf(0F, 0F, 0F, 0F)
        )
    }

    private fun identityMatrix(): Array<FloatArray> {
        return arrayOf(
            floatArrayOf(1F, 0F, 0F, 0F),
            floatArrayOf(0F, 1F, 0F, 0F),
            floatArrayOf(0F, 0F, 1F, 0F),
            floatArrayOf(0F, 0F, 0F, 1F)
        )
    }

    private fun translationMatrix(
        translation: Vector3
    ): Array<FloatArray> {
        val matrix = identityMatrix()

        matrix[0][3] = translation.x
        matrix[1][3] = translation.y
        matrix[2][3] = translation.z

        return matrix
    }

    private fun scaleMatrix(
        scale: Vector3
    ): Array<FloatArray> {
        val matrix = identityMatrix()

        matrix[0][0] = scale.x
        matrix[1][1] = scale.y
        matrix[2][2] = scale.z

        return matrix
    }

    private fun rotation3DXMatrix(
        degrees: Float
    ): Array<FloatArray> {
        val radians = degreesToRadians(degrees)
        val matrix = identityMatrix()

        matrix[1][1] = cos(radians)
        matrix[1][2] = -sin(radians)

        matrix[2][1] = sin(radians)
        matrix[2][2] = cos(radians)

        return matrix
    }

    private fun rotation3DYMatrix(
        degrees: Float
    ): Array<FloatArray> {
        val radians = degreesToRadians(degrees)
        val matrix = identityMatrix()

        matrix[0][0] = cos(radians)
        matrix[0][2] = sin(radians)

        matrix[2][0] = -sin(radians)
        matrix[2][2] = cos(radians)

        return matrix
    }

    private fun rotation3DZMatrix(
        degrees: Float
    ): Array<FloatArray> {
        val radians = degreesToRadians(degrees)
        val matrix = identityMatrix()

        matrix[0][0] = cos(radians)
        matrix[0][1] = -sin(radians)

        matrix[1][0] = sin(radians)
        matrix[1][1] = cos(radians)

        return matrix
    }

    private fun rotation2DMatrix(
        degrees: Float
    ): Array<FloatArray> {
        val radians = degreesToRadians(degrees)
        val matrix = identityMatrix()

        matrix[0][0] = cos(radians)
        matrix[0][1] = -sin(radians)

        matrix[1][0] = sin(radians)
        matrix[1][1] = cos(radians)

        return matrix
    }

    private fun shearMatrix(
        shearX: Float,
        shearY: Float
    ): Array<FloatArray> {
        val matrix = identityMatrix()

        matrix[0][2] = shearX
        matrix[1][2] = shearY

        return matrix
    }

    private fun quaternionMatrix(degrees: Float, axis: Vector3): Array<FloatArray> {
        val radians = degreesToRadians(degrees * 0.5F)

        val w = cos(radians)
        val x = sin(radians) * axis.x
        val y = sin(radians) * axis.y
        val z = sin(radians) * axis.z

        val matrix = identityMatrix()

        matrix[0][0] = w * w + x * x - y * y - z * z
        matrix[0][1] = 2F * x * y - 2F * w * z
        matrix[0][2] = 2F * x * z + 2F * w * y

        matrix[1][0] = 2F * x * y + 2F * w * z
        matrix[1][1] = w * w + y * y - x * x - z * z
        matrix[1][2] = 2F * y * z - 2F * w * x

        matrix[2][0] = 2F * x * z - 2F * w * y
        matrix[2][1] = 2F * y * z + 2F * w * x
        matrix[2][2] = w * w + z * z - x * x - y * y

        return matrix
    }

    // PUBLIC
    fun rotate2D(degrees: Float, vectorList: Array<Vector3>): Array<Vector3> {
        return affineVectorList(vectorList, rotation2DMatrix(degrees))
    }

    fun rotate3DX(degrees: Float, vectorList: Array<Vector3>): Array<Vector3> {
        return affineVectorList(vectorList, rotation3DXMatrix(degrees))
    }

    fun rotate3DY(degrees: Float, vectorList: Array<Vector3>): Array<Vector3> {
        return affineVectorList(vectorList, rotation3DYMatrix(degrees))
    }

    fun rotate3DZ(degrees: Float, vectorList: Array<Vector3>): Array<Vector3> {
        return affineVectorList(vectorList, rotation3DZMatrix(degrees))
    }

    fun scale(scaling: Vector3, vectorList: Array<Vector3>): Array<Vector3> {
        return affineVectorList(vectorList, scaleMatrix(scaling))
    }

    fun translate(translation: Vector3, vectorList: Array<Vector3>): Array<Vector3> {
        return affineVectorList(vectorList, translationMatrix(translation))
    }

    fun shear(shearX: Float, shearY: Float, vectorList: Array<Vector3>): Array<Vector3> {
        return affineVectorList(vectorList, shearMatrix(shearX, shearY))
    }

    fun quaternionRotate(
        degrees: Float,
        axis: Vector3,
        vectorList: Array<Vector3>
    ): Array<Vector3> {
        return affineVectorList(vectorList, quaternionMatrix(degrees, axis))
    }

}

// UTILITY TO OPERATE FASTER
fun Array<Vector3>.translate(translation: Vector3) =
    AffineTransformation.translate(translation, this)

fun Array<Vector3>.scale(scaling: Vector3) =
    AffineTransformation.scale(scaling, this)

fun Array<Vector3>.rotate2D(degrees: Float) =
    AffineTransformation.rotate2D(degrees, this)

fun Array<Vector3>.shear(shearX: Float, shearY: Float) =
    AffineTransformation.shear(shearX, shearY, this)

fun Array<Vector3>.rotate3DX(degrees: Float) =
    AffineTransformation.rotate3DX(degrees, this)

fun Array<Vector3>.rotate3DY(degrees: Float) =
    AffineTransformation.rotate3DY(degrees, this)

fun Array<Vector3>.rotate3DZ(degrees: Float) =
    AffineTransformation.rotate3DZ(degrees, this)

fun Array<Vector3>.quaternionRotate(degrees: Float, axis: Vector3) =
    AffineTransformation.quaternionRotate(degrees, axis, this)