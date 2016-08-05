package cluedo.game;

import java.util.ArrayList;

public class Player {
	private String name;
	private ArrayList<Card> hand;
	private Character character;
	private boolean isAlive = true;
	
	public Player(String name) {
		this.name = name;
		hand = new ArrayList<Card>();
	}
	
	public void chooseCharacter(Character c) {
		character = c;
	}
	
	public Character character() {
		return character;
	}
	
	public void deal(Card c) {
		hand.add(c);
	}
	
	public Card[] hand() {
		return hand.toArray(new Card[hand.size()]);
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public void die() {
		isAlive = false;
		if (character.location() != null)
			character.location().removeCharacter(character);
	}
	
	public String name() {
		return name;
	}
}
