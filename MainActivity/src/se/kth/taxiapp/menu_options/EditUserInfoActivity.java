package se.kth.taxiapp.menu_options;

import java.util.ArrayList;

import se.kth.taxiapp.ApplicationContextProvider;
import se.kth.taxiapp.MainActivity;
import se.kth.taxiapp.R;
import se.kth.taxiapp.R.layout;
import se.kth.taxiapp.R.menu;
import se.kth.taxiapp.db.Address;
import se.kth.taxiapp.db.DataSource;
import se.kth.taxiapp.db.UserProfile;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

public class EditUserInfoActivity extends ListActivity {

	private ApplicationContextProvider app;
	private DataSource datasource;
	private UserProfile currentUser;
	private ArrayList<Address> myAddressBook;
	private AdressBookArrayAdapter adapter;
	
	private int selectedItemPosition = -1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_user_info);
		
		Log.i("EditUserInfoActivity", "EditUserInfoActivity.onCreate()");
		
		app = (ApplicationContextProvider)getApplication();
		
		if(app==null){
			Log.e("EditUserInfoActivity", "EditUserInfoActivity.onCreate() — AppProvider NULL");
		}else{
			Log.i("EditUserInfoActivity", "EditUserInfoActivity.onCreate() — AppProvider OK");
		}
						
		this.datasource = DataSource.instance(this);
		
		if(datasource==null){
			Log.e("EditUserInfoActivity", "EditUserInfoActivity.onCreate() — datasource NULL");
		}else{
			Log.i("EditUserInfoActivity", "EditUserInfoActivity.onCreate() — datasource OK");
		}
		
		currentUser = app.getCurrentUser(); //TODO: create an AlertDialog awaring user about that
		
		this.adapter = new AdressBookArrayAdapter(this, fetchUserInfoFromDB());
		
		setActionButtons();
		
		setListAdapter(adapter);
	
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
		  
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,			
					long id) {
				
				Log.i("EditUserInfoActivity", "EditUserInfoActivity.setOnItemClickListener() — Position: " + position);
				
				selectedItemPosition = position;
				
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.i("EditUserInfoActivity", "EditUserInfoActivity.onResume() ");
		
		this.adapter.clear();
		this.adapter.notifyDataSetChanged();
		
		this.adapter = new AdressBookArrayAdapter(this, fetchUserInfoFromDB());
		setListAdapter(adapter);
	}

	private ArrayList<String> fetchUserInfoFromDB() {
		
		Log.i("EditUserInfoActivity", "EditUserInfoActivity.fetchUserInfoFromDB() — ");

		UserProfile user = datasource.getUserProfileFromDB();
		
		ArrayList<String> userInfo = new ArrayList<String>();
		
		if(user != null && user.getId()!= -1){
			userInfo.add(user.getName());
			userInfo.add(user.getPhone());
			userInfo.add(user.getEmail());
			
			Log.i("EditUserInfoActivity", "EditUserInfoActivity.fetchUserInfoFromDB() — UserInfo: " + userInfo );
		}else{
			Log.e("EditUserInfoActivity", "EditUserInfoActivity.fetchUserInfoFromDB() — NO USER FETCHED");
		}
		
		return userInfo;
	}

	private void setActionButtons() {
		
		Log.i("EditUserInfoActivity", "EditUserInfoActivity.setActionButtons()");
		
		Button editInfoButton = (Button) findViewById(R.id.button_userInfo_Edit);
		
		editInfoButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
			public void onClick(View v) {
            	Log.i("EditUserInfoActivity", "EditUserInfoActivity.EditButton.onClick");
            	//startActivity(new Intent(EditUserInfoActivity.this, AddAddressActivity.class));
            }
        });
		
		Button okInfoButton = (Button) findViewById(R.id.button_userInfo_OK);
		
		okInfoButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
			public void onClick(View v) {
            	Log.i("EditUserInfoActivity", "EditUserInfoActivity.EditButton.onClick");
            	finish();
            	
            	//startActivity(new Intent(EditUserInfoActivity.this, MainActivity.class));
            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_user_info, menu);
		return true;
	}

}
