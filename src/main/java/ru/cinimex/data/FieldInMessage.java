/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.data;

import java.io.Serializable;


public class FieldInMessage extends BodyMessage implements Serializable {
	private static final long serialVersionUID = -8339379564574068914L;
	private Field field;
	
	public FieldInMessage() {}
	
	public FieldInMessage(Field field) {
		if (field == null) {
			throw new IllegalArgumentException();
		}
		this.field = field;
	}
	
	public Field getField() {
		return field;
	}
	
	@Override
	public String toString() {
		return field.toString();
	}
}
