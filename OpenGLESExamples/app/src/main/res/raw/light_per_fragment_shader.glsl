precision mediump float;

varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;

// The position of the light in eye space.
uniform vec3 uLightPosition;

void main() {
    // Will be used for attenuation.
    float distance = length(uLightPosition - vPosition);

    // Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(uLightPosition - vPosition);

    // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
    // pointing in the same direction then it will get max illumination.
    float diffuse = max(dot(vNormal, lightVector), 0.1);

    // Add attenuation.
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));

    // Multiply the color by the diffuse illumination level to get final output color.
    gl_FragColor = vColor * diffuse;
}