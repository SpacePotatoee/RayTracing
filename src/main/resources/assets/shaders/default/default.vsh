#version 330 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Color;

uniform mat4 model;
uniform mat4 proj;

out vec4 color;

void main() {
    color = Color;
    gl_Position = proj * model * vec4(Position, 1.0);
}