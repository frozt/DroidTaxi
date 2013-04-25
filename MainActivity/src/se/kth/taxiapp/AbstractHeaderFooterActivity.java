package se.kth.taxiapp;

import se.kth.taxiapp.menu_options.AddressBookActivity;
import se.kth.taxiapp.menu_options.EditUserInfoActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public abstract class AbstractHeaderFooterActivity extends Activity {

	private final String MAIN_ACTIVITY = "Main";
	private final String DEST_ACTIVITY = "Destination";
	private final String CONF_ACTIVITY = "Confirmation";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abstract_header_footer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_abstract_header_footer, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    //respond to menu item selection
		
		switch (item.getItemId()) {
		
			case R.id.menu_user_settings:
				startActivity(new Intent(this, EditUserInfoActivity.class));
				return true;
			
			case R.id.menu_address_book:
				startActivity(new Intent(this, AddressBookActivity.class));
				return true;
		}
		
		return true;
	}
	
	protected void setButtonImagestoDefault(){
		
		ImageButton myHomeButton = (ImageButton) findViewById(R.id.home);
		int iconHomeID = R.drawable.ic_action_home;
		
		Drawable homeImage = getResources().getDrawable(iconHomeID);
		myHomeButton.setImageDrawable(homeImage);
		
		ImageButton myDestinationButton = (ImageButton) findViewById(R.id.destination);
		int iconDestinationID = R.drawable.ic_action_map;
		
		Drawable destinationImage = getResources().getDrawable(iconDestinationID);
		myDestinationButton.setImageDrawable(destinationImage);
		
	}
	
	protected void setNavigationButtons(){
    	toHome();
    	toDestination();
    	toConfirmation();
    }
    
    protected void toHome(){
    	final ImageButton button = (ImageButton) findViewById(R.id.home);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	goToHome(v);
            }
        });
    }
    
    public void goToHome(View v) {
    	//no possible to calls to itself
    	if(!this.getClass().toString().contains(MAIN_ACTIVITY)){
    		Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
    	}
	}
    
    protected void toDestination(){
    	
    	final ImageButton button = (ImageButton) findViewById(R.id.destination);
        
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	goToDestination(v);
            	
            }
        });
    }
    
    public void goToDestination(View v) {
    	//no possible to calls to itself
    	if(!this.getClass().toString().contains(DEST_ACTIVITY)){
    		Intent intent = new Intent(this, DestinationActivity.class);
            startActivity(intent);
    	}
    	
	}
    
    protected void toConfirmation(){
    	final ImageButton button = (ImageButton) findViewById(R.id.confirmation);
        
    	button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	goToConfirmation(v);
            	
            }
        });
    }
    
    protected void goToConfirmation(View v) {
    	//no possible to calls to itself
    	if(!this.getClass().toString().contains(CONF_ACTIVITY)){
    		Intent intent = new Intent(this, ConfirmationActivity.class);
            startActivity(intent);            
    	}
	}
    
    protected void setUserName(String userName){
    	TextView userNameText = (TextView) findViewById(R.id.textCurrentUserName);
    	userNameText.setText(userName);
    }
    
    protected void changeImageButton(ImageButton button, int id){
    	Drawable image = getResources().getDrawable(id);
    	button.setImageDrawable(image);
    }
    
    

}
