/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import ru.cinimex.client.view.TypeField;
import ru.cinimex.client.view.View;
import ru.cinimex.connector.BodyMessage;
import ru.cinimex.connector.Connector;
import ru.cinimex.connector.FieldInBody;
import ru.cinimex.connector.Header;
import ru.cinimex.connector.Message;
import ru.cinimex.connector.PointInBody;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldLogic;
import ru.cinimex.data.TypeCell;

public class ClientController {
	private Connector connector = null;
	private View view;
	protected ClientData data;
	private boolean interrupt = false;
	
	public ClientController() {
		view = new View(this);
		data = new ClientData(ClientState.NOT_CONNECT);
	}
	
	public static void main(String[] args) {
		new ClientController();
	}
	
	public void reactionOnStart(final String url, final String port) {
		if (url == null || port == null) {
			throw new NullPointerException("I found null pointer.");
		}
		interrupt = false;
		try {
			new Thread(new Runnable() {
				public void run() {
					connect(url, port);
					Message msg = getInitMsg();
					send(msg);
					view.switchToStartGameMode();
					data.setState(ClientState.WAIT);
					processingGame();
				}
			}).start();
		} catch (IllegalArgumentException e) {
			view.log.println(e.getMessage());
			e.printStackTrace();
		} catch (RuntimeException e) {
			view.log.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Message getInitMsg() {
		Field field = view.getField(TypeField.OUR);
		FieldInBody body = new FieldInBody(field);
		Header header = Header.INIT;
		return new Message(header, body);
	}
	
	private void processingGame() {
		while (!interrupt) {
			try {
				Message msg = recieve();
				Header header = msg.getHeader();
				BodyMessage body = msg.getBody();
				if (header.equals(Header.BAD_INIT) || header.equals(Header.BAD_STROKE)) {
					reactionOnBadInit();
				} else if (header.equals(Header.TKO_WIN) || header.equals(Header.WIN)) {
					reactionOnWin(header);
				} else if (header.equals(Header.TKO_LOOSE) || header.equals(Header.LOOSE)) {
					reactionOnLoose(header);
				} else if (header.equals(Header.BIG_BANG) || header.equals(Header.STRIKE)) {
					reactionOnGoodShot(header, body);
				} else if (header.equals(Header.STROKE)) {
					reactionOnStroke(header, body);
				} else if (header.equals(Header.NOT_STROKE)) {
					reactionOnStrokeTabu(body);						
				} 
			} catch (RuntimeException e) {
				e.printStackTrace();
				view.log.println(e.getMessage());
				interrupt = true;
				endGame();
			}
		}
	}
	

	protected void reactionOnBadInit() {
		view.log.println("Bad init.");
		interrupt = true;
		endGame();
	}
	
	protected void reactionOnBadStroke() {
		view.log.println("bad stroke.");
		waitAndReactionToStroke();
	}
	
	protected void reactionOnStroke(Header header, BodyMessage body) {
		if (body != null || body instanceof PointInBody) {
			PointInBody point = (PointInBody) body;
			if (view.getCell(point, TypeField.OUR) == TypeCell.WATER.ordinal()) {
				view.setCell(point, TypeCell.MISS, TypeField.OUR);
			}
		} 
		view.log.println("You stroke.");
		waitAndReactionToStroke();
	}
	
	protected void reactionOnGoodShot(Header header, BodyMessage body) {
		if (view.getLastStroke() == null) {
			view.log.println("Upps! We have problem! " +
					"Sorry. End game(");
			endGame();
		}
		view.setCell(view.getLastStroke(), TypeCell.STRAKE, TypeField.OPPONENT);
		if (header.equals(Header.BIG_BANG)) {
			paintPaddedShip(view.getLastStroke(), TypeField.OPPONENT);
			view.log.println("Yeeh! You blew up this ship!");
		} else {
			view.log.println("Good shot!");	
		}
		view.cleanLastStroke();
		waitAndReactionToStroke();
	}
	
	private void waitAndReactionToStroke() {
		data.setState(ClientState.STROKE);
		long beginTime = new Date().getTime();
		long maxTimeStroke = 3000000L;
		view.log.println(view.getLastStroke() + "\n=========\n");
		view.cleanLastStroke();
		while (view.getLastStroke() == null) {
			if ((beginTime + maxTimeStroke) < new Date().getTime()) {
				interrupt = true;
				view.log.println("Your time is over. You lose.");
				return;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		data.setState(ClientState.WAIT); 
		try {
			send(new Message(Header.STROKE, view.getLastStroke()));
		} catch (RuntimeException e) {
			view.log.println(e.getMessage());
			interrupt = true;
		}
	}
	
	protected void endGame() {
		data.setState(ClientState.NOT_CONNECT);
		view.switchToEndGame();
	}
	
	private void paintPaddedShip(PointInBody point, TypeField typeField) {
		if (point == null || typeField == null) {
			throw new NullPointerException();
		}
		
		if (view.getCell(point, typeField) == TypeCell.WATER.ordinal()) {
			view.setCell(point, TypeCell.MISS, typeField);
		}
		if (view.getCell(point, typeField) != TypeCell.STRAKE.ordinal()) {
			return;
		}
		view.setCell(point, TypeCell.BIG_BANG, typeField);
		Field field = new Field();
		int x = point.getX();
		int y = point.getY();
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i < 0 || 
						i >= field.WIDTH || 
						j < 0 || 
						j >= field.HEIGHT) {
					continue;
				}
				paintPaddedShip(new PointInBody(i, j), typeField);
			}
		}
		
	}
		
	protected void reactionOnOfferToStroke(BodyMessage body) {
		view.log.println("Your turn began.");
		if (body != null && (body instanceof PointInBody)) {
			view.log.println("Your opponent missed!");
			PointInBody point = (PointInBody) body;
			int cellXY = view.getCell(point, TypeField.OUR);
			if (cellXY == TypeCell.WATER.ordinal() || 
					cellXY == TypeCell.MISS.ordinal()) {
				view.setCell(point, TypeCell.MISS, TypeField.OUR);
			}
		}							
		waitAndReactionToStroke();
	}
	
	protected void reactionOnStrokeTabu(BodyMessage body) {
		view.log.println("Please wait for the opponent's turn.");
		data.setState(ClientState.WAIT);
		if (body != null && (body instanceof PointInBody)) {
			view.log.println("Hit on our ship!");
			PointInBody point = (PointInBody) body;
			boolean isBigBang = FieldLogic.detectBigBang(view.getField(TypeField.OUR), point);
			view.setCell(point, TypeCell.STRAKE, TypeField.OUR);
			if (isBigBang) {
				paintPaddedShip(point, TypeField.OUR);
			}
		} else if (view.getLastStroke() != null) {
			if (view.getCell(view.getLastStroke(), TypeField.OPPONENT) == TypeCell.WATER.ordinal()) {
				view.setCell(view.getLastStroke(), TypeCell.MISS, TypeField.OPPONENT);
			}
			view.cleanLastStroke();
		}
	}
	
	protected void reactionOnWin(Header header) {
		if (header.equals(Header.TKO_WIN)) {
			view.log.println("Congratulations! You win! " +
					"Enemy fleet fled.");
		} else {
			view.log.println(
					"Congratulations! You win! You " +
					"have a terrific excerpt. Enemy " +
					"fleet defeated.");
		}
		interrupt = true;
		endGame();
	}
	
	protected void reactionOnLoose(Header header) {
		if (header.equals(Header.TKO_LOOSE)) {
			view.log.println("Oh! TKO lose. We lose!");
		} else {
			view.log.println("Oh! We lose!");
		}
		interrupt = true;
		endGame();
	}

	
	protected void connect(String url, String port) throws IllegalArgumentException,
														RuntimeException {	
		try {
			int intPort = Integer.parseInt(port);
			Socket socket = new Socket(url, intPort);
			connector = new Connector(socket);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("I found bad port.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("I dosn't now this host name.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Connect is falling");
		}
	}
	
	public void close() {
		if (connector == null) {
			return;
		}
		connector.close();
		connector = null;
	}
	
	public Message recieve() throws RuntimeException {
		if (connector == null) {
			throw new RuntimeException("Connect is lost.");
		}
		try {
			return connector.recieve();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void send(Message msg) throws RuntimeException {
		try {
			connector.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem in connect.");
		}
	}
	
}
