/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.connector;

import java.io.Serializable;
import ru.cinimex.data.Field;

public class FieldInBody extends BodyMessage implements Serializable {
	private static final long serialVersionUID = -8339379564574068914L;
	private Field field;
	
	public FieldInBody() {}
	
	public FieldInBody(Field field) {
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
