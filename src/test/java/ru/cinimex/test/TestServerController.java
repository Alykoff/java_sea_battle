/**
 * @author Alykov Gali
 * @date 23.04.2013
 */
package ru.cinimex.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.SocketException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import ru.cinimex.connector.Connector;
import ru.cinimex.data.BodyMessage;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldInMessage;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.server.ServerController;
import ru.cinimex.server.ServerMessages;

public class TestServerController {
	int s, w, t, b, m;
		
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		s = TypeCell.SHIP.ordinal();
		w = TypeCell.WATER.ordinal();
		t = TypeCell.STRAKE.ordinal();
		b = TypeCell.BIG_BANG.ordinal();
		m = TypeCell.MISS.ordinal();
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

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
	public void testReactionOnStroke() {
		
	}
}
