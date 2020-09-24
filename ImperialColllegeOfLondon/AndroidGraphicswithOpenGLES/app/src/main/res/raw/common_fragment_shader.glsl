precision mediump float;

varying vec4 vColor;

void main() {
    float depth = 1.0 - gl_FragCoord.z;
    gl_FragColor = vColor;
    // gl_FragColor = vec4(vColor.xyz, 1);
}