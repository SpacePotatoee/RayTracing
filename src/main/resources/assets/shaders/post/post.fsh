#version 430 core

uniform sampler2D PrevSampler;
uniform int NumOfMeshes;
uniform int Frame;

in vec2 texCoord;
out vec4 fragColor;

const float PI = 3.14159265359;
const int NUMBER_OF_BOUNCES = 9;
const int RAYS_PER_PIXEL = 2;

layout (std140) uniform Camera {
    mat4 projMat;
    mat4 iProjMat;
    mat4 modelViewMat;
    mat4 iModelViewMat;
    vec3 cameraPosition;
} CameraMat;

struct HitInfo {
    bool hit;
    vec3 pos;
    vec3 color;
    vec3 normal;
};

struct HitMaterial {
    vec3 normal;
    vec3 color;
    vec3 emissionColor;
    float emissionStrength;
};

struct Ray {
    vec3 pos;
    vec3 origin;
    vec3 dir;
    bool hit;
    HitMaterial material;
};

struct Triangle {
    vec3 pointA;
    vec3 pointB;
    vec3 pointC;
};

struct Mesh {
    ivec2 indexes; //x = num of triangles  y = start index
    vec4 color; //RGB + Emission
};

layout (std430, binding = 1) buffer Meshes {
    Mesh meshes[];
} MeshData;

layout (std430, binding = 0) buffer Triangles {
    Triangle triangles[];
} TriangleBuffer;

vec3 hashwithoutsine31(float p)
{
    vec3 p3 = fract(vec3(p,p,p) * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yzx+33.33);
    return fract((p3.xxy+p3.yzz)*p3.zyx);
}

float rand(float value) {
    return fract(sin(dot(value, 7843.223)) * 4768.5453) * 2.0 - 1.0;
}

vec3 projectAndDivide(mat4 projectionMat, vec3 pos) {
    vec4 homogeneousPos = projectionMat * vec4(pos, 1.0);
    return homogeneousPos.xyz / homogeneousPos.w;
}

vec3 randomHemispherePoint(float value, vec3 normal) {
    vec3 rand = hashwithoutsine31(value * 374195) * 2.0 - 1.0;
    rand = normalize(rand / cos(rand));

    int mul = dot(rand, normal) <= 0.0 ? -1 : 1;

    return rand * mul;
}

void copyHitToRay(inout Ray ray, in HitInfo info) {
    ray.hit = info.hit;
    ray.pos = info.pos;
    ray.material.color = info.color;
    ray.material.normal = info.normal;
}

//https://www.youtube.com/watch?v=XgUhgSlQvic
HitInfo triangleIntersection(in Ray ray, in Triangle tri) {
    HitInfo info;
    info.pos = vec3(999999);
    vec3 edgeAB = tri.pointB - tri.pointA;
    vec3 edgeAC = tri.pointC - tri.pointA;

    vec3 flatNormal = normalize(cross(edgeAB, edgeAC));

    float num = -dot(flatNormal, ray.origin - tri.pointA);
    float denom = dot(flatNormal, ray.dir);

    if (denom >= 0.0f) {
        return info;
    }

    float t = num / denom;

    if (t <= 0.0f) {
        return info;
    }

    vec3 pos = ray.origin + ray.dir * t;

    vec3 BC = tri.pointC - tri.pointB;
    vec3 BP = pos - tri.pointB;
    float pointBDot = dot(flatNormal, normalize(cross(BC, BP)));

    vec3 AB = tri.pointB - tri.pointA;
    vec3 AP = pos - tri.pointA;
    float pointADot = dot(flatNormal, normalize(cross(AB, AP)));

    vec3 CA = tri.pointA - tri.pointC;
    vec3 CP = pos - tri.pointC;
    float pointCDot = dot(flatNormal, normalize(cross(CA, CP)));

    if (pointADot >= 0.0 && pointBDot >= 0.0 && pointCDot >= 0.0) {
        info.hit = true;
        info.pos = pos;
        info.normal = flatNormal;
    }

    return info;
}

void traverseScene(inout Ray ray) {
    vec3 closestPos = vec3(999999);
    for (int i = 0; i < NumOfMeshes; i++) {
        Mesh currentMesh = MeshData.meshes[i];
        int endIndex = currentMesh.indexes.x + currentMesh.indexes.y;
        for (int j = currentMesh.indexes.y; j < endIndex; j++) {

            HitInfo info = triangleIntersection(ray, TriangleBuffer.triangles[j]);

            if (info.hit) {
                float prevDistance = distance(ray.origin, closestPos);
                float currentDistance = distance(ray.origin, info.pos);
                bool shouldUpdateRay = currentDistance < prevDistance;
                if (shouldUpdateRay) {
                    info.color = currentMesh.color.rgb;
                    closestPos = info.pos;
                    copyHitToRay(ray, info);
                    ray.material.emissionStrength += currentMesh.color.a;
                }
            }
        }
    }
}

vec3 bounceRays(in Ray ray, int rng) {
    vec3 color = vec3(1.0);
    vec3 totalLight = vec3(0.0);

    for (int i = 0; i < NUMBER_OF_BOUNCES; i++) {
        traverseScene(ray);

        if (ray.hit) {
            ray.hit = false;
            ray.dir = randomHemispherePoint(hashwithoutsine31((texCoord.x * 62.5412 * texCoord.y * 38.61453) + rng + (i) * 83.53512).x, ray.material.normal);
            ray.origin = ray.pos;
            vec3 light = (ray.material.color * ray.material.emissionStrength) / NUMBER_OF_BOUNCES;
            totalLight += light * color;
            color *= ray.material.color;
        } else {
            color = vec3(0.0);
        }
    }

    return totalLight;
}

void main() {
    vec3 prevColor = texture(PrevSampler, texCoord).rgb;

    vec3 viewPos = projectAndDivide(CameraMat.iProjMat, vec3(texCoord, 1.0) * 2.0 - 1.0);
    vec3 localSpace = (CameraMat.iModelViewMat * vec4(viewPos, 1.0)).xyz - CameraMat.cameraPosition;
    vec3 rayDir = normalize(localSpace);

    vec3 newFrame = vec3(0.0);
    for (int i = 0; i < RAYS_PER_PIXEL; i++) {
        Ray ray;
        ray.origin = CameraMat.cameraPosition;
        ray.dir = rayDir;
        HitMaterial material;
        ray.material = material;

//        newFrame += hashwithoutsine31((texCoord.x * 62.5412 * texCoord.y * 38.61453) + Frame * 83.53512).x;
        newFrame += bounceRays(ray, i + 1 + Frame);
    }

    newFrame = clamp(newFrame / RAYS_PER_PIXEL, vec3(-1), vec3(1));

    float weight = 1.0 / (Frame + 1);
//    vec3 outputColor = mix(newFrame, prevColor, 0.99);
    vec3 outputColor = prevColor * (1.0 - weight) + newFrame * weight;

    fragColor = vec4(outputColor, 1.0);
}