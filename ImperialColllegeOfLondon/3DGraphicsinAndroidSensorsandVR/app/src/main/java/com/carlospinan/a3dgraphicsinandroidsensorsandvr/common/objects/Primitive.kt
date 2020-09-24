package com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.objects

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.checkGLError
import com.carlospinan.a3dgraphicsinandroidsensorsandvr.common.createProgram

const val FLOAT_SIZE = 4 // Bytes per float

/**
 * @author Carlos Piñan
 */
abstract class Primitive(
    private val context: Context,
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
    }

    abstract fun setUp()

    abstract fun draw(modelViewProjectionMatrix: FloatArray)

    protected fun loadTexture(resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES32.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options().apply {
                inScaled = false
            }
            val bitmap = BitmapFactory.decodeResource(
                context.resources,
                resourceId,
                options
            )

            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureHandle[0])
            checkGLError("glBindTexture $textureHandle")

            //set filtering
            GLES32.glTexParameteri(
                GLES32.GL_TEXTURE_2D,
                GLES32.GL_TEXTURE_MIN_FILTER,
                GLES32.GL_NEAREST
            )
            checkGLError("glTexParameteri - GL_TEXTURE_MIN_FILTER")

            GLES32.glTexParameteri(
                GLES32.GL_TEXTURE_2D,
                GLES32.GL_TEXTURE_MAG_FILTER,
                GLES32.GL_NEAREST
            )
            checkGLError("glTexParameteri - GL_TEXTURE_MAG_FILTER")

            //load bitmap into bound texture
            GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
            checkGLError("GLUtils - texImage2D")

            bitmap.recycle()

        } else {
            checkGLError("Error loading texture $resourceId")
        }
        return textureHandle[0]
    }

}