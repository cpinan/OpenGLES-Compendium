package com.carlospinan.introductiontoandroidgraphics.week2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carlospinan.introductiontoandroidgraphics.week2.views.CubeCanvasTimer3View

/**
 * @author Carlos Piñan
 */
class CubeCanvasTimer3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CubeCanvasTimer3View(this))
    }

}