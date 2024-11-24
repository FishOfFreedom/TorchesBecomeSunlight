#version 150

#moj_import <fog.glsl>
#moj_import <matrix.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform float GameTime;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

const float RETICULATION = 3.;
const float NB_ARMS = 5.;
const float COMPR = .1;
const float SPEED = 3.6;
const float GALAXY_R = 1./2.;
const float BULB_R = 1./2.5;
const vec4 GALAXY_COL = vec4(.9,.1,.1,.8);
const vec4 BULB_COL   = vec4(1.,1.0,1.0,.8);
const float BULB_BLACK_R = 1./4.;
const vec4 BULB_BLACK_COL   = vec4(0,0,0,1);
const vec4 SKY_COL    = vec4(0.,0.,0.,0.);

#define Pi 3.14

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float tex(vec2 uv)
{
    float n = texture(Sampler0,uv).r;
    return 1.-abs(2.*n-1.);
}


float noise(vec2 uv)
{
    float v=0.;
    float a=-SPEED*GameTime* 1000,co=cos(a),si=sin(a);
    mat2 M = mat2(co,-si,si,co);
    float s=1.;
    for (int i=0; i<7; i++)
    {
        uv = M*uv;
        float b = tex(uv*s);
        v += 1./s* pow(b,RETICULATION);
        s *= 2.;
    }
    return v/2.;
}

void main() {
	vec2 uv = texCoord0-0.5;
	vec4 col;

	float rho = length(uv);
	float ang = atan(uv.y,uv.x);
	float shear = 2.*log(rho);
	float c = cos(shear), s=sin(shear);
	mat2 R = mat2(c,-s,s,c);

	float r;
	r = rho/GALAXY_R; float dens = exp(-r*r);
	r = rho/BULB_R;	  float bulb = exp(-r*r);
	r = rho/BULB_BLACK_R; float bulb_black = exp(-r*r);
	float phase = NB_ARMS*(ang-shear);
	float rang = ang-COMPR*cos(phase)+SPEED*GameTime*1000;
	vec2 uv1 = rho*vec2(cos(rang),sin(rang));
	float spires = 1.+NB_ARMS*COMPR*sin(phase);
	dens *= .7*spires;

	float gaz = noise(.09*1.2*R*uv1);
	float gaz_trsp = pow((1.-gaz*dens),2.);

	float ratio = .35+GameTime;
	float stars = texture(Sampler0,ratio*uv1+.5).r;

	col = mix(SKY_COL,
			  gaz_trsp*(1.7*GALAXY_COL) + 1.2*stars,
			  dens);
	col = mix(col, 2.*BULB_COL,.5* bulb);
    col = mix(col, 1.2*BULB_BLACK_COL, 1.9*bulb_black);
    col.w = col.w*smoothstep(0.5,0.35,rho);

	fragColor = vec4(col);
}
