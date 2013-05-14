/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.client.gui;

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
import ru.cinimex.data.Field;
import ru.cinimex.data.Point;
import ru.cinimex.data.TypeCell;
import ru.cinimex.data.TypeField;

public abstract class View extends JFrame {
	private static final long serialVersionUID = 7308962792053719857L;
	public static final int DEFAULT_HEIGHT = 400;
	public static final int DEFAULT_WIDTH = 500;
	protected final String DEFAULT_TITLE = "Sea battle";	
	protected final String DEFAULT_PORT = "9000";
	protected final String DEFAULT_URL = "127.0.0.1";
	protected final String TITLE_START_BUTTON = "Start game";
	protected final String TITLE_LOSE_BUTTON = "Lose";
	protected final String TITLE_OUR_FIELD_PANEL = "Your field";
	protected final String TITLE_OPPONENT_FIELD_PANEL = "Opponent field";
	
	private static final int[][] DEFAULT_FIELD = new int[][] {
		new int[] {3, 3, 3, 3, 0, 3, 3, 3, 0, 0},
		new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		new int[] {3, 3, 3, 0, 3, 3, 0, 3, 3, 0},
		new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		new int[] {3, 3, 0, 0, 0, 0, 3, 0, 3, 0},
		new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		new int[] {3, 0, 3, 0, 0, 0, 0, 0, 0, 0},
		new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
	};
	private Point lastStroke;
//	protected final ClientController controller;
	protected final LogComponent log;
	private final Panel panelTable;
	private final Panel panelOpponentTable;
	private JButton startButton;
	private JButton endButton;
	
	@SuppressWarnings("serial")
	public View() {
		setLookAndFeel();
		setTitle(DEFAULT_TITLE);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		hideFavicone();
		log = new LogComponent();
//		controller = clientController;
		startButton = new JButton(TITLE_START_BUTTON);
		endButton = new JButton(TITLE_LOSE_BUTTON);
		
		panelTable = new Panel(TITLE_OUR_FIELD_PANEL) {
			@Override
			protected void onclickCell(Point point) {
				onclickToField(point);
			}
		};
		panelTable.setField(DEFAULT_FIELD);// TODO test.
		
		panelOpponentTable = new Panel(TITLE_OPPONENT_FIELD_PANEL) {
			private static final long serialVersionUID = -5972736539712850752L;
			@Override
			protected void onclickCell(Point point) {
				onclickToOpponentField(point);
			}
		};
		panelOpponentTable.setVisible(false);
		
		addComponents();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		locationInCenterMonitor(this);
		setResizable(false);
	}
	
	public void switchToStartGameMode() {
		panelOpponentTable.setVisible(true);
		startButton.setEnabled(false);
		endButton.setEnabled(true);
		log.println("Starting game...\n");
	}
	
	protected void  hideFavicone() {
		final int IMAGE_WIDTH = 1;
		final int IMAGE_HEIGHT = 1;
		Image icon = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, 
							BufferedImage.TYPE_INT_ARGB_PRE);
		setIconImage(icon);
	}
	
	protected void setLookAndFeel() {
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
		panel.add(panelTable);
		panel.add(panelOpponentTable);
		
		add(panel);
	}
	
	protected JPanel createManagePanel() {
		// fieldsPanel
		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new BorderLayout());
		fieldsPanel.setBorder(BorderFactory.createTitledBorder("Connect setting"));
		
		JPanel urlPanel = new JPanel();
		JLabel urlLabel = new JLabel("Url  ");
		final JTextField urlTextField = new JTextField(10);
		urlTextField.setText(DEFAULT_URL);
		urlPanel.add(urlLabel);
		urlPanel.add(urlTextField);
		
		JPanel portPanel = new JPanel();
		JLabel portLabel = new JLabel("Port");
		final JTextField portTextField = new JTextField(10);
		portTextField.setText(DEFAULT_PORT);
		portPanel.add(portLabel);
		portPanel.add(portTextField);
		
		fieldsPanel.add(urlPanel, BorderLayout.SOUTH);
		fieldsPanel.add(portPanel, BorderLayout.NORTH);
		// buttonPanel
		JPanel buttonPanel = new JPanel();
		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				onclickLoosing();
			}
		});
		endButton.setEnabled(false);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				onclickStart(urlTextField.getText(), portTextField.getText());
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
	
	protected void onclickToField(Point point) {
		if (panelOpponentTable.isVisible()) {
			return;
		}
		TypeCell cellCode = getCell(point, TypeField.OUR);
		if (cellCode.equals(TypeCell.WATER)) {
			setCell(point, TypeCell.SHIP, TypeField.OUR);
		} else if (cellCode.equals(TypeCell.SHIP)) {
			setCell(point, TypeCell.WATER, TypeField.OUR);
		}
	}
	
	protected void onclickToOpponentField(Point point) {// XXX write last stroke
		TypeCell cellCode = getCell(point, TypeField.OPPONENT);
		System.out.println(cellCode.toString());
		if (cellCode.equals(TypeCell.WATER) && lastStroke == null) {
			lastStroke = point;
		}
	}
	
	public void switchToEndGame() {
		log.println("Ending game...");
		panelOpponentTable.setVisible(false);
		panelOpponentTable.cleanField();
		panelTable.cleanField();
		startButton.setEnabled(true);
		endButton.setEnabled(false);
	}
	
	public Field getField(TypeField typeField) {
		if (typeField == null) {
			throw new NullPointerException();
		}
		if (typeField.equals(TypeField.OUR)) {
			return panelTable.getField();
		} else if (typeField.equals(TypeField.OPPONENT)) {
			return panelOpponentTable.getField();
		}
		throw new RuntimeException();
	}
	
	public TypeCell getCell(Point point, TypeField typeField) {
		if (point == null || typeField == null) {
			throw new NullPointerException();
		}
		if (typeField.equals(TypeField.OUR)) {
			int type = panelTable.getCell(point);
			return TypeCell.getType(type);
		} else if (typeField.equals(TypeField.OPPONENT)) {
			int type = panelOpponentTable.getCell(point);
			return TypeCell.getType(type);
		}
		throw new RuntimeException();
	}
	
	public void setCell(Point point, TypeCell type, TypeField typeField) {
		if (point == null || type == null || typeField == null) {
			throw new NullPointerException();
		}
		if (typeField.equals(TypeField.OUR)) {
			panelTable.setCell(point, type);
		} else if (typeField.equals(TypeField.OPPONENT)) {
			panelOpponentTable.setCell(point, type);
		} else {
			throw new RuntimeException();
		}
	}
	
	public Point getLastStroke() {
		return lastStroke;
	}
	
	public void cleanLastStroke() {
		lastStroke = null;
	}
	
	public void println(String text) {
		log.println(text);
	}
	
	protected abstract void onclickStart(final String url, final String port);
	protected abstract void onclickLoosing();
}
