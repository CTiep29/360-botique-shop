package objectfuctions;

import java.util.ArrayList;
import java.util.HashMap;

import objects.CategoryObject;
import objects.ProductObject;
import util.ConnectionPool;

public interface CategoryFunction {
	public boolean addCategory(CategoryObject item);

	public boolean editCategory(CategoryObject item);

	public boolean delCategory(CategoryObject item);

	public ArrayList<CategoryObject> getCategoryObject(CategoryObject similar, int at, byte total);

	public int getNoOfRecords();

	public int getNoOfRecordsByCID(int cid);

	public CategoryObject getCategory(int category_id);

	public ArrayList<ProductObject> getProductByCID(int cid, int at, byte total);

	public String getCategoryNameById(int categoryId);

	public ArrayList<CategoryObject> searchCategory(String name);

	public HashMap<String, Integer> getProductCountByCID();
	
	public ArrayList<CategoryObject> getAllCategory();

	public ConnectionPool getCP();

	public void releaseConnection();
}
