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

//vec3 getSkyColor(in vec3 dir) {
//    float upDot = clamp(dot(UP_DIR, dir), 0.0, 1.0);
//    float sun = pow(clamp(dot(dir, SUN_DIR), 0.0, 1.0), 100);
//    float moon = smoothstep(0.995, 1.0,clamp(dot(dir, MOON_DIR), 0.0, 1.0));
//    vec3 horizonColor = texture(SkyGradient, vec2(0.25, TIME)).rgb;
//    vec3 upColor = texture(SkyGradient, vec2(0.75, TIME)).rgb;
//
//    return mix(horizonColor, upColor, upDot) + sun + moon;
//}

void main() {
    payload.hitValue = vec3(0.2, 0.5, 1.0);
}