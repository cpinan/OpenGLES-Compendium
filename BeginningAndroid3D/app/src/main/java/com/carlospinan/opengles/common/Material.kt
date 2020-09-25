package com.carlospinan.opengles.common

/**
 * @author Carlos Piñan
 */
data class Material(
    var emissive: Vector3 = Vector3.zero(),
    var ambient: Vector3 = Vector3(1F, 1F, 1F),
    var diffuse: Vector3 = Vector3(1F, 1F, 1F),
    var specular: Vector3 = Vector3(1F, 1F, 1F),
    var specularShininess: Float = 5F,
    var alpha: Float = 1.0F
)