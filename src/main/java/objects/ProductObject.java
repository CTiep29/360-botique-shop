package objects;

public class ProductObject {
	private int product_id; // PK
	private String product_name;
	private String product_size;
	private int product_price;
	private String product_unit;
	private String product_description;
	private int product_sex;
	private int product_quantity;
	private int product_sold;
	private int product_deleted;
	private Integer category_id;
	private String product_last_modified;
	public String product_created_date;
	public String product_image;

	public ProductObject() {
		super();
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_size() {
		return product_size;
	}

	public void setProduct_size(String product_size) {
		this.product_size = product_size;
	}

	public int getProduct_price() {
		return product_price;
	}

	public void setProduct_price(int product_price) {
		this.product_price = product_price;
	}

	public String getProduct_unit() {
		return product_unit;
	}

	public void setProduct_unit(String product_unit) {
		this.product_unit = product_unit;
	}

	public String getProduct_description() {
		return product_description;
	}

	public void setProduct_description(String product_description) {
		this.product_description = product_description;
	}

	public int getProduct_sex() {
		return product_sex;
	}

	public void setProduct_sex(int product_sex) {
		this.product_sex = product_sex;
	}

	public int getProduct_quantity() {
		return product_quantity;
	}

	public void setProduct_quantity(int product_quantity) {
		this.product_quantity = product_quantity;
	}

	public int getProduct_sold() {
		return product_sold;
	}

	public void setProduct_sold(int product_sold) {
		this.product_sold = product_sold;
	}

	public int getProduct_deleted() {
		return product_deleted;
	}

	public void setProduct_deleted(int product_deleted) {
		this.product_deleted = product_deleted;
	}

	public Integer getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getProduct_last_modified() {
		return product_last_modified;
	}

	public void setProduct_last_modified(String product_last_modified) {
		this.product_last_modified = product_last_modified;
	}

	public String getProduct_created_date() {
		return product_created_date;
	}

	public void setProduct_created_date(String product_created_date) {
		this.product_created_date = product_created_date;
	}

	public String getProduct_image() {
		return product_image;
	}

	public void setProduct_image(String product_image) {
		this.product_image = product_image;
	}
}
