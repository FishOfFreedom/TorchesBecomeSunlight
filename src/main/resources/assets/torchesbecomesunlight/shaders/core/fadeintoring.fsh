#version 150

uniform float GameTime;
uniform vec4 color1;
uniform float animation;
uniform sampler2D Sampler0;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void mainImage(out vec4 fragColor,in vec2 fragCoord)
{
    vec2 uv1 = fragCoord/iResolution.xy;

    vec2 uv = fragCoord/iResolution.xy;
    vec4 color = texture(Sampler0,uv);
    uv -= 0.5;
    float le = length(uv)*2.;
    float v = animation;
    vec4 color1 = mix(vec4(1.),color,smoothstep(v,v+0.1,le));
    vec4 color2 = mix(color,vec4(1.),smoothstep(v+0.1,v+0.2,le));
    vec4 color3 = mix(color1,color2,smoothstep(v,v+0.2,le));

    fragColor = color3;
}

void main() {
    mainImage(fragColor,texCoord0);
}
