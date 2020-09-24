precision lowp float;

varying vec4 vColor;

varying vec3 vLightWeighting;

varying vec4 vDiffuseColor;

varying float vDiffuseLightWeighting;

varying float vPointLightWeighting;

varying vec4 vSpecularColor;

varying float vSpecularLightWeighting;

varying vec2 vTextureCoordinate;

uniform sampler2D uTextureSampler; //texture

void main() {
     vec4 diffuseColor = vDiffuseLightWeighting * vDiffuseColor;

     vec4 specularColor = vSpecularLightWeighting * vSpecularColor;

     vec4 fragmentColor = texture2D(uTextureSampler, vec2(vTextureCoordinate.s, vTextureCoordinate.t)); //load the color texture

     gl_FragColor = vec4(fragmentColor.rgb * vLightWeighting, fragmentColor.a) + specularColor + diffuseColor; //the fragment color
}