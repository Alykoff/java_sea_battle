/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import java.io.IOException;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.server.EndGameException;

public interface ServerReactionCommand {
	public boolean execute(ClientData activeClient, 
			Connector activeConnector, 
			ClientData notActiveClient, 
			Connector notActiveConnector,
			BodyMessage body) throws EndGameException, IOException;
}
