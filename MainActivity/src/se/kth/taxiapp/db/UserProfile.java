package se.kth.taxiapp.db;

import android.util.Log;

public class UserProfile {

	
	private long id;
	private String name, email, address_name, address, phone;
	private AddressBook myAddressBook;
	private static final String ADDRESS_DEFAULT_TAG = "No_address_name";
	
	/**
	 * @param args
	 */
	public UserProfile(long id, String name, String email, String phone, String addressName, String address) {
				
		
		Log.i("UserProfileActivity", "UserProfileActivity.Constructor()");
		
		this.id = id;
		this.name = name;
		
		this.email = email;
		this.phone = phone;

		this.myAddressBook = new AddressBook();
		
		if(myAddressBook == null){
			Log.w("UserProfileActivity", "UserProfileActivity.Constructor - AddressBook:  NULL");
		}else{
			Log.i("UserProfileActivity", "UserProfileActivity.Constructor - AddressBook:  OK");
		}
						
		if(addressName != null && !addressName.isEmpty()){
			if(address != null){
				this.myAddressBook.addAddress(addressName, address, null, null);
				Log.i("UserProfileActivity", "UserProfileActivity.Constructor - Creating Address:  " + addressName);
			}else{
				this.myAddressBook.addAddress(ADDRESS_DEFAULT_TAG, ADDRESS_DEFAULT_TAG, null, null);
				Log.w("UserProfileActivity", "UserProfileActivity.Constructor - ERROR Creating Address:  " + addressName);
			}
		}
	}
	
	
	@Override
	public String toString(){
		return "[Name: " + this.name + " Phone: " + this.phone + " Email: " + this.email + "]";
	}
	
	
	
	public AddressBook getMyAddressBook() {
		return myAddressBook;
	}

	public void setMyAddressBook(AddressBook myAddressBook) {
		this.myAddressBook = myAddressBook;
	}

	
	public long getId(){
		return this.id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	public UserProfile() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}



}
