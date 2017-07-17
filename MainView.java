package MongoDbDBT230;

/**
 * View class designed to interact with the user
 * 
 * @author Ramon Caballero
 *
 */
public class MainView {

	/**
	 * Displays a welcome message upon starting the application.
	 */
	public static void displayWelcome() {
		System.out.println("Welcome to the Humongous Store\u2122!\n");
	}

	/**
	 * Prompts the user to choose from a specified list of products and returns
	 * the index of the product within the array passed in.
	 * 
	 * @param options
	 *            - the list of products from which the user will choose
	 * @return - the index of the product within the array passed in
	 */
	public static int promptUserForProduct(Product[] options) {
		return ConsoleIO.promptForMenuSelection("Choose a product to add to your order: ", options, true,
				"Finish order");
	}

	/**
	 * Prints out a string to the console
	 * 
	 * @param s
	 *            - the string to display
	 */
	public static void displayString(String s) {
		System.out.println(s);
	}
}
