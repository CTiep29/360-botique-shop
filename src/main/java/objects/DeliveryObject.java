package objects;

public class DeliveryObject {
	private int delivery_quantity;
	private int delivery_price;
	private int delivery_note_id; // PK
	private int product_id; // PK

	public DeliveryObject() {
		super();
	}

	public DeliveryObject(int delivery_quantity, int delivery_price, int delivery_note_id, int product_id) {
		super();
		this.delivery_quantity = delivery_quantity;
		this.delivery_price = delivery_price;
		this.delivery_note_id = delivery_note_id;
		this.product_id = product_id;
	}

	public int getDelivery_quantity() {
		return delivery_quantity;
	}

	public int getDelivery_price() {
		return delivery_price;
	}

	public int getDelivery_note_id() {
		return delivery_note_id;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setDelivery_quantity(int delivery_quantity) {
		this.delivery_quantity = delivery_quantity;
	}

	public void setDelivery_price(int delivery_price) {
		this.delivery_price = delivery_price;
	}

	public void setDelivery_note_id(int delivery_note_id) {
		this.delivery_note_id = delivery_note_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

}
