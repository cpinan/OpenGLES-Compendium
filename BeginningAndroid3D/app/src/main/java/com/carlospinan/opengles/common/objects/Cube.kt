package com.carlospinan.opengles.common.objects

import com.carlospinan.opengles.common.*

/**
 * @author Carlos Piñan
 */
class Cube(
    mesh: Mesh,
    textures: Array<Texture>,
    material: Material,
    shader: Shader
) : Object3d(
    mesh,
    textures,
    material,
    shader
) {

    companion object {
        val cubeData = floatArrayOf(
            // x,     y,    z,   u,       v		nx,  ny, nz
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, -1f, 1f, 1f,    // front top left      	0
            -0.5f, -0.5f, 0.5f, 0.0f, 1.0f, -1f, -1f, 1f,    // front bottom left   	1
            0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 1f, -1f, 1f,    // front bottom right	2
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 1f, 1f, 1f,  // front top right		3

            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1f, 1f, -1f, // back top left		4
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, -1f, -1f, -1f,    // back bottom left		5
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1f, -1f, -1f, // back bottom right	6
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 1f, 1f, -1f  // back top right    	7
        )

        val cubeDataNoTexture = floatArrayOf(
            // x,     y,    z,   	nx,  ny, nz
            -0.5f, 0.5f, 0.5f, -1f, 1f, 1f,    // front top left      	0
            -0.5f, -0.5f, 0.5f, -1f, -1f, 1f,    // front bottom left   	1
            0.5f, -0.5f, 0.5f, 1f, -1f, 1f,    // front bottom right	2
            0.5f, 0.5f, 0.5f, 1f, 1f, 1f,   // front top right		3

            -0.5f, 0.5f, -0.5f, -1f, 1f, -1f,  // back top left		4
            -0.5f, -0.5f, -0.5f, -1f, -1f, -1f,    // back bottom left		5
            0.5f, -0.5f, -0.5f, 1f, -1f, -1f,  // back bottom right	6
            0.5f, 0.5f, -0.5f, 1f, 1f, -1f   // back top right    	7
        )

        // order to draw vertices
        val cubeDrawOrder = shortArrayOf(
            0, 3, 1, 3, 2, 1,    // Front panel
            4, 7, 5, 7, 6, 5,   // Back panel
            4, 0, 5, 0, 1, 5,    // Side
            7, 3, 6, 3, 2, 6,    // Side
            4, 7, 0, 7, 3, 0,    // Top
            5, 6, 1, 6, 2, 1    // Bottom
        )
    }

}