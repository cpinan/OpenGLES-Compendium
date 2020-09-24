package com.carlospinan.introductiontoandroidgraphics.week2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carlospinan.introductiontoandroidgraphics.week2.views.CubeCanvasView

/**
 * @author Carlos Piñan
 */
class CubeCanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CubeCanvasView(this))
    }

}
