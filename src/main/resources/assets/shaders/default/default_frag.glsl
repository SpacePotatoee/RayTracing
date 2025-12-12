#version 450

layout(location = 0) out vec4 fragColor;

layout(location = 0) in vec3 normal;

void main() {
    vec3 lightDir = normalize(vec3(1.0));
    float light = dot(lightDir, normal);

    fragColor = vec4(vec3(1.0) * light, 1.0);
}