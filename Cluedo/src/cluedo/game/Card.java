package cluedo.game;

import java.lang.Character;

public abstract class Card {
	protected boolean isMurderComponent;
	protected Player holder;
	
	/**
	 * Returns the name of the object represented by this card, in
	 * reader-friendly formatting (with underscores replaced by spaces
	 * and the words properly capitalised).
	 * 
	 * @return 		the name of the object represented by this card
	 */
	public abstract String name();
	
	public boolean isMurderComponent() {
		return isMurderComponent;
	}
	
	public void dealTo(Player p) {
		holder = p;
	}
	
	public Player holder() {
		return holder;
	}
	
	/**
	 * Returns the lower-case version of the character. Ignores non-alphabetic
	 * and lower-case letters.
	 * 
	 * @param c		the character to convert
	 * @return		the lower-case version of character c
	 */
	protected char toLower(char c) {
		if (Character.isAlphabetic(c) && !Character.isLowerCase(c)) {
			return Character.toLowerCase(c);
		}
		return c;
	}
}
