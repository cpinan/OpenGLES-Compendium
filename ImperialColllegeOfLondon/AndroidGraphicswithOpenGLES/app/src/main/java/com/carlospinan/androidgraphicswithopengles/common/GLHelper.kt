package com.carlospinan.androidgraphicswithopengles.common

import android.content.Context
import android.opengl.GLES32
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader

private const val TAG = "GLError"

/**
 * @author Carlos Piñan
 */
fun checkGLError(glOperation: String) {
    var error: Int
    if (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
        Log.e(TAG, "$glOperation\nglError $error")
    }
}

fun loadShader(context: Context, shaderType: Int, resourceId: Int): Int {
    return loadShader(shaderType, readResource(context, resourceId))
}

fun loadShader(shaderType: Int, shaderCode: String): Int {
    val shader = GLES32.glCreateShader(shaderType)
    GLES32.glShaderSource(shader, shaderCode)
    GLES32.glCompileShader(shader)
    checkGLError("Error loading shader: shaderType = $shaderType - shaderCode\n$shaderCode")

    // Get the compilation status.
    val compileStatus = IntArray(1)
    GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compileStatus, 0)

    // If the compilation failed, delete the shader.
    if (compileStatus[0] == 0) {
        Log.e(TAG, "Error compiling shader: " + GLES32.glGetShaderInfoLog(shader))
        GLES32.glDeleteShader(shader)
    }
    return shader
}

fun parseObjFile(context: Context, resourceId: Int) {
    val vertex = floatArrayOf()
    val faces = intArrayOf()
    // v = vertices
    // vt = textures
    // vn = normals
    // f = faces
}

fun readResource(context: Context, resourceId: Int): String {
    val body = StringBuilder()
    context.apply {
        val stream = resources.openRawResource(resourceId)
        val inputStreamReader = InputStreamReader(stream)
        val bufferedReader = BufferedReader(inputStreamReader as Reader?)
        var newLine = bufferedReader.readLine()
        while (newLine != null) {
            body.append(newLine).append("\n")
            newLine = bufferedReader.readLine()
        }
    }
    return body.toString()
}

fun createProgram(
    context: Context,
    vertexShaderRes: Int,
    fragmentShaderRes: Int,
    log: String = ""
): Int {
    val vertexShader = loadShader(context, GLES32.GL_VERTEX_SHADER, vertexShaderRes)
    val fragmentShader = loadShader(context, GLES32.GL_FRAGMENT_SHADER, fragmentShaderRes)

    return GLES32.glCreateProgram()
        .also {
            GLES32.glAttachShader(it, vertexShader)
            checkGLError("GLHelper.kt attaching vertexShader = $vertexShader")

            GLES32.glAttachShader(it, fragmentShader)
            checkGLError("GLHelper.kt attaching fragmentShader = $fragmentShader")

            GLES32.glLinkProgram(it)
            checkGLError("GLHelper.kt linking program $it")

            // Get the link status.
            val linkStatus = IntArray(1)
            GLES32.glGetProgramiv(it, GLES32.GL_LINK_STATUS, linkStatus, 0)

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error compiling program: " + GLES32.glGetProgramInfoLog(it))
                GLES32.glDeleteProgram(it)
            }

            if (log.isNotEmpty()) {
                checkGLError(log)
            }
        }
}

fun degreesToRadians(degrees: Float) = (degrees * Math.PI / 180F).toFloat()