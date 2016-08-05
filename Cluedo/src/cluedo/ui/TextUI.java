package cluedo.ui;

import java.io.IOException;

import cluedo.game.Board;

/**
 * A text-based implementation of the UI interface.
 * 
 * @author Louis Thie
 */
public class TextUI implements UI {
	
	private Board board;
	
	/**
	 * Constructs a new TextUI to handle user
	 * interactions for the given Board.
	 * 
	 * @param b		the Board that will use this TextUI
	 */
	public TextUI (Board b) {
		board = b;
	}
	
	/**
	 * Displays the current game board.
	 */
	@Override
	public void displayBoard() {
		TextBoardDrawer.drawBoard(board);
	}
	
	/**
	 * Displays the given message.
	 * 
	 * @param msg	the message to display
	 */
	@Override
	public void print(String msg) {
		System.out.println(msg);
	}

	/**
	 * Asks the user the given question and returns their answer.
	 * 
	 * @param question		the question to ask
	 * @return				the user's answer
	 */
	@Override
	public String askString(String question) {
		System.out.println(question);
		byte[] arr = new byte[30];
		
		while (true) {
			try {
				System.in.read(arr, 0, arr.length);
				break;
			} catch (IOException e) {
				System.out.println("Sorry, something went wrong with reading your answer, please enter it again.");
			}
		}
		
		return new String(arr).trim();
	}

	/**
	 * Asks the user the given question and returns their response.
	 * Ensures that the user answers with an integer.
	 * 
	 * @param question		the question to ask
	 * @return				the user's answer
	 */
	@Override
	public int askInt(String question) {
		System.out.println(question);
		int res = -1;
		
		while (true) {
			byte[] arr = new byte[30];
			try {
				System.in.read(arr, 0, arr.length);
				String s = new String(arr).trim();
				res = Integer.parseInt(s);
				break;
			} catch (IOException e) {
				System.out.println("Sorry, something went wrong with reading your answer, please enter it again.");
			} catch (NumberFormatException e) {
				System.out.println("Please make sure you enter a number, rather than a word.");
			}
		}
		
		
		return res;
	}

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
	@Override
	public int askOpt(String question, String[] options) {
		System.out.println(question);
		for (int i = 0; i < options.length; i++) {
			System.out.println("\t"+(i+1)+"\t"+options[i]);
		}
		System.out.println("(Please enter the number of the option you wish to select)");
		
		int ans = -1;
		
		while (ans <= 0 || ans > options.length) {
			byte[] arr = new byte[30];
			
			try {
				System.in.read(arr, 0, arr.length);
				String s = new String(arr).trim();
				ans = Integer.parseInt(s);
			} catch (IOException e) {
				System.out.println("Sorry, something went wrong with reading your answer, please enter it again.");
			} catch (NumberFormatException e) {
				System.out.println("Please make sure you enter a number, rather than a word.");
			}
			
			if (ans <= 0 || ans > options.length)
				System.out.println("Please choose one of the options which have been provided.");
		}
		
		return ans;
	}

	/**
	 * Asks the user the given question and returns their response.
	 * Ensures the user answers in a manner which can be translated
	 * into true or false.
	 * 
	 * @param question		the question to ask
	 * @return				the user's answer
	 */
	@Override
	public boolean askBool(String question) {
		System.out.println(question+" (y/n)");
		
		while (true) {
			byte[] arr = new byte[30];
			
			try {
				System.in.read(arr, 0, arr.length);
				String s = new String(arr).trim();
				if (s.toLowerCase().equals("y") || s.toLowerCase().equals("yes"))
					return true;
				else if (s.toLowerCase().equals("n") || s.toLowerCase().equals("no"))
					return false;
				System.out.println("Please enter one of: 'y', 'yes', 'n', 'no'");
				System.out.println(question);
			} catch (IOException e) {
				System.out.println("Sorry, something went wrong with reading your anser, please enter it again.");
			}
		}
	}

	/**
	 * Clears the screen of the UI. This is to prevent players from
	 * seeing each other's cards during the game.
	 */
	@Override
	public void clear() {
		for (int i = 0; i < 50; i++) System.out.println();
	}
	
}
