public class Product {

	/**
	 * @param args
	 */

	long id;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long[] getName() {
		return name;
	}

	public void setName(long[] name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	long[] name;
	float price;

	public Product() {

	}

	public Product(long id, long[] name, float price) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
	}

}
