#version 330 core

uniform sampler2D DiffuseTextureSampler;
uniform sampler2D DiffuseDepthSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseTextureSampler, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    if (depth >= 1.0) {
        color.rgb = vec3(0.5294117647, 0.80784313725, 0.92156862745);
    }

    fragColor = vec4(color.rgb, 1.0);
}