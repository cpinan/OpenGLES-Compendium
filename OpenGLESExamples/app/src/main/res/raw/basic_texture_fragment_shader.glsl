precision mediump float;

uniform sampler2D uTexture;

varying vec2 vTextureCoordinate;
varying vec3 vPosition;
varying vec4 vColor;

void main() {
    // Multiply the color by the diffuse illumination level and texture value to get final output color.
    vec4 ambientColor = vColor * vec4(0.8, 0.9, 0.9, 1.0);
    gl_FragColor = (ambientColor * texture2D(uTexture, vTextureCoordinate));

}