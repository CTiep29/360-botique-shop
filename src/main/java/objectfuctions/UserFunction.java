package objectfuctions;

import java.util.ArrayList;
import objects.UserObject;
import util.ConnectionPool;

public interface UserFunction {
	public UserObject getUserObject(int id);

	public UserObject getUserObject(String username, String password);

	public ArrayList<UserObject> getStaffs();

	public ConnectionPool getCP();

	public void releaseConnection();
}
