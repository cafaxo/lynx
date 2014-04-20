#version 120

#define PI 3.1415926535
#define LIGHT_SIZE 11

uniform sampler2D shadowMapInfo;

uniform float lights[LIGHT_SIZE * 16];

uniform int numLights;

uniform vec2 occlusionMapDimensions;

varying vec2 textureCoordsOut;

varying vec4 colorOut;

void main(void) {
    vec4 finalColor = vec4(0.0F);
    vec2 position = textureCoordsOut * occlusionMapDimensions;
    
    for (int i = 0; i < numLights; i += 1)
    {
        int lightOffset = i * LIGHT_SIZE;
        
        vec2 lightPosition = vec2(lights[lightOffset], lights[lightOffset + 1]);
        float lightRadius = lights[lightOffset + 2];
        float lightConeStart = lights[lightOffset + 3];
        float lightConeSize = lights[lightOffset + 4];
        vec3 lightColor = vec3(lights[lightOffset + 5], lights[lightOffset + 6], lights[lightOffset + 7]);
        vec3 lightAttenuationInfo = vec3(lights[lightOffset + 8], lights[lightOffset + 9], lights[lightOffset + 10]);
        
        vec2 direction = lightPosition - position;
        float lightDistanceSquared = pow(direction.x, 2.f) + pow(direction.y, 2.f);
        
        if (lightDistanceSquared < pow(lightRadius, 2.f))
        {
            float theta = atan(direction.y, direction.x);
            float coord = (theta + PI) / (2.0 * PI);
            
            float dist = abs(lightConeStart - coord);
            
            if (min(dist, 1.f - dist) < lightConeSize)
            {
                float lightDistance = sqrt(lightDistanceSquared);
                float shadowStrength = step(lightDistance / lightRadius, texture2D(shadowMapInfo, vec2(coord, float(i) / 16.f)).r);
                
                if (shadowStrength > 0.f)
                {
                    float lightAttenuation = 1.0 / (lightAttenuationInfo.x + lightAttenuationInfo.y * lightDistance + lightAttenuationInfo.z * lightDistanceSquared);
                    
                    finalColor += vec4(lightAttenuation, lightAttenuation, lightAttenuation, 1.0) * vec4(lightColor, 1.0f);
                }
            }
        }
    }
    
    gl_FragColor = finalColor;
}
