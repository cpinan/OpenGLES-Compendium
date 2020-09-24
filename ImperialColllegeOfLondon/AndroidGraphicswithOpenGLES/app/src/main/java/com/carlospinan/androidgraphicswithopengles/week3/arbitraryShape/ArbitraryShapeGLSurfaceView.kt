package com.carlospinan.androidgraphicswithopengles.week3.arbitraryShape

import android.content.Context
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLRenderer
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLSurfaceView

/**
 * @author Carlos Piñan
 */
class ArbitraryShapeGLSurfaceView(
    context: Context
) : BaseGLSurfaceView(context) {

    override fun setupRenderer(): BaseGLRenderer {
        return ArbitraryShapeGLRenderer(context)
    }

}