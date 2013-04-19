/**
 * @author Alykov Gali
 * @date 16.04.2013
 */
package ru.cinimex.test;

import ru.cinimex.data.Field;
import ru.cinimex.data.LogicForField;

public class TestFieldValidator {
	public static void main(String[] args) {
		int[][] f = new int[][] {
				new int[] {3, 3, 3, 3, 0, 3, 3, 3, 0, 0},
				new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				new int[] {3, 3, 3, 0, 3, 3, 0, 3, 3, 0},
				new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				new int[] {3, 3, 0, 0, 0, 0, 3, 0, 3, 0},
				new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				new int[] {3, 0, 3, 0, 0, 0, 0, 0, 0, 0},
				new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};
		Field field = new Field();
		field.setField(f);
//		System.out.println(field.isBigBang(0, 3));
		System.out.println(LogicForField.isValidInitField(field));
	}
}
