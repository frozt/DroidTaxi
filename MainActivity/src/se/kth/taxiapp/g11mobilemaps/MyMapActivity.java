package se.kth.taxiapp.g11mobilemaps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import se.kth.taxiapp.DestinationActivity;
import se.kth.taxiapp.ApplicationContextProvider;
import se.kth.taxiapp.R;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyMapActivity extends Activity implements OnMapClickListener{
	
	static final LatLng STOCKHOLM = new LatLng(59.335356, 18.062832);
	//static final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap map;
	private Marker destination;
	private MarkerOptions currentLocation;
	public final static String EXTRA_MESSAGE = "se.kth.taxiapp.g11mobilemaps.MapActivity.MESSAGE";
	
	public final static String DIALOG_MESSAGE = "Selected Destination: ";
	public final static String ADD_NULL = "Place found but street name not resolved. You might need to reboot your device!";
	
	FragmentManager myFragmentManager;
	MapFragment myMapFragment;
	
	protected ApplicationContextProvider app;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.i("UserProfileActivity", "MyMapActivty.onCreate() — ");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		app = (ApplicationContextProvider)getApplication();
		setUserName(app.getCurrentUser().getName());
		
		myFragmentManager = getFragmentManager();
		myMapFragment = (MapFragment)myFragmentManager.findFragmentById(R.id.map);
		map = myMapFragment.getMap();
		
		
		initMap();
		setButtonActions();
	}
	
	private void setUserName(String name){
		TextView userNameText = (TextView) findViewById(R.id.textCurrentUserName);
    	userNameText.setText(name);
	}

	private void setButtonActions() {
		
		Button clearMarker = (Button) findViewById(R.id.buttonClearMarker);
		
		clearMarker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	map.clear();
            	map.addMarker(currentLocation);
            }
        });
		
		Button selectDestination = (Button) findViewById(R.id.buttonSelectMarker);
		
		selectDestination.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
			public void onClick(View v) {
            	
            	if(destination!=null){
            		
            		String address = getAddressString(destination.getPosition().latitude, destination.getPosition().longitude);
            		
            		Log.i("UserProfileActivity", "setButtonActions.onSelectClick.onCreate() — Address: " + address);
            		
            		if(address!=null){
            			showDialog(DIALOG_MESSAGE + address);
            		}else{
            			showDialog(ADD_NULL);
            			Log.w("UserProfileActivity", "setButtonActions.onSelectClick.onCreate() — Address NULL! ");
            		}
            		
            	}else{
            		//display alert dialog
            	}
            }
        });
		
	}
	
	
	
	private void initMap(){
		
		Log.i("UserProfileActivity", "MyMapActivty.initMap() — ");
		
		currentLocation = new MarkerOptions();
		currentLocation.position(STOCKHOLM);
		currentLocation.title("Hotörget");
		currentLocation.snippet("Here you are!");
		currentLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user2));
		
		map.addMarker(currentLocation);
		    
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		map.setMyLocationEnabled(true);
	    map.getUiSettings().setZoomControlsEnabled(true);
	    map.getUiSettings().setCompassEnabled(true);
	    map.getUiSettings().setMyLocationButtonEnabled(true);
	    
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(STOCKHOLM, 19));

	    // Zoom in, animating the camera.
	    map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
	    
	    //setting listeners
	    map.setOnMapClickListener(this);
		
	}

	
	private void showDialog(String message){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		
		builder.setMessage(message)
        	
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			//go to destination page
        			dialog.dismiss();
        			goToDestination();
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
	
	@SuppressLint("NewApi")
	private void goToDestination(){
		Intent sendDataIntent = new Intent(this, DestinationActivity.class); 	
		sendDataIntent.putExtra(EXTRA_MESSAGE, getAddressString(destination.getPosition().latitude, destination.getPosition().longitude));		
		startActivity(sendDataIntent);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	

	@Override
	public void onMapClick(LatLng point) {
		// for now it is possible just to introduce 1 destination at a time
		
		if (currentLocation != null) {
			// there exists another location
			map.clear();
			map.addMarker(currentLocation);

			String pointAddress = getAddressString(point.latitude,
					point.longitude);

			if (pointAddress != null) {
				destination = map.addMarker(new MarkerOptions().position(point)
						.title(pointAddress));
			} else {
				destination = map.addMarker(new MarkerOptions().position(point)
						.title(point.toString()));
			}

		} else {
			destination = map.addMarker(new MarkerOptions().position(point)
					.title(point.toString()));
		}
		
		
		if(destination != null && currentLocation != null){
			
			LatLng from = currentLocation.getPosition();
			LatLng to = destination.getPosition();
						
			// Add a thin red line from London to New York.
			Polyline routeLine = map.addPolyline(new PolylineOptions()
				.add(from, to)
				.width(5)
				.color(Color.RED));
			
			//Distance
			
			Location fromLocation = new Location("");
			fromLocation.setLatitude(from.latitude);
			fromLocation.setLongitude(from.longitude);
			
			Location toLocation = new Location("");
			toLocation.setLatitude(to.latitude);
			toLocation.setLongitude(to.longitude);
			
			float distance = fromLocation.distanceTo(toLocation);
			
			Toast.makeText(getApplicationContext(), "Distance: " + distance, Toast.LENGTH_LONG).show();
			
			//float distance = Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
			
			
		}
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
