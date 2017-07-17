package MongoDbDBT230;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Model class representing a single product to order
 * 
 * @author Ramon Caballero
 *
 */
public class Product implements Serializable {

	private static final long serialVersionUID = 9158791822400713638L;

	private int productID;
	private float price;
	private String name;
	private Category category;

	/**
	 * Creates a new instance of a product
	 * 
	 * @param productID
	 *            - the ID with which to identify the product (must be unique)
	 * @param price
	 *            - the price of the product, in USD
	 * @param name
	 *            - the name of the product
	 * @param category
	 *            - the category of the product
	 */
	public Product(int productID, float price, String name, Category category) {
		this.setPrice(price);
		this.productID = productID;
		this.name = name;
		this.category = category;
	}

	/**
	 * Provides a string representation of the product for the user to see,
	 * displaying the product's category, name, and price in USD.
	 */
	@Override
	public String toString() {
		String prodString = "Category: " + category + " | Product: " + name;

		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
		prodString += " | Price: " + currencyFormatter.format(price);

		return prodString;
	}

	/**
	 * Compares two products by using their productIDs.
	 */
	@Override
	public boolean equals(Object o) {
		Product other = (Product) o;
		return this.productID == other.productID;
	}

	/**
	 * @return - the product's ID
	 */
	public int getProductID() {
		return productID;
	}

	/**
	 * @return - the product's price
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * Sets the product's price
	 * 
	 * @param price
	 *            - the product's new price
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * @return the product's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return - the product's category
	 */
	public Category getCategory() {
		return category;
	}
}
