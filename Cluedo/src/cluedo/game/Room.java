package cluedo.game;

import java.awt.Point;
import java.util.ArrayList;

import cluedo.ui.UI;

public class Room {
	public enum RoomName {
		KITCHEN,
		BALLROOM,
		CONSERVATORY,
		BILLIARD_ROOM,
		DINING_ROOM,
		LIBRARY,
		HALL,
		LOUNGE,
		STUDY;
		
		public String toString() {
			char[] name = this.name().replaceAll("_", " ").toCharArray();
			boolean startOfWord = true;
			
			for (int i = 0; i < name.length; i++) {
				if (startOfWord) {
					startOfWord = false;
				} else {
					if (name[i] == ' ') {
						startOfWord = true;
					} else {
						name[i] = java.lang.Character.toLowerCase(name[i]);
					}
				}
			}
			
			return new String(name);
		}
	}
	
	private RoomName name;
	private ArrayList<Character> characters;
	private ArrayList<Weapon> weapons;
	private Room connection;
	
	private int row, col;
	private int charRow, charCol;
	private int wepRow, wepCol;
	private int[][] layout;
	
	public Room(RoomName name) {
		this.name = name;
		characters = new ArrayList<Character>();
		weapons = new ArrayList<Weapon>();
		
		assignFields();
	}
	
	public RoomName name() {
		return name;
	}
	
	public void addCharacter(Character c) {
		characters.add(c);
	}
	
	public boolean containsCharacter(Character c) {
		return characters.contains(c);
	}
	
	public Character[] characters() {
		return (Character[]) characters.toArray();
	}
	
	public void addWeapon(Weapon w) {
		weapons.add(w);
	}
	
	public boolean containsWeapon(Weapon w) {
		return weapons.contains(w);
	}
	
	public Weapon[] weapons() {
		return (Weapon[]) weapons.toArray();
	}
	
	public void connectTo(Room other) {
		connection = other;
	}
	
	public boolean hasConnection() {
		return connection != null;
	}
	
	public Room connection() {
		return connection;
	}
	
	public boolean canEnter(int fromRow, int fromCol, int toRow, int toCol) {
		int fRow = fromRow - row;
		int fCol = fromCol - col;
		int tRow = toRow - row;
		int tCol = toCol - col;
		
		if (fRow != tRow) {
			if (layout[tRow][tCol] <= 1) {
				return false;
			} else if (layout[tRow][tCol] == UP && fRow == tRow-1) {
				return true;
			} else if (layout[tRow][tCol] == DOWN && fRow == tRow+1) {
				return true;
			}
		} else if (fCol != tCol) {
			if (layout[tRow][tCol] <= 1) {
				return false;
			} else if (layout[tRow][tCol] == LEFT && fCol == tCol-1) {
				return true;
			} else if (layout[tRow][tCol] == RIGHT && fCol == tCol+1) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean canLeave(Board.Direction d) {
		switch (d) {
		case UP:
			for (int i = 0; i < layout[0].length; i++)
				if (layout[0][i] == UP)
					return true;
			break;
		case RIGHT:
			for (int i = 0; i < layout.length; i++)
				if (layout[i][layout[i].length-1] == RIGHT)
					return true;
			break;
		case DOWN:
			for (int i = 0; i < layout[layout.length-1].length; i++)
				if (layout[layout.length-1][i] == DOWN)
					return true;
			break;
		case LEFT:
			for (int i = 0; i < layout.length; i++)
				if (layout[i][0] == LEFT)
					return true;
		}
		return false;
	}

	public Point getExitPoint(Board.Direction d, UI ui) {
		ArrayList<Point> exits = new ArrayList<Point>();
		switch (d) {
		case UP:
			for (int i = 0; i < layout[0].length; i++)
				if (layout[0][i] == UP)
					exits.add(new Point(row - 1, col + i));
			break;
		case RIGHT:
			for (int i = 0; i < layout.length; i++)
				if (layout[i][layout[i].length-1] == RIGHT)
					exits.add(new Point(row + i, col + layout[i].length));
			break;
		case DOWN:
			for (int i = 0; i < layout[layout.length-1].length; i++)
				if (layout[layout.length-1][i] == DOWN)
					exits.add(new Point(row + layout.length, col + i));
			break;
		case LEFT:
			for (int i = 0; i < layout.length; i++)
				if (layout[i][0] == LEFT)
					exits.add(new Point(row + i, col - 1));
		}
		
		if (exits.size() > 1) {
			String question = "There are multiple exits in this direction.\nWould you like to use the exit leading to:";
			for (int i = 0; i < exits.size(); i++) {
				question += "\n\t"+(i+1)+"\t("+exits.get(i).getX()+","+exits.get(i).getY()+")";
			}
			question += "?\n(Please enter the number of your answer.)";
			
			int ans = ui.askInt(question);
			
			return exits.get(ans-1);
		} else if (exits.size() == 1) {
			return exits.get(0);
		}
		
		return new Point(-1,-1);
	}
	
	public boolean hasChar(int row, int charCount) {
		if (row == charRow) {
			for (int i = 0; i < characters.size(); i++) {
				if (charCount == charCol+i) {
					return true;
				}
			}
		} else if (row == wepRow) {
			for (int i = 0; i < weapons.size(); i++) {
				if (charCount == wepCol+i) {
					return true;
				}
			}
		}
		return false;
	}
	
	public char getChar(int row, int charCount) {
		if (row == charRow) {
			for (int i = 0; i < characters.size(); i++) {
				if (charCount == charCol+i) {
					characters.get(i).name().toChar();
				}
			}
		} else if (row == wepRow) {
			for (int i = 0; i < weapons.size(); i++) {
				if (charCount == wepCol+i) {
					weapons.get(i).name().toChar();
				}
			}
		}
		return ' ';
	}
	
	
	private void assignFields() {
		if (name.equals(RoomName.BALLROOM)) {
			row = ballroomRow;
			col = ballroomCol;
			charRow = ballroomCharRow;
			charCol = ballroomCharCol;
			wepRow = ballroomWepRow;
			wepCol = ballroomWepCol;
			layout = ballroom;
		} else if (name.equals(RoomName.BILLIARD_ROOM)) {
			row = billiardRow;
			col = billiardCol;
			charRow = billiardCharRow;
			charCol = billiardCharCol;
			wepRow = billiardWepRow;
			wepCol = billiardWepCol;
			layout = billiard;
		} else if (name.equals(RoomName.CONSERVATORY)) {
			row = conservatoryRow;
			col = conservatoryCol;
			charRow = conservatoryCharRow;
			charCol = conservatoryCharCol;
			wepRow = conservatoryWepRow;
			wepCol = conservatoryWepCol;
			layout = conservatory;
		} else if (name.equals(RoomName.DINING_ROOM)) {
			row = diningRow;
			col = diningCol;
			charRow = diningCharRow;
			charCol = diningCharCol;
			wepRow = diningWepRow;
			wepCol = diningWepCol;
			layout = dining;
		} else if (name.equals(RoomName.HALL)) {
			row = hallRow;
			col = hallCol;
			charRow = hallCharRow;
			charCol = hallCharCol;
			wepRow = hallWepRow;
			wepCol = hallWepCol;
			layout = hall;
		} else if (name.equals(RoomName.KITCHEN)) {
			row = kitchenRow;
			col = kitchenCol;
			charRow = kitchenCharRow;
			charCol = kitchenCharCol;
			wepRow = kitchenWepRow;
			wepCol = kitchenWepCol;
			layout = kitchen;
		} else if (name.equals(RoomName.LIBRARY)) {
			row = libraryRow;
			col = libraryCol;
			charRow = libraryCharRow;
			charCol = libraryCharCol;
			wepRow = libraryWepRow;
			wepCol = libraryWepCol;
			layout = library;
		} else if (name.equals(RoomName.LOUNGE)) {
			row = loungeRow;
			col = loungeCol;
			charRow = loungeCharRow;
			charCol = loungeCharCol;
			wepRow = loungeWepRow;
			wepCol = loungeWepCol;
			layout = lounge;
		} else if (name.equals(RoomName.STUDY)) {
			row = studyRow;
			col = studyCol;
			charRow = studyCharRow;
			charCol = studyCharCol;
			wepRow = studyWepRow;
			wepCol = studyWepCol;
			layout = study;
		}
	}
	
	// Room coordinates and layouts
	//==============================
	// 0 = not this room
	// 1 = this room
	// UP = doorway leading up
	// RIGHT = doorway leading right
	// DOWN = doorway leading down
	// LEFT = doorway leading left
	
	// doorways
	private final int UP = 10, RIGHT = 11, DOWN = 12, LEFT = 13; 
	
	// Kitchen coordinates on the map and floor layout,
	// and character list starting coordinates
	private int kitchenRow = 2, kitchenCol = 0;
	private int kitchenCharRow = 5, kitchenCharCol = 4;
	private int kitchenWepRow = 6, kitchenWepCol = 44;
	private int[][] kitchen = {
			{1, 1, 1, 1, 1, 2},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, DOWN, 1},
	};
	
	// Ballroom coordinates on the map and floor layout,
	// and character list starting coordinates
	private int ballroomRow = 2, ballroomCol = 8;
	private int ballroomCharRow = 6, ballroomCharCol = 23;
	private int ballroomWepRow = 7, ballroomWepCol = 23;
	private int[][] ballroom = {
			{0, 0, 1, 1, 1, 1, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{LEFT, 1, 1, 1, 1, 1, 1, RIGHT},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, DOWN, 1, 1, 1, 1, DOWN, 1}
	};
	
	// Conservatory coordinates on the map and floor layout,
	// and character list starting coordinates
	private int conservatoryRow = 2, conservatoryCol = 18;
	private int conservatoryCharRow = 4, conservatoryCharCol = 41;
	private int conservatoryWepRow = 5, conservatoryWepCol = 41;
	private int[][] conservatory = {
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{DOWN, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 0}
	};
	
	// Billiard room coordinates on the map and floor layout,
	// and character list starting coordinates
	private int billiardRow = 9, billiardCol = 18;
	private int billiardCharRow = 11, billiardCharCol = 41;
	private int billiardWepRow = 12, billiardWepCol = 41;
	private int[][] billiard = {
			{1, 1, 1, 1, 1, 1},
			{LEFT, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, DOWN, 1}
	};
	
	// Dining room coordinates on the map and floor layout,
	// and character list starting coordinates
	private int diningRow = 10, diningCol = 0;
	private int diningCharRow = 14, diningCharCol = 5;
	private int diningWepRow = 15, diningWepCol = 5;
	private int[][] dining = {
			{1, 1, 1, 1, 1, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, RIGHT},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, DOWN, 1}
	};
	
	// Library coordinates on the map and floor layout,
	// and character list starting coordinates
	private int libraryRow = 15, libraryCol = 17;
	private int libraryCharRow = 17, libraryCharCol = 39;
	private int libraryWepRow = 18, libraryWepCol = 39;
	private int[][] library = {
			{0, 1, 1, UP, 1, 1, 0},
			{1, 1, 1, 1, 1, 1, 1},
			{LEFT, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 1, 0}
	};
	
	// Hall coordinates on the map and floor layout,
	// and character list starting coordinates
	private int hallRow = 19, hallCol = 9;
	private int hallCharRow = 22, hallCharCol = 21;
	private int hallWepRow = 24, hallWepCol = 21; 
	private int[][] hall = {
			{1, 1, UP, UP, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, RIGHT},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1}
	};
	
	// Lounge coordinates on the map and floor layout,
	// and character list starting coordinates
	private int loungeRow = 20, loungeCol = 0;
	private int loungeCharRow = 23, loungeCharCol = 4;
	private int loungeWepRow = 24, loungeWepCol = 4;
	private int[][] lounge = {
			{1, 1, 1, 1, 1, 1, UP},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 0}
	};
	
	// Study coordinates on the map and floor layout,
	// and character list starting coordinates
	private int studyRow = 22, studyCol = 17;
	private int studyCharRow = 23, studyCharCol = 39;
	private int studyWepRow = 24, studyWepCol = 39;
	private int[][] study = {
			{UP, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 1, 1}
	};
}
