package cluedo.gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cluedo.game.Board;
import cluedo.game.Card;
import cluedo.game.Player;

public class HandDrawer extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private Board b;
	private Player p;
	
	public HandDrawer(Board b) {
		this.b = b;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	public void drawBoard(GraphicsUI gui) {
		if (p != null && p.equals(b.getCurrentPlayer())
				|| b.getCurrentPlayer() == null)
			return;	// already showing this player's cards/this player is null
		
		removeAll();	// remove current cards
		
		p = b.getCurrentPlayer();
		
		JLabel curName = new JLabel("<html>Current player:<br>"+p.name()+", playing as "+p.character().name()+"</html>");
		add(curName);
		
		JPanel handPanel = new JPanel();
		handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
		Card[] hand = p.hand();
		
		for (Card c : hand) {
			CardLabel lbl = new CardLabel(c.icon(""), c);
			lbl.addMouseListener(gui);
			handPanel.add(lbl);
		}
		
		add(handPanel);
		
		revalidate();
	}
}
