package cluedo.game;

public class CharacterCard extends Card {
	private Character character;
	
	public CharacterCard(Character character, boolean isMurderComponent) {
		this.character = character;
		this.isMurderComponent = isMurderComponent;
	}
	
	public String name() {
		return character.name().toString();
	}
	
	public Character character() {
		return character;
	}
}
