/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client;

import ru.cinimex.data.FieldInMessage;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;

public class ClientMessages {
	
	public Message getTKOLose() {
		return new Message(Header.TKO_LOSE, null);
	}
	
	public Message getInit(FieldInMessage body) {
		return new Message(Header.INIT, body);
	}
	
	public Message getStroke(Point stroke) {
		return new Message(Header.STROKE, stroke);
	}
	
}
