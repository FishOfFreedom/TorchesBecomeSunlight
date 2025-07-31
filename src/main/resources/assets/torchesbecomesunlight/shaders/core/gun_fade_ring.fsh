#version 150

uniform float GameTime;
uniform vec4 color1;
uniform float animation;
uniform sampler2D Sampler0;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

const float wave = 1.6;

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    float iTime = GameTime*1000.;
    vec2 uv = fragCoord*2.-0.5;
    
    vec2 uv1 = uv-0.5;
    uv = vec2((atan(uv1.y,uv1.x)+3.14)/6.283185,length(uv1)*2.);
    
    
    // 基础线条位置（屏幕中央）
    float baseY = 2.1-animation*2.1;
    
    // 创建不规则波动 - 使用多种噪声函数组合
    float noise = 0.0;
    
    // 使用分形布朗运动(FBM)创建复杂的不规则波动
    float timeScale = iTime * 0.7;
    noise += sin(uv.x * 8.0 + timeScale) * 0.5*wave;
    noise += sin(uv.x * 15.0 + timeScale * 1.7) * 0.1*wave;
    noise += sin(uv.x * 25.0 + timeScale * 2.3) * 0.05*wave;
    
    // 添加一些随机变化
    float randTime = iTime * 1.2;
    noise += sin(uv.x * 6.0 + randTime) * 0.08;
    
    // 确保头尾相连 - 使用周期函数
    float periodic = sin(uv.x * 6.283185); // 2π
    noise += periodic * 0.05 * sin(iTime * 0.5);
    
    float edge = smoothstep(0.0, 0.05, uv.x) * 
                smoothstep(1.0, 0.95, uv.x);
    noise = mix(baseY-0.55,noise,edge);
    
    // 最终波动位置
    float waveAmplitude = 0.15;
    float lineY = baseY + noise * waveAmplitude;
    
    
    // 混合背景和线条颜色
    //vec3 color = mix(bgColor, lineColor, line);
    //vec3 color = mix(vec3(0.), vec3(1.), smoothstep(lineY,lineY+0.03,uv.y));
    vec4 tex = texture(Sampler0,fragCoord);
    vec4 color = vec4(tex.xyz,mix(0.,tex.w,smoothstep(lineY,lineY+0.03,uv.y)));
    
    fragColor = color;
}

void main() {
    mainImage(fragColor,texCoord0);
}
