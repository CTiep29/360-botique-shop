package util;

import java.sql.*;

public interface ConnectionPool {
	// Xin kết nối để làm việc
	public Connection getConnection(String objectName) throws SQLException;

	// Yêu cầu trả về kết nối
	public void releaseConnection(Connection con, String objectName) throws SQLException;
}
