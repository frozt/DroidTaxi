package se.kth.taxiapp.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressBook {
	
	
	//List<Address> addressbook = new ArrayList<Address>();

	HashMap<String, Address> addressbook = new HashMap<String, Address>();
	
	public AddressBook() {
	}
	
	public AddressBook(List<String> addresses) {
	}
	

	public AddressBook getAddressBook(){
		return this;
	}
	
	public Address getAddressElement(String addressName){
		return addressbook.get(addressName);
	}
	
	public void addAddress(String name, String address, String latitude, String longitude){
		Address element = new Address(name, address, latitude, longitude);
		addressbook.put(name, element);
	}
	
	public void deleteAddress(String address){
		addressbook.remove(address);
	}
	
	
	

}
