/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.server.commands;

import java.io.IOException;

import ru.cinimex.connector.Connector;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.Field;
import ru.cinimex.data.Message;
import ru.cinimex.server.EndGameException;
import ru.cinimex.server.ServerMessages;

public class ReactionOnInvalidMsg extends ServerReactionCommand {

	public ReactionOnInvalidMsg(Field notActiveField, Message msg) {
		super(notActiveField, msg);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean execute(ClientData activeClient, Connector activeConnector,
			ClientData notActiveClient, Connector notActiveConnector,
			Message msg) throws EndGameException, IOException {
		
		notActiveConnector.send(ServerMessages.getTKOWin());
		activeConnector.send(ServerMessages.getTKOLoose());
		throw new EndGameException();
	}

}
