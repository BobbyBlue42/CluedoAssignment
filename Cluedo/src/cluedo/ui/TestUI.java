package cluedo.ui;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A UI for JUnit testing. Does not display anything, and allows
 * the test case to set the answer for the next question ahead of
 * time and programmatically.
 * 
 * @author Louis Thie
 */
public class TestUI implements UI {

	Queue<Object> answers = new ArrayDeque<Object>();
	
	/**
	 * Adds an answer to the queue, which will be given when its
	 * turn comes.
	 * 
	 * @param answer		the answer to add to the queue
	 */
	public void addAnswer(Object answer) {
		answers.offer(answer);
	}
	
	@Override
	public void displayBoard() {}

	@Override
	public void print(String msg) {}

	@Override
	public String askString(String question) {
		return (String)answers.poll();
	}

	@Override
	public int askInt(String question) {
		return (Integer)answers.poll();
	}

	@Override
	public int askOpt(String question, String[] options) {
		return (Integer)answers.poll();
	}

	@Override
	public boolean askBool(String question) {
		return (Boolean)answers.poll();
	}

	@Override
	public void clear() {}

}
