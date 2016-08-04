package cluedo.game;

public class WeaponCard extends Card {
	private Weapon weapon;
	
	public WeaponCard(Weapon weapon, boolean isMurderComponent) {
		this.weapon = weapon;
		this.isMurderComponent = isMurderComponent;
	}
	
	public String name() {
		return weapon.name().toString();
	}
	
	public Weapon weapon() {
		return weapon;
	}
}
