/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.client.commands;

import java.util.HashMap;

import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.Header;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.data.TypeField;
import ru.cinimex.server.EndGameException;
import ru.cinimex.server.FieldLogic;

public class ReactionClientCommandFactory {
	HashMap<Header, ClientReactionCommand> commands;
	public ReactionClientCommandFactory() {
		commands = new HashMap<Header, ClientReactionCommand>();
		commands.put(Header.BAD_INIT, new ClientReactionCommand() {
			@Override
			public void execute(BodyMessage body, View view, Connector connector,
					ClientData data) {
				view.println("Bad init.");
				throw new EndGameException();
			}
		});
		commands.put(Header.TKO_WIN, new ClientReactionCommand() {
			@Override
			public void execute(BodyMessage body, View view, Connector connector,
					ClientData data) {
				view.println(
						"Congratulations! You win! You " +
						"have a terrific excerpt. Enemy " +
						"fleet defeated.");
				throw new EndGameException();
			}
		});
		commands.put(Header.WIN, new ClientReactionCommand() {
			@Override
			public void execute(BodyMessage body, View view, Connector connector,
					ClientData data) {
				view.println("Congratulations! You win! " +
					"Enemy fleet fled.");
				throw new EndGameException();
			}
		});
		commands.put(Header.TKO_LOSE, new ClientReactionCommand() {
			@Override
			public void execute(BodyMessage body, View view, Connector connector,
					ClientData data) {
				view.println("Oh! TKO lose. We lose!");
				throw new EndGameException();
			}
		});
		commands.put(Header.LOSE, new ClientReactionCommand() {
			@Override
			public void execute(BodyMessage body, View view, Connector connector,
					ClientData data) {
				view.println("Oh! We lose!");
				throw new EndGameException();
			}
		});
		commands.put(Header.BIG_BANG, new ClientReactionCommand() {
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
				paintPaddedShip(view, stroke, TypeField.OPPONENT);
				view.println("Yeeh! You blew up this ship!");
				view.cleanLastStroke();
				sendStroke(view, data, connector);
			}
		});
		commands.put(Header.STRIKE, new ClientReactionCommand() {
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
		});
		commands.put(Header.STROKE, new ClientReactionCommand() {

			@Override
			public void execute(BodyMessage body, View view, Connector connector,
					ClientData data) {
				if (body != null || body instanceof Point) {
					Point point = (Point) body;
					reactionOnSuccessfulStroke(view, point);
				} 
				view.println("You stroke.");
				sendStroke(view, data, connector);
			}
		});
		commands.put(Header.NOT_STROKE, new ClientReactionCommand() {
			@Override
			public void execute(BodyMessage body, View view, Connector connector,
					ClientData data) {
				
				view.println("Please wait for the opponent's turn.");
				data.setState(ClientState.WAIT);
				if (body != null && (body instanceof Point)) {
					Point point = (Point) body;
					reactionOnHitInOurShip(view, point);
				} else if (view.getLastStroke() != null) {
					reactionOnMiss(view);
				}
			}
		});
		commands.put(Header.INIT, new ClientReactionCommand() {
			@Override
			public void execute(BodyMessage body, View view, Connector connector,
					ClientData data) {}
		});
	}
	
	public ClientReactionCommand getCommand(Header header) {
		return commands.get(header);
	}
}
