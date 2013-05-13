/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldInMessage;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.data.TypeField;
import ru.cinimex.server.FieldLogic;

public class ClientController {
	private Connector connector = null;
	private View view;
	private ClientData data;
	protected boolean interrupt = false;
	protected final int MAX_TIME_STROKE = 3 * 60 * 1000;
	
	public ClientController() {
		setData(new ClientData(ClientState.NOT_CONNECT));
	}
	
	public void startClient() {
		setView(new View(this));
		this.view.setVisible(true);
	}
	
	public static void main(String[] args) {
		ClientController controller = new ClientController();
		controller.startClient();
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
					getView().switchToStartGameMode();
					getData().setState(ClientState.WAIT);
					processingGame();
				}
			}).start();
		} catch (IllegalArgumentException e) {
			getView().println(e.getMessage());
			e.printStackTrace();
		} catch (RuntimeException e) {
			getView().println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Message getInitMsg() {
		Field field = getView().getField(TypeField.OUR);
		FieldInMessage body = new FieldInMessage(field);
		Header header = Header.INIT;
		return new Message(header, body);
	}
	
	public void processingGame() {
		while (!interrupt) {
			try {
				gameLoop();
			} catch (RuntimeException e) {
				e.printStackTrace();
				getView().println(e.getMessage());
				interrupt = true;
				endGame();
			}
		}
	}
	
	public void gameLoop() {
		Message msg = recieve();
		Header header = msg.getHeader();
		BodyMessage body = msg.getBody();
		if (header.equals(Header.BAD_INIT)) {
			reactionOnBadInit();
		} else if (header.equals(Header.TKO_WIN) || header.equals(Header.WIN)) {
			reactionOnWin(header);
		} else if (header.equals(Header.TKO_LOSE) || header.equals(Header.LOSE)) {
			reactionOnLoose(header);
		} else if (header.equals(Header.BIG_BANG) || header.equals(Header.STRIKE)) {
			reactionOnGoodShot(header, body);
		} else if (header.equals(Header.STROKE)) {
			reactionOnStroke(header, body);
		} else if (header.equals(Header.NOT_STROKE)) {
			reactionOnStrokeTabu(body);
		}
	}	
	
	public void reactionOnBadInit() {// XXX
		getView().println("Bad init.");
		interrupt = true;
		endGame();
	}
	
//	public void reactionOnBadStroke() {
//		getView().println("bad stroke.");
//		waitAndReactionToStroke();
//	}
	
	public void reactionOnStroke(Header header, BodyMessage body) {// XXX
		if (body != null || body instanceof Point) {
			Point point = (Point) body;
			reactionOnSuccessfulStroke(point);
		} 
		getView().println("You stroke.");
		waitAndReactionToStroke();
	}
	
	public void reactionOnSuccessfulStroke(Point point) {// XXX
		if (getView().getCell(point, TypeField.OUR).equals(TypeCell.WATER)) {
			getView().setCell(point, TypeCell.MISS, TypeField.OUR);
		}		
	}
	
	public void reactionOnGoodShot(Header header, BodyMessage body) { // XXX
		final Point stroke = getView().getLastStroke();
		if (stroke == null) {
			getView().println("Upps! We have problem! " +
					"Sorry. End game(");
			interrupt = true;
			endGame();
		}
		getView().setCell(stroke, TypeCell.STRIKE, TypeField.OPPONENT);
		if (header.equals(Header.BIG_BANG)) {
			paintPaddedShip(stroke, TypeField.OPPONENT);
			getView().println("Yeeh! You blew up this ship!");
		} else {
			getView().println("Good shot!");
		}
		getView().cleanLastStroke();
		waitAndReactionToStroke();
	}
	
	public void waitAndReactionToStroke() { // XXX
		long beginTime = new Date().getTime();
		getData().setState(ClientState.STROKE);
		getView().cleanLastStroke();
		while (getView().getLastStroke() == null) {
			if ((beginTime + MAX_TIME_STROKE) < new Date().getTime()) {
				interrupt = true;
				getView().println("Your time is over. You lose.");
				return;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		getData().setState(ClientState.WAIT); 
		try {
			Point stroke = getView().getLastStroke();
			send(new Message(Header.STROKE, stroke));
		} catch (RuntimeException e) {
			getView().println(e.getMessage());
			interrupt = true;
			endGame();
		}
	}
	
	public void endGame() {
		getData().setState(ClientState.NOT_CONNECT);
		getView().switchToEndGame();
	}
	
	public void paintPaddedShip(Point point, TypeField typeField) {// XXX
		if (point == null || typeField == null) {
			throw new NullPointerException();
		}
		if (getView().getCell(point, typeField).equals(TypeCell.WATER)) {
			getView().setCell(point, TypeCell.MISS, typeField);
		}
		if (!getView().getCell(point, typeField).equals(TypeCell.STRIKE)) {
			return;
		}
		getView().setCell(point, TypeCell.BIG_BANG, typeField);
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
				paintPaddedShip(new Point(i, j), typeField);
			}
		}
	
	}
	
//	public void reactionOnOfferToStroke(BodyMessage body) {
//		getView().println("Your turn began.");
//		if (body != null && (body instanceof Point)) {
//			getView().println("Your opponent missed!");
//			Point point = (Point) body;
//			TypeCell cellXY = getView().getCell(point, TypeField.OUR);
//			if (cellXY.equals(TypeCell.WATER) || cellXY.equals(TypeCell.MISS)) {
//				getView().setCell(point, TypeCell.MISS, TypeField.OUR);
//			}
//		}							
//		waitAndReactionToStroke();
//	}
	
	public void reactionOnStrokeTabu(BodyMessage body) { // XXX
		getView().println("Please wait for the opponent's turn.");
		getData().setState(ClientState.WAIT);
		if (body != null && (body instanceof Point)) {
			Point point = (Point) body;
			reactionOnHitInOurShip(point);
		} else if (getView().getLastStroke() != null) {
			reactionOnMiss();
		}
	}
	
	public void reactionOnHitInOurShip(Point point) {// XXX
		getView().println("Hit on our ship!");
		Field field = getView().getField(TypeField.OUR);
		boolean isBigBang = FieldLogic.isBigBang(field, point);
		getView().setCell(point, TypeCell.STRIKE, TypeField.OUR);
		if (isBigBang) {
			paintPaddedShip(point, TypeField.OUR);
		}
	}
	
	public void reactionOnMiss() { // XXX
		Point stroke = getView().getLastStroke();
		TypeCell  typeCellUnderStrokePoint = getView().getCell(stroke, TypeField.OPPONENT);
		if (typeCellUnderStrokePoint.equals(TypeCell.WATER)) {
			getView().setCell(stroke, TypeCell.MISS, TypeField.OPPONENT);
		}
		getView().cleanLastStroke();
	}
	
	public void reactionOnWin(Header header) { // XXX
		if (header.equals(Header.TKO_WIN)) {
			getView().println("Congratulations! You win! " +
					"Enemy fleet fled.");
		} else {
			getView().println(
					"Congratulations! You win! You " +
					"have a terrific excerpt. Enemy " +
					"fleet defeated.");
		}
		interrupt = true;
		endGame();
	}
	
	public void reactionOnLoose(Header header) { // XXX
		if (header.equals(Header.TKO_LOSE)) {
			getView().println("Oh! TKO lose. We lose!");
		} else if (header.equals(Header.LOSE)) {
			getView().println("Oh! We lose!");
		} else {
			throw new RuntimeException("Bad header.");
		}
		interrupt = true;
		endGame();
	}

	
	public void connect(String url, String port) throws IllegalArgumentException,
														RuntimeException {	
		try {
			int intPort = Integer.parseInt(port);
			Socket socket = new Socket(url, intPort);
			setConnector(new Connector(socket));
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
		if (getConnector() == null) {
			return;
		}
		getConnector().close();
		setConnector(null);
	}
	
	public Message recieve() throws RuntimeException {
		if (getConnector() == null) {
			throw new RuntimeException("Connect is lost.");
		}
		try {
			return getConnector().recieve();
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
			getConnector().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem in connect.");
		}
	}

	protected void setView(View view) {
		this.view = view;
	}

	public View getView() {
		return view;
	}

	protected void setData(ClientData data) {
		this.data = data;
	}

	public ClientData getData() {
		return data;
	}

	protected void setConnector(Connector connector) {
		this.connector = connector;
	}

	public Connector getConnector() {
		return connector;
	}
	
	public void exit() {
		
	}
	
}
