package com.allthekingsmen.util;

import com.allthekingsmen.game.Light;
import com.badlogic.gdx.math.MathUtils;

public class LightNode {
	float intensity = Light.DEFAULT_LIGHT_INTENSITY;
	static final float MOD = 0.001f;
	LightNode p;
	float g;
	Coordinates pos;
	
	public LightNode(Coordinates pos) {
		this.pos = pos;
	}
	public LightNode(Coordinates pos, LightNode p) {
		this.pos = pos;
		this.p = p;
	}
	public LightNode(Coordinates pos, float g) {
		this.pos = pos;
		this.g = g;
	}
	public LightNode(Coordinates pos, LightNode p, float g) {
		this.pos = pos;
		this.p = p;
		this.g = g;
	}
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	public void setP(LightNode p) {
		this.p = p;
	}
	public void setG(float g) {
		this.g = g;
	}
	public float totalCost() {
		return g;
	}
	public float getlightValue() {
		if(g>0) return MathUtils.clamp((1/(g*g*(1/intensity))), 0, 1)-MOD;
		else return 1-MOD;
	}
	
}