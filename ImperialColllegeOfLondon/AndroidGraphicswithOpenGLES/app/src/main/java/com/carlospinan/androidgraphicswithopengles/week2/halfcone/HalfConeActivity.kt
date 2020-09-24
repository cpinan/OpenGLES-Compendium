package com.carlospinan.androidgraphicswithopengles.week2.halfcone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * @author Carlos Piñan
 */
class HalfConeActivity : AppCompatActivity() {

    private lateinit var view: HalfConeGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = HalfConeGLSurfaceView(this)
        setContentView(view)

        val uiOptions: Int = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        window.decorView.systemUiVisibility = uiOptions
    }

    override fun onPause() {
        super.onPause()
        if (::view.isInitialized) {
            view.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::view.isInitialized) {
            view.onResume()
        }
    }

}