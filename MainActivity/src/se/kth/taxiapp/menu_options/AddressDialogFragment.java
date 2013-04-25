package se.kth.taxiapp.menu_options;

import java.util.ArrayList;
import java.util.List;

import se.kth.taxiapp.db.Address;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

@SuppressLint("ValidFragment")
public class AddressDialogFragment extends DialogFragment {

	private Address add;
	private List<android.location.Address> addResults;

	private String[] items;

	public interface AddressDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog,
				int itemSelected, Address add, android.location.Address address);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	AddressDialogListener mListener;


	public void setParameters(Address add,
			List<android.location.Address> results){
		
		this.add = add;
		this.addResults = results;
		
		Log.i("AddressDialogFragment",
				"AddressDialogFragment.setPositiveButton() — Constructor: "
						+ " Results size: " + addResults.size() + " Address: " + add.getAddress());
		
		ArrayList<String> optionsList = new ArrayList<String>();

		for (android.location.Address a : results) {
			String city = a.getAddressLine(1);
			optionsList.add(a.getAddressLine(0) + "," + city);
		}
		
		this.items = new String[optionsList.size()];
		optionsList.toArray(items);
		
	}
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Build the dialog and set up the button click handlers

		Log.i("AddressDialogFragment",
				"AddressDialogFragment.onCreateDialog() — Creating DialopFragment: ");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder

		.setTitle("Select you address")

		.setSingleChoiceItems(items, 0,	new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {
				// String value = cs[item].toString();
			}
		})

		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				
				int checkedItem = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
				
				Log.i("AddressDialogFragment",
						"AddressDialogFragment.setPositiveButton() — ItemSelected: "
								+ checkedItem);

				if(checkedItem >= 0 && checkedItem < addResults.size()){
					Log.i("AddressDialogFragment",
							"AddressDialogFragment.setPositiveButton() — Setting address Name");
					
					add.setName(addResults.get(checkedItem).getAddressLine(0));
					
					mListener.onDialogPositiveClick(AddressDialogFragment.this, id,
							add, addResults.get(checkedItem));

				}else{
					Log.i("AddressDialogFragment",
							"AddressDialogFragment.setPositiveButton() — ItemSelected OUT OF BOUNDS");
					
					mListener.onDialogPositiveClick(AddressDialogFragment.this, id,
							add, addResults.get(0));
				}
				
				dismiss();
			}
		})

		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Send the negative button event back to the host activity
				mListener.onDialogNegativeClick(AddressDialogFragment.this);

				dismiss();
			}
		});
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (AddressDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

	

}
