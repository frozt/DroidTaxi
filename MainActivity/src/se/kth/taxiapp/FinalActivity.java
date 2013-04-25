package se.kth.taxiapp;

import se.kth.taxiapp.context.RandomPlacesGenerator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnCreateContextMenuListener;
import android.widget.TextView;

public class FinalActivity extends AbstractHeaderFooterActivity {

	protected ApplicationContextProvider app;
	private String arrivalTime = "9 min";
	private String destination;
	private RandomPlacesGenerator placesGenerator;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final);
		
		//setNavigationButtons();
		
		app = (ApplicationContextProvider)getApplication();
		placesGenerator= ApplicationContextProvider.getRandomGenerator();
		
		setTextBoxes();
		
		setUserName(app.getCurrentUser().getName());
		
	}


	private void setTextBoxes() {
		Intent intentFromConfirmation = getIntent();		
		destination = intentFromConfirmation.getStringExtra(ConfirmationActivity.EXTRA_MESSAGE);
		
		TextView arrivalText = (TextView) findViewById(R.id.finalArrivalTextId);
		arrivalText.setText(arrivalTime);
		
		int cost = placesGenerator.getPlaceCost(destination);
		
		TextView costText = (TextView) findViewById(R.id.finalCostTextId);
		costText.setText("" + cost + "$");
		
		TextView approved = (TextView) findViewById(R.id.textApproved);
		approved.setText("Taxi request approved");
	}
	
	
	

}
