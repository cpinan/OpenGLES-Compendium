attribute vec3 aVertexPosition;
uniform mat4 uMVPMatrix;
varying vec4 vColor;

void main() {
    gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);
    vColor = vec4(1.0, 0.0, 0.0, 1.0);
}