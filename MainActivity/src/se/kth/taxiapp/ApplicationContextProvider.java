package se.kth.taxiapp;

import java.util.HashMap;

import se.kth.taxiapp.context.RandomPlacesGenerator;
import se.kth.taxiapp.db.UserProfile;
import android.app.Application;
import android.content.Context;

public class ApplicationContextProvider extends Application{
	
	
	/**
     * Keeps a reference of the application context
     */
    private static Context sContext;
	
	private static RandomPlacesGenerator generator; 
	private static final String CURRENT_USER_LOCATION = "Hotorget";
	
	private HashMap<String, UserProfile> userProfiles = new HashMap<String, UserProfile>();
	private UserProfile currentUser;
	
	
	

	@Override
	public void onCreate(){
		this.generator = RandomPlacesGenerator.getInstance();
		this.sContext = getApplicationContext();
	}
	
	/**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }
    
      
    public UserProfile getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserProfile currentUser) {
		this.currentUser = currentUser;
	}
    
	
	public static RandomPlacesGenerator getRandomGenerator(){
		return generator;
	}
	
	public String getUserLocation(){
		return CURRENT_USER_LOCATION;
	}
	
		
	public UserProfile getUserProfile(String userName){
		return userProfiles.get(userName);
	}
	
	public Boolean existingUserProfile(){
		
		if(userProfiles.isEmpty()){
			return false;
		}
		
		return true;
	}
	

}
