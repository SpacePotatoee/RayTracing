#type vertex
#version 330 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Color;

out vec4 color;

void main() {
    color = Color;
    gl_Position = vec4(Position, 1.0);
}



#type fragment
#version 330 core

in vec4 color;
out vec4 fragColor;

void main() {
    fragColor = color;
}