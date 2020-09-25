uniform mat4 uModelViewProjectionMatrix;

uniform mat4 uModelViewMatrix;

// The position of the light in eye space.
uniform vec3 uLightPosition;

attribute vec4 aVertexPosition;

attribute vec4 aVertexColor;

attribute vec3 aVertexNormal;

varying vec4 vColor;

void main() {
    // Transform the vertex into eye space.
    vec3 modelViewVertex = vec3(uModelViewMatrix * aVertexPosition);

    // Transform the normal's orientation into eye space.
    vec3 modelViewNormal = vec3(uModelViewMatrix * vec4(aVertexNormal, 0.0));

    // Will be used for attenuation.
    float distance = length(uLightPosition - modelViewVertex);

    // Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(uLightPosition - modelViewVertex);

    // Calculate the dot product of the light vector and vertex normal.
    // If the normal and light vector are
    // pointing in the same direction then it will get max illumination.
    float diffuse = max( dot(modelViewNormal, lightVector), 0.1);

    // Attenuate the light based on distance.
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));

    // Multiply the color by the illumination level. It will be interpolated across the triangle.
    vColor = aVertexColor * diffuse;

    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
    gl_Position = uModelViewProjectionMatrix * aVertexPosition;
}