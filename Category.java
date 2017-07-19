package MongoDbDBT230;

/**
 * Enumeration for describing the category that a product belongs to.
 * 
 * @author Ramon Caballero
 *
 */
public enum Category {

	ELECTRONICS("Electronics", 1), ANIMALS("Animals", 2), CLOTHING("Clothing", 3), VEHICLES("Vehicles", 4);

	private String category;
	private long categoryID;

	private Category(String category, long categoryID) {
		this.category = category;
		this.categoryID = categoryID;
	}

	public long getCategoryID() {
		return this.categoryID;
	}

	@Override
	public String toString() {
		return this.category;
	}
}
