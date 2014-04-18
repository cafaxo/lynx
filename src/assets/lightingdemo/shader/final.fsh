#version 120

uniform sampler2D diffuseMap;

uniform sampler2D diffuseMapBlurred;

uniform sampler2D shadowMapBlurred; // TODO: may need to insert extra pass for light evaluation... let's check if I can put this into shadowMap.fsh

uniform int blurView;

uniform vec2 sourceDimensions;

uniform vec2 viewSource;

uniform float viewDistance;

uniform float viewFadeDistance;

uniform vec4 ambientLightColor;

varying vec2 textureCoordsOut;

varying vec4 colorOut;

void main()
{
    vec4 diffuseMapColor = texture2D(diffuseMap, textureCoordsOut);
    vec4 shadowMapBlurredColor = texture2D(shadowMapBlurred, textureCoordsOut);
    
    vec4 finalColor = vec4(0.f);
    
    if (blurView == 1)
    {
        vec4 diffuseMapBlurredColor = texture2D(diffuseMapBlurred, textureCoordsOut);
        vec2 currentPosition = textureCoordsOut * sourceDimensions;
        float currentDistance = distance(viewSource, currentPosition);
        
        // FOV concept. constant sight until viewDistance, then fade into diffuseMapBlurred
        
        float fadeFactor = 1.F;
        
        if (currentDistance > viewDistance && currentDistance < viewDistance + viewFadeDistance)
        {
            fadeFactor = ((viewFadeDistance + viewDistance - currentDistance) / viewFadeDistance);
        }
        else if (currentDistance > viewDistance + viewFadeDistance)
        {
            fadeFactor = 0.F;
        }
        
        finalColor = (diffuseMapColor * fadeFactor) + (diffuseMapBlurredColor * (1.f-fadeFactor));
    }
    else
    {
        finalColor = diffuseMapColor;
    }
    
    gl_FragColor = vec4(finalColor.rgb * (ambientLightColor.rgb + shadowMapBlurredColor.rgb), 1.f);
}
