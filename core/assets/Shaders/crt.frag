#version 300 es

precision mediump float;

uniform vec2 u_resolution;
uniform float time;
uniform float offset;

//texture 0 
//uniform sampler2D u_texture;
uniform sampler2D tex0;
uniform sampler2D lightmap_sampler;
uniform float light_factor;
 
in vec2 v_texCoord0; 
 
out vec4 fragmentColor;

const vec3 GREENY = vec3(0.9, 1.1, 0.9);

const float PI = 3.1415926535; 

vec2 fishEyeDistortion(vec2 coord){
	float aperture = 178.0;
	float apertureHalf = 0.5 * aperture * (PI / 180.0);
	float maxFactor = sin(apertureHalf);
	
	vec2 fy_uv = coord;
	vec2 fy_xy = 2.0 * coord - 1.0; // convert to [-1.0 ... 1.0]
	
	float d = length(fy_xy); // distance from center
	
	if (d < (2.0 - maxFactor)){
		d = length(fy_xy * maxFactor);

		float z = sqrt(1.0 - d * d);
		float r = atan(d, z) / PI;
		float phi = atan(fy_xy.y, fy_xy.x);
	
		fy_uv.x = r * cos(phi) + 0.5;
		fy_uv.y = r * sin(phi) + 0.5;
		coord = fy_uv;
	}

	return coord;
}

void main() { 
	//sample our texture 
	vec4 texColor = texture(tex0, v_texCoord0); 
	float org_alpha = texColor.a;
	
	vec2 uv = v_texCoord0;
	
	// Fish eye
	//uv = fishEyeDistortion(uv);

	uv.y = 1.0 - uv.y;
	
	vec3 col;

	// RGB shift
	col.r = texture(tex0, fract(vec2(uv.x + offset, -uv.y))).r;
	col.g = texture(tex0, fract(vec2(uv.x + 0.000, -uv.y))).g;
	col.b = texture(tex0, fract(vec2(uv.x - offset, -uv.y))).b;

	// scanline
	col *= 0.9 + 0.1 * sin(10.0 * time - uv.y * 1000.0);
	
	// vignette
	col *= 0.5 + 0.5 * 16.0 * uv.x * uv.y * (1.0 - uv.x) * (1.0 - uv.y);
		 
	//convert to grayscale using NTSC conversion weights 
	//float gray = dot(texColor.rgb, vec3( 0.299, 0.587, 0.114)); 
	 
	// colorize
	col *= GREENY;
		 
	//gl_FragColor = vec4(col, org_alpha);
	//fragmentColor = vec4(col, org_alpha);
    
    // Sample lightmap
    vec4 lightmapColor = texture(lightmap_sampler, v_texCoord0);
    lightmapColor *= light_factor;
    
    fragmentColor = vec4(col + lightmapColor.rgb, org_alpha);
}