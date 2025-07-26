#type vertex
#version 330 core

layout (location = 0) in vec3 Position;

void main() {
    gl_Position = vec4(Position, 1.0);
}



#type fragment
#version 330 core

out vec4 fragColor;

void main() {
    fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}