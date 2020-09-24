package com.carlospinan.openglesexamples.common

import android.content.Context
import android.opengl.GLES32
import android.os.SystemClock
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader

private const val TAG = "GLError"

/**
 *  v = vertices
 *  vt = textures
 *  vn = normals
 *  f = faces
 *
 *  https://stackoverflow.com/questions/41012719/how-to-load-and-display-obj-file-in-android-with-opengl-es-2
 */
class ObjParser(
    val id: Long,
    val name: String,
    val vertex: FloatArray,
    val order: IntArray
)

// Validated with Blend model. Check raw/torus.obj
fun parseObjFile(context: Context, resourceId: Int): ObjParser {
    val content = readResource(context, resourceId)
    val array = content.split("\n")

    var name = "Unnamed"
    val parsedVertex = mutableListOf<Float>()
    val parsedOrder = mutableListOf<Int>()

    for (line in array) {
        if (line.isNotEmpty()) {

            when (line[0]) {
                'o' -> {
                    // Name of the object.
                    name = line.substring(1).trim()
                }
                // TODO Refactor this to reuse line.substring...
                'v' -> {
                    // Vertices of the object.
                    line.substring(1).trim().split(" ")
                        .forEach {
                            parsedVertex.add(it.toFloat())
                        }
                }
                'f' -> {
                    // TODO This one could be more complex, for now just taking basic triangles, and order.
                    // Faces of the object. Used for order
                    line.substring(1).trim().split(" ")
                        .forEach {
                            parsedOrder.add(it.toInt() - 1)
                        }
                }
            }
        }
    }

    if (parsedVertex.isEmpty() || parsedOrder.isEmpty()) {
        throw Exception("Empty arrays -> ${parsedVertex.size} ; ${parsedOrder.size}")
    }

    val id = SystemClock.uptimeMillis()
    val vertex = FloatArray(parsedVertex.size)
    val order = IntArray(parsedOrder.size)

    var vertexIndex = 0
    var orderIndex = 0

    for (vertexValue in parsedVertex) {
        vertex[vertexIndex++] = vertexValue
    }

    for (orderValue in parsedOrder) {
        order[orderIndex++] = orderValue
    }

    return ObjParser(
        id = id,
        name = name,
        vertex = vertex,
        order = order
    )
}


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