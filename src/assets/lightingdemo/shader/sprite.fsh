#version 120

uniform sampler2D textureSampler;

varying vec2 textureCoordsOut;

varying vec4 colorOut;

void main()
{
    gl_FragColor = texture2D(textureSampler, textureCoordsOut) * colorOut;
}
