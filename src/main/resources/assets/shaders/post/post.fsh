#version 430 core

uniform sampler2D DiffuseTextureSampler;
uniform sampler2D DiffuseDepthSampler;
uniform int NumOfTriangles;

layout (std140) uniform Camera {
    mat4 projMat;
    mat4 iProjMat;
    mat4 modelViewMat;
    mat4 iModelViewMat;
    vec3 cameraPosition;
} CameraMat;

struct Ray {
    vec3 pos;
    vec3 origin;
    vec3 dir;
    bool hit;
    vec3 normal;
};

struct Triangle {
    vec3 pointA;
    vec3 pointB;
    vec3 pointC;
};

layout (std430, binding = 0) buffer Triangles {
    Triangle triangles[];
} TriangleBuffer;

const Triangle mainTriangle = Triangle(vec3(0.5f, 0.5f, 0.0f), vec3(-0.5f, 0.5f, 0.0f), vec3(-0.5f, -0.5f, 0.0f));

in vec2 texCoord;
out vec4 fragColor;

vec3 projectAndDivide(mat4 projectionMat, vec3 pos) {
    vec4 homogeneousPos = projectionMat * vec4(pos, 1.0);
    return homogeneousPos.xyz / homogeneousPos.w;
}

void triangleIntersection(inout Ray ray, in Triangle tri) {
    vec3 edgeAB = tri.pointB - tri.pointA;
    vec3 edgeAC = tri.pointC - tri.pointA;

    vec3 flatNormal = cross(edgeAB, edgeAC);

    float num = -dot(flatNormal, ray.origin - tri.pointA);
    float denom = dot(flatNormal, ray.dir);

    if (denom >= 0.0f) {
        return;
    }

    float t = num / denom;

    if (t <= 0.0f) {
        return;
    }

    vec3 pos = ray.origin + ray.dir * t;

    vec3 BC = tri.pointC - tri.pointB;
    vec3 BP = pos - tri.pointB;
    float pointBDot = dot(flatNormal, cross(BC, BP));

    vec3 AB = tri.pointB - tri.pointA;
    vec3 AP = pos - tri.pointA;
    float pointADot = dot(flatNormal, cross(AB, AP));

    vec3 CA = tri.pointA - tri.pointC;
    vec3 CP = pos - tri.pointC;
    float pointCDot = dot(flatNormal, cross(CA, CP));

    if (pointADot >= 0.0 && pointBDot >= 0.0 && pointCDot >= 0.0) {
        ray.hit = true;
        ray.pos = pos;
        ray.normal = flatNormal;
    }
}

void main() {
    vec4 color = texture(DiffuseTextureSampler, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 viewPos = projectAndDivide(CameraMat.iProjMat, vec3(texCoord, 1.0) * 2.0 - 1.0);
    vec3 localSpace = (CameraMat.iModelViewMat * vec4(viewPos, 1.0)).xyz - CameraMat.cameraPosition;
    vec3 rayDir = normalize(localSpace);

    Ray ray;
    ray.origin = CameraMat.cameraPosition;
    ray.dir = rayDir;

    vec3 closestPos = vec3(999999999);
    for (int i = 0; i < NumOfTriangles; i++) {
        Triangle currentTriangle = TriangleBuffer.triangles[i];

        Triangle triangle = Triangle(currentTriangle.pointA, currentTriangle.pointB, currentTriangle.pointC);
        triangleIntersection(ray, triangle);

        if (ray.hit) {
            float prevDistance = distance(CameraMat.cameraPosition, closestPos);
            float currentDistance = distance(CameraMat.cameraPosition, ray.pos);
            closestPos = currentDistance > prevDistance ? closestPos : ray.pos;
            ray.pos = closestPos;
        }
    }

    if (ray.hit) {
        color.rgb = vec3(ray.pos);
    } else {
        if (depth >= 1.0) {
            color.rgb = vec3(0.5294117647, 0.80784313725, 0.92156862745);
        }
    }

    fragColor = vec4(color.rgb, 1.0);
}