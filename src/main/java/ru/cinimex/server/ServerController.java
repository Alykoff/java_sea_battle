/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldInMessage;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import static ru.cinimex.server.FieldLogic.*;
import ru.cinimex.server.commands.ReactionCommandFactory;
import ru.cinimex.server.commands.ServerReactionCommand;

public class ServerController {
	private ClientData client1;
	private ClientData client2;
	private ServerSocket serverSocket;
	private Connector connector1;
	private Connector connector2;
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
		
	private void connectClients() {
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
	
	private void connectClientsLoop() throws IOException, 
										ClassNotFoundException {
		Socket socket = serverSocket.accept();
		Connector grabbedConnector = new Connector(socket);
		Message clientMsg = grabbedConnector.recieve();
		if (!isValidInit(clientMsg)) {
			System.out.println("bad init");
			grabbedConnector.send(ServerMessages.getBadInitMsg());
			return;
		}
		grabbedConnector.send(ServerMessages.getInitMsg());
		Field field = ((FieldInMessage)clientMsg.getBody()).getField();
		if (getClient1() == null || getConnector1() == null) {
			setClient1(new ClientData(ClientState.NOT_CONNECT, field));
			setConnector1(grabbedConnector);
		} else if (getClient2() == null || getConnector2() == null) {
			setClient2(new ClientData(ClientState.NOT_CONNECT, field));
			setConnector2(grabbedConnector);
		}
	}
	
	private boolean isClientCollected() {
		if (getClient1() != null && 
				getClient2() != null && 
				getConnector1() != null && 
				getConnector2() != null) {
			return true;
		}
		return false;
	}
	
	private void startGame() {
		try {
			actionsAndSettingsBeforeStartGame();
		} catch (IOException e1) {
			e1.printStackTrace();
			setEndGame(true);
			return;
		}
		while (!isEndGame()) {
			try {
				gameLoop();
			} catch (EndGameException e) {
				setEndGame(true);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				setEndGame(true);
			} catch (IOException e) {
				e.printStackTrace();
				setEndGame(true);
			}
		}
	}
	
	private void actionsAndSettingsBeforeStartGame() throws IOException {
		endGame = false;
		getClient1().setState(ClientState.STROKE);
		getClient2().setState(ClientState.WAIT_STROKE);
		getActiveConnector().send(ServerMessages.getStrokeMsg());
		getNotActiveConnector().send(ServerMessages.getNotStrokeMsg());
	}
	
	private void gameLoop() throws ClassNotFoundException, IOException {
		Message message = getActiveConnector().recieve();
		if (!isValidGameMsg(message)) {
			getNotActiveConnector().send(ServerMessages.getTKOWin());
			getActiveConnector().send(ServerMessages.getTKOLoose());
			endGame = true;
			return;
		}
		
		Field notActiveField = getNotActiveClient().getField();
		ReactionCommandFactory commandFactory = new ReactionCommandFactory();
		ServerReactionCommand command = commandFactory.getReactionCommand(notActiveField, message);
		boolean isSwitchClient = command.execute(getActiveClient(), 
				getActiveConnector(), 
				getNotActiveClient(), 
				getNotActiveConnector(), 
				message.getBody());
		
		if (isSwitchClient) {
			switchActiveAndNotActiveClient();
		}
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
	
	private boolean isValidInit(Message clientMsg) {
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
	
	private boolean isValidStroke(Message clientMsg) {
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
	
	private void endGame() {
		setClient1(null);
		setClient2(null);
		if (getConnector1() != null) getConnector1().close();
		if (getConnector2() !=  null) getConnector2().close();
		setConnector1(null);
		setConnector2(null);
	}
	
	private Connector getActiveConnector() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return getConnector1();
		}
		return getConnector2();
	}
	
	private Connector getNotActiveConnector() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return getConnector2();
		}
		return getConnector1();
	}
	
	private ClientData getActiveClient() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return getClient1();
		}
		return getClient2();
	}
	
	
	private ClientData getNotActiveClient() {
		if (isClient1StrokeAndCliend2WaitStroke()) {
			return getClient2();
		}
		return getClient1();
	}

	private void switchActiveAndNotActiveClient() {
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
	
	private boolean isClient1StrokeAndCliend2WaitStroke() throws RuntimeException {
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
		if (serverSocket == null) {
			return;
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isEndGame() {
		return endGame;
	}
	
	public void setEndGame(boolean endGame) {
		this.endGame = endGame;
	}

	public void setClient1(ClientData client1) {
		this.client1 = client1;
	}

	public ClientData getClient1() {
		return client1;
	}

	public void setClient2(ClientData client2) {
		this.client2 = client2;
	}

	public ClientData getClient2() {
		return client2;
	}

	public void setConnector1(Connector connector1) {
		this.connector1 = connector1;
	}

	public Connector getConnector1() {
		return connector1;
	}

	public void setConnector2(Connector connector2) {
		this.connector2 = connector2;
	}

	public Connector getConnector2() {
		return connector2;
	}
	
}

