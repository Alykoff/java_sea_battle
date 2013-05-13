/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import ru.cinimex.data.Field;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;

public class ReactionCommandFactory {
	
	public ServerReactionCommand getReactionCommand(Field notActiveField, Message msg) {
		if (notActiveField == null || msg == null) {
			throw new NullPointerException("notActiveClient or msg is nullpointer");
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
}
