package com.allthekingsmen.game;

import java.util.ArrayList;
import java.util.Arrays;

import com.allthekingsmen.util.LightCalculator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

public class Map {
	//Settings
	public static final int MAP_W = 100;
	public static final int MAP_H = 100;
	static final int[][] MAP_FORMAT = {{0,0},{1,0},{0,1},{1,1}};
	
	//TILE
	static final int TILE_EMPTY = 0x000000;
	static final int TILE_FILLED = 0xffffff;
	static final int TILE_SPAWN = 0xff0000;
	
	//LIGHT
	static final int LIGHT_EMPTY = 0x000000;
	static final int LIGHT_NATURAL = 0xff0000;
	
	//FLOOR
	static final int FLOOR_GRASS = 0x00ff00;
	static final int FLOOR_ROCK = 0x646464;
	
	static final int FLOOR_DIRT = 0x573d00;
	static final int FLOOR_WATER = 0x0000ff;
	static final int FLOOR_SAND = 0xfcecce;
	static final int FLOOR_FARM = 0x8b7058;
	
	//MATERIAL
	static final int MATERIAL_EMPTY = 0xff0000;
	static final int MATERIAL_ROCK = 0x000000;
	
	int[][] data_tile;
	float[][] data_base_light;
	float[][] data_light;
	int[][] data_floor;
	int[][] data_material;
	boolean[][] data_border;
	
	public Player player;
	boolean updateLighting;
	
	ArrayList<Light> lights;
	
	public Map() {
		loadBinary();
		rebuildLightMap();
	}
	
	private void loadBinary() {
		Pixmap p = new Pixmap(Gdx.files.internal("assets\\levels.png"));
		data_tile = new int[MAP_W][MAP_H];
		data_base_light = new float[MAP_W][MAP_H];
		data_light = new float[MAP_W][MAP_H];
		data_floor = new int[MAP_W][MAP_H];
		data_material = new int[MAP_W][MAP_H];
		data_border = new boolean[MAP_W][MAP_H];
		lights = new ArrayList<Light>();
		
		for (int y = 0; y < MAP_W; y++) {
			for (int x = 0; x < MAP_H; x++) {
				processBinaryTile(p, x, y);
				processBinaryLight(p, x, y);
				processBinaryFloor(p, x, y);
				processBinaryMaterial(p, x, y);
			}
		}
	}
	private void processBinaryTile(Pixmap pixmap, int x, int y) {
		int p = getPixel(pixmap, x+(MAP_W*MAP_FORMAT[0][0]), y+(MAP_H*MAP_FORMAT[0][1]));
		switch(p) {
		   case TILE_EMPTY:
		      break;
		   case TILE_FILLED:
			   data_tile[x][y] = TILE_FILLED;
		      break;
		   case TILE_SPAWN:
			   player = new Player(this, x, MAP_H - (y+1));
			   break;
		   default: 
			   //Map error?
		}
		
	}
	private void processBinaryLight(Pixmap pixmap, int x, int y) {
		updateLighting = true;
		int p = getPixel(pixmap, x+(MAP_W*MAP_FORMAT[1][0]), y+(MAP_H*MAP_FORMAT[1][1]));
		switch(p) {
		   case LIGHT_EMPTY:
			   data_base_light[x][y] = 0f;
		      break;
		   case LIGHT_NATURAL:
			   data_base_light[x][y] = 1f;
		      break;
		   default: 
			   data_base_light[x][y] = 0f;
		}
	}
	private void processBinaryFloor(Pixmap pixmap, int x, int y) {
		data_floor[x][y] = getPixel(pixmap, x+(MAP_W*MAP_FORMAT[2][0]), y+(MAP_H*MAP_FORMAT[2][1]));
	}
	private void processBinaryMaterial(Pixmap pixmap, int x, int y) {
		data_material[x][y] = getPixel(pixmap, x+(MAP_W*MAP_FORMAT[3][0]), y+(MAP_H*MAP_FORMAT[3][1]));
	}
	
	private int getPixel(Pixmap pixmap, int x, int y) {
		return  ((pixmap.getPixel(x, y) >>> 8) & 0xffffff);
	}
	
	private void rebuildLightMap() {
		rebuildBorderMap();
		lights.clear();
		for (int y = 0; y < MAP_W; y++) {
			for (int x = 0; x < MAP_H; x++) {
				if(data_base_light[x][y] == 1f) lights.add(new Light(x, y));
			}
		}
	}
	
	private void rebuildBorderMap() {
		boolean u = false, d = false, l = false, r = false;
		for (int y = 0; y < MAP_W; y++) {
			for (int x = 0; x < MAP_H; x++) {
				if(data_material[x][y] != MATERIAL_EMPTY) {
					if(x != 0) l = data_material[x-1][y] == MATERIAL_EMPTY;
					if(x != MAP_W-1) r = data_material[x+1][y] == MATERIAL_EMPTY;
					if(y != 0) d = data_material[x][y-1] == MATERIAL_EMPTY;
					if(y != MAP_H-1) u = data_material[x][y+1] == MATERIAL_EMPTY;
					data_border[x][y] = l||r||u||d;
				}
			}
		}
	}
	
	public void update (float deltaTime) {
		player.update(deltaTime);
		
		for(Light l : lights) {
			if(l.needsUpdate()) l.buildLight(data_material, data_base_light, data_border);
		}
		
		//THIS IS THE SPEED CAP
		if(updateLighting) {
			data_light = new float[MAP_W][MAP_H];
			for(Light l : lights) {
				for (int y = 0; y < MAP_W; y++) {
					for (int x = 0; x < MAP_H; x++) {
						if(l.getInfluenceArray()[x][y] > 0f) {
							if(data_light[x][y] > 0f) {
								if(data_light[x][y] < l.getInfluenceArray()[x][y]) data_light[x][y] = l.getInfluenceArray()[x][y];
							} else {
								data_light[x][y] = l.getInfluenceArray()[x][y];
							}
						}
					}
				}
				
			}
		updateLighting = false;
		}
		
	}

}
