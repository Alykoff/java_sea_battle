/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import ru.cinimex.connector.Connector;
import ru.cinimex.connector.Message;

public class ClientController {
	Connector connector = null;
	
	public void connect(String url, String port) throws IllegalArgumentException,
														RuntimeException {	
		if (url == null || port == null) {
			throw new IllegalArgumentException("I found null pointer.");
		}
		try {
			int intPort = Integer.parseInt(port);
			Socket socket = new Socket(url, intPort);
			connector = new Connector(socket);
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
		if (connector == null) {
			return;
		}
		connector.close();
		connector = null;
	}
	
	public Message recieve() throws RuntimeException {
		if (connector == null) {
			throw new RuntimeException("Connect is lost.");
		}
		try {
			return connector.recieve();
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
			connector.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem in connect.");
		}
	}
	
}
