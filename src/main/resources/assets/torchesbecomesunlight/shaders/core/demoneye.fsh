#version 150

uniform float GameTime;
uniform sampler2D Sampler0;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float rand(vec2 co){
	return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float hermite(float t)
{
	return t * t * (3.0 - 2.0 * t);
}

float noise(vec2 co, float frequency)
{
	vec2 v = vec2(co.x * frequency, co.y * frequency);

	float ix1 = floor(v.x);
	float iy1 = floor(v.y);
	float ix2 = floor(v.x + 1.0);
	float iy2 = floor(v.y + 1.0);

	float fx = hermite(fract(v.x));
	float fy = hermite(fract(v.y));

	float fade1 = mix(rand(vec2(ix1, iy1)), rand(vec2(ix2, iy1)), fx);
	float fade2 = mix(rand(vec2(ix1, iy2)), rand(vec2(ix2, iy2)), fx);

	return mix(fade1, fade2, fy);
}

float pnoise(vec2 co, float freq, int steps, float persistence)
{
	float value = 0.0;
	float ampl = 1.0;
	float sum = 0.0;
	for(int i=0 ; i<steps ; i++)
	{
		sum += ampl;
		value += noise(co, freq) * ampl;
		freq *= 2.0;
		ampl *= persistence;
	}
	return value / sum;
}

vec4 demon(vec2 uv ) {
	float gradientStep = 0.2;

	vec2 pos = uv;
	pos *= 5.;

	pos -= 2.5;
	float gradient = smoothstep(0.2,0.25,length(pos))*smoothstep(0.4,0.1,length(pos));
	pos = vec2(length(pos),atan(pos.x,pos.y));
	pos.x *= 4.;
	pos.x += -GameTime*1000. * 0.1;

	vec4 brighterColor = vec4( 1.0, 1.0, 1., 1.);
	vec4 darkerColor = vec4(1.0, 1.0, 1., 0.5);
	vec4 middleColor = mix(brighterColor, darkerColor, 0.5);

	float noiseTexel = pnoise(pos, 10.0, 5, 0.5);

	float firstStep = smoothstep(0.0, noiseTexel, gradient);
	float darkerColorStep = smoothstep(0.0, noiseTexel, gradient - gradientStep);
	float darkerColorPath = firstStep - darkerColorStep;
	vec4 color = mix(brighterColor, darkerColor, darkerColorPath);

	float middleColorStep = smoothstep(0.0, noiseTexel, gradient - 0.2 * 2.0);

	color = mix(color, middleColor, darkerColorStep - middleColorStep);
	color = mix(vec4(0.), color, firstStep);
	vec4 finalColor = color;
	return finalColor;
}

vec4 demon1(vec2 uv ) {
	float gradientStep = 0.2;

	vec2 pos = uv;
	pos *= 10.;

	pos -= 5.;
	float gradient = smoothstep(-0.1,0.075,pos.y)*smoothstep(0.1,-0.075,pos.y);

	//pos.y*=20.;
	gradient *= (0.5-abs(0.5-uv.x))*6.*smoothstep(0.42,0.5,length(pos))*smoothstep(16.,5.,length(pos));
	//float gradient = 0.1;
	//pos.y/=20.;
	pos = vec2(length(pos),atan(pos.x,pos.y));
	pos.x *= 4.;
	pos.x += -GameTime*1000. * 2.;

	vec4 brighterColor = vec4( 1.0, 1.0, 1., 1.);
	vec4 darkerColor = vec4(1.0, 1.0, 1., 1.);
	vec4 middleColor = mix(brighterColor, darkerColor, 0.5);
	pos.x/=10.;
	float noiseTexel = pnoise(pos, 10.0, 5, 0.5);

	float firstStep = smoothstep(0.0, noiseTexel, gradient);
	float darkerColorStep = smoothstep(0.0, noiseTexel, gradient - gradientStep);
	float darkerColorPath = firstStep - darkerColorStep;
	vec4 color = mix(brighterColor, darkerColor, darkerColorPath);

	float middleColorStep = smoothstep(0.0, noiseTexel, gradient - 0.2 * 2.0);

	color = mix(color, middleColor, darkerColorStep - middleColorStep);
	color = mix(vec4(0.), color, firstStep);
	vec4 finalColor = color;
	return finalColor;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
	vec2 uv= fragCoord;
	vec4 da =  demon1(uv)+demon(uv);
	fragColor = da*vertexColor ;
}

void main() {
    mainImage(fragColor, texCoord0);
}