#version 450

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec3 Normal;

layout(location = 0) out vec3 normal;

layout(push_constant) uniform matricies {
    mat4 projMat;
    mat4 modelViewMat;
} cameraMats;

void main() {
    gl_Position = cameraMats.projMat * cameraMats.modelViewMat * vec4(inPos, 1.0);
    normal = Normal;
}