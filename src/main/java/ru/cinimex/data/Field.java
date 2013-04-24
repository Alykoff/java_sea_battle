/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.data;

import java.io.Serializable;
import java.util.Arrays;

public class Field implements Cloneable, Serializable {
	private static final long serialVersionUID = 2932346134737082459L;
	public static final int HEIGHT = 10;
	public static final int WIDTH = 10;
	private int[][] field = new int[HEIGHT][WIDTH];
	
	public Field() {
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j] = TypeCell.WATER.ordinal();
			}
		}
	}
	
	public Field(int[][] field) {
		for (int[] cells : field) {
			for (int cell : cells) {
				boolean flagMatch = false;
				for (TypeCell type : TypeCell.values()) {
					if (cell == type.ordinal()) {
						flagMatch = true;
					}
				}
				if (!flagMatch) {
					throw new IllegalArgumentException("Bad data");
				}
			}
		}
		this.field = field;
	}
		
	public void setField(int[][] field) {
		this.field = field;
	}
	
	public int[][] getField() {
		return field;
	}
	
	public int getCell(int x, int y) {
		if (x < 0 || 
				y < 0 || 
				x > (WIDTH - 1) || 
				y > (HEIGHT - 1)) {
			throw new IllegalArgumentException();
		}
		return field[x][y];
	}
		
	public void makeStroke(int x, int y) {
		int cell = field[x][y];
		if (cell == TypeCell.SHIP.ordinal()) {
			field[x][y] = TypeCell.STRAKE.ordinal();
		} else if (cell == TypeCell.WATER.ordinal()) {
			field[x][y] = TypeCell.MISS.ordinal();
		}
	}
	
	public void setCell(int x, int y, TypeCell type) {
		field[x][y] = type.ordinal();
	}
	
	public void setCell(int x, int y, int type) {
		field[x][y] = type;
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < field.length; i++) {
			result += Arrays.toString(field[i]) + "\n";
		}
		return result;
	}	
	
	@Override
	public Field clone() {
		int[][] newField = new int[WIDTH][HEIGHT];
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				newField[i][j] = field[i][j];
			}
		}
		return new Field(newField);
	}
		
}
