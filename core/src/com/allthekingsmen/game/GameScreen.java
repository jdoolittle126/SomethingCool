package com.allthekingsmen.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends BaseScreen {
	Map map;
	MapRenderer mapRenderer;
	OverlayRenderer overlayRenderer;

	public GameScreen(Game game) {
		super(game);
	}

	@Override
	public void show () {
		map = new Map();
		mapRenderer = new MapRenderer(map);
		overlayRenderer = new OverlayRenderer();
	}

	@Override
	public void render (float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		map.update(delta);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mapRenderer.render(delta);
		overlayRenderer.render(delta);

		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(new TitleScreen(game));
		}
	}

	
	@Override
	public void resize(int width, int height) {
		mapRenderer.onResize(width, height);
		overlayRenderer.onResize(width, height);
		super.resize(width, height);
	}

	@Override
	public void hide () {
		Gdx.app.debug("AllTheKingsMen", "dispose game screen");
		mapRenderer.dispose();
	}
}
