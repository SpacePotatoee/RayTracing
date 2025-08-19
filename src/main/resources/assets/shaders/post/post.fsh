#version 330 core

uniform sampler2D DiffuseTextureSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseTextureSampler, texCoord);
    fragColor = color;
}