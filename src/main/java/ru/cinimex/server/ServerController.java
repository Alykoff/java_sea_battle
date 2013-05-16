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
import ru.cinimex.data.Message;

public class ServerController {
	private ClientData client1;
	private ClientData client2;
	private ServerSocket serverSocket;
	private Connector connector1;
	private Connector connector2;
	private boolean endGame;
	private ServerMessageValidator msgValidator = new ServerMessageValidator();
	private SelectorClientsAndConnectors selector = new SelectorClientsAndConnectors();
	private boolean stop;
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
		do {
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
		} while (!isStop());
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
		if (!msgValidator.isValidInit(clientMsg)) {
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
		do {
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
		} while (!isEndGame());
	}
	
	private void actionsAndSettingsBeforeStartGame() throws IOException {
		endGame = false;
		getClient1().setState(ClientState.STROKE);
		getClient2().setState(ClientState.WAIT_STROKE);
		getConnector1().send(ServerMessages.getStrokeMsg());
		getConnector2().send(ServerMessages.getNotStrokeMsg());
	}
	
	private void gameLoop() throws ClassNotFoundException, IOException {
		ClientData activeClient = selector.getActiveClient(getClient1(), getClient2());
		ClientData notActiveClient = selector.getNotActiveClient(getClient1(), getClient2());
		Connector activeConnector = 
			selector.getActiveConnector(getClient1(), getClient2(), getConnector1(), getConnector2());
		Connector notActiveConnector = 
			selector.getNotActiveConnector(getClient1(), getClient2(), getConnector1(), getConnector2());
		
		Message message = activeConnector.recieve();
		
		Field notActiveField = notActiveClient.getField();
		ServerReactionCommand command = new ServerReactionCommand(notActiveField, message);
		boolean isSwitchClient = command.execute(activeClient,
				activeConnector,
				notActiveClient,
				notActiveConnector,
				message);
		
		if (isSwitchClient) {
			selector.switchActiveAndNotActiveClient(getClient1(), getClient2());
		}
	}
	
	private void endGame() {
		setClient1(null);
		setClient2(null);
		if (getConnector1() != null) getConnector1().close();
		if (getConnector2() !=  null) getConnector2().close();
		setConnector1(null);
		setConnector2(null);
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
	
	public boolean isStop() {
		return stop;
	}
	
	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
