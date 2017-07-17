package MongoDbDBT230;

import java.util.Date;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Sample application to test MongoDB in Java.
 * 
 * @author Ramon Caballero
 *
 */
public class Program {

	private Product[] storeProducts;

	/**
	 * Starts the app
	 */
	public void run() {
		shopSetup();
		MainView.displayWelcome();
		while (true) {
			MainView.displayString(""); // aesthetic
			switch (userChoice()) {
			case -1:
				MainView.displayString("\nThank you for shopping at the Humongous Store\u2122!\n");
				return;

			case 0:
				MainView.displayString(""); // aesthetic
				shop();
				break;

			case 1:
				MainView.displayString(""); // aesthetic
				switch (historyReviewFilter()) {
				case -1:
					break;

				case 0:
					// TODO query stuff from MongoDB
					break;
				}
			}
		}
	}

	/**
	 * Prompts the user for how they will proceed <br>
	 * The list of choices with their return values are: <br>
	 * &nbsp;&nbsp;&nbsp;<b>-1.</b> Exit the app <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>0.</b> Start a new order <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>1.</b> Take a look at the user's order history
	 * 
	 * @return the user's choice
	 */
	private int userChoice() {
		String[] options = new String[] { "Start a new order", "Take a look at your order history" };
		return ConsoleIO.promptForMenuSelection("Select one: ", options, true, "Exit");
	}

	/**
	 * Prompts the user to choose from all filters when listing their previous
	 * orders <br>
	 * The list of choices with their return values are: <br>
	 * &nbsp;&nbsp;&nbsp;<b>-1.</b> Exit to the order screen <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>0.</b> Filter by date ordered <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>1.</b> Filter by category of product <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>2.</b> Filter by the product's ID
	 * 
	 * @return - the user's choice as an int
	 */
	private int historyReviewFilter() {
		String[] options = new String[] { "Date ordered", "Category", "Product ID" };
		return ConsoleIO.promptForMenuSelection("Select your filter: ", options, true, "Exit");
	}

	/**
	 * Populates the storeProducts array from which the customer will add
	 * products to their order
	 */
	private void shopSetup() {
		storeProducts = new Product[] { new Product(1, 499.99f, "PS4", Category.ELECTRONICS),
				new Product(5, 529.99f, "OnePlus 5", Category.ELECTRONICS),
				new Product(2, 299.99f, "Nintendo Switch", Category.ELECTRONICS) };
	}

	/**
	 * Takes the user to pick out products to add to their order
	 */
	public void shop() {
		Order userOrder = new Order(1);

		int choice;
		while (true) {
			choice = MainView.promptUserForProduct(storeProducts);
			if (choice == -1) {
				if (!processOrder(userOrder)) {
					MainView.displayString("\nWe're sorry, we were unable to process your order at this time.\n");
					continue;
				} else {
					MainView.displayString("\nYour order has been processed and shipped! Thank you for "
							+ "shopping with us at the Humongous Store\u2122!\n");
					viewOrder(userOrder.getOrderID());
					return;
				}
			}
			userOrder.addProduct(storeProducts[choice]);
			MainView.displayString("\n" + userOrder + "\n");
		}
	}

	/**
	 * Attempts to process the order into MongoDB
	 * 
	 * @param userOrder
	 *            - the user's order
	 * @return whether or not the order was processed successfully
	 */
	public boolean processOrder(Order userOrder) {
		if (userOrder.isEmpty()) {
			MainView.displayString("\nYou may not submit an empty order.\n");
			return false;
		} else {
			
			MongoClientURI uri = new MongoClientURI("mongodb://10.10.17.14:27017");
			
			// You can instantiate a MongoClient object without any parameters 
			// to connect to a MongoDB instance running on localhost on port 27017:
			// You must however change the IP to your VM ware (or where ever
			// mongo is installed) as the connection defaults to your local machine
			MongoClient mongoClient = new MongoClient(uri);
			
			String jsonString = null; 
			
			// Specify the name of the database to the getDatabase() method.
			// If a database does not exist, MongoDB creates the database
			// when you first store data for that database.
			MongoDatabase database = mongoClient.getDatabase("storedb");

			// Specify the name of the collection to the getCollection() method.
			// If a collection does not exist, MongoDB creates the collection
			// when you first store data for that collection.
			MongoCollection<Document> collection = database.getCollection("Orders");
			
			// Converting order to JSON string
			
			userOrder.setOrderDate(new Date());
			
			Gson gsonConv = new Gson();
			
			jsonString = gsonConv.toJson(userOrder);
			
			// creating new document(row in SQL) and inserting the JSON string
			Document doc = Document.parse(jsonString);
			
			collection.insertOne(doc);
			mongoClient.close();
			return true;
		}
	}
	
	public Order viewOrder(long orderID){
		MongoClientURI connectionString = new MongoClientURI("mongodb://10.10.17.14:27017");
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase database = mongoClient.getDatabase("storedb"); 
		MongoCollection<Document> collection = database.getCollection("Orders");
		
		BasicDBObject whereClause = new BasicDBObject("orderID", orderID);
		FindIterable<Document> docs = collection.find(whereClause);
		
		Gson gsonConv = new Gson();
		
		Order o = null;
		
		for (Document d : docs) {	
			d.remove("_id");
			String json = d.toJson();			
			o = gsonConv.fromJson(json, Order.class);
		}
		
		mongoClient.close();
		return o;
	}
}
