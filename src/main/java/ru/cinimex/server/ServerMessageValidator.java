/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.server;

import static ru.cinimex.server.FieldLogic.isValidInitField;
import static ru.cinimex.server.FieldLogic.validateStroke;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldInMessage;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;

public class ServerMessageValidator {
	public boolean isValidInit(Message clientMsg) {
		if (clientMsg == null) {
			throw new NullPointerException();
		}
		if (!clientMsg.getHeader().equals(Header.INIT) ||
				(clientMsg.getBody() == null) ||
				!(clientMsg.getBody() instanceof FieldInMessage)) {
			return false;
		}
		
		FieldInMessage fieldInBody = (FieldInMessage)clientMsg.getBody();
		Field field = fieldInBody.getField();
		
		if (field == null || !isValidInitField(field)) {
			return false;
		}
		return true;
	}
	

	public boolean isValidGameMsg(Message msg) {
		if (msg == null) {
			return false;
		}
		Header header = msg.getHeader();
		if (header == null) {
			return false;
		}
		if (!header.equals(Header.LOOSE) && 
				!header.equals(Header.TKO_LOOSE) &&
				!header.equals(Header.STROKE)) {
			return false;
		}
		if (header.equals(Header.STROKE) && !isValidStroke(msg)) {
			return false;
		}
		return true;
	}
	
	private boolean isValidStroke(Message msg) {
		if (!msg.getHeader().equals(Header.STROKE) ||
				(msg.getBody() == null) ||
				!(msg.getBody() instanceof Point)) {
			return false;
		}
		
		Point point = (Point)msg.getBody();
		int x = point.getX();
		int y = point.getY();
		
		if (!validateStroke(x, y)) {
			return false;
		}
		return true;
	}
}
