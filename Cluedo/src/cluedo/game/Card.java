package cluedo.game;

import java.lang.Character;

public class Card {
	protected boolean isMurderComponent;
	private GamePiece piece;
	
	public Card(GamePiece piece, boolean isMurderComponent) {
		this.piece = piece;
		this.isMurderComponent = isMurderComponent;
	}
	
	public String name() {
		return piece.name();
	}
	
	public GamePiece piece() {
		return piece;
	}
	
	public boolean isMurderComponent() {
		return isMurderComponent;
	}
}
