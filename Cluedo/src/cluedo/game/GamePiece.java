package cluedo.game;

import javax.swing.ImageIcon;

/**
 * Allows Card to represent any of the GamePiece classes - Character, Room or Weapon.
 * Ensures that Card can get all the information it needs from them (their name).
 * 
 * @author Louis Thie
 */
public interface GamePiece {
	/**
	 * Returns a reader-friendly version of this GamePiece's name.
	 * 
	 * @return	reader-friendly name of this
	 */
	public String name();
	
	public ImageIcon icon(String mod);
}
