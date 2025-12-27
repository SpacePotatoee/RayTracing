#version 460
#extension GL_EXT_ray_tracing : enable
#extension GL_EXT_nonuniform_qualifier : enable
#extension GL_EXT_scalar_block_layout : enable
#extension GL_EXT_shader_explicit_arithmetic_types_int64 : require
#extension GL_EXT_buffer_reference2 : require

//8 floats. 3 verts
const int STRIDE = 8 * 3;

struct Ray {
    bool hit;
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
    uint64_t meshAddress;

    float time;
} cameraInfo;

layout(buffer_reference, scalar) buffer Vertices {
    float f[];
};

layout(location = 0) rayPayloadInEXT Ray ray;
hitAttributeEXT vec2 baryCoords;

vec3 getNormal(vec3 bary) {
    int offset = STRIDE * (gl_PrimitiveID);
    Vertices vertices = Vertices(cameraInfo.vertAddress);

    offset += 4;
    vec3 normal1 = vec3(vertices.f[offset], vertices.f[offset + 1], vertices.f[offset + 2]);
    offset += 8;
    vec3 normal2 = vec3(vertices.f[offset], vertices.f[offset + 1], vertices.f[offset + 2]);
    offset += 8;
    vec3 normal3 = vec3(vertices.f[offset], vertices.f[offset + 1], vertices.f[offset + 2]);

    return vec3(normal1 * bary.z + normal2 * bary.x + normal3 * bary.y);
}

void main() {
    vec3 bary = vec3(baryCoords , 1.0 - baryCoords.x - baryCoords.y);
    ray.hit = true;
    ray.hitNormal = getNormal(bary);
    ray.hitValue = vec3(1.0, 0.0, 0.0);
    ray.rayPos = ray.rayOrigin + ray.rayDir * gl_HitTEXT;
}