package cluedo.tests;

import cluedo.game.*;
import cluedo.game.Character;

import static org.junit.Assert.*;
import org.junit.rules.Timeout;

import java.util.ArrayList;

import org.junit.Test;

public class CluedoTests {
	/**
	 * Testing that the Board constructor makes a Character for each CharacterName.
	 */
	@Test
	public void test01() {
		Board b = new Board(true);
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
		Board b = new Board(true);
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
		Board b = new Board(true);
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
		Board b = new Board(true);
		
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
	 * Tests whether the Character data is stored right.
	 */
	@Test
	public void test05() {
		Character.CharacterName name = Character.CharacterName.MISS_SCARLETT;
		Character c = new Character(name, name.getRow(), name.getCol());
		
		assertEquals("Miss Scarlett", c.name());
		assertEquals(name.ordinal(), c.toInt());
		
		Player p = new Player("Bob");
		assertNull(c.player());
		c.assignTo(p);
		assertEquals(p, c.player());
		
		assertNull(c.location());
		
		Room r = new Room(Room.RoomName.KITCHEN);
		c.enterRoom(r);
		assertEquals(r, c.location());
		
		c.leaveRoom();
		assertNull(c.location());
		
		assertEquals(name.getRow(), c.getRow());
		assertEquals(name.getCol(), c.getCol());
		c.setRow(c.getRow()+1);
		c.setCol(c.getCol()+1);
		assertEquals((name.getRow()+1), c.getRow());
		assertEquals((name.getCol()+1), c.getCol());
		assertEquals(name.getRow(), c.getStartRow());
		assertEquals(name.getCol(), c.getStartCol());
	}
	
	/**
	 * Tests whether Room data is stored right.
	 */
	@Test
	public void test06() {
		Room.RoomName name = Room.RoomName.KITCHEN;
		Room r = new Room(name);
		
		assertEquals("Kitchen", r.name());
		assertEquals(name.ordinal(), r.toInt());
		
		assertFalse(r.hasConnection());
		assertNull(r.connection());
		Room other = new Room(Room.RoomName.BALLROOM);
		r.connectTo(other);
		assertTrue(r.hasConnection());
		assertEquals(other, r.connection());
		
		assertTrue(r.canEnter(8, 4, 7, 4));
		assertFalse(r.canEnter(8, 3, 7, 3));
		
		assertTrue(r.canLeave(Board.Direction.DOWN));
		assertFalse(r.canLeave(Board.Direction.RIGHT));
		
		Character c = new Character(Character.CharacterName.MISS_SCARLETT, 0, 0);
		assertFalse(r.hasChar(5, 4));	// should not be displaying anything yet
		assertTrue(r.getCharacters().isEmpty());
		
		r.addCharacter(c);
		assertTrue(r.getCharacters().contains(c));
		assertFalse(r.hasChar(5, 4));	// character should not show up if they do not have a player
		
		Player p = new Player("Bob");
		p.chooseCharacter(c);
		c.assignTo(p);
		assertTrue(r.hasChar(5, 4));	// character should show up now
		
		p.die();
		assertFalse(r.hasChar(5, 4));	// character should not show up if the player is dead
		
		Weapon w = new Weapon(Weapon.WeaponName.DAGGER, r);
		assertFalse(r.hasChar(6, 4));
		assertTrue(r.getWeapons().isEmpty());
		
		r.addWeapon(w);
		assertTrue(r.getWeapons().contains(w));
		assertTrue(r.hasChar(6, 4));
		
		assertEquals(new java.awt.Point(8, 4), r.getExitPoint(Board.Direction.DOWN, new Board(true)));
	}
	
	/**
	 * Tests whether Weapon data is stored right.
	 */
	@Test
	public void test07() {
		Weapon.WeaponName name = Weapon.WeaponName.DAGGER;
		Room r = new Room(Room.RoomName.KITCHEN);
		Weapon w = new Weapon(name, r);
		
		assertEquals("Dagger", w.name());
		
		assertEquals('D', w.toChar());
		
		assertEquals(r, w.location());
		r = new Room(Room.RoomName.BALLROOM);
		w.moveToRoom(r);
		assertEquals(r, w.location());
	}
	
	/**
	 * Tests whether Player data is stored right.
	 */
	@Test
	public void test08() {
		Character.CharacterName cName = Character.CharacterName.MISS_SCARLETT;
		Character c = new Character(cName, cName.getRow(), cName.getCol());
		
		Room r = new Room(Room.RoomName.KITCHEN);
		
		Card c1 = new Card(r, false);
		Card c2 = new Card(c, false);
		Card c3 = new Card(new Weapon(Weapon.WeaponName.DAGGER, r), false);
		
		Player p = new Player("Bob");
		p.chooseCharacter(c);
		
		p.deal(c1);
		p.deal(c2);
		p.deal(c3);
		
		assertEquals("Bob", p.name());
		
		assertEquals(c1, p.hand()[0]);
		assertEquals(c2, p.hand()[1]);
		assertEquals(c3, p.hand()[2]);
		
		assertEquals(c, p.character());
		
		assertTrue(p.isAlive());
		p.die();
		assertFalse(p.isAlive());
	}
	
	/**
	 * Tests startup phase of the Board.
	 */
	@Test
	public void test09() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		ArrayList<Player> players = b.getPlayers();
		
		assertEquals(3, players.size());
		
		assertEquals("Bob", players.get(0).name());
		assertEquals("Miss Scarlett", players.get(0).character().name());
		assertEquals(6, players.get(0).hand().length);
		
		assertEquals("Ted", players.get(1).name());
		assertEquals("Professor Plum", players.get(1).character().name());
		assertEquals(6, players.get(1).hand().length);
		
		assertEquals("Bill", players.get(2).name());
		assertEquals("Colonel Mustard", players.get(2).character().name());
		assertEquals(6, players.get(2).hand().length);
		
		assertEquals(0, b.getFaceUpCards().size());		// 18 cards should be evenly dealt without any left over
		
		int wepCount = 0;
		for (Room r : b.getRooms()) {
			if (r.getWeapons().size() == 1) {
				wepCount++;
			} else if (r.getWeapons().size() > 1) {
				// cannot have more than 1 weapon per room at game start
				fail();
			}
		}
		assertEquals(6, wepCount);
	}
	
	/**
	 * Tests basic movement.
	 */
	@Test
	public void test10() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		
		int row = currentPlayer.character().getRow();
		int col = currentPlayer.character().getCol();
		
		ui.addAnswer(1);	// move
		ui.addAnswer(1);	// up
		b.playTurn(currentPlayer, 1);
		assertEquals(row-1, currentPlayer.character().getRow());
		assertEquals(col, currentPlayer.character().getCol());
		
		ui.addAnswer(1);	// move
		ui.addAnswer(2);	// right
		b.playTurn(currentPlayer, 1);
		assertEquals(row-1, currentPlayer.character().getRow());
		assertEquals(col+1, currentPlayer.character().getCol());
		
		ui.addAnswer(1);	// move
		ui.addAnswer(4);	// left
		b.playTurn(currentPlayer, 1);
		assertEquals(row-1, currentPlayer.character().getRow());
		assertEquals(col, currentPlayer.character().getCol());
		
		ui.addAnswer(1);	// move
		ui.addAnswer(3);	// down
		b.playTurn(currentPlayer, 1);
		assertEquals(row, currentPlayer.character().getRow());
		assertEquals(col, currentPlayer.character().getCol());
	}
	
	/**
	 * Tests moving out of the grid (should not work).
	 */
	@Test
	public void test11() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		int row = currentPlayer.character().getRow();
		int col = currentPlayer.character().getCol();
		
		// Player 1 (or rather, Miss Scarlett) should only be able to move upwards on their first turn.
		ui.addAnswer(1);	// move
		ui.addAnswer(4);	// left - try to go into empty square, shouldn't work
		ui.addAnswer(1);	// move
		ui.addAnswer(3);	// down - try to go out of the array, shouldn't work
		ui.addAnswer(1);	// move
		ui.addAnswer(2);	// right - try to go into empty square, shouldn't work
		ui.addAnswer(1);	// move
		ui.addAnswer(1);	// up
		
		b.playTurn(currentPlayer, 1);
		assertEquals(row-1, currentPlayer.character().getRow());
		assertEquals(col, currentPlayer.character().getCol());
	}
	
	/**
	 * Tests moving through walls into rooms (should not work).
	 */
	@Test
	public void test12() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		int row = currentPlayer.character().getRow();
		int col = currentPlayer.character().getCol();
		
		// Player 1 (or rather, Miss Scarlett) should not be able to enter the
		// room on her left at this point
		ui.addAnswer(1);	// move
		ui.addAnswer(1);	// up
		ui.addAnswer(1);	// move
		ui.addAnswer(4);	// left - try to enter the room, shouldn't work
		ui.addAnswer(1);	// move
		ui.addAnswer(1);	// up
		
		b.playTurn(currentPlayer, 2);
		assertEquals(row-2, currentPlayer.character().getRow());
		assertEquals(col, currentPlayer.character().getCol());
	}
	
	/**
	 * Tests hypothesising outside a room (should not work).
	 */
	@Test(timeout=3000)
	public void test13() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		int row = currentPlayer.character().getRow();
		int col = currentPlayer.character().getCol();
		
		// If hypothesising works, there will not be enough answers in the UI,
		// so a timeout is necessary. There is no way this test should take longer
		// than 3 seconds.
		ui.addAnswer(2);	// hypothesise
		ui.addAnswer(1);	// move
		ui.addAnswer(1);	// up
		
		b.playTurn(currentPlayer, 1);
		assertEquals(row-1, currentPlayer.character().getRow());
		assertEquals(col, currentPlayer.character().getCol());
	}
	
	/**
	 * Tests entering rooms and hypothesising.
	 */
	@Test
	public void test14() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		
		for (int i = 0; i < 6; i++) {
			ui.addAnswer(1);	// move
			ui.addAnswer(1);	// up
		}
		ui.addAnswer(1);	// move
		ui.addAnswer(4);	// left
		ui.addAnswer(1);	// move
		ui.addAnswer(3);	// down - enter room
		ui.addAnswer(2);	// hypothesise
		ui.addAnswer(1);	// choose one of the characters
		ui.addAnswer(1);	// choose one of the weapons
		ui.addAnswer(false);	// no - do not want to look at cards
		ui.addAnswer(5);	// debug/test dispute option
		
		b.playTurn(currentPlayer, 8);
		assertTrue(currentPlayer.character().location() != null);
	}
	
	/**
	 * Tests hypothesising and bringing characters and weapons to the room.
	 */
	@Test
	public void test15() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);

		for (int i = 0; i < 6; i++) {
			ui.addAnswer(1);	// move
			ui.addAnswer(1);	// up
		}
		ui.addAnswer(1);	// move
		ui.addAnswer(4);	// left
		ui.addAnswer(1);	// move
		ui.addAnswer(3);	// down - enter room
		ui.addAnswer(2);	// hypothesise
		ui.addAnswer(6);	// choose Colonel Mustard
		
		Room r = b.getRoomByCode(9);
		
		ArrayList<Character> chars = r.getCharacters();
		ArrayList<Weapon> weapons = r.getWeapons();
		
		int charSize = chars.size();
		int wepSize = weapons.size();
		int weapon = 1;		// by default choose the candlestick
		if (wepSize > 0 && weapons.get(0).name().equals("Candlestick")) {
			// but if the candlestick is already in the room,
			weapon = 2;		// choose the dagger
		}
		ui.addAnswer(weapon);
		ui.addAnswer(false);	// no - do not want to look at cards
		ui.addAnswer(5);	// debug/test auto-dispute option

		b.playTurn(currentPlayer, 8);

		assertEquals(charSize+2, chars.size());
		// Player 3 chose Colonel Mustard, so can test whether P3's character is in the room
		assertTrue(chars.contains(b.getPlayers().get(2).character()));
		assertEquals(wepSize+1, weapons.size());
		if (weapon == 1) {
			assertTrue(weapons.get(weapons.size()-1).name().equals("Candlestick"));
		} else {
			assertTrue(weapons.get(weapons.size()-1).name().equals("Dagger"));
		}
	}
	
	/**
	 * Tests leaving rooms normally.
	 */
	@Test
	public void test16() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		int row = currentPlayer.character().getRow();
		int col = currentPlayer.character().getCol();
		
		for (int i = 0; i < 6; i++) {
			ui.addAnswer(1);	// move
			ui.addAnswer(1);	// up
		}
		ui.addAnswer(1);	// move
		ui.addAnswer(4);	// left
		ui.addAnswer(1);	// move
		ui.addAnswer(3);	// down - enter room
		ui.addAnswer(2);	// hypothesise
		ui.addAnswer(1);	// choose one of the characters
		ui.addAnswer(1);	// choose one of the weapons
		ui.addAnswer(false);	// no - do not want to look at cards
		ui.addAnswer(5);	// debug/test dispute option
		// next turn
		ui.addAnswer(1);	// move
		ui.addAnswer(1);	// up
		
		b.playTurn(currentPlayer, 8);
		b.playTurn(currentPlayer, 1);
		
		assertNull(currentPlayer.character().location());
		assertFalse(b.getRoomByCode(9).getCharacters().contains(currentPlayer.character()));
		assertEquals(row-6, currentPlayer.character().getRow());
		assertEquals(col-1, currentPlayer.character().getCol());
	}
	
	/**
	 * Tests leaving rooms through walls (should not work).
	 */
	@Test
	public void test17() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		int row = currentPlayer.character().getRow();
		int col = currentPlayer.character().getCol();
		
		for (int i = 0; i < 6; i++) {
			ui.addAnswer(1);	// move
			ui.addAnswer(1);	// up
		}
		ui.addAnswer(1);	// move
		ui.addAnswer(4);	// left
		ui.addAnswer(1);	// move
		ui.addAnswer(3);	// down - enter room
		ui.addAnswer(2);	// hypothesise
		ui.addAnswer(1);	// choose one of the characters
		ui.addAnswer(1);	// choose one of the weapons
		ui.addAnswer(false);	// no - do not want to look at cards
		ui.addAnswer(5);	// debug/test dispute option
		// next turn
		ui.addAnswer(1);	// move
		ui.addAnswer(2);	// right - should not work
		ui.addAnswer(1);	// move
		ui.addAnswer(1);	// up
		
		b.playTurn(currentPlayer, 8);
		b.playTurn(currentPlayer, 1);
		
		assertNull(currentPlayer.character().location());
		assertFalse(b.getRoomByCode(9).getCharacters().contains(currentPlayer.character()));
		assertEquals(row-6, currentPlayer.character().getRow());
		assertEquals(col-1, currentPlayer.character().getCol());
	}
	
	/**
	 * Tests leaving rooms via secret passageways.
	 */
	@Test
	public void test18() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		
		for (int i = 0; i < 6; i++) {
			ui.addAnswer(1);	// move
			ui.addAnswer(1);	// up
		}
		ui.addAnswer(1);	// move
		ui.addAnswer(4);	// left
		ui.addAnswer(1);	// move
		ui.addAnswer(3);	// down - enter room
		ui.addAnswer(2);	// hypothesise
		ui.addAnswer(1);	// choose one of the characters
		ui.addAnswer(1);	// choose one of the weapons
		ui.addAnswer(false);	// no - do not want to look at cards
		ui.addAnswer(5);	// debug/test dispute option
		// next turn
		ui.addAnswer(5);	// take secret passageway
		ui.addAnswer(2);	// hypothesise
		ui.addAnswer(1);	// choose one of the characters
		ui.addAnswer(1);	// choose one of the weapons
		ui.addAnswer(false);	// no - do not want to look at cards
		ui.addAnswer(5);	// debug/test dispute option
		
		b.playTurn(currentPlayer, 8);
		b.playTurn(currentPlayer, 1);
		
		assertFalse(b.getRoomByCode(9).getCharacters().contains(currentPlayer.character()));
		assertTrue(b.getRoomByCode(4).getCharacters().contains(currentPlayer.character()));
	}
	
	/**
	 * Tests leaving rooms when given multiple exit choices.
	 */
	@Test
	public void test19() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		int row = currentPlayer.character().getRow();
		int col = currentPlayer.character().getCol();
		
		for (int i = 0; i < 7; i++) {
			ui.addAnswer(1);	// move
			ui.addAnswer(1);	// up
		}
		for (int i = 0; i < 4; i++) {
			ui.addAnswer(1);	// move
			ui.addAnswer(2);	// right
		}
		ui.addAnswer(1);	// move
		ui.addAnswer(3);	// down - enter room
		ui.addAnswer(2);	// hypothesise
		ui.addAnswer(1);	// choose one of the characters
		ui.addAnswer(1);	// choose one of the weapons
		ui.addAnswer(false);	// no - do not want to look at cards
		ui.addAnswer(5);	// debug/test dispute option
		// next turn
		ui.addAnswer(1);	// move
		ui.addAnswer(1);	// up
		ui.addAnswer(2);	// choose the second of the two exit options
		
		b.playTurn(currentPlayer, 12);
		b.playTurn(currentPlayer, 1);
		
		assertEquals(row-7, currentPlayer.character().getRow());
		assertEquals(col+5, currentPlayer.character().getCol());
	}
	
	/**
	 * Tests correctly accusing and winning.
	 */
	@Test
	public void test20() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		
		ArrayList<Card> envelope = b.getEnvelope();
		Character murderChar = (Character)envelope.get(0).piece();
		Weapon murderWeapon = (Weapon)envelope.get(1).piece();
		Room murderRoom = (Room)envelope.get(2).piece();
		
		ui.addAnswer(3);	// make accusation
		ui.addAnswer(murderRoom.toInt()+1);		// state the room the murder happened in
		ui.addAnswer(murderChar.toInt()+1);		// accuse the character that committed the murder
		ui.addAnswer(murderWeapon.toInt()+1);	// state the weapon they used to commit the murder
		
		b.playTurn(currentPlayer, 1);
		
		assertTrue(currentPlayer.isAlive());
		assertTrue(b.gameOver());
	}
	
	/**
	 * Tests falsely accusing and dying.
	 */
	@Test
	public void test21() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		
		ArrayList<Card> envelope = b.getEnvelope();
		Character murderChar = (Character)envelope.get(0).piece();
		Weapon murderWeapon = (Weapon)envelope.get(1).piece();
		Room murderRoom = (Room)envelope.get(2).piece();
		
		ui.addAnswer(3);	// make accusation
		int room = murderRoom.toInt();		// state an incorrect room that the murder happened in
		if (room == 0)	room = 2;			// if room answer is 0, correct option is 1, so choose 2
		ui.addAnswer(room);
		ui.addAnswer(murderChar.toInt()+1);		// accuse the character that committed the murder
		ui.addAnswer(murderWeapon.toInt()+1);	// state the weapon they used to commit the murder
		
		b.playTurn(currentPlayer, 1);
		
		assertFalse(currentPlayer.isAlive());
		assertFalse(b.gameOver());
	}
	
	/**
	 * Tests everyone falsely accusing and the last player standing being the winner.
	 */
	@Test
	public void test22() {
		Board b = new Board(true);
		TestUI ui = new TestUI();
		setupBoard(b, ui);
		
		Player currentPlayer = nextPlayer(b);
		
		ArrayList<Card> envelope = b.getEnvelope();
		Character murderChar = (Character)envelope.get(0).piece();
		Weapon murderWeapon = (Weapon)envelope.get(1).piece();
		Room murderRoom = (Room)envelope.get(2).piece();
		
		int room = murderRoom.toInt();		// state an incorrect room that the murder happened in
		if (room == 0) room = 2;			// if room answer is 0, correct option is 1, so choose 2
		int character = murderChar.toInt()+1;	// accuse the character that committed the murder
		int weapon = murderWeapon.toInt()+1;	// state the weapon they used to commit the murder

		ui.addAnswer(3);	// make accusation
		ui.addAnswer(room);
		ui.addAnswer(character);
		ui.addAnswer(weapon);
		
		b.playTurn(currentPlayer, 1);
		
		assertFalse(currentPlayer.isAlive());
		assertFalse(b.gameOver());
		
		currentPlayer = nextPlayer(b);

		ui.addAnswer(3);	// make accusation
		ui.addAnswer(room);
		ui.addAnswer(character);
		ui.addAnswer(weapon);
		
		b.playTurn(currentPlayer, 1);
		
		assertFalse(currentPlayer.isAlive());
		assertTrue(b.gameOver());
	}
	
	
	private void setupBoard(Board b, TestUI ui) {
		b.setUI(ui);
		
		ui.addAnswer(3);		// 3 players
		ui.addAnswer("Bob");	// Player 1's name is Bob
		ui.addAnswer(1);		// Bob chooses Miss Scarlett
		ui.addAnswer("Ted");	// Player 2's name is Ted
		ui.addAnswer(1);		// Ted chooses Professor Plum
		ui.addAnswer("Bill");	// Player 3's name is Bill
		ui.addAnswer(4);		// Bill chooses Colonel Mustard
		
		b.startGame();
	}
	
	private Player nextPlayer(Board b) {
		ArrayList<Player> players = b.getPlayers();
		Player currentPlayer = b.getCurrentPlayer();
		
		if (currentPlayer == null) {
			return players.get(0);
		}
		
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).equals(currentPlayer)) {
				i++;
				if (i == players.size()) {
					return players.get(0);
				} else {
					return players.get(i);
				}
			}
		}
		
		return null;	// should not be able to reach here
	}
}
