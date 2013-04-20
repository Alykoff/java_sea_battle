/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.data;

import java.util.Date;

public class ClientData {
	protected int id;
	protected Field field = new Field();
	protected ClientState state;
	protected long dateLastMsg;
	
	public ClientData(ClientState state) {
		if (state == null) {
			throw new RuntimeException();
		}
		this.setState(state);
		this.setId(id);
		setDateLastMsg(new Date().getTime());
	}
	
	public ClientData(ClientState state, Field field) {
		this(state);
		if (field == null) {
			throw new RuntimeException();
		}
		this.field = field;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	public void setState(ClientState state) {
		this.state = state;
	}

	public ClientState getState() {
		return state;
	}

	public void setDateLastMsg(long dateLastMsg) {
		this.dateLastMsg = dateLastMsg;
	}

	public long getDateLastMsg() {
		return dateLastMsg;
	}
	
}
