/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.connector;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = -637186686978647165L;
	private final Header header;
	private BodyMessage body;
	
	public Message(Header header, BodyMessage body) {
		if (header == null) throw new RuntimeException();
		this.header = header;
		this.body = body;
	}
	
	public Header getHeader() {
		return header;
	}
	public void setBody(BodyMessage body) {
		this.body = body;
	}
	public BodyMessage getBody() {
		return body;
	}
	
	public String toString() {
		return "Header = " + header + "\n" +
				"Body = " + body;
	}
}
