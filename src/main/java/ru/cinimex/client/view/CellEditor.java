/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.client.view;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public abstract class CellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = -427021415087014931L;
	private JButton button;
	protected int row;
	protected int column;
	
	public CellEditor(JCheckBox checkBox) {
		 super(checkBox);
		 button = new JButton();
	     button.setOpaque(false);
	     button.setVisible(false);
	     button.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent paramMouseEvent) {
	    		 onclickAction();
	    		 fireEditingStopped();				
			}
			
			public void mousePressed(MouseEvent paramMouseEvent) {
	    		 onclickAction();
	    		 fireEditingStopped();				
			}
			
			public void mouseClicked(MouseEvent paramMouseEvent) {
	    		 onclickAction();
	    		 fireEditingStopped();			
			}
			public void mouseExited(MouseEvent paramMouseEvent) {}			
			public void mouseEntered(MouseEvent paramMouseEvent) {}
		});
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, 
			 boolean isSelected, int row, int column) {
		this.row = row;
		this.column = column;
		return button;
	}
	
	protected abstract void onclickAction();	 
}
