#version 330 core

in vec4 color;
in vec3 normal;
out vec4 fragColor;

void main() {
    float lighting = dot(normal, normalize(vec3(0.5,0.7,1)));
    fragColor = color * lighting;
}