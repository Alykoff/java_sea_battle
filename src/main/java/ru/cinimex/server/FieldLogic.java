/**
 * @author Alykov Gali
 * @date 16.04.2013
 */
package ru.cinimex.server;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ru.cinimex.data.Field;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.data.UnvalidCellException;


public class FieldLogic {
	private static final int NUM_OF_SHIP_CELL = 20;
	private static int notCountedOneCellShips = 4;
	private static int notCountedTwoCellShips = 3;
	private static int notCountedThreeCellShips = 2;
	private static int notCountedFourCellShips = 1;
	private static ArrayList<Point2D.Float> shipsCells = 
												new ArrayList<Point2D.Float>(NUM_OF_SHIP_CELL);
	
	public static boolean isValidInitField(Field field) {
		for (int i = 0; i < Field.WIDTH; i++) {
			for (int j = 0; j < Field.HEIGHT; j++) {
				if ((field.getCell(i, j) != TypeCell.SHIP.ordinal()) && 
						(field.getCell(i, j) != TypeCell.WATER.ordinal())) {
					return false;
				}
			}
		}
		return validateForType(TypeCell.SHIP, field);
	}
	
	public static boolean isWinningStroke(Field field, Point point) {
		if (point == null || field == null) {
			throw new NullPointerException();
		}
		int x = point.getX();
		int y = point.getY();
		Field newField = field.clone();
		newField.makeStroke(x, y);
		return validateForType(TypeCell.STRIKE, newField);
	}
	
	public static boolean isStrike(Field field, Point point) {
		if (point == null || field == null) {
			throw new NullPointerException();
		}
		int x = point.getX();
		int y = point.getY();
		if (field.getCell(x, y) == TypeCell.SHIP.ordinal()) {
			return true;
		}
		return false;
	}
	
	public static boolean validateStroke(Point point) {
		if (point == null) {
			throw new NullPointerException();
		}
		int x = point.getX();
		int y = point.getY();
		return validateStroke(x, y);
	}
	
	public static boolean validateStroke(int x, int y) {
		if (x < 0 || 
				x >= Field.WIDTH || 
				y < 0 || 
				y >= Field.HEIGHT) {
			return false;
		}
		return true;
	}
	
	public static boolean detectBigBang(Field field, Point point) {
		if (field == null || point == null) {
			throw new NullPointerException();
		}
		int x = point.getX();
		int y = point.getY();
		if (field.getCell(x, y) != TypeCell.SHIP.ordinal()) {
			return false;
		}
		Field newField = field.clone();
		newField.setCell(x, y, TypeCell.STRIKE);
		ArrayList<Point2D.Float> acc = new ArrayList<Point2D.Float>();
		return detectBigBankHelper(x, y, newField, acc);
	}
	
	private static boolean detectBigBankHelper(int x, int y, Field field, 
						ArrayList<Point2D.Float> acc) {
		if (field.getCell(x, y) == TypeCell.SHIP.ordinal()) {
			return false;
		}
		if (acc.contains(new Point2D.Float(x, y)) ||
				field.getCell(x, y) != TypeCell.STRIKE.ordinal()) {
			return true;
		}
		acc.add(new Point2D.Float(x, y));
		boolean flagNotShip = true;
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i < 0 || 
						i >= Field.WIDTH || 
						j < 0 || 
						j >= Field.HEIGHT || 
						(i == x && j == y)) {
					continue;
				}
				flagNotShip = flagNotShip && detectBigBankHelper(i, j, field, acc);
			}
		}
		return flagNotShip;
	}
	
	private static synchronized boolean validateForType(TypeCell type, Field field) {
		setDefault();
		for (int i = 0; i < Field.WIDTH; i++) {
			for (int j = 0; j < Field.HEIGHT; j++) {
				try {
					checkCell(i, j, type, field);
				} catch (UnvalidCellException e) {
					return false;
				}
			}
		}
		
		if (notCountedOneCellShips != 0 || 
				notCountedTwoCellShips != 0 || 
				notCountedThreeCellShips != 0 || 
				notCountedFourCellShips != 0) {
			return false;
		}
		return true;
	}
		
	private static void checkCell(int i, int j, TypeCell type, Field field) throws UnvalidCellException {
		if (shipsCells.contains(new Point2D.Float(i, j))) {
			return;
		}
		ArrayList<Point2D.Float> acc = new ArrayList<Point2D.Float>();
		ArrayList<Point2D.Float> pointsOfShip;
		pointsOfShip = grabShip(i, j, type, field, acc);

		if (pointsOfShip.size() == 0) {
			return;
		} else if (pointsOfShip.size() == 1) {
			notCountedOneCellShips--;
			shipsCells.addAll(pointsOfShip);
		} else if (pointsOfShip.size() == 2) {
			notCountedTwoCellShips--;
			shipsCells.addAll(pointsOfShip);
		} else if (pointsOfShip.size() == 3) {
			notCountedThreeCellShips--;
			shipsCells.addAll(pointsOfShip);
		} else if (pointsOfShip.size() == 4) {
			notCountedFourCellShips--;
			shipsCells.addAll(pointsOfShip);
		} else {
			throw new UnvalidCellException();
		}
	}
	
	private static void setDefault() {
		notCountedOneCellShips = 4;
		notCountedTwoCellShips = 3;
		notCountedThreeCellShips = 2;
		notCountedFourCellShips = 1;
		shipsCells = new ArrayList<Point2D.Float>(NUM_OF_SHIP_CELL);
	}
	
	private static boolean isValidCell(int i, int j, Field field, TypeCell type) {
//		if (field.getCell(i, j) != type.ordinal()) {
//			return true;
//		}
		boolean isNotValidCell = (
				((i - 1) >= 0) && 
				((j - 1) >= 0) && 
				(field.getCell(i - 1, j - 1) == type.ordinal())
			) || (
				((i - 1) >= 0) && 
				((j + 1) < Field.HEIGHT) && 
				(field.getCell(i - 1, j + 1) == type.ordinal())
			) || (
				((i + 1) < Field.WIDTH) && 
				((j - 1) >= 0) && 
				(field.getCell(i + 1, j - 1) == type.ordinal())
			) || (
				((i + 1) < Field.WIDTH) && 
				((j + 1) < Field.HEIGHT) && 
				(field.getCell(i + 1, j + 1) == type.ordinal())
			) || (
				((i - 1) >= 0) &&
				(field.getCell(i - 1, j) == type.ordinal()) &&
				((j - 1) >= 0) &&
				(field.getCell(i, j - 1) == type.ordinal())
			) || (
				((j - 1) >= 0) &&
				(field.getCell(i, j - 1) == type.ordinal()) &&
				((i + 1) < Field.WIDTH) &&
				(field.getCell(i + 1, j) == type.ordinal())
			) || (
				((i - 1) >= 0) &&
				(field.getCell(i - 1, j) == type.ordinal()) &&
				((j + 1) < Field.HEIGHT) &&
				(field.getCell(i, j + 1) == type.ordinal())					
			) || (
				((j + 1) < Field.HEIGHT) &&
				(field.getCell(i, j + 1) == type.ordinal()) &&
				((i + 1) < Field.WIDTH) &&
				(field.getCell(i + 1, j) == type.ordinal())
			);
		
		return !isNotValidCell;
	}
	
	private static ArrayList<Point2D.Float> grabShip(int x, int y, TypeCell type,
					Field field, ArrayList<Point2D.Float> acc) throws UnvalidCellException {
		if (field.getCell(x, y) != type.ordinal()) {
			return acc;
		}
		for (Point2D point : acc) {
			if (x == point.getX() && y == point.getY()) {
				return acc;
			}
		}
		if (!isValidCell(x, y, field, type)) {
			throw new UnvalidCellException();
		}
		acc.add(new Point2D.Float(x, y));
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				int xx = x + i;
				int yy = y + j;
				if ((xx == 0 && yy == 0) ||
						xx < 0 ||
						xx >= Field.WIDTH ||
						yy < 0 ||
						yy >= Field.HEIGHT) {
					continue;
				}
				grabShip(xx, yy, type, field, acc);
			}
		}
		return acc;
	}
	
}
