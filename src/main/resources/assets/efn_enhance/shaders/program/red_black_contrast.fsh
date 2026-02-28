#version 150

uniform sampler2D DiffuseSampler;
uniform float Intensity;
uniform float SkyHueMin;
uniform float SkyHueMax;
uniform float SkySaturationMin;
uniform float SkyValueMin;

in vec2 texCoord;
out vec4 fragColor;

// RGB 转 HSV
vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

void main() {
    vec4 originalColor = texture(DiffuseSampler, texCoord);
    vec3 hsv = rgb2hsv(originalColor.rgb);

    // 判断是否为天空：蓝色调范围内，且有适当饱和度和明度
    bool isSky = (hsv.x >= SkyHueMin && hsv.x <= SkyHueMax) &&
    (hsv.y >= SkySaturationMin) &&
    (hsv.z >= SkyValueMin);

    vec3 finalRGB = isSky ? vec3(1.0, 0.0, 0.0) : vec3(0.0, 0.0, 0.0);
    vec3 mixedRGB = mix(originalColor.rgb, finalRGB, Intensity);
    fragColor = vec4(mixedRGB, originalColor.a);
}