package objectmodels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.javatuples.Triplet;

import functionimpl.DeliveryNoteFunctionImpl;
import objectfuctions.DeliveryNoteFunction;
import objects.DeliveryNoteObject;
import objects.DeliveryObject;
import objects.StoreObject;
import util.ConnectionPool;

import java.util.Calendar;

public class DeliveryNoteModel {
	private DeliveryNoteFunction dn;

	public DeliveryNoteModel(ConnectionPool cp) {
		this.dn = new DeliveryNoteFunctionImpl(cp);
	}

	public ConnectionPool getCP() {
		return this.dn.getCP();
	}

	public void releaseConnection() {
		this.dn.releaseConnection();
	}

	public boolean addDeliveryNote(DeliveryNoteObject item, ArrayList<DeliveryObject> itemDetails) {
		return this.dn.addDeliveryNote(item, itemDetails);
	}

	public ArrayList<StoreObject> getStores() {
		return this.dn.getStores();
	}

	public HashMap<String, Object> getDeliveryNoteObject(int id) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		ResultSet rs = this.dn.getDeliveryNote(id);

		if (rs != null) {
			try {
				if (rs.next()) {
					item.put("delivery_note_date", rs.getString("delivery_note_date"));
					item.put("delivery_note_value", rs.getInt("delivery_note_value"));
					item.put("store_name", rs.getString("store_name"));
					item.put("user_fullname", rs.getString("user_fullname"));
					item.put("user_id", rs.getInt("user_id"));
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return item;
	}

	public Triplet<ArrayList<DeliveryNoteObject>, Integer, HashMap<Integer, Integer>> getDeliveryNoteObjects(
			Triplet<DeliveryNoteObject, Short, Byte> infos) {
		int at = (infos.getValue1() - 1) * infos.getValue2();

		Triplet<DeliveryNoteObject, Integer, Byte> infos2 = new Triplet<>(infos.getValue0(), at, infos.getValue2());

		ArrayList<ResultSet> res = this.dn.getDeliveryNotes(infos2);

		ArrayList<DeliveryNoteObject> list = new ArrayList<>();
		HashMap<Integer, Integer> days = new HashMap<>();
		int total = 0;

		if (res.size() > 0) {
			// Lấy danh sách ResultSet
			ResultSet rsDeliveryNotes = res.get(0);
			ResultSet rsTotal = res.get(1);

			try {
				// Duyệt qua tất cả các bản ghi trong ResultSet sản phẩm
				while (rsDeliveryNotes.next()) {
					DeliveryNoteObject item = new DeliveryNoteObject();
					item = new DeliveryNoteObject();
					item.setDelivery_note_id(rsDeliveryNotes.getInt("delivery_note_id"));
					item.setDelivery_note_date(rsDeliveryNotes.getString("delivery_note_date"));
					item.setDelivery_note_value(rsDeliveryNotes.getInt("delivery_note_value"));
					item.setStore_id(rsDeliveryNotes.getShort("store_id"));
					item.setUser_id(rsDeliveryNotes.getInt("user_id"));
					// Đưa vào tập hợp
					list.add(item);
				}

				// Lấy tổng số bản ghi từ ResultSet tổng
				if (rsTotal.next()) {
					total = rsTotal.getInt("total");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return new Triplet<>(list, total, days);
	}

	public ArrayList<HashMap<String, Object>> getDeliveryNoteDetails(int id) {
		ArrayList<HashMap<String, Object>> items = new ArrayList<>();
		ResultSet rs = this.dn.getDetails(id);
		if (rs != null) {
			try {
				while (rs.next()) {
					HashMap<String, Object> item = new HashMap<>();
					item.put("product_name", rs.getString("product_name"));
					item.put("product_id", rs.getInt("product_id"));
					item.put("delivery_quantity", rs.getInt("delivery_quantity"));
					item.put("product_unit", rs.getString("product_unit"));
					item.put("delivery_price", rs.getInt("delivery_price"));
					items.add(item);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return items;
	}

	public int getRevenueByMonth(String month) {
		int revenue = 0;
		ResultSet rs = this.dn.getRevenue(month);
		if (rs != null) {
			try {
				while (rs.next()) {
					revenue = rs.getInt("total_revenue");
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return revenue;
	}

	public int getProfitByMonth(String month) {
		int profit = 0;
		ResultSet rs = this.dn.getProfit(month);
		if (rs != null) {
			try {
				while (rs.next()) {
					profit = rs.getInt("total_profit");
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return profit;
	}

	public int getResultSet(ResultSet rs) {
		int result = 1;
		if (rs != null) {
			try {
				if (rs.next()) {
					result = rs.getInt(1);
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return result;
	}

	public String getThisMonth() {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String monthString = String.format("%04d-%02d", year, month);
		return monthString;
	}

	public String getLastMonth(String thisMonth) {
		String[] parts = thisMonth.split("-");
		int month = Integer.parseInt(parts[1]);
		int year = Integer.parseInt(parts[0]);
		if (month > 1) {
			month--;
		} else {
			month = 12;
			year--;
		}
		String lastMonth = String.format("%04d-%02d", year, month);
		return lastMonth;
	}

	public static String getToday() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String todayString = String.format("%04d-%02d-%02d", year, month, day);
		return todayString;
	}

	public static String getLastDay(String stringDay) {
		String[] parts = stringDay.split("-");
		int thisDay = Integer.parseInt(parts[2]);
		int thisMonth = Integer.parseInt(parts[1]);
		int thisYear = Integer.parseInt(parts[0]);
		Calendar cal = Calendar.getInstance();
		cal.set(thisYear, thisMonth - 1, thisDay);
		cal.add(Calendar.DAY_OF_MONTH, -1); // Lùi lại 1 ngày
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String yesterdayString = String.format("%04d-%02d-%02d", year, month, day);
		return yesterdayString;
	}

	public HashMap<String, Object> getDateRevenueOnDashboard() {
		HashMap<String, Object> item = new HashMap<String, Object>();
		String dateString = getToday();
		ResultSet rs = this.dn.getRevenue(dateString);
		int thisRevenue = getResultSet(rs);
		item.put("this_revenue", thisRevenue);
		int lastRevenue = 0;
		while (lastRevenue == 0) {
			dateString = getLastDay(dateString);
			ResultSet lastRs = this.dn.getRevenue(dateString);
			lastRevenue = getResultSet(lastRs);
		}
		if (lastRevenue != 0) {
			float increase = ((float) thisRevenue / lastRevenue * 100) - 100;
			String formattedIncrease = String.format("%.2f", increase);
			item.put("increase", formattedIncrease);
		} else {
			item.put("increase", 0.0f);
		}

		return item;
	}

	public HashMap<String, Object> getMonthRevenueOnDashboard() {
		HashMap<String, Object> item = new HashMap<String, Object>();
		String monthString = getThisMonth();
		ResultSet rs = this.dn.getRevenue(monthString);
		int thisRevenue = getResultSet(rs);
		item.put("this_revenue", thisRevenue);
		int lastRevenue = 0;
		while (lastRevenue == 0) {
			monthString = getLastMonth(monthString);
			ResultSet lastRs = this.dn.getRevenue(monthString);
			lastRevenue = getResultSet(lastRs);
		}
		if (lastRevenue != 0) {
			float increase = ((float) thisRevenue / lastRevenue * 100) - 100;
			String formattedIncrease = String.format("%.2f", increase);
			item.put("increase", formattedIncrease);
		} else {
			item.put("increase", 0.0f);
		}
		return item;
	}

	public HashMap<String, Object> getDateProfitOnDashboard() {
		HashMap<String, Object> item = new HashMap<String, Object>();
		String dateString = getToday();
		ResultSet rs = this.dn.getProfit(dateString);
		int thisProfit = getResultSet(rs);
		item.put("this_profit", thisProfit);
		int lastProfit = 0;
		while (lastProfit == 0) {
			dateString = getLastDay(dateString);
			ResultSet lastRs = this.dn.getProfit(dateString);
			lastProfit = getResultSet(lastRs);
		}
		if (lastProfit != 0) {
			float increase = ((float) thisProfit / lastProfit * 100) - 100;
			String formattedIncrease = String.format("%.2f", increase);
			item.put("increase", formattedIncrease);
		} else {
			item.put("increase", 0.0f);
		}
		return item;
	}

	public HashMap<String, Object> getMonthProfitOnDashboard() {
		HashMap<String, Object> item = new HashMap<String, Object>();
		String monthString = getThisMonth();
		ResultSet rs = this.dn.getProfit(monthString);
		int thisProfit = getResultSet(rs);
		item.put("this_profit", thisProfit);
		int lastProfit = 0;
		while (lastProfit == 0) {
			monthString = getLastMonth(monthString);
			ResultSet lastRs = this.dn.getProfit(monthString);
			lastProfit = getResultSet(lastRs);
		}
		if (lastProfit != 0) {
			float increase = ((float) thisProfit / lastProfit * 100) - 100;
			String formattedIncrease = String.format("%.2f", increase);
			item.put("increase", formattedIncrease);
		} else {
			item.put("increase", 0.0f);
		}
		return item;
	}

	public int getQuantityByMonth(String month) {
		int quantity = 0;
		ResultSet rs = this.dn.getQuantity(month);
		if (rs != null) {
			try {
				while (rs.next()) {
					quantity = rs.getInt("quantity_product");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return quantity;
	}

	public ArrayList<HashMap<String, Object>> getDeliveryQuantityByCategory() {
		ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		ResultSet rs = this.dn.getDeliveryQuantityByCategory();
		if (rs != null) {
			try {
				while (rs.next()) {
					HashMap<String, Object> item = new HashMap<>();
					item.put("category_name", rs.getString("category_name"));
					item.put("total", rs.getInt("total"));
					items.add(item);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return items;
	}

	public ArrayList<HashMap<String, String>> getDeliveryQuantityByUser(int userId) {
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		ResultSet rs = this.dn.getDeliveryQuantityByUser(userId);
		if (rs != null) {
			try {
				while (rs.next()) {
					HashMap<String, String> item = new HashMap<>();
					item.put("month", rs.getString("month"));
					item.put("total_delivery_quantity", "" + rs.getInt("total_delivery_quantity"));
					items.add(item);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return items;
	}

	public ArrayList<HashMap<String, String>> getDeliveryValueByUser(int userId) {
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		ResultSet rs = this.dn.getDeliveryValueByUser(userId);
		if (rs != null) {
			try {
				while (rs.next()) {
					HashMap<String, String> item = new HashMap<>();
					item.put("month", rs.getString("month"));
					item.put("total_value", "" + rs.getInt("total_value"));
					items.add(item);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return items;
	}
}
