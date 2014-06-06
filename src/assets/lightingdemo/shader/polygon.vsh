#version 120

uniform mat4 camera;

attribute vec2 position;

attribute vec4 color;

varying vec4 colorOut;

void main()
{
    colorOut = color;
    
    gl_Position = camera * vec4(position.x, position.y, 0.0, 1.0);
}
