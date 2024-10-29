package functionimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.javatuples.Triplet;

import basic.BasicImpl;
import objectfuctions.DeliveryNoteFunction;
import objects.DeliveryNoteObject;
import objects.DeliveryObject;
import objects.StoreObject;
import util.ConnectionPool;

public class DeliveryNoteFunctionImpl extends BasicImpl implements DeliveryNoteFunction {
	public DeliveryNoteFunctionImpl(ConnectionPool cp, String objectName) {
		super(cp, objectName);
	}

	public DeliveryNoteFunctionImpl(ConnectionPool cp) {
		super(cp, "DeliveryNote");
	}

	// thêm phiếu xuất
	@Override
	public boolean addDeliveryNote(DeliveryNoteObject item, ArrayList<DeliveryObject> itemDetails) {
		StringBuilder sqlNote = new StringBuilder();
		sqlNote.append(
				"INSERT INTO tbldelivery_note(delivery_note_date, store_id, user_id, delivery_note_value, delivery_note_profit) VALUES(?,?,?,0,0)");
		StringBuilder sqlDetails = new StringBuilder();
		sqlDetails.append(
				"INSERT INTO tbldelivery(product_id, delivery_price,delivery_quantity,delivery_note_id) VALUES(?, ?, ?,?)");
		StringBuilder sqlUpdateQuantity = new StringBuilder();
		sqlUpdateQuantity.append("UPDATE tblproduct SET product_quantity = ?, product_sold = ? WHERE product_id = ?");
		String sqlTotal = "SELECT SUM(delivery_price * delivery_quantity) AS value FROM tbldelivery WHERE delivery_note_id = ?";
		StringBuilder sqlUpdate = new StringBuilder();
		sqlUpdate.append(
				"UPDATE tbldelivery_note SET delivery_note_value = ?, delivery_note_profit = ? WHERE delivery_note_id = ?");
		try {
			this.con.setAutoCommit(false);
			// Thực hiện chèn dữ liệu vào bảng tbldelivery_note
			PreparedStatement preNote = this.con.prepareStatement(sqlNote.toString(),
					PreparedStatement.RETURN_GENERATED_KEYS);
			preNote.setString(1, item.getDelivery_note_date());
			preNote.setInt(2, item.getStore_id());
			preNote.setInt(3, item.getUser_id());
			preNote.executeUpdate();

			ResultSet generatedKeys = preNote.getGeneratedKeys();
			int deliveryNoteId = 0;
			int profit = 0;
			if (generatedKeys.next()) {
				deliveryNoteId = generatedKeys.getInt(1);
			}

			PreparedStatement preDetails = this.con.prepareStatement(sqlDetails.toString());
			PreparedStatement preQuantity = this.con.prepareStatement(sqlUpdateQuantity.toString());
			for (DeliveryObject deliveryObj : itemDetails) {
				preDetails.setInt(1, deliveryObj.getProduct_id());
				preDetails.setInt(2, deliveryObj.getDelivery_price());
				preDetails.setInt(3, deliveryObj.getDelivery_quantity());
				preDetails.setInt(4, deliveryNoteId);
				int quantity = getQuantity(deliveryObj.getProduct_id())[0];
				int sold_quantity = getQuantity(deliveryObj.getProduct_id())[1];
				quantity -= deliveryObj.getDelivery_quantity();
				sold_quantity += deliveryObj.getDelivery_quantity();
				preQuantity.setInt(1, quantity);
				preQuantity.setInt(2, sold_quantity);
				preQuantity.setInt(3, deliveryObj.getProduct_id());
				profit += getProfitOfProduct(deliveryObj);
				preDetails.addBatch();
				preQuantity.addBatch();
			}
			preDetails.executeBatch();
			preQuantity.executeBatch();

			PreparedStatement preTotal = this.con.prepareStatement(sqlTotal);
			preTotal.setInt(1, deliveryNoteId);
			ResultSet rsTotal = preTotal.executeQuery();
			int value = 0;
			if (rsTotal.next()) {
				value = rsTotal.getInt("value");
			}

			PreparedStatement preUpdate = this.con.prepareStatement(sqlUpdate.toString());
			preUpdate.setInt(1, value);
			preUpdate.setInt(2, profit);
			preUpdate.setInt(3, deliveryNoteId);
			preUpdate.executeUpdate();

			this.con.commit(); // Hoàn thành giao dịch
			this.con.setAutoCommit(true); // Đặt lại chế độ tự động commit
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				this.con.rollback(); // Rollback giao dịch nếu xảy ra lỗi
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	public int getProfitOfProduct(DeliveryObject pro) {
		int profit = 0;
		int pro_id = pro.getProduct_id();
		int quantity = pro.getDelivery_quantity();
		int price = pro.getDelivery_price();
		while (quantity != 0) {
			int[] infor = getShipment(pro_id);
			int receivedNoteId = infor[0];
			int receivedQuantity = infor[2];
			int receivedPrice = infor[1];
			if (quantity >= receivedQuantity) {
				quantity -= receivedQuantity;
				profit += receivedQuantity * (price - receivedPrice);
				receivedQuantity = 0;
			} else {
				receivedQuantity -= quantity;
				profit += quantity * (price - receivedPrice);
				quantity = 0;
			}
			setStock(receivedNoteId, receivedQuantity, pro_id);
		}
		return profit;
	}

	public void setStock(int received_note_id, int quantityRemaining, int productID) {
		StringBuilder sqlUpdate = new StringBuilder();
		sqlUpdate.append("UPDATE tblshipment SET shipment_quantity = ? WHERE received_note_id = ? AND product_id = ?");
		try {
			PreparedStatement preUpdate = this.con.prepareStatement(sqlUpdate.toString());
			preUpdate.setInt(1, quantityRemaining);
			preUpdate.setInt(2, received_note_id);
			preUpdate.setInt(3, productID);
			preUpdate.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int[] getShipment(int id) {
		int[] result = { 0, 0, 0 };
		String sql = "SELECT received_note_id, shipment_price, shipment_quantity FROM tblshipment";
		sql += " WHERE product_id = ? AND shipment_quantity > 0";
		sql += " LIMIT 1";
		ResultSet rs = this.get(sql, id);
		if (rs != null) {
			try {
				while (rs.next()) {
					result[0] = rs.getInt("received_note_id");
					result[1] = rs.getInt("shipment_price");
					result[2] = rs.getInt("shipment_quantity");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public ArrayList<ResultSet> getDeliveryNotes(DeliveryNoteObject similar, int at, byte total) {
		// select tất bản ghi
		return this.getDeliveryNotes(new Triplet<>(similar, at, total));
	}

	@Override
	public ArrayList<ResultSet> getDeliveryNotes(Triplet<DeliveryNoteObject, Integer, Byte> infos) {
		DeliveryNoteObject similar = infos.getValue0();
		int at = infos.getValue1();
		byte total = infos.getValue2();

		// select tất bản ghi
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM tbldelivery_note ");
		sql.append(this.createConditions(similar)).append(" ");
		sql.append("ORDER BY delivery_note_id DESC ");
		sql.append("LIMIT ").append(at).append(", ").append(total).append("; ");

		// select lấy tổng số bản ghi
		sql.append("SELECT COUNT(delivery_note_id) AS TOTAL FROM tbldelivery_note ")
				.append(this.createConditions(similar)).append(";");
		return this.getRes(sql.toString());
	}

	private StringBuilder createConditions(DeliveryNoteObject similar) {
		StringBuilder tmp = new StringBuilder();
		if (similar != null) {
			tmp.append("WHERE 1=1 ");
			if (similar.getDelivery_note_date() != null) {
				tmp.append("AND delivery_note_date = '").append(similar.getDelivery_note_date()).append("' ");
			}
			if (similar.getUser_id() != null) {
				tmp.append("AND user_id = ").append(similar.getUser_id()).append(" ");
			}
		}

		return tmp;
	}

	// lấy chi tiết phiếu xuất
	@Override
	public ResultSet getDetails(int id) {
		String sql = "SELECT tblproduct.product_name, tbldelivery.product_id, tblproduct.product_unit, delivery_quantity,delivery_price FROM tbldelivery_note ";
		sql += "";
		sql += "INNER JOIN tbldelivery ON tbldelivery_note.delivery_note_id = tbldelivery.delivery_note_id ";
		sql += "INNER JOIN tblproduct ON tbldelivery.product_id = tblproduct.product_id ";
		sql += "WHERE tbldelivery_note.delivery_note_id = ? ";
		return this.get(sql, id);
	}

	public ArrayList<StoreObject> getStores() {
		ArrayList<StoreObject> items = new ArrayList<>();
		StoreObject item;
		String sql = "SELECT * FROM tblstore ";

		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			// truyền giá trị cho tham số
			ResultSet rs = pre.executeQuery();// Lấy về tập kết quả
			if (rs != null) {
				while (rs.next()) {
					item = new StoreObject();
					item.setStore_id(rs.getInt("store_id"));
					item.setStore_name(rs.getString("store_name"));
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
	public ResultSet getDeliveryNote(int id) {
		String sql = "SELECT delivery_note_date, delivery_note_value, store_name, user_fullname, tbldelivery_note.user_id FROM tbldelivery_note ";
		sql += "INNER JOIN tblstore ON tbldelivery_note.store_id = tblstore.store_id ";
		sql += "INNER JOIN tbluser ON tbldelivery_note.user_id = tbluser.user_id ";
		sql += "WHERE delivery_note_id = ?;";
		return this.get(sql, id);
	}

	public int[] getQuantity(int id) {
		int[] result = { 0, 0 };
		String sql = "SELECT product_quantity, product_sold FROM tblproduct ";
		sql += "WHERE product_id = ?";
		ResultSet rs = this.get(sql, id);
		if (rs != null) {
			try {
				while (rs.next()) {
					result[0] = rs.getInt("product_quantity");
					result[1] = rs.getInt("product_sold");
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public ResultSet getProfit(String date) {
		String sql = "SELECT SUM(delivery_note_profit) as total_profit FROM tbldelivery_note";
		String patternDate = "\\d{4}-\\d{2}-\\d{2}";
		String patternMonth = "\\d{4}-\\d{2}";
		if (Pattern.matches(patternDate, date)) {
			sql += " WHERE DATE_FORMAT(tbldelivery_note.delivery_note_date, \"%Y-%m-%d\") = ?";
		} else if (Pattern.matches(patternMonth, date)) {
			sql += " WHERE DATE_FORMAT(tbldelivery_note.delivery_note_date, \"%Y-%m\") = ?";
		} else {
			return null;
		}
		return this.get(sql, date);
	}

	@Override
	public ResultSet getRevenue(String date) {
		String sql = "SELECT SUM(delivery_note_value) as total_revenue FROM tbldelivery_note";
		String patternDate = "\\d{4}-\\d{2}-\\d{2}";
		String patternMonth = "\\d{4}-\\d{2}";
		if (Pattern.matches(patternDate, date)) {
			sql += " WHERE DATE_FORMAT(tbldelivery_note.delivery_note_date, \"%Y-%m-%d\") = ?";
		} else if (Pattern.matches(patternMonth, date)) {
			sql += " WHERE DATE_FORMAT(tbldelivery_note.delivery_note_date, \"%Y-%m\") = ?";
		} else {
			return null;
		}
		return this.get(sql, date);
	}

	@Override
	public ResultSet getQuantity(String date) {
		String sql = "SELECT SUM(delivery_quantity) as quantity_product FROM tbldelivery";
		sql += " INNER JOIN tbldelivery_note ON tbldelivery_note.delivery_note_id = tbldelivery.delivery_note_id";
		String patternDate = "\\d{4}-\\d{2}-\\d{2}";
		String patternMonth = "\\d{4}-\\d{2}";
		if (Pattern.matches(patternDate, date)) {
			sql += " WHERE DATE_FORMAT( tbldelivery_note.delivery_note_date, \"%Y-%m-%d\") = ?";
		} else if (Pattern.matches(patternMonth, date)) {
			sql += " WHERE DATE_FORMAT( tbldelivery_note.delivery_note_date, \"%Y-%m\") = ?";
		} else {
			return null;
		}
		return this.get(sql, date);
	}

	@Override
	public ResultSet getDeliveryQuantityByCategory() {
		String sql = "SELECT category_name, SUM(delivery_quantity) as total";
		sql += " FROM tblproduct INNER JOIN tbldelivery ON tblproduct.product_id = tbldelivery.product_id";
		sql += " INNER JOIN tblcategory ON tblproduct.category_id = tblcategory.category_id";
		sql += " GROUP BY category_name";
		return this.get(sql, 0);
	}

	@Override
	public ResultSet getDeliveryQuantityByUser(int userId) {
		String sql = "SELECT DATE_FORMAT(dn.delivery_note_date, '%Y-%m') AS month, ";
		sql += "SUM(d.delivery_quantity) AS total_delivery_quantity ";
		sql += "FROM tbldelivery d JOIN tbldelivery_note dn ON d.delivery_note_id = dn.delivery_note_id ";
		sql += "WHERE dn.user_id = ? AND dn.delivery_note_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) ";
		sql += "GROUP BY DATE_FORMAT(dn.delivery_note_date, '%Y-%m') ORDER BY month";
		return this.get(sql, userId);
	}

	@Override
	public ResultSet getDeliveryValueByUser(int userId) {
		String sql = "SELECT DATE_FORMAT(delivery_note_date, '%Y-%m') AS month, ";
		sql += "SUM(delivery_note_value) AS total_value FROM tbldelivery_note ";
		sql += "WHERE user_id = ? AND delivery_note_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) ";
		sql += "GROUP BY DATE_FORMAT(delivery_note_date, '%Y-%m') ORDER BY month";
		return this.get(sql, userId);
	}
}
