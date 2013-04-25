/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.data;

public enum TypeCell {
	WATER,
	MISS,
	STRIKE,
	SHIP,
	BIG_BANG;
	
	public static TypeCell getType(int n) {
		if (n == WATER.ordinal()) {
			return WATER;
		} else if (n == MISS.ordinal()) {
			return MISS;
		} else if (n == STRIKE.ordinal()) {
			return STRIKE;
		} else if (n == SHIP.ordinal()) {
			return SHIP;
		} else if (n == BIG_BANG.ordinal()) {
			return BIG_BANG;
		} else {
			throw new IllegalArgumentException("Not found type.");
		}
	}
}
