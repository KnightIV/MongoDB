package MongoDbDBT230;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Library to interact with the user through the console.
 * 
 * @author Ramon Caballero
 *
 */
public class ConsoleIO {

	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * Prompts the user for a string and returns their output (with whitespaces
	 * removed)
	 * 
	 * @param prompt
	 *            - the prompt to display to the user
	 * @param isEmpty
	 *            - whether or not the user may leave their response empty,
	 *            regardless of whitespace
	 * @return - the user's input as a string
	 */
	public static String promptForString(String prompt, boolean isEmpty) {
		while (true) {
			System.out.print(prompt);
			String answer = "";
			try {
				answer = in.readLine().trim();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (answer.length() == 0 && !isEmpty) {
				System.out.println("\nYour answer cannot be left empty. Try again.\n");
				continue;
			}

			return answer;
		}
	}

	/**
	 * Prompts the user for an int and returns the user's input
	 * 
	 * @param prompt
	 *            - the prompt to display to the user
	 * @param min
	 *            - the minimum number the user is allowed to input
	 * @param max
	 *            - the maximum number the user is allowed to input
	 * @return - the user's input as an int
	 */
	public static int promptForInt(String prompt, int min, int max) {
		while (true) {
			String rawAnswer = promptForString(prompt, false);
			int ans;

			try {
				ans = Integer.parseInt(rawAnswer);
			} catch (NumberFormatException e) {
				System.out.println("\nYour input must be a whole number. Try again.\n");
				continue;
			}

			if (ans < min || ans > max) {
				System.out.println("\nYou must type a number between " + min + " and " + max + ". Try again.\n");
				continue;
			}

			return ans;
		}
	}

	/**
	 * Takes in an array of objects and uses their string representations to
	 * prompt the user to choose between these.
	 * 
	 * @param prompt
	 *            - the prompt to display to the user
	 * @param options
	 *            - the options from which the user will pick
	 * @param withQuit
	 *            - whether or not the user is allowed to quit
	 * @param quitMessage
	 *            - the message to display if <b>withQuit</b> is true
	 * @return the index of the object that the player chose (-1 if the user
	 *         chose to quit)
	 */
	public static int promptForMenuSelection(String prompt, Object[] options, boolean withQuit, String quitMessage) {
		int min = 1;

		if (withQuit) {
			System.out.println("0 - " + quitMessage);
			min = 0;
		}

		for (int i = 0; i < options.length; i++) {
			int choice = i + 1;
			System.out.println(choice + " - " + options[i]);
		}

		return promptForInt(prompt, min, options.length) - 1;
	}

	/**
	 * Prompts the user to pick between 2 specified choices, one of them
	 * signifying that the user chose the <b>true</b> option, and the other
	 * signifying that they chose the <b>false</b> option.
	 * 
	 * @param prompt
	 *            - the prompt to display to the user
	 * @param trueString
	 *            - the choice that will return true
	 * @param falseString
	 *            - the choice that will return false
	 * @return true or false, depending on the user's input
	 */
	public static boolean promptForBool(String prompt, String trueString, String falseString) {
		while (true) {
			String input = promptForString(prompt, false);

			if (input.equalsIgnoreCase(trueString))
				return true;
			if (input.equalsIgnoreCase(falseString))
				return false;

			System.out.println("\nYour response didn't match any of the specified options. Try again.\n");
		}
	}
}
