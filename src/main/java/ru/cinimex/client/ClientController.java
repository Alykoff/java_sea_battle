/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import ru.cinimex.client.commands.ClientReactionCommand;
import ru.cinimex.client.commands.ReactionClientCommandFactory;
import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldInMessage;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.TypeField;
import ru.cinimex.server.EndGameException;

public class ClientController {
	private Connector connector = null;
	private View view;
	private ClientData data;
	protected boolean interrupt = false;
	protected final int MAX_TIME_STROKE = 3 * 60 * 1000;
	protected final ClientMessages msgFactory = new ClientMessages();
	
	public ClientController() {
		setData(new ClientData(ClientState.NOT_CONNECT));
	}
	
	public void startClient() {
		final ClientController controller = this;
		setView(new View() {
			private static final long serialVersionUID = -5275030778037759937L;
			
			@Override
			protected void onclickStart(final String url, final String port) {
				controller.reactionOnStart(url, port);
			}
			
			@Override
			protected void onclickLoosing() {
				controller.send(msgFactory.getTKOLose());
				controller.close();
				log.println("By your command of the fleet " +
						"retreats.\nThe battle was lost!\n");
				endGame();
			}
		});
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
					getView().startGameMode();
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
		return msgFactory.getInit(body);
	}
	
	public void processingGame() {
		while (!interrupt) {
			try {
				gameLoop();
			} catch (EndGameException e) {
				endGame();
			} catch (RuntimeException e) {
				e.printStackTrace();
				getView().println(e.getMessage());
				endGame();
			}
		}
	}
	
	public void gameLoop() {
		Message msg = recieve();
		Header header = msg.getHeader();
		BodyMessage body = msg.getBody();
		ReactionClientCommandFactory factory = new ReactionClientCommandFactory();
		ClientReactionCommand command = factory.getCommand(header);
		command.execute(body, view, connector, data);
	}
	
	public void endGame() {
		interrupt = true;
		getData().setState(ClientState.NOT_CONNECT);
		getView().endGame();
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
	
}
