#version 150
#define PI 3.14159265359
uniform sampler2D DiffuseSampler;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;
//更牛逼的冰地球版本
//冰地球🧊🧊🧊 我只想毁掉👹👹👹 德罗巴⚽⚽⚽ 大呀🚨🚨🚨 原子弹给我爆☢☢☢
vec4 getColor(vec2 uv) {
    float GameTime = sin(PI * Time) * 1.5;
    float waveFrequency = 10.0;
    float waveAmplitude = 0.08;
    float waveSpeed = 10.0;
    float waveOffsetY = sin(uv.x * waveFrequency + GameTime * waveSpeed) * waveAmplitude;
    uv.y += waveOffsetY;

    float glitchSpeed = 4.0;
    float colorShiftAmount = 0.010;
    float glitchIntensity = 0.08;

    vec2 colorShiftOffset = vec2(sin(GameTime * glitchSpeed * 0.7), cos(GameTime * glitchSpeed)) * colorShiftAmount;

    float glitchTime = GameTime * glitchSpeed * 5.0;
    float glitchLine = floor(uv.y * 30.0);
    float glitchValue = sin(glitchTime + glitchLine * 10.0);
    float glitchTrigger = step(0.9, fract(glitchTime * 0.1));
    float glitchOffsetX = (glitchValue - 0.5) * glitchIntensity * glitchTrigger;

    float r = texture(DiffuseSampler, uv + colorShiftOffset + vec2(glitchOffsetX, 0.0)).r;
    float g = texture(DiffuseSampler, uv).g;
    float b = texture(DiffuseSampler, uv - colorShiftOffset - vec2(glitchOffsetX, 0.0)).b;
    float a = texture(DiffuseSampler, uv).a;

    return vec4(r, g, b, a);
}

void main() {
    float GameTime = sin(PI * Time);
    vec4 baseColor = getColor(texCoord);

    float bloomIntensity = 5.0 - GameTime;
    float bloomThreshold = 0.8 - (GameTime / 4);
    float bloomRadius = 0.05;

    vec4 bloomColor = vec4(0.0);

    vec2 offsets[4];
    offsets[0] = vec2(1.0, 1.0);
    offsets[1] = vec2(-1.0, 1.0);
    offsets[2] = vec2(1.0, -1.0);
    offsets[3] = vec2(-1.0, -1.0);

    for (int i = 0; i < 4; i++) {
        vec2 sampleUV = texCoord + normalize(offsets[i]) * bloomRadius;
        vec4 sampleColor = getColor(sampleUV);
        float brightness = dot(sampleColor.rgb, vec3(0.2126, 0.7152, 0.0722));
        if (brightness > bloomThreshold) {
            bloomColor += sampleColor;
        }
    }

    float baseBrightness = dot(baseColor.rgb, vec3(0.2126, 0.7152, 0.0722));
    if (baseBrightness > bloomThreshold) {
        bloomColor += baseColor;
    }

    bloomColor /= 5.0;

    fragColor = baseColor + bloomColor * bloomIntensity;
}

//void main() {
//    vec2 uv = texCoord;
//
//    float waveFrequency = 10.0;
//    float waveAmplitude = 0.08;
//    float waveSpeed = 20.0;
//    float waveOffset = sin(uv.x * waveFrequency + Time * waveSpeed) * waveAmplitude;
//    uv.y += waveOffset;
//
//    float glitchSpeed = 20.0;
//    float colorShiftAmount = 0.02;
//    float glitchIntensity = 0.2;
//
//    vec2 colorShiftOffset = vec2(sin(Time * glitchSpeed * 0.7), cos(Time * glitchSpeed)) * colorShiftAmount;
//
//    float glitchTime = Time * glitchSpeed * 5.0;
//    float glitchLine = floor(uv.y * 30.0);
//    float glitchValue = sin(glitchTime + glitchLine * 10.0);
//    float glitchTrigger = step(0.9, fract(glitchTime * 0.1));
//    float glitchOffsetX = (glitchValue - 0.5) * glitchIntensity * glitchTrigger;
//
//    float r = texture(DiffuseSampler, uv + colorShiftOffset + vec2(glitchOffsetX, 0.0)).r;
//    float g = texture(DiffuseSampler, uv).g;
//    float b = texture(DiffuseSampler, uv - colorShiftOffset - vec2(glitchOffsetX, 0.0)).b;
//    float a = texture(DiffuseSampler, uv).a;
//
//    vec4 baseColor = vec4(r, g, b, a);
//    float bloomThreshold = 0.3;
//    float bloomIntensity = 5;
//    float blurSize = 0.018;
//    int BLUR_SAMPLES = 7;
//    vec3 bloomColor = vec3(0.0);
//    int halfSamples = BLUR_SAMPLES / 2;
//
//    for (int x = -halfSamples; x <= halfSamples; x++) {
//        for (int y = -halfSamples; y <= halfSamples; y++) {
//            vec2 offset = vec2(float(x), float(y)) * blurSize;
//            vec4 sampleTex = texture(DiffuseSampler, texCoord + offset);
//
//            vec3 brightParts = max(vec3(0.0), sampleTex.rgb - bloomThreshold);
//            bloomColor += brightParts;
//        }
//    }
//
//    bloomColor /= float(BLUR_SAMPLES * BLUR_SAMPLES);
//
//    fragColor = baseColor + vec4(bloomColor * bloomIntensity, 0.0);
//}