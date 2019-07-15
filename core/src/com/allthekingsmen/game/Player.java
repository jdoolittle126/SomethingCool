package com.allthekingsmen.game;

import java.util.HashMap;

import com.allthekingsmen.util.AStar;
import com.allthekingsmen.util.Coordinates;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player {
	static final int UP = 2;
	static final int DOWN = 0;
	static final int LEFT = 3;
	static final int RIGHT = 1;
	
	static final float ACCELERATION = 15f;
	static final float MAX_VEL = 6f;
	static final float DAMP = 0.95f;
	
	static final int KEY_AUX = Keys.F;
	static final int KEY_PRIM = Buttons.LEFT;
	
	final int KEY_UP = Keys.W;
	final int KEY_DOWN = Keys.S;
	final int KEY_LEFT = Keys.A;
	final int KEY_RIGHT = Keys.D;
	
	final float OFFSET_BOUNDS_X = 0.67f;
	final float OFFSET_BOUNDS_Y = 1.315f;
	
	float rot = 0;
	int direction = UP;
	Vector2 pos = new Vector2();
	Vector2 accel = new Vector2();
	Vector2 vel = new Vector2();
	public Rectangle bounds = new Rectangle();
	public boolean lighting = false;

	int dir = UP;
	Map map;
	
	final float FRAME_DURATION = .05f;
	float elapsed_time = 0f;
	HashMap<String, Animation> animations;
    Sprite currentFrame;

	public Player(Map map, float x, float y) {
		currentFrame = new Sprite(new Texture(Gdx.files.internal("assets\\player1.png")));
		buildAnimations();
		this.map = map;
		pos.x = x;
		pos.y = y;
		bounds.width = 0.685f;
		bounds.height = 0.685f;
		bounds.x = pos.x + OFFSET_BOUNDS_X;
		bounds.y = pos.y + OFFSET_BOUNDS_Y;
	}
	
	public Sprite getCurrentFrame() {
		return currentFrame;
	}
	
	public void buildAnimations() {
		animations = new HashMap<String, Animation>();
		animations.put("pickaxe_swing", new Animation(FRAME_DURATION, new TextureAtlas(Gdx.files.internal("assets\\swing.atlas")).findRegions("swinging"), PlayMode.LOOP));
	}

	public void update (float deltaTime) {
		elapsed_time += Gdx.graphics.getDeltaTime();
		float r = rot;
		if(r<0) r = 360f+r;
		direction = (int) Math.floor((r+45)/90);
		if(direction == 4) direction = 0;
		
		processKeys();
		accel.scl(deltaTime);
		vel.add(accel.x, accel.y);
		
		if (accel.x == 0) vel.x *= DAMP;
		if (accel.y == 0) vel.y *= DAMP;
		
		if (vel.x > MAX_VEL) vel.x = MAX_VEL;
		if (vel.x < -MAX_VEL) vel.x = -MAX_VEL;
		
		if (vel.y > MAX_VEL) vel.y = MAX_VEL;
		if (vel.y < -MAX_VEL) vel.y = -MAX_VEL;
		vel.scl(deltaTime);
		tryMove();
		vel.scl(1.0f / deltaTime);
		
	}

	private void processKeys () {
		//Movement
		if (Gdx.input.isKeyPressed(KEY_UP)) {
			accel.y = ACCELERATION;
		} else if (Gdx.input.isKeyPressed(KEY_DOWN)) {
			accel.y = -ACCELERATION;
		}
		
		if (Gdx.input.isKeyPressed(KEY_LEFT)) {
			accel.x = -ACCELERATION;
		} else if (Gdx.input.isKeyPressed(KEY_RIGHT)) {
			accel.x = ACCELERATION;
		}
		
		if(Gdx.input.isButtonPressed(KEY_PRIM)) {
			currentFrame = new Sprite(animations.get("pickaxe_swing").getKeyFrame(elapsed_time));
		} else {
			currentFrame = new Sprite(new Texture(Gdx.files.internal("assets\\player1.png")));
		}
		
		if(Gdx.input.isKeyPressed(KEY_AUX)) {
			lighting = !lighting;
		}
		

	}

	Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

	private void tryMove() {
		rot = (float) Math.toDegrees(Math.atan2(vel.x, -vel.y));
		bounds.x += vel.x;
		fetchCollidableRects();
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)) {
				if (vel.x < 0)
					bounds.x = rect.x + rect.width + 0.01f;
				else
					bounds.x = rect.x - bounds.width - 0.01f;
				vel.x = 0;
			}
		}

		bounds.y += vel.y;
		fetchCollidableRects();
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)) {
				if (vel.y < 0) {
					bounds.y = rect.y + rect.height + 0.01f;
				} else
					bounds.y = rect.y - bounds.height - 0.01f;
				vel.y = 0;
			}
		}

		pos.x = bounds.x - OFFSET_BOUNDS_X;
		pos.y = bounds.y - OFFSET_BOUNDS_Y;
	}

	private void fetchCollidableRects() {
		int p1x = (int) bounds.x;
		int p1y = (int) Math.floor(bounds.y);
		int p2x = (int) (bounds.x + bounds.width);
		int p2y = (int) Math.floor(bounds.y);
		int p3x = (int) (bounds.x + bounds.width);
		int p3y = (int) (bounds.y + bounds.height);
		int p4x = (int) bounds.x;
		int p4y = (int) (bounds.y + bounds.height);

		int[][] data_tile = map.data_tile;
		int tile1 = data_tile[p1x][map.data_tile[0].length - 1 - p1y];
		int tile2 = data_tile[p2x][map.data_tile[0].length - 1 - p2y];
		int tile3 = data_tile[p3x][map.data_tile[0].length - 1 - p3y];
		int tile4 = data_tile[p4x][map.data_tile[0].length - 1 - p4y];

		if (tile1 == Map.TILE_FILLED)
			r[0].set(p1x, p1y, 1, 1);
		else
			r[0].set(-1, -1, 0, 0);
		if (tile2 == Map.TILE_FILLED)
			r[1].set(p2x, p2y, 1, 1);
		else
			r[1].set(-1, -1, 0, 0);
		if (tile3 == Map.TILE_FILLED)
			r[2].set(p3x, p3y, 1, 1);
		else
			r[2].set(-1, -1, 0, 0);
		if (tile4 == Map.TILE_FILLED)
			r[3].set(p4x, p4y, 1, 1);
		else
			r[3].set(-1, -1, 0, 0);

	}

}
