#version 120

uniform mat4 camera;

attribute vec2 position;

attribute vec2 textureCoords;

attribute vec4 color;

varying vec2 textureCoordsOut;

varying vec4 colorOut;

void main()
{
    textureCoordsOut = textureCoords;
    colorOut = color;
    
    gl_Position = vec4(position.x, position.y, 0.0, 1.0) * camera;
}
