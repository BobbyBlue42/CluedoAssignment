package cluedo.game;

import java.awt.Point;
import java.util.ArrayList;

import cluedo.ui.UI;

/**
 * Represents the rooms of a Cluedo game, and stores all
 * information which is necessary to do so.
 * 
 * @author Louis Thie
 */
public class Room implements GamePiece {
	/**
	 * Has all the names of the rooms typically found in a Cluedo
	 * game.
	 * 
	 * @author Louis Thie
	 */
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

		/**
		 * Returns a human-friendly version of the name of this RoomName.
		 * That is, all underscores are replaced with spaces and words are
		 * properly capitalised.
		 * 
		 * @return	human-friendly version of this RoomName's name
		 */
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
	
	/**
	 * Constructs a Room with the given name. Assigns fields
	 * depending on which name is given.
	 * 
	 * @param name	the name of this Room
	 */
	public Room(RoomName name) {
		this.name = name;
		characters = new ArrayList<Character>();
		weapons = new ArrayList<Weapon>();
		
		assignFields();
	}
	
	/**
	 * Returns a reader-friendly version of this Character's name.
	 * 
	 * @return	reader-friendly name
	 */
	public String name() {
		return name.toString();
	}
	
	/**
	 * Returns the integer that this Room is represented by
	 * in the Board's grid.
	 * 
	 * @return
	 */
	public int toInt() {
		return name.ordinal();
	}
	
	/**
	 * Moves a Character into this Room.
	 * 
	 * @param c		the Character to add to this Room
	 */
	public void addCharacter(Character c) {
		// add character at the end of the list of characters with players
		// characters without players will be moved along
		int index = -1;
		for (int i = 0; i < characters.size(); i++) {
			if (characters.get(i).player() == null) {
				index = i;
				break;
			}
		}
		if (index > -1)
			characters.add(index, c);
		else
			characters.add(c);
		
		// doesn't matter if the characters are all stacked on top of each other
		// also, all of the rooms have a '1' at (relative) (1,1)
		c.setRow(row+1);
		c.setCol(col+1);
	}
	
	/**
	 * Removes the given Character from this Room.
	 * 
	 * @param c		the Character to remove from this Room
	 */
	public void removeCharacter(Character c) {
		characters.remove(c);
	}
	
	/**
	 * Returns a List of all the Characters in this Room.
	 * 
	 * @return		ArrayList of Characters in this Room
	 */
	public ArrayList<Character> getCharacters() {
		return characters;
	}
	
	/**
	 * Moves the given Weapon into this Room.
	 * 
	 * @param w		the Weapon to add to this Room
	 */
	public void addWeapon(Weapon w) {
		weapons.add(w);
	}
	
	/**
	 * Removes the given Weapon from this Room.
	 * 
	 * @param w		the Weapon to remove from this Room
	 */
	public void removeWeapon(Weapon w) {
		weapons.remove(w);
	}
	
	/**
	 * Returns a List of all the Weapons in this Room.
	 * 
	 * @return		ArrayList of Weapons in this Room
	 */
	public ArrayList<Weapon> getWeapons() {
		return weapons;
	}
	
	/**
	 * Connects this Room to another Room via a secret passageway.
	 * 
	 * @param other		the Room to connect this Room to
	 */
	public void connectTo(Room other) {
		connection = other;
	}
	
	/**
	 * Checks whether this Room is connected to another Room
	 * via a secret passageway.
	 * 
	 * @return	whether this Room is connected to another Room directly
	 */
	public boolean hasConnection() {
		return connection != null;
	}
	
	/**
	 * Gets the Room that this Room is connected to via a 
	 * secret passageway.
	 * 
	 * @return	the connected Room
	 */
	public Room connection() {
		return connection;
	}
	
	/**
	 * Return whether it is possible to enter this Room from the given position,
	 * heading towards the given position.
	 * 
	 * @param fromRow	the Player's current row
	 * @param fromCol	the Player's current column
	 * @param toRow		the Player's target row
	 * @param toCol		the Player's target column
	 * @return			whether or not it is possible to enter this Room like this
	 */
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
	
	/**
	 * Checks whether it is possible to leave this Room in the given
	 * Direction.
	 * 
	 * @param d		the Direction to check
	 * @return		whether there is a door in that Direction
	 */
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

	/**
	 * Returns a single Point at which the current player will emerge from
	 * this room. If multiple exits are available in the same Direction,
	 * asks the user which they would prefer.
	 * 
	 * @param d			the Direction the user wants to leave in
	 * @param board		the Board that contains this Room
	 * @return			a Point representing the co-ordinates that the Player wants to exit to
	 */
	public Point getExitPoint(Board.Direction d, Board board) {
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
			String question = "There are multiple exits in this direction.\nWhich exit would you like to use?";
			String[] options = new String[exits.size()];
			for (int i = 0; i < exits.size(); i++) {
				options[i] = "Row "+exits.get(i).getX()+", Column "+exits.get(i).getY();
			}
			
			int ans = board.askOpt(question, options);
			
			return exits.get(ans-1);
		} else if (exits.size() == 1) {
			return exits.get(0);
		}
		
		return null;	// there are no exit points in this direction
	}
	
	/**
	 * Checks whether or not this Room contains anything which would
	 * require to be printed at the given position.
	 * 
	 * @param row			The row at which something would be printed
	 * @param charCount		The count of how many characters have already been printed in this row
	 * @return				Whether or not something should be printed here
	 */
	public boolean hasChar(int row, int charCount) {
		if (row == charRow) {
			for (int i = 0; i < characters.size(); i++) {
				if (charCount == charCol+i && characters.get(i).player() != null && characters.get(i).player().isAlive()) {
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
	
	/**
	 * Returns the character to be printed at the given position.
	 * This is mainly useful for a text-based UI.
	 * 
	 * @param row			The row on the board that the character is being printed in
	 * @param charCount		The count of how many characters have already been printed in this row
	 * @param board			the Board containing this Room
	 * @return				the character that should be printed next
	 */
	public char getChar(int row, int charCount, Board board) {
		ArrayList<Player> playerList = board.getPlayers();
		if (row == charRow) {
			for (int i = 0; i < characters.size(); i++) {
				if (charCount == charCol+i && characters.get(i).player() != null) {
					return (char)('0' + (playerList.indexOf(characters.get(i).player()) + 1));
				}
			}
		} else if (row == wepRow) {
			for (int i = 0; i < weapons.size(); i++) {
				if (charCount == wepCol+i) {
					return weapons.get(i).toChar();
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
	private final static int UP = 10;
	private final static int RIGHT = 11;
	private final static int DOWN = 12;
	private final static int LEFT = 13; 
	
	// Kitchen coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int kitchenRow = 2, kitchenCol = 0;
	private static final int kitchenCharRow = 5, kitchenCharCol = 4;
	private static final int kitchenWepRow = 6, kitchenWepCol = 4;
	private static final int[][] kitchen = {
			{1, 1, 1, 1, 1, 2},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, DOWN, 1},
	};
	
	// Ballroom coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int ballroomRow = 2, ballroomCol = 8;
	private static final int ballroomCharRow = 6, ballroomCharCol = 23;
	private static final int ballroomWepRow = 7, ballroomWepCol = 23;
	private static final int[][] ballroom = {
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
	private static final int conservatoryRow = 2, conservatoryCol = 18;
	private static final int conservatoryCharRow = 4, conservatoryCharCol = 41;
	private static final int conservatoryWepRow = 5, conservatoryWepCol = 41;
	private static final int[][] conservatory = {
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{DOWN, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 0}
	};
	
	// Billiard room coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int billiardRow = 9, billiardCol = 18;
	private static final int billiardCharRow = 11, billiardCharCol = 41;
	private static final int billiardWepRow = 12, billiardWepCol = 41;
	private static final int[][] billiard = {
			{1, 1, 1, 1, 1, 1},
			{LEFT, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, DOWN, 1}
	};
	
	// Dining room coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int diningRow = 10, diningCol = 0;
	private static final int diningCharRow = 14, diningCharCol = 5;
	private static final int diningWepRow = 15, diningWepCol = 5;
	private static final int[][] dining = {
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
	private static final int libraryRow = 15, libraryCol = 17;
	private static final int libraryCharRow = 17, libraryCharCol = 39;
	private static final int libraryWepRow = 18, libraryWepCol = 39;
	private static final int[][] library = {
			{0, 1, 1, UP, 1, 1, 0},
			{1, 1, 1, 1, 1, 1, 1},
			{LEFT, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 1, 0}
	};
	
	// Hall coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int hallRow = 19, hallCol = 9;
	private static final int hallCharRow = 22, hallCharCol = 21;
	private static final int hallWepRow = 24, hallWepCol = 21; 
	private static final int[][] hall = {
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
	private static final int loungeRow = 20, loungeCol = 0;
	private static final int loungeCharRow = 23, loungeCharCol = 4;
	private static final int loungeWepRow = 24, loungeWepCol = 4;
	private static final int[][] lounge = {
			{1, 1, 1, 1, 1, 1, UP},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 0}
	};
	
	// Study coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int studyRow = 22, studyCol = 17;
	private static final int studyCharRow = 23, studyCharCol = 39;
	private static final int studyWepRow = 24, studyWepCol = 39;
	private static final int[][] study = {
			{UP, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 1, 1}
	};
}
