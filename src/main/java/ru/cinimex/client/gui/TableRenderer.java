/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import ru.cinimex.data.Field;
import ru.cinimex.data.TypeCell;

public class TableRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -987456588483157087L;
	private Field field;
	private final Color COLOR_WATER = new Color(240, 245, 250);
	private final Color COLOR_MISS = new Color(190, 190, 190);
	private final Color COLOR_BIG_BANG = new Color(190, 100, 100);
	private final Color COLOR_SHIP = Color.BLACK;
	private final Color COLOR_STRAKE = Color.RED;
	
	public TableRenderer(Field field) {
		super();
		if (field == null) {
			throw new NullPointerException();
		}
		this.field = field;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (!(value instanceof Integer)) {
			throw new RuntimeException();
		}
		Integer intValue = field.getCell(row, column);
		if (intValue.equals(TypeCell.WATER.ordinal())) {
			setForeground(COLOR_WATER);
			setBackground(COLOR_WATER);
		} else if (intValue.equals(TypeCell.MISS.ordinal())) {
			setForeground(COLOR_MISS);
			setBackground(COLOR_MISS);			
		} else if (intValue.equals(TypeCell.SHIP.ordinal())) {
			setForeground(COLOR_SHIP);
			setBackground(COLOR_SHIP);			
		} else if (intValue.equals(TypeCell.STRIKE.ordinal())) {
			setForeground(COLOR_STRAKE);
			setBackground(COLOR_STRAKE);
		} else if (intValue.equals(TypeCell.BIG_BANG.ordinal())) {
			setForeground(COLOR_BIG_BANG);
			setBackground(COLOR_BIG_BANG);
		}
		
		return this;
	}	
	 
}
