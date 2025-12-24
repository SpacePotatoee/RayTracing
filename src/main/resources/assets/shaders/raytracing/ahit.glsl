#version 460
#extension GL_EXT_ray_tracing : enable

struct HitPayload{
    vec3 hitValue;
    vec3 rayOrigin;
    vec3 rayDir;
    vec3 rayPos;
};

layout(location = 0) rayPayloadInEXT HitPayload payload;

void main() {
    payload.hitValue = vec3(1.0, 0.0, 0.0);
    payload.rayPos = payload.rayOrigin + payload.rayDir * gl_HitTEXT;
}