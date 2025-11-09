#version 120
#define PI 3.14159265359
varying vec2 texCoord;

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform float Time;

void main() {
    float GameTime = sin(PI * Time);
    //对抗命运
    float glitchStrength = GameTime * 0.015;
    float glitchOffset = sin(texCoord.y * 250.0 + Time * 100.0) * cos(Time * 30.0) * glitchStrength;
    vec2 glitchCoord = texCoord + vec2(glitchOffset, 0.0);

    vec4 baseColor = texture2D(DiffuseSampler, glitchCoord);

    float luminance = dot(baseColor.rgb, vec3(0.299, 0.587, 0.114));
    vec3 finalColor = vec3(luminance);

    float bloomThreshold = 0.5;
    vec3 bloomColor = vec3(0.0);
    vec2 pixelSize = 1.0 / InSize;
    float bloomIntensity = 1.8 * GameTime;

    for (int x = -2; x <= 2; x++) {
        for (int y = -2; y <= 2; y++) {
            vec2 offset = vec2(float(x), float(y)) * pixelSize;
            vec4 sampleColor = texture2D(DiffuseSampler, glitchCoord + offset);
            float sampleLuminance = dot(sampleColor.rgb, vec3(0.299, 0.587, 0.114));
            if (sampleLuminance > bloomThreshold) {
                bloomColor += sampleColor.rgb;
            }
        }
    }

    bloomColor /= 5.0;

    finalColor += bloomColor * bloomIntensity;

    gl_FragColor = vec4(finalColor, baseColor.a);
}