package cluedo.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import cluedo.ui.UI;

public class Board {
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
	
	private UI ui;
	
	// 0 = empty
	// 1 = corridor
	// 2 = Kitchen
	// 3 = Ballroom
	// 4 = Conservatory
	// 5 = Billiard Room
	// 6 = Dining Room
	// 7 = Library
	// 8 = Hall
	// 9 = Lounge
	// 10 = Study
	// 11 = NW/SE stairs
	// 12 = NE/SW stairs
	private int[][] grid;
	
	// 0 = empty
	// 1 = Miss Scarlett
	// 2 = Professor Plum
	// 3 = Mrs. Peacock
	// 4 = Reverend Green
	// 5 = Mrs. White
	// 6 = Colonel Mustard
	private int[][] playerGrid;
	
	private char[][] roomNames;
	
	private Random rand;
	
	private boolean gameOver = false;
	
	public Board() {
		players = new ArrayList<Player>();
		rand = new Random(System.currentTimeMillis());
		
		// TODO: initialise UI (or potentially add as argument)
		
		setupChars();
		setupRooms();
		setupWeapons();
		setupPack();
		
		setupGrids();
		
		run();
	}
	
	private void setupChars() {
		characters = new ArrayList<Character>();
		
		for (Character.CharacterName name : Character.CharacterName.values()) {
			characters.add(new Character(name, name.getRow(), name.getCol()));
		}
	}
	
	private void setupRooms() {
		rooms = new ArrayList<Room>();
		
		for (Room.RoomName name : Room.RoomName.values()) {
			rooms.add(new Room(name));
		}
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
		
		int murderChar = rand.nextInt(characters.size());
		int murderWeapon = rand.nextInt(weapons.size());
		int murderRoom = rand.nextInt(rooms.size());
		
		for (int i = 0; i < characters.size(); i++) {
			pack.add(new CharacterCard(characters.get(i), i == murderChar));
		}
		
		for (int i = 0; i < weapons.size(); i++) {
			pack.add(new WeaponCard(weapons.get(i), i == murderWeapon));
		}
		
		for (int i = 0; i < rooms.size(); i++) {
			pack.add(new RoomCard(rooms.get(i), i == murderRoom));
		}
	}

	public void endTurn() {
		int i = players.indexOf(currentPlayer) + 1;
		if (i == players.size())
			i = 0;
		currentPlayer = players.get(i);
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public void deal(Card c, Player p) {
		c.dealTo(p);
		p.deal(c);
	}
	
	public int dieRoll() {
		return rand.nextInt(5) + 1;
	}
	
	public boolean move(Character c, Direction d) {
		if (c != null) {
			int row = c.getRow();
			int col = c.getCol();
			
			if (d.equals(Direction.UP)) {
				if (grid[row][col] == 1 
						&& grid[row-1][col] == 1
						&& playerGrid[row-1][col] == 0) {	// cannot move through other characters
					// in a corridor
					playerGrid[row-1][col] = playerGrid[row][col];
					playerGrid[row][col] = 0;
					return true;
				} else if (grid[row][col] != 1) {
					// inside a room
					Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, ui);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.name().ordinal();
							return true;
						} else {
							ui.print("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}
				} else if (grid[row-1][col] != 1) {
					// trying to enter a room
					Room r = getRoomByCode(grid[row-1][col]);
					if (r.canEnter(row, col, row-1, col)) {
						r.addCharacter(c);
						c.enterRoom(r);
						return true;
					}
				}
			} else if (d.equals(Direction.DOWN)) {
				if (grid[row][col] == 1 
						&& grid[row+1][col] == 1
						&& playerGrid[row+1][col] == 0) {	// cannot move through other characters
					// in a corridor
					playerGrid[row+1][col] = playerGrid[row][col];
					playerGrid[row][col] = 0;
					return true;
				} else if (grid[row][col] != 1) {
					// inside a room
					Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, ui);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.name().ordinal();
							return true;
						} else {
							ui.print("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}
				} else if (grid[row+1][col] != 1) {
					// trying to enter a room
					Room r = getRoomByCode(grid[row+1][col]);
					if (r.canEnter(row, col, row+1, col)) {
						r.addCharacter(c);
						c.enterRoom(r);
						return true;
					}
				}
			} else if (d.equals(Direction.LEFT)) {
				if (grid[row][col] == 1 
						&& grid[row][col-1] == 1
						&& playerGrid[row][col-1] == 0) {	// cannot move through other characters
					// in a corridor
					playerGrid[row][col-1] = playerGrid[row][col];
					playerGrid[row][col] = 0;
					return true;
				} else if (grid[row][col] != 1) {
					// inside a room
					Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, ui);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.name().ordinal();
							return true;
						} else {
							ui.print("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}
				} else if (grid[row][col-1] != 1) {
					// trying to enter a room
					Room r = getRoomByCode(grid[row][col-1]);
					if (r.canEnter(row, col, row, col-1)) {
						r.addCharacter(c);
						c.enterRoom(r);
						return true;
					}
				}
			} else if (d.equals(Direction.RIGHT)) {
				if (grid[row][col] == 1 
						&& grid[row][col+1] == 1
						&& playerGrid[row][col+1] == 0) {	// cannot move through other characters
					// in a corridor
					playerGrid[row][col+1] = playerGrid[row][col];
					playerGrid[row][col] = 0;
					return true;
				} else if (grid[row][col] != 1) {
					// inside a room
					Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, ui);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.name().ordinal();
							return true;
						} else {
							ui.print("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}
				} else if (grid[row][col+1] != 0) {
					// trying to enter a room
					Room r = getRoomByCode(grid[row][col+1]);
					if (r.canEnter(row, col, row, col+1)) {
						r.addCharacter(c);
						c.enterRoom(r);
						return true;
					}
				}
			}
			
			ui.print("Sorry, the move you requested is invalid. Please try a different move.");
			return false;
		}
		
		ui.print("Sorry, there was an error (character was null).");
		return false;
	}
	
	public void run() {
		ui.print("Welcome to Cluedo, the Great Detective Game!");
		int people = ui.askInt("How many people will be playing?");
		while (people > 6 || people < 3) {
			ui.print("Please enter a number between 3 and 6 (inclusive).");
			people = ui.askInt("How many people will be playing?");
		}
		
		ArrayList<Integer> charIndices = new ArrayList<Integer>();
		
		for (int i = 1; i <= people; i++) {
			Player p = new Player(ui.askString("Player "+i+", please enter your name:"));
			
			String charQuestion = "Which character would you like to play as?";
			for (int j = 0; j < characters.size(); j++) {
				if (!charIndices.contains(j)) {
					charQuestion += "\n\t"+(j+1)+"\t"+characters.get(j).name().toString();
				}
			}
			charQuestion += "\n(Please enter the number corresponding to your choice of character.)";
			
			int character = ui.askInt(charQuestion);
			
			while (character > 6 || character < 1 || charIndices.contains(character)) {
				ui.print("Sorry, that is an invalid choice of character.");
				character = ui.askInt(charQuestion);
			}
			
			charIndices.add(character);
			
			p.chooseCharacter(characters.get(character));
			
			players.add(p);
		}
		
		
		
		/*while (!gameOver) {
			for (Player p : players) {
				
			}
		}*/
	}
	
	private void setupGrids() {
		int[][] boardArr = {
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
				{2,2,2,2,2,11,0,1,1,1,3,3,3,3,1,1,1,0,4,4,4,4,4,12},
				{2,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,4,4,4,4,4,4},
				{2,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,4,4,4,4,4,4},
				{2,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,4,4,4,4,4,4},
				{2,2,2,2,2,2,1,1,3,3,3,3,3,3,3,3,1,1,1,4,4,4,4,0},
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
		
		int[][] charArr = new int[26][24];
		
		// note: these values are the same as the ordinal value of the respective character's name
		charArr[25][7] = 1;		// Miss Scarlett
		charArr[20][23] = 2;	// Professor Plum
		charArr[7][23] = 3;		// Mrs. Peacock
		charArr[1][14] = 4;		// Reverend Green
		charArr[1][9] = 5;		// Mrs. White
		charArr[18][0] = 6;		// Colonel Mustard
		
		playerGrid = charArr;
		
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
	
	private void addName(String name, int row, int column) {
		char[] room = name.toCharArray();
		for (int col = column; (col-column) < room.length; col++) {
			roomNames[row][col] = room[col-column];
		}
	}
	
	public int[][] getPlayerGrid() {
		return playerGrid;
	}
	
	public int[][] getGrid() {
		return grid;
	}
	
	public char[][] getRoomNameGrid() {
		return roomNames;
	}
	
	public Room getRoomByCode(int num) {
		// 2 = Kitchen
		// 3 = Ballroom
		// 4 = Conservatory
		// 5 = Billiard Room
		// 6 = Dining Room
		// 7 = Library
		// 8 = Hall
		// 9 = Lounge
		// 10 = Study
		for (Room r : rooms) {
			if (r.name().ordinal() == num-2)
				return r;
		}
		return null;
	}
}
