import java.util.ArrayList;
import java.util.HashMap;

import com.tangosol.dev.assembler.If_acmpeq;
import com.tangosol.util.*;
import com.tangosol.util.LongArray.Iterator;

public class MDSearch {

	private static AbstractSparseArray idTree;
	private static HashMap<Long, AbstractSparseArray> priceMap;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public static int insert(long id, float price, long[] name) {
		Product product = (Product) idTree.get(id);
		if (product == null) {// insertion
			product = new Product();
			product.setId(id);
			product.setName(name);
			product.setPrice(price);
			idTree.set(id, product);
			// TODO insert other trees
				//update priceMap
			for (int i = 0; i < name.length; i++) {
				insertInPriceMap(product, name[i]);
			}
			return 1;
		} else {// update
			if (name.length == 0) {
				float oldPrice = product.getPrice();
				product.setPrice(price);
				updateInPriceMapPriceOnly(product,oldPrice);//update priceMap
				
			} else {
				float oldPrice = product.getPrice();
				long[] oldName = product.getName();
				product.setName(name);
				product.setPrice(price);
				updateInPriceMapAll(product,oldPrice,oldName);//update priceMap				
			}
			return 0;
		}
	}

	public static double find(long id) {
		Product product = (Product) idTree.get(id);
		if (product == null) {
			return 0.0;
		} else {
			return product.getPrice();
		}
	}

	public static long delete(long id) {
		Product product = (Product) idTree.get(id);
		if (product == null) {
			return 0;
		} else {
			deleteInPriceMap(product);
			product = (Product) idTree.remove(id);
			long[] name = product.getName();
			long sum = 0L;
			for (int i = 0; i < name.length; i++) {
				sum += name[i];
			}
			return sum;
		}
	}

	public static void deleteInPriceMap(Product product) {
		Product oldProduct = (Product) idTree.get(product.getId());
		long[] key = oldProduct.getName();
		for (int i = 0; i < key.length; i++) {
			AbstractSparseArray asa = priceMap.get(key[i]);
			if (asa == null) {
				throw new RuntimeException("no such map");
			} else {
				float oldPrice = oldProduct.getPrice();
				Object getObject = asa.get((long) (100 * oldPrice));
				if (getObject instanceof ArrayList) {// there are duplicate
														// prices
					ArrayList<Product> list = (ArrayList<Product>) getObject;
					Product delete = null;
					boolean turnToSingle = false;
					if (list.size() == 2) {// after deletion, it will become a
											// plain node
						turnToSingle = true;
					}
					for (Product possibleProduct : list) {
						if (possibleProduct.getId() == product.getId()) {
							delete = possibleProduct;
							break;
						}
					}
					list.remove(delete);
					if (turnToSingle) {
						list = (ArrayList<Product>) asa
								.remove((long) (100 * oldPrice));
						insertInPriceMap(list.get(0),key[i]);
					}
				} else {// it is a plain node
					asa.remove((long) (100 * oldPrice));
				}
			}
		}
	}
	
	public static void deleteInPriceMap(Product product,float oldPrice) {
		long[] key = product.getName();
		for (int i = 0; i < key.length; i++) {
			AbstractSparseArray asa = priceMap.get(key[i]);
			if (asa == null) {
				throw new RuntimeException("no such map");
			} else {
				Object getObject = asa.get((long) (100 * oldPrice));
				if (getObject instanceof ArrayList) {// there are duplicate
														// prices
					ArrayList<Product> list = (ArrayList<Product>) getObject;
					Product delete = null;
					boolean turnToSingle = false;
					if (list.size() == 2) {// after deletion, it will become a
											// plain node
						turnToSingle = true;
					}
					for (Product possibleProduct : list) {
						if (possibleProduct.getId() == product.getId()) {
							delete = possibleProduct;
							break;
						}
					}
					list.remove(delete);
					if (turnToSingle) {
						list = (ArrayList<Product>) asa
								.remove((long) (100 * oldPrice));
						insertInPriceMap(list.get(0),key[i]);
					}
				} else {// it is a plain node
					asa.remove((long) (100 * oldPrice));
				}
			}
		}
	}
	
	public static void deleteInPriceMap(Product product,float oldPrice,long[]oldName) {
		for (int i = 0; i < oldName.length; i++) {
			AbstractSparseArray asa = priceMap.get(oldName[i]);
			if (asa == null) {
				throw new RuntimeException("no such map");
			} else {
				Object getObject = asa.get((long) (100 * oldPrice));
				if (getObject instanceof ArrayList) {// there are duplicate
														// prices
					ArrayList<Product> list = (ArrayList<Product>) getObject;
					Product delete = null;
					boolean turnToSingle = false;
					if (list.size() == 2) {// after deletion, it will become a
											// plain node
						turnToSingle = true;
					}
					for (Product possibleProduct : list) {
						if (possibleProduct.getId() == product.getId()) {
							delete = possibleProduct;
							break;
						}
					}
					list.remove(delete);
					if (turnToSingle) {
						list = (ArrayList<Product>) asa
								.remove((long) (100 * oldPrice));
						insertInPriceMap(list.get(0),oldName[i]);
					}
				} else {// it is a plain node
					asa.remove((long) (100 * oldPrice));
				}
			}
		}
	}

	/**
	 * Note: For priceMap, the update is simply delete the old 
	 * product and insert a new one
	 * This is the one when only new price is provided, 
	 * so we insert the node with new price but old names
	 */
	public static void updateInPriceMapPriceOnly(Product product) {
		 long []name = ((Product) (idTree.get(product.getId())))
			.getName();
		deleteInPriceMap(product);//delete with the old name sets
		for (int i = 0; i < name.length; i++) {
			insertInPriceMap(product, name[i]);
		}		
	}
	
	public static void updateInPriceMapPriceOnly(Product product,float oldPrice) {
		 long []name = ((Product) (idTree.get(product.getId())))
			.getName();
		deleteInPriceMap(product,oldPrice);//delete with the old name sets
		for (int i = 0; i < name.length; i++) {
			insertInPriceMap(product, name[i]);
		}		
	}
	
	private static void insertInPriceMap(Product product, long partName) {
		AbstractSparseArray  asa = priceMap.get(partName);
		long index = (long) (100 * product.getPrice());
		if (asa != null) {//there is some data associated with this part of name
			Object getObject = asa.get(index);
			if(getObject instanceof ArrayList){//there is more than one product with the same price
				ArrayList<Product> list = (ArrayList<Product>) getObject;
				list.add(product);
			}else{//there is only one, but afterwards it will be 2
//				asa.set((long) (100 * product.getPrice()), product);
				ArrayList<Product> list = new ArrayList<Product>();
				list.add((Product)getObject);
				list.add(product);
				asa.remove(index);
				asa.set(index, list);
			}			
		} else {//there is not a one with same price, just insert it.
			asa = new SparseArray();
			asa.set(index, product);
			priceMap.put(partName, asa);
		}
	}

	/**
	 * 
	 * @param product
	 */
	public static void updateInPriceMapAll(Product product){
		long[] key = product.getName();
		long []name = ((Product) (idTree.get(product.getId())))
				.getName();
		product.setName(name);
		deleteInPriceMap(product);//delete with old name
		for (int i = 0; i < key.length; i++) {//insert with new name group
			insertInPriceMap(product, key[i]);
		}
	}
	
	public static void updateInPriceMapAll(Product product, float oldPrice,long[]oldName){
		long[] key = product.getName();
		deleteInPriceMap(product,oldPrice,oldName);//delete with old name
		for (int i = 0; i < key.length; i++) {//insert with new name group
			insertInPriceMap(product, key[i]);
		}
	}



	public static float findMinPrice(long w) {
		AbstractSparseArray asa = priceMap.get(w);
		long first = asa.getFirstIndex();
		float result = first/100;
		return result;
	}

	public static float findMaxPrice(long w) {
		AbstractSparseArray asa = priceMap.get(w);
		long last = asa.getLastIndex();
		float result = last/100;
		return result;
	}

	public static long findPriceRange(long n, int low, int high) {
		long result = 0L;
		AbstractSparseArray asa=priceMap.get(n);
		Iterator i=asa.iterator();
		while (i.hasNext()) {
			Object nextObject = i.next();
			float price = i.getIndex()/100;
			if (price>high) {
				break;
			}
			if (price>=low&&price<=high) {
				if (nextObject instanceof ArrayList) {
					ArrayList<Product> list= (ArrayList<Product>) nextObject;
					result+=list.size();
				}else {
					result+=1;
				}
			}			
		}
		return result;
	}

	public static float priceHike(long l, long h, int percentage) {
		float sum = 0f;
		long firstExist = 0L;
		for(long tempt =l;tempt<=h;tempt++){
			if (idTree.exists(tempt)) {
				firstExist = tempt;
				break;
			}
		}
		LongArray.Iterator iterator = idTree.iterator(firstExist);
		while (iterator.hasNext()) {
			Product object = (Product) iterator.next();
			float oldPrice = object.getPrice();
			float offset = Float.parseFloat(String.format("%2f", oldPrice*(percentage/100.0f)));
			sum+=offset;
			object.setPrice(oldPrice+offset);
			updateInPriceMapPriceOnly(object, oldPrice);
		}
		
		return sum;
	}

	public static void updateMap() {

	}

}
