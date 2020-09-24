attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;
attribute vec4 aVertexColor;
attribute vec2 aTextureCoordinate; //texture coordinate

uniform mat4 uMVPMatrix;
uniform vec3 uPointLightingLocation;
uniform vec3 uPointLightingColor;
uniform vec3 uAmbientColor;
uniform vec3 uDiffuseLightLocation;
uniform vec4 uDiffuseColor; // color of the diffuse light
uniform vec3 uAttenuation; //light attenuation
uniform vec4 uSpecularColor;
uniform vec3 uSpecularLightLocation;
uniform float uMaterialShininess;

varying vec2 vTextureCoordinate;
varying vec4 vColor;
varying vec3 vLightWeighting;
varying vec4 vDiffuseColor;
varying float vPointLightWeighting;
varying float vDiffuseLightWeighting;
varying vec4 vSpecularColor;
varying float vSpecularLightWeighting;

void main() {
     gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);

     vLightWeighting = vec3(1.0, 1.0, 1.0);

     vec4 mvPosition = uMVPMatrix * vec4(aVertexPosition, 1.0);

     vec3 lightDirection = normalize(uPointLightingLocation - mvPosition.xyz);

     vec3 diffuseLightDirection = normalize(uDiffuseLightLocation - mvPosition.xyz);

     vec3 transformedNormal = normalize((uMVPMatrix * vec4(aVertexNormal, 0.0)).xyz);

     vLightWeighting = uAmbientColor;

     gl_PointSize = 40.0;

     vDiffuseColor = uDiffuseColor;

     vSpecularColor = uSpecularColor;

     float specularLightWeighting = 0.0;

     vec3 eyeDirection = normalize(-mvPosition.xyz);

     vec3 specularlightDirection = normalize(uSpecularLightLocation - mvPosition.xyz);

     vec3 inverseLightDirection = normalize(uPointLightingLocation);

     vec3 reflectionDirection = reflect(-lightDirection, transformedNormal);

     vPointLightWeighting = distance(uPointLightingLocation, mvPosition.xyz);

     vPointLightWeighting = 10.0 / (vPointLightWeighting * vPointLightWeighting);

     vec3 vertexToLightSource = mvPosition.xyz - uPointLightingLocation;

     float diff_light_dist = length(vertexToLightSource);

     float attenuation = 1.0 / (uAttenuation.x + uAttenuation.y * diff_light_dist + uAttenuation.z * diff_light_dist * diff_light_dist);

     float diffuseLightWeighting = 0.0;

     diffuseLightWeighting = attenuation * max(dot(transformedNormal, lightDirection), 0.0);

     vDiffuseLightWeighting = diffuseLightWeighting;

     specularLightWeighting = attenuation * pow(max(dot(reflectionDirection, eyeDirection), 0.0), uMaterialShininess);

     vSpecularLightWeighting = specularLightWeighting;

     vColor = aVertexColor;

     vTextureCoordinate = aTextureCoordinate;
}