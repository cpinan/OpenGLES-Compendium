package com.carlospinan.a3dgraphicsinandroidsensorsandvr.week3.binocular

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.ViewConfiguration
import android.view.WindowManager
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.views.BaseGLRenderer
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.views.BaseGLSurfaceView
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * @author Carlos Piñan
 */

// Scale factor for the touch motions
private const val TOUCH_SCALE_FACTOR = 180.0f / 320

// Zoom factor
private const val TOUCH_ZOOM_FACTOR = 1.0f / 320

class BinocularGLSurfaceView(
    context: Context
) : BaseGLSurfaceView(context), SensorEventListener {

    private lateinit var windowManager: WindowManager
    private lateinit var sensorManager: SensorManager
    private lateinit var rotationSensor: Sensor

    private var pointerCount = 0
    private var previousTouchX = 0F
    private var previousTouchY = 0F
    private lateinit var customRenderer: BinocularGLRenderer

    private var firstTouchX = 0F
    private var firstTouchY = 0F

    private var secondTouchX = 0F
    private var secondTouchY = 0F

    private var touchDistance = 0F
    private var viewScaledTouchSlope: Float = 0F

    override fun setupRenderer(): BaseGLRenderer {
        return BinocularGLRenderer(context)
    }

    override fun additionalSetup() {
        super.additionalSetup()

        val activity = context as Activity
        windowManager = activity.window.windowManager

        sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor: Sensor? in sensors) {
            Log.i("SENSOR", "Type is ${sensor?.name}")
        }

        viewScaledTouchSlope = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        renderMode = RENDERMODE_WHEN_DIRTY
        customRenderer = renderer as BinocularGLRenderer
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        when (e.action) {
            MotionEvent.ACTION_POINTER_UP or MotionEvent.ACTION_UP -> {
                pointerCount--
                if (pointerCount < -2) {
                    secondTouchX = -1F
                    secondTouchY = -1F
                }
                if (pointerCount < -1) {
                    secondTouchX = -1F
                    secondTouchY = -1F
                }
            }
            MotionEvent.ACTION_POINTER_DOWN or MotionEvent.ACTION_DOWN -> {
                pointerCount++
                if (pointerCount == 1) { // 1 finger
                    firstTouchX = e.getX(0)
                    firstTouchY = e.getY(0)
                } else if (pointerCount == 2) { // 2 finger
                    secondTouchX = e.getX(0)
                    secondTouchY = e.getY(0)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                secondTouchX = e.getX(0)
                secondTouchY = e.getY(0)

                if (isPinchGesture(e)) {
                    secondTouchX = e.getX(0)
                    secondTouchY = e.getY(0)
                    touchDistance = distance(e, 0, 1)

                    customRenderer.mZoom = touchDistance * TOUCH_ZOOM_FACTOR
                    requestRender()

                } else {
                    var dX = x - previousTouchX
                    var dY = y - previousTouchY

                    if (y > height / 2) {
                        dX *= -1
                    }
                    if (x < width / 2) {
                        dY *= -1
                    }

                    customRenderer.angleY += dX * TOUCH_SCALE_FACTOR
                    customRenderer.angleX += dY * TOUCH_SCALE_FACTOR
                    requestRender()
                }
            }
        }

        previousTouchX = x
        previousTouchY = y
        return true
    }

    private fun distance(
        e: MotionEvent,
        first: Int,
        second: Int
    ): Float {
        return if (e.pointerCount >= 2) {
            val x = e.getX(first) - e.getX(second)
            val y = e.getY(first) - e.getY(second)
            sqrt(x * x + y * y.toDouble()).toFloat() //Euclidean distance
        } else {
            0F
        }
    }

    private fun isPinchGesture(event: MotionEvent): Boolean { //check if it is a pinch gesture
        if (event.pointerCount == 2) { //multi-touch
            //check the distances between the touch locations
            val distanceCurrent = distance(event, 0, 1)
            val diffPrimX: Float = firstTouchX - event.getX(0)
            val diffPrimY: Float = firstTouchY - event.getY(0)
            val diffSecX: Float = secondTouchX - event.getX(1)
            val diffSecY: Float = secondTouchY - event.getY(1)
            if (abs(distanceCurrent - touchDistance) > viewScaledTouchSlope && diffPrimY * diffSecY <= 0 && diffPrimX * diffSecX <= 0) {
                //if the distance between the touch is above the threshold and the fingers are moving in opposing directions
                return true
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (rotationSensor != null) {
            sensorManager.registerListener(
                this,
                rotationSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == rotationSensor) {
            updateOrientation(event.values)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    private fun updateOrientation(rotationVector: FloatArray) {
        val rotationMatrix = FloatArray(9)

        SensorManager.getRotationMatrixFromVector(
            rotationMatrix,
            rotationVector
        )

        // Relative world axis with respect to the device's axis

        val (deviceRelativeAxisX, deviceRelativeAxisY) =
            when (windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_90 ->
                    Pair(SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X)
                Surface.ROTATION_180 ->
                    Pair(SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Z)
                Surface.ROTATION_270 ->
                    Pair(SensorManager.AXIS_MINUS_Z, SensorManager.AXIS_X)
                else -> // Or Surface.ROTATION_0
                    Pair(SensorManager.AXIS_X, SensorManager.AXIS_Z)
            }

        val adjustedRotationMatrix = FloatArray(9)

        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            deviceRelativeAxisX,
            deviceRelativeAxisY,
            adjustedRotationMatrix
        )

        val orientation = FloatArray(3)
        // Convert the rotation matrix into yaw (azimuth), pitch and roll

        SensorManager.getOrientation(
            adjustedRotationMatrix,
            orientation
        )

        val radianToDegree = -180F / Math.PI.toFloat()

        val yaw = orientation[0] * radianToDegree // x
        val pitch = orientation[1] * radianToDegree // y
        val roll = orientation[2] * radianToDegree // z

        customRenderer.yaw = yaw
        customRenderer.pitch = pitch
        customRenderer.roll = roll

        requestRender()

    }

}