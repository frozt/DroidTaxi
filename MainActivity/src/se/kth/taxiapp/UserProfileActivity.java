package se.kth.taxiapp;

import se.kth.taxiapp.db.Address;
import se.kth.taxiapp.db.DataSource;
import se.kth.taxiapp.db.MySQLiteOpenHelper;
import se.kth.taxiapp.db.UserProfile;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class UserProfileActivity extends Activity {

	
	protected ApplicationContextProvider app;
	//private MySQLiteOpenHelper dataBase;
	private DataSource datasource;
	
	static final String HOME_ADDRESS_TAG = "Home";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        
        //creates and open the database so we can use it
        //this.dataBase = MySQLiteOpenHelper.instance();
        
        Log.i("UserProfileActivity", "UserProfileActivity.oncreate() — DataSource: OK");
        
        this.datasource = DataSource.instance(this);
	    
        if(datasource != null){
        	Log.i("UserProfileActivity", "UserProfileActivity.oncreate() — DataSource: OPEN");
        	this.datasource.open();
        }else{
        	Log.i("UserProfileActivity", "UserProfileActivity.oncreate() — DataSource: NOT OPEN");
        }
        
        app = (ApplicationContextProvider)getApplication();
        
        checkUserProfile();
        
        toHome();
    }

    
    private void checkUserProfile() {
    	
    	UserProfile currentUser = datasource.getUserProfileFromDB();
    	    	
    	if(currentUser!=null){
    		
    		if(!app.existingUserProfile()){
    			//we need to set the existing profile as a current user
    			Log.i("MainActivity", "MainActivity.checkUserProfile() — Setting currentUser in Application");
    			app.setCurrentUser(currentUser);
    		}
    		
    		Log.i("MainActivity", "MainActivity.checkUserProfile() — default_user DO NOT EXISTS - going to USER_PROFILE");
			Intent intent = new Intent(this, MainActivity.class);
	        startActivity(intent);
    	}
		
	}
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_profile, menu);
        return true;
    }
    
    protected void toHome(){
    	
    	final Button button = (Button) findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	if(createUserProfile() != -1){
            		goToHome(v);
            	}else{
            		//higthLight fields not correct typed 
            	}
            	
            }
        });
    }
    
    public void goToHome(View v) {
		Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
	}
    
    
    private long createUserProfile(){
    	
    	EditText userNameText = (EditText) findViewById(R.id.nameText);
    	String name = userNameText.getText().toString();

    	EditText userEmailText = (EditText) findViewById(R.id.emailText);
    	String email = userEmailText.getText().toString();
    	
    	EditText userPhoneText = (EditText) findViewById(R.id.phoneText);
    	String phone = userPhoneText.getText().toString();
    	
    	EditText userAddressText = (EditText) findViewById(R.id.addressText);
    	String address = userAddressText.getText().toString();
    	    	
    	
    	//UserProfile(id, name, email, addressName, address, phone)
    	UserProfile userProfile = new UserProfile(-1, name, email, phone, HOME_ADDRESS_TAG, address);
    	
    	Log.i("UserProfileActivity", "UserProfileActivity.createUserProfile() — New Profile: " + userProfile.toString());
    	
    	long id = addUserProfileToDB(userProfile);
    	
    	if( id != -1){
    		userProfile.setId(id);
    		app.setCurrentUser(userProfile);
    	}else{
    		showDialog("Name field cannot be empty.");
    	}
    	
    	Log.i("UserProfileActivity", "UserProfileActivity.createUserProfile() — New ProfileID: " + id);
    	
    	return id;
    	
    }

    private long addUserProfileToDB(UserProfile userProfile) {
    	
    	Log.i("UserProfileActivity", "UserProfileActivity.addUserProfileToDB()");
    	
    	Address homeAddress = userProfile.getMyAddressBook().getAddressElement(HOME_ADDRESS_TAG);
    	
    	if(homeAddress!=null){
    		Log.i("UserProfileActivity", "UserProfileActivity.addUserProfileToDB() — HomeAddress: OK:  Name: " + homeAddress.getName() 
    				+ " Address: " + homeAddress.getAddress());
    	}else{
    		Log.e("UserProfileActivity", "UserProfileActivity.addUserProfileToDB() — HomeAddress: NULL!! ");
    	}
    	
    	//TODO: make a GPS request to get latitue and longitude instead passing null values
    	long id = datasource.addUserProfile(userProfile, homeAddress.getName(), 
    			homeAddress.getAddress(), null, null);
    	
    	Log.i("UserProfileActivity", "UserProfileActivity.addUserProfileToDB() — Adding New Profile to DB: " + id);
    	return id; 
	}
    
    private void showDialog(String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
        	
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			//go to destination page
        			dialog.dismiss();
        			//goToDestination();
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
}















