package se.kth.taxiapp;

import java.util.ArrayList;
import java.util.List;


import se.kth.taxiapp.context.RandomPlacesGenerator;
import se.kth.taxiapp.db.Address;
import se.kth.taxiapp.db.DataSource;
import se.kth.taxiapp.g11mobilemaps.MyMapActivity;
import se.kth.taxiapp.menu_options.AdressBookArrayAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DestinationActivity extends AbstractHeaderFooterActivity{
	
	private static final int defaultCost = 25;
	private TextView myText = null, selectAddress =null;
	public final static String EXTRA_MESSAGE = "se.kth.taxiapp.DestinationActivity.MESSAGE";
	private static final String NEW_PLACE_MAP = "New place";
	private String arrivalTime;
	private String optionSelected;
	List<String> places  = new ArrayList<String>();
	ArrayAdapter<String> dataAdapter;
	
	protected ApplicationContextProvider app;
	private RandomPlacesGenerator placesGenerator;
	private DataSource datasource;
	private Spinner s;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destination);
		
		this.datasource = DataSource.instance(this);
	    
        if(datasource != null){
        	Log.i("DestinationActivity", "DestinationActivity.oncreate() — DataSource: OK");
        	this.datasource.open();
        }else{
        	Log.i("DestinationActivity", "DestinationActivity.oncreate() — DataSource: NULL");
        }
		
		app = (ApplicationContextProvider)getApplication();
		
		this.s = (Spinner) findViewById(R.id.spinner1);
		
		placesGenerator= ApplicationContextProvider.getRandomGenerator();
		
		setNavigationButtons();
		
		//initPlaces();
		
		setUpSpinner();
		
		myText = (TextView) findViewById(R.id.textViewCost);
		selectAddress = (TextView) findViewById(R.id.textDestination);
		selectAddress.setText("Select Destination:");
		getIntents();
		
		setUserName(app.getCurrentUser().getName());
		
		
	}
	
	
	private void initPlaces(){
		places.add("Centralen");
		places.add("Kista");
		places.add("Brommaplan");
		places.add("New place...");
	}
	
	private void getIntents(){
		//we dont know where this intent comes from
		Intent intentFromAnywhere = getIntent();
		Bundle extras = intentFromAnywhere.getExtras();
		
		if(extras!=null){
			//from main
			if(extras.containsKey(MainActivity.EXTRA_MESSAGE)){
				arrivalTime = intentFromAnywhere.getStringExtra(MainActivity.EXTRA_MESSAGE);
			}
			
			//from maps
			if(extras.containsKey(MyMapActivity.EXTRA_MESSAGE)){
				String optionFromMap = intentFromAnywhere.getStringExtra(MyMapActivity.EXTRA_MESSAGE);
				uploadSpinner(optionFromMap);
			}
			
			//from confirmation
			if(extras.containsKey(ConfirmationActivity.EXTRA_MESSAGE)){
				optionSelected =  intentFromAnywhere.getStringExtra(ConfirmationActivity.EXTRA_MESSAGE);
				
				if( optionSelected!= null){
					uploadSpinner(optionSelected);
				}
			}
		}
	}

	private void uploadSpinner(String item) {
		Spinner s = (Spinner) findViewById(R.id.spinner1);
		int position = dataAdapter.getPosition(item);
		
		if(position >= 0){
			s.setSelection(dataAdapter.getPosition(item));
		}else{
			dataAdapter.add(item);
		    dataAdapter.notifyDataSetChanged();
		    s.setSelection(dataAdapter.getPosition(item));
		}
				
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		setButtonImagestoDefault();
		int iconDestinationID = R.drawable.ic_action_map_white;		
		Drawable destinationImage = getResources().getDrawable(iconDestinationID);
		ImageButton myButton = (ImageButton) findViewById(R.id.destination);
		myButton.setImageDrawable(destinationImage);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.i("AddressBookActivity", "AddressBookActivity.onResume() ");
		dataAdapter.clear();
		dataAdapter.notifyDataSetChanged();
		setUpSpinner();
	}
	
	
	private void setUpSpinner(){
		
		Log.i("DestinationActivity", "DestinationActivity.setUpSpinner() — DataSource: OK");
		
		ArrayList<Address> myAddressBook = datasource.getUserAddressBook(app.getCurrentUser().getId());
		
		if(myAddressBook.isEmpty()){
			Log.i("DestinationActivity", "DestinationActivity.setUpSpinner() — AddressBook is EMPTY");
		}else{
			Log.i("DestinationActivity", "DestinationActivity.setUpSpinner() — AddressBook Items: " + myAddressBook.size());
		}
				
		for(Address a : myAddressBook){
			this.places.add(a.getName() + ":" + a.getAddress());
		}
		
		places.add(NEW_PLACE_MAP);
		
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, places);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
		s.setAdapter(dataAdapter);
		
		//create event listener for spinner
		
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        
				String item = (String) parent.getItemAtPosition(pos);
		        
				if(!(item instanceof String)){
					Log.e("DestinationActivity", "DestinationActivity.setUpSpinner().setOnItemSelectedListener() — item is not Strind");
				}
		        
		        if(item == NEW_PLACE_MAP){
		        	//go to MapActivity
		        	goToMap();
		        }else{
		        	int cost = placesGenerator.getPlaceCost((String) item);
			        setTextViewCost(cost);
			        optionSelected = (String) item;
		        }
		        
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	setTextViewCost(defaultCost);
		    }
		});
		
	}
	
	private void setTextViewCost(int cost){
		if(myText != null){
			myText.setText(cost + " $");
		}
	}
	
	private void goToMap(){
		Intent sendDataIntent = new Intent(this, MyMapActivity.class); 				
		sendDataIntent.putExtra(EXTRA_MESSAGE, "");		
		startActivity(sendDataIntent);
	}
	
	@Override
	public void goToConfirmation(View v) {
		Intent sendDataIntent = new Intent(this, ConfirmationActivity.class); 				
		sendDataIntent.putExtra(EXTRA_MESSAGE, optionSelected);
		String currentLocation = getIntent().getStringExtra(MainActivity.CURRENT_LOCATION);
		sendDataIntent.putExtra(MainActivity.CURRENT_LOCATION, currentLocation);
		startActivity(sendDataIntent);
	}
	
	

}
