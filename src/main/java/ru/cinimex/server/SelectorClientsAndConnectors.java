/**
 * @author Alykov Gali
 * @date 14.05.2013
 */
package ru.cinimex.server;

import ru.cinimex.connector.Connector;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;

public class SelectorClientsAndConnectors {
	public Connector getActiveConnector(ClientData client1, ClientData client2, 
			Connector connector1, Connector connector2) {
		if (isClient1StrokeAndCliend2WaitStroke(client1, client2)) {
			return connector1;
		}
		return connector2;
	}
	
	public Connector getNotActiveConnector(ClientData client1, ClientData client2,
			Connector connector1, Connector connector2) {
		if (isClient1StrokeAndCliend2WaitStroke(client1, client2)) {
			return connector2;
		}
		return connector1;
	}
	
	public ClientData getActiveClient(ClientData client1, ClientData client2) {
		if (isClient1StrokeAndCliend2WaitStroke(client1, client2)) {
			return client1;
		}
		return client2;
	}
	
	public ClientData getNotActiveClient(ClientData client1, ClientData client2) {
		if (isClient1StrokeAndCliend2WaitStroke(client1, client2)) {
			return client2;
		}
		return client1;
	}
	
	public boolean isClient1StrokeAndCliend2WaitStroke(ClientData client1, 
								ClientData client2) throws RuntimeException {
		if (client1.getState().equals(ClientState.STROKE) && 
				client2.getState().equals(ClientState.WAIT_STROKE)) {
			return true;
		} else if (client1.getState().equals(ClientState.WAIT_STROKE) &&
				client2.getState().equals(ClientState.STROKE)) {
			return false;
		}
		throw new RuntimeException("Bad client state.");
	}
	
	public void switchActiveAndNotActiveClient(ClientData client1, ClientData client2) {
		if (client1.getState().equals(ClientState.WAIT_STROKE) &&
				client2.getState().equals(ClientState.STROKE)) {
			client1.setState(ClientState.STROKE);
			client2.setState(ClientState.WAIT_STROKE);
		} else if (client2.getState().equals(ClientState.WAIT_STROKE) &&
				client1.getState().equals(ClientState.STROKE)) {
			client1.setState(ClientState.WAIT_STROKE);
			client2.setState(ClientState.STROKE);
		} else {
			throw new RuntimeException("Illegal case.");
		}
	}
}
