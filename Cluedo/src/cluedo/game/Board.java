package cluedo.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cluedo.gui.GraphicsUI;

/**
 * Stores all the information or references required for a game of Cluedo to complete.
 * Handles all the main logic of the game, as well as the interfacing with the UI.
 * 
 * @author Louis Thie
 */
public class Board {
	/**
	 * Represents a direction for the player to move in
	 * 
	 * @author Louis Thie
	 */
	public enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT
	}
	
	private ArrayList<Player> players;
	private Player currentPlayer;
	private ArrayList<Character> characters;
	private ArrayList<Room> rooms;
	private ArrayList<Weapon> weapons;
	private ArrayList<Card> pack;
	private ArrayList<Card> envelope;
	private ArrayList<Card> faceUpCards = new ArrayList<Card>();
	
	private GraphicsUI gui;
	
	/**
	 * 0 = empty
	 * 1 = corridor
	 * 2 = Kitchen
	 * 3 = Ballroom
	 * 4 = Conservatory
	 * 5 = Billiard Room
	 * 6 = Dining Room
	 * 7 = Library
	 * 8 = Hall
	 * 9 = Lounge
	 * 10 = Study
	 * 11 = NW/SE stairs
	 * 12 = NE/SW stairs
	 */
	private int[][] grid;
	
	/**
	 * 0 = empty
	 * 1 = Miss Scarlett
	 * 2 = Professor Plum
	 * 3 = Mrs. Peacock
	 * 4 = Reverend Green
	 * 5 = Mrs. White
	 * 6 = Colonel Mustard
	 */
	private int[][] playerGrid;
	
	private char[][] roomNames;
	
	private Random rand;
	private boolean gameOver = false;
	
	private Room origin;
	private int diceRoll;
	private int remainingMoves;
	
	/**
	 * Constructs a Board object.
	 */
	public Board() {
		players = new ArrayList<Player>();
		rand = new Random(System.currentTimeMillis());
		
		setupGame();
	}
	
	private void setupGame() {
		setupChars();
		setupRooms();
		setupWeapons();
		setupPack();
		setupGrids();
	}
	
	private void setupChars() {
		characters = new ArrayList<Character>();
		
		for (Character.CharacterName name : Character.CharacterName.values()) {
			characters.add(new Character(name, name.getRow(), name.getCol()));
		}
	}
	
	private void setupRooms() {
		rooms = new ArrayList<Room>();
		Room kitchen = null, study = null, lounge = null, conservatory = null;
		
		for (Room.RoomName name : Room.RoomName.values()) {
			rooms.add(new Room(name));
			switch (name) {
			case KITCHEN:
				kitchen = rooms.get(rooms.size()-1);
				break;
			case STUDY:
				study = rooms.get(rooms.size()-1);
				break;
			case LOUNGE:
				lounge = rooms.get(rooms.size()-1);
				break;
			case CONSERVATORY:
				conservatory = rooms.get(rooms.size()-1);
				break;
			default:
				break;
			}
		}
		
		// should not technically need this,
		// but my laptop would not compile without it for some reason
		if (kitchen == null || study == null || lounge == null || conservatory == null)
			throw new RuntimeException("The linked rooms shouldn't be null at this point.");
		
		kitchen.connectTo(study);
		study.connectTo(kitchen);
		lounge.connectTo(conservatory);
		conservatory.connectTo(lounge);
	}
	
	private void setupWeapons() {
		weapons = new ArrayList<Weapon>();
		
		ArrayList<Integer> roomNums = new ArrayList<Integer>();
		
		Weapon.WeaponName[] names = Weapon.WeaponName.values();
		
		for (int i = 0; i < names.length; i++) {
			int randNum = rand.nextInt(rooms.size());
			if (!roomNums.contains(randNum)) {
				roomNums.add(randNum);
			} else {
				i--;	// cancels out with the i++ at the end of this iteration
						// means there does not need to be a nested loop
			}
		}
		
		for (int i = 0; i < names.length; i++) {
			weapons.add(new Weapon(names[i], rooms.get(roomNums.get(i))));
		}
	}
	
	private void setupPack() {
		pack = new ArrayList<Card>();
		envelope = new ArrayList<Card>(3);
		
		int murderChar = rand.nextInt(characters.size());
		int murderWeapon = rand.nextInt(weapons.size());
		int murderRoom = rand.nextInt(rooms.size());
		
		for (int i = 0; i < characters.size(); i++) {
			pack.add(new Card(characters.get(i), i == murderChar));
			if (i == murderChar)
				envelope.add(pack.get(pack.size()-1));
		}
		
		for (int i = 0; i < weapons.size(); i++) {
			pack.add(new Card(weapons.get(i), i == murderWeapon));
			if (i == murderWeapon)
				envelope.add(pack.get(pack.size()-1));
		}
		
		for (int i = 0; i < rooms.size(); i++) {
			pack.add(new Card(rooms.get(i), i == murderRoom));
			if (i == murderRoom)
				envelope.add(pack.get(pack.size()-1));
		}
	}
	
	/**
	 * Set the UI to be used for this game.
	 * 
	 * @param ui	the UI to use
	 */
	public void setUI(GraphicsUI gui) {
		this.gui = gui;
	}
	
	/**
	 * Returns the Player whose turn it currently is.
	 * 
	 * @return	the current Player
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public int getDiceRoll() {
		return diceRoll;
	}
	
	public int getRemainingMoves() {
		return remainingMoves;
	}
	
	public void setRemainingMoves(int num) {
		remainingMoves = num;
	}
	
	public void decrementMoves() {
		remainingMoves--;
	}
	
	/**
	 * Handles the movement of Characters (not including movement through
	 * secret passageways or movement due to a hypothesis). Also checks the 
	 * validity of all moves, including not allowing players to re-enter the
	 * same Room in the same turn.
	 * 
	 * @param c			the Character to move
	 * @param d			the Direction to move in
	 * @param origin	the Room the Character was in at the start of the turn
	 * @return			true if the Character was moved, false otherwise
	 */
	public boolean move(Character c, Direction d, Room origin) {
		if (c != null) {
			int row = c.getRow();
			int col = c.getCol();
			
			if (d.equals(Direction.UP)) {
				if (row == 0) {
					// trying to move out of the array
					gui.notification("Sorry, you cannot move in that direction");
					return false;
				}
				if (grid[row][col] == 1 
						&& grid[row-1][col] == 1
						&& playerGrid[row-1][col] == 0) {	// cannot move through other characters
					// in a corridor
					playerGrid[row-1][col] = playerGrid[row][col];
					playerGrid[row][col] = 0;
					c.setRow(row-1);
					return true;
				} else if (grid[row][col] != 1) {
					// inside a room - covered by the gui now
					/*Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, this);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.toInt() + 1;
							c.leaveRoom();
							c.setRow(exitRow);
							c.setCol(exitCol);
							r.removeCharacter(c);
							return true;
						} else {
							gui.notification("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}*/
				} else if (grid[row-1][col] != 1) {
					if (grid[row-1][col] == 0) {
						// trying to move out of the array
						gui.notification("Sorry, you cannot move in that direction");
						return false;
					}
					// trying to enter a room
					Room r = getRoomByCode(grid[row-1][col]);
					if (r.equals(origin)) {
						gui.notification("Sorry, you cannot re-enter a room you left this turn.");
						return false;
					}
					if (r.canEnter(row, col, row-1, col)) {
						playerGrid[row][col] = 0;
						r.addCharacter(c);
						c.enterRoom(r);
						return true;
					}
				}
			} else if (d.equals(Direction.DOWN)) {
				if (row == grid.length-1) {
					// trying to move out of the array
					gui.notification("Sorry, you cannot move in that direction");
					return false;
				}
				if (grid[row][col] == 1 
						&& grid[row+1][col] == 1
						&& playerGrid[row+1][col] == 0) {	// cannot move through other characters
					// in a corridor
					playerGrid[row+1][col] = playerGrid[row][col];
					playerGrid[row][col] = 0;
					c.setRow(row+1);
					return true;
				} else if (grid[row][col] != 1) {
					// inside a room - covered by the gui now
					/*Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, this);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.toInt() + 1;
							c.leaveRoom();
							c.setRow(exitRow);
							c.setCol(exitCol);
							r.removeCharacter(c);
							return true;
						} else {
							gui.notification("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}*/
				} else if (grid[row+1][col] != 1) {
					if (grid[row+1][col] == 0) {
						// trying to move out of the array
						gui.notification("Sorry, you cannot move in that direction");
						return false;
					}
					// trying to enter a room
					Room r = getRoomByCode(grid[row+1][col]);
					if (r.equals(origin)) {
						gui.notification("Sorry, you cannot re-enter a room you left this turn.");
						return false;
					}
					if (r.canEnter(row, col, row+1, col)) {
						playerGrid[row][col] = 0;
						r.addCharacter(c);
						c.enterRoom(r);
						return true;
					}
				}
			} else if (d.equals(Direction.LEFT)) {
				if (col == 0) {
					// trying to move out of the array
					gui.notification("Sorry, you cannot move in that direction");
					return false;
				}
				if (grid[row][col] == 1 
						&& grid[row][col-1] == 1
						&& playerGrid[row][col-1] == 0) {	// cannot move through other characters
					// in a corridor
					playerGrid[row][col-1] = playerGrid[row][col];
					playerGrid[row][col] = 0;
					c.setCol(col-1);
					return true;
				} else if (grid[row][col] != 1) {
					// inside a room - covered by the gui now
					/*Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, this);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.toInt() + 1;
							c.leaveRoom();
							c.setRow(exitRow);
							c.setCol(exitCol);
							r.removeCharacter(c);
							return true;
						} else {
							gui.notification("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}*/
				} else if (grid[row][col-1] != 1) {
					if (grid[row][col-1] == 0) {
						// trying to move out of the array
						gui.notification("Sorry, you cannot move in that direction");
						return false;
					}
					// trying to enter a room
					Room r = getRoomByCode(grid[row][col-1]);
					if (r.equals(origin)) {
						gui.notification("Sorry, you cannot re-enter a room you left this turn.");
						return false;
					}
					if (r.canEnter(row, col, row, col-1)) {
						playerGrid[row][col] = 0;
						r.addCharacter(c);
						c.enterRoom(r);
						return true;
					}
				}
			} else if (d.equals(Direction.RIGHT)) {
				if (col == grid[row].length-1) {
					// trying to move out of the array
					gui.notification("Sorry, you cannot move in that direction");
					return false;
				}
				if (grid[row][col] == 1 
						&& grid[row][col+1] == 1
						&& playerGrid[row][col+1] == 0) {	// cannot move through other characters
					// in a corridor
					playerGrid[row][col+1] = playerGrid[row][col];
					playerGrid[row][col] = 0;
					c.setCol(col+1);
					return true;
				} else if (grid[row][col] != 1) {
					// inside a room - covered by the gui now
					/*Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, this);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.toInt() + 1;
							c.leaveRoom();
							c.setRow(exitRow);
							c.setCol(exitCol);
							r.removeCharacter(c);
							return true;
						} else {
							gui.notification("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}*/
				} else if (grid[row][col+1] != 0) {
					if (grid[row][col+1] == 0) {
						// trying to move out of the array
						gui.notification("Sorry, you cannot move in that direction");
						return false;
					}
					// trying to enter a room
					Room r = getRoomByCode(grid[row][col+1]);
					if (r.equals(origin)) {
						gui.notification("Sorry, you cannot re-enter a room you left this turn.");
						return false;
					}
					if (r.canEnter(row, col, row, col+1)) {
						playerGrid[row][col] = 0;
						r.addCharacter(c);
						c.enterRoom(r);
						return true;
					}
				}
			}
			
			gui.notification("Sorry, the move you requested is invalid. Please try a different move.");
			return false;
		}
		
		gui.notification("Sorry, there was an error (character was null).");
		return false;
	}
	
	/**
	 * Checks whether the game is over.
	 * 
	 * @return	whether the game is over
	 */
	public boolean gameOver() {
		return gameOver;
	}
	
	/**
	 * Sets up everything directly connected to the players of this game.
	 */
	public void startGame() {
		// Welcome players
		gui.notification("Welcome to Cluedo, the Great Detective Game!");
		// Allow players to choose their characters
		setupPlayers();
		// Deal non-murder-component cards to players
		dealCards();
		// Put the weapons in rooms, maximum one at a time
		setOutWeapons();
	}
	
	public void startTurn() {
		if (currentPlayer == null) currentPlayer = players.get(0);
		
		int aliveCount = 0;
		for (Player p : players) {
			if (p.isAlive()) aliveCount++;
		}
		
		if (aliveCount > 1) {
			diceRoll = rand.nextInt(6) + 1;
			diceRoll += rand.nextInt(6) + 1;
			
			remainingMoves = diceRoll;
			
			origin = currentPlayer.character().location();
		} else {
			gameOver = true;
		}
	}
	
	public void endTurn() {
		int nextPlayer = 0;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).equals(currentPlayer)) {
				nextPlayer = i+1;
			}
		}
		if (nextPlayer == players.size()) nextPlayer = 0;
		currentPlayer = players.get(nextPlayer);
		
		if (!currentPlayer.isAlive()) {
			endTurn();
		} else {
			startTurn();
		}
	}
	
	public void win() {
		gameOver = true;
	}
	
	public ArrayList<Player> getHypoPlayers() {
		ArrayList<Player> ps = new ArrayList<Player>();
		
		boolean found = false;
		for (Player p : players) {
			if (found) ps.add(p);
			if (p.equals(currentPlayer)) found = true;
		}
		for (Player p : players) {
			if (p.equals(currentPlayer)) return ps;
			ps.add(p);
		}
		
		// should be unable to get here, and if we do,
		// there would be an issue with having it 
		return null;
	}
	
	public Room origin() {
		return origin;
	}
	
	private void setupPlayers() {
		int people = gui.getPlayerCount();
		
		for (int i = 0; i < people; i++) {
			HashMap<String,Object> userInfo = gui.askForUserInfo(
					characters.toArray(new Character[6]));
			Player p = new Player((String)userInfo.get("name"));
			p.chooseCharacter((Character)userInfo.get("character"));
			((Character)userInfo.get("character")).assignTo(p);
			
			players.add(p);
		}
		
		setupCharGrid();
	}
	
	private void dealCards() {
		ArrayList<Card> cards = new ArrayList<Card>(18);
		
		for (Card c : pack) {
			if (!c.isMurderComponent()) {
				cards.add(c);
			}
		}
		
		while (cards.size() >= players.size()) {
			for (Player p : players) {
				Card c = cards.remove(rand.nextInt(cards.size()));
				p.deal(c);
			}
		}
		
		// lay any remaining cards face-up on the table
		for (Card c : cards) {
			faceUpCards.add(c);
		}
	}
	
	private void setOutWeapons() {
		ArrayList<Room> roomList = new ArrayList<Room>();
		for (Room r : rooms) {
			roomList.add(r);
		}
		
		for (Weapon w : weapons) {
			int room = rand.nextInt(roomList.size());
			Room r = roomList.remove(room);
			r.addWeapon(w);
			w.moveToRoom(r);
		}
	}
	
	private void setupGrids() {
		int[][] boardArr = {
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
				{2,2,2,2,2,11,0,1,1,1,3,3,3,3,1,1,1,0,4,4,4,4,4,4},
				{2,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,4,4,4,4,4,4},
				{2,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,4,4,4,4,4,4},
				{2,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,4,4,4,4,4,4},
				{2,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,1,4,4,4,12,0},
				{0,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,3,3,3,3,3,3,3,3,1,1,1,1,1,1,1,0},
				{0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,5,5,5,5,5,5},
				{6,6,6,6,6,1,1,1,1,1,1,1,1,1,1,1,1,1,5,5,5,5,5,5},
				{6,6,6,6,6,6,6,6,1,1,0,0,0,0,0,1,1,1,5,5,5,5,5,5},
				{6,6,6,6,6,6,6,6,1,1,0,0,0,0,0,1,1,1,5,5,5,5,5,5},
				{6,6,6,6,6,6,6,6,1,1,0,0,0,0,0,1,1,1,5,5,5,5,5,5},
				{6,6,6,6,6,6,6,6,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,0},
				{6,6,6,6,6,6,6,6,1,1,0,0,0,0,0,1,1,1,7,7,7,7,7,0},
				{6,6,6,6,6,6,6,6,1,1,0,0,0,0,0,1,1,7,7,7,7,7,7,7},
				{0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,7,7,7,7,7,7,7},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,7,7,7,7,7,7,7},
				{0,1,1,1,1,1,1,1,1,8,8,8,8,8,8,1,1,1,7,7,7,7,7,0},
				{12,9,9,9,9,9,9,1,1,8,8,8,8,8,8,1,1,1,1,1,1,1,1,1},
				{9,9,9,9,9,9,9,1,1,8,8,8,8,8,8,1,1,1,1,1,1,1,1,0},
				{9,9,9,9,9,9,9,1,1,8,8,8,8,8,8,1,1,10,10,10,10,10,10,11},
				{9,9,9,9,9,9,9,1,1,8,8,8,8,8,8,1,1,10,10,10,10,10,10,10},
				{9,9,9,9,9,9,9,1,1,8,8,8,8,8,8,1,1,10,10,10,10,10,10,10},
				{9,9,9,9,9,9,0,1,0,8,8,8,8,8,8,0,1,0,10,10,10,10,10,10}
		};
		
		grid = boardArr;
		
		roomNames = new char[26][49];	// for all the room names
		
		addName("Kitchen", 3, 3);
		addName("Conser-", 2, 39);
		addName("vatory", 3, 40);
		addName("Ballroom", 4, 21);
		addName("Billiard", 9, 39);
		addName("Room", 10, 41);
		addName("Dining", 11, 5);
		addName("Room", 12, 6);
		addName("Library", 16, 38);
		addName("Lounge", 21, 4);
		addName("Hall", 20, 22);
		addName("Study", 22, 39);
	}
	
	private void setupCharGrid() {
		playerGrid = new int[26][24];
		
		// note: these values are the same as the ordinal value (+1) of the respective character's name
		for (int i = 0; i < players.size(); i++) {
			Character c = players.get(i).character();
			playerGrid[c.getStartRow()][c.getStartCol()] = c.toInt() + 1;
		}
	}
	
	private void addName(String name, int row, int column) {
		char[] room = name.toCharArray();
		for (int col = column; (col-column) < room.length; col++) {
			roomNames[row][col] = room[col-column];
		}
	}
	
	/**
	 * Returns the 2D grid representing the positions of every Player's Character.
	 * 
	 * @return	the grid of Characters
	 */
	public int[][] getPlayerGrid() {
		return playerGrid;
	}
	
	/**
	 * Returns the 2D grid representing the game board.
	 * 
	 * @return	the game board
	 */
	public int[][] getGrid() {
		return grid;
	}
	
	/**
	 * Returns the grid of char values from which the Rooms' names can be found.
	 * 
	 * @return	the grid of Room names
	 */
	public char[][] getRoomNameGrid() {
		return roomNames;
	}
	
	/**
	 * Returns a List of all the cards which were not dealt at the game's start,
	 * and which must therefore be displayed face-up next to the board (according
	 * to DJ Pearce).
	 * 
	 * @return	ArrayList of face-up Cards
	 */
	public ArrayList<Card> getFaceUpCards() {
		return faceUpCards;
	}

	/**
	 * Returns a List of all the Players in the game.
	 * 
	 * @return	ArrayList of all the Players
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}

	/**
	 * Returns a List of all the Characters in the game.
	 * For testing purposes.
	 * 
	 * @return	ArrayList of all the Characters
	 */
	public ArrayList<Character> getCharacters() {
		return characters;
	}

	/**
	 * Returns a List of all the Weapons in the game.
	 * For testing purposes.
	 * 
	 * @return	ArrayList of all the Weapons
	 */
	public ArrayList<Weapon> getWeapons() {
		return weapons;
	}

	/**
	 * Returns a List of all the Rooms in the game.
	 * For testing purposes.
	 * 
	 * @return	ArrayList of all the Rooms
	 */
	public ArrayList<Room> getRooms() {
		return rooms;
	}
	
	/**
	 * Returns a List of all the Cards in the game.
	 * For testing purposes.
	 * 
	 * @return	ArrayList of all the Cards
	 */
	public ArrayList<Card> getPack() {
		return pack;
	}
	
	/**
	 * Returns a List of the Cards in the murder envelope.
	 * The Cards represent, in order, the Character, Weapon
	 * and Room that make up the murder scenario.
	 * 
	 * @return		ArrayList of the murder components
	 */
	public ArrayList<Card> getEnvelope() {
		return envelope;
	}
	
	/**
	 * Returns the Room which is represented by num in the game board.
	 * Key:
	 *  2 = Kitchen
	 *  3 = Ballroom
	 *  4 = Conservatory
	 *  5 = Billiard Room
	 *  6 = Dining Room
	 *  7 = Library
	 *  8 = Hall
	 *  9 = Lounge
	 * 10 = Study
	 * 
	 * @param num	The code of the Room
	 * @return		The Room represented by num
	 * @return		null if num does not represent any Room
	 */
	public Room getRoomByCode(int num) {
		for (Room r : rooms) {
			if (r.toInt() == num-2)
				return r;
		}
		return null;
	}

	public Character getCharacterByCode(int num) {
		for (Character c : characters) {
			if (c.toInt() == num-1)
				return c;
		}
		return null;
	}
	
	public Room getRoomForPassage(int row, int col) {
		if (row == 20 && col == 0) {
			return getRoomByCode(9);
		} else if (row == 22 && col == 23) {
			return getRoomByCode(10);
		} else if (row == 2 && col == 5) {
			return getRoomByCode(2);
		} else if (row == 6 && col == 22) {
			return getRoomByCode(4);
		}
		return null;
	}
	
	/**
	 * A way for classes other than this to use the UI. Asks the user the 
	 * question, giving them a choice of all the options. The return value
	 * will be one larger than the index of the option which was chosen.
	 * 
	 * @param question	The question to ask
	 * @param options	The options to give
	 * @return			The (index + 1) of the user's choice
	 */
	/*public int askOpt(String question, String[] options) {
		return ui.askOpt(question, options);
	}*/
}
