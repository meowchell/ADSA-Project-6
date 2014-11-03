import com.tangosol.util.*;

public class MDSearch {

	
	private static AbstractSparseArray idTree;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public static int insert(long id, float price, long[] name) {

		Product product = (Product) idTree.get(id);
		if (product==null) {//insertion
			product = new Product();
			product.setId(id);
			product.setName(name);
			product.setPrice(price);
			idTree.set(id, product);
			//TODO update other trees
			return 1;
		}else{//update
			if (name.length==0) {
				product.setPrice(price);
			}else {
				product.setName(name);
				product.setPrice(price);
			}
			//TODO update other trees
			return 0;
		}
	}

	public static double find(long id) {
		Product product = (Product) idTree.get(id);
		if(product==null){
			return 0.0;
		}else{
			return product.getPrice();
		}		
	}

	public static void delete(long id) {

	}

	public static void findMinPrice(long w) {

	}

	public static void findMaxPrice(long w) {

	}

	
	public static void findPriceRange(long n, int low, int high){
		
	}
	
	public static void priceHike(long l, long h, int high){
		
	}
	
	public static void updateMap(){
		
	}
	
	
}
