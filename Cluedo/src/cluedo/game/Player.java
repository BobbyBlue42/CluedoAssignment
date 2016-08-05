package cluedo.game;

import java.util.ArrayList;

/**
 * Represents the players playing Cluedo.
 * Stores information required for this.
 * 
 * @author Louis Thie
 */
public class Player {
	private String name;
	private ArrayList<Card> hand;
	private Character character;
	private boolean isAlive = true;
	
	/**
	 * Constructs new Player object with the given name.
	 * 
	 * @param name	the name of the player to be represented by this
	 */
	public Player(String name) {
		this.name = name;
		hand = new ArrayList<Card>();
	}
	
	/**
	 * Sets the Character of this Player.
	 * 
	 * @param c		the Character to choose
	 */
	public void chooseCharacter(Character c) {
		character = c;
	}
	
	/**
	 * Returns the Character of this Player.
	 * 
	 * @return	the Character chosen by this player
	 */
	public Character character() {
		return character;
	}
	
	/**
	 * Adds a Card to this Player's hand.
	 * 
	 * @param c		the Card to add
	 */
	public void deal(Card c) {
		hand.add(c);
	}
	
	/**
	 * Returns the hand of this Player.
	 * 
	 * @return	the Cards this Player has
	 */
	public Card[] hand() {
		return hand.toArray(new Card[hand.size()]);
	}
	
	/**
	 * Returns whether this Player is still alive or not.
	 * 
	 * @return	whether this is alive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * Kills this Player and removes their character from the board.
	 */
	public void die() {
		isAlive = false;
		if (character.location() != null)
			character.location().removeCharacter(character);
	}
	
	/**
	 * Returns this Player's name.
	 * 
	 * @return	this Player's name
	 */
	public String name() {
		return name;
	}
}
