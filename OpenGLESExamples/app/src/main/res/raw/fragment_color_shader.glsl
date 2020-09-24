precision mediump float;

varying vec3 vColor;

void main() {
    vec3 ambientColor = vec3(0.7, 0.8, 0.6);
    gl_FragColor = vec4(vColor * ambientColor, 1.0);
    // gl_FragColor = vec4(1, 0.5, 0, 1.0);
}