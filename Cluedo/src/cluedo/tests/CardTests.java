package cluedo.tests;

import cluedo.game.*;
import cluedo.game.Character;

import static org.junit.Assert.*;
import org.junit.Test;

public class CardTests {
	private Board board;
	private Player p1, p2;
	private Character ch1, ch2;
	private Room r1, r2;
	private Weapon w1, w2;
	private Card c1, c2;
	
	// TODO: implement some tests
	
	private void setup() {
		board = new Board();
		p1 = new Player("Foo");
		p2 = new Player("Bar");
		
		Character.CharacterName m = Character.CharacterName.COLONEL_MUSTARD;
		ch1 = new Character(m, m.getRow(), m.getCol());
		ch1.assignTo(p1);
		Character.CharacterName s = Character.CharacterName.MISS_SCARLETT;
		ch2 = new Character(s, s.getRow(), s.getCol());
		ch2.assignTo(p2);
		
		r1 = new Room(Room.RoomName.BALLROOM);
		r2 = new Room(Room.RoomName.BILLIARD_ROOM);
		
		w1 = new Weapon(Weapon.WeaponName.CANDLESTICK, r1);
		w2 = new Weapon(Weapon.WeaponName.LEAD_PIPE, r2);
	}
}
