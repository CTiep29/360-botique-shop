package objects;

public class CategoryObject {
	public int category_id;
	public String category_name;
	public String category_description;
	public String category_image;
	public String category_created_date;
	public String category_last_modified;

	public CategoryObject() {

	}

	public CategoryObject(int category_id, String category_name, String category_description, String category_image,
			String category_created_date, String category_last_modified) {
		this.category_id = category_id;
		this.category_name = category_name;
		this.category_description = category_description;
		this.category_image = category_image;
		this.category_created_date = category_created_date;
		this.category_last_modified = category_last_modified;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getCategory_description() {
		return category_description;
	}

	public void setCategory_description(String category_description) {
		this.category_description = category_description;
	}

	public String getCategory_image() {
		return category_image;
	}

	public void setCategory_image(String category_image) {
		this.category_image = category_image;
	}

	public String getCategory_created_date() {
		return category_created_date;
	}

	public void setCategory_created_date(String category_created_date) {
		this.category_created_date = category_created_date;
	}

	public String getCategory_last_modified() {
		return category_last_modified;
	}

	public void setCategory_last_modified(String category_last_modified) {
		this.category_last_modified = category_last_modified;
	}

	@Override
	public String toString() {
		return "CategoryObject [category_id=" + category_id + ", category_name=" + category_name
				+ ", category_description=" + category_description + ", category_image=" + category_image
				+ ", category_created_date=" + category_created_date + ", category_last_modified="
				+ category_last_modified + "]";
	}
}
