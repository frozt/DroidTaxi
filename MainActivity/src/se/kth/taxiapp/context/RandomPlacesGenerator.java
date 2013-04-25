package se.kth.taxiapp.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomPlacesGenerator{
	
	private static  RandomPlacesGenerator instance = new RandomPlacesGenerator();
	private static HashMap<String, Integer> randomPlaces;
	
	
	private RandomPlacesGenerator(){
		loadPlaces();
	}
	
	
	//PUBLIC INTERFACE METHODS
	
	public static RandomPlacesGenerator getInstance(){
		return instance;
	}
	
	public String getRandomPlace(){
		Random random = new Random();
		List<String> keys = new ArrayList<String>(randomPlaces.keySet());
		String randomKey = keys.get( random.nextInt(keys.size()) );
		return randomKey;
	}
	
	public Integer getPlaceCost(String place){
		if(randomPlaces.get(place) != null){
			return randomPlaces.get(place);
		}
		return 65;
	}
	
	//PRIVATE METHODS
	private static void loadPlaces(){
		randomPlaces = new HashMap<String, Integer>();
		randomPlaces.put("Centralen", 20);
		randomPlaces.put("Kista", 30);
		randomPlaces.put("Brommaplan", 40);
		randomPlaces.put("Lilejholmen", 50);
		randomPlaces.put("Solna", 60);
		randomPlaces.put("New place...", 100);
	}
	


}
