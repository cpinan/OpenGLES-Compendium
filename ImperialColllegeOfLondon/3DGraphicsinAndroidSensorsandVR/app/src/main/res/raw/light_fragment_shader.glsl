precision mediump float;

varying vec4 vColor;
varying float vPointLightWeighting;

void main() {
    gl_FragColor = vec4(vColor.xyz * vPointLightWeighting, 1);
}