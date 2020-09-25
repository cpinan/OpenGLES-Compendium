/**
 *
You can model lighting by dividing lighting into three separate types:

Ambient Lighting: This type of lighting models the lighting on an object that is
constant at all object vertices and does not depend on the position of the light.

Diffuse Lighting: This type of lighting models the lighting on an object that depends
on the angle that the object’s vertex normals make with the light source.

Specular Lighting: This type of lighting models the lighting on an object that
depends on the angle that the object’s vertex normals make with the light source
and also the position of the viewer or camera.

Light Vector: The vector that represents the direction from the object’s vertex to the
light source.
Normal Vector: The vector that is assigned to an object’s vertex that is used to
determine that vertex’s lighting from a diffuse light source. Normal vectors are also
vectors perpendicular to surfaces.
Eye Vector: The vector that represents the direction from the object’s vertex to the
location of the viewer or camera. This is used in specular lighting calculations.
Point Light: A light source that radiates light in all directions and has a position in
3D space. A point light contains ambient, diffuse, and specular lighting components.

 */
package com.carlospinan.opengles.common

/**
 * @author Carlos Piñan
 */
class PointLight {
    var ambientLight = Vector3(1F, 1F, 1F)
    var diffuseLight = Vector3(1F, 1F, 1F)
    var specularLight = Vector3(1F, 1F, 1F)
    var specularShininess = 5f
    var position: Vector3 = Vector3.zero()
}