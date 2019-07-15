package com.allthekingsmen.util;

public class Coordinates {
	public int x, y;
	
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean compare(Coordinates c) {
		return c.x == x && c.y == y;
	}

	@Override
	public String toString() {
		return this.x + ", " + this.y;
	}
	
	

}
