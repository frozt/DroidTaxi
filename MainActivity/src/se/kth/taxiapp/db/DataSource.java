package se.kth.taxiapp.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import se.kth.taxiapp.ApplicationContextProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Geocoder;
import android.util.Log;

public class DataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteOpenHelper dbHelper;

	private Context context; 
	
	private static DataSource sInstance = null;

	/*
	 * CONSTRUCTOR
	 */
	public DataSource(Context context) {
		this.context = context;
		this.dbHelper = new MySQLiteOpenHelper(context, database);
	}

	/**
	 * Singleton for DataBase
	 * 
	 * @return singleton instance
	 */
	public static DataSource instance(Context context) {
		
		if (sInstance == null) {
			Log.i("DataSource", " DataSource.instance() — New Instance");
			sInstance = new DataSource(context);
		} else {
			Log.i("DataSource",
					" DataSource.instance() — Instance already created");
		}
		return sInstance;
	}

	/*
	 * PUBLIC DB INTERFACE
	 */
	public void open() throws SQLException {
		if (dbHelper == null) {
			Log.w("DataSource", " DataSource.open() — dbHelper is NULL");
		} else {
			Log.i("DataSource", " DataSource.open() — dbHelper is OK");
		}
		database = dbHelper.open();
	}

	public void close() {
		dbHelper.close();
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		String myPath = MySQLiteOpenHelper.DB_PATH
				+ MySQLiteOpenHelper.DATABASE_NAME;

		try {
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			int i = checkDB.getVersion();
		} catch (SQLiteException e) {
		}

		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}
	
	/*
	 * PUBLIC DELTE INTERFACE
	 */

	
	public void deleteAddressFromAddressBook(long userId, String addressName, String address){
		
		Log.i("DataSource", " DataSource.deleteAddressFromAddressBook() — AddressName: "
				+ addressName);

		if (database == null) {
			Log.w("DataSource", " DataSource.deleteAddressFromAddressBook() — database NULL");
		} else {
			Log.i("DataSource", " DataSource.deleteAddressFromAddressBook() — database is OK");
		}
				
		database.delete(MySQLiteOpenHelper.TABLE_AB, MySQLiteOpenHelper.COLUMN_AB_FK + "=? AND " + MySQLiteOpenHelper.COLUMN_AB_ADRESS + "=?",
		          new String[] { String.valueOf(userId), address});
		
		
	}
	
	
	/*
	 * PUBLIC READ INTERFACE
	 */
	public UserProfile getUserByName(String profileName) {

		Log.i("DataSource", " DataSource.getUserByName() — Name: "
				+ profileName);

		if (database == null) {
			Log.w("DataSource", " DataSource.getUserByName() — database NULL");
		} else {
			Log.i("DataSource", " DataSource.getUserByName() — database is OK");
		}

		Cursor cursor = database.query(MySQLiteOpenHelper.TABLE_USER,
				MySQLiteOpenHelper.allUserColumns,
				MySQLiteOpenHelper.COLUMN_USER_NAME + "=?",
				new String[] { profileName }, null, null, null, null);

		if (!cursor.moveToFirst()) {
			Log.w("DataSource",
					" DataSource.getUserByName() — User does not exist");
			return null;
		}

		Log.i("DataSource",
				" DataSource.getUserByName() — User Exist - Cursor Rows: "
						+ cursor.getCount());

		if (cursor.getCount() > 1) {
			Log.w("DataSource",
					" DataSource.getUserByName() — More than 1 user with this name");
			return null;
		}

		UserProfile user = new UserProfile(cursor.getInt(0),
				cursor.getString(1), cursor.getString(2), null, null,
				cursor.getString(3));

		return user;
	}
	
	public UserProfile getUserProfileFromDB(){
		
		Log.i("DataSource", " DataSource.getUserProfileFromDB() — ");
		
		String query = "select * from " + MySQLiteOpenHelper.TABLE_USER;
		
		Log.i("DataSource", " DataSource.getUserProfileFromDB() — Query: " + query);
		
		Cursor cursor = dbHelper.select(query);
				
		if(!cursor.moveToFirst()){
			Log.w("DataSource", " DataSource.getUserProfileFromDB() — No user set it!");
			return null;
		}
		
			
		if(cursor.getCount() > 1){
			Log.e("DataSource", " DataSource.getUserProfileFromDB() — DB contains more than 1 user!");
			return null;
		}
		
		
		//public UserProfile(long id, String name, String email, String phone, String addressName, String address) {
				
		UserProfile user = new UserProfile(cursor.getInt(0),
				cursor.getString(1), cursor.getString(2), cursor.getString(3), null,
				null);
					
		Log.e("DataSource", " DataSource.getUserProfileFromDB() — Fetched Profile: " + user.toString());
		
		return user;
		
	}
	
	public ArrayList<Address> getUserAddressBook(long userId) {
		
		if (database == null) {
			Log.w("DataSource", " DataSource.getUserAddressBook() — database NULL");
		} else {
			Log.i("DataSource", " DataSource.getUserAddressBook() — database is OK");
		}
		
		Cursor cursor = database.query(MySQLiteOpenHelper.TABLE_AB,
				MySQLiteOpenHelper.allAddressesColumns,
				MySQLiteOpenHelper.COLUMN_AB_FK + "=?",
				new String[] { String.valueOf(userId) }, null, null, null, null);
		
		if (!cursor.moveToFirst()) {
			Log.w("DataSource",
					" DataSource.getUserAddressBook() — AddressBook does not exist");
			return null;
		}
			
		ArrayList<Address> addressBook = new ArrayList<Address>();
		
		do {
			Log.i("DataSource", " DataSource.getUserAddressBook() — Cursor Fields: " + cursor.getColumnCount());
			Address element = new Address(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
			addressBook.add(element);
	     
		} while(cursor.moveToNext());
		
		return addressBook;
	}
	
	
	

	/*
	 * PUBLIC CREATE INTERFACE
	 */

	public long addUserProfile(UserProfile profile, String addressName,
			String address, String latitude, String longitude) {

		
		Log.i("DataSource", " DataSource.addUserProfile() -ReceivedProfile: " + profile.toString());
		
		UserProfile currentUser = getUserProfileFromDB();
		
		if(currentUser!=null){			
			Log.w("DataSource", " DataSource.addUserProfile(): A user already exists: " + currentUser.getName() + " Id: " + currentUser.getId());
			return currentUser.getId(); 
		}

		if (profile.getName() == null || profile.getName().isEmpty()) {
			return -1;
		}

		if (profile.getEmail() == null || profile.getEmail().isEmpty()) {
			profile.setEmail("default_email");
		}

		if (profile.getPhone() == null || profile.getPhone().isEmpty()) {
			profile.setPhone("default_phone");
		}

		ContentValues values = new ContentValues();
		values.put(MySQLiteOpenHelper.COLUMN_USER_NAME, profile.getName());
		values.put(MySQLiteOpenHelper.COLUMN_USER_EMAIL, profile.getEmail());
		values.put(MySQLiteOpenHelper.COLUMN_USER_PHONE, profile.getPhone());

		long profileID = database.insert(MySQLiteOpenHelper.TABLE_USER, null,
				values);

		Log.w("DataSource", " DataSource.addUserProfile() - Added ID: "
				+ profileID);

		// TODO: add the address to the address book TABLE
		addAddressToAddressBook(profileID, addressName,
				address, latitude, latitude);

		return profileID;
	}

	public long addAddressToAddressBook(long userId, String addressName, String address, String latitude, String longitude) {
		
		if(userId == -1){
			Log.e("DataSource", " DataSource.addAddressToAddressBook() - UserID: -1 COULD NOT PERFORM ACTION");
			return -1;
		}
			
		Log.i("DataSource"," ");
		
		Log.i("DataSource", " DataSource.addAddressToAddressBook() - Parameters - usedID: " + userId 
				+ " addressName: " + addressName
				+ " address: " + address
				+ " latitude: " + latitude
				+ " longitude: " + longitude);
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteOpenHelper.COLUMN_AB_ADRESS_NAME, addressName);
		values.put(MySQLiteOpenHelper.COLUMN_AB_ADRESS, address);
		values.put(MySQLiteOpenHelper.COLUMN_AB_LATITUDE, latitude);
		values.put(MySQLiteOpenHelper.COLUMN_AB_LONGITUDE, longitude);
		values.put(MySQLiteOpenHelper.COLUMN_AB_FK, userId);
		
		Log.i("DataSource", " DataSource.addAddressToAddressBook() - Values: " + 
				" ForeignKey: " + values.getAsString(MySQLiteOpenHelper.COLUMN_AB_FK) + 
				" AddressName: " + values.getAsString(MySQLiteOpenHelper.COLUMN_AB_ADRESS_NAME) + 
				" Address: " + values.getAsString(MySQLiteOpenHelper.COLUMN_AB_ADRESS) + 
				" Latitude: " + values.getAsString(MySQLiteOpenHelper.COLUMN_AB_LATITUDE) + 
				" Lontitude_: " + values.getAsString(MySQLiteOpenHelper.COLUMN_AB_LONGITUDE));
	
		long profileID = database.insert(MySQLiteOpenHelper.TABLE_AB, null,
					values);
		
		if(profileID == -1){
			Log.e("DataSource", " DataSource.addAddressToAddressBook() - AddressID: -1 Something went WRONG!");
			return -1;
		}
		
		Log.i("DataSource", " DataSource.addAddressToAddressBook() - Added ID: "
				+ profileID);
			
		return profileID;
			
	}
	
}
