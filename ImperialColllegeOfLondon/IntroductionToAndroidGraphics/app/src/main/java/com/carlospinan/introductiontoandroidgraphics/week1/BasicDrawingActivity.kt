package com.carlospinan.introductiontoandroidgraphics.week1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carlospinan.introductiontoandroidgraphics.week1.views.BasicDrawingView

/**
 * @author Carlos Piñan
 */
class BasicDrawingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(BasicDrawingView(this))
    }

}