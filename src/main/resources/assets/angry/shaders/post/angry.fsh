#version 150

uniform sampler2D DiffuseSampler;
uniform vec3 BlockScreenPos;
uniform float Time;
uniform float Radius;
uniform float Strength;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    if (BlockScreenPos.z < 0.0) {
        fragColor = texture(DiffuseSampler, texCoord);
        return;
    }

    vec2 screenCenter = BlockScreenPos.xy;
    vec2 toCenter = screenCenter - texCoord;
    float dist = length(toCenter);

    if (dist > Radius) {
        fragColor = texture(DiffuseSampler, texCoord);
        return;
    }

    float falloff = 1.0 - smoothstep(0.0, Radius, dist);
    float distortionFactor = sin(dist * 25.0 - Time * 5.0) * Strength * falloff;
    vec2 offset = normalize(toCenter) * distortionFactor;
    vec2 distortedCoord = texCoord + offset;

    fragColor = texture(DiffuseSampler, distortedCoord);
}