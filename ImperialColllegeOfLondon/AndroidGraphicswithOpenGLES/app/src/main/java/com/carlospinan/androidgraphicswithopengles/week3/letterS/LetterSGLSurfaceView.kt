package com.carlospinan.androidgraphicswithopengles.week3.letterS

import android.content.Context
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLRenderer
import com.carlospinan.androidgraphicswithopengles.common.views.BaseGLSurfaceView

/**
 * @author Carlos Piñan
 */
class LetterSGLSurfaceView(
    context: Context
) : BaseGLSurfaceView(context) {

    override fun setupRenderer(): BaseGLRenderer {
        return LetterSGLRenderer(context)
    }

}