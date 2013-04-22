/**
 * @author Alykov Gali
 * @date 22.04.2013
 */
package ru.cinimex.server;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static ru.cinimex.server.FieldLogic.*;
import ru.cinimex.data.Field;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;

public class FieldLogicTest extends TestCase {

	int s, w, t, b, m;
	
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
	public void testIsValidInitField() {

		Field validInitField1;
		Field validInitField2;
		Field invalidInitFieldWithLongShip;
		Field invalidInitFieldWithBadPositionShip;
		Field invalidInitFieldWithStrake1;
		Field invalidInitFieldWithStrake2;
		Field invalidInitFieldWithMiss;
		Field invalidInitFieldWithBigBag;
		
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
		validInitField1 = new Field(validData1);
		
		int[][] validData2 = new int[][] {
				new int[] {s, w, w, w, w, s, s, s, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, s, s, w, s, s, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, s, s, s},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, w, w, w, s}
		};
		validInitField2 = new Field(validData2);
		
		int[][] invalidDataWithLongShip = new int[][] {
				new int[] {s, w, w, w, w, s, s, s, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, s, s, w, s, s, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, s, s, s},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, w, w, w, s}
				
		};
		invalidInitFieldWithLongShip = new Field(invalidDataWithLongShip);
		
		int[][] invalidDataWithBadPositionShip = new int[][] {
				new int[] {s, w, w, w, w, s, s, s, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, s, s, w, s, s, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, s, s, s},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, w, w, w, s}
		};
		invalidInitFieldWithBadPositionShip = new Field(invalidDataWithBadPositionShip);
		
		int[][] invalidDataWithStrake1 = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, t, s}
		};
		invalidInitFieldWithStrake1 = new Field(invalidDataWithStrake1);
		
		int[][] invalidDataWithStrake2 = new int[][] {
				new int[] {t, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, s}
		};
		invalidInitFieldWithStrake2 = new Field(invalidDataWithStrake2);
		
		int[][] invalidDataWithMiss = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {m, w, w, w, w, w, w, w, w, s}
		};
		invalidInitFieldWithMiss = new Field(invalidDataWithMiss);
		
		int[][] invalidDataWithBigBang = new int[][] {
				new int[] {s, s, s, s, w, s, s, s, w, w},
				new int[] {t, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, s, w, s, s, w, s, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, s, w, w, w, w, s, w, s, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {s, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {b, w, w, w, w, w, w, w, w, s}
		};
		invalidInitFieldWithBigBag = new Field(invalidDataWithBigBang);
		
		assertTrue("validField1 faild", isValidInitField(validInitField1));
		assertTrue("validField2 faild", isValidInitField(validInitField2));
		assertFalse(isValidInitField(invalidInitFieldWithLongShip));
		assertFalse(isValidInitField(invalidInitFieldWithBadPositionShip));
		assertFalse("invalid field with strake1", isValidInitField(invalidInitFieldWithStrake1));
		assertFalse("invalid field with strake2", isValidInitField(invalidInitFieldWithStrake2));
		assertFalse("invalid field with miss", isValidInitField(invalidInitFieldWithMiss));
		assertFalse("invalid field with big bang", isValidInitField(invalidInitFieldWithBigBag));	
	}
	
	@Test
	public void testIsWiningStroke() {
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
		Point validPointWin1 = new Point(0, 0);
		Point invalidPointWin1 = new Point(1, 0);
		Field validWinField1 = new Field(validDataWin1);
		
		int[][] invalidWinDataWithShip1 = new int[][] {
				new int[] {s, t, t, t, w, t, t, t, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, t, t, w, t, t, w, t, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, t, w, w, w, w, t, w, t, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {t, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, w},
				new int[] {w, w, w, w, w, w, w, w, w, s}
				
		};
		Point invalidPointWinWithShip1 = new Point(9, 9);
		Field invalidWinFieldWithShip1 = new Field(invalidWinDataWithShip1);
		
		// valid field and ends point
		assertTrue("validField1 faild", isWinningStroke(validWinField1, validPointWin1));
		// valid field, not end point
		assertFalse("valide Field and unvalid point faild", isWinningStroke(validWinField1, invalidPointWin1));
		// invalid field, not end point
		assertFalse(isWinningStroke(invalidWinFieldWithShip1, invalidPointWinWithShip1));
		
		try {
			isWinningStroke(null, null);			
			fail("NullPointerException did't catch");
		} catch (NullPointerException e) {
			// not catching.
		}		
	}
	
	@Test
	public void isStrikeTest() {
		try {
			isStrike(null, null);
		} catch (NullPointerException e) {
			// not catching
		}
		int[][] data1 = new int[][] {
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
		Field field = new Field(data1);
		
		Point validStrike = new Point(0, 0);
		Point invalidStrike = new Point(1, 0);
		
		assertTrue(isStrike(field, validStrike));
		assertFalse(isStrike(field, invalidStrike));
	}
	
	
}
