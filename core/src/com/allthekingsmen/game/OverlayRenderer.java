package com.allthekingsmen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class OverlayRenderer {
	static int VIEW_W = 20;
	static int VIEW_H = 15;
	
	SpriteBatch batch = new SpriteBatch(5460);
	TextureRegion overlay = new TextureRegion(new Texture(Gdx.files.internal("assets\\overlay.png")), 0, 0, 1000, 750);
	
	public OverlayRenderer () {

	}
	
	public void render (float deltaTime) {
		batch.begin();
		batch.draw(overlay, 0, 0);
		batch.end();
	}

	public void onResize(int w, int h) {
	}
	
	
	public void dispose () {

	}
}

