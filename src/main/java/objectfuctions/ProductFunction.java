package objectfuctions;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.javatuples.Quartet;

import enums.EDIT_TYPE;
import enums.PRO_ORDER;
import objects.ProductObject;
import util.ShareControl;

public interface ProductFunction extends ShareControl {
	public boolean addProduct(ProductObject item);

	public boolean editProduct(ProductObject item, EDIT_TYPE et);

	public boolean delProduct(ProductObject item);

	public ResultSet getProduct(int id);

	public ResultSet getProduct(int id, String name);

	public ArrayList<ResultSet> getProducts(ProductObject similar, int at, byte total);

	public ArrayList<ResultSet> getProducts(Quartet<ProductObject, Integer, Byte, PRO_ORDER> infos);

	public ResultSet getInventoryDay(int id);

	public ResultSet getInventoryQuantity();
}
