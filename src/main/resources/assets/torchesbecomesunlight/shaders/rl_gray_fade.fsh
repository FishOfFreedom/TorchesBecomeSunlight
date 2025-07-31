#version 150

in vec2 screenPos;
in vec2 guiPos;
in vec2 uv;

out vec4 fragColor;

uniform vec2 ScreenSize;
uniform vec4 Color = vec4(1.0);
uniform float GuiScale;
uniform vec4 SquareVertex;

uniform float animation;

vec4 run(vec2 uv, vec4 squareVertex){
    uv.x = uv.x + 1-((squareVertex.x+squareVertex.z)/ScreenSize.x);
    uv.y = uv.y - 1+((squareVertex.y+squareVertex.w)/ScreenSize.y);
    uv.x = uv.x *ScreenSize.x / (squareVertex.z - squareVertex.x);
    uv.y = uv.y *ScreenSize.y / (squareVertex.w - squareVertex.y);

    float sizeY = (20./ScreenSize.y);
    float sizeX = (20./ScreenSize.x);

    vec4 fragColor = mix(vec4(0.),Color,smoothstep(-1.-sizeY,-1.,uv.y)*smoothstep(1.+sizeY,1.,uv.y));
    fragColor = mix(vec4(0.),fragColor,smoothstep(-1.-sizeX,-1.,uv.x)*smoothstep(1.+sizeX,1.,uv.x));
    float aaa = mix(0.,fragColor.a,smoothstep(0.+animation,0.1+animation,(uv.x+1.)/2.));
    if(aaa<0.){
        discard;
    }

    fragColor = vec4(fragColor.rgb,aaa);
    return fragColor;
}

void main() {
    fragColor = run(uv,SquareVertex * GuiScale);
}