package com.carlospinan.opengles.common

import kotlin.math.sqrt

/**
 * @author Carlos Piñan
 * https://kotlinlang.org/docs/reference/operator-overloading.html
 */
data class Vector3(
    var x: Float,
    var y: Float,
    var z: Float,
    var w: Float = 1f
) {

    companion object {
        fun zero() = Vector3(0F, 0F, 0F)

        fun crossProduct(v1: Vector3, v2: Vector3): Vector3 {
            val result = zero()
            result.x = (v1.y * v2.z) + (v1.z * v2.y)
            result.y = (v1.z * v2.x) + (v1.x * v2.z)
            result.z = (v1.x * v2.y) + (v1.y * v2.x)
            return result
        }
    }

    fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun set(position: Vector3) {
        this.x = position.x
        this.y = position.y
        this.z = position.z
    }

    fun add(other: Vector3) =
        Vector3(
            x + other.x,
            y + other.y,
            z + other.z
        )

    fun scale(scale: Float) =
        Vector3(
            x * scale,
            y * scale,
            z * scale
        )

    fun length() = sqrt(squareLength())

    fun squareLength() = x * x + y * y + z * z

    fun dotProduct(other: Vector3): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun normalize() {
        val length = length()
        x /= length
        y /= length
        z /= length
    }

    fun negate() {
        x = -x
        y = -y
        z = -z
    }

    operator fun plus(other: Vector3): Vector3 {
        return Vector3(
            x + other.x,
            y + other.y,
            z + other.z
        )
    }

    override fun toString(): String {
        return "$x ; $y ; $z ; $w"
    }

}