package basic;

import java.sql.*;
import java.util.*;

import util.*;

public class BasicImpl implements Basic {
	// Bộ quản lý kết nối của riêng Basic
	private ConnectionPool cp;

	// Kết nối để làm việc với CSDL
	protected Connection con;

	// Tên đối tượng làm việc với Basic
	private String objectName;

	public BasicImpl(ConnectionPool cp, String objectName) {
		// Xác định tên đối tượng
		this.objectName = objectName;

		// Xác định Bộ quản lý kết nối của riêng Basic
		if (cp == null) {
			this.cp = new ConnectionPoolImpl();
		} else {
			this.cp = cp;
		}

		// Xin kết nối từ ConnectionPool để Basic làm việc
		try {
			this.con = this.cp.getConnection(this.objectName);

			// Kiểm tra chế độ thực thi của kết nối
			if (this.con.getAutoCommit()) {
				this.con.setAutoCommit(false); // Chấm dứt chế độ tự động
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean exe(PreparedStatement pre) {
		if (pre != null) {
			try {
				int result = pre.executeUpdate();

				// Kiểm tra kết quả
				if (result == 0) {
					this.con.rollback();
					return false;
				}

				// Xác nhận thực thi sau cùng
				this.con.commit();
				return true;

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				// Trở lại trạng thái an toàn của kết nối
				try {
					this.con.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public boolean add(PreparedStatement pre) {
		// TODO Auto-generated method stub
		return this.exe(pre);
	}

	@Override
	public boolean edit(PreparedStatement pre) {
		// TODO Auto-generated method stub
		return this.exe(pre);
	}

	@Override
	public boolean del(PreparedStatement pre) {
		// TODO Auto-generated method stub
		return this.exe(pre);
	}

	@Override
	public ResultSet get(String sql, int value) {
		// TODO Auto-generated method stub

		// Biên dịch câu lệnh SQL
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);

			if (value > 0) {
				pre.setInt(1, value);
			}

			return pre.executeQuery();
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
		return null;
	}

	@Override
	public ResultSet get(String sql, String condition) {
		// TODO Auto-generated method stub

		// Biên dịch câu lệnh SQL
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);

			if (condition != "") {
				pre.setString(1, condition);
			}

			return pre.executeQuery();
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
		return null;
	}

	@Override
	public ResultSet get(String sql, int id, String name) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setInt(1, id);
			pre.setString(2, name);

			return pre.executeQuery();
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

		return null;
	}

	@Override
	public ResultSet gets(String sql) {
		// TODO Auto-generated method stub
		return this.get(sql, 0);
	}

	@Override
	public ArrayList<ResultSet> getRes(String multiSelect) {
		// TODO Auto-generated method stub
		ArrayList<ResultSet> res = new ArrayList<>();

		try {
			PreparedStatement pre = this.con.prepareStatement(multiSelect);

			boolean result = pre.execute();
			do {
				res.add(pre.getResultSet());

				// Sang select tiếp theo
				result = pre.getMoreResults(Statement.KEEP_CURRENT_RESULT);

			} while (result);
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
		return res;
	}

	@Override
	public ArrayList<ResultSet> getRes(PreparedStatement pre) {
		// TODO Auto-generated method stub
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
			this.cp.releaseConnection(con, objectName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
