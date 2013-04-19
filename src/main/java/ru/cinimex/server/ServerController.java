/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import ru.cinimex.connector.Connector;
import ru.cinimex.connector.FieldInBody;
import ru.cinimex.connector.Header;
import ru.cinimex.connector.Message;
import ru.cinimex.connector.PointInBody;
import ru.cinimex.data.ClientDataCore;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.LogicForField;
import ru.cinimex.data.TypeCell;

public class ServerController {
	protected ClientDataCore client1;
	protected ClientDataCore client2;
	protected ServerSocket serverSocket;
	protected Connector connector1;
	protected Connector connector2;
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
				Field field = new Field(((FieldInBody)clientMsg.getBody()).getField());
				if (client1 == null || connector1 == null) {
					client1 = new ClientDataCore(ClientState.NOT_CONNECT, field);
					connector1 = connector;
				} else if (client2 == null || connector2 == null) {
					client2 = new ClientDataCore(ClientState.NOT_CONNECT, field);
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
		client1.setState(ClientState.STROKE);
		client2.setState(ClientState.WAIT_STROKE);
		connector1.send(ServerMessages.getStrokeMsg());
		connector2.send(ServerMessages.getNotStrokeMsg());
		while (true) {
			ClientDataCore activeClient = getActiveClient();
			ClientDataCore passiveClient = getPassiveClient();
			Connector activeConnector = getActiveClientConnector();
			Connector passiveConnector = getPassiveClientConnector();
			
			Message msgFromActiveConnector = activeConnector.recieve();
			
			if (msgFromActiveConnector.getHeader().equals(Header.TKO_LOOSE)) {
				passiveConnector.send(ServerMessages.getTKOWin());
				break;
			}
			if (!isValidStroke(msgFromActiveConnector)) {
				activeConnector.send(ServerMessages.getBadStrokeMsg());
				continue;
			}
			
			PointInBody point = (PointInBody)msgFromActiveConnector.getBody();
			int x = point.getX();
			int y = point.getY();
			
			Field passiveField = passiveClient.getField();
			if (LogicForField.isValidLooseField(x, y, passiveField)) {
				passiveConnector.send(ServerMessages.getLoose(point));
				activeConnector.send(ServerMessages.getWin());
				break;
			} else if (LogicForField.detectBigBang(x, y, passiveField)) {
				passiveClient.getField().setCell(x, y, TypeCell.STRAKE);
				try {
					passiveConnector.send(ServerMessages.getNotStrokeMsg(point));
				} catch (SocketException e) {
					activeConnector.send(ServerMessages.getTKOWin());
					return;
				}
				activeConnector.send(ServerMessages.getBigBang());
				
			} else if (passiveField.isStrike(x, y)) {
				passiveClient.getField().setCell(x, y, TypeCell.STRAKE);
				try {
					passiveConnector.send(ServerMessages.getNotStrike(point));
				} catch (SocketException e) {
					activeConnector.send(ServerMessages.getTKOWin());
					return;
				}
				activeConnector.send(ServerMessages.getStrike());
			} else {
				activeClient.setState(ClientState.WAIT_STROKE);
				passiveClient.setState(ClientState.STROKE);
				try {
					passiveConnector.send(ServerMessages.getStrokeMsg(point));
				} catch (SocketException e) {
					activeConnector.send(ServerMessages.getTKOWin());
					return;
				}
				activeConnector.send(ServerMessages.getMiss());
			}
		}
	}

	protected void endGame() {
		client1 = null;
		client2 = null;
		if (connector1 != null) connector1.close();
		if (connector2 !=  null) connector2.close();
		connector1 = null;
		connector2 = null;
	}
	
	private boolean isValidInit(Message clientMsg) throws IOException, 
								ClassNotFoundException, ClassCastException {
		if (clientMsg == null) throw new RuntimeException();
		if (!clientMsg.getHeader().equals(Header.INIT) ||
				(clientMsg.getBody() == null) ||
				!(clientMsg.getBody() instanceof FieldInBody) ||
				(((FieldInBody)clientMsg.getBody()).getField() == null) ||
				!LogicForField.isValidInitField(
						new Field(
							((FieldInBody)clientMsg.getBody()).getField()))) {
			return false;
		}
		return true;			
	}
	
	private boolean isValidStroke(Message clientMsg) {
		if (clientMsg == null) throw new RuntimeException();
		if (!clientMsg.getHeader().equals(Header.STROKE) ||
				(clientMsg.getBody() == null) ||
				!(clientMsg.getBody() instanceof PointInBody) ||
				!LogicForField.validateStroke(
						((PointInBody)clientMsg.getBody()).getX(),
						((PointInBody)clientMsg.getBody()).getY())) {
			return false;
		}
		return true;
	}
	
	private Connector getActiveClientConnector() {
		if (client1.getState().equals(ClientState.STROKE) &&
				client2.getState().equals(ClientState.WAIT_STROKE)) {
			return connector1;
		} else if (client2.getState().equals(ClientState.STROKE) &&
				client1.getState().equals(ClientState.WAIT_STROKE)) {
			return connector2;
		} else {
			throw new RuntimeException();
		}
	}
	
	private Connector getPassiveClientConnector() {
		if (client1.getState().equals(ClientState.STROKE) &&
				client2.getState().equals(ClientState.WAIT_STROKE)) {
			return connector2;
		} else if (client2.getState().equals(ClientState.STROKE) &&
				client1.getState().equals(ClientState.WAIT_STROKE)) {
			return connector1;
		} else {
			throw new RuntimeException();
		}
	}
	
	private ClientDataCore getActiveClient() {
		if (client1.getState().equals(ClientState.STROKE) &&
				client2.getState().equals(ClientState.WAIT_STROKE)) {
			return client1;
		} else if (client2.getState().equals(ClientState.STROKE) &&
				client1.getState().equals(ClientState.WAIT_STROKE)) {
			return client2;
		} else {
			throw new RuntimeException();
		}
	}
	
	private ClientDataCore getPassiveClient() {
		if (client1.getState().equals(ClientState.STROKE) &&
				client2.getState().equals(ClientState.WAIT_STROKE)) {
			return client2;
		} else if (client2.getState().equals(ClientState.STROKE) &&
				client1.getState().equals(ClientState.WAIT_STROKE)) {
			return client1;
		} else {
			throw new RuntimeException();
		}
	}
		
	private void sleepUnderErr() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
