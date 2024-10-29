package objectfuctions;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.javatuples.Triplet;

import objects.DeliveryNoteObject;
import objects.DeliveryObject;
import objects.StoreObject;
import util.ShareControl;

public interface DeliveryNoteFunction extends ShareControl {
	public boolean addDeliveryNote(DeliveryNoteObject item, ArrayList<DeliveryObject> itemDetails);

	public ResultSet getDetails(int id);

	public ResultSet getDeliveryNote(int id);

	public ArrayList<StoreObject> getStores();

	public ResultSet getProfit(String date);

	public ResultSet getRevenue(String date);

	public ResultSet getQuantity(String date);

	public ResultSet getDeliveryQuantityByCategory();

	public ResultSet getDeliveryQuantityByUser(int userId);

	public ResultSet getDeliveryValueByUser(int userId);

	public ArrayList<ResultSet> getDeliveryNotes(DeliveryNoteObject similar, int at, byte total);

	public ArrayList<ResultSet> getDeliveryNotes(Triplet<DeliveryNoteObject, Integer, Byte> infos);
}
