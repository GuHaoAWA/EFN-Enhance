#version 150

uniform sampler2D DiffuseSampler;     // 原始场景
uniform sampler2D MaskSampler;         // 球体遮罩
uniform vec4 BackgroundColor;          // 背景色（红色）

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 originalColor = texture(DiffuseSampler, texCoord);
    float mask = texture(MaskSampler, texCoord).r;

    // 简化：任何有颜色的像素都视为实体
    bool hasColor = originalColor.r > 0.01 ||
    originalColor.g > 0.01 ||
    originalColor.b > 0.01;

    // 天空识别：通过蓝色调
    bool isBlueDominant = originalColor.b > originalColor.r * 0.8 &&
    originalColor.b > originalColor.g * 0.8;

    // 天空特征：蓝色调且亮度适中
    float luminance = (originalColor.r + originalColor.g + originalColor.b) / 3.0;
    bool isSky = isBlueDominant && luminance > 0.1 && luminance < 0.9;

    // 实体：不是天空且有颜色
    bool isEntity = !isSky && hasColor;

    if (mask > 0.5) {
        // 球体内
        if (isEntity) {
            // 实体纯黑
            fragColor = vec4(0.0, 0.0, 0.0, 1.0);
        } else {
            // 背景纯红
            fragColor = BackgroundColor;
        }
    } else {
        // 球体外
        if (isEntity) {
            // 实体纯黑
            fragColor = vec4(0.0, 0.0, 0.0, 1.0);
        } else {
            // 背景纯红
            fragColor = BackgroundColor;
        }
    }
}