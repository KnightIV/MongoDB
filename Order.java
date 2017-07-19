package MongoDbDBT230;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Model class representing an order with products within.
 * 
 * @author Ramon Caballero
 *
 */
public class Order implements Serializable {

	private static final long serialVersionUID = -3481568987740539789L;

	private int orderID;
	private float totalPrice;
	private Date orderDate;
	private ArrayList<Product> products = new ArrayList<Product>();

	/**
	 * Creates an order with the specified orderID.
	 * 
	 * @param orderID
	 *            - the ID with which to identify the order
	 */
	public Order(int orderID) {
		this.orderID = orderID;
	}

	/**
	 * Adds a product to the order.
	 * 
	 * @param prodToAdd
	 *            - the product to add
	 */
	public void addProduct(Product prodToAdd) {
		products.add(prodToAdd);
		updateTotalPrice();
	}

	/**
	 * Removes the specified product from the order.<br>
	 * In the case of multiple instances of the product existing in the order,
	 * it will remove the first occurrence of the product.
	 * 
	 * @param prodToRemove
	 *            - the product to remove
	 */
	public void removeProduct(Product prodToRemove) {
		products.remove(prodToRemove);
		updateTotalPrice();
	}

	/**
	 * Removes a product at a specific index in the list of products the order
	 * holds.
	 * 
	 * @param index
	 *            - the index at which to remove the product.
	 */
	public void removeProduct(int index) {
		products.remove(index);
		updateTotalPrice();
	}

	/**
	 * @return whether or not the order lacks any products
	 */
	public boolean isEmpty() {
		return products.isEmpty();
	}

	@Override
	public String toString() {
		String orderString = "Order " + orderID;
		for (Product p : products) {
			orderString += "\n\t" + p;
		}
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
		orderString += "\nTotal: " + currencyFormatter.format(totalPrice);
		return orderString;
	}

	/**
	 * Updates the total price of the order based on the products within the
	 * order.
	 */
	private void updateTotalPrice() {
		totalPrice = 0;
		for (int i = 0; i < products.size(); i++) {
			totalPrice += products.get(i).getPrice();
		}
	}

	/**
	 * @return - the orderID
	 */
	public int getOrderID() {
		return this.orderID;
	}

	/**
	 * @return the total price of the order
	 */
	public float getTotalPrice() {
		return this.totalPrice;
	}

	/**
	 * @return the date the order was placed
	 */
	public Date getOrderDate() {
		return orderDate;
	}

	/**
	 * Sets the orderDate
	 * 
	 * @param orderDate
	 *            - the date that this order was placed
	 */
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public ArrayList<Product> getProducts() {
		return products;
	}
}
