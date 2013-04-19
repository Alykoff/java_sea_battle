/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.connector;

import java.io.Serializable;

import ru.cinimex.data.Field;

public class FieldInBody extends BodyMessage implements Serializable {
	private static final long serialVersionUID = -8339379564574068914L;
	public static int length = 10;
	private int[][] field = new int[length][length];
	
	public FieldInBody() {}
	
	public FieldInBody(int[][] field) {
		if (field.length != length) {
			throw new RuntimeException();
		}
		for (int i = 0; i < field.length; i++) {
			if (field[i].length != length) {
				throw new RuntimeException();
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
	
	@Override
	public String toString() {
		return new Field(field).toString();
	}
}
