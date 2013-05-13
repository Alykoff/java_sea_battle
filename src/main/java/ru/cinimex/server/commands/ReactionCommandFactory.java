/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import ru.cinimex.data.Field;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import ru.cinimex.server.ServerMessageValidator;

public class ReactionCommandFactory {
	private ServerMessageValidator msgValidator = new ServerMessageValidator();
	
	public ServerReactionCommand getReactionCommand(Field notActiveField, Message msg) {
		if (notActiveField == null) {
			throw new NullPointerException("notActiveClient or msg is nullpointer");
		}
		if (!msgValidator.isValidGameMsg(msg)) {
			return new ReactionOnInvalidMsg();
		}
		Header header = msg.getHeader();
		if (header.equals(Header.TKO_LOSE) || header.equals(Header.LOSE)) {
			return new ReactionOnLose();
		} else if (header.equals(Header.STROKE)) {
			Point point = (Point) msg.getBody();
			ReactionOnStrokeFactory strokeFactory = new ReactionOnStrokeFactory();
			return strokeFactory.getStrokeCommand(notActiveField, point);
		}
		throw new RuntimeException("This point can't be achieved.");
	}
}
