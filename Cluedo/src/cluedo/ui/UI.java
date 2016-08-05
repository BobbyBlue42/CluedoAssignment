package cluedo.ui;

/**
 * An interface which can handle all user-related inputs and outputs.
 * Any classes implementing this must store their own references to
 * the Board.
 * 
 * @author Louis Thie
 */
public interface UI {
	/**
	 * Displays the current game board.
	 */
	public void displayBoard();
	
	/**
	 * Displays the given message.
	 * 
	 * @param msg	the message to display
	 */
	public void print(String msg);
	
	/**
	 * Asks the user the given question and returns their answer.
	 * 
	 * @param question		the question to ask
	 * @return				the user's answer
	 */
	public String askString(String question);
	
	/**
	 * Asks the user the given question and returns their response.
	 * Ensures that the user answers with an integer.
	 * 
	 * @param question		the question to ask
	 * @return				the user's answer
	 */
	public int askInt(String question);
	
	/**
	 * Asks the user the given question and gives them the given
	 * options to choose from. Ensures the user chooses one of the
	 * options provided, and returns the user's choice (which will
	 * be one greater than the index of the option's choice in the
	 * array of options).
	 * 
	 * @param question		the question to ask
	 * @param options		the options to give the user
	 * @return				the user's choice
	 */
	public int askOpt(String question, String[] options);

	/**
	 * Asks the user the given question and returns their response.
	 * Ensures the user answers in a manner which can be translated
	 * into true or false.
	 * 
	 * @param question		the question to ask
	 * @return				the user's answer
	 */
	public boolean askBool(String question);

	/**
	 * Clears the screen of the UI. This is to prevent players from
	 * seeing each other's cards during the game.
	 */
	public void clear();
}
