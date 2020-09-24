precision mediump float;
varying vec4 vColor;
varying float vPointLightWeighting;

void main() {
	// gl_FragColor = vColor; // change the color based on the variable from the vertex shader.
	gl_FragColor = vec4(vColor.xyz * vPointLightWeighting, 1);
}