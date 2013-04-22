/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client.gui;

import javax.swing.JCheckBox;
import javax.swing.JTable;

public abstract class Table extends JTable {
	private static final long serialVersionUID = -2696777139620798189L;
	
	@SuppressWarnings("serial")
	public Table(TableModel tableModel, int width, int height) {
		super(tableModel);
		for (int i = 0; i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(width);
			getColumnModel().getColumn(i).setCellEditor(new CellEditor(new JCheckBox()) {
				@Override
				protected void onclickAction() {
					onclickToCell(row, column);
				}				
			});
		}
		setCellSelectionEnabled(true);
		setDefaultRenderer(Integer.class, new TableRenderer(tableModel.getField()));
		setRowHeight(height);
	}
	
	protected abstract void onclickToCell(int row, int column);
}
