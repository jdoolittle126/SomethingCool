package com.allthekingsmen.util;

public class Node {
	Node p;
	float g, h;
	Coordinates pos;
	
	public Node(Coordinates pos) {
		this.pos = pos;
	}
	public Node(Coordinates pos, Node p) {
		this.pos = pos;
		this.p = p;
	}
	public Node(Coordinates pos, float g, float h) {
		this.pos = pos;
		this.g = g;
		this.h = h;
	}
	public Node(Coordinates pos, Node p, float g, float h) {
		this.pos = pos;
		this.p = p;
		this.g = g;
		this.h = h;
	}
	public void setP(Node p) {
		this.p = p;
	}
	public void setG(float g) {
		this.g = g;
	}
	public void setH(float h) {
		this.h = h;
	}
	public float totalCost() {
		return g+h;
	}
	
}