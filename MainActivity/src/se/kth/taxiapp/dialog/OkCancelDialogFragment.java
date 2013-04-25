package se.kth.taxiapp.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View.OnClickListener;

@SuppressLint({ "NewApi", "ValidFragment" })
public class OkCancelDialogFragment extends DialogFragment {
	
	private String message;
	
	@SuppressLint("ValidFragment")
	public OkCancelDialogFragment(String message){
		this.message = message;
	}

	@SuppressLint("NewApi")
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		
		builder.setMessage(this.message)
        	
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			//go to destination page
        			
        		}
        	})
        
        	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			// go to maps
        		}	
        });
		
		
		// Create the AlertDialog object and return it
		return builder.create();
		
	}

}
