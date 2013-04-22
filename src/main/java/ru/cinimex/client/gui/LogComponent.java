/**
 * @author Alykov Gali
 * @date 10.04.2013
 */
package ru.cinimex.client.gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogComponent extends JPanel {
	private static final long serialVersionUID = -5190813472048143104L;
	private JTextArea textArea;
	public LogComponent() {
		super();
		textArea = new JTextArea(6, 20);
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea);
		setBorder(BorderFactory.createTitledBorder("Info"));
		add(scroll);
	}
	
	public void println(String text) {
		String oldText = textArea.getText();
		textArea.setText(text + '\n' + oldText);
	}
	
	public void print(String text) {
		String oldText = textArea.getText();
		textArea.setText(text + oldText);
	}
	
	public void clean() {
		textArea.setText("");
	}
}
