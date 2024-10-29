package functionimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;

import objectfuctions.CategoryFunction;
import objects.CategoryObject;
import objects.ProductObject;
import util.ConnectionPool;
import util.ConnectionPoolImpl;

public class CategoryFunctionImpl implements CategoryFunction {
	private Connection con;
	private ConnectionPool cp;

	public CategoryFunctionImpl(ConnectionPool cp) {
		if (cp == null) {
			this.cp = new ConnectionPoolImpl();
		} else {
			this.cp = cp;
		}
		// Xin kết nối
		try {
			this.con = this.cp.getConnection("Category");
			// Kiểm tra và chấm dứt chế độ thực thi tự động của kết nối
			if (this.con.getAutoCommit()) {
				// Huỷ chế độ thực thi của kết nối
				this.con.setAutoCommit(false);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private boolean exe(PreparedStatement pre) {
		// Pre đã được biên dịch và truyền đầy đủ giá trị cho các tham số
		if (pre != null) {
			try {
				int results = pre.executeUpdate();

				if (results == 0) {
					this.con.rollback();
					return false;
				}

				// Xác nhận thực thi sau cùng
				this.con.commit();
				return true;

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				try {
					// Trở lại trạng thái an toàn
					this.con.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} finally {
				pre = null;
			}

		}
		return false;
	}

	@Override
	public boolean addCategory(CategoryObject item) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO tblcategory(");
		sql.append(
				"category_name, category_description, category_image, category_created_date, category_last_modified)");
		sql.append("VALUES(?,?,?,?,?)");
		try {
			PreparedStatement pre = this.con.prepareStatement(sql.toString());

			pre.setString(1, item.getCategory_name());
			pre.setString(2, item.getCategory_description());
			pre.setString(3, item.getCategory_image());
			pre.setString(4, item.getCategory_created_date());
			pre.setString(5, item.getCategory_last_modified());

			return this.exe(pre);

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
	public boolean editCategory(CategoryObject item) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE tblcategory SET ");
		sql.append(
				"category_name=?, category_description=?, category_image=?, category_created_date=?, category_last_modified=?");
		sql.append("WHERE category_id = ?");

		try {
			PreparedStatement pre = this.con.prepareStatement(sql.toString());

			pre.setString(1, item.getCategory_name());
			pre.setString(2, item.getCategory_description());
			pre.setString(3, item.getCategory_image());
			pre.setString(4, item.getCategory_created_date());
			pre.setString(5, item.getCategory_last_modified());
			pre.setInt(6, item.getCategory_id());

			return this.exe(pre);

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
	public boolean delCategory(CategoryObject item) {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM tblcategory WHERE category_id = ?";

		try {
			PreparedStatement pre = this.con.prepareStatement(sql.toString());
			pre.setInt(1, item.getCategory_id());

			return this.exe(pre);

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
	public CategoryObject getCategory(int category_id) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM tblcategory WHERE category_id = ?;";

		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setInt(1, category_id);

			ResultSet rs = pre.executeQuery();
			CategoryObject item = null;
			if (rs != null) {
				if (rs.next()) {
					item = new CategoryObject();
					item.setCategory_id(rs.getInt("category_id"));
					item.setCategory_name(rs.getString("category_name"));
					item.setCategory_description(rs.getString("category_description"));
					item.setCategory_image(rs.getString("category_image"));
					item.setCategory_created_date(rs.getString("category_created_date"));
					item.setCategory_last_modified(rs.getString("category_last_modified"));
				}
				rs.close();
			}
			return item;
		} catch (Exception e) {
			// TODO: handle exception
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
	public ArrayList<ProductObject> getProductByCID(int cid, int at, byte total) {
		// TODO Auto-generated method stub
		ArrayList<ProductObject> list = new ArrayList<>();
		String sql = "SELECT * FROM tblproduct WHERE category_id = ? LIMIT ?, ?";
		ProductObject item = null;
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setInt(1, cid);
			pre.setInt(2, at);
			pre.setByte(3, total);
			ResultSet rs = pre.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					item = new ProductObject();
					item.setProduct_id(rs.getInt("product_id"));
					item.setProduct_name(rs.getString("product_name"));
					item.setProduct_price(rs.getInt("product_price"));
					item.setProduct_unit(rs.getString("product_unit"));
					item.setProduct_quantity(rs.getInt("product_quantity"));
					list.add(item);
				}
				rs.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			// Quay trở lại trạng thái an toàn của kết nối
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return list;
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
			this.cp.releaseConnection(this.con, "Category");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<CategoryObject> getCategoryObject(CategoryObject similar, int at, byte total) {
		// TODO Auto-generated method stub
		ArrayList<CategoryObject> results = new ArrayList<>();
		CategoryObject item = null;

		String sql = "SELECT * FROM tblcategory ";
		sql += "";
		sql += "ORDER BY category_id ASC ";
		sql += "LIMIT " + at + ", " + total;
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);

			ResultSet rs = pre.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					item = new CategoryObject();
					item.setCategory_id(rs.getInt("category_id"));
					item.setCategory_name(rs.getString("category_name"));
					item.setCategory_description(rs.getString("category_description"));
					item.setCategory_image(rs.getString("category_image"));
					item.setCategory_created_date(rs.getString("category_created_date"));
					item.setCategory_last_modified(rs.getString("category_last_modified"));
					results.add(item);
				}
				rs.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			// Quay trở lại trạng thái an toàn của kết nối
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return results;
	}

	@Override
	public int getNoOfRecords() {
		// TODO Auto-generated method stub
		int noOfRecords = 0;
		String sql = "SELECT COUNT(*) FROM tblcategory";
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			ResultSet rs = pre.executeQuery();
			if (rs.next()) {
				noOfRecords = rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return noOfRecords;
	}

	@Override
	public int getNoOfRecordsByCID(int cid) {
		// TODO Auto-generated method stub
		int noOfRecords = 0;
		String sql = "SELECT COUNT(*) FROM tblproduct WHERE category_id = ?";
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setInt(1, cid);
			ResultSet rs = pre.executeQuery();
			if (rs.next()) {
				noOfRecords = rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return noOfRecords;
	}

	@Override
	public ArrayList<CategoryObject> searchCategory(String name) {
		// TODO Auto-generated method stub
		ArrayList<CategoryObject> list = new ArrayList<>();
		String sql = "SELECT * FROM tblcategory WHERE category_name LIKE ?";
		CategoryObject item = null;
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setString(1, "%" + name + "%");
			ResultSet rs = pre.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					item = new CategoryObject();
					item.setCategory_id(rs.getInt("category_id"));
					item.setCategory_name(rs.getString("category_name"));
					item.setCategory_description(rs.getString("category_description"));
					item.setCategory_image(rs.getString("category_image"));
					item.setCategory_created_date(rs.getString("category_created_date"));
					item.setCategory_last_modified(rs.getString("category_last_modified"));
					list.add(item);
				}
				rs.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			// Quay trở lại trạng thái an toàn của kết nối
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public HashMap<String, Integer> getProductCountByCID() {
		HashMap<String, Integer> categoryProductCount = new HashMap<>();
		String sql = "SELECT category_id, COUNT(*) AS product_count " + "FROM tblproduct " + "GROUP BY category_id";

		try (PreparedStatement pre = this.con.prepareStatement(sql); ResultSet rs = pre.executeQuery()) {

			while (rs.next()) {
				int categoryId = rs.getInt("category_id");
				int productCount = rs.getInt("product_count");
				String categoryName = getCategoryNameById(categoryId);
				categoryProductCount.put(categoryName, productCount);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Xử lý ngoại lệ và rollback kết nối
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		return categoryProductCount;
	}

	public String getCategoryNameById(int categoryId) {
		String categoryName = "";
		String sql = "SELECT category_name FROM tblcategory WHERE category_id = ?";
		try (PreparedStatement pre = this.con.prepareStatement(sql)) {
			pre.setInt(1, categoryId);
			ResultSet rs = pre.executeQuery();
			if (rs.next()) {
				categoryName = rs.getString("category_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryName;
	}

	@Override
	public ArrayList<CategoryObject> getAllCategory() {
		// TODO Auto-generated method stub
		ArrayList<CategoryObject> results = new ArrayList<>();
		CategoryObject item = null;

		String sql = "SELECT * FROM tblcategory ";

		try {
			PreparedStatement pre = this.con.prepareStatement(sql);

			ResultSet rs = pre.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					item = new CategoryObject();
					item.setCategory_id(rs.getInt("category_id"));
					item.setCategory_name(rs.getString("category_name"));
					results.add(item);
				}
				rs.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			// Quay trở lại trạng thái an toàn của kết nối
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return results;
	}
}
