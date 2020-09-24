attribute vec3 aVertexPosition;
attribute vec4 aVertexColor;

uniform mat4 uMVPMatrix;
varying vec4 vColor;

void main() {
    gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);
    vColor = aVertexColor;
}