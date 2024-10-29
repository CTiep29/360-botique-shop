package functionimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import objectfuctions.UserFunction;
import objects.UserObject;
import util.ConnectionPool;
import util.ConnectionPoolImpl;

public class UserFunctionImpl implements UserFunction {
	private Connection con;
	private util.ConnectionPool cp;

	public UserFunctionImpl(ConnectionPool cp) {
		if (cp == null) {
			this.cp = new ConnectionPoolImpl();

		} else {
			this.cp = cp;
		}
		try {
			this.con = this.cp.getConnection("User");
			if (this.con.getAutoCommit()) {
				this.con.setAutoCommit(false);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public UserObject getUserObject(int id) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM tbluser WHERE user_id=?";
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setInt(1, id);
			ResultSet rs = pre.executeQuery();
			UserObject item = null;
			if (rs != null) {
				if (rs.next()) {
					item = new UserObject();
					item.setUser_id(rs.getInt("user_id"));
					item.setUser_name(rs.getString("user_name"));
					item.setUser_fullname(rs.getString("user_fullname"));
					item.setUser_avatar(rs.getString("user_avatar"));
					item.setUser_role(rs.getInt("user_role"));
				}
				rs.close();
			}
			return item;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public UserObject getUserObject(String username, String password) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM tbluser WHERE (user_name=?) AND (user_password=md5(?))";
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setString(1, username);
			pre.setString(2, password);
			ResultSet rs = pre.executeQuery();
			UserObject item = null;
			if (rs != null) {
				if (rs.next()) {
					item = new UserObject();
					item.setUser_id(rs.getInt("user_id"));
					item.setUser_name(rs.getString("user_name"));
					item.setUser_fullname(rs.getString("user_fullname"));
					item.setUser_avatar(rs.getString("user_avatar"));
					item.setUser_role(rs.getInt("user_role"));
				}
				rs.close();
			}
			return item;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ConnectionPool getCP() {
		// TODO Auto-generated method stub
		return this.cp;
	}

	@Override
	public void releaseConnection() {
		// TODO Auto-generated method stub
		try {
			this.cp.releaseConnection(this.con, "User");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<UserObject> getStaffs() {
		ArrayList<UserObject> items = new ArrayList<>();
		UserObject item;
		String sql = "SELECT * FROM tbluser WHERE user_role = ?";

		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			// truyền giá trị cho tham số
			pre.setInt(1, 2);
			ResultSet rs = pre.executeQuery();// Lấy về tập kết quả
			if (rs != null) {
				while (rs.next()) {
					item = new UserObject();
					item.setUser_id(rs.getInt("user_id"));
					item.setUser_fullname(rs.getString("user_fullname"));
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
}
