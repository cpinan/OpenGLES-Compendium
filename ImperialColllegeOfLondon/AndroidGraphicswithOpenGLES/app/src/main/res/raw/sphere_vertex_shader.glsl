attribute vec3 aVertexPosition;
uniform mat4 uMVPMatrix;
varying vec4 vColor;
attribute vec4 aVertexColor;

uniform vec3 uPointLightingLocation;
varying float vPointLightWeighting; // Point light intensity

void main() {
    vec4 mvPosition = uMVPMatrix * vec4(aVertexPosition, 1.0); // Calculate the position of the vertex

    vec3 lightDirection = normalize(uPointLightingLocation - mvPosition.xyz); // The direction of the light source

    float distanceFromLight = distance(uPointLightingLocation, mvPosition.xyz); // Distance from the light source

    vPointLightWeighting = 10.0 / (distanceFromLight * distanceFromLight); // Intensity for the point light source

	gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0); // Calculate the position of the vertex

	vColor = aVertexColor; // Get the color from the application program
}