/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import ru.cinimex.data.Message;

public class Connector {
	protected Socket socket;
	
	public Connector(Socket socket) {
		if (socket == null) {
			throw new RuntimeException();
		}
		this.socket = socket;
	}
	
	public boolean isClose() {
		return socket.isClosed();
	}
	
	public void send(Message msg) throws IOException {
		
		System.out.println("\n\n==============Send==============");
		System.out.println(new java.util.Date().toString());
		System.out.println(msg.toString());
		
		OutputStream outputStream = null;
		ObjectOutputStream objOutputStream = null;
		outputStream = socket.getOutputStream();
		objOutputStream = new ObjectOutputStream(outputStream);
		objOutputStream.writeObject(msg);
		
		System.out.println("===================================");
	}
	
	public Message recieve() throws IOException, ClassNotFoundException, ClassCastException {
		
		System.out.println("\n\n====================Resieve==================");
		System.out.println(new java.util.Date().toString());
		
		InputStream inputStream = null;
		ObjectInputStream objInputStream = null;
		System.out.println(socket.isClosed() + " " + new java.util.Date().toString());
		inputStream = socket.getInputStream();
		objInputStream = new ObjectInputStream(inputStream);
		
		Message msg = (Message) objInputStream.readObject();
		System.out.println(msg.toString());
		System.out.println("=============================================");
		
		return msg;		
	}
	
	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
