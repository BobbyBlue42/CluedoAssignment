package cluedo.ui;

import cluedo.game.Board;
import cluedo.game.Player;
import cluedo.game.Room;

public class TextBoardDrawer {
	public static void drawBoard(Board b) {
		int[][] board = b.getGrid();
		int[][] players = b.getPlayerGrid();
		char[][] names = b.getRoomNameGrid();
		
		Player p = b.getCurrentPlayer();
		
		if (p != null) {
			System.out.println("Current player: "+p.name()+" ("+p.character().name()+")\n");
		}
		
		for (int row = 0; row < board.length; row++) {
			int charCount = 0;	// for room names
			
			for (int col = 0; col < board[row].length; col++) {
				// print start of board
				if (col == 0) {
					if (board[row][col] != 0) {
						System.out.print("|");
						if (players[row][col] != 0) {
							System.out.print(players[row][col]);	// 1-6, so only one character
						} else if (row == board.length-1) {
							System.out.print("_");
						} else if (board[row][col] == 12) {
							System.out.print("‡");
						} else if (row < board.length-1 && board[row+1][col] == 0) {
							System.out.print("_");
						} else {
							System.out.print(" ");
						}
						charCount+=2;
					} else {
						System.out.print(" ");
						if (row < board.length-1 && board[row+1][col] != 0) {
							System.out.print("_");
						} else {
							System.out.print(" ");
						}
						charCount+=2;
					}
					if (board[row][col] == 0 && board[row][col+1] != 0
							|| board[row][col] == 1) {
						System.out.print("|");
					} else {
						System.out.print(" ");
					}
					charCount++;
				} else {
					if (board[row][col] == 0) {									// Empty
						if (row < board.length-1 && board[row+1][col] != 0) {
							System.out.print("_");
						} else {
							System.out.print(" ");
						}
						if (col < board[row].length-1 &&  board[row][col+1] != 0) {
							System.out.print("|");
						} else {
							System.out.print(" ");
						}
						charCount+=2;
					} else if (board[row][col] == 1) {							// Corridors
						if (players[row][col] != 0) {
							System.out.print(players[row][col]);
						} else {
							if (row < board.length-1 && board[row+1][col] > 1) {
								// room below
								Room r = b.getRoomByCode(board[row+1][col]);
								if (r.canEnter(row, col, row+1, col)) {
									System.out.print(" ");
								} else {
									System.out.print("_");
								}
							} else {
								System.out.print("_");
							}
						}
						if (col < board[row].length-1 && board[row][col+1] > 1) {
							// room on right
							Room r = b.getRoomByCode(board[row][col+1]);
							if (r.canEnter(row, col, row, col+1)) {
								System.out.print(" ");
							} else {
								System.out.print("|");
							}
						} else {
							System.out.print("|");
						}
						charCount+=2;
					} else if (board[row][col] == 11) {							// NW-SE Stairs
						System.out.print("†");
						if (col < board[row].length-1 && board[row][col+1] > 1) {
							System.out.print(" ");
						} else {
							System.out.print("|");
						}
						charCount+=2;
					} else if (board[row][col] == 12) {							// NE-SW Stairs
						System.out.print("‡");
						if (col < board[row].length-1 && board[row][col+1] > 1) {
							System.out.print(" ");
						} else {
							System.out.print("|");
						}
						charCount+=2;
					} else {													// Room
						Room r = b.getRoomByCode(board[row][col]);
						if (names[row][charCount] > 0) {
							System.out.print(names[row][charCount]);
						} else if (r.hasChar(row, charCount)) {
							System.out.print(r.getChar(row, charCount));
						} else if (row < board.length-1 && board[row+1][col] <= 1) {
							if (r.canEnter(row+1, col, row, col)) {
								System.out.print(" ");
							} else {
								System.out.print("_");
							}
						} else if (row == board.length-1) {
							System.out.print("_");
						} else {
							System.out.print(" ");
						}
						charCount++;
						
						if (names[row][charCount] > 0) {
							System.out.print(names[row][charCount]);
						} else if (r.hasChar(row, charCount)) {
							System.out.print(r.getChar(row, charCount));
						} else if (col < board[row].length-1 && board[row][col+1] <= 1) {
							if (r.canEnter(row, col+1, row, col)) {
								System.out.print(" ");
							} else {
								System.out.print("|");
							}
						} else if (col == board[row].length-1) {
							System.out.print("|");
						} else {
							System.out.print(" ");
						}
						charCount++;
					}
				}
				
			}
			
			switch (row) {
			case 6:
				System.out.print("      Key:");
				break;
			case 7:
				System.out.print("       1 = Miss Scarlett");
				break;
			case 8:
				System.out.print("       2 = Professor Plum");
				break;
			case 9:
				System.out.print("       3 = Mrs. Peacock");
				break;
			case 10:
				System.out.print("       4 = Reverend Green");
				break;
			case 11:
				System.out.print("       5 = Mrs. White");
				break;
			case 12:
				System.out.print("       6 = Colonel Mustard");
				break;
			case 14:
				System.out.print("       C = Candlestick");
				break;
			case 15:
				System.out.print("       D = Dagger");
				break;
			case 16:
				System.out.print("       P = Lead Pipe");
				break;
			case 17:
				System.out.print("       G = Revolver");
				break;
			case 18:
				System.out.print("       R = Rope");
				break;
			case 19:
				System.out.print("       S = Spanner");
				break;
			}
			
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		// for testing purposes
		
		Board b = new Board();
		TextBoardDrawer.drawBoard(b);
	}
}
