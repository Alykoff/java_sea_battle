/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import java.io.IOException;
import java.net.SocketException;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.server.EndGameException;
import ru.cinimex.server.ServerMessages;

public class ReactionOnBigBang extends ReactionOnStroke {
	@Override
	public boolean execute(ClientData activeClient, Connector activeConnector,
			ClientData notActiveClient, Connector notActiveConnector,
			Message msg) throws EndGameException, IOException {
		if (msg == null || 
				msg.getBody() == null || 
				!(msg.getBody() instanceof Point)) {
			try {
				notActiveConnector.send(ServerMessages.getTKOWin());
			} catch (SocketException e) {
				e.printStackTrace();
			}
			activeConnector.send(ServerMessages.getTKOLoose());	
			throw new EndGameException();
		}
		
		Point point = (Point) msg.getBody();		
		int x = point.getX();
		int y = point.getY();
		notActiveClient.getField().setCell(x, y, TypeCell.STRIKE);
		try {
			notActiveConnector.send(ServerMessages.getNotStroke(point));
		} catch (SocketException e) {
			activeConnector.send(ServerMessages.getTKOWin());
			throw new EndGameException();
		}
		activeConnector.send(ServerMessages.getBigBang());
		
		return false;
	}

}
