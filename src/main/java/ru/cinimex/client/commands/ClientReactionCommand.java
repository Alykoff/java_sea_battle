/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.client.commands;

import java.io.IOException;
import java.util.Date;
import ru.cinimex.client.ClientMessages;
import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.data.TypeField;
import ru.cinimex.server.EndGameException;

public abstract class ClientReactionCommand {
	protected final int MAX_TIME_STROKE = 3 * 60 * 1000;
	protected final ClientMessages msgFactory = new ClientMessages();
	
	abstract public void execute(BodyMessage body, View view, 
							Connector connector, ClientData data);
	
	protected Point waitAndReactionToStroke(View view, ClientData data, 
													Connector connector) {
		long beginTime = new Date().getTime();
		data.setState(ClientState.STROKE);
		view.cleanLastStroke();
		while (view.getLastStroke() == null) {
			if ((beginTime + MAX_TIME_STROKE) < new Date().getTime()) {
				view.println("Your time is over. You lose.");
				throw new EndGameException();
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		data.setState(ClientState.WAIT); 
		try {
			return view.getLastStroke();
		} catch (RuntimeException e) {
			view.println(e.getMessage());
			throw new EndGameException();
		}
	}
	
	protected void sendStroke(View view, ClientData data, Connector connector) {
		Point stroke = waitAndReactionToStroke(view, data, connector);
		send(msgFactory.getStroke(stroke), connector);
	}
	
	public void send(Message msg, Connector connector) throws RuntimeException {
		try {
			connector.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem in connect.");
		}
	}
	
	protected void paintPaddedShip(View view, Point point, TypeField typeField) {
		if (point == null || typeField == null) {
			throw new NullPointerException();
		}
		if (view.getCell(point, typeField).equals(TypeCell.WATER)) {
			view.setCell(point, TypeCell.MISS, typeField);
		}
		if (!view.getCell(point, typeField).equals(TypeCell.STRIKE)) {
			return;
		}
		view.setCell(point, TypeCell.BIG_BANG, typeField);
		int x = point.getX();
		int y = point.getY();
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i < 0 || 
						i >= Field.WIDTH || 
						j < 0 || 
						j >= Field.HEIGHT) {
					continue;
				}
				paintPaddedShip(view, new Point(i, j), typeField);
			}
		}
		
	}
	
}
