package cluedo.game;

import java.util.ArrayList;

import javax.swing.ImageIcon;

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

		// room images downloaded from http://happywithgame.com/wp-content/uploads/2015/08/clue-game-cards-rooms.jpg
		private static ImageIcon KITCHEN_name_icon = new ImageIcon(Room.class.getResource("img/KITCHEN_name.png"));
		private static ImageIcon KITCHEN_card_icon = new ImageIcon(Room.class.getResource("img/KITCHEN.png"));
		private static ImageIcon KITCHEN_card_large_icon = new ImageIcon(Room.class.getResource("img/KITCHEN_large.png"));
		private static ImageIcon BALLROOM_name_icon = new ImageIcon(Room.class.getResource("img/BALLROOM_name.png"));
		private static ImageIcon BALLROOM_card_icon = new ImageIcon(Room.class.getResource("img/BALLROOM.png"));
		private static ImageIcon BALLROOM_card_large_icon = new ImageIcon(Room.class.getResource("img/BALLROOM_large.png"));
		private static ImageIcon CONSERVATORY_name_icon = new ImageIcon(Room.class.getResource("img/CONSERVATORY_name.png"));
		private static ImageIcon CONSERVATORY_card_icon = new ImageIcon(Room.class.getResource("img/CONSERVATORY.png"));
		private static ImageIcon CONSERVATORY_card_large_icon = new ImageIcon(Room.class.getResource("img/CONSERVATORY_large.png"));
		private static ImageIcon BILLIARD_ROOM_name_icon = new ImageIcon(Room.class.getResource("img/BILLIARD_ROOM_name.png"));
		private static ImageIcon BILLIARD_ROOM_card_icon = new ImageIcon(Room.class.getResource("img/BILLIARD_ROOM.png"));
		private static ImageIcon BILLIARD_ROOM_card_large_icon = new ImageIcon(Room.class.getResource("img/BILLIARD_ROOM_large.png"));
		private static ImageIcon DINING_ROOM_name_icon = new ImageIcon(Room.class.getResource("img/DINING_ROOM_name.png"));
		private static ImageIcon DINING_ROOM_card_icon = new ImageIcon(Room.class.getResource("img/DINING_ROOM.png"));
		private static ImageIcon DINING_ROOM_card_large_icon = new ImageIcon(Room.class.getResource("img/DINING_ROOM_large.png"));
		private static ImageIcon LIBRARY_name_icon = new ImageIcon(Room.class.getResource("img/LIBRARY_name.png"));
		private static ImageIcon LIBRARY_card_icon = new ImageIcon(Room.class.getResource("img/LIBRARY.png"));
		private static ImageIcon LIBRARY_card_large_icon = new ImageIcon(Room.class.getResource("img/LIBRARY_large.png"));
		private static ImageIcon HALL_name_icon = new ImageIcon(Room.class.getResource("img/HALL_name.png"));
		private static ImageIcon HALL_card_icon = new ImageIcon(Room.class.getResource("img/HALL.png"));
		private static ImageIcon HALL_card_large_icon = new ImageIcon(Room.class.getResource("img/HALL_large.png"));
		private static ImageIcon LOUNGE_name_icon = new ImageIcon(Room.class.getResource("img/LOUNGE_name.png"));
		private static ImageIcon LOUNGE_card_icon = new ImageIcon(Room.class.getResource("img/LOUNGE.png"));
		private static ImageIcon LOUNGE_card_large_icon = new ImageIcon(Room.class.getResource("img/LOUNGE_large.png"));
		private static ImageIcon STUDY_name_icon = new ImageIcon(Room.class.getResource("img/STUDY_name.png"));
		private static ImageIcon STUDY_card_icon = new ImageIcon(Room.class.getResource("img/STUDY.png"));
		private static ImageIcon STUDY_card_large_icon = new ImageIcon(Room.class.getResource("img/STUDY_large.png"));
		
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

		public ImageIcon icon(String mod) {
			try {
				return (ImageIcon) getClass().getDeclaredField(name()+mod+"_icon").get(this);
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {}
			return null;
		}
	}
	
	private RoomName name;
	private ArrayList<Character> characters;
	private ArrayList<Weapon> weapons;
	private Room connection;
	
	private int nameRow, nameWidth;
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
	
	public String toString() {
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
	
	public boolean exitToPoint(Character c, int row, int col) {
		if (characters.contains(c)) {
			// square would be one *outside* of the layout array
			
			if (col-this.col >= 0 && col-this.col < layout[0].length) {
				// col is within bounds
				if (row-this.row == -1) {
					// trying to go up
					if (layout[row-this.row+1][col-this.col] == UP) {
						return true;
					}
				} else if (row-this.row == layout.length) {
					// trying to go down
					if (layout[row-this.row-1][col-this.col] == DOWN) {
						return true;
					}
				} else if (row-this.row == layout.length - 1) {
					// trying to go down (special case for Conservatory)
					if (layout[row-this.row-1][col-this.col] == DOWN) {
						return true;
					}
				}
			} else if (row-this.row >= 0 && row-this.row < layout.length) {
				System.out.println("horizontal");
				// row is within bounds
				if (col-this.col == -1) {
					// trying to go left
					if (layout[row-this.row][col-this.col+1] == LEFT) {
						return true;
					}
				} else if (col-this.col == layout[0].length) {
					// trying to go right
					if (layout[row-this.row][col-this.col-1] == RIGHT) {
						return true;
					}
				}
			}
		}
		
		return false;
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
	
	public ImageIcon getIcon(int row, int col) {
		if (row == nameRow) {
			return name.icon("_name");
		} else if (layout[row-this.row][col-this.col] >= 10) {
			int door = layout[row-this.row][col-this.col];
			switch (door) {
			case UP:
				return door_up;
			case RIGHT:
				return door_right;
			case DOWN:
				return door_down;
			case LEFT:
				return door_left;
			}
		} else if (row == charRow) {
			int actual = col-charCol;	// translate from board ordinate
			if (actual >= 0 && actual < characters.size()) {
				return characters.get(actual).icon("_room");
			}
		} else if (row == wepRow) {
			int actual = col-wepCol;
			if (actual >= 0 && actual < weapons.size()) {
				return weapons.get(actual).icon("");
			}
		}
		return null;
	}
	
	public int getNameWidth() {
		return nameWidth;
	}
	
	
	private void assignFields() {
		if (name.equals(RoomName.BALLROOM)) {
			nameRow = ballroomNameRow;
			nameWidth = ballroomNameWidth;
			row = ballroomRow;
			col = ballroomCol;
			charRow = ballroomCharRow;
			charCol = ballroomCharCol;
			wepRow = ballroomWepRow;
			wepCol = ballroomWepCol;
			layout = ballroom;
		} else if (name.equals(RoomName.BILLIARD_ROOM)) {
			nameRow = billiardNameRow;
			nameWidth = billiardNameWidth;
			row = billiardRow;
			col = billiardCol;
			charRow = billiardCharRow;
			charCol = billiardCharCol;
			wepRow = billiardWepRow;
			wepCol = billiardWepCol;
			layout = billiard;
		} else if (name.equals(RoomName.CONSERVATORY)) {
			nameRow = conservatoryNameRow;
			nameWidth = conservatoryNameWidth;
			row = conservatoryRow;
			col = conservatoryCol;
			charRow = conservatoryCharRow;
			charCol = conservatoryCharCol;
			wepRow = conservatoryWepRow;
			wepCol = conservatoryWepCol;
			layout = conservatory;
		} else if (name.equals(RoomName.DINING_ROOM)) {
			nameRow = diningNameRow;
			nameWidth = diningNameWidth;
			row = diningRow;
			col = diningCol;
			charRow = diningCharRow;
			charCol = diningCharCol;
			wepRow = diningWepRow;
			wepCol = diningWepCol;
			layout = dining;
		} else if (name.equals(RoomName.HALL)) {
			nameRow = hallNameRow;
			nameWidth = hallNameWidth;
			row = hallRow;
			col = hallCol;
			charRow = hallCharRow;
			charCol = hallCharCol;
			wepRow = hallWepRow;
			wepCol = hallWepCol;
			layout = hall;
		} else if (name.equals(RoomName.KITCHEN)) {
			nameRow = kitchenNameRow;
			nameWidth = kitchenNameWidth;
			row = kitchenRow;
			col = kitchenCol;
			charRow = kitchenCharRow;
			charCol = kitchenCharCol;
			wepRow = kitchenWepRow;
			wepCol = kitchenWepCol;
			layout = kitchen;
		} else if (name.equals(RoomName.LIBRARY)) {
			nameRow = libraryNameRow;
			nameWidth = libraryNameWidth;
			row = libraryRow;
			col = libraryCol;
			charRow = libraryCharRow;
			charCol = libraryCharCol;
			wepRow = libraryWepRow;
			wepCol = libraryWepCol;
			layout = library;
		} else if (name.equals(RoomName.LOUNGE)) {
			nameRow = loungeNameRow;
			nameWidth = loungeNameWidth;
			row = loungeRow;
			col = loungeCol;
			charRow = loungeCharRow;
			charCol = loungeCharCol;
			wepRow = loungeWepRow;
			wepCol = loungeWepCol;
			layout = lounge;
		} else if (name.equals(RoomName.STUDY)) {
			nameRow = studyNameRow;
			nameWidth = studyNameWidth;
			row = studyRow;
			col = studyCol;
			charRow = studyCharRow;
			charCol = studyCharCol;
			wepRow = studyWepRow;
			wepCol = studyWepCol;
			layout = study;
		}
	}

	private static ImageIcon door_up = new ImageIcon(Room.class.getResource("img/room_door_up.png"));
	private static ImageIcon door_right = new ImageIcon(Room.class.getResource("img/room_door_right.png"));
	private static ImageIcon door_down = new ImageIcon(Room.class.getResource("img/room_door_down.png"));
	private static ImageIcon door_left = new ImageIcon(Room.class.getResource("img/room_door_left.png"));
	
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
	private static final int kitchenNameRow = 3, kitchenNameWidth = 6;
	private static final int kitchenRow = 2, kitchenCol = 0;
	private static final int kitchenCharRow = 5, kitchenCharCol = 0;
	private static final int kitchenWepRow = 6, kitchenWepCol = 0;
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
	private static final int ballroomNameRow = 4, ballroomNameWidth = 8;
	private static final int ballroomRow = 2, ballroomCol = 8;
	private static final int ballroomCharRow = 6, ballroomCharCol = 10;
	private static final int ballroomWepRow = 7, ballroomWepCol = 10;
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
	private static final int conservatoryNameRow = 2, conservatoryNameWidth = 6;
	private static final int conservatoryRow = 2, conservatoryCol = 18;
	private static final int conservatoryCharRow = 3, conservatoryCharCol = 18;
	private static final int conservatoryWepRow = 4, conservatoryWepCol = 18;
	private static final int[][] conservatory = {
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{DOWN, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 0}
	};
	
	// Billiard room coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int billiardNameRow = 9, billiardNameWidth = 6;
	private static final int billiardRow = 9, billiardCol = 18;
	private static final int billiardCharRow = 11, billiardCharCol = 18;
	private static final int billiardWepRow = 12, billiardWepCol = 18;
	private static final int[][] billiard = {
			{1, 1, 1, 1, 1, 1},
			{LEFT, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, DOWN, 1}
	};
	
	// Dining room coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int diningNameRow = 12, diningNameWidth = 8;
	private static final int diningRow = 10, diningCol = 0;
	private static final int diningCharRow = 14, diningCharCol = 1;
	private static final int diningWepRow = 15, diningWepCol = 1;
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
	private static final int libraryNameRow = 16, libraryNameWidth = 7;
	private static final int libraryRow = 15, libraryCol = 17;
	private static final int libraryCharRow = 17, libraryCharCol = 18;
	private static final int libraryWepRow = 18, libraryWepCol = 18;
	private static final int[][] library = {
			{0, 1, 1, UP, 1, 1, 0},
			{1, 1, 1, 1, 1, 1, 1},
			{LEFT, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 1, 0}
	};
	
	// Hall coordinates on the map and floor layout,
	// and character list starting coordinates
	private static final int hallNameRow = 20, hallNameWidth = 6;
	private static final int hallRow = 19, hallCol = 9;
	private static final int hallCharRow = 22, hallCharCol = 10;
	private static final int hallWepRow = 24, hallWepCol = 10; 
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
	private static final int loungeNameRow = 21, loungeNameWidth = 7;
	private static final int loungeRow = 20, loungeCol = 0;
	private static final int loungeCharRow = 23, loungeCharCol = 0;
	private static final int loungeWepRow = 24, loungeWepCol = 0;
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
	private static final int studyNameRow = 23, studyNameWidth = 7;
	private static final int studyRow = 22, studyCol = 17;
	private static final int studyCharRow = 24, studyCharCol = 18;
	private static final int studyWepRow = 25, studyWepCol = 18;
	private static final int[][] study = {
			{UP, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{0, 1, 1, 1, 1, 1, 1}
	};
	
	public ImageIcon icon(String mod) {
		return name.icon(mod);
	}
}
