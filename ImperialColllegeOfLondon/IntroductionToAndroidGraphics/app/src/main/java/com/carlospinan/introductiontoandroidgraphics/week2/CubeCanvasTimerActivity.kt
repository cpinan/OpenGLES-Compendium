package com.carlospinan.introductiontoandroidgraphics.week2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carlospinan.introductiontoandroidgraphics.week2.views.CubeCanvasTimerView

/**
 * @author Carlos Piñan
 */
class CubeCanvasTimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CubeCanvasTimerView(this))
    }

}