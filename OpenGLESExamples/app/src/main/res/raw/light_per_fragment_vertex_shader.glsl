uniform mat4 uModelViewProjectionMatrix;
uniform mat4 uModelViewMatrix;

attribute vec4 aVertexPosition;
attribute vec4 aVertexColor;
attribute vec3 aVertexNormal;

varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;

void main() {
    // Transform the vertex into eye space.
    vPosition = vec3(uModelViewMatrix * aVertexPosition);

    // Pass through the color.
    vColor = aColor;

    // Transform the normal's orientation into eye space.
    vNormal = vec3(uModelViewMatrix * vec4(aVertexNormal, 0.0));

    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
    gl_Position = uModelViewProjectionMatrix * aVertexPosition;
}