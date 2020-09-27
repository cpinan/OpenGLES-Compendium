package com.carlospinan.advancedandroidappdevelopmentcapstone.week1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author Carlos Piñan
 * @case https://www.coursera.org/learn/aada-capstone/supplement/0hi9s/guidelines-for-assignment-1
 */
class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GalleryView(this))
    }

}