/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.client.view;

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
import ru.cinimex.client.controller.ClientController;
import ru.cinimex.connector.Header;
import ru.cinimex.connector.Message;
import ru.cinimex.connector.PointInBody;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.TypeCell;

public class View extends JFrame {
	private static final long serialVersionUID = 7308962792053719857L;
	public static final int DEFAULT_HEIGHT = 400;
	public static final int DEFAULT_WIDTH = 500;
	public static final String DEFAULT_TITLE = "Sea battle";	
	protected static final String DEFAULT_PORT = "9000";
	protected static final String DEFAULT_URL = "127.0.0.1";
	protected String TITLE_START_BUTTON = "Start game";
	protected String TITLE_LOSE_BUTTON = "Lose";
	protected String TITLE_OUR_FIELD_PANEL = "Your field";
	protected String TITLE_OPPONENT_FIELD_PANEL = "Opponent field";
	
	protected static final int[][] DEFAULT_FIELD = new int[][] {
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
	protected ClientController controller;
	protected PointInBody lastStroke;

	public final LogComponent log;
	protected Panel panelTable;
	protected Panel panelOpponentTable;
	protected JButton startButton;
	protected JButton endButton;
	
	@SuppressWarnings("serial")
	public View(ClientController clientController) {
		setLookAndFeel();
		setTitle(DEFAULT_TITLE);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		hideFavicone();
		log = new LogComponent();
		controller = clientController;
		startButton = new JButton(TITLE_START_BUTTON);
		endButton = new JButton(TITLE_LOSE_BUTTON);
		
		panelTable = new Panel(TITLE_OUR_FIELD_PANEL) {
			@Override
			protected void onclickCell(PointInBody point) {
				onclickToField(point);
			}
		};
		panelTable.setField(DEFAULT_FIELD);// TODO test.

		panelOpponentTable = new Panel(TITLE_OPPONENT_FIELD_PANEL) {
			private static final long serialVersionUID = -5972736539712850752L;
			@Override
			protected void onclickCell(PointInBody point) {
				onclickToOpponentField(point);
			}
		};
		panelOpponentTable.setVisible(false);

		addComponents();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		locationInCenterMonitor(this);
		setResizable(false);
		setVisible(true);
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
	
	protected void onclickStart(final String url, final String port) {
		controller.reactionOnStart(url, port);
	}
	
	protected void onclickLoosing() {
		controller.send(new Message(Header.TKO_LOOSE, null));
		controller.close();
		log.println("By your command of the fleet " +
				"retreats.\nThe battle was lost!\n");
		switchToEndGame();
	}
	
	protected void onclickToField(PointInBody point) {
//		if (!data.equals(ClientState.NOT_CONNECT)) {
//			return;
//		}
		int cellCode = getCell(point, TypeField.OUR);
		if (cellCode == TypeCell.WATER.ordinal()) {
			setCell(point, TypeCell.SHIP, TypeField.OUR);
		} else if (cellCode == TypeCell.SHIP.ordinal()) {
			setCell(point, TypeCell.WATER, TypeField.OUR);
		}
//		panelTable.update();
	}
	
	protected void onclickToOpponentField(PointInBody point) {
		if (lastStroke == null || point == null) {
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
	
	public int getCell(PointInBody point, TypeField typeField) {
		if (point == null || typeField == null) {
			throw new NullPointerException();
		}
		if (typeField.equals(TypeField.OUR)) {
			return panelTable.getCell(point);
		} else if (typeField.equals(TypeField.OPPONENT)) {
			return panelOpponentTable.getCell(point);
		}
		throw new RuntimeException();
	}
	
	public void setCell(PointInBody point, TypeCell type, TypeField typeField) {
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
	
	public PointInBody getLastStroke() {
		return lastStroke;
	}
	
	public void cleanLastStroke() {
		lastStroke = null;
	}
}
