#version 460
#extension GL_EXT_ray_tracing : enable
#extension GL_EXT_nonuniform_qualifier : enable
#extension GL_EXT_scalar_block_layout : enable
#extension GL_EXT_shader_explicit_arithmetic_types_int64 : require
#extension GL_EXT_buffer_reference2 : require

//8 floats. 3 verts
const int STRIDE = 8 * 3;

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
    vec4 cameraPos;

    mat4 invProjMat;
    mat4 invModelViewMat;

    uint64_t vertAddress;
} cameraInfo;

layout(buffer_reference, scalar) buffer Vertices {
    float f[];
};

layout(location = 0) rayPayloadInEXT HitPayload payload;
hitAttributeEXT vec2 baryCoords;

void main() {
    int offset = STRIDE * (gl_PrimitiveID);
    Vertices vertices = Vertices(cameraInfo.vertAddress);

    offset += 4;
    vec3 normal1 = vec3(vertices.f[offset], vertices.f[offset + 1], vertices.f[offset + 2]);
    offset += 8;
    vec3 normal2 = vec3(vertices.f[offset], vertices.f[offset + 1], vertices.f[offset + 2]);
    offset += 8;
    vec3 normal3 = vec3(vertices.f[offset], vertices.f[offset + 1], vertices.f[offset + 2]);

    vec3 bary = vec3(baryCoords , 1.0 - baryCoords.x - baryCoords.y);
    payload.hitNormal = vec3(normal1 * bary.z + normal2 * bary.x + normal3 * bary.y);

    payload.hitValue = vec3(1.0, 0.0, 0.0);
    payload.rayPos = payload.rayOrigin + payload.rayDir * gl_HitTEXT;
}