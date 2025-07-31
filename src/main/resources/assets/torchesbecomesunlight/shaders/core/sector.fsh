#version 150

const float PI = 3.14159265;

uniform float GameTime;
uniform vec4 color1;
uniform float radiu;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 uv = fragCoord*2.-0.5;

    uv-=0.5;
    uv = vec2(length(uv),atan(uv.x,uv.y));

    float col = smoothstep(-PI+radiu,-PI+radiu+0.01,uv.y)*smoothstep(PI-radiu,PI-radiu-0.01,uv.y);
    col = 1.-mix(col,1.,smoothstep(0.99,1,uv.x));

    fragColor = mix(vec4(0.),vertexColor,col);
}

void main() {
    mainImage(fragColor,texCoord0);
}
