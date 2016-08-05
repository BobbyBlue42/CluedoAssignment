package cluedo.game;

/**
 * Represents the weapons of a Cluedo game, and stores all
 * information which is necessary to do so.
 * 
 * @author Louis Thie
 */
public class Weapon implements GamePiece {
	/**
	 * Has all the names of the characters typically found in a Cluedo
	 * game.
	 * 
	 * @author Louis Thie
	 */
	public enum WeaponName {
		CANDLESTICK('C'),
		DAGGER('D'),
		LEAD_PIPE('P'),
		REVOLVER('G'),
		ROPE('R'),
		SPANNER('S');
		
		private char c;
		
		private WeaponName(char c) {
			this.c = c;
		}
		
		/**
		 * Returns the character used to represent the Weapon that has
		 * this name on the board.
		 * 
		 * @return		the character which represents this name's Weapon
		 */
		public char toChar() {
			return c;
		}

		/**
		 * Returns a human-friendly version of the name of this WeaponName.
		 * That is, all underscores are replaced with spaces and words are
		 * properly capitalised.
		 * 
		 * @return	human-friendly version of this WeaponName's name
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
	
	private WeaponName name;
	private Room location;
	
	/**
	 * Constructs a Weapon with the given name, in the given
	 * Room.
	 * 
	 * @param name		the name of this Weapon
	 * @param room		the Room this Weapon is in
	 */
	public Weapon(WeaponName name, Room room) {
		this.name = name;
		location = room;
	}
	
	/**
	 * Returns a reader-friendly version of this Weapon's name.
	 * 
	 * @return	reader-friendly name
	 */
	public String name() {
		return name.toString();
	}
	
	/**
	 * Returns the character this Weapon is represented by on the Board.
	 * 
	 * @return	the character representing this Weapon
	 */
	public char toChar() {
		return name.toChar();
	}
	
	/**
	 * Gets the Room which this Weapon is situated in.
	 * 
	 * @return	the Room this Weapon is currently in
	 */
	public Room location() {
		return location;
	}
	
	/**
	 * Sets the Room which this Weapon is situated in.
	 * 
	 * @param r		the Room to move this Weapon to
	 */
	public void moveToRoom(Room r) {
		location = r;
	}
}
