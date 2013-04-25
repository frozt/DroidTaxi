package se.kth.taxiapp.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.kth.taxiapp.ApplicationContextProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	// data/data/ and /databases remain the same always. The one that must be
	// changed is com.example which represents
	// the MAIN package of your project
	public static String DB_PATH = "/data/data/taxi.app/databases/";

	// USER TABLE DEFINITION
	public static final String TABLE_USER = "userProfile";
	
	public static final String COLUMN_USER_ID = "userID";
	
	public static final String COLUMN_USER_NAME = "name";
	public static final String COLUMN_USER_EMAIL = "email";
	public static final String COLUMN_USER_PHONE = "phone";

	public static String[] allUserColumns = { MySQLiteOpenHelper.COLUMN_USER_ID,
			MySQLiteOpenHelper.COLUMN_USER_NAME,
			MySQLiteOpenHelper.COLUMN_USER_EMAIL,
			MySQLiteOpenHelper.COLUMN_USER_PHONE };

	private String[] userNameID = { MySQLiteOpenHelper.COLUMN_USER_ID,
			MySQLiteOpenHelper.COLUMN_USER_NAME };

	// ADDRESS BOOK TABLE DEFINITION
	public static final String TABLE_AB = "addressBook";
	public static final String COLUMN_AB_ID = "abID";
	public static final String COLUMN_AB_ADRESS_NAME = "addressName";
	public static final String COLUMN_AB_ADRESS = "address";
	public static final String COLUMN_AB_LATITUDE = "latitude";
	public static final String COLUMN_AB_LONGITUDE = "lontitude";
	
	public static final String COLUMN_AB_FK = COLUMN_USER_ID;
	

	public static final String DATABASE_NAME = "taxiapp.db";
	private static final int DATABASE_VERSION = 1;

	private static SQLiteDatabase mDataBase;
	private static MySQLiteOpenHelper sInstance = null;

	// Database creation sql statement

	private static final String DATABASE_CREATE = "create table " + TABLE_USER
			
			+ "(" + COLUMN_USER_ID + " integer primary key autoincrement, "
			
			+ COLUMN_USER_NAME + " text not null, " 
			+ COLUMN_USER_EMAIL	+ " text, " 
			+ COLUMN_USER_PHONE + " text);";
	
	private static final String DATABASE_AB_TABLE = "create table " + TABLE_AB
			
			+ "(" + COLUMN_AB_ID + " integer primary key autoincrement, "
			
			+ COLUMN_AB_ADRESS_NAME + " text, "
			+ COLUMN_AB_ADRESS + " text, " 
			+ COLUMN_AB_LATITUDE + " text, " 
			+ COLUMN_AB_LONGITUDE + " text, "
			
			+ COLUMN_AB_FK + " integer, "
			
			+ " FOREIGN KEY (" + COLUMN_AB_FK + ") REFERENCES " +  TABLE_USER + " (userID));";

	
	public static String[] allAddressesColumns = { MySQLiteOpenHelper.COLUMN_AB_ID,
		MySQLiteOpenHelper.COLUMN_AB_ADRESS_NAME,
		MySQLiteOpenHelper.COLUMN_AB_ADRESS,
		MySQLiteOpenHelper.COLUMN_AB_LATITUDE,
		MySQLiteOpenHelper.COLUMN_AB_LONGITUDE, 
		MySQLiteOpenHelper.COLUMN_AB_FK};

	
	
	//ON DELETE CASCADE
	
	/*
	 * CONSTRUCTORS
	 */
	
	
	private MySQLiteOpenHelper() {
		super(ApplicationContextProvider.getContext(), DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public MySQLiteOpenHelper(Context context, SQLiteDatabase database) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mDataBase = database;
	}
	

	@Override
	public void onCreate(SQLiteDatabase database) {
		
		if(database == null){
			Log.i("MySQLiteOpenHelper", " MySQLiteOpenHelper.onCreate() —  Database: NULL");
		}else{
			Log.i("MySQLiteOpenHelper", " MySQLiteOpenHelper.onCreate() —  Database: OK");
		}
		
		database.execSQL("PRAGMA foreign_keys = ON;");
		
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_AB_TABLE);
		
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
	    onCreate(db);

	}
	
	public SQLiteDatabase open() throws SQLException {

		Log.w("DataSource", " DataSource.open() — dbHelper is NULL");
		
		String myPath = MySQLiteOpenHelper.DB_PATH
				+ MySQLiteOpenHelper.DATABASE_NAME;

		Log.i("DataSource", " DataSource.checkDataBase() — DataBase: " + myPath
				+ " do NOT EXISS!");
		
		this.mDataBase = this.getWritableDatabase();

		
		return this.mDataBase;
		/*
		 * if(!checkDataBase()){ Log.i("DataSource",
		 * " DataSource.checkDataBase() — DataBase: " + myPath +
		 * " do NOT EXISS!"); database = dbHelper.getWritableDatabase(); }else{
		 * database = SQLiteDatabase.openDatabase(myPath, null,
		 * SQLiteDatabase.OPEN_READWRITE); Log.w("DataSource",
		 * " DataSource.open() — DataBase: " + myPath + " already EXISTS!"); }
		 */

	}

	


	/**
	 * Select method
	 * 
	 * @param query
	 *            select query
	 * @return - Cursor with the results
	 * @throws android.database.SQLException
	 *             sql exception
	 */
	public Cursor select(String query) throws SQLException {
		return mDataBase.rawQuery(query, null);
	}

	/**
	 * Insert method
	 * 
	 * @param table
	 *            - name of the table
	 * @param values
	 *            values to insert
	 * @throws android.database.SQLException
	 *             sql exception
	 */
	public long insert(String table, ContentValues values) throws SQLException {
		return mDataBase.insert(table, null, values);
	}

	/**
	 * Delete method
	 * 
	 * @param table
	 *            - table name
	 * @param where
	 *            WHERE clause, if pass null, all the rows will be deleted
	 * @throws android.database.SQLException
	 *             sql exception
	 */
	public void delete(String table, String where) throws SQLException {
		mDataBase.delete(table, where, null);
	}

	/**
	 * Update method
	 * 
	 * @param table
	 *            - table name
	 * @param values
	 *            - values to update
	 * @param where
	 *            - WHERE clause, if pass null, all rows will be updated
	 */
	public void update(String table, ContentValues values, String where) {
		mDataBase.update(table, values, where, null);
	}

	/**
	 * Let you make a raw query
	 * 
	 * @param command
	 *            - the sql comand you want to run
	 */
	public void sqlCommand(String command) {
		mDataBase.execSQL(command);
	}

	@Override
	public synchronized void close() {

		if (mDataBase != null)
			mDataBase.close();

		super.close();

	}

}
