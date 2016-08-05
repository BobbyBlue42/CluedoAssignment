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
		while (true) {
			Random rand = new Random(System.currentTimeMillis());
			Board board = new Board();
			UI ui = new TextUI(board);
			board.setUI(ui);
			board.startGame();
			
			while (!board.gameOver()) {
				for (Player p : board.getPlayers()) {
					// check how many players are left alive - if only 1 player left, they win
					int aliveCount = 0;
					for (Player player : board.getPlayers())
						if (player.isAlive())
							aliveCount++;
					
					if (aliveCount == 1) {
						Player lastPlayer = null;
						for (Player player : board.getPlayers()) {
							if (player.isAlive())
								lastPlayer = player;
						}
						if (lastPlayer != null) {
							ui.print(lastPlayer.name()+" is the last player left alive, and thus wins the game");
							board.setGameOver();
							break;
						}
						throw new RuntimeException("Should not be able to not have a last player");
					}

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
