/**
 * @author Alykov Gali
 * @date 23.04.2013
 */
package ru.cinimex.test;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.cinimex.data.Header;
import ru.cinimex.data.Message;
import ru.cinimex.data.Point;
import static ru.cinimex.server.ServerMessages.*;

public class TestServerMessages {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetInitMsg() {
		Message msg = getInitMsg();
		assertNotNull(msg);
		assertTrue(msg.getHeader().equals(Header.INIT));
		assertNull(msg.getBody());
	}
	
	@Test
	public void testGetBadInitMsg() {
		Message msg = getBadInitMsg();
		assertNotNull(msg);
		assertTrue(msg.getHeader().equals(Header.BAD_INIT));
		assertNull(msg.getBody());
	}
	
	@Test
	public void testGetStrokeMsg() {
		Message msg1 = getStrokeMsg();
		assertNotNull(msg1);
		assertTrue(msg1.getHeader().equals(Header.STROKE));
		assertNull(msg1.getBody());
		
		Point point = new Point();
		Message msg2 = getStrokeMsg(point);
		try {
			getStrokeMsg(null);
			fail("null test fail");
		} catch(NullPointerException e) {
			// not tracing
		}
		assertNotNull(msg2);
		assertTrue(msg2.getHeader().equals(Header.STROKE));
		assertNotNull(msg2.getBody());
	}
	
	@Test
	public void testGetNotStrokeMsg() {
		Message msg1 = getNotStrokeMsg();
		assertNotNull(msg1);
		assertTrue(msg1.getHeader().equals(Header.NOT_STROKE));
		assertNull(msg1.getBody());
		
		Point point = new Point();
		Message msg2 = getNotStroke(point);
		try {
			getNotStroke(null);
			fail("null test fail");
		} catch (NullPointerException e) {
			// not tracing
		}
		assertNotNull(msg2);
		assertTrue(msg2.getHeader().equals(Header.NOT_STROKE));
		assertNotNull(msg2.getBody());
	}
	
}
