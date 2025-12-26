#version 460
#extension GL_EXT_ray_tracing : enable
#extension GL_EXT_shader_explicit_arithmetic_types_int64 : require

struct HitPayload{
    vec3 hitValue;
    vec3 rayOrigin;
    vec3 rayDir;
    vec3 rayPos;

    vec3 hitNormal;
};

layout(set = 0, binding = 0) uniform CameraInfo {
    mat4 projMat;
    mat4 modelViewMat;
    mat4 invProjMat;
    mat4 invModelViewMat;

    vec3 cameraPos;
    uint64_t vertAddress;

    float time;
} cameraInfo;

const vec3 UP_DIR = vec3(0.0, 1.0, 0.0);
const float PI = 3.141592;

float TIME = mod(cameraInfo.time * 5, 1.0);
float angle = (TIME * 360 - 90) * (PI / 180);
vec3 SUN_DIR = normalize(vec3(-cos(angle), -sin(angle), 0.5));
vec3 MOON_DIR = vec3(-SUN_DIR.x, -SUN_DIR.y, SUN_DIR.z);

layout(location = 0) rayPayloadInEXT HitPayload payload;
layout(set=3, binding=0) uniform sampler2D SkyGradient;

vec3 getSkyColor(in vec3 dir) {
    float upDot = clamp(dot(UP_DIR, dir), 0.0, 1.0);
    float sun = pow(clamp(dot(dir, SUN_DIR), 0.0, 1.0), 100);
    float moon = smoothstep(0.995, 1.0,clamp(dot(dir, MOON_DIR), 0.0, 1.0));
    vec3 horizonColor = texture(SkyGradient, vec2(0.25, TIME)).rgb;
    vec3 upColor = texture(SkyGradient, vec2(0.75, TIME)).rgb;

    return mix(horizonColor, upColor, upDot) + sun + moon;
}

void main() {
    payload.hitValue = getSkyColor(payload.rayDir);
}