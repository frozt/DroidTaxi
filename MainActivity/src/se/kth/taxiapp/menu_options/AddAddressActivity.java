package se.kth.taxiapp.menu_options;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import se.kth.taxiapp.ApplicationContextProvider;
import se.kth.taxiapp.R;
import se.kth.taxiapp.R.layout;
import se.kth.taxiapp.R.menu;
import se.kth.taxiapp.db.Address;
import se.kth.taxiapp.db.DataSource;
import se.kth.taxiapp.db.UserProfile;
import se.kth.taxiapp.menu_options.AddressDialogFragment.AddressDialogListener;
import android.location.Geocoder;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

public class AddAddressActivity extends FragmentActivity implements AddressDialogFragment.AddressDialogListener{

	private ApplicationContextProvider app;
	private DataSource datasource;
	private UserProfile currentUser;

	private int addressOptionPosition = -1;

	final CharSequence[] items = { "m/s", "km/h", "mph" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_address);

		Log.i("AddAddressActivity", "AddAddressActivity.onCreate()");

		app = (ApplicationContextProvider) getApplication();

		if (app == null) {
			Log.e("AddAddressActivity",
					"AddAddressActivity.onCreate() — AppProvider NULL");
		} else {
			Log.i("AddAddressActivity",
					"AddAddressActivity.onCreate() — AppProvider OK");
		}

		this.datasource = DataSource.instance(this);

		if (datasource == null) {
			Log.e("AddAddressActivity",
					"AddAddressActivity.onCreate() — datasource NULL");
		} else {
			Log.i("AddAddressActivity",
					"AddAddressActivity.onCreate() — datasource OK");
		}

		currentUser = app.getCurrentUser(); // TODO: create an AlertDialog
											// awaring user about that

		setActionButtons();
	}

	public void showNoticeDialog(Address add, List<android.location.Address> results) {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialog = new AddressDialogFragment();
		
		((AddressDialogFragment) dialog).setParameters(add, results);
		
		dialog.show(getFragmentManager(), "AddressDialogFragment");
	}
	
	
	@Override
	public void onDialogPositiveClick(DialogFragment dialog, int item, Address add, android.location.Address address) {
		
		Log.i("AddAddressActivity",
				"AddAddressActivity.onDialogPositiveClick() — : AddressLine0: " + address.getAddressLine(0));
		
		Log.i("AddAddressActivity",
				"AddAddressActivity.onDialogPositiveClick() — : Lat/Lon: " + address.getLatitude() + " | " + address.getLongitude());
		
		Log.i("AddAddressActivity",
				"AddAddressActivity.onDialogPositiveClick() — AdminArea: " + address.getAdminArea() + "  SubAdminArea: " + address.getSubAdminArea()
				+ " Sublocality: " + address.getSubLocality() + " Locale: " + address.getLocale());
		
		GeoPoint p1 = new GeoPoint((int) (address.getLatitude() * 1E6),
				(int) (address.getLongitude() * 1E6));
		
		addAddressToDB(add, p1);
		
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		Log.i("AddAddressActivity",
				"AddAddressActivity.onDialogNegativeClick() — DialogCanceled!");		
	}

	

	private void setActionButtons() {

		Log.i("AddAddressActivity", "AddAddressActivity.setActionButtons()");

		Button saveButton = (Button) findViewById(R.id.add_address_saveButton);

		saveButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				EditText addName = (EditText) findViewById(R.id.add_address_name);
				String nameStr = addName.getText().toString();

				EditText address = (EditText) findViewById(R.id.add_address_address);
				String addressStr = address.getText().toString();

				Log.i("AddAddressActivity",
						"AddAddressActivity.setActionButtons() — Text:  "
								+ nameStr + " " + addressStr);

				if (addressStr != null && !addressStr.isEmpty()
						&& nameStr != null && !nameStr.isEmpty()) {

				
					// add to DB
					Address newAdd = new Address(nameStr, addressStr, null,
							null);
				
					Log.i("AddAddressActivity",
							"AddAddressActivity.setActionButtons() — Adding Address");
				
					List<android.location.Address> results = getAddressesResult(newAdd.getAddress());
					
					if(results.size()>1){
						showNoticeDialog(newAdd, results);
					}else if(results.size() > 0){
						GeoPoint p1 = new GeoPoint((int) (results.get(0).getLatitude() * 1E6),
								(int) (results.get(0).getLatitude() * 1E6));
						
						addAddressToDB(newAdd, p1);
					}

				} else {
					// launch a toast an abort DB insert
					Toast.makeText(getApplicationContext(),
							"Some fields are empty!", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private List<android.location.Address> getAddressesResult(String address){
		
		Log.i("AddAddressActivity",
				" AddAddressActivity.getLatitudeLongitude() - ");

		if (address == null) {
			return null;
		}

		Geocoder coder = new Geocoder(this, Locale.getDefault());

		List<android.location.Address> addresses;

		try {

			addresses = coder.getFromLocationName(address, 5);

			Log.i("AddAddressActivity",
					" AddAddressActivity.getLatitudeLongitude() - Fetching address: "
							+ addresses.size());

			ArrayList<String> optionsList = new ArrayList<String>();

			for (android.location.Address a : addresses) {
				String city = a.getAddressLine(1);
				optionsList.add(a.getAddressLine(0) + "," + city);
			}
			return addresses;

		} catch (Exception e) {
			Log.e("AddAddressActivity",
					" AddAddressActivity.getLatitudeLongitude() - Exception: "
							+ e.toString());
		}
		return null;
	}
	

	private void addAddressToDB(Address address, GeoPoint p1) {

		Log.i("AddAddressActivity", "AddAddressActivity.addAddressToDB() — ");

		if (currentUser != null && currentUser.getId() != -1) {

			Log.i("AddAddressActivity",
					"AddAddressActivity.addAddressToDB() CurrentUser OK ");

			//GeoPoint point = getLatitudeLongitude(address.getAddress());

			String latitude, longitude;

			if (p1 != null) {
				latitude = String.valueOf(p1.getLatitudeE6());
				longitude = String.valueOf(p1.getLongitudeE6());
			} else {
				latitude = " ";
				longitude = " ";
			}

			long result = datasource.addAddressToAddressBook(
					currentUser.getId(), address.getName(),
					address.getAddress(), latitude, longitude);

			if (result != -1) {
				Log.i("AddAddressActivity",
						"AddAddressActivity.addAddressToDB() Added to DB: "
								+ result);
				Toast.makeText(getApplicationContext(), "Address added",
						Toast.LENGTH_LONG).show();
			} else {
				Log.i("AddAddressActivity",
						"AddAddressActivity.addAddressToDB() NOT Added to DB: "
								+ result);
				Toast.makeText(getApplicationContext(),
						"Database Error. Could not perform action!",
						Toast.LENGTH_LONG).show();
			}
			
			finish();

		} else {
			Log.i("AddAddressActivity", "AddAddressActivity. CurrentUser NULL ");
			// launch a toast an abort DB insert
			Toast.makeText(getApplicationContext(), "There is no user set!",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_address, menu);
		return true;

	}

	

}
