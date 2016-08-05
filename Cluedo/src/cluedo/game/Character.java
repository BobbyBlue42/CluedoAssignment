package cluedo.game;

import java.awt.Point;

/**
 * Represents the characters of a Cluedo game, and stores all
 * information which is necessary to do so.
 * 
 * @author Louis Thie
 */
public class Character implements GamePiece {
	/**
	 * Has all the names of the characters typically found in a Cluedo
	 * game, as well as their starting positions.
	 * 
	 * @author Louis Thie
	 */
	public enum CharacterName {
		MISS_SCARLETT(25, 7),
		PROFESSOR_PLUM(20, 23),
		MRS_PEACOCK(7, 23),
		MRS_WHITE(1, 14),
		REVEREND_GREEN(1, 9),
		COLONEL_MUSTARD(18, 0);
		
		private int startRow, startCol;
		
		private CharacterName(int startRow, int startCol) {
			this.startRow = startRow;
			this.startCol = startCol;
		}
		
		/**
		 * Returns the starting row of the Character with this name.
		 * 
		 * @return	starting row of the Character containing this
		 */
		public int getRow() {
			return startRow;
		}
		
		/**
		 * Returns the starting column of the Character with this name.
		 * 
		 * @return	starting column of the Character containing this
		 */
		public int getCol() {
			return startCol;
		}
		
		/**
		 * Returns a human-friendly version of the name of this CharacterName.
		 * That is, all underscores are replaced with spaces and words are
		 * properly capitalised.
		 * 
		 * @return	human-friendly version of this CharacterName's name
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
	
	private CharacterName name;
	private Room location;
	private Player player;
	private int row, col;
	
	/**
	 * Constructs a new Character object using the CharacterName
	 * given, and stores the row and column given.
	 * 
	 * @param name		the CharacterName of this Character
	 * @param row		the starting row
	 * @param col		the starting column
	 */
	public Character(CharacterName name, int row, int col) {
		this.name = name;
		this.row = row;
		this.col = col;
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
	 * Returns the integer that this Character is represented by in
	 * the Board's grid.
	 * 
	 * @return	integer version of the name
	 */
	public int toInt() {
		return name.ordinal();
	}
	
	/**
	 * Sets the Player that controls this Character.
	 * 
	 * @param p		the Player to control this
	 */
	public void assignTo(Player p) {
		player = p;
	}
	
	/**
	 * Returns the Player that controls this Character.
	 * 
	 * @return		the Player controlling this
	 */
	public Player player() {
		return player;
	}
	
	/**
	 * Sets the Room that this Character is in.
	 * 
	 * @param r		the Room to enter
	 */
	public void enterRoom(Room r) {
		location = r;
	}
	
	/**
	 * Sets the Room that this Character is in to null.
	 */
	public void leaveRoom() {
		location = null;
	}
	
	/**
	 * Returns the Room this Character is in.
	 * 
	 * @return		the Room this Character is in
	 */
	public Room location() {
		return location;
	}
	
	/**
	 * Returns the starting row of this Character
	 * 
	 * @return		this Character's starting row
	 */
	public int getStartRow() {
		return name.getRow();
	}
	
	/**
	 * Returns the current row of this Character
	 * 
	 * @return		this Character's current row
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * Sets this Character's current row
	 * 
	 * @param row	the row to move this Character to
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Returns the starting column of this Character
	 * 
	 * @return		this Character's starting column
	 */
	public int getStartCol() {
		return name.getCol();
	}
	
	/**
	 * Returns the current column of this Character
	 * 
	 * @return		this Character's current column
	 */
	public int getCol() {
		return col;
	}
	
	/**
	 * Sets this Character's current column
	 * 
	 * @param column	the column to move this Character to
	 */
	public void setCol(int col) {
		this.col = col;
	}
}
