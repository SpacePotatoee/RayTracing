#version 460
#extension GL_EXT_ray_tracing : enable

struct Ray {
    bool hit;
    vec3 hitValue;
    vec3 rayOrigin;
    vec3 rayDir;
    vec3 rayPos;

    vec3 hitNormal;
};

struct Material {
    vec3 color;
    vec3 emissiveColor;
    float emissiveStrength;
};

layout(set = 4, binding = 0) uniform MaterialBuffer {
    Material materials[];
} matBuffer;

layout(location = 0) rayPayloadInEXT Ray ray;

hitAttributeEXT vec2 baryCoords;

void main() {
    ray.hitValue = vec3(1.0);
}