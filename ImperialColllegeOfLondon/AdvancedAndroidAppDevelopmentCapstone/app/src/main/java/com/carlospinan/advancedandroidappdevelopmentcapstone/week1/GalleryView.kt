package com.carlospinan.advancedandroidappdevelopmentcapstone.week1

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import com.carlospinan.advancedandroidappdevelopmentcapstone.common.*
import java.util.*
import kotlin.math.max
import kotlin.math.min

private const val SCALE_FACTOR = 0.5F

/**
 * @author Carlos Piñan
 */
class GalleryView(context: Context) : View(context) {

    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()

    private val minSize = min(screenWidth, screenHeight)
    private val ratio = screenWidth / screenHeight
    private val scale = ratio * minSize / 4F

    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            strokeWidth = 5F
            color = 0xFF00FF00.toInt()
            style = Paint.Style.STROKE
        }

    private val blackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            strokeWidth = 5F
            color = 0xFF000000.toInt()
            style = Paint.Style.STROKE
        }

    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.STROKE
            color = 0xFF0000FF.toInt()
            strokeWidth = 5F
        }

    private val magentaPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.STROKE
            color = Color.MAGENTA
            strokeWidth = 5F
        }

    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.STROKE
            color = 0xFFFF0000.toInt()
            strokeWidth = 5F
        }

    private val blueVioletPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.STROKE
            color = 0xFF8A2BE2.toInt()
            strokeWidth = 5F
        }

    private val borderFrontOffset = 0
    private val borderBackOffset = 12
    private val innerFrontOffset = 24
    private val innerBackOffset = 36

    private var angleZ = 0F
    private val galleryPoints = mutableListOf<Vector3>()

    private lateinit var drawGallery: Array<Vector3>
    private lateinit var drawPlotPoints: Array<Vector3>

    private lateinit var plotPoints: Array<Vector3>

    private lateinit var drawPortraits: Array<GalleryData>
    private val portraits = mutableListOf<GalleryData>()

    init {
        portraits.add(frame01)
        portraits.add(frame02)

        galleryPoints.addAll(galleryBorderPointsFront)
        galleryPoints.addAll(galleryBorderPointsBack)
        galleryPoints.addAll(galleryInnerPointsFront)
        galleryPoints.addAll(galleryInnerPointsBack)

        setupGraphData()
        applyTransformations()

        // INFINITE ROTATION
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                applyTransformations()

                angleZ++
                angleZ %= 360F

                postInvalidate()
            }
        }
        timer.scheduleAtFixedRate(timerTask, 100L, 80L)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawColor(0xFFFFFF)
        drawGallery(canvas)
    }

    private fun drawGallery(canvas: Canvas) {
        // CONNECT FRONT BORDER
        for (i in 0 until 12) {
            canvas.drawVectors(drawGallery[i % 12], drawGallery[(i + 1) % 12], redPaint)
        }

        // CONNECT BACK BORDER
        for (i in 0 until 12) {
            canvas.drawVectors(
                drawGallery[borderBackOffset + i % 12],
                drawGallery[borderBackOffset + (i + 1) % 12],
                blackPaint
            )
        }

        // CONNECT FRONT WITH BACK
        for (i in 0..11) {
            // Skip intermediate points
            if (!skipBorderFrontPoints.contains(i)) {
                canvas.drawVectors(drawGallery[i], drawGallery[borderBackOffset + i], greenPaint)
            }
        }

        // DRAW INNER FRONT POINTS
        drawInnerPoints(canvas, innerFrontOffset, borderFrontOffset, bluePaint)

        // DRAW INNER BACK POINTS
        drawInnerPoints(canvas, innerBackOffset, borderBackOffset, magentaPaint)

        // CONNECT INNER POINTS
        for (i in 0..11) {
            canvas.drawVectors(
                drawGallery[innerFrontOffset + i],
                drawGallery[innerBackOffset + i],
                blueVioletPaint
            )
        }


        // DRAW PLOT DATA
        val plotPath = Path()
        plotPath.moveTo(drawPlotPoints[0].x, drawPlotPoints[0].y)
        for (i in 1 until plotPoints.size - 4) {
            plotPath.lineTo(drawPlotPoints[i].x, drawPlotPoints[i].y)
        }
        canvas.drawPath(plotPath, greenPaint)

        // DRAW PLOT RECTANGLE
        val index = plotPoints.size - 4

        // Bottom left to bottom right
        canvas.drawVectors(
            drawPlotPoints[index],
            drawPlotPoints[index + 1],
            greenPaint
        )

        // Bottom right to top right
        canvas.drawVectors(
            drawPlotPoints[index + 1],
            drawPlotPoints[index + 2],
            greenPaint
        )

        // Top right to top left
        canvas.drawVectors(
            drawPlotPoints[index + 2],
            drawPlotPoints[index + 3],
            greenPaint
        )

        // Top left to bottom left
        canvas.drawVectors(
            drawPlotPoints[index + 3],
            drawPlotPoints[index],
            greenPaint
        )

        // DRAW FRAMES
        for (data in drawPortraits) {
            for (i in data.portraitInfo.indices) {
                drawFrame(canvas, data.portraitInfo[i], data.paint[i])
            }
        }
    }

    private fun drawFrame(canvas: Canvas, array: Array<Vector3>, paint: Paint) {
        val path = Path()
        path.moveTo(array[0].x, array[0].y)

        val size = array.size
        for (i in 1 until size) {
            path.lineTo(array[i % size].x, array[i % size].y)
        }
        path.close()

        canvas.drawPath(path, paint)
    }

    private fun drawInnerPoints(canvas: Canvas, offsetA: Int, offsetB: Int, paint: Paint) {
        canvas.drawVectors(
            drawGallery[offsetA],
            drawGallery[offsetB + 1],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 1],
            drawGallery[offsetB + 2],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 2],
            drawGallery[offsetB + 11],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 3],
            drawGallery[offsetB + 4],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 4],
            drawGallery[offsetB + 10],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 5],
            drawGallery[offsetB + 5],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 6],
            drawGallery[offsetB + 8],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 7],
            drawGallery[offsetB + 7],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 8],
            drawGallery[offsetA + 9],
            paint
        )

        canvas.drawVectors(
            drawGallery[offsetA + 10],
            drawGallery[offsetA + 11],
            paint
        )
    }

    private fun applyTransformations() {
        drawGallery = applyTransformation(galleryPoints.toTypedArray())
        drawPlotPoints = applyTransformation(plotPoints)
        transformFrames()
    }

    private fun transformFrames() {
        val parsedPortraits = mutableListOf<GalleryData>()
        for (data in portraits) {
            val array = mutableListOf<Array<Vector3>>()
            for (info in data.portraitInfo) {
                array.add(applyTransformation(info))
            }
            val newGalleryData = GalleryData(
                portraitInfo = array.toTypedArray(),
                paint = data.paint
            )
            parsedPortraits.add(newGalleryData)
        }
        drawPortraits = parsedPortraits.toTypedArray()
    }

    private fun applyTransformation(source: Array<Vector3>): Array<Vector3> {
        return source
            .scale(Vector3(scale * SCALE_FACTOR, scale * SCALE_FACTOR, scale * SCALE_FACTOR * 0.8F))
            .quaternionRotate(45F, Vector3(0F, 1F, 0F))
            .quaternionRotate(30F, Vector3(1F, 0F, 0F))
            .quaternionRotate(angleZ, Vector3(0F, 0F, 1F))
            .translate(Vector3(width / 2, height / 2, 50))
    }

    private fun setupGraphData() {
        var minValue = Float.MAX_VALUE
        var maxValue = Float.MIN_VALUE

        val left = -1F
        val right = 1F
        val y = -3F

        val increaseFactor = (right - left) / plotData.size.toFloat()
        var value = left

        val array = arrayListOf<Vector3>()

        for (i in plotData.indices) {
            array.add(Vector3(value, y, plotData[i].toFloat()))
            value += increaseFactor
            minValue = min(minValue, plotData[i].toFloat())
            maxValue = max(maxValue, plotData[i].toFloat())
        }

        val difference = (maxValue - minValue)

        // between z -1 to z 1
        for (i in array.indices) {
            array[i].z = 2F * ((array[i].z - minValue) / difference) - 1F
        }

        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE

        var minZ = Float.MAX_VALUE
        var maxZ = Float.MIN_VALUE

        for (i in array.indices) {
            minX = min(minX, array[i].x)
            maxX = max(maxX, array[i].x)

            minZ = min(minZ, array[i].z)
            maxZ = max(maxZ, array[i].z)
        }

        // Add borders at the end
        array.add(Vector3(minX, y, minZ)) // Bottom Left
        array.add(Vector3(maxX, y, minZ)) // Bottom Right

        array.add(Vector3(maxX, y, maxZ)) // Top Right
        array.add(Vector3(minX, y, maxZ)) // Top Left

        plotPoints = array.toTypedArray()

    }

}