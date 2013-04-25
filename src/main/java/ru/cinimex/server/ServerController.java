/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldInMessage;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import static ru.cinimex.server.FieldLogic.*;
import ru.cinimex.data.TypeCell;

public class ServerController {
	private ClientData client1;
	private ClientData client2;
	private ServerSocket serverSocket;
	protected Connector connector1;
	protected Connector connector2;
	private Connector grabbedConnector;
	
//	private ClientData activeClient;
//	private ClientData notActiveClient;
//	private Connector activeConnector;
//	private Connector notActiveConnector;
	
	private boolean endGame;
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
			} finally {
				System.out.println("Ending game...");
				endGame();
				System.out.println("Game over.");
			}
		}
	}
		
	public void connectClients() {
		while (!isClientCollected()) {
			try {
				connectClientsLoop();
			} catch (IOException e) {
				e.printStackTrace();
				sleepUnderErr();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				sleepUnderErr();
			}
		}
	}
	
	public void connectClientsLoop() throws IOException, 
										ClassNotFoundException {
		Socket socket = getServerSocket().accept();
		grabbedConnector = new Connector(socket);
		Message clientMsg = getGrabbedConnector().recieve();
		if (!isValidInit(clientMsg)) {
			System.out.println("bad init");
			getGrabbedConnector().send(ServerMessages.getBadInitMsg());
			return;
		}
		getGrabbedConnector().send(ServerMessages.getInitMsg());
		Field field = ((FieldInMessage)clientMsg.getBody()).getField();
		if (getClient1() == null || connector1 == null) {
			setClient1(new ClientData(ClientState.NOT_CONNECT, field));
			connector1 = getGrabbedConnector();
		} else if (getClient2() == null || connector2 == null) {
			setClient2(new ClientData(ClientState.NOT_CONNECT, field));
			connector2 = getGrabbedConnector();
		}
	}
	
	public boolean isClientCollected() {
		if (getClient1() != null && 
				getClient2() != null && 
				connector1 != null && 
				connector2 != null) {
			return true;
		}
		return false;
	}
	
	public void startGame() {
		try {
			actionsAndSettingsBeforeStartGame();
		} catch (IOException e1) {
			e1.printStackTrace();
			endGame = true;
			return;
		}
		while (!isEndGame()) {
			try {
				gameLoop();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				endGame = true;
			} catch (IOException e) {
				e.printStackTrace();
				endGame = true;
			}
		}
	}
	
	public void actionsAndSettingsBeforeStartGame() throws IOException {
		endGame = false;
		getClient1().setState(ClientState.STROKE);
		getClient2().setState(ClientState.WAIT_STROKE);
		getActiveConnector().send(ServerMessages.getStrokeMsg());
		getNotActiveConnector().send(ServerMessages.getNotStrokeMsg());
//		connector1.send(ServerMessages.getStrokeMsg());
//		connector2.send(ServerMessages.getNotStrokeMsg());
	}
	
	public void gameLoop() throws ClassNotFoundException, IOException {
		Message message = getActiveConnector().recieve();
		if (!isValidGameMsg(message)) {
			getNotActiveConnector().send(ServerMessages.getTKOWin());
			getActiveConnector().send(ServerMessages.getTKOLoose());
			endGame = true;
			return;
		}
		
		Header header = message.getHeader();
		
		if (header.equals(Header.TKO_LOOSE) || header.equals(Header.LOOSE)) {
			reactionOnLose();
		} else if (header.equals(Header.STROKE)) {
			Point point = (Point) message.getBody();
			reactionOnStroke(point);
		}
	}
	
	public void reactionOnLose() throws IOException {
		getNotActiveConnector().send(ServerMessages.getTKOWin());
		getActiveConnector().send(ServerMessages.getTKOLoose()); // XXX maybe delete this row?
		endGame = true;
	}
	
	public void reactionOnStroke(Point point) throws IOException {
		Field notActiveField = getNotActiveClient().getField();
		
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
	
	public void reactionOnMiss(Point point) throws IOException {
		try {
			getNotActiveConnector().send(ServerMessages.getStrokeMsg(point));
		} catch (SocketException e) {
			getActiveConnector().send(ServerMessages.getTKOWin());
			endGame = true;
			return;
		}
		getActiveConnector().send(ServerMessages.getMiss());
		switchActiveAndNotActiveClient();
	}
	
	public void reactionOnStrike(Point point) throws IOException {
		int x = point.getX();
		int y = point.getY();
		getNotActiveClient().getField().setCell(x, y, TypeCell.STRIKE);
		try {
			getNotActiveConnector().send(ServerMessages.getNotStroke(point));
		} catch (SocketException e) {
			getActiveConnector().send(ServerMessages.getTKOWin());
			endGame = true;
			return;
		}
		getActiveConnector().send(ServerMessages.getStrike());
	}
	
	public void reactionOnWinStroke(Point point) throws IOException {// TODO catch SocketException
		getNotActiveConnector().send(ServerMessages.getLoose(point));
		getActiveConnector().send(ServerMessages.getWin());
		endGame = true;
	}
	
	public void reactionOnBigBang(Point point) throws IOException {
		int x = point.getX();
		int y = point.getY();
		getNotActiveClient().getField().setCell(x, y, TypeCell.STRIKE);
		try {
			getNotActiveConnector().send(ServerMessages.getNotStrokeMsg(point));
		} catch (SocketException e) {
			getActiveConnector().send(ServerMessages.getTKOWin());
			endGame = true;
			return;
		}
		getActiveConnector().send(ServerMessages.getBigBang());
	}
	
	public boolean isValidGameMsg(Message msg) {
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
	
	public boolean isValidInit(Message clientMsg) {
		if (clientMsg == null) {
			throw new NullPointerException();
		}
		if (!clientMsg.getHeader().equals(Header.INIT) ||
				(clientMsg.getBody() == null) ||
				!(clientMsg.getBody() instanceof FieldInMessage)) {
			return false;
		}
		
		FieldInMessage fieldInBody = (FieldInMessage)clientMsg.getBody();
		Field field = fieldInBody.getField();
		
		if (field == null || !isValidInitField(field)) {
			return false;
		}
		return true;
	}
	
	public boolean isValidStroke(Message clientMsg) {
		if (clientMsg == null) {
			throw new NullPointerException();
		}
		if (!clientMsg.getHeader().equals(Header.STROKE) ||
				(clientMsg.getBody() == null) ||
				!(clientMsg.getBody() instanceof Point)) {
			return false;
		}
		
		Point point = (Point)clientMsg.getBody();
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
	
	public Connector getActiveConnector() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return connector1;
		}
		return connector2;
	}
	
	public Connector getNotActiveConnector() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return connector2;
		}
		return connector1;
	}
	
	public ClientData getActiveClient() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return getClient1();
		}
		return getClient2();
	}
	
	
	public ClientData getNotActiveClient() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return getClient2();
		}
		return getClient1();
	}

	public void switchActiveAndNotActiveClient() {
		if (getClient1().getState().equals(ClientState.WAIT_STROKE) &&
				getClient2().getState().equals(ClientState.STROKE)) {
			getClient1().setState(ClientState.STROKE);
			getClient2().setState(ClientState.WAIT_STROKE);
		} else if (getClient2().getState().equals(ClientState.WAIT_STROKE) &&
				getClient1().getState().equals(ClientState.STROKE)) {
			getClient1().setState(ClientState.WAIT_STROKE);
			getClient2().setState(ClientState.STROKE);
		} else {
			throw new RuntimeException("Illegal case.");
		}
	}
	
	public boolean isClient1StrokeAndCliend2WaitStroke() throws RuntimeException {
		if (getClient1().getState().equals(ClientState.STROKE) && 
				getClient2().getState().equals(ClientState.WAIT_STROKE)) {
			return true;
		} else if (getClient1().getState().equals(ClientState.WAIT_STROKE) &&
				getClient2().getState().equals(ClientState.STROKE)) {
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
	
	public void close() {
		if (getServerSocket() == null) {
			return;
		}
		try {
			getServerSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ClientData getClient1() {
		return client1;
	}
	
	public void setClient1(ClientData data) {
		this.client1 = data;
	}

	public ClientData getClient2() {
		return client2;
	}
	
	public void setClient2(ClientData data) {
		this.client2 = data;
	}

	public boolean isEndGame() {
		return endGame;
	}
	
	public void setEndGame(boolean endGame) {
		this.endGame = endGame;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public Connector getGrabbedConnector() {
		return grabbedConnector;
	}
	
	public Connector getConnector1() {
		return this.connector1;
	}
	
	public Connector getConnector2() {
		return this.connector2;
	}
}

