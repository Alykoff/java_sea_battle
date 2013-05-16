/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import static ru.cinimex.server.FieldLogic.isBigBang;
import static ru.cinimex.server.FieldLogic.isStrike;
import static ru.cinimex.server.FieldLogic.isWin;

import java.io.IOException;
import java.net.SocketException;

import ru.cinimex.connector.Connector;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.Field;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.server.EndGameException;
import ru.cinimex.server.ServerMessageValidator;
import ru.cinimex.server.ServerMessages;

public class ServerReactionCommand {
	private ServerMessageValidator msgValidator = new ServerMessageValidator();
	private ServerReactionState state = null;
	
	public ServerReactionCommand(Field notActiveField, Message msg) {
		if (notActiveField == null) {
			throw new NullPointerException("notActiveClient or msg is nullpointer");
		}
		if (!msgValidator.isValidGameMsg(msg)) {
			state = ServerReactionState.INVALID;
		}
		Header header = msg.getHeader();
		if (header.equals(Header.TKO_LOSE) || header.equals(Header.LOSE)) {
			state = ServerReactionState.LOSE;
		} else if (header.equals(Header.STROKE)) {
			Point point = (Point) msg.getBody();
			if (isWin(notActiveField, point)) {
				state = ServerReactionState.WIN;
			} else if (isBigBang(notActiveField, point)) {
				state = ServerReactionState.BIG_BANG;
			} else if (isStrike(notActiveField, point)) {
				state = ServerReactionState.STRIKE;
			} else {
				state = ServerReactionState.MISS;
			}
		}
		if (state == null) {
			throw new RuntimeException("This point can't be achieved.");
		}
	}
	
	public boolean execute(ClientData activeClient, 
			Connector activeConnector, 
			ClientData notActiveClient, 
			Connector notActiveConnector,
			Message msg) throws EndGameException, IOException {
		if (state.equals(ServerReactionState.INVALID)) {
			return reactionOnInvalidMsg(activeConnector, notActiveConnector);
		} else if (state.equals(ServerReactionState.LOSE)) {
			return reactionOnLose(activeConnector, notActiveConnector);
		} else if (state.equals(ServerReactionState.MISS)) {
			return reactionOnMiss(activeConnector, notActiveConnector, msg);
		} else if (state.equals(ServerReactionState.BIG_BANG)) {
			return reactionOnBigBang(activeConnector, notActiveConnector, notActiveClient, msg);
		} else if (state.equals(ServerReactionState.STRIKE)) {
			return reactionOnStrike(activeConnector, notActiveConnector, notActiveClient, msg);
		} else if (state.equals(ServerReactionState.WIN)) {
			return reactionOnWin(activeConnector, notActiveConnector, msg);
		} else {
			throw new RuntimeException("This point can't be achieved.");
		}
	}
	
	private boolean reactionOnInvalidMsg(Connector activeConnector, 
			Connector notActiveConnector) throws IOException {
		notActiveConnector.send(ServerMessages.getTKOWin());
		activeConnector.send(ServerMessages.getTKOLoose());
		throw new EndGameException();
	}
	
	private boolean reactionOnLose(Connector activeConnector, 
			Connector notActiveConnector) throws IOException {
		notActiveConnector.send(ServerMessages.getTKOWin());
		activeConnector.send(ServerMessages.getTKOLoose());
		throw new EndGameException();
	}
	
	private boolean reactionOnMiss(Connector activeConnector,
			Connector notActiveConnector, Message msg) throws IOException {
		
		if (msg == null || 
				msg.getBody() == null || 
				!(msg.getBody() instanceof Point)) {
			try {
				notActiveConnector.send(ServerMessages.getTKOWin());
			} catch (SocketException e) {
				e.printStackTrace();
			}
			activeConnector.send(ServerMessages.getTKOLoose());	
			throw new EndGameException();
		}
		Point point = (Point) msg.getBody();
		try {
			notActiveConnector.send(ServerMessages.getStrokeMsg(point));
		} catch (SocketException e) {
			activeConnector.send(ServerMessages.getTKOWin());
			throw new EndGameException();
		}
		activeConnector.send(ServerMessages.getMiss());
		return true;
	}
	
	private boolean reactionOnStrike(Connector activeConnector,
			Connector notActiveConnector, ClientData notActiveClient, 
										Message msg) throws IOException {
		if (msg == null || 
				msg.getBody() == null || 
				!(msg.getBody() instanceof Point)) {
			try {
				notActiveConnector.send(ServerMessages.getTKOWin());
			} catch (SocketException e) {
				e.printStackTrace();
			}
			activeConnector.send(ServerMessages.getTKOLoose());	
			throw new EndGameException();
		}
		
		Point point = (Point) msg.getBody();		
		int x = point.getX();
		int y = point.getY();
		notActiveClient.getField().setCell(x, y, TypeCell.STRIKE);
		try {
			notActiveConnector.send(ServerMessages.getNotStroke(point));
		} catch (SocketException e) {
			activeConnector.send(ServerMessages.getTKOWin());
			throw new EndGameException();
		}
		activeConnector.send(ServerMessages.getStrike());
		
		return false;
	}
	
	private boolean reactionOnWin(Connector activeConnector,
			Connector notActiveConnector, Message msg) throws IOException {
		if (msg == null || 
				msg.getBody() == null || 
				!(msg.getBody() instanceof Point)) {
			throw new RuntimeException("nullpointer or bad instance");
		}
		Point point = (Point) msg.getBody();
		notActiveConnector.send(ServerMessages.getLoose(point));
		activeConnector.send(ServerMessages.getWin());
		throw new EndGameException();
	}
	
	private boolean reactionOnBigBang(Connector activeConnector,
			Connector notActiveConnector, ClientData notActiveClient, 
										Message msg) throws IOException {
		if (msg == null || 
				msg.getBody() == null || 
				!(msg.getBody() instanceof Point)) {
			try {
				notActiveConnector.send(ServerMessages.getTKOWin());
			} catch (SocketException e) {
				e.printStackTrace();
			}
			activeConnector.send(ServerMessages.getTKOLoose());	
			throw new EndGameException();
		}
		
		Point point = (Point) msg.getBody();		
		int x = point.getX();
		int y = point.getY();
		notActiveClient.getField().setCell(x, y, TypeCell.STRIKE);
		try {
			notActiveConnector.send(ServerMessages.getNotStroke(point));
		} catch (SocketException e) {
			activeConnector.send(ServerMessages.getTKOWin());
			throw new EndGameException();
		}
		activeConnector.send(ServerMessages.getBigBang());
		
		return false;
	}
}

enum ServerReactionState {
	INVALID,
	LOSE,
	MISS,
	BIG_BANG,
	STRIKE,
	WIN
}