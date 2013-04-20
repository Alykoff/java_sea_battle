/**
 * @author Alykov Gali
 * @date 11.04.2013
 */
package ru.cinimex.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import ru.cinimex.client.view.View;
import ru.cinimex.client.view.LogComponent;

public class TestFrame extends JFrame {
	private static final long serialVersionUID = 6182293296261742100L;
	public static final int DEFAULT_HEIGHT = 400;
	public static final int DEFAULT_WIDTH = 500;
	public static final String DEFAULT_TITLE = "Sea battle";	
	protected LogComponent log;
	
	public TestFrame() {
		makePretty();
		log = new LogComponent();
		setTitle(DEFAULT_TITLE);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		// hide favicon.
		Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
		setIconImage(icon);
		//
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		locationInCenterMonitor(this);
		setResizable(false);
		addComponents();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new View();
	}
	
	protected void makePretty() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
	}
	
	protected void locationInCenterMonitor(Component component) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		component.setLocation(
			(screenWidth - component.getWidth()) / 2,
			(screenHeight - component.getHeight()) / 2
		);
	}

	protected void addComponents() {
		JPanel panel = new JPanel();
		
		JPanel managePanel = createManagePanel();
		panel.add(managePanel);
		panel.add(log);
		
		
		
		add(panel);
	}
	
//	protected JPanel createTablePane/
	
	protected JPanel createManagePanel() {
		// fieldsPanel
		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new BorderLayout());
		fieldsPanel.setBorder(BorderFactory.createTitledBorder("Connect setting"));
		
		JPanel urlPanel = new JPanel();
		JLabel urlLabel = new JLabel("Url  ");
		final JTextField urlTextField = new JTextField(10);
		urlPanel.add(urlLabel);
		urlPanel.add(urlTextField);
		
		JPanel portPanel = new JPanel();
		JLabel portLabel = new JLabel("Port");
		final JTextField portTextField = new JTextField(10);
		portPanel.add(portLabel);
		portPanel.add(portTextField);		
		
		fieldsPanel.add(urlPanel, BorderLayout.SOUTH);
		fieldsPanel.add(portPanel, BorderLayout.NORTH);
		// buttonPanel
		JPanel buttonPanel = new JPanel();
		JButton endButton = new JButton("Lossing");
		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				// TODO
			}
		});
		JButton startButton = new JButton("Start game");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				// TODO
			}
		});
		buttonPanel.add(startButton);
		buttonPanel.add(endButton);
		// managePanel
		JPanel managePanel = new JPanel();
		managePanel.setLayout(new BorderLayout());
		managePanel.add(fieldsPanel, BorderLayout.NORTH);
		managePanel.add(buttonPanel, BorderLayout.SOUTH);
		return managePanel;
	}
	
}
