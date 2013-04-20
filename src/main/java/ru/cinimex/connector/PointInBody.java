/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.connector;

import java.io.Serializable;

public class PointInBody extends BodyMessage implements Serializable {
	private static final long serialVersionUID = 1615049120227370753L;
	private int x;
	private int y;
	
	public PointInBody() {}
	
	public PointInBody(int x, int y) {
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
