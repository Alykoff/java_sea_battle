/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.client.commands;

import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.data.TypeField;
import ru.cinimex.server.FieldLogic;

public class ReactionOnNotStroke extends ClientReactionCommand {

	@Override
	public boolean execute(BodyMessage body, View view, Connector connector,
			ClientData data) {
		
		view.println("Please wait for the opponent's turn.");
		data.setState(ClientState.WAIT);
		if (body != null && (body instanceof Point)) {
			Point point = (Point) body;
			reactionOnHitInOurShip(view, point);
		} else if (view.getLastStroke() != null) {
			reactionOnMiss(view);
		}
		return false;
	}
	
	protected void reactionOnHitInOurShip(View view, Point point) {
		view.println("Hit on our ship!");
		Field field = view.getField(TypeField.OUR);
		boolean isBigBang = FieldLogic.isBigBang(field, point);
		view.setCell(point, TypeCell.STRIKE, TypeField.OUR);
		if (isBigBang) {
			paintPaddedShip(view, point, TypeField.OUR);
		}
	}
	
	protected void reactionOnMiss(View view) {
		Point stroke = view.getLastStroke();
		TypeCell typeCellUnderStrokePoint = view.getCell(stroke, TypeField.OPPONENT);
		if (typeCellUnderStrokePoint.equals(TypeCell.WATER)) {
			view.setCell(stroke, TypeCell.MISS, TypeField.OPPONENT);
		}
		view.cleanLastStroke();
	}
	
}
