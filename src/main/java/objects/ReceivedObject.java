package objects;

public class ReceivedObject {
	private int received_price;
	private int received_quantity;
	private int product_id; // PK
	private int received_note_id; // PK

	public int getReceived_price() {
		return received_price;
	}

	public void setReceived_price(int received_price) {
		this.received_price = received_price;
	}

	public int getReceived_quantity() {
		return received_quantity;
	}

	public void setReceived_quantity(int received_quantity) {
		this.received_quantity = received_quantity;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getReceived_note_id() {
		return received_note_id;
	}

	public void setReceived_note_id(int received_note_id) {
		this.received_note_id = received_note_id;
	}

}
