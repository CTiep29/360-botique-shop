package objects;

public class StoreObject {
	public int store_id;
	public String store_name;

	public StoreObject(int store_id, String store_name) {
		super();
		this.store_id = store_id;
		this.store_name = store_name;
	}

	public StoreObject() {
		super();
	}

	public int getStore_id() {
		return store_id;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

}
