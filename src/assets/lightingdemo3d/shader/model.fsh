#version 120

uniform sampler2D textureSampler;

varying vec2 textureCoordsOut;

varying vec3 normalOut;

void main()
{
    gl_FragColor = texture2D(textureSampler, textureCoordsOut);
}
