package com.allthekingsmen.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class AStar {
	private ArrayList<Node> open, closed, children;
	private Coordinates start, end;
	private int mod_x, mod_y;
	
	public AStar(Coordinates start, Coordinates end) {
		this.start = start;
		this.end = end;
		open = new ArrayList<Node>(); 
		closed = new ArrayList<Node>();
		children  = new ArrayList<Node>();
		
		
	}
	
	private void findPath(boolean[][] map) {
		open.clear();
		closed.clear();
		open.add(new Node(start, 0, 0));
		
		while(!open.isEmpty()) {
			Node current_node = open.get(0);
			for(Node n : open) {
				if(n.totalCost() < current_node.totalCost()) current_node = n;
			}
			closed.add(current_node);
			open.remove(current_node);
			
			if(current_node.pos.compare(end)) break;
			
			children.clear();
			
			if(current_node.pos.x + 1 < map[0].length && map[current_node.pos.x + 1][current_node.pos.y]) 
				children.add(buildNode(new Coordinates(current_node.pos.x + 1, current_node.pos.y), current_node));
			if(current_node.pos.y + 1 < map.length && map[current_node.pos.x][current_node.pos.y + 1]) 
				children.add(buildNode(new Coordinates(current_node.pos.x, current_node.pos.y + 1), current_node));
			if(current_node.pos.x - 1 >= 0 && map[current_node.pos.x - 1][current_node.pos.y])
				children.add(buildNode(new Coordinates(current_node.pos.x - 1, current_node.pos.y), current_node));
			if(current_node.pos.y - 1 >= 0 && map[current_node.pos.x][current_node.pos.y - 1])
				children.add(buildNode(new Coordinates(current_node.pos.x, current_node.pos.y - 1), current_node));
				
			for(Node child : children) {
				if(inList(child, closed) == -1) {
					child.setG(calcG(child));
					child.setH(calcH(child));
					int index = inList(child, open);
					if(index > -1 && open.get(index).g < child.g) continue;
					open.add(child);
				}
				
			}
		}	
		
		if(open.isEmpty()) System.out.println("no path");
		else {
			Collections.reverse(closed);
			for(Node n : closed) {
				System.out.println(n.pos);
			}
			
		}
		
	}
	
	private int inList(Node n, ArrayList<Node> l) {
		for(Node a : l) {
			if(n.pos.compare(a.pos)) return l.indexOf(a);
		}
		return -1;
	}
	
	private Node buildNode(Coordinates c, Node p) {
		Node n = new Node(c, p);
		return n;
	}
	
	private float calcG(Node n) {
		if(n.p == null) 
			return 0;
		else 
			return n.p.g + 1;
	}
	
	private float calcH(Node n) {
		return Math.abs(n.pos.x - end.x) + Math.abs(n.pos.y - end.y);
		
	}
	
}
