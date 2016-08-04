package cluedo;

import cluedo.game.Board;
import cluedo.ui.TextUI;
import cluedo.ui.UI;

public class Main {

	public static void main(String[] args) {
		Board board = new Board();
		UI ui = new TextUI(board);
		board.setUI(ui);
		board.run();
	}

}
