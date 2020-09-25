package com.carlospinan.opengles.androiddoc.objects

import android.opengl.GLES32

/**
 * @author Carlos Piñan
 */

fun loadShader(type: Int, shaderCode: String): Int {
    // create a vertex shader type (GLES32.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES32.GL_FRAGMENT_SHADER)
    return GLES32.glCreateShader(type).also { shader ->
        // add the source code to the shader and compile it
        GLES32.glShaderSource(shader, shaderCode)
        GLES32.glCompileShader(shader)

        // Get the compilation status.
        val compileStatus = IntArray(1)
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compileStatus, 0)

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            GLES32.glDeleteShader(shader)
        }
    }
}