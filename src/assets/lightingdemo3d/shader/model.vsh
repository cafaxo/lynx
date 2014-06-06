#version 120

uniform mat4 projection;

uniform mat4 view;

attribute vec3 position;

attribute vec2 textureCoords;

attribute vec3 normal;

varying vec2 textureCoordsOut;

varying vec3 normalOut;

void main()
{
    textureCoordsOut = textureCoords;
    normalOut = vec4(vec4(normal, 0.f) * view).xyz;
    
    gl_Position = projection * view * vec4(position.x, position.y, position.z, 1.0);
}
