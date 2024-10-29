package objects;

public class DeliveryNoteObject {
	private int delivery_note_id; // PK
	private String delivery_note_date;
	private short store_id;
	private int delivery_note_value;
	private Integer  user_id;

	public int getDelivery_note_id() {
		return delivery_note_id;
	}

	public void setDelivery_note_id(int delivery_note_id) {
		this.delivery_note_id = delivery_note_id;
	}

	public String getDelivery_note_date() {
		return delivery_note_date;
	}

	public void setDelivery_note_date(String delivery_note_date) {
		this.delivery_note_date = delivery_note_date;
	}

	public short getStore_id() {
		return store_id;
	}

	public void setStore_id(short store_id) {
		this.store_id = store_id;
	}

	public int getDelivery_note_value() {
		return delivery_note_value;
	}

	public void setDelivery_note_value(int delivery_note_value) {
		this.delivery_note_value = delivery_note_value;
	}

	public Integer  getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

}
