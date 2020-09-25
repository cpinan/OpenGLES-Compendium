uniform mat4 uModelViewProjectionMatrix;
uniform mat4 uModelViewMatrix;

attribute vec4 aVertexPosition;
attribute vec4 aVertexColor;
attribute vec2 aTextureCoordinate; // s ; t

varying vec3 vPosition;
varying vec4 vColor;
varying vec2 vTextureCoordinate;

void main() {
    vTextureCoordinate = aTextureCoordinate;

    // Transform the vertex into eye space.
    vPosition = vec3(uModelViewMatrix * aVertexPosition);

    // Pass through the color.
    vColor = aVertexColor;

    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
    gl_Position = uModelViewProjectionMatrix * aVertexPosition;
}