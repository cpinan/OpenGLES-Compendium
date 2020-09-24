package com.carlospinan.androidgraphicswithopengles.week1

import android.content.Context
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLRenderer
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLSurfaceView

/**
 * @author Carlos Piñan
 */
class TriangleGLSurfaceView(
    context: Context
) : BaseGLSurfaceView(context) {

    override fun setupRenderer(): BaseGLRenderer {
        return TriangleGLRenderer(context)
    }

}