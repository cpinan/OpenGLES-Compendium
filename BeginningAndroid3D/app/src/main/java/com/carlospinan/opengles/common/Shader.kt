package com.carlospinan.opengles.common

import android.content.Context
import android.opengl.GLES32
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * @author Carlos Piñan
 */
class Shader(
    private val context: Context,
    private val vertexResourceId: Int,
    private val fragmentResourceId: Int
) {

    private var shaderProgram: Int = 0

    private var vertexShader: Int = 0
    private var fragmentShader: Int = 0

    init {
        startShaderProgram()
    }

    private fun startShaderProgram() {
        shaderProgram = GLES32.glCreateProgram()
        GLCommon.checkGLError("Error creating program Shader.kt-31")

        startVertexShader()
        startFragmentShader()

        GLES32.glLinkProgram(shaderProgram)
        GLCommon.checkGLError("Error Linking Program in Shader.kt-55")

        val debugInfo = GLES32.glGetProgramInfoLog(shaderProgram)
        Log.d("DEBUG", debugInfo)
    }

    private fun startVertexShader() {
        val buffer = readInShader(vertexResourceId)

        vertexShader = GLES32.glCreateShader(GLES32.GL_VERTEX_SHADER)
        GLES32.glShaderSource(vertexShader, buffer.toString())
        GLES32.glCompileShader(vertexShader)

        GLES32.glAttachShader(shaderProgram, vertexShader)
    }

    private fun startFragmentShader() {
        val buffer = readInShader(fragmentResourceId)

        fragmentShader = GLES32.glCreateShader(GLES32.GL_FRAGMENT_SHADER)
        GLES32.glShaderSource(fragmentShader, buffer.toString())
        GLES32.glCompileShader(fragmentShader)

        GLES32.glAttachShader(shaderProgram, fragmentShader)
    }

    private fun readInShader(resourceId: Int): StringBuffer {
        val stringBuffer = StringBuffer()
        val inputStream = context.resources.openRawResource(resourceId)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        try {
            var read: String? = bufferedReader.readLine()
            while (read != null) {
                stringBuffer.append(read).append("\n")
                read = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error in ReadInShader(): " + e.localizedMessage)
        }
        return stringBuffer
    }

    fun getShaderUniformVariableLocation(value: String) =
        GLES32.glGetUniformLocation(shaderProgram, value)

    fun getShaderVertexAttributeVariableLocation(value: String): Int {
        val result = GLES32.glGetAttribLocation(shaderProgram, value)
        GLCommon.checkGLError("Error Shader.kt-110 in '$value' - $result")
        return result
    }

    fun activateShader() {
        GLES32.glUseProgram(shaderProgram)
        GLCommon.checkGLError("Error Shader.kt-166 in shader - $shaderProgram")
    }

    fun deactivateShader() {
        // Revert back to Fixed Function Rendering Pipeline
        GLES32.glUseProgram(0)
    }

    fun setShaderUniformVariableValue(key: String, value: Vector3) {
        val location = GLES32.glGetUniformLocation(shaderProgram, key)
        GLES32.glUniform3f(location, value.x, value.y, value.z)
        GLCommon.checkGLError("Error Shader.kt-126 in $key")
    }

    fun setShaderUniformVariableValue(key: String, value: Int) {
        val location = GLES32.glGetUniformLocation(shaderProgram, key)
        GLES32.glUniform1i(location, value)
        GLCommon.checkGLError("Error Shader.kt-132 in $key")
    }

    fun setShaderUniformVariableValue(key: String, value: Float) {
        val location = GLES32.glGetUniformLocation(shaderProgram, key)
        GLES32.glUniform1f(location, value)
        GLCommon.checkGLError("Error Shader.kt-138 in $key")
    }

    fun setShaderVariableValueFloatMatrix4Array(
        key: String,
        count: Int,
        transpose: Boolean,
        value: FloatArray,
        offset: Int
    ) {
        val location = GLES32.glGetUniformLocation(shaderProgram, key)
        //public static void glUniformMatrix4fv (int location, int count, boolean transpose, float[] value, int offset)
        GLES32.glUniformMatrix4fv(location, count, transpose, value, offset)
        GLCommon.checkGLError("Error Shader.kt-144 in $key")
    }

    fun setShaderVariableValueFloatMatrix3Array(
        key: String,
        count: Int,
        transpose: Boolean,
        value: FloatArray,
        offset: Int
    ) {
        val location = GLES32.glGetUniformLocation(shaderProgram, key)
        GLES32.glUniformMatrix3fv(location, count, transpose, value, offset)
    }

}