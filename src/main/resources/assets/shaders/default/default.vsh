#version 330 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Color;
layout (location = 2) in vec3 Normal;

uniform mat4 model;
uniform mat4 proj;

out vec4 color;
out vec3 normal;

void main() {
    color = Color;
    normal = Normal;
    gl_Position = proj * model * vec4(Position, 1.0);
}