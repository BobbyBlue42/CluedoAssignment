package cluedo.game;

public class RoomCard extends Card {
	private Room room;
	
	public RoomCard(Room room, boolean isMurderComponent) {
		this.room = room;
		this.isMurderComponent = isMurderComponent;
	}
	
	public String name() {
		return room.name().toString();
	}
}
