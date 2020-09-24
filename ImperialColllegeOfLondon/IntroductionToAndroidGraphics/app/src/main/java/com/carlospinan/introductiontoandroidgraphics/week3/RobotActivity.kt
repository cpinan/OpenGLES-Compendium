package com.carlospinan.introductiontoandroidgraphics.week3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carlospinan.introductiontoandroidgraphics.week3.views.RobotView

/**
 * @author Carlos Piñan
 */
class RobotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(RobotView(this))
    }

}