/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.client.commands;

import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.server.EndGameException;

public class ReactionOnLose extends ClientReactionCommand {

	@Override
	public boolean execute(BodyMessage body, View view, Connector connector,
			ClientData data) {
		view.println("Oh! We lose!");
		throw new EndGameException();
	}

}
