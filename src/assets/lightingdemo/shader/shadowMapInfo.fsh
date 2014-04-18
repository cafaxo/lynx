#version 120

#define PI 3.1415926535
#define LIGHT_SIZE 11

uniform sampler2D textureSampler;

uniform float lights[LIGHT_SIZE * 16];

uniform int numLights;

uniform vec2 occlusionMapDimensions;

uniform vec2 shadowMapDimensions;

varying vec2 textureCoordsOut;

varying vec4 colorOut;

void main(void) {
    int lightNumber = int(textureCoordsOut.y * shadowMapDimensions.y);
    
    if (lightNumber < numLights)
    {
        int lightOffset = lightNumber * LIGHT_SIZE;
        
        vec2 lightPosition = vec2(lights[lightOffset], lights[lightOffset + 1]);
        vec2 lightRadius = vec2(lights[lightOffset + 2]);
        vec3 lightColor = vec3(lights[lightOffset + 5], lights[lightOffset + 6], lights[lightOffset + 7]);
        
        vec2 raytracingSourceSize = 2 * lightRadius;
        vec2 raytracingSourceOffset = lightPosition - lightRadius;
        
        float dist = 1.0;
        
        for (float y = 0.0; y < lightRadius.x * 2.f; y += 1.0)
        {
            vec2 transformed = vec2(textureCoordsOut.x, y / raytracingSourceSize.y);
            
            vec2 norm = transformed * 2.0 - 1.0;
            float theta = PI * 1.5 + norm.x * PI;
            float r = (1.0 + norm.y) * 0.5;
            
            vec2 coord = vec2(-r * sin(theta), -r * cos(theta)) / 2.0 + 0.5;
            
            vec2 untransformed = ((coord * raytracingSourceSize) + raytracingSourceOffset) / occlusionMapDimensions;
            
            vec4 data = texture2D(textureSampler, untransformed);
            
            if (data.a > 0.75)
            {
                dist = y / (lightRadius.x * 2.f);
                break;
            }
        }
        
        gl_FragColor = vec4(vec3(dist), 1.0);
    }
}
