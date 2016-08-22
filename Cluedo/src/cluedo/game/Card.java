package cluedo.game;

import javax.swing.ImageIcon;

/**
 * Represents all cards in the Cluedo game, and stores
 * all information regarding those cards.
 * 
 * @author Louis Thie
 */
public class Card {
	private boolean isMurderComponent;
	private GamePiece piece;
	
	/**
	 * Constructs a Card object, using the GamePiece that the
	 * Card will represent and a boolean value representing
	 * whether or not it is one of the components of the murder.
	 * 
	 * @param piece					GamePiece this Card will represent
	 * @param isMurderComponent		whether the GamePiece is part of the murder solution
	 */
	public Card(GamePiece piece, boolean isMurderComponent) {
		this.piece = piece;
		this.isMurderComponent = isMurderComponent;
	}
	
	/**
	 * Returns the name of the GamePiece that this Card represents.
	 * 
	 * @return	Human-friendly version of the GamePiece's name
	 */
	public String name() {
		return piece.name();
	}
	
	/**
	 * Returns the GamePiece that this Card represents.
	 * 
	 * @return	the GamePiece represented by this Card
	 */
	public GamePiece piece() {
		return piece;
	}
	
	/**
	 * Returns whether or not the GamePiece this Card represents
	 * was part of the murder.
	 * 
	 * @return	whether this Card is part of the murder solution
	 */
	public boolean isMurderComponent() {
		return isMurderComponent;
	}
	
	public ImageIcon icon(String mod) {
		return piece.icon("_card"+mod);
	}
}
