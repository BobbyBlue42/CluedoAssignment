package cluedo.game;

public class Weapon {
	public enum WeaponName {
		CANDLESTICK('C'),
		DAGGER('D'),
		LEAD_PIPE('P'),
		REVOLVER('G'),
		ROPE('R'),
		SPANNER('S');
		
		private char c;
		
		private WeaponName(char c) {
			this.c = c;
		}
		
		public char toChar() {
			return c;
		}
		
		public String toString() {
			char[] name = this.name().replaceAll("_", " ").toCharArray();
			boolean startOfWord = true;
			
			for (int i = 0; i < name.length; i++) {
				if (startOfWord) {
					startOfWord = false;
				} else {
					if (name[i] == ' ') {
						startOfWord = true;
					} else {
						name[i] = java.lang.Character.toLowerCase(name[i]);
					}
				}
			}
			
			return new String(name);
		}
	}
	
	private WeaponName name;
	private Room location;
	
	public Weapon(WeaponName name, Room room) {
		this.name = name;
		location = room;
	}
	
	public WeaponName name() {
		return name;
	}
	
	public Room location() {
		return location;
	}
	
	public void moveToRoom(Room r) {
		location = r;
	}
}
