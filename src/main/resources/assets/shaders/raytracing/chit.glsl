#version 460
#extension GL_EXT_ray_tracing : enable
#extension GL_EXT_nonuniform_qualifier : enable
#extension GL_EXT_scalar_block_layout : enable
#extension GL_EXT_shader_explicit_arithmetic_types_int64 : require
#extension GL_EXT_buffer_reference2 : require

//8 floats. 3 verts
const int STRIDE = (8 * 3);

struct Material {
    vec4 color;
    vec4 emissiveColor;
};

struct Ray {
    bool hit;
    vec3 hitValue;
    vec3 rayOrigin;
    vec3 rayDir;
    vec3 rayPos;
    vec3 hitNormal;
    Material hitMaterial;
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

layout(buffer_reference, scalar) buffer MaterialBuffer {
    Material materials[];
};

layout(buffer_reference, scalar) buffer Vertices {
    int f[];
};

layout(location = 0) rayPayloadInEXT Ray ray;

hitAttributeEXT vec2 baryCoords;

int getMatIndex() {
    int offset = STRIDE * gl_PrimitiveID;
    Vertices vertices = Vertices(cameraInfo.vertAddress);

    return vertices.f[offset + 3];
}

void main() {
    MaterialBuffer materialBuffer = MaterialBuffer(cameraInfo.meshAddress);
    int matIndex = getMatIndex();
    Material mat = materialBuffer.materials[matIndex];
    ray.hitMaterial = mat;

    ray.hitValue = mat.color.rgb;
}