package cluedo.game;

import java.util.ArrayList;

public class Player {
	private String name;
	private ArrayList<Card> hand;
	private Character character;
	
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
	
	public boolean isHolding(Card c) {
		return hand.contains(c);
	}
	
	public String name() {
		return name;
	}
}
