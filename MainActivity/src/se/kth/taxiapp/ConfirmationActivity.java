package se.kth.taxiapp;


import se.kth.taxiapp.context.RandomPlacesGenerator;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ConfirmationActivity extends AbstractHeaderFooterActivity {

	protected ApplicationContextProvider app;
	private String arrivalTime;
	private String destination;
	private RandomPlacesGenerator placesGenerator;
	
	public final static String EXTRA_MESSAGE = "se.kth.taxiapp.ConfirmationActivity.MESSAGE";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation);
		setNavigationButtons();
		
		app = (ApplicationContextProvider)getApplication();
		//placesGenerator= ApplicationContextProvider.getRandomGenerator();
		
		setUserName(app.getCurrentUser().getName());
		
		setTextBoxes();
		
		
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_confirmation, menu);
		return true;
	}*/
	
	@Override
	protected void onStart(){
		super.onStart();
		
		setButtonImagestoDefault();
		
		int iconConfirmationID = R.drawable.ic_action_checkwhite;		
		Drawable confirmationImage = getResources().getDrawable(iconConfirmationID);
		ImageButton myButton = (ImageButton) findViewById(R.id.confirmation);
		
		myButton.setImageDrawable(confirmationImage);

	}
	
	private void setTextBoxes(){
		Intent intentFromDestination = getIntent();		
		destination = intentFromDestination.getStringExtra(DestinationActivity.EXTRA_MESSAGE);
		
		Intent intentFromMain = getIntent();		
		arrivalTime = intentFromMain.getStringExtra(MainActivity.EXTRA_MESSAGE);
		
		//From and To texts
		TextView myFromText = (TextView) findViewById(R.id.textViewConfirmationFromText);
		myFromText.setText(intentFromMain.getStringExtra(MainActivity.CURRENT_LOCATION));
		
		TextView myToText = (TextView) findViewById(R.id.textViewConfirmationToText);
		
		if(destination!=null){
			myToText.setText(destination);
		}
			
		
		//Time and Cost estimations
		
		/*
		TextView myTimeText = (TextView) findViewById(R.id.textViewConfirmationTime);
		myTimeText.setText(arrivalTime);
		
		TextView myCostText = (TextView) findViewById(R.id.textViewConfirmationCost);
		
		if(destination!=null){
			int cost = placesGenerator.getPlaceCost(destination);
			myCostText.setText(""+cost+"$");
		}*/
	}
	
	//Called whenever button click event is triggered
	public void sendMessageToFinal(View view){		
		//send the info to DisplayMessageApp
		Intent sendDataIntent = new Intent(this, FinalActivity.class); 	
		sendDataIntent.putExtra(EXTRA_MESSAGE, destination);		
		startActivity(sendDataIntent);
	}
	
	@Override
	public void goToDestination(View v) {
		Intent sendDataIntent = new Intent(this, DestinationActivity.class); 				
		sendDataIntent.putExtra(EXTRA_MESSAGE, destination);		
		startActivity(sendDataIntent);
	}
}
