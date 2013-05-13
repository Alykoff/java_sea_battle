/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import static ru.cinimex.server.FieldLogic.validateStroke;
import ru.cinimex.data.Field;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;

public class ReactionCommandFactory {
	
	public ServerReactionCommand getReactionCommand(Field notActiveField, Message msg) {
		
		if (notActiveField == null) {
			throw new NullPointerException("notActiveClient or msg is nullpointer");
		}
		if (!isValidGameMsg(msg)) {
			return new ReactionOnInvalidMsg();
		}
		Header header = msg.getHeader();
		if (header.equals(Header.TKO_LOOSE) || header.equals(Header.LOOSE)) {
			return new ReactionOnLose();
		} else if (header.equals(Header.STROKE)) {
			Point point = (Point) msg.getBody();
			ReactionOnStrokeFactory strokeFactory = new ReactionOnStrokeFactory();
			return strokeFactory.getStrokeCommand(notActiveField, point);
		}
		throw new RuntimeException("This point can't be achieved.");
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
