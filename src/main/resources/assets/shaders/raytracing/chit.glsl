#version 460
#extension GL_EXT_ray_tracing : enable

struct HitPayload{
    vec3 hitValue;
    vec3 rayOrigin;
    vec3 rayDir;
    vec3 rayPos;

    vec3 hitNormal;
};

layout(location = 0) rayPayloadInEXT HitPayload payload;

hitAttributeEXT vec2 baryCoords;

void main() {
//    payload.hitValue = vec3(baryCoords, 1.0 - baryCoords.x - baryCoords.y);
//    float light = dot(vec3(1.0), payload.hitNormal);
//    payload.hitValue = vec3(1) * light;
    payload.hitValue = payload.hitNormal;
}