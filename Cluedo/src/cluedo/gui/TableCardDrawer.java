package cluedo.gui;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cluedo.game.Card;

public class TableCardDrawer extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Card> cards;
	private boolean beenSetUp = false;
	
	public TableCardDrawer(ArrayList<Card> faceUpCards) {
		cards = faceUpCards;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setSize(240, 390);
		setPreferredSize(getSize());
	}
	
	public void drawBoard(GraphicsUI gui) {
		if (!beenSetUp && cards.size() > 0) {
			beenSetUp = true;
			add(new JLabel("Table cards:"));
			
			for (Card c : cards) {
				CardLabel lbl = new CardLabel(c.icon(""), c);
				lbl.addMouseListener(gui);
				add(lbl);
			}
			revalidate();
		}
	}
}
