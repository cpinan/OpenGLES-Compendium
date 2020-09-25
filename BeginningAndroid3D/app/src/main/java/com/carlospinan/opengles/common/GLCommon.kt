package com.carlospinan.opengles.common

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLU
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader

/**
 * @author Carlos Piñan
 */

object GLCommon {

    fun checkGLError(glOperation: String) {
        var error: Int
        while (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
            val gluErrorString = GLU.gluErrorString(error)
            Log.e("ERROR", "$glOperation IN CHECKGLERROR() : glError - $gluErrorString")
            // throw RuntimeException("$glOperation: glError $error")
        }
    }

    fun Context.readTextFileFromResource(resourceId: Int): String {
        val body = StringBuilder()
        this.apply {
            val stream = resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(stream)
            val bufferedReader = BufferedReader(inputStreamReader as Reader?)
            var newLine: String? = bufferedReader.readLine()
            while (newLine != null) {
                body.append(newLine).append("\n")
                newLine = bufferedReader.readLine()
            }
        }
        return body.toString()
    }

}