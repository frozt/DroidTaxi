package se.kth.taxiapp.context;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import se.kth.taxiapp.MainActivity;
import se.kth.taxiapp.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class LocationProvider {
	private LocationManager locationManager;
	private Context context;
	private static final int UPDATE_ADDRESS = 1;
	private Handler mHandler;

	public LocationProvider(Context context) {
		this.context = context;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			// Build an alert dialog here that requests that the user enable
			// the location services, then when the user clicks the "OK" button,
			// call enableLocationSettings()
			showGpsDialog();
		}
	}

	private void showGpsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("GPS is not enabled. Do you want to enable GPS?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								enableLocationSettings();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Do nothing.
					}
				}).show();
	}

	// Method to launch Settings
	private void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(settingsIntent);
	}

	public Location getCurrentLocation() {
		Location gpsLocation = requestLocationFromProvider(
				LocationManager.GPS_PROVIDER, R.string.not_support_gps);
		return gpsLocation;
	}

	public void updateCurrentLocation(final MainActivity mainActivity) {
		Location gpsLocation = getCurrentLocation();
		if (gpsLocation != null) {
			// Handler for updating text fields on the UI like the lat/long and
			// address.
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case UPDATE_ADDRESS:
						TextView locationTextView = (TextView) mainActivity.findViewById(R.id.locationTextView);
						locationTextView.setText((String) msg.obj);
						mainActivity.displayNearestTaxi();
						break;
					}
				}
			};

			// Update the UI immediately if a location is obtained.
			updateUILocation(gpsLocation);
		}
	}

	private void updateUILocation(Location location) {
		// Bypass reverse-geocoding only if the Geocoder service is available on
		// the device.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& Geocoder.isPresent()) {
			doReverseGeocoding(location);
		}
	}

	private void doReverseGeocoding(Location location) {
		// Since the geocoding API is synchronous and may take a while. You
		// don't want to lock
		// up the UI thread. Invoking reverse geocoding in an AsyncTask.
		(new ReverseGeocodingTask(context))
				.execute(new Location[] { location });
	}

	/**
	 * Method to get the last known location.
	 * 
	 * @param provider
	 *            Name of the requested provider.
	 * @param errorResId
	 *            Resource id for the string message to be displayed if the
	 *            provider does not exist on the device.
	 * @return A previously returned {@link android.location.Location} from the
	 *         requested provider, if exists.
	 */
	private Location requestLocationFromProvider(final String provider,
			final int errorResId) {
		Location location = null;
		if (locationManager.isProviderEnabled(provider)) {
			location = locationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(context, errorResId, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	// AsyncTask encapsulating the reverse-geocoding API. Since the geocoder API
	// is blocked,
	// we do not want to invoke it from the UI thread.
	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
		Context mContext;

		public ReverseGeocodingTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected Void doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

			Location loc = params[0];
			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				String addressText = String.format(
						"%s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address.getLocality(),
						address.getCountryName());
				
				// Update address field on UI.
				Message.obtain(mHandler, UPDATE_ADDRESS, addressText)
						.sendToTarget();
			}
			return null;
		}
	}

}
