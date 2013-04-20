/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client.view;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.TypeCell;

public abstract class Panel extends JPanel {
	private static final long serialVersionUID = -4987709731476037735L;
	private TableModel model;
	private Table table;
	
	@SuppressWarnings("serial")
	public Panel(String panelName) {
		super();
		this.model = new TableModel();
		this.table = new Table(model, 20, 20) {
			@Override
			protected void onclickToCell(int row, int column) {
				onclickCell(row, column);
			}			
		};
		setBorder(BorderFactory.createTitledBorder(panelName));
		add(table);
	}
	
	public Panel(String panelName, ClientData clientData) {
		this(panelName);
		this.model = new TableModel(clientData);
	}

	public Field getField() {
		Field field = new Field();
		int[][] elements = new int[field.HEIGHT][field.WIDTH];
		for (int i = 0; i < field.HEIGHT; i++) {
			for (int j = 0; j < field.WIDTH; j++) {
				elements[i][j] = (Integer)table.getValueAt(i, j);
			}
		}		
		field.setField(elements);
		return field;
	}
	
	public void cleanField() {
		Field field = new Field();
		for (int i = 0; i < field.WIDTH; i++) {
			for (int j = 0; j < field.HEIGHT; j++) {
				setCell(i, j, TypeCell.WATER);
			}
		}
	}
	
	public void setField(int[][] field) {
		Field temp = new Field();
		for (int i = 0; i < temp.WIDTH; i++) {
			for (int j = 0; j < temp.HEIGHT; j++) {
				setCell(i, j, field[i][j]);
			}
		}
	}
	
	public ClientState getState() {
		return model.getState();
	}
	
	public void setState(ClientState state) {
		model.setState(state);
	}
	
	public void setCell(int row, int column, TypeCell type) {
		table.getModel().setValueAt(type.ordinal(), row, column);
		table.repaint();
	}
	
	public void setCell(int row, int column, int type) {
		table.getModel().setValueAt(type, row, column);
	}
	
	public int getCell(int row, int column) {
		Object cell = table.getModel().getValueAt(row, column);
		return (Integer) cell;
	}
	
	public int getId() {
		return model.getId();
	}
	
	public void update() {
		repaint();
	}
	
	protected abstract void onclickCell(int row, int column);
	
}
