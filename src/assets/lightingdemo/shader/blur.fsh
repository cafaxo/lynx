#version 120

uniform sampler2D textureSampler;
uniform vec2 direction;

const int gaussRadius = 11;
float gaussFilter[gaussRadius] = float[gaussRadius](0.0402,0.0623,0.0877,0.1120,0.1297,0.1362,0.1297,0.1120,0.0877,0.0623,0.0402);

varying vec2 textureCoordsOut;

varying vec4 colorOut;

void main()
{
    vec2 texCoord = textureCoordsOut - float(int(gaussRadius/2)) * direction;
	vec4 color = vec4(0);
    
    for (int i = 0; i < gaussRadius; ++i)
    {
		color += gaussFilter[i] * texture2D(textureSampler, texCoord);
		texCoord += direction;
	}
    
	gl_FragColor = color;
}
