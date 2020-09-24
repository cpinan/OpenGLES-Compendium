package com.carlospinan.introductiontoandroidgraphics.week2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carlospinan.introductiontoandroidgraphics.week2.views.CubeCanvasTimer2View

/**
 * @author Carlos Piñan
 */
class CubeCanvasTimer2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CubeCanvasTimer2View(this))
    }

}