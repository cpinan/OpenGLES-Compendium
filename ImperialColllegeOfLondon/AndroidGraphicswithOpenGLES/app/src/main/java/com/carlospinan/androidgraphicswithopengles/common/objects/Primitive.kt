package com.carlospinan.androidgraphicswithopengles.common.objects

import android.content.Context
import com.carlospinan.androidgraphicswithopengles.common.createProgram

const val FLOAT_SIZE = 4

/**
 * @author Carlos Piñan
 */
abstract class Primitive(
    context: Context,
    vertexResourceId: Int,
    fragmentResourceId: Int
) {

    protected var program = 0
        private set

    init {
        program = createProgram(
            context,
            vertexResourceId,
            fragmentResourceId,
            "program has failed in Primitive.kt"
        )

        setUp()
    }

    abstract fun setUp()

    abstract fun draw(modelViewProjectionMatrix: FloatArray)

}