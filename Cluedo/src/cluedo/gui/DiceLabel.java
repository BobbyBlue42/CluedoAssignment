package cluedo.gui;

import javax.swing.JLabel;

import cluedo.game.Board;

public class DiceLabel extends JLabel {
	
	private static final long serialVersionUID = 1L;
	
	private Board b;
	
	public DiceLabel(Board b) {
		super("<html>Dice roll:<br>Remaining moves:</html>");
		this.b = b;
	}
	
	public void redraw() {
		String txt = "<html>Dice roll: "+b.getDiceRoll()
				+"<br>Remaining moves: "+b.getRemainingMoves()
				+"</html>";
		setText(txt);
		revalidate();
	}
}
