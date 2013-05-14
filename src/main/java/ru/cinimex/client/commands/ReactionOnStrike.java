/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.client.commands;

import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.data.TypeField;
import ru.cinimex.server.EndGameException;

public class ReactionOnStrike extends ClientReactionCommand {

	@Override
	public void execute(BodyMessage body, View view, Connector connector,
			ClientData data) {
		
		final Point stroke = view.getLastStroke();
		if (stroke == null) {
			view.println("Upps! We have problem! " +
					"Sorry. End game(");
			throw new EndGameException();
		}
		view.setCell(stroke, TypeCell.STRIKE, TypeField.OPPONENT);
		view.println("Good shot!");
		view.cleanLastStroke();
		sendStroke(view, data, connector);
	}

}
