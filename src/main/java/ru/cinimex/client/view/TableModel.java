/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client.view;

import javax.swing.table.AbstractTableModel;

import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;

public class TableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4164115690980271575L;
	private ClientData data;
	private boolean editingFlag = true;
	
	public TableModel() {
		data = new ClientData(ClientState.NOT_CONNECT);
	}
	
	public TableModel(ClientData data) {
		if (data == null) {
			throw new NullPointerException();
		}
		this.data = data;		
	}
	
	public int getColumnCount() {
		return data.getField().WIDTH;
	}

	public int getRowCount() {
		return data.getField().HEIGHT;
	}
	
	public Object getValueAt(int numRow, int numColumn) {
		return getField().getCell(numRow, numColumn);
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		if (value instanceof Integer) {
			getField().setCell(row, column, (Integer) value);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
	    return "";
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getColumnClass(int columnIndex) {
		return Integer.class;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return editingFlag;
	}
	
	public int getId() {
		return data.getId();
	}
	
	public Field getField() {
		return data.getField();
	}
	
	public ClientState getState() {
		return data.getState();
	}
	
	public void setField(Field field) {
		data.setField(field);
	}
	
	public void setState(ClientState state) {
		data.setState(state);
	}
	
	public void setEditable(boolean flag) {
		editingFlag = flag;
	}
}
