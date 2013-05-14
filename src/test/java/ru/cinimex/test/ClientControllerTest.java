/**
 * @author Alykov Gali
 * @date 25.04.2013
 */
package ru.cinimex.test;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

import ru.cinimex.client.ClientController;
import ru.cinimex.client.gui.View;
import ru.cinimex.connector.Connector;

@Ignore
public class ClientControllerTest extends TestCase {

	ClientController controller;
	ClientController spyController;
	Connector mockConnector = mock(Connector.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		controller = new ClientController();
		spyController = spy(controller);
		mockConnector = mock(Connector.class);
		View mockView = spy(new View() {
			private static final long serialVersionUID = -8704363206343707533L;
			@Override
			protected void onclickStart(String url, String port) {
				
			}
			@Override
			protected void onclickLoosing() {
				
			}
		});
		when(spyController.getView()).thenReturn(mockView);
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
/*	@Test
	public void testProcessingGameBadInit() {
		when(spyController.getConnector()).thenReturn(mockConnector);
		when(spyController.recieve()).thenReturn(ServerMessages.getBadInitMsg());
		spyController.processingGame();
		verify(spyController, times(1)).reactionOnBadInit();
	}
	
	@Test
	public void testProcessingGameTKOWint() {
		when(spyController.getConnector()).thenReturn(mockConnector);
		when(spyController.recieve()).thenReturn(ServerMessages.getWin());
		spyController.processingGame();
		verify(spyController, times(1)).reactionOnWin(ServerMessages.getWin().getHeader());
		
	}
	
//	@Test
//	public void testProcessingGame
	
	@Ignore
	@Test
	public void ttestProcessingGamere() {
		ClientController controller = new ClientController();
		ClientController spyController = spy(controller);
		Connector connector = mock(Connector.class);
		when(spyController.getConnector()).thenReturn(connector);
//		View mockView = mock(View.class);
		View mockView = spy(new View(controller));
		when(spyController.getView()).thenReturn(mockView);
		// ==== bad init
		when(spyController.recieve()).thenReturn(ServerMessages.getBadInitMsg());
		spyController.processingGame();
		verify(spyController, times(1)).reactionOnBadInit();
		// ==== tko win
		spyController = spy(controller);
		when(spyController.getConnector()).thenReturn(connector);
		when(spyController.recieve()).thenReturn(ServerMessages.getWin());
		spyController.processingGame();
		verify(spyController, times(1)).reactionOnWin(ServerMessages.getWin().getHeader());
		// ==== win
		spyController = spy(controller);
		when(spyController.getConnector()).thenReturn(connector);
		when(spyController.recieve()).thenReturn(ServerMessages.getTKOWin());
		spyController.processingGame();
		verify(spyController, times(1)).reactionOnWin(ServerMessages.getTKOWin().getHeader());
		// ==== big bang
		spyController = spy(controller);
		when(mockView.getLastStroke()).thenReturn(new Point(0, 0));
		when(mockView.getCell(new Point(0, 0), TypeField.OPPONENT)).thenReturn(TypeCell.MISS);
		when(mockView.getCell(new Point(0, 0), TypeField.OUR)).thenReturn(TypeCell.MISS);
		when(spyController.getView()).thenReturn(mockView);
//		when(spyController.paintPaddedShip(null, null)).
		
		when(spyController.getConnector()).thenReturn(connector);
		when(spyController.recieve()).thenReturn(ServerMessages.getBigBang());
		spyController.processingGame();
		Header header = ServerMessages.getBigBang().getHeader();
		BodyMessage body = ServerMessages.getBigBang().getBody();
		verify(spyController, times(1)).reactionOnGoodShot(header, body);
		// ==== lose
		spyController = spy(controller);
		when(spyController.getConnector()).thenReturn(connector);
		when(spyController.recieve()).thenReturn(ServerMessages.getLoose(null));
		spyController.processingGame();
		verify(spyController, times(1)).reactionOnLoose(ServerMessages.getLoose(null).getHeader());
	}*/
	
}
