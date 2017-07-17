package MongoDbDBT230;

/**
 * Enumeration for describing the category that a product belongs to.
 * 
 * @author Ramon Caballero
 *
 */
public enum Category {

	ELECTRONICS("Electronics", 1);

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
