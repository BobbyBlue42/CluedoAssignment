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
	
}
