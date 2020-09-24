package com.carlospinan.androidgraphicswithopengles.week2.cube

import android.content.Context
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLRenderer
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLSurfaceView

/**
 * @author Carlos Piñan
 */
class CubeGLSurfaceView(
    context: Context
) : BaseGLSurfaceView(context) {

    override fun setupRenderer(): BaseGLRenderer {
        return CubeGLRenderer(context)
    }

}