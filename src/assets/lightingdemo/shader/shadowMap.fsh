#version 120

#define PI 3.1415926535
#define LIGHT_SIZE 11

uniform sampler2D shadowMapInfo;

uniform sampler2D occlusionMap;

uniform float lights[LIGHT_SIZE * 16];

uniform int numLights;

uniform vec2 occlusionMapDimensions;

varying vec2 textureCoordsOut;

varying vec4 colorOut;

void main(void) {
    vec4 finalColor = vec4(0.0F);
    
    for (int i = 0; i < numLights; i += 1)
    {
        int lightOffset = i * LIGHT_SIZE;
        
        vec2 lightPosition = vec2(lights[lightOffset], lights[lightOffset + 1]);
        vec2 lightRadius = vec2(lights[lightOffset + 2]);
        float lightConeStart = lights[lightOffset + 3];
        float lightConeSize = lights[lightOffset + 4];
        vec3 lightColor = vec3(lights[lightOffset + 5], lights[lightOffset + 6], lights[lightOffset + 7]);
        vec3 lightAttenuationInfo = vec3(lights[lightOffset + 8], lights[lightOffset + 9], lights[lightOffset + 10]);
        
        vec2 transformed = ((textureCoordsOut * occlusionMapDimensions) - (lightPosition - lightRadius)) / (2 * lightRadius);
        
        if (transformed.x > 0.f && transformed.x < 1.f && transformed.y > 0.f && transformed.y < 1.f && texture2D(occlusionMap, textureCoordsOut).a == 0.f)
        {
            vec2 norm = transformed * 2.0 - 1.0;
            float theta = atan(norm.y, norm.x);
            float coord = 1.f - ((theta + PI) / (2.0 * PI));
            
            float dist = abs(lightConeStart - coord);
            
            if (min(dist, 1.f - dist) < lightConeSize)
            {
                float shadowStrength = step(length(norm), texture2D(shadowMapInfo, vec2(coord, float(i) / 16.f)).r);
                
                if (shadowStrength > 0.f)
                {
                    float lightDistance = distance(lightPosition, textureCoordsOut * occlusionMapDimensions);
                    float lightAttenuation = 1.0 / (lightAttenuationInfo.x + lightAttenuationInfo.y * lightDistance + lightAttenuationInfo.z * lightDistance * lightDistance);
                
                    finalColor += vec4(lightAttenuation, lightAttenuation, lightAttenuation, 1.0) * vec4(lightColor, 1.0f);
                }
            }
        }
    }
    
    gl_FragColor = finalColor;
}
