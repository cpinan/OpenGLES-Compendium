package com.carlospinan.opengles.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils
import android.util.Log
import java.io.IOException

/**
 * @author Carlos Piñan
 */
class Texture(
    private val context: Context,
    private val resourceId: Int
) {

    companion object {
        fun setActiveTextureUnit(value: Int) {
            GLES32.glActiveTexture(value)
        }
    }

    var bitmap: Bitmap? = null
        private set

    private var textureId = 0

    init {
        initializeTexture()

        // Setup Default Texture Parameters
        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MIN_FILTER,
            GLES32.GL_NEAREST
        )
        GLCommon.checkGLError("Error Texture.kt line 39")

        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MAG_FILTER,
            GLES32.GL_LINEAR
        )
        GLCommon.checkGLError("Error Texture.kt line 45")

        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_S,
            GLES32.GL_CLAMP_TO_EDGE
        )
        GLCommon.checkGLError("Error Texture.kt line 52")

        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_T,
            GLES32.GL_CLAMP_TO_EDGE
        )
        GLCommon.checkGLError("Error Texture.kt line 59")
    }

    fun activateTexture() {
        if (textureId != 0) {
            GLES32.glBindTexture(
                GLES32.GL_TEXTURE_2D,
                textureId
            )
            GLCommon.checkGLError("Error Texture.kt line 69")
        } else {
            Log.e("ERROR", "- Texture ERROR- m_TextureId = 0 ; Error in ActivateTexture()! ")
        }
    }

    private fun initializeTexture(): Boolean {
        /*
		 * Android
		 * public static void glGenTextures (int n, int[] textures, int offset)
		 * Returns n currently unused names for texture objects in the array textures.
		 */
        val textures = IntArray(1)
        GLES32.glGenTextures(1, textures, 0)
        GLCommon.checkGLError("Error Texture.kt line 83")

        /*
		 * Android
		 * public static void glBindTexture (int target, int texture)
		 */
        textureId = textures[0]
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureId)
        GLCommon.checkGLError("Error Texture.kt line 91")

        // Loads in Texture from Resource File
        loadTexture()

        /*
		 * Android
		 * static void	 texImage2D(int target, int level, Bitmap bitmap, int border)
		 * A version of texImage2D that determines the internalFormat and type automatically.
		 */
        GLUtils.texImage2D(
            GLES32.GL_TEXTURE_2D,
            0,
            bitmap,
            0
        )
        GLCommon.checkGLError("Error Texture.kt line 107")

        return true
    }

    private fun loadTexture() {
        val inputStream = context.resources.openRawResource(resourceId)
        try {
            bitmap = BitmapFactory.decodeStream(inputStream)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                Log.e("ERROR - Texture ERROR", "Error in LoadTexture()! ")
            }
        }
    }

}