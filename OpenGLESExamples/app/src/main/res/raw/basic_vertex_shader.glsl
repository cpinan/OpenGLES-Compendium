attribute vec3 aVertexPosition;

uniform mat4 uMVPMatrix;

void main() {
    gl_Position = uMVPMatrix * vec4(aVertexPosition, 1);
}