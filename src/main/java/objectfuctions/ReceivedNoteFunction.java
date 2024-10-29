package objectfuctions;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.javatuples.Triplet;

import objects.ReceivedNoteObject;
import objects.ReceivedObject;
import objects.SupplierObject;
import util.ShareControl;

public interface ReceivedNoteFunction extends ShareControl {
	public boolean addReceivedNote(ReceivedNoteObject item, ArrayList<ReceivedObject> itemDetails);

	public ResultSet getDetail(int id);

	public ResultSet getReceivedNote();

	public ResultSet getReceivedNote(int id);

	public ResultSet getQuantity(String date);

	public ResultSet getInvested(String date);

	public ArrayList<SupplierObject> getSuppliers();

	public ArrayList<ResultSet> getReceivedNotes(ReceivedNoteObject similar, int at, byte total);

	public ArrayList<ResultSet> getReceivedNotes(Triplet<ReceivedNoteObject, Integer, Byte> infos);

	public ResultSet getReceivedQuantityByCategory();

	public ResultSet getReceivedQuantityByUser(int userId);

	public ResultSet getReceivedValueByUser(int userId);
}
