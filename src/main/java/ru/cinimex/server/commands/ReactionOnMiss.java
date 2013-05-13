/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import java.io.IOException;
import java.net.SocketException;

import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.Point;
import ru.cinimex.server.EndGameException;
import ru.cinimex.server.ServerMessages;

public class ReactionOnMiss extends ReactionOnStroke {

	@Override
	public boolean execute(ClientData activeClient, Connector activeConnector,
			ClientData notActiveClient, Connector notActiveConnector,
			BodyMessage body) throws EndGameException, IOException {

		if (body == null || !(body instanceof Point)) {
			try {
				notActiveConnector.send(ServerMessages.getTKOWin());
			} catch (SocketException e) {
				e.printStackTrace();
			}
			activeConnector.send(ServerMessages.getTKOLoose());	
			throw new EndGameException();
		}
		Point point = (Point) body;
		try {
			notActiveConnector.send(ServerMessages.getStrokeMsg(point));
		} catch (SocketException e) {
			activeConnector.send(ServerMessages.getTKOWin());
			throw new EndGameException();
		}
		activeConnector.send(ServerMessages.getMiss());
		return true;
	}

}
