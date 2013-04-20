/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import ru.cinimex.connector.BodyMessage;
import ru.cinimex.connector.Connector;
import ru.cinimex.connector.FieldInBody;
import ru.cinimex.connector.Header;
import ru.cinimex.connector.Message;
import ru.cinimex.connector.PointInBody;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import static ru.cinimex.data.FieldLogic.*;
import ru.cinimex.data.TypeCell;

public class ServerController {
	protected ClientData client1;
	protected ClientData client2;
	protected ServerSocket serverSocket;
	protected Connector connector1;
	protected Connector connector2;
	
	private ClientData activeClient;
	private ClientData notActiveClient;
	private Connector activeConnector;
	private Connector notActiveConnector;
	
	protected boolean isEndGame;
	public static final int SERVER_PORT = 9000;
	
	public ServerController() {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) {
		new ServerController().startServer();
	}

	public void startServer() {
		System.out.println("Starting sever...");
		while (true) {
			try {
				System.out.println("Connect clients...");
				connectClients();
				System.out.println("All clients are connect.");
				System.out.println("Start game.");
				startGame();
			} catch (IOException e) {
				e.printStackTrace();
				sleepUnderErr();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				sleepUnderErr();
			} catch (ClassCastException e) {
				e.printStackTrace();
				sleepUnderErr();
			} finally {
				System.out.println("Ending game...");
				endGame();
				System.out.println("Game over.");
			}
		}
	}
		
	protected void connectClients() {
		while (!isClientCollected()) {
			try {
				Socket socket = serverSocket.accept();
				Connector connector = new Connector(socket);
				Message clientMsg = connector.recieve();
				if (!isValidInit(clientMsg)) {
					System.out.println("bad init");
					connector.send(ServerMessages.getBadInitMsg());
					continue;
				}
				connector.send(ServerMessages.getInitMsg());
				Field field = ((FieldInBody)clientMsg.getBody()).getField();
				if (client1 == null || connector1 == null) {
					client1 = new ClientData(ClientState.NOT_CONNECT, field);
					connector1 = connector;
				} else if (client2 == null || connector2 == null) {
					client2 = new ClientData(ClientState.NOT_CONNECT, field);
					connector2 = connector;
				}
			} catch (IOException e) {
				e.printStackTrace();
				sleepUnderErr();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				sleepUnderErr();
			} catch (ClassCastException e) {
				e.printStackTrace();
				sleepUnderErr();
			}
		}
	}
	
	protected boolean isClientCollected() {
		if (client1 != null && 
				client2 != null && 
				connector1 != null && 
				connector2 != null) {
			return true;
		}
		return false;
	}
	
	protected void startGame() throws IOException, ClassNotFoundException, ClassCastException {
		isEndGame = false;
		client1.setState(ClientState.STROKE);
		client2.setState(ClientState.WAIT_STROKE);
		connector1.send(ServerMessages.getStrokeMsg());
		connector2.send(ServerMessages.getNotStrokeMsg());
		
		while (!isEndGame) {
			activeClient = getActiveClient();
			notActiveClient = getNotActiveClient();
			activeConnector = getActiveConnector();
			notActiveConnector = getNotActiveConnector();
			
			Message message = activeConnector.recieve();
			if (!isValidGameMsg(message)) {
				notActiveConnector.send(ServerMessages.getTKOWin());
				activeConnector.send(ServerMessages.getTKOLoose());
				isEndGame = true;
				continue;
			}
			
			Header header = message.getHeader();
			BodyMessage body = message.getBody();
			
			if (header.equals(Header.TKO_LOOSE) || header.equals(Header.LOOSE)) {
				reactionOnLose();
			} else {
				reactionOnStroke(body);
			}			
		}
	}
	
	private void reactionOnLose() throws IOException {
		notActiveConnector.send(ServerMessages.getTKOWin());
		isEndGame = true;
	}
	
	private void reactionOnStroke(BodyMessage body) throws IOException {
		PointInBody point = (PointInBody)body;
		Field notActiveField = notActiveClient.getField();
		
		if (isWinningStroke(notActiveField, point)) {
			reactionOnWinStroke(point);
		} else if (detectBigBang(notActiveField, point)) {
			reactionOnBigBang(point);
		} else if (isStrike(notActiveField, point)) {
			reactionOnStrike(point);
		} else {
			reactionOnMiss(point);
		}
	}
	
	private void reactionOnMiss(PointInBody point) throws IOException {
		activeClient.setState(ClientState.WAIT_STROKE);
		notActiveClient.setState(ClientState.STROKE);
		try {
			notActiveConnector.send(ServerMessages.getStrokeMsg(point));
		} catch (SocketException e) {
			activeConnector.send(ServerMessages.getTKOWin());
			isEndGame = true;
			return;
		}
		activeConnector.send(ServerMessages.getMiss());
	}
	
	private void reactionOnStrike(PointInBody point) throws IOException {
		int x = point.getX();
		int y = point.getY();
		notActiveClient.getField().setCell(x, y, TypeCell.STRAKE);
		try {
			notActiveConnector.send(ServerMessages.getNotStrike(point));
		} catch (SocketException e) {
			activeConnector.send(ServerMessages.getTKOWin());
			isEndGame = true;
			return;
		}
		activeConnector.send(ServerMessages.getStrike());
	}
	
	private void reactionOnWinStroke(PointInBody point) throws IOException {
		notActiveConnector.send(ServerMessages.getLoose(point));
		activeConnector.send(ServerMessages.getWin());
		isEndGame = true;
	}
	
	private void reactionOnBigBang(PointInBody point) throws IOException {
		int x = point.getX();
		int y = point.getY();
		notActiveClient.getField().setCell(x, y, TypeCell.STRAKE);
		try {
			notActiveConnector.send(ServerMessages.getNotStrokeMsg(point));
		} catch (SocketException e) {
			activeConnector.send(ServerMessages.getTKOWin());
			isEndGame = true;
			return;
		}
		activeConnector.send(ServerMessages.getBigBang());
	}
	
	private boolean isValidGameMsg(Message msg) {
		if (msg == null) {
			return false;
		}
		Header header = msg.getHeader();
		if (header == null) {
			return false;
		}
		if (!header.equals(Header.LOOSE) && 
				!header.equals(Header.TKO_LOOSE) &&
				!header.equals(Header.STROKE)) {
			return false;
		}
		if (header.equals(Header.STROKE) && !isValidStroke(msg)) {
			return false;
		}
		return true;
	}
	
	private boolean isValidInit(Message clientMsg) throws IOException, 
								ClassNotFoundException, ClassCastException {
		if (clientMsg == null) {
			throw new RuntimeException();
		}
		if (!clientMsg.getHeader().equals(Header.INIT) ||
				(clientMsg.getBody() == null) ||
				!(clientMsg.getBody() instanceof FieldInBody)) {
			return false;
		}
		
		FieldInBody fieldInBody = (FieldInBody)clientMsg.getBody();
		Field field = fieldInBody.getField();
		
		if (field == null || !isValidInitField(field)) {
			return false;
		}
		return true;			
	}
	
	private boolean isValidStroke(Message clientMsg) {
		if (clientMsg == null) {
			throw new RuntimeException();
		}
		if (!clientMsg.getHeader().equals(Header.STROKE) ||
				(clientMsg.getBody() == null) ||
				!(clientMsg.getBody() instanceof PointInBody)) {
			return false;
		}
		
		PointInBody point = (PointInBody)clientMsg.getBody();
		int x = point.getX();
		int y = point.getY();
		
		if (!validateStroke(x, y)) {
			return false;
		}
		return true;
	}	
	
	protected void endGame() {
		client1 = null;
		client2 = null;
		if (connector1 != null) connector1.close();
		if (connector2 !=  null) connector2.close();
		connector1 = null;
		connector2 = null;
	}
	
	private Connector getActiveConnector() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return connector1;
		}
		return connector2;
	}
	
	private Connector getNotActiveConnector() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return connector2;
		}
		return connector1;
	}
	
	private ClientData getActiveClient() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return client1;
		}
		return client2;
	}
	
	private ClientData getNotActiveClient() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return client2;
		}
		return client1;
	}
	
	private boolean isClient1StrokeAndCliend2WaitStroke() throws RuntimeException {
		if (client1.getState().equals(ClientState.STROKE) && 
				client2.getState().equals(ClientState.WAIT_STROKE)) {
			return true;
		} else if (client1.getState().equals(ClientState.WAIT_STROKE) &&
				client2.getState().equals(ClientState.STROKE)) {
			return false;			
		}
		throw new RuntimeException("Bad client state.");	
	}	
		
	private void sleepUnderErr() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}	
}

