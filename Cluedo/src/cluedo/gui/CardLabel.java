package cluedo.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import cluedo.game.Card;

public class CardLabel extends JLabel {
	
	private static final long serialVersionUID = 1L;
	
	private Card card;
	
	public CardLabel(ImageIcon icon, Card c) {
		super(icon);
		card = c;
	}
	
	public Card getCard() {
		return card;
	}
}
