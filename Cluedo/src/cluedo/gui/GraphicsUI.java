package cluedo.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import cluedo.Main;
import cluedo.game.Board;
import cluedo.game.Character;
import cluedo.game.Player;
import cluedo.game.Room;
import cluedo.game.Weapon;
import cluedo.game.Board.Direction;
import cluedo.game.Card;

public class GraphicsUI extends JFrame implements WindowListener, MouseListener {
	
	private static final long serialVersionUID = 1L;

	private GraphicsUI root = this;
	
	private JPanel outermostPanel;
	private GraphicsBoardDrawer gameBoard;
	private TableCardDrawer faceUps;
	private HandDrawer hand;
	private DiceLabel dice;
	
	private Board board;
	
	public GraphicsUI(Board b) {
		super("Cluedo, the Great Detective Game");
		
		board = b;
		board.setUI(this);

		setSize(500, 600);
		setPreferredSize(getSize());
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		
		setupPanel();
		getContentPane().add(outermostPanel);

		// tell frame to fire a WindowsListener event
		// but not to close when "x" button clicked.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		pack();
		setVisible(true);
		
		board.startGame();
		drawBoard();
		board.startTurn();
		drawBoard();
	}
	
	private void setupPanel() {
		outermostPanel = new JPanel();
		// standard swing layouts weren't giving me the result I wanted
		outermostPanel.setLayout(new BoxLayout(outermostPanel, BoxLayout.Y_AXIS));
		
		setupMenu();
		
		JPanel tablePanel = new JPanel();
		gameBoard = new GraphicsBoardDrawer(board, this);
		gameBoard.setAlignmentX(Component.LEFT_ALIGNMENT);
		tablePanel.add(gameBoard);
		faceUps = new TableCardDrawer(board.getFaceUpCards());
		faceUps.setAlignmentX(Component.LEFT_ALIGNMENT);
		tablePanel.add(faceUps);
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
		outermostPanel.add(tablePanel);
		
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.X_AXIS));
		
		JPanel turnPanel = new JPanel();
		turnPanel.setLayout(new BoxLayout(turnPanel, BoxLayout.Y_AXIS));
		dice = new DiceLabel(board);
		turnPanel.add(dice, "wrap 20");
		
		JButton accuseButton = new JButton("Make accusation");
		accuseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (board.gameOver()) return;	// no accusing after game is done
				
				int ans = JOptionPane.showConfirmDialog(
						root,
						"Are you sure you want to make an accusation?",
						"Accusation?",
						JOptionPane.YES_NO_OPTION
				);
				
				if (ans == JOptionPane.YES_OPTION) {
					root.accusation();
				}
			}
		});
		turnPanel.add(accuseButton, "wrap 10");
		
		JButton endTurnButton = new JButton("End Turn");
		endTurnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.endTurn();
				// game should not be able to end when this button is pressed
				hand.setVisible(false);
				drawBoard();
				notification("It is now "+board.getCurrentPlayer().name()+"'s turn.\nPlease hand the controls over to them.");
				hand.setVisible(true);
				drawBoard();
				root.drawBoard();
			}
		});
		turnPanel.add(endTurnButton);
		
		playerPanel.add(turnPanel);
		
		hand = new HandDrawer(board);
		playerPanel.add(hand);
		outermostPanel.add(playerPanel);
	}
	
	private void setupMenu() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setMaximumSize(new Dimension(this.getWidth(), 20));
		menuBar.setMinimumSize(new Dimension(this.getWidth(), 20));
		menuBar.setPreferredSize(new Dimension(this.getWidth(), 20));
		
		JMenu menu = new JMenu("Game");
		menu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(menu);
		
		JMenuItem restart = new JMenuItem("Start new game", KeyEvent.VK_N);
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = JOptionPane.showConfirmDialog(
						root,
						"Are you sure you want to restart the game?",
						"Restart?",
						JOptionPane.YES_NO_OPTION
				);
				if (ans == JOptionPane.YES_OPTION)
					root.startNewGame();
			}
		});
		restart.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menu.add(restart);
		
		JMenuItem endTurn = new JMenuItem("End turn", KeyEvent.VK_E);
		endTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.endTurn();
				// game should not be able to end when this item is pressed/activated
				hand.setVisible(false);
				drawBoard();
				notification("It is now "+board.getCurrentPlayer().name()+"'s turn.\nPlease hand the controls over to them.");
				hand.setVisible(true);
				drawBoard();
				root.drawBoard();
			}
		});
		endTurn.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		menu.add(endTurn);
		
		menu.addSeparator();
		
		JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_X);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.dispatchEvent(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
			}
		});
		exit.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		menu.add(exit);
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);
		
		JMenuItem movement = new JMenuItem("Movement Help", KeyEvent.VK_M);
		movement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "To move, click on a tile adjacent to your character."
						+ "\nYou may also click further away, as long as you click within the same row or column as your character."
						+ "\nWhen in a room, you may click on a secret passage to use it. To leave the room normally, just click on\na tile just outside one of the room's doors.";
				JOptionPane.showMessageDialog(root, message, "Movement Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpMenu.add(movement);
		
		outermostPanel.add(menuBar);
	}
	
	public HashMap<String, Object> askForUserInfo(Character[] options) {
		String name = null;
		
		while (name == null || name.isEmpty()) {
			// apparently I need to use a JTextField, so I will play it safe and assume a JOptionPane is not good enough
			//name = JOptionPane.showInputDialog(this, new JLabel("What is your name?"), "Input name", JOptionPane.QUESTION_MESSAGE);
			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
			namePanel.add(new JLabel("What is your name?"));
			JTextField nameField = new JTextField(30) {
			    public void addNotify() {
			        super.addNotify();
			        requestFocus();
			    }
			};
			namePanel.add(nameField);
			
			JOptionPane.showMessageDialog(this, namePanel, "Input your name", JOptionPane.PLAIN_MESSAGE);
			name = nameField.getText();
		}
		
		JPanel charSelect = new JPanel();
		charSelect.setLayout(new BoxLayout(charSelect, BoxLayout.Y_AXIS));
		charSelect.add(new JLabel(name+", please select a character"));
		
		ButtonGroup charButtons = new ButtonGroup();
		for (Character c : options) {
			JRadioButton button = new JRadioButton(c.toString());
			
			if (c.player() != null) {
				button.setEnabled(false);
			}
			
			charButtons.add(button);
			charSelect.add(button);
		}
		
		Character userChar = null;
		
		while (userChar == null) {
			JOptionPane.showMessageDialog(this, charSelect);
			for (Component c : charSelect.getComponents()) {
				if (c instanceof JRadioButton && ((JRadioButton)c).isSelected()) {
					String charName = ((JRadioButton)c).getText();
					for (Character ch : options) {
						if (ch.toString().equals(charName)) {
							userChar = ch;
						}
					}
				}
			}
		}
		
		HashMap<String, Object> choices = new HashMap<String, Object>();
		choices.put("name", name);
		choices.put("character", userChar);
		
		return choices;
	}
	
	public int getPlayerCount() {
		Integer ans = null;
		do {
			ans = (Integer)JOptionPane.showInputDialog(
				this,
				"How many people are playing?",
				"Number of players",
				JOptionPane.QUESTION_MESSAGE,
				null,
				new Integer[]{3,4,5,6},
				0
			);
		} while (ans == null);
		return ans;
	}
	
	public void drawBoard() {
		gameBoard.drawBoard();
		faceUps.drawBoard(this);
		hand.drawBoard(this);
		dice.redraw();
		revalidate();
		repaint();
	}
	
	public void hypothesise() {
		Room murderRoom = board.getCurrentPlayer().character().location();
		
		ArrayList<Character> chars = board.getCharacters();
		Character murderChar = null;
		do {
			murderChar = (Character) JOptionPane.showInputDialog(
					this,
					"Who do you think committed the murder?",
					"Murderer",
					JOptionPane.QUESTION_MESSAGE,
					null,
					chars.toArray(new Character[chars.size()]),
					chars.get(0)
			);
		} while (murderChar == null);
		
		if (murderChar.location() != null) {
			murderChar.location().removeCharacter(murderChar);
		} else {
			board.getPlayerGrid()[murderChar.getRow()][murderChar.getCol()] = 0;
		}
		murderChar.enterRoom(murderRoom);
		murderRoom.addCharacter(murderChar);
		
		drawBoard();
		
		ArrayList<Weapon> weapons = board.getWeapons();
		Weapon murderWeapon = null;
		do {
			murderWeapon = (Weapon) JOptionPane.showInputDialog(
					this,
					"Which weapon do you think was used for the murder?",
					"Murder weapon",
					JOptionPane.QUESTION_MESSAGE,
					null,
					weapons.toArray(new Weapon[weapons.size()]),
					weapons.get(0)
			);
		} while (murderWeapon == null);
		murderWeapon.location().removeWeapon(murderWeapon);
		murderWeapon.moveToRoom(murderRoom);
		murderRoom.addWeapon(murderWeapon);
		
		drawBoard();
		
		hand.setVisible(false);
		drawBoard();
		checkHypothesis(board.getHypoPlayers(), murderChar, murderWeapon, murderRoom);
		hand.setVisible(true);
		drawBoard();
	}
	
	private void checkHypothesis(ArrayList<Player> players, Character c, Weapon w, Room r) {
		for (Player p : players) {
			notification("Asking "+p.name()+" about their cards.\nPlease hand the controls over to them.");
			
			notification(p.name()+", you have been asked to dispute a hypothesis made by "+board.getCurrentPlayer().name()
					+"\nthat the murder was committed by "+c.name()+" in the "+r.name()
					+"\nusing the "+w.name()+".");
			
			int cards = JOptionPane.showConfirmDialog(
					this,
					"Would you like to look at your cards?",
					"Look at cards?",
					JOptionPane.YES_NO_OPTION
			);
			
			if (cards == JOptionPane.YES_OPTION) {
				JPanel handCards = new JPanel();
				handCards.setLayout(new BoxLayout(handCards, BoxLayout.Y_AXIS));
				handCards.add(new JLabel(p.name()+"'s hand:"));
				
				JPanel cardPanel = new JPanel();
				cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.X_AXIS));
				for (Card card : p.hand()) {
					CardLabel lbl = new CardLabel(card.icon(""), card);
					lbl.addMouseListener(this);
					handCards.add(lbl);
				}
				
				JOptionPane.showMessageDialog(
						this,
						handCards,
						p.name()+"'s hand",
						JOptionPane.PLAIN_MESSAGE
				);
			}
			
			while (true) {
				String [] options = new String[]{
						"Character ("+c.name()+")",
						"Weapon ("+w.name()+")",
						"Room ("+r.name()+")",
						"None"
				};
				int ans = JOptionPane.showOptionDialog(
						this,
						"Which of these would you like to dispute?",
						"Dispute",
						JOptionPane.CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[3]
				);
				
				if (ans == 0) {
					// disputing character
					for (Card card : p.hand()) {
						if (card.name().equals(c.name())) {
							notification(p.name()+" has disproven the hypothesis that the murder was"
									+"\ncommited by "+c.name()+" in the "+r.name()+" with the\n"
									+w.name()+" by showing that they are holding the "+card.name()+" card."
									+"(Please hand the controls back to "+board.getCurrentPlayer().name()+".)");
							return;
						}
					}
					notification("You cannot dispute a hypothesis unless you have"
								+"\none of the corresponding cards in your hand");
				} else if (ans == 1) {
					// disputing weapon
					for (Card card : p.hand()) {
						if (card.name().equals(w.name())) {
							notification(p.name()+" has disproven the hypothesis that the murder was"
									+"\ncommited by "+c.name()+" in the "+r.name()+" with the\n"
									+w.name()+" by showing that they are holding the "+card.name()+" card."
									+"(Please hand the controls back to "+board.getCurrentPlayer().name()+".)");
							return;
						}
					}
					notification("You cannot dispute a hypothesis unless you have"
								+"\none of the corresponding cards in your hand");
				} else if (ans == 2) {
					// disputing room
					for (Card card : p.hand()) {
						if (card.name().equals(r.name())) {
							notification(p.name()+" has disproven the hypothesis that the murder was"
									+"\ncommited by "+c.name()+" in the "+r.name()+" with the\n"
									+w.name()+" by showing that they are holding the "+card.name()+" card."
									+"(Please hand the controls back to "+board.getCurrentPlayer().name()+".)");
							return;
						}
					}
					notification("You cannot dispute a hypothesis unless you have"
								+"\none of the corresponding cards in your hand");
				} else {
					// claiming they cannot dispute or closed dialog
					boolean found = false;
					for (Card card : p.hand()) {
						if (card.name().equals(r.name())
								|| card.name().equals(c.name())
								|| card.name().equals(w.name())) {
							notification("If you have one of the cards corresponding to a"
									+"\nhypothesis in your hand, you *must* dispute the"
									+"\nhypothesis.");
							found = true;
							break;
						}
					}
					if (found) continue;	// ask again
					else break;		// move on to the next person
				}
			}
		}
	}
	
	public void accusation() {
		ArrayList<Room> rooms = board.getRooms();
		Room murderRoom = null;
		do {
			murderRoom = (Room) JOptionPane.showInputDialog(
					this,
					"Where do you think the murder was committed?",
					"Murder location",
					JOptionPane.QUESTION_MESSAGE,
					null,
					rooms.toArray(new Room[rooms.size()]),
					rooms.get(0)
			);
		} while (murderRoom == null);
		
		ArrayList<Character> chars = board.getCharacters();
		Character murderChar = null;
		do {
			murderChar = (Character) JOptionPane.showInputDialog(
					this,
					"Who do you think committed the murder?",
					"Murderer",
					JOptionPane.QUESTION_MESSAGE,
					null,
					chars.toArray(new Character[chars.size()]),
					chars.get(0)
			);
		} while (murderChar == null);
		
		ArrayList<Weapon> weapons = board.getWeapons();
		Weapon murderWeapon = null;
		do {
			murderWeapon = (Weapon) JOptionPane.showInputDialog(
					this,
					"Which weapon do you think was used for the murder?",
					"Murder weapon",
					JOptionPane.QUESTION_MESSAGE,
					null,
					weapons.toArray(new Weapon[weapons.size()]),
					weapons.get(0)
			);
		} while (murderWeapon == null);
		
		notification("You have accused "+murderChar.name()+" of committing"
				+"\nthe murder using the "+murderWeapon.name()+" in the\n"
				+murderRoom.name()+".\n"
				+"Please make sure that nobody else is watching the screen"
				+"\nas the solution is revealed to you.");
		
		ArrayList<Card> envelope = board.getEnvelope();
		// character, then weapon, then room
		boolean incorrectScenario = false;
		
		String message = "You accused "+murderChar.name()+" of committing"
						+"\nthe murder. Your answer was ";
		if (envelope.get(0).name().equals(murderChar.name())) {
			message += "correct.";
		} else {
			message += "incorrect.";
			incorrectScenario = true;
		}
		notification(message);
		
		message = "You said that the murder was committed by"
				+"\nusing the "+murderWeapon.name()+". Your"
				+"\n answer was ";
		if (envelope.get(1).name().equals(murderWeapon.name())) {
			message += "correct.";
		} else {
			message += "incorrect.";
			incorrectScenario = true;
		}
		notification(message);
		
		message = "You said that the murder was committed in"
				+"\nthe "+murderRoom.name()+". Your answer was\n";
		if (envelope.get(2).name().equals(murderRoom.name())) {
			message += "correct.";
		} else {
			message += "incorrect.";
			incorrectScenario = true;
		}
		notification(message);
		
		if (incorrectScenario) {
			// kill the player and continue the game
			board.getCurrentPlayer().die();
			hand.setVisible(false);
			drawBoard();
			message = board.getCurrentPlayer().name()+" has made an incorrect accusation,\nand has therefore been eliminated.";
			board.endTurn();
			if (!board.gameOver()) {
				notification(message);
				hand.setVisible(true);
				drawBoard();
			}
		} else {
			// the player wins and the game stops
			board.win();
		}
	}
	
	public void notification(String message) {
		JOptionPane.showMessageDialog(this, message, "", JOptionPane.PLAIN_MESSAGE);
	}
	
	public void startNewGame() {
		// TODO: finish this
		Main.restartGame();
		/*removeAll();
		revalidate();
		repaint();
		
		board = new Board();
		board.setUI(this);
		
		setupPanel();
		getContentPane().add(outermostPanel);
		
		pack();
		
		board.startGame();
		drawBoard();
		board.startTurn();
		drawBoard();*/
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int ans = JOptionPane.showConfirmDialog(this,
				new JLabel("Are you sure you want to close Cluedo?"),
				"Close Cluedo?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (ans == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof CardLabel) {
			CardLabel lbl = (CardLabel) e.getSource();
			ImageIcon largeIcon = lbl.getCard().icon("_large");
			JOptionPane.showMessageDialog(this, "", lbl.getCard().name(), JOptionPane.INFORMATION_MESSAGE, largeIcon);
		} else if (e.getSource() instanceof JLabel) {
			if (board.gameOver()) return;	// no moving after game is done
			
			JLabel lbl = (JLabel) e.getSource();
			Point p = gameBoard.findLabel(lbl);
			if (p != null && board.getRemainingMoves() > 0) {	// should never be null here, but good to be safe
				Character c = board.getCurrentPlayer().character();
				
				move(c.getRow(), c.getCol(), p.y, p.x);
			}
		}
		
	}
	
	public void move(int fromRow, int fromCol, int toRow, int toCol) {
		Character c = board.getCurrentPlayer().character();
		int[][] grid = board.getGrid();
		int[][] characters = board.getPlayerGrid();
		
		if (grid[toRow][toCol] >= 11) {
			// trying to take a secret passageway
			Room passageRoom = board.getRoomForPassage(toRow, toCol);
			if (c.location().equals(passageRoom)) {
				Room endRoom = passageRoom.connection();
				passageRoom.removeCharacter(c);
				c.enterRoom(endRoom);
				endRoom.addCharacter(c);
				board.setRemainingMoves(0);
				drawBoard();
				hypothesise();
			}
		} else if (c.location() != null) {
			// trying to exit a room
			if (characters[toRow][toCol] == 0 && grid[toRow][toCol] == 1) {
				if (c.location().exitToPoint(c, toRow, toCol)) {
					characters[toRow][toCol] = c.toInt()+1;
					c.location().removeCharacter(c);
					c.leaveRoom();
					c.setRow(toRow);
					c.setCol(toCol);
					board.decrementMoves();
					drawBoard();
				}
			}
		} else if (fromRow == toRow && fromCol != toCol) {
			// user clicked in the same row as their character
			
			// first, check whether there is a room in the way
			int[][] boardGrid = board.getGrid();
			int[][] playerGrid = board.getPlayerGrid();
			boolean failed = false;
			for (int i = Math.min(fromCol, toCol); i <= Math.abs(fromCol-toCol); i++) {
				if (boardGrid[toRow][i] != 1 || (playerGrid[toRow][i] != 0 && playerGrid[toRow][i] != c.toInt()+1)) {
					failed = true;
					break;
				}
			}
			
			if (!failed) {
				Board.Direction dir = (fromCol-toCol > 0)? Direction.LEFT : Direction.RIGHT;
				for (int i = 0; i < Math.abs(fromCol-toCol); i++) {
					if (board.move(c, dir, board.origin())) {
						board.decrementMoves();
						if (c.location() != null) {
							board.setRemainingMoves(0);
							drawBoard();
							hypothesise();
						}
						if (board.getRemainingMoves() == 0) break;
					}
				}
				drawBoard();
			}
		} else if (fromCol == toCol && fromRow != toRow) {
			// user clicked in the same column as their character
			
			// first, check whether there is a room in the way
			int[][] boardGrid = board.getGrid();
			int[][] playerGrid = board.getPlayerGrid();
			boolean failed = false;
			for (int i = Math.min(fromRow, toRow); i <= Math.abs(fromRow-toRow); i++) {
				if (boardGrid[i][toCol] != 1 || (playerGrid[i][toCol] != 0 && playerGrid[i][toCol] != c.toInt()+1)) {
					failed = true;
					break;
				}
			}
			
			if (!failed) {
				Board.Direction dir = (fromRow-toRow > 0)? Direction.UP : Direction.DOWN;
				for (int i = 0; i < Math.abs(fromRow-toRow); i++) {
					if (board.move(c, dir, board.origin())) {
						board.decrementMoves();
						if (c.location() != null) {
							board.setRemainingMoves(0);
							drawBoard();
							hypothesise();
						}
						if (board.getRemainingMoves() == 0) break;
					}
				}
				drawBoard();
			}
		}
	}

	/* unneeded methods */
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
