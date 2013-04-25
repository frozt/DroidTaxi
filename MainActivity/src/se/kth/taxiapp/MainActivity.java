package se.kth.taxiapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.kth.taxiapp.context.LocationFinder;
import se.kth.taxiapp.context.LocationFinder.LocationResult;
import se.kth.taxiapp.context.LocationProvider;
import se.kth.taxiapp.db.DataSource;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AbstractHeaderFooterActivity {

	 
	private TextView arrivalTimeText;
	public final static String EXTRA_MESSAGE = "se.kth.taxiapp.MainActivity.MESSAGE";
	public final static String CURRENT_LOCATION = "current_location";
	
	public final static String default_user_profile = "franky";
	
	private ApplicationContextProvider app;
	private DataSource datasource;
	
	ArrayList<String> strings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.i("MainActivity", "MainActivity.oncreate() — Opening Database ");
		
		this.datasource = DataSource.instance(this);
	    
        if(datasource != null){
        	Log.i("MainActivity", "MainActivity.oncreate() — DataSource: OK");
        	this.datasource.open();
        }else{
        	Log.i("MainActivity", "MainActivity.oncreate() — DataSource: NULL");
        }
		
		app = (ApplicationContextProvider)getApplication();
		
	}
	
	     
	@Override
	protected void onStart(){
		super.onStart();
		
		Log.i("MainActivity", "MainActivity.onStart() — ");
		
		setNavigationButtons();
		
		if(app.getCurrentUser()!=null){
			Log.i("MainActivity", "MainActivity.onStart() — Setting currentUser: " + app.getCurrentUser().getName());
			setUserName(app.getCurrentUser().getName());;
		}else{
			Log.i("MainActivity", "MainActivity.onStart() — NO currentUser set it in AppContextProvider");
		}
		
		arrivalTimeText = (TextView) findViewById(R.id.arrivalTimeTextView);
		
		setButtonImagestoDefault();
		
		int iconHomeID = R.drawable.ic_action_home_white;		
		Drawable homeImage = getResources().getDrawable(iconHomeID);
		ImageButton myButton = (ImageButton) findViewById(R.id.home);
		
		myButton.setImageDrawable(homeImage);

		
		//GPS location
	//	LocationProvider provider = new LocationProvider(this);
	//	provider.updateCurrentLocation(this);
		LocationFinder myLocation = new LocationFinder();
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location location){
		    	TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
				locationTextView.setText(getAddressString(location.getLatitude(), location.getLongitude()));
		    }
		};
		myLocation.getLocation(this, locationResult);
		

		
		
	}
		
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	public void displayNearestTaxi()
	{
		ProgressBar loadingBar = (ProgressBar) findViewById(R.id.connectionProgressBar);
		loadingBar.setVisibility(View.INVISIBLE);
		arrivalTimeText.setText("9 minutes");
		arrivalTimeText.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void goToDestination(View v) {
		Intent sendDataIntent = new Intent(this, DestinationActivity.class);
		TextView locationTextView = ((TextView)findViewById(R.id.locationTextView));
		sendDataIntent.putExtra(CURRENT_LOCATION, locationTextView.getText().toString());
		sendIntent(sendDataIntent);
	}
	
	@Override
	public void goToConfirmation(View v) {
		Intent sendDataIntent = new Intent(this, ConfirmationActivity.class);
		TextView locationTextView = ((TextView)findViewById(R.id.locationTextView));
		sendDataIntent.putExtra(CURRENT_LOCATION, locationTextView.getText().toString());
		sendIntent(sendDataIntent);		
	}
	
	private void sendIntent(Intent sendDataIntent){
		String message = arrivalTimeText.getText().toString();
		
		//send the info to DisplayMessageApp
		sendDataIntent.putExtra(EXTRA_MESSAGE, message);		
		startActivity(sendDataIntent);
	}
private String getAddressString(Double latitude, Double longitude){
		
		Log.i("UserProfileActivity", "MyMapActivity.getAddressString — ");
		
		Geocoder geocoder;
		
		List<android.location.Address> addresses;
		
		geocoder = new Geocoder(this, Locale.getDefault());
		
		
		
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 2);
			
			Log.i("UserProfileActivity", "MyMapActivity.getAddressString — AddressesElements: " + addresses.size() + " List: " + 
			addresses.toString());
			
			String address = addresses.get(0).getAddressLine(0);
						
			String city = addresses.get(0).getAddressLine(1);
			String country = addresses.get(0).getAddressLine(2);
			
			Log.i("UserProfileActivity", "MyMapActivity.getAddressString — Address: " + address + " City: " +
			city + " Country: " + country);
			
			return address;
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

}
