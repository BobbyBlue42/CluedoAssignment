package cluedo.gui;

import java.awt.Point;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cluedo.game.Board;

public class GraphicsBoardDrawer extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static ImageIcon emptySquare = new ImageIcon(GraphicsBoardDrawer.class.getResource("img/empty.png"));
	private static ImageIcon floorSquare = new ImageIcon(GraphicsBoardDrawer.class.getResource("img/floor.png"));
	private static ImageIcon roomSquare = new ImageIcon(GraphicsBoardDrawer.class.getResource("img/room.png"));

	private static ImageIcon passageway_NE_SW_icon = new ImageIcon(GraphicsBoardDrawer.class.getResource("img/passageway_NE_SW.png"));
	private static ImageIcon passageway_NW_SE_icon = new ImageIcon(GraphicsBoardDrawer.class.getResource("img/passageway_NW_SE.png"));
	
	private Board b;
	
	private JLabel[][] boardLabels;
	
	public GraphicsBoardDrawer(Board b, GraphicsUI parent) {
		this.b = b;
		
		setSize(390, 420); // 26 rows, 24 cols, with 15x15 squares
		setMinimumSize(getSize());
		setPreferredSize(getSize());
		setMaximumSize(getSize());
		
		boardLabels = new JLabel[26][24];
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		setupBoard(parent);
	}
	
	public void setupBoard(MouseListener l) {
		int[][] board = b.getGrid();
		
		for (int i = 0; i < 26; i++) {
			JPanel row = new JPanel();
			row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
			for (int j = 0; j < 24; j++) {
				if (board[i][j] == 0) {
					boardLabels[i][j] = new JLabel(emptySquare);
					boardLabels[i][j].addMouseListener(l);
					row.add(boardLabels[i][j]);
				} else if (board[i][j] == 1) {
					boardLabels[i][j] = new JLabel(floorSquare);
					boardLabels[i][j].addMouseListener(l);
					row.add(boardLabels[i][j]);
				} else if (board[i][j] > 1 && board[i][j] < 11) {
					ImageIcon icon = b.getRoomByCode(board[i][j]).getIcon(i, j);
					if (icon != null && icon.getIconWidth() > 15) {
						boardLabels[i][j] = new JLabel(icon);
						boardLabels[i][j].addMouseListener(l);
						int length = b.getRoomByCode(board[i][j]).getNameWidth();
						row.add(boardLabels[i][j]);
						for (int count = 0; count < length-1; count++) {
							j++;
						}
					} else {
						boardLabels[i][j] = new JLabel(roomSquare);
						boardLabels[i][j].addMouseListener(l);
						row.add(boardLabels[i][j]);
					}
					
				} else if (board[i][j] == 11) {
					boardLabels[i][j] = new JLabel(passageway_NW_SE_icon);
					boardLabels[i][j].addMouseListener(l);
					row.add(boardLabels[i][j]);
				} else if (board[i][j] == 12) {
					boardLabels[i][j] = new JLabel(passageway_NE_SW_icon);
					boardLabels[i][j].addMouseListener(l);
					row.add(boardLabels[i][j]);
				}
			}
			add(row);
		}
	}
	
	public void drawBoard() {
		int[][] board = b.getGrid();
		int[][] players = b.getPlayerGrid();
		
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 24; j++) {
				if (board[i][j] == 0) {
					boardLabels[i][j].setIcon(emptySquare);
				} else if (board[i][j] == 1) {
					if (players[i][j] > 0) {
						boardLabels[i][j].setIcon(
								b.getCharacterByCode(players[i][j]).icon("_floor")
								);
					} else {
						boardLabels[i][j].setIcon(floorSquare);
					}
				} else if (board[i][j] > 1 && board[i][j] < 11) {
					ImageIcon icon = b.getRoomByCode(board[i][j]).getIcon(i, j);
					if (icon == null) {
						boardLabels[i][j].setIcon(roomSquare);
					} else {
						if (icon.getIconWidth() > 15) {
							// room name icon
							int nameWidth = b.getRoomByCode(board[i][j]).getNameWidth();
							for (int count = 0; count < nameWidth; count++) {
								j++;
							}
						} else {
							boardLabels[i][j].setIcon(icon);
						}
					}
					/*if (players[i][j] > 0) {
						boardLabels[i][j].setIcon(
								b.getCharacterByCode(players[i][j]).icon("_room")
								);
					} else {
						boardLabels[i][j].setIcon(roomSquare);
					}*/
				}
			}
		}
	}
	
	public Point findLabel(JLabel lbl) {
		for (int y = 0; y < 26; y++) {
			for (int x = 0; x < 24; x++) {
				if (lbl.equals(boardLabels[y][x]))
					return new Point(x,y);
			}
		}
		return null;
	}
}
