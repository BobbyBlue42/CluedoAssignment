package cluedo.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import cluedo.ui.UI;

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
	private ArrayList<Card> faceUpCards;
	
	private UI ui;
	
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
	public void setUI(UI ui) {
		this.ui = ui;
	}
	
	/**
	 * Returns the Player whose turn it currently is.
	 * 
	 * @return	the current Player
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
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
	private boolean move(Character c, Direction d, Room origin) {
		if (c != null) {
			int row = c.getRow();
			int col = c.getCol();
			
			if (d.equals(Direction.UP)) {
				if (row == 0) {
					// trying to move out of the array
					ui.print("Sorry, you cannot move in that direction");
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
					// inside a room
					Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, this);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.toInt() + 1;
							c.leaveRoom();
							c.setRow(exitRow);
							c.setCol(exitCol);
							return true;
						} else {
							ui.print("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}
				} else if (grid[row-1][col] != 1) {
					if (grid[row-1][col] == 0) {
						// trying to move out of the array
						ui.print("Sorry, you cannot move in that direction");
						return false;
					}
					// trying to enter a room
					Room r = getRoomByCode(grid[row-1][col]);
					if (r.equals(origin)) {
						ui.print("Sorry, you cannot re-enter a room you left this turn.");
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
					ui.print("Sorry, you cannot move in that direction");
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
					// inside a room
					Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, this);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.toInt() + 1;
							c.leaveRoom();
							c.setRow(exitRow);
							c.setCol(exitCol);
							return true;
						} else {
							ui.print("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}
				} else if (grid[row+1][col] != 1) {
					if (grid[row+1][col] == 0) {
						// trying to move out of the array
						ui.print("Sorry, you cannot move in that direction");
						return false;
					}
					// trying to enter a room
					Room r = getRoomByCode(grid[row+1][col]);
					if (r.equals(origin)) {
						ui.print("Sorry, you cannot re-enter a room you left this turn.");
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
					ui.print("Sorry, you cannot move in that direction");
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
					// inside a room
					Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, this);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.toInt() + 1;
							c.leaveRoom();
							c.setRow(exitRow);
							c.setCol(exitCol);
							return true;
						} else {
							ui.print("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}
				} else if (grid[row][col-1] != 1) {
					if (grid[row][col-1] == 0) {
						// trying to move out of the array
						ui.print("Sorry, you cannot move in that direction");
						return false;
					}
					// trying to enter a room
					Room r = getRoomByCode(grid[row][col-1]);
					if (r.equals(origin)) {
						ui.print("Sorry, you cannot re-enter a room you left this turn.");
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
					ui.print("Sorry, you cannot move in that direction");
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
					// inside a room
					Room r = getRoomByCode(grid[row][col]);
					if (r.canLeave(d)) {
						Point exit = r.getExitPoint(d, this);
						int exitRow = (int) exit.getX();
						int exitCol = (int) exit.getY();
						if (playerGrid[exitRow][exitCol] == 0) {
							playerGrid[exitRow][exitCol] = c.toInt() + 1;
							c.leaveRoom();
							c.setRow(exitRow);
							c.setCol(exitCol);
							return true;
						} else {
							ui.print("Sorry, there is already a character standing in that spot.");
							return false;
						}
					}
				} else if (grid[row][col+1] != 0) {
					if (grid[row][col+1] == 0) {
						// trying to move out of the array
						ui.print("Sorry, you cannot move in that direction");
						return false;
					}
					// trying to enter a room
					Room r = getRoomByCode(grid[row][col+1]);
					if (r.equals(origin)) {
						ui.print("Sorry, you cannot re-enter a room you left this turn.");
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
			
			ui.print("Sorry, the move you requested is invalid. Please try a different move.");
			return false;
		}
		
		ui.print("Sorry, there was an error (character was null).");
		return false;
	}
	
	/**
	 * Handles the actual running of the game. Calling this will start the game.
	 */
	public void run() {
		// Welcome players
		ui.print("Welcome to Cluedo, the Great Detective Game!");
		// Allow players to choose their characters
		setupPlayers(ui.askInt("How many people will be playing?"));
		// Deal non-murder-component cards to players
		dealCards();
		// Put the weapons in rooms, maximum one at a time
		setOutWeapons();
		
		while (!gameOver) {
			for (Player p : players) {
				// check how many players are left alive - if only 1 player left, they win
				int aliveCount = 0;
				for (Player player : players)
					if (player.isAlive())
						aliveCount++;
				
				if (aliveCount == 1) {
					Player lastPlayer = null;
					for (Player player : players) {
						if (player.isAlive())
							lastPlayer = player;
					}
					if (lastPlayer != null) {
						ui.print(lastPlayer.name()+" is the last player left alive, and thus wins the game");
						gameOver = true;
						break;
					}
					throw new RuntimeException("Should not be able to not have a last player");
				}
				
				if (!p.isAlive())	// if player has made a false accusation
					continue;		// skip them because they are out of the game
				
				currentPlayer = p;
				
				int dieRoll = rand.nextInt(5) + 1;
				boolean hasHypothesised = false;
				
				Room startingRoom = p.character().location();
				
				while (dieRoll > 0 || (p.character().location() != null && !hasHypothesised)) {
					ui.displayBoard();
					ui.print("You have "+dieRoll+" moves remaining\n");
					
					String question = "What would you like to do? (Some options may be unavailable)";
					String[] options = new String[4];
					
					if (startingRoom != null
							&& startingRoom.hasConnection()
							&& startingRoom.equals(p.character().location())) {
						options = new String[5];
						options[4] = "Take the secret stairs";
					}
					options[0] = "Move";
					options[1] = "Make hypothesis";
					options[2] = "Make accusation";
					options[3] = "Look at your cards";
					
					int choice = ui.askOpt(question, options);
					
					if (choice == 1) {
						// Move
						// Valid checking will be done by the move method
						if (dieRoll <= 0) {
							ui.print("Sorry, you cannot move any further in this turn");
						} else {
							question = "Which direction would you like to move in?";
							options = new String[4];
							options[0] = "Up";
							options[1] = "Right";
							options[2] = "Down";
							options[3] = "Left";
							
							int moveDir = ui.askOpt(question, options);
							
							boolean moved = false;
							
							if (moveDir == 1) {
								moved = move(p.character(), Direction.UP, startingRoom);
							} else if (moveDir == 2) {
								moved = move(p.character(), Direction.RIGHT, startingRoom);
							} else if (moveDir == 3) {
								moved = move(p.character(), Direction.DOWN, startingRoom);
							} else if (moveDir == 4) {
								moved = move(p.character(), Direction.LEFT, startingRoom);
							} else {
								ui.print("Sorry, that was not a valid direction.");
							}
							
							dieRoll -= moved ? 1 : 0;
							
							// if player has entered a room, their turn is over (except for a hypothesis)
							if (p.character().location() != null && !p.character().location().equals(startingRoom)) {
								dieRoll = 0;
							}
						}
					} else if (choice == 2) {
						// hypothesis
						if (p.character().location() == null) {
							ui.print("Sorry, you cannot hypothesise if you are not inside a room.");
						} else {
							Room room = p.character().location();
							question = "You are hypothesising about a murder which may have happened in the " + room.name().toString();
							question += "\nWhich character do you think may have commited the murder?";
							
							options = new String[characters.size()];
							for (int i = 0; i < characters.size(); i++) {
								options[i] = characters.get(i).name().toString();
							}
							
							int charAns = ui.askOpt(question, options);
							Character character = characters.get(charAns-1);
							
							question = "You are hypothesising about a murder which "+character.name().toString()
									+" may have commited in the "+room.name().toString();
							question += "\nWhich weapon do you think they may have used?";
							
							options = new String[weapons.size()];
							for (int i = 0; i < weapons.size(); i++) {
								options[i] = weapons.get(i).name().toString();
							}
							
							int weapAns = ui.askOpt(question, options);
							Weapon weapon = weapons.get(weapAns-1);
							
							if (!room.equals(character.location())) {
								// first, remove the character from wherever they are right now
								if (character.location() == null) {
									// character is on the normal grid
									playerGrid[character.getRow()][character.getCol()] = 0;
								} else {
									// character is in a room
									character.location().removeCharacter(character);
								}
								
								room.addCharacter(character);
								character.enterRoom(room);
							}
							if (!room.equals(weapon.location())) {
								weapon.location().removeWeapon(weapon);
								room.addWeapon(weapon);
								weapon.moveToRoom(room);
							}
							
							ui.displayBoard();	// to show that the character and weapon have been moved
							hypothesise(room, character, weapon);
							
							hasHypothesised = true;
							// just to make sure - shouldn't be over 0 at this point anyway
							dieRoll = 0;
						}
					} else if (choice == 3) {
						// accusation
						question = "Which room do you think the murder happened in?";
						options = new String[rooms.size()];
						for (int i = 0; i < rooms.size(); i++) {
							options[i] = rooms.get(i).name().toString();
						}
						
						int roomNo = ui.askOpt(question, options);
						Room room = rooms.get(roomNo-1);
						
						question = "Which character do you think commited the murder?";
						options = new String[characters.size()];
						for (int i = 0; i < characters.size(); i++) {
							options[i] = characters.get(i).name().toString();
						}
						
						int charNo = ui.askOpt(question, options);
						Character character = characters.get(charNo-1);
						
						question = "What weapon do you think they commited the murder with?";
						options = new String[weapons.size()];
						for (int i = 0; i < weapons.size(); i++) {
							options[i] = weapons.get(i).name().toString();
						}
						
						int weapNo = ui.askOpt(question, options);
						Weapon weapon = weapons.get(weapNo-1);
						
						ui.print("You have accused "+character.name().toString()+" of commiting the murder "
								+"in the "+room.name().toString()+" with the "+weapon.name().toString());
						ui.print("All other players, you have 5 seconds to please look away from the screen.");
						wait(5000);
						
						boolean allRight = true;
						
						// envelope is set up in order of char -> weapon -> room
						Character murderChar = (Character)envelope.get(0).piece();
						Weapon murderWeapon = (Weapon)envelope.get(1).piece();
						Room murderRoom = (Room)envelope.get(2).piece();
	
						ui.print("You guessed that the murder happened in the "+room.name().toString());
						wait(2000);
						
						if (room.equals(murderRoom)) {
							ui.print("Your guess was correct");
						} else {
							ui.print("Your guess was incorrect");
							allRight = false;
						}
						
						wait(1000);
						ui.print("You guessed that the murder was committed by "+character.name().toString());
						wait(2000);
						
						if (character.equals(murderChar)) {
							ui.print("Your guess was correct");
						} else {
							ui.print("Your guess was incorrect");
							allRight = false;
						}
	
						wait(1000);
						ui.print("You guessed that the murderer used the "+weapon.name().toString());
						wait(2000);
						
						if (weapon.equals(murderWeapon)) {
							ui.print("Your guess was correct");
						} else {
							ui.print("Your guess was incorrect");
							allRight = false;
						}
						
						wait(2000);
						
						if (allRight) {
							gameOver = true;
							ui.print(p.name()+" has won the game! The murder was committed by "+character.name().toString()
									+" in the "+room.name().toString()+" using the "+weapon.name().toString());
						} else {
							ui.clear();
							ui.print(p.name()+" made an incorrect accusation, and was eliminated from the game.");
							p.die();
							wait(5000);
						}
						
						break;	// either way, the player's turn is over now
					} else if (choice == 4) {
						lookAtCards(p);
					} else if (choice == 5) {
						// secret passageway
						Room endRoom = startingRoom.connection();
						startingRoom.removeCharacter(p.character());
						endRoom.addCharacter(p.character());
						p.character().enterRoom(endRoom);
						dieRoll = 0;
					}
				}
				
				if (gameOver) break;
			}
		}
	}
	
	private void setupPlayers(int people) {
		while (people > 6 || people < 3) {
			ui.print("Please enter a number between 3 and 6 (inclusive).");
			people = ui.askInt("How many people will be playing?");
		}
		
		ArrayList<Character> chars = new ArrayList<Character>();
		for (Character c : characters)
			chars.add(c);
		
		for (int i = 1; i <= people; i++) {
			Player p = new Player(ui.askString("Player "+i+", please enter your name:"));
			
			String charQuestion = "Which character would you like to play as, "+p.name()+"?";
			String[] options = new String[chars.size()];
			
			for (int j = 0; j < chars.size(); j++) {
				options[j] = chars.get(j).name().toString();
			}
			
			int character = ui.askOpt(charQuestion, options);
			
			p.chooseCharacter(chars.get(character-1));
			p.character().assignTo(p);
			
			chars.remove(character-1);
			
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
		faceUpCards = new ArrayList<Card>();
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
	
	private void hypothesise(Room r, Character c, Weapon w) {
		boolean foundCurPlayer = false;
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (!foundCurPlayer) {
				if (p.equals(currentPlayer))
					foundCurPlayer = true;
			} else {
				ui.print("Asking "+p.name()+" about their cards. Please hand the controls over to them for now.\n");
				wait(1000);
				ui.print(p.name()+", you have been asked to dispute a hypothesis made by "+currentPlayer.name()
						+", that the murder was commited by "+c.name()+" in the "+r.name()+" using the "+w.name());
				boolean lookAtCards = ui.askBool("Would you like to look at your cards before answering?");
				
				if (lookAtCards) {
					lookAtCards(p);
				}
				
				while (true) {
					String question = "Would you like to dispute any of this hypothesised murder's components?";
					String[] options = {
							r.name().toString(),
							c.name().toString(),
							w.name().toString(),
							"None"
					};
					
					int choice = ui.askOpt(question, options);
					
					if (choice == 1) {
						// disputing room
						for (Card card : p.hand())
							if (card.name().equals(r.name().toString())) {
								ui.print(p.name()+" has disproven the hypothesis that the murder was commited by "+c.name().toString()
										+" in the "+r.name().toString()+" with the "+w.name().toString()+" by showing that they are holding "
										+"the "+card.name()+" card.");
								return;
							}
						ui.print("You cannot dispute a hypothesis unless you have one of the corresponding cards in your hand.");
					} else if (choice == 2) {
						// disputing character
						for (Card card : p.hand())
							if (card.name().equals(c.name().toString())) {
								ui.print(p.name()+" has disproven the hypothesis that the murder was commited by "+c.name().toString()
										+" in the "+r.name().toString()+" with the "+w.name().toString()+" by showing that they are holding "
										+"the "+card.name()+" card.");
								return;
							}
						ui.print("You cannot dispute a hypothesis unless you have one of the corresponding cards in your hand.");
					} else if (choice == 3) {
						// disputing weapon
						for (Card card : p.hand())
							if (card.name().equals(w.name().toString())) {
								ui.print(p.name()+" has disproven the hypothesis that the murder was commited by "+c.name().toString()
										+" in the "+r.name().toString()+" with the "+w.name().toString()+" by showing that they are holding "
										+"the "+card.name()+" card.");
								return;
							}
						ui.print("You cannot dispute a hypothesis unless you have one of the corresponding cards in your hand.");
					} else {
						// claiming they cannot dispute
						boolean found = false;
						for (Card card : p.hand())
							if (card.name().equals(r.name().toString())
									|| card.name().equals(c.name().toString())
									|| card.name().equals(w.name().toString())) {
								ui.print("If you have one of the cards corresponding to a hypothesis in your hand, you *must* dispute "
										+"the hypothesis.");
								found = true;
								break;
							}
						if (found) continue;
						else break;
					}
				}
			}
		}
		
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.equals(currentPlayer)) {
				return;
			} else {
				ui.print("Asking "+p.name()+" about their cards. Please hand the controls over to them for now.");
				boolean lookAtCards = ui.askBool("Would you like to look at your cards before answering?");
				
				if (lookAtCards) {
					lookAtCards(p);
				}
				
				while (true) {
					String question = "Would you like to dispute any of this hypothesised murder's components?";
					String[] options = {
							r.name().toString(),
							c.name().toString(),
							w.name().toString(),
							"None"
					};
					
					int choice = ui.askOpt(question, options);
					
					if (choice == 1) {
						// disputing room
						for (Card card : p.hand())
							if (card.name().equals(r.name().toString())) {
								ui.print(p.name()+" has disproven the hypothesis that the murder was commited by "+c.name().toString()
										+" in the "+r.name().toString()+" with the "+w.name().toString()+" by showing that they are holding "
										+"the "+card.name()+" card.");
								return;
							}
						ui.print("You cannot dispute a hypothesis unless you have one of the corresponding cards in your hand.");
					} else if (choice == 2) {
						// disputing character
						for (Card card : p.hand())
							if (card.name().equals(c.name().toString())) {
								ui.print(p.name()+" has disproven the hypothesis that the murder was commited by "+c.name().toString()
										+" in the "+r.name().toString()+" with the "+w.name().toString()+" by showing that they are holding "
										+"the "+card.name()+" card.");
								return;
							}
						ui.print("You cannot dispute a hypothesis unless you have one of the corresponding cards in your hand.");
					} else if (choice == 3) {
						// disputing weapon
						for (Card card : p.hand())
							if (card.name().equals(w.name().toString())) {
								ui.print(p.name()+" has disproven the hypothesis that the murder was commited by "+c.name().toString()
										+" in the "+r.name().toString()+" with the "+w.name().toString()+" by showing that they are holding "
										+"the "+card.name()+" card.");
								return;
							}
						ui.print("You cannot dispute a hypothesis unless you have one of the corresponding cards in your hand.");
					} else {
						// claiming they cannot dispute
						boolean found = false;
						for (Card card : p.hand())
							if (card.name().equals(r.name().toString())
									|| card.name().equals(c.name().toString())
									|| card.name().equals(w.name().toString())) {
								ui.print("If you have one of the cards corresponding to a hypothesis in your hand, you *must* dispute "
										+"the hypothesis.");
								found = true;
								break;
							}
						if (found) continue;
						else break;
					}
				}
			}
		}
	}
	
	private void lookAtCards(Player p) {
		ui.print("You have 2 seconds to make sure nobody is looking at the screen while you check your cards.");
		
		wait(2000);
		
		ui.print("Your hand:");
		for (Card card : p.hand()) {
			ui.print("\t"+card.name());
		}
		boolean looking = true;
		while (looking)
			looking = !ui.askBool("Are you done looking at your cards?");
		
		ui.clear();
	}
	
	private void wait(int millis) {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() < start+millis);
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
			playerGrid[c.getStartRow()][c.getStartCol()] = i + 1;
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

	/**
	 * A way for classes other than this to use the UI. Asks the user the 
	 * question, giving them a choice of all the options. The return value
	 * will be one larger than the index of the option which was chosen.
	 * 
	 * @param question	The question to ask
	 * @param options	The options to give
	 * @return			The (index + 1) of the user's choice
	 */
	public int askOpt(String question, String[] options) {
		return ui.askOpt(question, options);
	}
}
