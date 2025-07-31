#version 150

uniform float GameTime;
uniform float animation;
uniform sampler2D Sampler0;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

#define iterations 17
#define formuparam 0.53

#define volsteps 20
#define stepsize 0.1

#define zoom   0.800
#define tile   0.850
#define speed  0.001

#define brightness 0.0005
#define darkmatter 0.300
#define distfading 0.730
#define saturation 0.850

float hash(vec2 p)
{
    return fract(sin(dot(p, vec2(127.1,311.7)))*43758.5453123);
}
float noise(vec2 p)
{
    vec2 i = floor(p);
    vec2 f = fract(p);
    // 四顶点插值
    float a = hash(i);
    float b = hash(i + vec2(1.0,0.0));
    float c = hash(i + vec2(0.0,1.0));
    float d = hash(i + vec2(1.0,1.0));
    // hermite 插值
    vec2 u = f*f*(3.0-2.0*f);
    return mix(a, b, u.x) +
           (c - a)* u.y * (1.0 - u.x) +
           (d - b) * u.x * u.y;
}


float easeInCirc(float i){
    return 1. - sqrt(1. - i*i);
}

// ==== 漩涡溶解主过程 ====
float cir(vec2 fragCoord )
{
    // 标准化并居中屏幕坐标
    vec2 uv = fragCoord.xy;
    uv = uv * 2.0 - 1.0;

    // 极坐标
    float r = length(uv);
    float theta = atan(uv.y, uv.x);
    
    // ==== 漩涡变换 ====
    // 扭曲幅度函数：半径越小扭曲越大，越外圈扭曲越小
    float swirlAmount = 3.;                    // 漩涡最大强度
    float swirl = swirlAmount * exp(-r*2.3);    // exp衰减
    float time = GameTime*1000.*0.8;

    // 时间动态，且让漩涡随时间顺逆时针周期变化
    float swirlDynamic = swirl * time/100.;

    // 漩涡扰动后极坐标
    float theta2 = theta + swirlDynamic;

    // 转回笛卡尔坐标，取得扭曲后的噪音采样点
    vec2 uv3 = vec2(cos(theta2), sin(theta2));
    vec2 uv2 = uv3 * r;
    uv2 = uv2*0.8 + time*0.05;  // 可调缩放和飘移

    // ==== 柏林噪音采样 ====
    float n = 0.0;
    n += 0.6*noise(7.0*uv2 + 0.2*time);
    n += 0.3*noise(13.2*uv2 - 0.13*time);
    n += 0.1*noise(23.2*uv2 + 0.08*time);

    float t = easeInCirc(animation);
	float offset = mix(0.3,0.,t);
    n = mix(0.,n,smoothstep(0.65+offset-t,0.8+offset-t,length(uv)));
    n = mix(1.,n,smoothstep(1.0+offset-t,0.9+offset-t,length(uv)));
    // 动态亮暗色调
    return n*2.;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	//get coords and direction
	vec2 uv=fragCoord-.5;
	if(length(uv)>0.5) discard;
  
	vec3 dir=vec3(uv*zoom,1.);
	float time=GameTime*1000.*speed+.25;

	//mouse rotation
	float a1=.5;
	float a2=.8;
	mat2 rot1=mat2(cos(a1),sin(a1),-sin(a1),cos(a1));
	mat2 rot2=mat2(cos(a2),sin(a2),-sin(a2),cos(a2));
	dir.xz*=rot1;
	dir.xy*=rot2;
	vec3 from=vec3(1.,.5,0.5);
	from+=vec3(time*1.,time,-1.);
	from.xz*=rot1;
	from.xy*=rot2;
	
	//volumetric rendering
	float s=0.1,fade=1.;
	vec3 v=vec3(0.);
	for (int r=0; r<volsteps; r++) {
		vec3 p=from+s*dir*.5;
		p = abs(vec3(tile)-mod(p,vec3(tile*2.))); // tiling fold
		float pa,a=pa=0.;
		for (int i=0; i<iterations; i++) { 
			p=abs(p)/dot(p,p)-formuparam; // the magic formula
			a+=abs(length(p)-pa); // absolute sum of average change
			pa=length(p);
		}
		float dm=max(0.,darkmatter-a*a*.001); //dark matter
		a*=a*a; // add contrast
		if (r>6) fade*=1.-dm; // dark matter, don't render near
		//v+=vec3(dm,dm*.5,0.);
		v+=fade;
		v+=vec3(s,s*s,s*s*s*s)*a*brightness*fade; // coloring based on distance
		fade*=distfading; // distance fading
		s+=stepsize;
	}
	float ap = 1.;

	v=mix(vec3(length(v)),v,saturation); //color adjust
	if(animation<=1.) {
		ap = cir(fragCoord);
	}

	fragColor = vec4(v*.01,ap);	
	
}

void main() {
    mainImage(fragColor, texCoord0);
}