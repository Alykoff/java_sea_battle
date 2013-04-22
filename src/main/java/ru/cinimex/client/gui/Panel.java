/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client.gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ru.cinimex.data.Field;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;

public abstract class Panel extends JPanel {
	private static final long serialVersionUID = -4987709731476037735L;
	private Table table;
	private final int HEIGHT_CELL = 20;
	private final int WIDTH_CELL = 20;
	
	@SuppressWarnings("serial")
	public Panel(String panelName) {
		super();
		TableModel model = new TableModel();
		this.table = new Table(model, WIDTH_CELL, HEIGHT_CELL) {
			@Override
			protected void onclickToCell(int row, int column) {
				Point point = new Point(row, column);
				onclickCell(point);
			}			
		};
		setBorder(BorderFactory.createTitledBorder(panelName));
		add(table);
	}
	
	public Field getField() {
		Field field = new Field();
		int[][] elements = new int[Field.HEIGHT][Field.WIDTH];
		for (int i = 0; i < Field.HEIGHT; i++) {
			for (int j = 0; j < Field.WIDTH; j++) {
				elements[i][j] = (Integer)table.getValueAt(i, j);
			}
		}		
		field.setField(elements);
		return field;
	}
	
	public void cleanField() {
		for (int i = 0; i < Field.WIDTH; i++) {
			for (int j = 0; j < Field.HEIGHT; j++) {
				setCell(i, j, TypeCell.WATER);
			}
		}
	}
	
	public void setField(int[][] field) {
		for (int i = 0; i < Field.WIDTH; i++) {
			for (int j = 0; j < Field.HEIGHT; j++) {
				setCell(i, j, field[i][j]);
			}
		}
	}
	
	public void setCell(int row, int column, TypeCell type) {
		table.getModel().setValueAt(type.ordinal(), row, column);
		table.repaint();
	}
	
	public void setCell(int row, int column, int type) {
		table.getModel().setValueAt(type, row, column);
	}
	
	public void setCell(Point point, TypeCell type) {
		if (point == null || type == null) {
			throw new NullPointerException();
		}
		int x = point.getX();
		int y = point.getY();
		table.getModel().setValueAt(type.ordinal(), x, y);
		table.repaint();
	}
	
	public int getCell(int row, int column) {
		Object cell = table.getModel().getValueAt(row, column);
		return (Integer) cell;
	}
	
	public int getCell(Point point) {
		if (point == null) {
			throw new NullPointerException();
		}
		int x = point.getX();
		int y = point.getY();
		Object cell = table.getModel().getValueAt(x, y);
		return (Integer) cell;
	}
	
	public void update() {
		repaint();
	}
	
	protected abstract void onclickCell(Point point);
	
}
