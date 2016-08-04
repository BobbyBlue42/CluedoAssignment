package cluedo.ui;

public interface UI {
	public void displayBoard();
	
	public void print(String msg);
	
	public String askString(String question);
	
	public int askInt(String question);
	
	public int askOpt(String question, String[] options);

	public boolean askBool(String question);

	public void clear();
}
