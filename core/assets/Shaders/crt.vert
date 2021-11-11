#version 300 es

precision mediump float;

in vec4 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform vec2 u_resolution;
uniform float time;
uniform float offset;

//out vec4 v_color;
out vec2 v_texCoord0;

void main() {
    v_texCoord0 = a_texCoord0;
	gl_Position = a_position;
}