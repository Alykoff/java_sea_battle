/**
 * @author Alykov Gali
 * @date 10.04.2013
 */
package ru.cinimex.server;

import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;

public class ServerMessages {
	
	public static Message getInitMsg() {
		return new Message(Header.INIT, null);
	}
	
	public static Message getBadInitMsg() {
		return new Message(Header.BAD_INIT, null);
	}
	
	public static Message getStrokeMsg() {
		return new Message(Header.STROKE, null);
	}
	
	public static Message getStrokeMsg(Point point) {
		if (point == null) {
			throw new NullPointerException();
		}
		return new Message(Header.STROKE, point);
	}
	
	public static Message getNotStrokeMsg() {
		return new Message(Header.NOT_STROKE, null);
	}
	
	public static Message getNotStrokeMsg(Point point) {
		if (point == null) {
			throw new NullPointerException();
		}
		return new Message(Header.NOT_STROKE, point);
	}
	
	@Deprecated
	public static Message getBadStrokeMsg() {
		return new Message(Header.BAD_STROKE, null);
	}
	
	public static Message getTKOWin() {
		return new Message(Header.TKO_WIN, null);
	}
	
	public static Message getTKOLoose() {
		return new Message(Header.TKO_LOSE, null);
	}
	
	public static Message getWin() {
		return new Message(Header.WIN, null);
	}
	
	public static Message getLoose(Point point) {
		return new Message(Header.LOSE, point);
	}
	
	public static Message getBigBang() {
		return new Message(Header.BIG_BANG, null);
	}
	
	public static Message getStrike() {
		return new Message(Header.STRIKE, null);
	}
	
	public static Message getNotStroke(Point point) {
		if (point == null) {
			throw new NullPointerException();
		}
		return new Message(Header.NOT_STROKE, point);
	}
	
	public static Message getMiss() {
		return new Message(Header.NOT_STROKE, null);
	}
}
