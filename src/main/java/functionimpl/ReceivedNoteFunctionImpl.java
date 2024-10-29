package functionimpl;

import org.javatuples.Triplet;

import basic.BasicImpl;
import objectfuctions.ReceivedNoteFunction;

import java.util.regex.Pattern;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import objects.ReceivedNoteObject;
import objects.ReceivedObject;
import objects.SupplierObject;
import util.ConnectionPool;

public class ReceivedNoteFunctionImpl extends BasicImpl implements ReceivedNoteFunction {
	public ReceivedNoteFunctionImpl(ConnectionPool cp, String objectName) {
		super(cp, objectName);
	}

	public ReceivedNoteFunctionImpl(ConnectionPool cp) {
		super(cp, "ReceivedNote");
	}

	@Override
	public boolean addReceivedNote(ReceivedNoteObject item, ArrayList<ReceivedObject> itemDetails) {
		StringBuilder sqlNote = new StringBuilder();
		sqlNote.append(
				"INSERT INTO tblreceived_note(received_note_date, supplier_id, user_id, received_note_total) VALUES(?,?,?,0)");
		String sqlTotal = "SELECT SUM(received_price * received_quantity) AS total_amount FROM tblreceived WHERE received_note_id = ?";
		StringBuilder sqlUpdate = new StringBuilder();
		sqlUpdate.append("UPDATE tblreceived_note SET received_note_total = ? WHERE received_note_id = ?");
		try {
			this.con.setAutoCommit(false);

			// Thực hiện chèn dữ liệu vào bảng tblreceived_note
			PreparedStatement preNote = this.con.prepareStatement(sqlNote.toString(),
					PreparedStatement.RETURN_GENERATED_KEYS);
			preNote.setString(1, item.getReceived_note_date());
			preNote.setInt(2, item.getSupplier_id());
			preNote.setInt(3, item.getUser_id());
			preNote.executeUpdate();

			ResultSet generatedKeys = preNote.getGeneratedKeys();
			int receivedNoteId = 0;
			if (generatedKeys.next()) {
				receivedNoteId = generatedKeys.getInt(1);
			}

			for (ReceivedObject receivedObj : itemDetails) {
				if (!addReceivedObject(receivedObj, receivedNoteId)) {
					return false;
				}

				if (addShipment(receivedObj, receivedNoteId)) {
					if (!updateQuality(receivedObj)) {
						return false;
					}
				}
			}

			PreparedStatement preTotal = this.con.prepareStatement(sqlTotal);
			preTotal.setInt(1, receivedNoteId);
			ResultSet rsTotal = preTotal.executeQuery();
			int total = 0;
			if (rsTotal.next()) {
				total = rsTotal.getInt("total_amount");
			}

			PreparedStatement preUpdate = this.con.prepareStatement(sqlUpdate.toString());
			preUpdate.setInt(1, total);
			preUpdate.setInt(2, receivedNoteId);
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

	public boolean addReceivedObject(ReceivedObject receivedObj, int receivedNoteId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"INSERT INTO tblreceived(product_id, received_price, received_quantity, received_note_id) VALUES (?, ?, ?, ?)");
		try {
			PreparedStatement preparedStatement = this.con.prepareStatement(sql.toString());
			preparedStatement.setInt(1, receivedObj.getProduct_id());
			preparedStatement.setInt(2, receivedObj.getReceived_price());
			preparedStatement.setInt(3, receivedObj.getReceived_quantity());
			preparedStatement.setInt(4, receivedNoteId);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addShipment(ReceivedObject receivedObj, int receivedNoteId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"INSERT INTO tblshipment(product_id, shipment_price, shipment_quantity, received_note_id) VALUES (?, ?, ?, ?)");
		try {
			PreparedStatement preparedStatement = this.con.prepareStatement(sql.toString());
			preparedStatement.setInt(1, receivedObj.getProduct_id());
			preparedStatement.setInt(2, receivedObj.getReceived_price());
			preparedStatement.setInt(3, receivedObj.getReceived_quantity());
			preparedStatement.setInt(4, receivedNoteId);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updateQuality(ReceivedObject receivedObj) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE tblproduct SET product_quantity = ? WHERE product_id = ?");

		try {
			PreparedStatement preparedStatement = this.con.prepareStatement(sql.toString());
			int quantity = getQuantity(receivedObj.getProduct_id());
			quantity += receivedObj.getReceived_quantity();
			preparedStatement.setInt(1, quantity);
			preparedStatement.setInt(2, receivedObj.getProduct_id());
			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public int getQuantity(int id) {
		int quantity = 0;
		String sql = "SELECT product_quantity FROM tblproduct";
		sql += " WHERE product_id = ?";
		ResultSet rs = this.get(sql, id);
		if (rs != null) {
			try {
				while (rs.next()) {
					quantity = rs.getInt("product_quantity");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return quantity;
	}

	private StringBuilder createConditions(ReceivedNoteObject similar) {
		StringBuilder tmp = new StringBuilder();
		if (similar != null) {
			tmp.append("WHERE 1=1 ");
			if (similar.getReceived_note_date() != null) {
				tmp.append("AND received_note_date=\'" + similar.getReceived_note_date() + "' ");
			}
			if (similar.getUser_id() != null) {
				tmp.append("AND user_id = ").append(similar.getUser_id()).append(" ");
			}
		}

		return tmp;
	}

	@Override
	public ResultSet getDetail(int id) {
		String sql = "SELECT tblproduct.product_name, tblproduct.product_id,tblproduct.product_unit,received_quantity,received_price FROM tblreceived_note ";
		sql += "";
		sql += "INNER JOIN tblreceived ON tblreceived_note.received_note_id = tblreceived.received_note_id ";
		sql += "INNER JOIN tblproduct ON tblreceived.product_id = tblproduct.product_id ";
		sql += "WHERE tblreceived_note.received_note_id = ? ";
		return this.get(sql, id);
	}

	@Override
	public ResultSet getReceivedNote() {
		String sql = "SELECT * FROM tblreceived_note ";
		sql += "";
		sql += "ORDER BY received_note_id DESC";
		sql += " ";
		sql += " LIMIT ?";
		return this.get(sql, 0);
	}

	@Override
	public ResultSet getReceivedNote(int id) {
		String sql = "SELECT received_note_date, received_note_total, supplier_name, user_fullname, tblreceived_note.user_id FROM tblreceived_note ";
		sql += "INNER JOIN tblsupplier ON tblreceived_note.supplier_id = tblsupplier.supplier_id ";
		sql += "INNER JOIN tbluser ON tblreceived_note.user_id = tbluser.user_id ";
		sql += "WHERE received_note_id = ?;";
		return this.get(sql, id);
	}

	@Override
	public ArrayList<ResultSet> getReceivedNotes(ReceivedNoteObject similar, int at, byte total) {
		return this.getReceivedNotes(new Triplet<>(similar, at, total));
	}

	@Override
	public ArrayList<ResultSet> getReceivedNotes(Triplet<ReceivedNoteObject, Integer, Byte> infos) {
		ReceivedNoteObject similar = infos.getValue0();
		int at = infos.getValue1();
		byte total = infos.getValue2();

		// select tất bản ghi
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM tblreceived_note ");
		sql.append(this.createConditions(similar)).append(" ");
		sql.append("ORDER BY received_note_id DESC ");
		sql.append("LIMIT ").append(at).append(", ").append(total).append("; ");
		// select lấy tổng số bản ghi
		sql.append("SELECT COUNT(received_note_id) AS TOTAL FROM tblreceived_note ")
				.append(this.createConditions(similar)).append(";");

		return this.getRes(sql.toString());
	}

	public ArrayList<SupplierObject> getSuppliers() {
		ArrayList<SupplierObject> items = new ArrayList<>();
		SupplierObject item;
		String sql = "SELECT * FROM tblsupplier ";

		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			// truyền giá trị cho tham số
			ResultSet rs = pre.executeQuery();// Lấy về tập kết quả
			if (rs != null) {
				while (rs.next()) {
					item = new SupplierObject();
					item.setSupplier_id(rs.getInt("supplier_id"));
					item.setSupplier_name(rs.getString("supplier_name"));
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
	public ResultSet getQuantity(String date) {
		String sql = "SELECT SUM(received_quantity) as quantity_product FROM tblreceived";
		sql += " INNER JOIN tblreceived_note ON tblreceived_note.received_note_id = tblreceived.received_note_id";
		String patternDate = "\\d{4}-\\d{2}-\\d{2}";
		String patternMonth = "\\d{4}-\\d{2}";
		if (Pattern.matches(patternDate, date)) {
			sql += " WHERE DATE_FORMAT( tblreceived_note.received_note_date, \"%Y-%m-%d\") = ?";
		} else if (Pattern.matches(patternMonth, date)) {
			sql += " WHERE DATE_FORMAT( tblreceived_note.received_note_date, \"%Y-%m\") = ?";
		} else {
			return null;
		}
		return this.get(sql, date);
	}

	@Override
	public ResultSet getInvested(String date) {
		String sql = "SELECT SUM(received_note_total) as invested FROM tblreceived_note";
		String pattern = "\\d{4}-\\d{2}-\\d{2}";
		String patternMonth = "\\d{4}-\\d{2}";
		if (Pattern.matches(pattern, date)) {
			sql += " WHERE DATE_FORMAT( tblreceived_note.received_note_date, \"%Y-%m-%d\") = ?";
		} else if (Pattern.matches(patternMonth, date)) {
			sql += " WHERE DATE_FORMAT( tblreceived_note.received_note_date, \"%Y-%m\") = ?";
		} else {
			return null;
		}
		return this.get(sql, date);
	}

	@Override
	public ResultSet getReceivedQuantityByCategory() {
		String sql = "SELECT category_name, SUM(received_quantity) as total";
		sql += " FROM tblproduct INNER JOIN tblreceived ON tblproduct.product_id = tblreceived.product_id";
		sql += " INNER JOIN tblcategory ON tblproduct.category_id = tblcategory.category_id";
		sql += " GROUP BY category_name";
		return this.get(sql, 0);
	}

	@Override
	public ResultSet getReceivedQuantityByUser(int userId) {
		String sql = "SELECT DATE_FORMAT(rn.received_note_date, '%Y-%m') AS month, ";
		sql += "SUM(r.received_quantity) AS total_received_quantity ";
		sql += "FROM tblreceived r JOIN tblreceived_note rn ON r.received_note_id = rn.received_note_id ";
		sql += "WHERE rn.user_id = ? AND rn.received_note_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) ";
		sql += "GROUP BY DATE_FORMAT(rn.received_note_date, '%Y-%m') ORDER BY month";
		return this.get(sql, userId);
	}

	@Override
	public ResultSet getReceivedValueByUser(int userId) {
		String sql = "SELECT DATE_FORMAT(received_note_date, '%Y-%m') AS month, ";
		sql += "SUM(received_note_total) AS total_value FROM tblreceived_note ";
		sql += "WHERE user_id = ? AND received_note_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) ";
		sql += "GROUP BY DATE_FORMAT(received_note_date, '%Y-%m') ORDER BY month";
		return this.get(sql, userId);
	}
}
