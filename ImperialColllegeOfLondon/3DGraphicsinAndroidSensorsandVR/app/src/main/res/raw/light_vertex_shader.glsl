attribute vec3 aVertexPosition;
attribute vec4 aVertexColor;

uniform mat4 uMVPMatrix;
uniform vec3 uPointLightingLocation;

varying vec4 vColor;
varying float vPointLightWeighting;

void main() {
    vec4 mvPosition = uMVPMatrix * vec4(aVertexPosition, 1.0);

    // The direction of the light source
    // vec3 lightDirection = normalize(uPointLightingLocation - mvPosition.xyz);

    // Distance from light source
    // float distanceFromLightSource = distance(lightDirection, mvPosition.xyz);
    float distanceFromLightSource = distance(uPointLightingLocation, mvPosition.xyz);

    // Intensity for the point light source
    vPointLightWeighting = 10.0 / (distanceFromLightSource * distanceFromLightSource);
    vColor = aVertexColor;

    gl_Position = mvPosition;
}