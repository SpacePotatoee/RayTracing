#version 460
#extension GL_EXT_ray_tracing : require

vec2 texCoord;

struct HitPayload{
    vec3 hitValue;
    vec3 rayOrigin;
    vec3 rayDir;
    vec3 rayPos;
};

layout(set = 0, binding = 0) uniform CameraInfo {
    mat4 projMat;
    mat4 modelViewMat;
    vec4 cameraPos;

    mat4 invProjMat;
    mat4 invModelViewMat;
} cameraInfo;

layout(set=1, binding=0) uniform accelerationStructureEXT accelStruct;
layout(set=2, binding=0, rgba8) uniform image2D outImage;
layout(location = 0) rayPayloadEXT HitPayload prd;

vec3 projectAndDivide(mat4 projectionMat, vec3 position) {
    vec4 homogeneousPos = projectionMat * vec4(position, 1.0);
    return homogeneousPos.xyz / homogeneousPos.w;
}

vec3 screenToViewSpace(float depth) {
    return  projectAndDivide(cameraInfo.invProjMat, vec3(texCoord, depth) * 2.0 - 1.0);
}

vec3 screenToLocalSpace(float depth) {
    vec3 viewPos = screenToViewSpace(depth);
    return (cameraInfo.invModelViewMat * vec4(viewPos, 1.0)).xyz;
}

vec3 getRayDir() {
    return normalize(screenToLocalSpace(1.0));
}

void main() {
    const vec2 pixelCenter = vec2(gl_LaunchIDEXT.xy) + vec2(0.5f, 0.5f);
    const vec2 invUv = pixelCenter/vec2(gl_LaunchSizeEXT.xy);
    texCoord = vec2(invUv.x, 1.0 - invUv.y);

    prd.rayDir = getRayDir();
    prd.rayOrigin = cameraInfo.cameraPos.xyz;


    traceRayEXT(accelStruct, gl_RayFlagsNoneEXT, 0xFFu, 0, 0, 0, prd.rayOrigin, 0.001, prd.rayDir, 10000, 0);

    ivec2 pixelCoord = ivec2(gl_LaunchIDEXT.xy);

    imageStore(outImage, pixelCoord, vec4(prd.hitValue, 1.f));
}