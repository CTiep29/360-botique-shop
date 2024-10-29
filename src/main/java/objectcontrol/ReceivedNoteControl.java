package objectcontrol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import org.javatuples.Triplet;

import objectmodels.ReceivedNoteModel;
import objects.InvoiceObject;
import objects.ReceivedNoteObject;
import objects.ReceivedObject;
import objects.SupplierObject;
import util.ConnectionPool;
import views.ReceivedNotes;

public class ReceivedNoteControl {
	private ReceivedNoteModel model;

	public ReceivedNoteControl(ConnectionPool cp) {
		this.model = new ReceivedNoteModel(cp);
	}

	public ConnectionPool getCP() {
		return this.model.getCP();
	}

	public void releaseConnection() {
		this.model.releaseConnection();
	}

	public boolean addReceivedNote(ReceivedNoteObject item, ArrayList<ReceivedObject> itemDetails) {
		return this.model.addReceivedNote(item, itemDetails);
	}

	public HashMap<String, Object> getReceivedNoteObject(int id) {
		return this.model.getReceivedNote(id);
	}

	public ArrayList<HashMap<String, Object>> getReceivedNoteDetails(int id) {
		return this.model.getReceivedNoteDetails(id);
	}

	public ArrayList<String> viewReceivedNotes(Triplet<ReceivedNoteObject, Short, Byte> infos) {
		Triplet<ArrayList<ReceivedNoteObject>, Integer, HashMap<Integer, Integer>> datas = this.model
				.getReceivedNoteObjects(infos);
		return ReceivedNotes.viewReceivedNotes(datas, infos);
	}

	public ArrayList<SupplierObject> getSuppliers() {
		return this.model.getSuppliers();
	}

	public int getQuantityByMonth(String month) {
		return this.model.getQuantityByMonth(month);
	}

	public int getInvestedByMonth(String month) {
		return this.model.getInvestedByMonth(month);
	}

	public HashMap<String, Object> getMonthQuantityOnDashboard() {
		return this.model.getMonthQuantityOnDashboard();
	}

	public HashMap<String, Object> getDateQuantityOnDashboard() {
		return this.model.getDateQuantityOnDashboard();
	}

	public HashMap<String, Object> getDateInvestedOnDashboard() {
		return this.model.getDateInvestedOnDashboard();
	}

	public HashMap<String, Object> getMonthInvestedOnDashboard() {
		return this.model.getMonthInvestedOnDashboard();
	}

	public ArrayList<HashMap<String, Object>> getReceivedQuantityByCategory() {
		return this.model.getReceivedQuantityByCategory();
	}

	public ArrayList<HashMap<String, String>> getReceivedQuantityByUser(int userId) {
		return this.model.getReceivedQuantityByUser(userId);
	}

	public ArrayList<HashMap<String, String>> getReceivedValueByUser(int userId) {
		return this.model.getReceivedValueByUser(userId);
	}

	public void printPDF(int id, ServletContext context) throws IOException {
		InvoiceObject io = new InvoiceObject(context);
		HashMap<String, Object> receivedNote = this.model.getReceivedNote(id);
		ArrayList<HashMap<String, Object>> details = this.model.getReceivedNoteDetails(id);
		io.createReceivedInvoice(receivedNote, details);
	}
}
