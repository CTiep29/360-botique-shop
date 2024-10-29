package objects;

public class ReceivedNoteObject {
	private int received_note_id; // PK
	private String received_note_date;
	private int received_note_total;
	private int supplier_id;
	private Integer user_id;

	public int getReceived_note_id() {
		return received_note_id;
	}

	public void setReceived_note_id(int received_note_id) {
		this.received_note_id = received_note_id;
	}

	public String getReceived_note_date() {
		return received_note_date;
	}

	public void setReceived_note_date(String received_note_date) {
		this.received_note_date = received_note_date;
	}

	public int getReceived_note_total() {
		return received_note_total;
	}

	public void setReceived_note_total(int received_note_total) {
		this.received_note_total = received_note_total;
	}

	public int getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(int supplier_id) {
		this.supplier_id = supplier_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

}
