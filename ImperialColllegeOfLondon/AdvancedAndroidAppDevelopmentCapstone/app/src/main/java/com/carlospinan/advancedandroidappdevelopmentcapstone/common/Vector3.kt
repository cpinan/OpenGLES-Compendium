package com.carlospinan.advancedandroidappdevelopmentcapstone.common

import kotlin.math.sqrt

/**
 * @author Carlos Piñan
 */
data class Vector3(
    val x: Float,
    val y: Float,
    var z: Float = 1F,
    val w: Float = 1F
) {

    constructor(x: Int, y: Int, z: Int, w: Float = 1F)
            : this(x.toFloat(), y.toFloat(), z.toFloat(), w)

    companion object {

        fun dotProduct(v1: Vector3, v2: Vector3): Float {
            return v1.x * v2.x +
                    v1.y * v2.y +
                    v1.z * v2.z
        }

        fun crossProduct(v1: Vector3, v2: Vector3): Vector3 {
            return Vector3(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.z
            )
        }

    }

    fun squareLength(): Float {
        return x * x + y * y + z * z
    }

    fun length() = sqrt(squareLength())

    fun normalizeWithW(): Vector3 {
        var newX = x
        var newY = y
        var newZ = z
        if (w != 0F) {
            newX /= w
            newY /= w
            newZ /= w
        }
        return Vector3(newX, newY, newZ, 1F)
    }

    fun normalize(): Vector3 {
        val length = length()
        return Vector3(
            x / length,
            y / length,
            z / length,
            1F
        )
    }

    operator fun plus(vector: Vector3): Vector3 {
        return Vector3(
            x + vector.x,
            y + vector.y,
            z + vector.z,
            (w + vector.w) / 2f
        )
    }

    operator fun minus(vector: Vector3): Vector3 {
        return Vector3(
            x - vector.x,
            y - vector.y,
            z - vector.z,
            (w + vector.w) / 2f
        )
    }

    operator fun times(value: Float): Vector3 {
        return Vector3(
            x * value,
            y * value,
            z * value,
            w
        )
    }

    operator fun div(vector: Vector3): Vector3 {
        return Vector3(
            x / vector.x,
            y / vector.y,
            z / vector.z,
            (w + vector.w) / 2f
        )
    }

    operator fun unaryMinus(): Vector3 {
        return Vector3(
            -x,
            -y,
            -z,
            w
        )
    }

}