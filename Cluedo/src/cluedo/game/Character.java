package cluedo.game;

import java.awt.Point;

public class Character {
	public enum CharacterName {
		MISS_SCARLETT(25, 7),
		PROFESSOR_PLUM(20, 23),
		MRS_PEACOCK(7, 23),
		MRS_WHITE(1, 14),
		REVEREND_GREEN(1, 9),
		COLONEL_MUSTARD(18, 0);
		
		private char c;
		private int startRow, startCol;
		
		private CharacterName(int startRow, int startCol) {
			this.c = c;
			this.startRow = startRow;
			this.startCol = startCol;
		}
		
		public int getRow() {
			return startRow;
		}
		
		public int getCol() {
			return startCol;
		}
		
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
	
	public Character(CharacterName name, int row, int col) {
		this.name = name;
		this.row = row;
		this.col = col;
	}
	
	public CharacterName name() {
		return name;
	}
	
	public void assignTo(Player p) {
		player = p;
	}
	
	public Player player() {
		return player;
	}
	
	public void enterRoom(Room r) {
		location = r;
	}
	
	public void leaveRoom() {
		location = null;
	}
	
	public Room location() {
		return location;
	}

	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
}
