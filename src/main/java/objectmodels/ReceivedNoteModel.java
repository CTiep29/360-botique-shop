package objectmodels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.javatuples.Triplet;

import functionimpl.ReceivedNoteFunctionImpl;
import objectfuctions.ReceivedNoteFunction;
import objects.ReceivedNoteObject;
import objects.ReceivedObject;
import objects.SupplierObject;
import util.ConnectionPool;

public class ReceivedNoteModel {
	private ReceivedNoteFunction rn;

	public ReceivedNoteModel(ConnectionPool cp) {
		this.rn = new ReceivedNoteFunctionImpl(cp);
	}

	public ConnectionPool getCP() {
		return this.rn.getCP();
	}

	public void releaseConnection() {
		this.rn.releaseConnection();
	}

	public boolean addReceivedNote(ReceivedNoteObject item, ArrayList<ReceivedObject> itemDetails) {
		return this.rn.addReceivedNote(item, itemDetails);
	}

	public ArrayList<SupplierObject> getSuppliers() {
		return this.rn.getSuppliers();
	}

	public HashMap<String, Object> getReceivedNote(int id) {
		ResultSet rs = this.rn.getReceivedNote(id);
		HashMap<String, Object> item = new HashMap<>();
		if (rs != null) {
			try {
				if (rs.next()) {
					item.put("received_note_date", rs.getString("received_note_date"));
					item.put("received_note_total", rs.getInt("received_note_total"));
					item.put("supplier_name", rs.getString("supplier_name"));
					item.put("user_fullname", rs.getString("user_fullname"));
					item.put("user_id", rs.getInt("user_id"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return item;
		} else {
			return null;
		}
	}

	public ArrayList<HashMap<String, Object>> getReceivedNoteDetails(int id) {
		ArrayList<HashMap<String, Object>> items = new ArrayList<>();
		ResultSet rs = this.rn.getDetail(id);

		if (rs != null) {
			try {
				while (rs.next()) {
					HashMap<String, Object> item = new HashMap<>();
					item.put("product_name", rs.getString("product_name"));
					item.put("product_id", rs.getInt("product_id"));
					item.put("received_quantity", rs.getInt("received_quantity"));
					item.put("product_unit", rs.getString("product_unit"));
					item.put("received_price", rs.getInt("received_price"));
					items.add(item);
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return items;
	}

	public int getQuantityByMonth(String month) {
		int quantity = 0;
		ResultSet rs = this.rn.getQuantity(month);
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

	public int getInvestedByMonth(String month) {
		int quantity = 0;
		ResultSet rs = this.rn.getInvested(month);
		if (rs != null) {
			try {
				while (rs.next()) {
					quantity = rs.getInt("invested");
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return quantity;
	}

	public Triplet<ArrayList<ReceivedNoteObject>, Integer, HashMap<Integer, Integer>> getReceivedNoteObjects(
			Triplet<ReceivedNoteObject, Short, Byte> infos) {
		int at = (infos.getValue1() - 1) * infos.getValue2();
		Triplet<ReceivedNoteObject, Integer, Byte> infos2 = new Triplet<>(infos.getValue0(), at, infos.getValue2());
		ArrayList<ResultSet> res = this.rn.getReceivedNotes(infos2);

		ArrayList<ReceivedNoteObject> list = new ArrayList<>();
		HashMap<Integer, Integer> days = new HashMap<>();
		int total = 0;
		if (res.size() > 0) {
			// Lấy danh sách ResultSet
			ResultSet rsReceivedNotes = res.get(0);
			ResultSet rsTotal = res.get(1);

			try {
				// Duyệt qua tất cả các bản ghi trong ResultSet sản phẩm
				while (rsReceivedNotes.next()) {
					ReceivedNoteObject item = new ReceivedNoteObject();
					item.setReceived_note_id(rsReceivedNotes.getInt("received_note_id"));
					item.setReceived_note_date(rsReceivedNotes.getString("received_note_date"));
					item.setReceived_note_total(rsReceivedNotes.getInt("received_note_total"));
					item.setSupplier_id(rsReceivedNotes.getInt("supplier_id"));
					item.setUser_id(rsReceivedNotes.getInt("user_id"));
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

	public HashMap<String, Object> getDateQuantityOnDashboard() {
		HashMap<String, Object> item = new HashMap<String, Object>();
		String dateString = getToday();
		ResultSet rs = this.rn.getQuantity(dateString);
		int thisQuantity = getResultSet(rs);
		item.put("this_quantity", thisQuantity);
		int lastQuantity = 0;
		while (lastQuantity == 0) {
			dateString = getLastDay(dateString);
			ResultSet lastRs = this.rn.getQuantity(dateString);
			lastQuantity = getResultSet(lastRs);
		}
		if (lastQuantity != 0) {
			float increase = ((float) thisQuantity / lastQuantity * 100) - 100;
			String formattedIncrease = String.format("%.2f", increase);
			item.put("increase", formattedIncrease);
		} else {
			item.put("increase", 0.0f);
		}

		return item;
	}

	public HashMap<String, Object> getMonthQuantityOnDashboard() {
		HashMap<String, Object> item = new HashMap<String, Object>();
		String monthString = getThisMonth();
		ResultSet rs = this.rn.getQuantity(monthString);
		int thisQuantity = getResultSet(rs);
		item.put("this_quantity", thisQuantity);
		int lastQuantity = 0;
		monthString = getLastMonth(monthString);
		ResultSet lastRs = this.rn.getQuantity(monthString);
		lastQuantity = getResultSet(lastRs);
		if (lastQuantity != 0) {
			float increase = ((float) thisQuantity / lastQuantity * 100) - 100;
			String formattedIncrease = String.format("%.2f", increase);
			item.put("increase", formattedIncrease);
		} else {
			item.put("increase", 0.0f);
		}
		return item;
	}

	public HashMap<String, Object> getDateInvestedOnDashboard() {
		HashMap<String, Object> item = new HashMap<String, Object>();
		String dateString = getToday();
		ResultSet rs = this.rn.getInvested(dateString);
		int thisInvested = getResultSet(rs);
		item.put("this_invested", thisInvested);
		int lastInvested = 0;
		while (lastInvested == 0) {
			dateString = getLastDay(dateString);
			ResultSet lastRs = this.rn.getInvested(dateString);
			lastInvested = getResultSet(lastRs);
		}
		if (lastInvested != 0) {
			float increase = ((float) thisInvested / lastInvested * 100) - 100;
			String formattedIncrease = String.format("%.2f", increase);
			item.put("increase", formattedIncrease);
		} else {
			item.put("increase", 0.0f);
		}
		return item;
	}

	public HashMap<String, Object> getMonthInvestedOnDashboard() {
		HashMap<String, Object> item = new HashMap<String, Object>();
		String monthString = getThisMonth();
		ResultSet rs = this.rn.getInvested(monthString);
		int thisInvested = getResultSet(rs);
		item.put("this_invested", thisInvested);
		int lastInvested = 0;
		while (lastInvested == 0) {
			monthString = getLastMonth(monthString);
			ResultSet lastRs = this.rn.getInvested(monthString);
			lastInvested = getResultSet(lastRs);
		}
		if (lastInvested != 0) {
			float increase = ((float) thisInvested / lastInvested * 100) - 100;
			String formattedIncrease = String.format("%.2f", increase);
			item.put("increase", formattedIncrease);
		} else {
			item.put("increase", 0.0f);
		}
		return item;
	}

	public ArrayList<HashMap<String, Object>> getReceivedQuantityByCategory() {
		ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		ResultSet rs = this.rn.getReceivedQuantityByCategory();
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

	public ArrayList<HashMap<String, String>> getReceivedQuantityByUser(int userId) {
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		ResultSet rs = this.rn.getReceivedQuantityByUser(userId);
		if (rs != null) {
			try {
				while (rs.next()) {
					HashMap<String, String> item = new HashMap<>();
					item.put("month", rs.getString("month"));
					item.put("total_received_quantity", "" + rs.getInt("total_received_quantity"));
					items.add(item);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return items;
	}

	public ArrayList<HashMap<String, String>> getReceivedValueByUser(int userId) {
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		ResultSet rs = this.rn.getReceivedValueByUser(userId);
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
