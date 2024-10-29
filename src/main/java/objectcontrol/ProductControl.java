package objectcontrol;

import java.util.*;
import org.javatuples.*;

import enums.EDIT_TYPE;
import enums.PRO_ORDER;
import objectmodels.ProductModel;
import objects.ProductObject;
import objects.UserObject;
import util.ConnectionPool;
import views.InventoryProducts;
import views.Products;

public class ProductControl {
	private ProductModel pm;

	public ProductControl(ConnectionPool cp) {
		this.pm = new ProductModel(cp);
	}

	public ConnectionPool getCP() {
		return this.pm.getCP();
	}

	public void releaseConnection() {
		this.pm.releaseConnection();
	}

	public boolean addProduct(ProductObject item) {
		return this.pm.addProduct(item);
	}

	public boolean editProduct(ProductObject item, EDIT_TYPE et) {
		return this.pm.editProduct(item, et);
	}

	public boolean delProduct(ProductObject item) {
		return this.pm.delProduct(item);
	}

	public ProductObject getProductObject(int id) {
		return this.pm.getProductObject(id);
	}

	public ProductObject getProductObject(int id, String name) {
		return this.pm.getProductObject(id, name);
	}

	public Pair<ArrayList<HashMap<String, Object>>, String> getInventoryList(int id) {
		ArrayList<HashMap<String, Object>> arr = this.pm.getInventoryDayList(id);
		ProductObject prObj = this.pm.getProductObject(id);
		String pro_name = prObj.getProduct_name();
		return new Pair<>(arr, pro_name);
	}

	public Pair<ArrayList<HashMap<String, Object>>, Integer> getInventoryQuantityList() {
		Pair<ArrayList<HashMap<String, Object>>, Integer> infor = this.pm.getInventoryQuantityList();
		ArrayList<HashMap<String, Object>> arr = infor.getValue0();
		for (int i = 0; i < arr.size(); i++) {
			HashMap<String, Object> item = arr.get(i);
			int id = (int) item.get("product_id");
			ProductObject prObj = this.pm.getProductObject(id);
			HashMap<String, Object> newItem = new HashMap<>(item);
			newItem.put("product_name", prObj.getProduct_name());
			newItem.put("product_size", prObj.getProduct_size());
			newItem.put("product_sex", prObj.getProduct_sex());
			newItem.put("total_quantity", item.get("total_quantity"));
			arr.set(i, newItem);
		}
		int total = infor.getValue1();
		return new Pair<>(arr, total);
	}

	public ArrayList<String> viewInventoryProducts(short page, byte totalPerPage) {
		Pair<ArrayList<HashMap<String, Object>>, Integer> datas = this.getInventoryQuantityList();
		return InventoryProducts.viewInventoryProducts(datas, page, totalPerPage);
	}

	public ArrayList<String> viewProducts(Quartet<ProductObject, Short, Byte, PRO_ORDER> infos, UserObject user) {
		Triplet<ArrayList<ProductObject>, Integer, HashMap<Integer, Integer>> datas = this.pm.getProductObjects(infos);

		ProductObject similar = infos.getValue0();
		if (similar.getProduct_deleted() == 0) {
			return Products.viewProducts(datas, infos, user);
		} else {
			return Products.viewDeletedProducts(datas, infos, user);
		}
	}
}
