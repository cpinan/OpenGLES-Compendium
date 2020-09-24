precision mediump float;

varying vec2 vTextureCoordinate;

uniform sampler2D uTextureSampler;

void main() {
    vec4 fragmentColor = texture2D(uTextureSampler, vec2(vTextureCoordinate.s, vTextureCoordinate.t));
    gl_FragColor = vec4(fragmentColor.rgb, fragmentColor.a);
}