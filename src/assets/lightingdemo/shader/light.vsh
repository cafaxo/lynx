#version 120

uniform mat4 camera;

attribute vec2 position;

attribute float lightDistance;

varying float lightDistanceOut;

void main()
{
    lightDistanceOut = lightDistance;
    
    gl_Position = vec4(position.x, position.y, 0.0, 1.0) * camera;
}
