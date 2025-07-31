#version 150

in vec2 screenPos;
in vec2 guiPos;
in vec2 uv;

out vec4 fragColor;

uniform vec2 ScreenSize;
uniform vec4 Color = vec4(1.0);
uniform float GuiScale;
uniform vec4 SquareVertex;

uniform float radia;
uniform float radiaOffset;
uniform float r;

vec4 run(vec2 uv, vec4 squareVertex){
    uv.x = uv.x + 1-((squareVertex.x+squareVertex.z)/ScreenSize.x);
    uv.y = uv.y - 1+((squareVertex.y+squareVertex.w)/ScreenSize.y);

    uv.x = uv.x *ScreenSize.x / (squareVertex.z - squareVertex.x);
    uv.y = uv.y *ScreenSize.y / (squareVertex.w - squareVertex.y);

    //uv.x = uv.x-((squareVertex.x+(squareVertex.z - squareVertex.x)/2)/ScreenSize.x)*ScreenSize.x / (squareVertex.z - squareVertex.x);
    //uv.y = uv.y+((squareVertex.y+(squareVertex.w - squareVertex.y)/2)/ScreenSize.y)*ScreenSize.y / (squareVertex.w - squareVertex.y);
    //uv *= ScreenSize / 2.;
    //uv.x += ScreenSize.x / 2. - squareVertex.x - (squareVertex.z - squareVertex.x) / 2. - 2.;
    //uv.y -= ScreenSize.y / 2. - squareVertex.y - (squareVertex.w - squareVertex.y) / 2. - 2.;
    //uv /= ScreenSize / 2.;

    vec2 porlar = uv;
    float dist = length(porlar);
    vec4 col;
    col = mix(Color,vec4(Color.rgb,0.),smoothstep(r-0.01,r,dist));

    vec4 col1;
    float raido = atan(porlar.y,porlar.x);
    if(raido>radiaOffset&&raido<radiaOffset+radia){
        col1 = col;
    }else{
        col1 = vec4(0.);
    }

    vec4 fragColor = col1;
    return fragColor;
}

void main() {
    fragColor = run(uv,SquareVertex * GuiScale);
}