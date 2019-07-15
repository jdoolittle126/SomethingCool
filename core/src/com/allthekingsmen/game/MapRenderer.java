package com.allthekingsmen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector3;

public class MapRenderer {
	static int VIEW_W = 20;
	static int VIEW_H = 15;
	
	Map map;
	OrthographicCamera cam;
	SpriteCache cache_floor, cache_material;
	SpriteBatch batch = new SpriteBatch(5460);
	ImmediateModeRenderer20 renderer = new ImmediateModeRenderer20(false, true, 0);
	int[][][] chunks, h;
	FPSLogger fps = new FPSLogger();
	
	private float px, py, px_old, py_old;
	private int c0x, c0y, c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, c5x, c5y, c6x, c6y, c7x, c7y, c8x, c8y;
	
	//Materials
	TextureRegion texture_floor_rock, texture_floor_water, texture_floor_sand, texture_floor_dirt, texture_floor_grass, texture_floor_farm, tile;
	Sprite playerSprite, texture_light;

	public MapRenderer (Map map) {

		loadMaterials();
		this.map = map;
		this.cam = new OrthographicCamera(VIEW_W, VIEW_H);
		this.cam.position.set(map.player.pos.x, map.player.pos.y, 0);
		this.cache_floor = new SpriteCache(Map.MAP_H*Map.MAP_W, false);
		this.cache_material = new SpriteCache(Map.MAP_H*Map.MAP_W, false);
		this.chunks = new int[2][(int) Math.ceil(Map.MAP_W/VIEW_W)][(int) Math.ceil(Map.MAP_H/VIEW_H)];
		h = new int[2][3][3];
		updateChunkInfo();
		
		buildMap();
	}
	
	private void loadMaterials() {
		this.texture_floor_rock = new TextureRegion(new Texture(Gdx.files.internal("assets\\rock.png")), 0, 0, 40, 40);
		this.texture_floor_water = new TextureRegion(new Texture(Gdx.files.internal("assets\\water.png")), 0, 0, 40, 40);
		this.texture_floor_sand = new TextureRegion(new Texture(Gdx.files.internal("assets\\sand.png")), 0, 0, 40, 40);
		this.texture_floor_dirt = new TextureRegion(new Texture(Gdx.files.internal("assets\\dirt.png")), 0, 0, 40, 40);
		this.texture_floor_grass = new TextureRegion(new Texture(Gdx.files.internal("assets\\grass.png")), 0, 0, 40, 40);
		this.texture_floor_farm = new TextureRegion(new Texture(Gdx.files.internal("assets\\farm.png")), 0, 0, 40, 40);
		this.tile = new TextureRegion(new Texture(Gdx.files.internal("assets\\block.png")), 0, 0, 40, 40);
		this.texture_light = new Sprite(new Texture(Gdx.files.internal("assets\\black.png")));
	}
	
	private void buildMap() {
		for (int cy = 0; cy < chunks[0][0].length; cy++) {
			for (int cx = 0; cx < chunks[0].length; cx++) {
				buildFloor(cx, cy);
				buildMaterial(cx, cy);
			}
		}	
	}
	
	private void buildFloor(int cx, int cy) {
		cache_floor.beginCache();
		for (int y = cy * VIEW_H; y < cy * VIEW_H + VIEW_H; y++) {
			for (int x = cx * VIEW_W; x < cx * VIEW_W + VIEW_W; x++) {
				if (x > Map.MAP_W || y > Map.MAP_H) continue;
				switch(map.data_floor[x][y]) {
				   case Map.FLOOR_GRASS:
					   cache_floor.add(texture_floor_grass, x, Map.MAP_H - (y+1), 1f, 1f);
				      break;
				   case Map.FLOOR_ROCK:
					  cache_floor.add(texture_floor_rock, x, Map.MAP_H - (y+1), 1f, 1f);
				      break;
				   case Map.FLOOR_DIRT:
					  cache_floor.add(texture_floor_dirt, x, Map.MAP_H - (y+1), 1f, 1f);
				      break;
				   case Map.FLOOR_WATER:
					  cache_floor.add(texture_floor_water, x, Map.MAP_H - (y+1), 1f, 1f);
				      break;
				   case Map.FLOOR_SAND:
					  cache_floor.add(texture_floor_sand, x, Map.MAP_H - (y+1), 1f, 1f);
				      break;
				   case Map.FLOOR_FARM:
					  cache_floor.add(texture_floor_farm, x, Map.MAP_H - (y+1), 1f, 1f);
				      break;
				}
					
				}
			}
		chunks[0][cx][cy] = cache_floor.endCache();
	}
	
	private void buildMaterial(int cx, int cy) {
		cache_material.beginCache();
		for (int y = cy * VIEW_H; y < cy * VIEW_H + VIEW_H; y++) {
			for (int x = cx * VIEW_W; x < cx * VIEW_W + VIEW_W; x++) {
				if (x > Map.MAP_W || y > Map.MAP_H) continue;
				TextureRegion mat = null;
				switch(map.data_material[x][y]) {
					case Map.MATERIAL_ROCK:
						cache_material.add(tile, x, Map.MAP_H - (y+1), 1f, 1f);	
						break;
				}
				
				
				
				}
			}
		chunks[1][cx][cy] = cache_material.endCache();
	}

	Vector3 lerpTarget = new Vector3();
	
	public void render (float deltaTime) {
		
		px = map.player.pos.x;
		py = map.player.pos.y;
		
		if(px != px_old || py != py_old) {
			updateChunkInfo();
			cam.position.lerp(lerpTarget.set(px, py, 0), 2f * deltaTime);
		}
		
		cam.update();
		
		cache_floor.setProjectionMatrix(cam.combined);
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	
		cache_floor.begin();
		
		for (int y1 = 0; y1 < h[0][0].length; y1++) {
			for (int x1 = 0; x1 < h[0].length; x1++) {
				if(h[0][x1][y1] >= 0 && h[0][x1][y1] < chunks[0].length) {
					if(h[1][x1][y1] >= 0 && h[1][x1][y1] < chunks[0][0].length) {
						cache_floor.draw(chunks[0][h[0][x1][y1]][h[1][x1][y1]]);
					}
				}
				
			}
		}
		
		cache_floor.end();
		
		cache_material.setProjectionMatrix(cam.combined);
		cache_material.begin();
		for (int y1 = 0; y1 < h[0][0].length; y1++) {
			for (int x1 = 0; x1 < h[0].length; x1++) {
				if(h[0][x1][y1] >= 0 && h[0][x1][y1] < chunks[0].length) {
					if(h[1][x1][y1] >= 0 && h[1][x1][y1] < chunks[0][0].length) {
						cache_material.draw(chunks[1][h[0][x1][y1]][h[1][x1][y1]]);
					}
				}
				
			}
		}
		cache_material.end();
		
		batch.setProjectionMatrix(cam.combined);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.begin();
		renderPlayer();
		if(map.player.lighting) {
			
		}
		
		
		for (int y1 = 0; y1 < h[0][0].length; y1++) {
			for (int x1 = 0; x1 < h[0].length; x1++) {
				if(h[0][x1][y1] >= 0 && h[0][x1][y1] < chunks[0].length) {
					if(h[1][x1][y1] >= 0 && h[1][x1][y1] < chunks[0][0].length) {
						
						for(int y = 0; y < VIEW_H; y++) {
							for(int x = 0; x < VIEW_W; x++) {
								int draw_x = (h[0][x1][y1]*VIEW_W)+x;
								int draw_y = Map.MAP_H -((h[1][x1][y1]*VIEW_H)+y+1);
								//add lighting cache, update per chunk
								texture_light.setBounds(draw_x, draw_y, 1f, 1f);
								texture_light.setColor(1, 0, 0, 1f-map.data_light[draw_x][(h[1][x1][y1]*VIEW_H)+y]);
								texture_light.draw(batch);
							}
						}

					}
				}
				
			}
		}
		
		
		
		batch.end();
		fps.log();
		
		px_old = px;
		py_old = py;
		
	}

	public void onResize(int w, int h) {
	}
	
	private void renderPlayer() {
		drawPlayer(map.player.getCurrentFrame());

	}
	
	public void drawPlayer(Sprite s) {
		s.setOrigin(1f, 1.7f);
		s.setPosition(map.player.pos.x, map.player.pos.y);
		s.setRotation(map.player.rot);
		s.setSize(2f, 2f);
		s.draw(batch);
	}

	private void updateChunkInfo() {
		/*
		1 2 3
		8 0 4
		7 6 5
		 */
		h[0][1][1] = (int) Math.floor(px / VIEW_W);
		h[0][0][0] = h[0][1][1]-1;
		h[0][1][0] = h[0][1][1];
		h[0][2][0] = h[0][1][1]+1;
		h[0][2][1] = h[0][1][1]+1;
		h[0][2][2] = h[0][1][1]+1;
		h[0][1][2] = h[0][1][1];
		h[0][0][2] = h[0][1][1]-1;
		h[0][0][1] = h[0][1][1]-1;
		h[1][1][1] = (int) Math.floor((Map.MAP_H-(py+1))/VIEW_H);
		h[1][0][0] = h[1][1][1]-1;	
		h[1][1][0] = h[1][1][1]-1;
		h[1][2][0] = h[1][1][1]-1;
		h[1][2][1] = h[1][1][1];
		h[1][2][2] = h[1][1][1]+1;
		h[1][1][2] = h[1][1][1]+1;
		h[1][0][2] = h[1][1][1]+1;
		h[1][0][1] = h[1][1][1];
	}
	
	public void dispose () {
		cache_floor.dispose();
		cache_material.dispose();
		batch.dispose();
		tile.getTexture().dispose();
	}
}
