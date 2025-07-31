#version 150

uniform float GameTime;
uniform vec4 color1;
uniform float animation;
uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 uv = fragCoord*2.-0.5;

    // 使用时间变化的阈值控制溶解进度
    float threshold = mod(animation, 1.0);

    // 生成噪声 - 使用分形噪声增加细节
    float noise = 0.0;
    vec2 noiseUV = uv / 5.0;
    float amp = 0.5;

    for (int i = 0; i < 5; i++) {
        noise += texture(Sampler1, noiseUV).r * amp;
        noiseUV *= 1.8;
        amp *= 0.6;
    }

    // 溶解计算
    float dissolve = step(threshold, noise);
    float edge = smoothstep(threshold - 0.05, threshold + 0.05, noise);

    // 添加背景网格
    vec4 bg = texture(Sampler0,fragCoord);
    // 组合效果
    vec3 color = vec3(0.0);
    float al = bg.w;

    // 未溶解部分 (白色)
    if (dissolve > 0.5) {
        color = vec3(0.0);
        al = 0.;
    }

    // 溶解边缘 (橙色发光效果)
    if (edge > 0.0 && edge < 1.0) {
        float glow = pow(1.0 - abs(noise - threshold) * 2.0, 2.0);
        color = mix(color, vec3(1.0), glow * 3.0);
    }


    // 最终混合 (溶解部分透明)
    vec3 finalColor = mix(bg.xyz, color, dissolve);

    fragColor = vec4(finalColor, al);
}

void main() {
    mainImage(fragColor,texCoord0);
}
