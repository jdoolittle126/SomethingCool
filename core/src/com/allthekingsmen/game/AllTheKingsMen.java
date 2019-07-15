package com.allthekingsmen.game;

import com.badlogic.gdx.Game;

public class AllTheKingsMen extends Game {
	
	@Override
	public void create() {
		setScreen(new TitleScreen(this));
	}
	
	/*
	 * TODO
	 * Day/Night
	 * Rework base lighting to update dynamically
	 * Entity Lights, both point and directional
	 * 
	 */
	
	
}
