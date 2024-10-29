package functionimpl;

import java.util.*;

import org.javatuples.Quartet;

import basic.BasicImpl;
import enums.EDIT_TYPE;
import enums.PRO_ORDER;
import libraries.Utilities_Date;
import objectfuctions.ProductFunction;
import objects.*;
import util.*;

import java.sql.*;

public class ProductFunctionImpl extends BasicImpl implements ProductFunction {
	public ProductFunctionImpl(ConnectionPool cp, String objectName) {
		super(cp, objectName);
	}

	public ProductFunctionImpl(ConnectionPool cp) {
		super(cp, "Product");
	}

	@Override
	public boolean addProduct(ProductObject item) {
		// Kiểm tra xem sản phẩm đã tồn tại trong CSDL hay chưa
		if (isExisting(item)) {
			return false;
		}
		// Sử dụng StringBuilder để xây dựng câu lệnh SQL
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO tblProduct(");
		sql.append("product_name, product_size, product_created_date,");
		sql.append("product_price, product_unit, product_description,");
		sql.append("product_sex, product_quantity, product_sold,");
		sql.append("product_deleted, category_id, product_last_modified,product_image)");
		sql.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		try {
			// Thực hiện câu lệnh SQL sử dụng PreparedSatatement
			PreparedStatement pre = this.con.prepareStatement(sql.toString());
			pre.setString(1, item.getProduct_name());
			pre.setString(2, item.getProduct_size());
			pre.setString(3, Utilities_Date.getDate());
			pre.setInt(4, item.getProduct_price());
			pre.setString(5, item.getProduct_unit());
			pre.setString(6, item.getProduct_description());
			pre.setInt(7, item.getProduct_sex());
			pre.setInt(8, item.getProduct_quantity());
			pre.setInt(9, item.getProduct_sold());
			pre.setInt(10, item.getProduct_deleted());
			pre.setInt(11, item.getCategory_id());
			pre.setString(12, Utilities_Date.getDate());
			pre.setString(13, item.getProduct_image());

			return this.add(pre);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return false;
	}

	private boolean isExisting(ProductObject item) {
		boolean flag = false;
		String sql = "SELECT product_id FROM tblproduct WHERE product_name='" + item.getProduct_name() + "' ";
		ResultSet rs = this.get(sql, 0);
		if (rs != null) {
			try {
				if (rs.next()) {
					flag = true;
				}
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	@Override
	public boolean editProduct(ProductObject item, EDIT_TYPE et) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE tblproduct SET ");
		switch (et) {
		case NORMAL:
			sql.append(" product_name = ?, product_size = ?,product_image = ?,");
			sql.append(" product_price = ?, product_unit = ?, product_description = ?,");
			sql.append(" product_sex = ?,category_id = ?, product_last_modified = ? ");
			break;
		case TRASH:
			sql.append(" product_deleted = 1, product_last_modified = ? ");
			break;
		case RESTORE:
			sql.append(" product_deleted = 0, product_last_modified = ? ");
			break;
		default:
		}
		sql.append(" WHERE product_id=?");
		// Biên dịch
		try {
			PreparedStatement pre = this.con.prepareStatement(sql.toString());
			switch (et) {
			case NORMAL:
				pre.setString(1, item.getProduct_name());
				pre.setString(2, item.getProduct_size());
				pre.setString(3, item.getProduct_image());
				pre.setInt(4, item.getProduct_price());
				pre.setString(5, item.getProduct_unit());
				pre.setString(6, item.getProduct_description());
				pre.setInt(7, item.getProduct_sex());
				pre.setInt(8, item.getCategory_id());
				pre.setString(9, Utilities_Date.getDate());
				pre.setInt(10, item.getProduct_id());
				break;
			case TRASH:
				pre.setString(1, Utilities_Date.getDate());
				pre.setInt(2, item.getProduct_id());
				break;
			case RESTORE:
				pre.setString(1, Utilities_Date.getDate());
				pre.setInt(2, item.getProduct_id());
				break;
			default:
			}

			return this.edit(pre);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public boolean delProduct(ProductObject item) {
		String sql = "DELETE FROM tblproduct WHERE product_id=?";
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setInt(1, item.getProduct_id());
			return this.del(pre);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ResultSet getProduct(int id) {
		String sql = "SELECT * FROM tblproduct WHERE product_id=?";

		return this.get(sql, id);
	} // trả về một ResultSet chứa thông tin của một sản phẩm dựa trên product_id

	@Override
	public ResultSet getProduct(int id, String name) {
		String sql = "SELECT * FROM tblproduct WHERE (product_id=?) and (product_name=?) ";
		return this.get(sql, id, name);
	}

	@Override
	public ArrayList<ResultSet> getProducts(ProductObject similar, int at, byte total) {
		// select tất bản ghi
		return this.getProducts(new Quartet<>(similar, at, total, PRO_ORDER.NAME));
	}

	@Override
	public ArrayList<ResultSet> getProducts(Quartet<ProductObject, Integer, Byte, PRO_ORDER> infos) {
		ProductObject similar = infos.getValue0();
		int at = infos.getValue1();
		byte total = infos.getValue2();
		PRO_ORDER po = infos.getValue3();

		String countDays = "(DATE(NOW()) - DATE(STR_TO_DATE(p.product_last_modified, \"%d/%m/%Y\"))) AS days";

		// select tất bản ghi
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT p.*, " + countDays + " FROM tblproduct p ");
		sql.append(this.createConditions(similar)).append(" ");
		switch (po) {
		case NAME:
			sql.append("ORDER BY product_name ASC ");
			break;
		default:
			sql.append("ORDER BY DATE(STR_TO_DATE(p.product_last_modified, \"%d/%m/%Y\")) DESC ");
			break;
		}
		sql.append("LIMIT ").append(at).append(", ").append(total).append("; ");
		sql.append("SELECT COUNT(product_id) AS TOTAL FROM tblproduct ").append(this.createConditions(similar))
				.append(";");
		return this.getRes(sql.toString());
	} // Trả về một danh sách ArrayList của ResultSet chứa danh sách sản phẩm dựa trên
		// thông tin bộ lọc và sắp xếp

	private StringBuilder createConditions(ProductObject similar) {
		StringBuilder tmp = new StringBuilder();

		if (similar != null) {
			if (similar.getCategory_id() == null) {
				if (similar.getProduct_deleted() > 0) {
					tmp.append("product_deleted>0 ");
				} else {
					tmp.append("product_deleted=0 ");
				}
			} else {
				tmp.append("product_deleted=0 AND category_id = " + similar.getCategory_id() + " ");
			}
		}

		// lấy từ khoá tìm kiếm
		String key = similar.getProduct_name();
		if (key != null && !key.equalsIgnoreCase("")) {
			tmp.append(" AND (");
			tmp.append("(product_name LIKE '%" + key + "%') OR ");
			tmp.append("(product_description LIKE '%" + key + "%') OR ");
			tmp.append("(product_sex = " + key + ") ");
			tmp.append(") ");
		}
		if (!tmp.toString().equalsIgnoreCase("")) {
			tmp.insert(0, " WHERE ");
		}

		return tmp;
	}

	public ArrayList<ProductObject> getProductObjects(ProductObject similar, byte total) {

		ArrayList<ProductObject> items = new ArrayList<>();
		ProductObject item;
		String sql = "SELECT * FROM tblproduct ";
		sql += "";
		sql += "ORDER BY product_id DESC";
		sql += "";
		sql += " LIMIT ?";

		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			// truyền giá trị cho tham số
			pre.setByte(1, total);
			ResultSet rs = pre.executeQuery();// Lấy về tập kết quả
			if (rs != null) {
				while (rs.next()) {
					item = new ProductObject();
					item.setProduct_id(rs.getInt("product_id"));
					item.setProduct_name(rs.getString("product_name"));
					item.setProduct_size(rs.getString("product_size"));
					item.setProduct_price(rs.getInt("product_price"));
					item.setProduct_unit(rs.getString("product_unit"));
					item.setProduct_description(rs.getString("product_description"));
					item.setProduct_sex(rs.getInt("product_sex"));
					item.setProduct_quantity(rs.getInt("product_quantity"));
					item.setProduct_sold(rs.getInt("product_sold"));
					item.setProduct_deleted(rs.getInt("product_deleted"));
					item.setCategory_id(rs.getInt("category_id"));
					item.setProduct_last_modified(rs.getString("product_last_modified"));

					// Đưa vào tập hợp
					items.add(item);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			// Trở về trạng thái an toàn của kết nối
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return items;
	}

	@Override
	public ResultSet getInventoryDay(int id) {
		String sql = "SELECT received_note_date, DATEDIFF(CURDATE(), received_note_date) AS days_passed, shipment_quantity";
		sql += " FROM tblshipment";
		sql += " INNER JOIN tblreceived_note ON tblshipment.received_note_id = tblreceived_note.received_note_id";
		sql += " WHERE shipment_quantity > 0  AND product_id = ?";
		sql += " HAVING days_passed >= 60";
		return this.get(sql, id);
	}

	@Override
	public ResultSet getInventoryQuantity() {
		String sql = "SELECT product_id, SUM(shipment_quantity) as total_quantity";
		sql += " FROM tblshipment";
		sql += " INNER JOIN tblreceived_note ON tblshipment.received_note_id = tblreceived_note.received_note_id";
		sql += " WHERE shipment_quantity > 0 AND DATEDIFF(CURDATE(), received_note_date) >= 60";
		sql += " GROUP BY product_id";
		return this.get(sql, 0);
	}
}
