#version 120

#define LIGHT_SIZE 11

uniform float lights[LIGHT_SIZE * 16];

uniform int lightId;

varying float lightDistanceOut;

void main(void) {
    int lightOffset = lightId * LIGHT_SIZE;
    
    vec2 lightPosition = vec2(lights[lightOffset], lights[lightOffset + 1]);
    vec2 lightRadius = vec2(lights[lightOffset + 2]);
    float lightConeStart = lights[lightOffset + 3];
    float lightConeSize = lights[lightOffset + 4];
    vec3 lightColor = vec3(lights[lightOffset + 5], lights[lightOffset + 6], lights[lightOffset + 7]);
    vec3 lightAttenuationInfo = vec3(lights[lightOffset + 8], lights[lightOffset + 9], lights[lightOffset + 10]);

    float lightAttenuation = 1.0 / (lightAttenuationInfo.x + lightAttenuationInfo.y * lightDistanceOut + lightAttenuationInfo.z * lightDistanceOut * lightDistanceOut);
    
    gl_FragColor = vec4(lightColor, lightAttenuation);
}
