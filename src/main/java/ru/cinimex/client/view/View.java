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
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import ru.cinimex.client.controller.ClientController;
import ru.cinimex.connector.BodyMessage;
import ru.cinimex.connector.FieldInBody;
import ru.cinimex.connector.Header;
import ru.cinimex.connector.Message;
import ru.cinimex.connector.PointInBody;
import ru.cinimex.data.ClientData;
import ru.cinimex.data.ClientState;
import ru.cinimex.data.Field;
import ru.cinimex.data.FieldLogic;
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
	protected boolean interrupt = false;
	protected ClientData data;
	protected PointInBody lastStroke;

	protected LogComponent log;
	protected Panel panelTable;
	protected Panel panelOpponentTable;
	protected JButton startButton;
	protected JButton endButton;
	
	public static void main(String[] args) {
		new View();
	}
	
	@SuppressWarnings("serial")
	public View() {
		setLookAndFeel();
		setTitle(DEFAULT_TITLE);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		hideFavicone();
		log = new LogComponent();
		data = new ClientData(ClientState.NOT_CONNECT);
		controller = new ClientController();
		startButton = new JButton(TITLE_START_BUTTON);
		endButton = new JButton(TITLE_LOSE_BUTTON);
		
		panelTable = new Panel(TITLE_OUR_FIELD_PANEL) {
			@Override
			protected void onclickCell(int row, int column) {
				onclickToField(row, column);
			}
		};
		panelTable.setField(DEFAULT_FIELD);// TODO test.

		panelOpponentTable = new Panel(TITLE_OPPONENT_FIELD_PANEL) {
			private static final long serialVersionUID = -5972736539712850752L;
			@Override
			protected void onclickCell(int row, int column) {
				onclickToOpponentField(row, column);
			}
		};
		panelOpponentTable.setVisible(false);

		addComponents();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		locationInCenterMonitor(this);
		setResizable(false);
		setVisible(true);
	}
	
	protected void  hideFavicone() {
		final int IMAGE_WIDTH = 1;
		final int IMAGE_HEIGHT = 1;
		Image icon = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
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
		interrupt = false;
		try {
			new Thread(new Runnable() {
				public void run() {
					controller.connect(url, port);
					Message msg = new Message(
							Header.INIT, 
							new FieldInBody(panelTable.getField()));
					controller.send(msg);
					panelOpponentTable.setVisible(true);
					startButton.setEnabled(false);
					endButton.setEnabled(true);
					log.println("Starting game...\n");
					data.setState(ClientState.WAIT);
					processingGame();
				}
			}).start();
		} catch (IllegalArgumentException e) {
			log.println(e.getMessage());
			e.printStackTrace();
		} catch (RuntimeException e) {
			log.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	protected void processingGame() {
		while (!interrupt) {
			try {
				Message msg = controller.recieve();
				Header header = msg.getHeader();
				BodyMessage body = msg.getBody();
				if (header.equals(Header.BAD_INIT)) {
					reactionOnBadInit();
				} else if (header.equals(Header.BAD_STROKE)) {
					reactionOnBadInit();
				} else if (header.equals(Header.BIG_BANG) || header.equals(Header.STRIKE)) {
					reactionOnGoodShot(header, body);
				} else if (header.equals(Header.STROKE)) {
					reactionOnStroke(header, body);
				} else if (header.equals(Header.NOT_STROKE)) {
					reactionOnStrokeTabu(body);						
				} else if (header.equals(Header.TKO_WIN) || header.equals(Header.WIN)) {
					reactionOnWin(header);
				} else if (header.equals(Header.TKO_LOOSE) || header.equals(Header.LOOSE)) {
					reactionOnLoose(header);
				}
			} catch (RuntimeException e) {
				log.println(e.getMessage());
				interrupt = true;
				endGame();
			}
		}
	}
	
	protected void onclickLoosing() {
		controller.send(new Message(Header.TKO_LOOSE, null));
		controller.close();
		log.println("By your command of the fleet " +
				"retreats.\nThe battle was lost!\n");
		endGame();
	}
	
	protected void onclickToField(int row, int column) {
		if (!data.equals(ClientState.NOT_CONNECT)) {
			return;
		}
		int cellCode = panelTable.getField().getCell(row, column);
		if (cellCode == TypeCell.WATER.ordinal()) {
			panelTable.setCell(row, column, TypeCell.SHIP);
		} else if (cellCode == TypeCell.SHIP.ordinal()) {
			panelTable.setCell(row, column, TypeCell.WATER);
		}
		panelTable.update();
	}
	
	protected void onclickToOpponentField(int row, int column) {
		if (data.equals(ClientState.NOT_CONNECT) ||
				data.equals(ClientState.WAIT) ||
				data.equals(ClientState.WAIT_STROKE) ||
				panelOpponentTable.getCell(row, column) != TypeCell.WATER.ordinal()) {
			return;
		}
		if (lastStroke == null) {
			lastStroke = new PointInBody(row, column);
		}
	}
	
	protected void endGame() {
		log.println("Ending game...");
		data.setState(ClientState.NOT_CONNECT);
		panelOpponentTable.setVisible(false);
		panelOpponentTable.cleanField();
		panelTable.cleanField();
		startButton.setEnabled(true);
		endButton.setEnabled(false);
	}
	
	protected void reactionOnBadInit() {
		log.println("Bad init.");
		interrupt = true;
		endGame();
	}
	
	protected void reactionOnBadStroke() {
		log.println("bad stroke.");
		waitAndReactionToStroke();
	}
	
	protected void reactionOnStroke(Header header, BodyMessage body) {
		if (body != null || body instanceof PointInBody) {
			int x = ((PointInBody)body).getX();
			int y = ((PointInBody)body).getY();
			if (panelTable.getCell(x, y) == TypeCell.WATER.ordinal()) {
				panelTable.setCell(x, y, TypeCell.MISS);
			}
		} 
		log.println("You stroke.");
		waitAndReactionToStroke();
	}
	
	protected void reactionOnGoodShot(Header header, BodyMessage body) {
		if (lastStroke == null) {
			log.println("Upps! We have problem! " +
					"Sorry. End game(");
			endGame();
		}
		int x = lastStroke.getX();
		int y = lastStroke.getY();
		panelOpponentTable.setCell(x, y, TypeCell.STRAKE);
		lastStroke = null;
		if (header.equals(Header.BIG_BANG)) {
			paintPaddedShip(x, y, panelOpponentTable);
			log.println("Yeeh! You blew up this ship!");
		} else {
			log.println("Good shot!");								
		}
		waitAndReactionToStroke();
	}
		
	protected void reactionOnOfferToStroke(BodyMessage body) {
		log.println("Your turn began.");
		if (body != null && (body instanceof PointInBody)) {
			log.println("Your opponent missed!");	
			int x = ((PointInBody) body).getX();
			int y = ((PointInBody) body).getY();
			int cellXY = panelTable.getField().getCell(x, y);
			if (cellXY == TypeCell.WATER.ordinal() || 
					cellXY == TypeCell.MISS.ordinal()) {
				panelTable.setCell(x, y, TypeCell.MISS);
			}
		}							
		waitAndReactionToStroke();
	}
	
	protected void reactionOnStrokeTabu(BodyMessage body) {
		log.println("Please wait for the opponent's turn.");
		data.setState(ClientState.WAIT);
		if (body != null && (body instanceof PointInBody)) {
			log.println("Hit on our ship!");
			PointInBody point = (PointInBody) body;
			int x = ((PointInBody) body).getX();
			int y = ((PointInBody) body).getY();
			boolean isBigBang = FieldLogic.detectBigBang(panelTable.getField(), point);
			panelTable.setCell(x, y, TypeCell.STRAKE);
			if (isBigBang) {
				paintPaddedShip(x, y, panelTable);
			}
		} else if (lastStroke != null) {
			int x = lastStroke.getX();
			int y = lastStroke.getY();
			if (panelOpponentTable.getCell(x, y) == TypeCell.WATER.ordinal()) {
				panelOpponentTable.setCell(x, y, TypeCell.MISS);
			}
			lastStroke = null;
		}
	}
	
	protected void reactionOnWin(Header header) {
		if (header.equals(Header.TKO_WIN)) {
			log.println("Congratulations! You win! " +
					"Enemy fleet fled.");
		} else {
			log.println(
					"Congratulations! You win! You " +
					"have a terrific excerpt. Enemy " +
					"fleet defeated.");
		}
		interrupt = true;
		endGame();
	}
	
	protected void reactionOnLoose(Header header) {
		if (header.equals(Header.TKO_LOOSE)) {
			log.println("Oh! TKO lose. We lose!");
		} else {
			log.println("Oh! We lose!");
		}
		interrupt = true;
		endGame();
	}

	private void waitAndReactionToStroke() {
		data.setState(ClientState.STROKE);
		long beginTime = new Date().getTime();
		long maxTimeStroke = 30000L;
		lastStroke = null;
		while (lastStroke == null) {
			if ((beginTime + maxTimeStroke) < new Date().getTime()) {
				interrupt = true;
				log.println("Your time is over. You lose.");
				return;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		data.setState(ClientState.WAIT); 
		try {
			controller.send(
				new Message(Header.STROKE, lastStroke));
		} catch (RuntimeException e) {
			log.println(e.getMessage());
			interrupt = true;
		}
	}
	
	private void paintPaddedShip(int x, int y, Panel panel) {
		if (panel.getCell(x, y) == TypeCell.WATER.ordinal()) {
			panel.setCell(x, y, TypeCell.MISS);
		}
		if (panel.getCell(x, y) != TypeCell.STRAKE.ordinal()) {
			return;
		}
		panel.setCell(x, y, TypeCell.BIG_BANG);
		Field field = new Field();
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i < 0 || 
						i >= field.WIDTH || 
						j < 0 || 
						j >= field.HEIGHT) {
					continue;
				}
				paintPaddedShip(i, j, panel);
			}
		}
		
	}
}
