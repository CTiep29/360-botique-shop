package objectcontrol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.javatuples.Triplet;

import objectmodels.DeliveryNoteModel;
import objects.DeliveryNoteObject;
import objects.DeliveryObject;
import objects.InvoiceObject;
import objects.StoreObject;
import util.ConnectionPool;
import views.DeliveryNotes;

public class DeliveryNoteControl {
	private DeliveryNoteModel dnm;

	public DeliveryNoteControl(ConnectionPool cp) {
		this.dnm = new DeliveryNoteModel(cp);
	}

	public ConnectionPool getCP() {
		return this.dnm.getCP();
	}

	public void releaseConnection() {
		this.dnm.releaseConnection();
	}

	public boolean addDeliveryNote(DeliveryNoteObject item, ArrayList<DeliveryObject> itemDetails) {
		return this.dnm.addDeliveryNote(item, itemDetails);
	}

	public HashMap<String, Object> getDeliveryNoteObject(int id) {
		return this.dnm.getDeliveryNoteObject(id);
	}

	public int getQuantityByMonth(String month) {
		return this.dnm.getQuantityByMonth(month);
	}

	public int getRevenueByMonth(String month) {
		return this.dnm.getRevenueByMonth(month);
	}

	public int getProfitByMonth(String month) {
		return this.dnm.getProfitByMonth(month);
	}

	public ArrayList<HashMap<String, Object>> getDeliveryNoteDetails(int id) {
		return this.dnm.getDeliveryNoteDetails(id);
	}

	public ArrayList<String> viewDeliveryNotes(Triplet<DeliveryNoteObject, Short, Byte> infos) {
		Triplet<ArrayList<DeliveryNoteObject>, Integer, HashMap<Integer, Integer>> datas = this.dnm
				.getDeliveryNoteObjects(infos);
		return DeliveryNotes.viewDeliveryNotes(datas, infos);
	}

	public ArrayList<StoreObject> getStores() {
		return this.dnm.getStores();
	}

	public HashMap<String, Object> getMonthProfitOnDashboard() {
		return this.dnm.getMonthProfitOnDashboard();
	}

	public HashMap<String, Object> getDateProfitOnDashboard() {
		return this.dnm.getDateProfitOnDashboard();
	}

	public HashMap<String, Object> getMonthRevenueOnDashboard() {
		return this.dnm.getMonthRevenueOnDashboard();
	}

	public HashMap<String, Object> getDateRevenueOnDashboard() {
		return this.dnm.getDateRevenueOnDashboard();
	}

	public ArrayList<HashMap<String, Object>> getDeliveryQuantityByCategory() {
		return this.dnm.getDeliveryQuantityByCategory();
	}

	public ArrayList<HashMap<String, String>> getDeliveryQuantityByUser(int userId) {
		return this.dnm.getDeliveryQuantityByUser(userId);
	}

	public ArrayList<HashMap<String, String>> getDeliveryValueByUser(int userId) {
		return this.dnm.getDeliveryValueByUser(userId);
	}

	public void printPDF(int id, ServletContext context) throws IOException {
		InvoiceObject io = new InvoiceObject(context);
		HashMap<String, Object> deliveryNote = this.dnm.getDeliveryNoteObject(id);
		ArrayList<HashMap<String, Object>> details = this.dnm.getDeliveryNoteDetails(id);
		io.createDeliveryInvoice(deliveryNote, details);
	}
}
