package com.carlospinan.androidgraphicswithopengles.week2.halfcone

import android.content.Context
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLRenderer
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLSurfaceView

/**
 * @author Carlos Piñan
 */
class HalfConeGLSurfaceView(
    context: Context
) : BaseGLSurfaceView(context) {

    override fun setupRenderer(): BaseGLRenderer {
        return HalfConeGLRenderer(context)
    }

}