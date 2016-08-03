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
	
	public boolean isHolding(Card c) {
		return hand.contains(c);
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public void die() {
		isAlive = false;
	}
	
	public String name() {
		return name;
	}
}
