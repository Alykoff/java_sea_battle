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

public class ReactionOnStroke extends ClientReactionCommand {

	@Override
	public boolean execute(BodyMessage body, View view, Connector connector,
			ClientData data) {
		if (body != null || body instanceof Point) {
			Point point = (Point) body;
			reactionOnSuccessfulStroke(view, point);
		} 
		view.println("You stroke.");
		sendStroke(view, data, connector);
		return true;
	}

	protected void reactionOnSuccessfulStroke(View view, Point point) {
		if (view.getCell(point, TypeField.OUR).equals(TypeCell.WATER)) {
			view.setCell(point, TypeCell.MISS, TypeField.OUR);
		}		
	}
	
}
