#version 150

uniform sampler2D DepthSampler;
uniform vec3 SphereCenter;
uniform float SphereRadius;
uniform vec3 CameraPos;
uniform mat4 InverseProjectionMatrix;
uniform mat4 InverseViewMatrix;

in vec2 texCoord;
out vec4 fragColor;

// 从屏幕坐标和深度重建世界坐标
vec3 worldPositionFromDepth(float depth, vec2 uv) {
    vec4 clipPos = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 viewPos = InverseProjectionMatrix * clipPos;
    viewPos /= viewPos.w;
    vec4 worldPos = InverseViewMatrix * viewPos;
    return worldPos.xyz;
}

void main() {
    float depth = texture(DepthSampler, texCoord).r;

    float mask = 0.0;

    // 重建世界坐标
    vec3 worldPos = worldPositionFromDepth(depth, texCoord);

    // 计算到球心的距离
    float distToCenter = length(worldPos - SphereCenter);

    // 生成遮罩：球体内为1，球体外为0
    if (distToCenter <= SphereRadius) {
        mask = 1.0;

        // 边缘羽化
        float distToSurface = SphereRadius - distToCenter;
        if (distToSurface < 1.0) {
            mask = distToSurface;
        }
    }

    // 处理天空盒
    if (depth >= 0.999) {
        // 检查视线是否与球体相交
        vec3 rayDir = normalize(worldPos - CameraPos);
        vec3 oc = CameraPos - SphereCenter;
        float a = dot(rayDir, rayDir);
        float b = 2.0 * dot(oc, rayDir);
        float c = dot(oc, oc) - SphereRadius * SphereRadius;
        float discriminant = b * b - 4.0 * a * c;

        if (discriminant >= 0.0) {
            float t = (-b - sqrt(discriminant)) / (2.0 * a);
            if (t > 0.0) {
                mask = 1.0;
            }
        }
    }

    // 输出遮罩（红色通道）
    fragColor = vec4(mask, mask, mask, 1.0);
}