attribute vec3 aVertexPosition;
attribute vec3 aVertexColor;

uniform mat4 uMVPMatrix;

varying vec3 vColor;

void main() {
    vColor = aVertexColor;
    gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);
}