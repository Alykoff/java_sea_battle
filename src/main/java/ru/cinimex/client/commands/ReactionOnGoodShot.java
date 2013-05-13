/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.client.commands;

import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.Header;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.data.TypeField;
import ru.cinimex.server.EndGameException;

public class ReactionOnGoodShot extends ClientReactionCommand {

	@Override
	public boolean execute(BodyMessage body, View view, Connector connector,
			ClientData data) {
		
		final Point stroke = view.getLastStroke();
		if (stroke == null) {
			view.println("Upps! We have problem! " +
					"Sorry. End game(");
			throw new EndGameException();
//			interrupt = true;
//			endGame();
		}
		view.setCell(stroke, TypeCell.STRIKE, TypeField.OPPONENT);
		if (header.equals(Header.BIG_BANG)) {
			paintPaddedShip(stroke, TypeField.OPPONENT);
			getView().println("Yeeh! You blew up this ship!");
		} else {
			getView().println("Good shot!");
		}
		getView().cleanLastStroke();
		waitAndReactionToStroke();
		return false;
	}

}
