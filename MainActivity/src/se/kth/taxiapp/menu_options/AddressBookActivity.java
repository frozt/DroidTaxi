package se.kth.taxiapp.menu_options;

import java.util.ArrayList;

import se.kth.taxiapp.AbstractHeaderFooterActivity;
import se.kth.taxiapp.ApplicationContextProvider;
import se.kth.taxiapp.MainActivity;
import se.kth.taxiapp.R;
import se.kth.taxiapp.R.id;
import se.kth.taxiapp.R.layout;
import se.kth.taxiapp.R.menu;
import se.kth.taxiapp.db.Address;
import se.kth.taxiapp.db.AddressBook;
import se.kth.taxiapp.db.DataSource;
import se.kth.taxiapp.db.UserProfile;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddressBookActivity extends ListActivity  {

	private static final String[] AddressBookOptions = new String[] {"Display Addresses", "Edit Address", "Add address", "Delete Address"};
	
	private ApplicationContextProvider app;
	private DataSource datasource;
	private UserProfile currentUser;
	private ArrayList<Address> myAddressBook;
	private AdressBookArrayAdapter adapter;
	
	private int selectedItemPosition = -1;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_book);
		
		Log.i("AddressBookActivity", "AddressBookActivity.onCreate()");
		
		app = (ApplicationContextProvider)getApplication();
		
		if(app==null){
			Log.e("AddressBookActivity", "AddressBookActivity.onCreate() — AppProvider NULL");
		}else{
			Log.i("AddressBookActivity", "AddressBookActivity.onCreate() — AppProvider OK");
		}
						
		this.datasource = DataSource.instance(this);
		
		if(datasource==null){
			Log.e("AddressBookActivity", "AddressBookActivity.onCreate() — datasource NULL");
		}else{
			Log.i("AddressBookActivity", "AddressBookActivity.onCreate() — datasource OK");
		}
		
		currentUser = app.getCurrentUser(); //TODO: create an AlertDialog awaring user about that
		
		this.adapter = new AdressBookArrayAdapter(this, fetchAddressFromDB());
		
		setUsername(currentUser.getName());
		
		setActionButtons();
		
		setListAdapter(adapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
		  
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,			
					long id) {
				
				Log.i("AddressBookActivity", "AddressBookActivity.setOnItemClickListener() — Position: " + position);
				
				selectedItemPosition = position;
				
			}
		});
	
		
	}
	// set the username on addressbook actiivty page
		private void setUsername(String name){
				TextView userNameText = (TextView) findViewById(R.id.textCurrentUserName);
		    	userNameText.setText(name);
			}


	private void setActionButtons() {
		
		Log.i("AddressBookActivity", "AddressBookActivity.setActionButtons()");
		
		Button addAddressButton = (Button) findViewById(R.id.button_ab_Add);
		
		if(addAddressButton==null){
			Log.w("AddressBookActivity", "AddressBookActivity.addAddressButton addAddressButton: NULL");
		}else{
			Log.i("AddressBookActivity", "AddressBookActivity.addAddressButton: OK");
			addAddressButton.setOnClickListener(new View.OnClickListener() {
	            @SuppressLint("NewApi")
				public void onClick(View v) {
	            	
	            	Log.i("AddressBookActivity", "AddressBookActivity.SaveButton.onClick");
	            	
	            	startActivity(new Intent(AddressBookActivity.this, AddAddressActivity.class));
	            }
	        });
		}
		
		
		Button deleteAddressButton = (Button) findViewById(R.id.button_ab_Delete);
		
		if(deleteAddressButton==null){
			Log.w("AddressBookActivity", "AddressBookActivity.addAddressButton deleteAddressButton: NULL");
		}else{
			
			Log.i("AddressBookActivity", "AddressBookActivity.addAddressButton: OK");
			
			deleteAddressButton.setOnClickListener(new View.OnClickListener() {
	            @SuppressLint("NewApi")
				public void onClick(View v) {
	            	
	            	Log.i("AddressBookActivity", "AddressBookActivity.SaveButton.onClick - ItemPos: " + selectedItemPosition);
	            	
	            	if(selectedItemPosition != -1){
	            		String selectedFromList =(String) (getListView().getItemAtPosition(selectedItemPosition));
	            		Log.i("AddressBookActivity", "AddressBookActivity.SaveButton.onClick - SeletecAddress: " + selectedFromList);
	            		
	            		showDialog("Do you want to delete this Address: ", selectedFromList);
	            		
	            	}else{
	            		Toast.makeText(getApplicationContext(), "Select an element, please", Toast.LENGTH_LONG).show();
	            	}
	            	
	            }
	        });
			
		}
		
		Button okAddressButton = (Button) findViewById(R.id.button_ab_OK);
		
		if(deleteAddressButton==null){
			Log.w("AddressBookActivity", "AddressBookActivity.addAddressButton deleteAddressButton: NULL");
		}else{
			
			okAddressButton.setOnClickListener(new View.OnClickListener() {
	            @SuppressLint("NewApi")
				public void onClick(View v) {
	            	Log.i("AddressBookActivity", "AddressBookActivity.SaveButton.onClick - Go to Home");
	            	//startActivity(new Intent(AddressBookActivity.this, MainActivity.class));
	            	finish();
	            }
	        });
			
		}
		
	}
	
	private void showDialog(String message, final String selectedAddressName){
		
		Log.i("AddressBookActivity", "AddressBookActivity.showDialog() — ");
				
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(message + " " + selectedAddressName)
        	
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		
				public void onClick(DialogInterface dialog, int id) {
					
					deleteAddressFromDB(selectedAddressName);
        								
					Log.i("AddressBookActivity", "AddressBookActivity.showDialog() — Refresh List - Delete: " + selectedAddressName);
					
					refreshListAdapter(selectedAddressName);
					
					Log.i("AddressBookActivity", "AddressBookActivity.showDialog() — dismiss dialog");
        			dialog.dismiss();
        		}
        	})
        
        	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		
        		public void onClick(DialogInterface dialog, int id) {
        			dialog.cancel();
        		}	
        });
		// Create the AlertDialog object and return it
		builder.create();
		builder.show();
	}

	
	private void refreshListAdapter(String selectedAddressName){
		
		
		Log.i("AddressBookActivity", "AddressBookActivity.refreshListAdapter() —");
		
		if(adapter == null){
			Log.e("AddressBookActivity", "AddressBookActivity.refreshListAdapter() — Adapter: NULL");
		}
		
		adapter.remove(selectedAddressName);
		adapter.notifyDataSetChanged();
		
		/*
		Log.i("AddressBookActivity", "AddressBookActivity.showDialog() — refreshListAdapter - clear");
		adapter.clear();
		
		Log.i("AddressBookActivity", "AddressBookActivity.showDialog() — refreshListAdapter - addAll");
		adapter.addAll(fetchAddressFromDB());
		
		Log.i("AddressBookActivity", "AddressBookActivity.showDialog() — refreshListAdapter - notifyDataSetChanged");
		adapter.notifyDataSetChanged();*/
	}
	private void  deleteAddressFromDB(String address){
		Log.i("AddressBookActivity", "AddressBookActivity.deleteAddressFromDB() — " + address);
		datasource.deleteAddressFromAddressBook(currentUser.getId(), "whatEver", address);
	}
		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.address_book, menu);
		return true;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.i("AddressBookActivity", "AddressBookActivity.onResume() ");
		
		this.adapter.clear();
		this.adapter.notifyDataSetChanged();
		
		this.adapter = new AdressBookArrayAdapter(this, fetchAddressFromDB());
		setListAdapter(adapter);
	}
	
	private ArrayList<String> fetchAddressFromDB(){
		Log.i("DisplayAddressBook", "DisplayAddressBook.fetchAddressFromDB() — DataSource: OK");
		
		this.myAddressBook = datasource.getUserAddressBook(currentUser.getId()); 
		
		ArrayList<String> addresses = new ArrayList<String>();
		
		for(Address item : this.myAddressBook){
			addresses.add(item.toString());
		}
		
		String[] addressList = new String[addresses.size()]; 
		addresses.toArray(addressList);
		
		return addresses;
		
	}

	
	
	
	
}
