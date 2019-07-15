package com.allthekingsmen.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.allthekingsmen.game.AllTheKingsMen;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 500*2;
		config.height = 375*2;
		new LwjglApplication(new AllTheKingsMen(), config);
	}
}
