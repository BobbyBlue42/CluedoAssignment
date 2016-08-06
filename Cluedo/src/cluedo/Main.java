package cluedo;

import java.util.Random;

import cluedo.game.Board;
import cluedo.game.Player;
import cluedo.ui.TextUI;
import cluedo.ui.UI;

/**
 * Entry-point to the Cluedo program. Initialises the game and
 * restarts it, once it has been completed and if the user wishes to do so.
 * 
 * @author Louis Thie
 */
public class Main {

	public static void main(String[] args) {
		boolean fastMode = false;
		
		for (String s : args) {
			if (s.equals("-fast"))
				fastMode = true;
		}
		
		while (true) {
			Random rand = new Random(System.currentTimeMillis());
			Board board = new Board(fastMode);
			UI ui = new TextUI(board);
			board.setUI(ui);
			board.startGame();
			
			while (!board.gameOver()) {
				for (Player p : board.getPlayers()) {

					if (!p.isAlive())	// if player has made a false accusation
						continue;		// skip them because they are out of the game
					
					int dieRoll = rand.nextInt(6)+1;
					
					board.playTurn(p, dieRoll);
					
					if (board.gameOver())
						break;
				}
			}
			
			if (!ui.askBool("Would you like to play again?"))
				break;
		}
	}

}
