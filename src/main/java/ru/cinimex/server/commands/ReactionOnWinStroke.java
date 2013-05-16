/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import java.io.IOException;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import ru.cinimex.server.EndGameException;
import ru.cinimex.server.ServerMessages;

public class ReactionOnWinStroke {//extends ReactionOnStroke {

//	@Override
//	public boolean execute(ClientData activeClient, Connector activeConnector,
//			ClientData notActiveClient, Connector notActiveConnector,
//			Message msg) throws EndGameException, IOException {
//		
//		if (msg == null || 
//				msg.getBody() == null || 
//				!(msg.getBody() instanceof Point)) {
//			throw new RuntimeException("nullpointer or bad instance");
//		}
//		Point point = (Point) msg.getBody();
//		notActiveConnector.send(ServerMessages.getLoose(point));
//		activeConnector.send(ServerMessages.getWin());
//		throw new EndGameException();
//	}
}
