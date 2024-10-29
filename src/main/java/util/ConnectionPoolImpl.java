package util;

import java.sql.*;
import java.util.*;

public class ConnectionPoolImpl implements ConnectionPool {
	// Trình điều khiển làm việc với MySQL
	private String driver;

	// Đường dẫn thực thi
	private String url;

	// Tài khoản làm việc
	private String username;
	private String userpass;

	// Đối tượng lưu trữ kết nối
	private Stack<Connection> pool;

	public ConnectionPoolImpl() {
		// Xác định trình điều khiển
		this.driver = "com.mysql.cj.jdbc.Driver";

		// Nạp trình điều khiển
		this.loadDriver();

		// Xác định đường dẫn
		this.url = "jdbc:mysql://localhost:3306/shop360_data2?allowMultiQueries=true";

		// Xác định tài khoản
		this.username = "tiep";
		this.userpass = "123456";

		// Xác định bộ nhớ
		this.pool = new Stack<>();

	}

	private void loadDriver() {
		try {
			Class.forName(this.driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection(String objectName) throws SQLException {
		if (this.pool.isEmpty()) {
			System.out.println(objectName + " have created the new Connection.");
			return DriverManager.getConnection(this.url, this.username, this.userpass);
		} else {
			System.out.println(objectName + " have popped the Connection.");
			return this.pool.pop();
		}

	}

	@Override
	public void releaseConnection(Connection con, String objectName) throws SQLException {
		// TODO Auto-generated method stub
		System.out.println(objectName + " have pushed the Connection.");
		this.pool.push(con);

	}

	protected void finalize() throws Throwable {
		// Loại bỏ các kết nối trong Pool
		this.pool.clear();
		this.pool = null;
		System.out.println("ConnectionPool is Closed.");
	}
}
