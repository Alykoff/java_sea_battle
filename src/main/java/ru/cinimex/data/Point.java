/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.data;

import java.io.Serializable;


public class Point extends BodyMessage implements Serializable {
	private static final long serialVersionUID = 1615049120227370753L;
	private int x;
	private int y;
	
	public Point() {}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return "x = " + x + "; y = " + y;
	}
}
