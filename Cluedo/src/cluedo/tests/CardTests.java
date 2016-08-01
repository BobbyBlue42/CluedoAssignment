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
	
	@Test
	public void testCharacterCard() {
		setup();
		
		c1 = new CharacterCard(ch1, true);
		c2 = new CharacterCard(ch2, false);
		
		assertTrue(c1.isMurderComponent());
		assertFalse(c2.isMurderComponent());
		assertEquals("Colonel Mustard", c1.name());
		assertEquals("Miss Scarlet", c2.name());
		
		board.deal(c1, p1);
		board.deal(c2, p2);
		
		assertEquals(p1, c1.holder());
		assertEquals(p2, c2.holder());
		
		assertTrue(p1.isHolding(c1));
		assertTrue(p2.isHolding(c2));
	}
	
	@Test
	public void testRoomCard() {
		setup();
		
		c1 = new RoomCard(r1, true);
		c2 = new RoomCard(r2, false);
		
		assertTrue(c1.isMurderComponent());
		assertFalse(c2.isMurderComponent());
		assertEquals("Ballroom", c1.name());
		assertEquals("Billiard Room", c2.name());
		
		board.deal(c1, p1);
		board.deal(c2, p2);
		
		assertEquals(p1, c1.holder());
		assertEquals(p2, c2.holder());
		
		assertTrue(p1.isHolding(c1));
		assertTrue(p2.isHolding(c2));
	}
	
	@Test
	public void testWeaponCard() {
		setup();
		
		c1 = new WeaponCard(w1, true);
		c2 = new WeaponCard(w2, false);
		
		assertTrue(c1.isMurderComponent());
		assertFalse(c2.isMurderComponent());
		assertEquals("Candlestick", c1.name());
		assertEquals("Lead Pipe", c2.name());
		
		board.deal(c1, p1);
		board.deal(c2, p2);
		
		assertEquals(p1, c1.holder());
		assertEquals(p2, c2.holder());
		
		assertTrue(p1.isHolding(c1));
		assertTrue(p2.isHolding(c2));
	}
	
	private void setup() {
		board = new Board();
		p1 = new Player("Foo");
		p2 = new Player("Bar");
		
		Character.CharacterName m = Character.CharacterName.COLONEL_MUSTARD;
		ch1 = new Character(m, m.getRow(), m.getCol());
		ch1.assignTo(p1);
		Character.CharacterName s = Character.CharacterName.MISS_SCARLET;
		ch2 = new Character(s, s.getRow(), s.getCol());
		ch2.assignTo(p2);
		
		r1 = new Room(Room.RoomName.BALLROOM);
		r2 = new Room(Room.RoomName.BILLIARD_ROOM);
		
		w1 = new Weapon(Weapon.WeaponName.CANDLESTICK, r1);
		w2 = new Weapon(Weapon.WeaponName.LEAD_PIPE, r2);
	}
}
