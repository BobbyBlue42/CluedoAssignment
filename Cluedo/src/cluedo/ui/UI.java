package cluedo.ui;

public interface UI {
	public void displayBoard();
	
	public void print(String msg);
	
	public String askString(String question);
	
	public int askInt(String question);
}
