precision mediump float;

varying vec2 vTextureCoordinate;

uniform sampler2D uTextureSampler;

void main() {
    gl_FragColor = texture2D(uTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.t));
}