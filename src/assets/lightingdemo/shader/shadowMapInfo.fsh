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
        float lightRadius = lights[lightOffset + 2];
        
        float angle = textureCoordsOut.x * 2 * PI;
        
        vec2 targetPosition = lightPosition + (lightRadius * vec2(cos(angle), sin(angle)));
        
        vec2 lightPositionNorm = lightPosition / occlusionMapDimensions;
        vec2 currentPositionNorm = lightPositionNorm;
        vec2 targetPositionNorm = targetPosition / occlusionMapDimensions;
        
        vec2 tracingStep = (targetPositionNorm - currentPositionNorm) / lightRadius;
        
        float dist = 1.0;
        
        for (int i = 0; i < lightRadius; i += 1)
        {
            vec4 data = texture2D(textureSampler, currentPositionNorm);
            
            if (data.a > 0.75)
            {
                dist = float(i) / lightRadius;
                break;
            }
            
            currentPositionNorm += tracingStep;
        }
        
        gl_FragColor = vec4(vec3(dist), 1.0);
    }
}
