/**
 * @author Alykov Gali
 * @date 23.04.2013
 */
package ru.cinimex.test;

import java.io.IOException;
import java.net.Socket;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.cinimex.client.ClientMessages;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldInMessage;
import ru.cinimex.data.Message;
import ru.cinimex.data.TypeCell;
import ru.cinimex.server.ServerController;
import static org.mockito.Mockito.*;

@Ignore
public class TestServerController extends TestCase {
	int s, w, t, b, m;
	ClientData nullClient;
	ClientData notConnectClient;
	Message validInitMsg;
	
	@Before
	public void setUp() throws Exception {
		s = TypeCell.SHIP.ordinal();
		w = TypeCell.WATER.ordinal();
		t = TypeCell.STRIKE.ordinal();
		b = TypeCell.BIG_BANG.ordinal();
		m = TypeCell.MISS.ordinal();
		
		int[][] validInitData1 = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, s, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w}
		};
		Field validInitField1 = new Field(validInitData1);
		FieldInMessage validFieldInMsg1 = new FieldInMessage(validInitField1);
		validInitMsg = new ClientMessages().getInit(validFieldInMsg1);
		
		notConnectClient = new ClientData(ClientState.NOT_CONNECT);
		nullClient = null;
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Ignore
	@Test
	public void testValidInitMsg() throws ClassCastException, IOException, ClassNotFoundException {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);

		Connector connector1 = new Connector(new Socket());
		Connector spyConnector = spy(connector1);
		when(spyConnector.recieve()).thenReturn(validInitMsg);
//		when(spyController.)
		when(spyController.getConnector1()).thenReturn(spyConnector);
		when(spyController.getConnector2()).thenReturn(spyConnector);
		
		when(spyController.getClient1()).thenReturn(nullClient);
		when(spyController.getClient2()).thenReturn(notConnectClient);
		
		
		controller.close();
	}
	
/*
	@Test
	public void testIsValidInit() {
		ServerController controller = new ServerController();
		try {
			controller.isValidInit(null);
			fail("NullPointerException did't catch");
		} catch (NullPointerException e) {
			// not tracing
		}
		int[][] validInitData1 = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, s, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w}
		};
		Field validInitField1 = new Field(validInitData1);
		FieldInMessage validFieldInMsg1 = new FieldInMessage(validInitField1);
		Point point = new Point();
		
		Message invalidMsg1 = new Message(Header.INIT, null);
		Message invalidMsg2 = new Message(Header.STROKE, validFieldInMsg1);
		Message invalidMsg3 = new Message(Header.INIT, point);
		assertFalse(controller.isValidInit(invalidMsg1));
		assertFalse(controller.isValidInit(invalidMsg2));
		assertFalse(controller.isValidInit(invalidMsg3));
		
		Message validMsg1 = new Message(Header.INIT, validFieldInMsg1);
		assertTrue(controller.isValidInit(validMsg1));
		
		int[][] invalidData1 = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, s, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w}
		};
		Field invalidField1 = new Field(invalidData1);
		FieldInMessage invalidFieldInBody1 = new FieldInMessage(invalidField1);
		Message invalidMsg4 = new Message(Header.INIT, invalidFieldInBody1);
		assertFalse(controller.isValidInit(invalidMsg4));
		
		controller.close();
	}
	

	@Test
	public void testIsValidGameMsg1() {
		ServerController controller = new ServerController();
		Header initHeader = Header.INIT;
		Message invalidMsg1 = new Message(initHeader, null);
		initHeader = null;
		
		assertFalse(controller.isValidGameMsg(invalidMsg1));
		assertFalse(controller.isValidGameMsg(null));
		
		initHeader = Header.INIT;
		Point invalidPoint1 = new Point(10, 10);
		Message invalidMsg2 = new Message(initHeader, invalidPoint1);
		assertFalse(controller.isValidGameMsg(invalidMsg2));
		
		int[][] validData1 = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, s, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w}
		};
		Field validInitField1 = new Field(validData1);
		FieldInMessage validInitFieldInBody1 = new FieldInMessage(validInitField1);
		Header strokeHeader = Header.STROKE;
		Message invalidMsg3 = new Message(strokeHeader, validInitFieldInBody1);
		assertFalse(controller.isValidGameMsg(invalidMsg3));
		
		Point validPoint = new Point();
		Message valideMsg1 = new Message(strokeHeader, validPoint);
		assertTrue(controller.isValidGameMsg(valideMsg1));
		
		controller.close();
	}
	
	@Test
	public void testIsClient1StrokeAndCliend2WaitStroke() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		
		assertTrue(spyController.isClient1StrokeAndCliend2WaitStroke());
		
		when(spyController.getClient1()).thenReturn(waitStrokeClient);
		when(spyController.getClient2()).thenReturn(strokeClient);
		assertFalse(spyController.isClient1StrokeAndCliend2WaitStroke());

		ClientData waitClient = new ClientData(ClientState.WAIT);
		when(spyController.getClient1()).thenReturn(waitStrokeClient);
		when(spyController.getClient2()).thenReturn(waitClient);
		try {
			spyController.isClient1StrokeAndCliend2WaitStroke();
			fail("not catching exception.");
		} catch (RuntimeException e) {
			// ignore
		}
		controller.close();
	}
	
	@Test
	public void testReactionOnBigBang() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Point point = new Point();
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		Message validStrokeMsg = ServerMessages.getNotStrokeMsg(point);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		try {
			spyController.reactionOnBigBang(point);
		} catch (IOException e1) {
			fail("catch exception.");
		}
		try {
			verify(notActiveConnector, times(1)).send(validStrokeMsg);
		} catch (IOException e) {
			fail("catch exception.");
		}
		try {
			verify(activeConnector, times(1)).send(ServerMessages.getBigBang());
		} catch (IOException e) {
			fail("catch exception.");
		}
		assertFalse(spyController.isEndGame());
		// ===== test situation with socket exception.
		try {
			doThrow(new SocketException()).when(notActiveConnector).send(validStrokeMsg);
		} catch (IOException e) {
			fail("catch exception");
		}
		try {
			spyController.reactionOnBigBang(point);
		} catch (IOException e1) {
			fail("catch exception.");
		}
		try {
			verify(activeConnector, times(1)).send(ServerMessages.getTKOWin());
		} catch (IOException e) {
			fail("catch exception.");
		}
		assertTrue(spyController.isEndGame());
		controller.close();
	}
	
	@Test
	public void testReactionOnWinStroke() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		Point point = new Point();
		try {
			spyController.reactionOnWinStroke(point);
		} catch (IOException e) {
			fail("catch exception");
		}
		try {
			verify(activeConnector, times(1)).send(ServerMessages.getWin());
			verify(notActiveConnector, times(1)).send(ServerMessages.getLoose(point));
		} catch (IOException e) {
			fail("catch exception");
		}
		assertTrue(spyController.isEndGame());
		
		controller.close();
	}
	
	@Test
	public void testGameLoop() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		// ===== invalid message.
		try {
			when(activeConnector.recieve()).thenReturn(null);
		} catch (ClassNotFoundException e1) {
			fail("catch exception");
		} catch (ClassCastException e1) {
			fail("catch exception");
		} catch (IOException e1) {
			fail("catch exception");
		}
		try {
			spyController.gameLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		assertTrue(spyController.isEndGame());
		try {
			verify(activeConnector, times(1)).send(ServerMessages.getTKOLoose());
			verify(notActiveConnector, times(1)).send(ServerMessages.getTKOWin());
		} catch (IOException e) {
			fail("catch exception");
		}
		// ===== tko loose message.
		spyController.setEndGame(false);
		Message tkoLooseMsg = new Message(Header.TKO_LOOSE, null);
		try {
			when(activeConnector.recieve()).thenReturn(tkoLooseMsg);
		} catch (ClassNotFoundException e1) {
			fail("catch exception");
		} catch (ClassCastException e1) {
			fail("catch exception");
		} catch (IOException e1) {
			fail("catch exception");
		}
		try {
			spyController.gameLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		assertTrue(spyController.isEndGame());
		try {
			verify(activeConnector, times(2)).send(ServerMessages.getTKOLoose());
			verify(notActiveConnector, times(2)).send(ServerMessages.getTKOWin());
		} catch (IOException e) {
			fail("catch exception");
		}
		// ===== miss stroke.
		spyController.setEndGame(false);
		Point point = new Point();
		Message strokeMsg = new Message(Header.STROKE, point);
		try {
			when(activeConnector.recieve()).thenReturn(strokeMsg);
		} catch (ClassNotFoundException e1) {
			fail("catch exception");
		} catch (ClassCastException e1) {
			fail("catch exception");
		} catch (IOException e1) {
			fail("catch exception");
		}
		try {
			spyController.gameLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		verify(spyController, times(1)).switchActiveAndNotActiveClient();
		assertFalse(spyController.isEndGame());
		
		controller.close();
	}
	
	@Test
	public void testReactionOnStrike() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		Point point = new Point(0, 0);
		Message notStrokeMsg = ServerMessages.getNotStroke(point);
		Message strakeMsg = ServerMessages.getStrike();
		// ===== valid case.
		try {
			spyController.reactionOnStrike(point);
		} catch (IOException e) {
			fail("catch exception.");
		}
		try {
			verify(notActiveConnector, times(1)).send(notStrokeMsg);
		} catch (IOException e) {
			fail("catch exception.");
		}
		try {
			verify(activeConnector, times(1)).send(strakeMsg);
		} catch (IOException e) {
			fail("catch exception");
		}
		int markedCell = 
			waitStrokeClient.getField().getCell(point.getX(), point.getY());
		assertTrue(markedCell == TypeCell.STRIKE.ordinal());
		// ===== socket exception case.
		try {
			doThrow(new SocketException()).when(notActiveConnector).send(notStrokeMsg);
		} catch (IOException e) {
			fail("catch exception");
		}
		try {
			spyController.reactionOnStrike(point);
		} catch (IOException e) {
			fail("catch exception.");
		}
		try {
			verify(notActiveConnector, times(2)).send(notStrokeMsg);
		} catch (IOException e) {
			fail("catch exception.");
		}
		try {
			verify(activeConnector, times(1)).send(ServerMessages.getTKOWin());
		} catch (IOException e) {
			fail("catch exception");
		}
		assertTrue(spyController.isEndGame());
		controller.close();
	}
	
	@Test
	public void testReactionOnMiss() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		Point point = new Point(0, 0);
		// ==== valid msg.
		try {
			spyController.reactionOnMiss(point);
			verify(notActiveConnector, times(1)).send(ServerMessages.getStrokeMsg(point));
			verify(activeConnector, times(1)).send(ServerMessages.getMiss());
			verify(spyController, times(1)).switchActiveAndNotActiveClient();
		} catch (IOException e) {
			fail("catch exception");
		}
		// ==== socket exception
		try {
			doThrow(new SocketException()).when(notActiveConnector).send(ServerMessages.getStrokeMsg(point));
			spyController.reactionOnMiss(point);
			verify(activeConnector, times(1)).send(ServerMessages.getTKOWin());
			assertTrue(spyController.isEndGame());
		} catch (IOException e) {
			fail("catch exception");
		}
		
		controller.close();
	}
	
	@Test
	public void testReactionOnStroke() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		// ==== win stroke
		Point winningPoint = new Point(0, 0);
		int[][] validDataWin1 = new int[][] {
				new int[] {s, t, t, t, w, t, t, t, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, t, t, w, t, t, w, t, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, t, w, w, w, w, t, w, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, t}
		};
		Field winField = new Field(validDataWin1);
		waitStrokeClient.setField(winField);
		try {
			spyController.reactionOnStroke(winningPoint);
			verify(spyController, times(1)).reactionOnWinStroke(winningPoint);
		} catch (IOException e) {
			fail("catch exception");
		}
		// ==== big bang stroke.
		Point bigBangPoint = new Point(0, 0);
		int[][] validBigBang = new int[][] {
				new int[] {s, t, t, t, w, t, t, t, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, t, t, w, t, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, t, w, w, w, w, t, w, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, t}
		};
		Field bigBangField = new Field(validBigBang);
		waitStrokeClient.setField(bigBangField);
		try {
			spyController.reactionOnStroke(bigBangPoint);
			verify(spyController, times(1)).reactionOnBigBang(bigBangPoint);
		} catch (IOException e) {
			fail("catch exception");
		}
		// ==== strike stroke.
		Point strikePoint = new Point(0, 0);
		int[][] validStrikeData = new int[][] {
				new int[] {s, s, t, t, w, t, t, t, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, t, t, w, t, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, t, w, w, w, w, t, w, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, t}
		};
		Field strikeField = new Field(validStrikeData);
		waitStrokeClient.setField(strikeField);
		try {
			spyController.reactionOnStroke(strikePoint);
			verify(spyController, times(1)).reactionOnStrike(strikePoint);
		} catch (IOException e) {
			fail("catch exception");
		}		
		controller.close();
		// ==== miss stroke.
		Point missPoint = new Point(1, 0);
		int[][] validMissData = new int[][] {
				new int[] {s, s, t, t, w, t, t, t, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, t, t, w, t, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, t, w, w, w, w, t, w, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, t}
		};
		Field missField = new Field(validMissData);
		waitStrokeClient.setField(missField);
		try {
			spyController.reactionOnStroke(missPoint);
			verify(spyController, times(1)).reactionOnMiss(missPoint);
		} catch (IOException e) {
			fail("catch exception");
		}		
		controller.close();
	}
	
	@Test
	public void testClientLoop() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		// ==== valid init.
		ServerSocket serverSocket = mock(ServerSocket.class);
		int[][] validData1 = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, s, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w}
		};
		Field validInitField = new Field(validData1);
		FieldInMessage validInitFieldInMsg = new FieldInMessage(validInitField);
		Message initMsg = new Message(Header.INIT, validInitFieldInMsg);
		Connector grabbedConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		try {
			when(serverSocket.accept()).thenReturn(new Socket("127.0.0.1", 9000));
		} catch (IOException e) {
			fail("catch exception");
		}
		try {
			when(grabbedConnector.recieve()).thenReturn(initMsg);
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (ClassCastException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		when(spyController.getGrabbedConnector()).thenReturn(grabbedConnector);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getServerSocket()).thenReturn(serverSocket);
		try {
			spyController.connectClientsLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		// ==== invalid init
		int[][] invalidData = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, s, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w}
		};
		Field invalidInitField = new Field(invalidData);
		FieldInMessage invalidInitFieldInMsg = new FieldInMessage(invalidInitField);
		Message invalidInitMsg = new Message(Header.INIT, invalidInitFieldInMsg);
		try {
			when(grabbedConnector.recieve()).thenReturn(invalidInitMsg);
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (ClassCastException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		try {
			spyController.connectClientsLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		try {
			verify(grabbedConnector, times(1)).send(ServerMessages.getBadInitMsg());
		} catch (IOException e) {
			fail("catch exception");
		}
		// ==== connect client1
		ClientData createdClient = new ClientData(ClientState.NOT_CONNECT, validInitField);
		when(spyController.getClient1()).thenReturn(null);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		try {
			when(grabbedConnector.recieve()).thenReturn(initMsg);
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (ClassCastException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		try {
			spyController.connectClientsLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		assertTrue(spyController.getConnector1() == grabbedConnector);
		// times equals 2 because, setClient1 calls in validInit.
		verify(spyController, times(2)).setClient1(createdClient);
		// ==== connect client2
		when(spyController.getClient1()).thenReturn(waitStrokeClient);
		when(spyController.getClient2()).thenReturn(null);
		try {
			when(grabbedConnector.recieve()).thenReturn(initMsg);
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (ClassCastException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		try {
			spyController.connectClientsLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		assertTrue(spyController.getConnector2() == grabbedConnector);
		verify(spyController, times(1)).setClient2(createdClient);
		
		controller.close();
	}
	
	@Test
	public void testActionsAndSettingsBeforeStartGame() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient2()).thenReturn(strokeClient);
		when(spyController.getClient1()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		// ==== test. switch active, and not active clients.
		try {
			spyController.actionsAndSettingsBeforeStartGame();
			assertFalse(spyController.isEndGame());
			assertTrue(spyController.getClient1().getState().equals(ClientState.STROKE));
			assertTrue(spyController.getClient2().getState().equals(ClientState.WAIT_STROKE));
			verify(activeConnector, times(1)).send(ServerMessages.getStrokeMsg());
			verify(notActiveConnector, times(1)).send(ServerMessages.getNotStrokeMsg());
		} catch (IOException e) {
			fail("catch exception");
		}
		controller.close();
	}
	
	@Test
	public void testSwitchActiveAndNotActiveClient() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);

		when(spyController.getClient2()).thenReturn(strokeClient);
		when(spyController.getClient1()).thenReturn(waitStrokeClient);
		// ==== switch client1 to stroke.
		spyController.switchActiveAndNotActiveClient();
		assertTrue(strokeClient.getState().equals(ClientState.WAIT_STROKE));
		assertTrue(waitStrokeClient.getState().equals(ClientState.STROKE));
		// ==== switch client2 to stroke.
		strokeClient.setState(ClientState.STROKE);
		waitStrokeClient.setState(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		spyController.switchActiveAndNotActiveClient();
		assertTrue(strokeClient.getState().equals(ClientState.WAIT_STROKE));
		assertTrue(waitStrokeClient.getState().equals(ClientState.STROKE));
		// ==== throw invalid data.
		ClientData waitClient1 = new ClientData(ClientState.WAIT_STROKE);
		ClientData waitClient2 = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient2()).thenReturn(waitClient1);
		when(spyController.getClient1()).thenReturn(waitClient2);
		try {
			spyController.switchActiveAndNotActiveClient();
			fail("catch exception");
		} catch (RuntimeException e) {
			// do nothing
		}
		
		controller.close();
	}
	
	@Test
	public void testEndGame() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		spyController.endGame();
		verify(spyController, times(1)).setClient1(null);
		verify(spyController, times(1)).setClient2(null);
		verify(spyController, times(1)).setConnector1(null);
		
		controller.close();
	}
	
	@Test
	public void testStartGame() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(strokeClient);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getNotActiveConnector()).thenReturn(notActiveConnector);
		when(spyController.getActiveConnector()).thenReturn(activeConnector);
		
		int numCallSetEndGameTrue = 0;
		int numCallGameLoop = 0;
		// ==== loop IOException case
		try {
			doThrow(new IOException()).when(spyController).gameLoop();
		} catch (IOException e) {
			fail("catch exception");
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		}
		spyController.startGame();
		verify(spyController, times(++numCallSetEndGameTrue)).setEndGame(true);
		try {
			verify(spyController, times(++numCallGameLoop)).gameLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		// ==== loop ClassNotFoundException
		try {
			doThrow(new ClassNotFoundException()).when(spyController).gameLoop();
		} catch (IOException e) {
			fail("catch exception");
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		}
		spyController.startGame();
		verify(spyController, times(++numCallSetEndGameTrue)).setEndGame(true);
		try {
			verify(spyController, times(++numCallGameLoop)).gameLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		
		// ==== settings IOException case
		try {
			doThrow(new IOException()).when(spyController).actionsAndSettingsBeforeStartGame();
		} catch (IOException e) {
			fail("catch exception");
		}
		spyController.startGame();
		verify(spyController, times(++numCallSetEndGameTrue)).setEndGame(true);
		try {
			verify(spyController, times(numCallGameLoop)).gameLoop();
		} catch (ClassNotFoundException e) {
			fail("catch exception");
		} catch (IOException e) {
			fail("catch exception");
		}
		
		controller.close();
	}
	
	@Test
	public void testIsClientCollected() {
		ServerController controller = new ServerController();
		ServerController spyController = spy(controller);
		Connector notActiveConnector = mock(Connector.class);
		Connector activeConnector = mock(Connector.class);
		ClientData strokeClient = new ClientData(ClientState.STROKE);
		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
		when(spyController.getClient1()).thenReturn(null);
		when(spyController.getClient2()).thenReturn(waitStrokeClient);
		when(spyController.getConnector1()).thenReturn(notActiveConnector);
		when(spyController.getConnector2()).thenReturn(activeConnector);
		
		// ==== 
		assertFalse(spyController.isClientCollected());
		// ====
		when(spyController.getClient1()).thenReturn(strokeClient);
		assertTrue(spyController.isClientCollected());
		
		controller.close();
	}
	
//	@Test
//	@Ignore
//	public void testConnectClients() {
//		ServerController controller = new ServerController();
//		ServerController spyController = spy(controller);
//		Connector notActiveConnector = mock(Connector.class);
//		Connector activeConnector = mock(Connector.class);
//		ClientData waitStrokeClient = new ClientData(ClientState.WAIT_STROKE);
//		when(spyController.getClient1()).thenReturn(null);
//		when(spyController.getClient2()).thenReturn(waitStrokeClient);
//		when(spyController.getConnector1()).thenReturn(notActiveConnector);
//		when(spyController.getConnector2()).thenReturn(activeConnector);
//		doThrow(new RuntimeException()).when(spyController).sleepUnderErr();
//		spyController.connectClients();
//		// ==== IOException case
//		try {
//			doThrow(new IOException()).when(spyController).connectClientsLoop();
//		} catch (ClassNotFoundException e) {
//			fail("catch exception");
//		} catch (IOException e) {
//			fail("catch exception");
//		}
//		try {
//			spyController.connectClients();
//			fail("exception lose");
//		} catch (RuntimeException e) {
//			// do nothing
//		}
//		controller.close();
//	}
*/	
}
