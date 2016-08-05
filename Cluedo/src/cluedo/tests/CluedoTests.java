package cluedo.tests;

import cluedo.game.*;
import cluedo.game.Character;
import cluedo.ui.TestUI;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class CluedoTests {
	/**
	 * Testing that the Board constructor makes a Character for each CharacterName.
	 */
	@Test
	public void test01() {
		Board b = new Board();
		ArrayList<Character> chars = b.getCharacters();
		Character.CharacterName[] names = Character.CharacterName.values();
		
		assertTrue(chars.size() == names.length);
		
		for (int i = 0; i < names.length; i++) {
			for (Character c : chars) {
				if (names[i].toString().equals(c.name())) {
					chars.remove(c);
					break;
				}
			}
		}
		
		assertTrue(chars.isEmpty());
	}
	
	/**
	 * Testing that the Board constructor makes a Weapon for each WeaponName.
	 */
	@Test
	public void test02() {
		Board b = new Board();
		ArrayList<Weapon> weapons = b.getWeapons();
		Weapon.WeaponName[] names = Weapon.WeaponName.values();
		
		assertTrue(weapons.size() == names.length);
		
		for (int i = 0; i < names.length; i++) {
			for (Weapon w : weapons) {
				if (names[i].toString().equals(w.name())) {
					weapons.remove(w);
					break;
				}
			}
		}
		
		assertTrue(weapons.isEmpty());
	}
	
	/**
	 * Testing that the Board constructor makes a Room for each RoomName.
	 */
	@Test
	public void test03() {
		Board b = new Board();
		ArrayList<Room> rooms = b.getRooms();
		Room.RoomName[] names = Room.RoomName.values();
		
		assertTrue(rooms.size() == names.length);
		
		for (int i = 0; i < names.length; i++) {
			for (Room r : rooms) {
				if (names[i].toString().equals(r.name())) {
					rooms.remove(r);
					break;
				}
			}
		}
		
		assertTrue(rooms.isEmpty());
	}
	
	/**
	 * Testing that the Board constructor makes a Card for each GamePiece.
	 */
	@Test
	public void test04() {
		Board b = new Board();
		
		ArrayList<Card> pack = b.getPack();
		
		ArrayList<Room> rooms = b.getRooms();
		ArrayList<Character> chars = b.getCharacters();
		ArrayList<Weapon> weapons = b.getWeapons();
		
		assertTrue(pack.size() == (rooms.size() + chars.size() + weapons.size()));

		for (Room r : rooms) {
			for (Card c : pack) {
				if (r.name().equals(c.name())) {
					pack.remove(c);
					break;
				}
			}
		}
		
		assertTrue(pack.size() == (chars.size() + weapons.size()));
		
		for (Character ch : chars) {
			for (Card c : pack) {
				if (ch.name().equals(c.name())) {
					pack.remove(c);
					break;
				}
			}
		}
		
		assertTrue(pack.size() == weapons.size());
		
		for (Weapon w : weapons) {
			for (Card c : pack) {
				if (w.name().equals(c.name())) {
					pack.remove(c);
					break;
				}
			}
		}
		
		assertTrue(pack.isEmpty());
	}
	
	/**
	 * Tests whether Player names are stored right.
	 */
	@Test
	public void test05() {
		
	}
}
