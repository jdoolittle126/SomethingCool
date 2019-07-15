package com.allthekingsmen.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.plaf.synth.SynthSeparatorUI;

import com.allthekingsmen.game.Light;
import com.allthekingsmen.game.Map;

public class LightCalculator {
	private static ArrayList<LightNode> open, closed, children;
	private static boolean[][] map;
	private static float[][] values;
	private static int SIZE = Light.LIGHT_RADIUS*2+1;
	private static float intensity = Light.DEFAULT_LIGHT_INTENSITY;

	public static void setup(boolean[][] m) {
		map = m;
		values = new float[map[0].length][map.length];
		open = new ArrayList<LightNode>(); 
		closed = new ArrayList<LightNode>();
		children  = new ArrayList<LightNode>();
	}
	
	public static void setup(boolean[][] m, float inten) {
		intensity = inten;
		map = m;
		values = new float[map[0].length][map.length];
		open = new ArrayList<LightNode>(); 
		closed = new ArrayList<LightNode>();
		children  = new ArrayList<LightNode>();
	}
	
	public static void calc() {
		open.clear();
		closed.clear();
		children.clear();
		open.add(new LightNode(new Coordinates(Light.LIGHT_RADIUS, Light.LIGHT_RADIUS), 0));
		open.get(0).intensity = intensity;
		while(!open.isEmpty()) {
			LightNode current_node = open.get(0);
			closed.add(current_node);
			open.remove(current_node);
			children.clear();
			if(current_node.pos.x + 1 < SIZE && map[current_node.pos.x + 1][current_node.pos.y]) 
				children.add(buildNode(new Coordinates(current_node.pos.x + 1, current_node.pos.y), current_node));
			if(current_node.pos.y + 1 < SIZE && map[current_node.pos.x][current_node.pos.y + 1]) 
				children.add(buildNode(new Coordinates(current_node.pos.x, current_node.pos.y + 1), current_node));
			if(current_node.pos.x - 1 >= 0 && map[current_node.pos.x - 1][current_node.pos.y])
				children.add(buildNode(new Coordinates(current_node.pos.x - 1, current_node.pos.y), current_node));
			if(current_node.pos.y - 1 >= 0 && map[current_node.pos.x][current_node.pos.y - 1])
				children.add(buildNode(new Coordinates(current_node.pos.x, current_node.pos.y - 1), current_node));
				
			for(LightNode child : children) {
				int index = inList(child, open);
				
				if(inList(child, closed) == -1) {
					child.setG(calcG(child));
					if(index > -1) {
						if(open.get(index).g > child.g) {
							open.remove(index);
							open.add(child);
						}
					} else {
						open.add(child);
					}
				}
			}
		}	
		
		for(LightNode n : closed) {
			values[n.pos.x][n.pos.y] = n.getlightValue();
		}
		
	}
	
	public static float getLightValue(int x, int y) {
		return values[x][y];
	}
	
	public static float[][] getAllValues(){
		return values;
	}
	private static int inList(LightNode n, ArrayList<LightNode> l) {
		for(LightNode a : l) {
			if(n.pos.compare(a.pos)) return l.indexOf(a);
		}
		return -1;
	}
	
	private static LightNode buildNode(Coordinates c, LightNode p) {
		LightNode n = new LightNode(c, p);
		n.setIntensity(intensity);
		return n;
	}
	
	private static float calcG(LightNode n) {
		if(n.p == null) 
			return 0;
		else 
			return n.p.g + 1;
	}
	
}
