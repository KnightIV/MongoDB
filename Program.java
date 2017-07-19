package MongoDbDBT230;

import java.text.NumberFormat;
import java.time.Month;
import java.util.Date;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
				MongoClientURI uri = new MongoClientURI("mongodb://10.10.17.14:27017");
				MongoClient mongoClient = new MongoClient(uri);
				MongoDatabase database = mongoClient.getDatabase("storedb");
				MongoCollection<Document> collection = database.getCollection("Aggregate");
				String report = "";
				NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

				switch (historyReviewFilter()) {
				case -1:
					mongoClient.close();
					break;

				// See a specific order
				case 0:
					int orderID = ConsoleIO.promptForInt("Type the order ID: ", 1, Integer.MAX_VALUE);
					MainView.displayString(viewOrder(orderID).toString());
					break;

				// See orders by month
				case 1:
					BasicDBObject monthFilter = new BasicDBObject("month",
							new BasicDBObject("$gt", 0).append("$lte", 11));
					FindIterable<Document> monthlySales = collection.find(monthFilter);
					for (Document d : monthlySales) {
						int curMonth = (Integer) d.get("month");
						double totalPrice = (Double) d.get("totalPrice");
						report += Month.of(curMonth + 1) + " Revenue: " + currencyFormatter.format(totalPrice) + "\n";
					}
					MainView.displayString(report);
					mongoClient.close();
					break;

				// See total sales EVER
				case 2:
					FindIterable<Document> totalSales = collection.find();
					for (Document d : totalSales) {
						if (d.containsKey("totalSales")) {
							report += "Total sales EVER: "
									+ currencyFormatter.format(d.get("totalSales", Double.class));
						}
					}
					MainView.displayString(report);
					mongoClient.close();
					break;

				// Sales by product
				case 3:
					FindIterable<Document> prodIterate = collection.find();
					for (Document d : prodIterate) {
						for (Product p : storeProducts) {
							String value = d.get("productName", String.class);
							if (value != null && value.equals(p.getName())) {
								report += "Product: " + p.getName() + " | Quantity sold: " + d.get("quantity") + "\n";
								break;
							}
						}
					}
					MainView.displayString(report);
					mongoClient.close();
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
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>0.</b> Filter by a specific orderID <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>1.</b> Filter by month <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>2.</b> Filter by total sales EVER <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<b>3.</b> Filter by sale quantity by product
	 * 
	 * @return - the user's choice as an int
	 */
	private int historyReviewFilter() {
		String[] options = new String[] { "See a specific order", "See orders by month", "See total sales EVER",
				"See sales by product" };
		return ConsoleIO.promptForMenuSelection("Select your filter: ", options, true, "Exit");
	}

	/**
	 * Populates the storeProducts array from which the customer will add
	 * products to their order
	 */
	private void shopSetup() {
		storeProducts = new Product[] { new Product(1, 499.99f, "PS4", Category.ELECTRONICS),
				new Product(5, 529.99f, "OnePlus 5", Category.ELECTRONICS),
				new Product(2, 299.99f, "Nintendo Switch", Category.ELECTRONICS),
				new Product(13, 999.99f, "Elephant", Category.ANIMALS),
				new Product(19, 1499.99f, "Giraffe", Category.ANIMALS),
				new Product(10, 450.00f, "Hippopotamus", Category.ANIMALS),
				new Product(21, 99.99f, "Zero-G Shoes", Category.CLOTHING),
				new Product(27, 299.99f, "Stilettos", Category.CLOTHING),
				new Product(25, 0.99f, "Humongous\u2122 Underwear", Category.CLOTHING),
				new Product(30, 100000.00f, "Aphelion Spaceship", Category.VEHICLES),
				new Product(1000, 14000000.0f, "Millenium Falcon", Category.VEHICLES),
				new Product(33, 50.00f, "Tank destroyer", Category.VEHICLES)};
	}

	/**
	 * Takes the user to pick out products to add to their order
	 */
	public void shop() {
		Order userOrder = new Order(getNextOrderID());

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
	 * Gets the next orderID so that orders in the database don't clash
	 * 
	 * @return - the next order's ID
	 */
	private int getNextOrderID() {
		MongoClientURI uri = new MongoClientURI("mongodb://10.10.17.14:27017");
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("storedb");
		MongoCollection<Document> collection = database.getCollection("Aggregate");
		if (collection.count() == 0) {
			Document doc = new Document();
			doc.append("orderID", 2);
			collection.insertOne(doc);
			mongoClient.close();
			return 1;
		}
		FindIterable<Document> docs = collection.find();
		int curID = 0;
		for (Document d : docs) {
			if (d.containsKey("orderID"))
				curID = (Integer) d.get("orderID");
		}
		BasicDBObject newDoc = new BasicDBObject().append("$inc", new BasicDBObject().append("orderID", 1));
		collection.updateOne(new BasicDBObject().append("orderID", curID), newDoc);
		mongoClient.close();
		return curID;
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
			// to connect to a MongoDB instance running on localhost on port
			// 27017:
			// You must however change the IP to your VM ware (or where ever
			// mongo is installed) as the connection defaults to your local
			// machine
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

			updateAggregateData(mongoClient, userOrder);

			mongoClient.close();

			return true;
		}
	}

	/**
	 * Updates all aggregate data in the database through the order the user
	 * submitted
	 * 
	 * @param client
	 *            - the mongo client to use
	 * @param userOrder
	 *            - the user's order
	 */
	@SuppressWarnings("deprecation")
	private void updateAggregateData(MongoClient client, Order userOrder) {
		MongoDatabase database = client.getDatabase("storedb");
		MongoCollection<Document> collection = database.getCollection("Aggregate");

		// Update monthly sales
		BasicDBObject dateWhere = new BasicDBObject("month", userOrder.getOrderDate().getMonth());
		FindIterable<Document> doc = collection.find(dateWhere);
		MongoCursor<Document> cursor = doc.iterator();
		if (!cursor.hasNext()) {
			Document curMonthSale = new Document();
			curMonthSale.append("month", userOrder.getOrderDate().getMonth());
			curMonthSale.append("totalPrice", userOrder.getTotalPrice());
			collection.insertOne(curMonthSale);
		} else {
			BasicDBObject newDoc = new BasicDBObject().append("$inc",
					new BasicDBObject().append("totalPrice", userOrder.getTotalPrice()));
			collection.updateOne(new BasicDBObject().append("month", userOrder.getOrderDate().getMonth()), newDoc);
		}

		// Update total sales EVER
		FindIterable<Document> allDocs = collection.find();
		MongoCursor<Document> totalCursor = allDocs.iterator();
		boolean wasFound = false;
		while (totalCursor.hasNext()) {
			Document nextDoc = totalCursor.next();
			if (nextDoc.containsKey("totalSales")) {
				double curTotalPrice = nextDoc.get("totalSales", Double.class);
				BasicDBObject newDoc = new BasicDBObject().append("$inc",
						new BasicDBObject().append("totalSales", userOrder.getTotalPrice()));
				collection.updateOne(new BasicDBObject().append("totalSales", curTotalPrice), newDoc);
				wasFound = true;
				break;
			}
		}

		if (!wasFound) {
			Document totalSales = new Document();
			totalSales.append("totalSales", userOrder.getTotalPrice());
			collection.insertOne(totalSales);
		}

		// Update sales by product
		for (Product p : userOrder.getProducts()) {
			BasicDBObject productFilter = new BasicDBObject("productName", p.getName());
			FindIterable<Document> productDocIterate = collection.find(productFilter);
			MongoCursor<Document> prodCursor = productDocIterate.iterator();

			if (prodCursor.hasNext()) {
				BasicDBObject newDoc = new BasicDBObject().append("$inc", new BasicDBObject().append("quantity", 1));
				collection.updateOne(new BasicDBObject().append("productName", p.getName()), newDoc);
			} else {
				Document prodDoc = new Document();
				prodDoc.append("productName", p.getName());
				prodDoc.append("quantity", 1);
				collection.insertOne(prodDoc);
			}
		}
	}

	/**
	 * Returns a specific order to display to the user
	 * 
	 * @param orderID
	 *            - the order's orderID
	 * @return - the order to display
	 */
	public Order viewOrder(int orderID) {
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
