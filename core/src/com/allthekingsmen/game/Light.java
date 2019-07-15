package com.allthekingsmen.game;

import com.allthekingsmen.util.LightCalculator;
import com.badlogic.gdx.math.MathUtils;

public class Light {
	private int x, y;
	private float intensity;
	private float[][] influence;
	private boolean update;
	private boolean isOn;
	
	public static final int LIGHT_RADIUS = 25;
	public static final float DEFAULT_LIGHT_INTENSITY = 40f;
	
	public Light(int x, int y) {
		this.x = x;
		this.y = y;
		this.intensity = DEFAULT_LIGHT_INTENSITY;
		this.update = true;
		this.isOn = true;
	}
	
	public Light(int x, int y, float intensity) {
		this.x = x;
		this.y = y;
		this.intensity = intensity;
		this.update = true;
		this.isOn = true;
	}
	
	public void translate(int dx, int dy) {
		x = MathUtils.clamp(x+dx, 0, Map.MAP_W-1);
		y = MathUtils.clamp(y+dy, 0, Map.MAP_H-1);
		update = true;
	}
	
	public void setX(int x) {
		this.x = x;
		update = true;
	}
	
	public void setY(int y) {
		this.y = y;
		update = true;
	}
	
	public void switchState() {
		this.isOn = !this.isOn;
	}
	
	public void setState(boolean state) {
		this.isOn = state;
	}
	
	public void setPos(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public boolean needsUpdate() {
		return update;
	}
	
	public float[][] getInfluenceArray(){
		return influence;
	}
	
	public void buildLight(int[][] data_material, float[][] data_light, boolean[][] data_border) {
		if(isOn) {
			boolean u, d, l, r;
			if(data_light[x][y] == 1f) {
					l = x == 0 || data_light[x-1][y] == 1f;
					r = x == Map.MAP_W-1 || data_light[x+1][y] == 1f;
					d = y == 0 || data_light[x][y-1] == 1f;
					u = y == Map.MAP_H-1 || data_light[x][y+1] == 1f;
					if(!l||!r||!u||!d) {
						boolean[][] a = new boolean[LIGHT_RADIUS*2 + 1][LIGHT_RADIUS*2 + 1];
						for(int x1 = x-LIGHT_RADIUS; x1 < x+LIGHT_RADIUS+1; x1++) {
							for(int y1 = y-LIGHT_RADIUS; y1 < y+LIGHT_RADIUS+1; y1++) {
								if(x1 < 0 || x1 > Map.MAP_W-1) continue;
								if(y1 < 0 || y1 > Map.MAP_H-1) continue;
								if(data_material[x1][y1] != Map.MATERIAL_EMPTY && !data_border[x1][y1]) continue;
								if(data_light[x1][y1] == 1f) continue;
								a[((x1+LIGHT_RADIUS)-x)][((y1+LIGHT_RADIUS)-y)] = true;
							}
						}
						LightCalculator.setup(a);
						LightCalculator.calc();
						float[][] values = LightCalculator.getAllValues();
						influence = new float[Map.MAP_W][Map.MAP_H];
						
						for(int x1 = x-LIGHT_RADIUS; x1 < x+LIGHT_RADIUS+1; x1++) {
							for(int y1 = y-LIGHT_RADIUS; y1 < y+LIGHT_RADIUS+1; y1++) {
								if(x1 < 0 || x1 > Map.MAP_W-1) continue;
								if(y1 < 0 || y1 > Map.MAP_H-1) continue;
								influence[x1][y1] = values[((x1+LIGHT_RADIUS)-x)][((y1+LIGHT_RADIUS)-y)];
						}
					}
				} else {
					influence = new float[Map.MAP_W][Map.MAP_H];
					influence[x][y] = 1f;
				}
				update = false;
			}
		}
	}
		
		
}
