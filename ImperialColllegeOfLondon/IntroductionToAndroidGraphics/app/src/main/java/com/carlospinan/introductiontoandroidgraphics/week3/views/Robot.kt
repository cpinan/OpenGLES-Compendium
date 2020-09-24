package com.carlospinan.introductiontoandroidgraphics.week3.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.carlospinan.introductiontoandroidgraphics.common.Vector3
import com.carlospinan.introductiontoandroidgraphics.common.objects.Cube
import com.carlospinan.introductiontoandroidgraphics.common.quaternionRotate
import com.carlospinan.introductiontoandroidgraphics.common.scale
import com.carlospinan.introductiontoandroidgraphics.common.translate

/**
 * @author Carlos Piñan
 */
class Robot {

    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
        strokeWidth = 2F
    }

    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
        strokeWidth = 2F
    }

    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
        strokeWidth = 2F
    }

    private val cyanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.CYAN
        strokeWidth = 2F
    }

    private val headCuboid = Cube(bluePaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(6F, 2.12F, 5F))
                .scale(Vector3(80, 80, 80))
        )
    }

    private val neckCuboid = Cube(redPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(19.2F, 11F, 16F))
                .scale(Vector3(25, 25, 25))
        )
    }

    private val bodyCuboid = Cube(redPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(3F, 2.5F, 5F))
                .scale(Vector3(160, 200, 80))
        )
    }

    // FROM HERE
    private val rightArm1Cuboid = Cube(bluePaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(5.4F, 5F, 5F))
                .scale(Vector3(50, 75, 80))
        )
    }

    private val rightArm2Cuboid = Cube(greenPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(5.4F, 5.5F, 5F))
                .scale(Vector3(50, 100, 80))
        )
    }

    private val rightArm3Cuboid = Cube(bluePaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(5.4F, 22F, 3F))
                .scale(Vector3(50, 31, 120))
        )
    }

    private val leftArm1Cuboid = Cube(bluePaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(13.8F, 5F, 5F))
                .scale(Vector3(50, 75, 80))
        )
    }

    private val leftArm2Cuboid = Cube(greenPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(13.8F, 5.5F, 5F))
                .scale(Vector3(50, 100, 80))
        )
    }

    private val leftArm3Cuboid = Cube(cyanPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(13.8F, 22F, 3F))
                .scale(Vector3(50, 31, 120))
        )
    }

    private val waistCuboid = Cube(redPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(3F, 15F, 5F))
                .scale(Vector3(160, 50, 80))
        )
    }

    private val rightLeg1Cuboid = Cube(bluePaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(7.4F, 9F, 5F))
                .scale(Vector3(50, 100, 80))
        )
    }

    private val rightLeg2Cuboid = Cube(greenPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(7.4F, 9F, 5F))
                .scale(Vector3(50, 125, 80))
        )
    }

    private val rightLeg3Cuboid = Cube(redPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(7.4F, 41.35F, 3F))
                .scale(Vector3(50, 31, 120))
        )
    }

    private val leftLeg1Cuboid = Cube(bluePaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(11.8F, 9F, 5F))
                .scale(Vector3(50, 100, 80))
        )
    }

    private val leftLeg2Cuboid = Cube(greenPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(11.8f, 9F, 5F))
                .scale(Vector3(50, 125, 80))
        )
    }

    private val leftLeg3Cuboid = Cube(redPaint).apply {
        setMatrix(
            baseCubeVertices
                .translate(Vector3(11.8f, 41.35F, 3F))
                .scale(Vector3(50, 31, 120))
        )
    }

    fun draw(canvas: Canvas) {
        headCuboid.draw(canvas)
        neckCuboid.draw(canvas)

        bodyCuboid.draw(canvas)

        rightArm1Cuboid.draw(canvas)
        rightArm2Cuboid.draw(canvas)
        rightArm3Cuboid.draw(canvas)

        leftArm1Cuboid.draw(canvas)
        leftArm2Cuboid.draw(canvas)
        leftArm3Cuboid.draw(canvas)

        waistCuboid.draw(canvas)

        rightLeg1Cuboid.draw(canvas)
        rightLeg2Cuboid.draw(canvas)
        rightLeg3Cuboid.draw(canvas)

        leftLeg1Cuboid.draw(canvas)
        leftLeg2Cuboid.draw(canvas)
        leftLeg3Cuboid.draw(canvas)
    }

    fun rotate(degreesX: Float, degreesY: Float, degreesZ: Float) {
        applyQuaternion(headCuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(neckCuboid, degreesX, degreesY, degreesZ)

        applyQuaternion(bodyCuboid, degreesX, degreesY, degreesZ)

        applyQuaternion(rightArm1Cuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(rightArm2Cuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(rightArm3Cuboid, degreesX, degreesY, degreesZ)

        applyQuaternion(leftArm1Cuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(leftArm2Cuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(leftArm3Cuboid, degreesX, degreesY, degreesZ)

        applyQuaternion(waistCuboid, degreesX, degreesY, degreesZ)

        applyQuaternion(rightLeg1Cuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(rightLeg2Cuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(rightLeg3Cuboid, degreesX, degreesY, degreesZ)

        applyQuaternion(leftLeg1Cuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(leftLeg2Cuboid, degreesX, degreesY, degreesZ)
        applyQuaternion(leftLeg3Cuboid, degreesX, degreesY, degreesZ)
    }

    private fun applyQuaternion(
        cube: Cube,
        degreesX: Float, degreesY: Float, degreesZ: Float
    ) {
        with(cube) {
            updateMatrix(
                baseCubeVertices
                    .quaternionRotate(degreesX, Vector3(1F, 0F, 0F))
                    .quaternionRotate(degreesY, Vector3(0F, 1F, 0F))
                    .quaternionRotate(degreesZ, Vector3(0F, 0F, 1F))
            )
        }
    }

}