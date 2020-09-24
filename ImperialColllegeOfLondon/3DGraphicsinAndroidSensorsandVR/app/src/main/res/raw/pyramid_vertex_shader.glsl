attribute vec3 aVertexPosition;
attribute vec2 aTextureCoordinate;

uniform mat4 uMVPMatrix;

varying vec2 vTextureCoordinate;

void main() {
    gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);
    vTextureCoordinate = aTextureCoordinate;
}