package cluedo.ui;

import java.io.IOException;

import cluedo.game.Board;

public class TextUI implements UI {
	
	private Board board;
	
	public TextUI (Board b) {
		board = b;
	}

	@Override
	public void displayBoard() {
		TextBoardDrawer.drawBoard(board);
	}

	@Override
	public void print(String msg) {
		System.out.println(msg);
	}

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
		
		return new String(arr);
	}

	@Override
	public int askInt(String question) {
		System.out.println(question);
		int res = -1;
		
		while (true) {
			byte[] arr = new byte[30];
			try {
				System.in.read(arr, 0, arr.length);
				String s = new String(arr);
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
	 * Returns the number of the option which has been selected (1 greater
	 * than the index of the option in the options array).
	 * 
	 * @param question		The question to ask the user
	 * @param options		The options to give the user
	 * @return				The user's choice (index + 1)
	 */
	@Override
	public int askOpt(String question, String[] options) {
		System.out.println(question);
		for (int i = 0; i < options.length; i++) {
			System.out.println("\t"+(i+1)+"\t"+options[i]);
		}
		System.out.println("(Please enter the number of the option you wish to select)");
		
		int ans = -1;
		
		while (ans <= 0 && ans > options.length) {
			byte[] arr = new byte[30];
			
			try {
				System.in.read(arr, 0, arr.length);
				String s = new String(arr);
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
	
	@Override
	public boolean askBool(String question) {
		System.out.println(question+" (y/n)");
		
		while (true) {
			byte[] arr = new byte[30];
			
			try {
				System.in.read(arr, 0, arr.length);
				String s = new String(arr);
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
	
	@Override
	public void clear() {
		for (int i = 0; i < 50; i++) System.out.println();
	}
	
}
