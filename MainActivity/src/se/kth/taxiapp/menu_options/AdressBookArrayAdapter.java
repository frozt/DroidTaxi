package se.kth.taxiapp.menu_options;

import java.util.ArrayList;
import java.util.List;

import se.kth.taxiapp.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdressBookArrayAdapter extends ArrayAdapter<String> {

	private final Activity  context;
	private final ArrayList<String> values;

	
	static class ViewHolder {
	    public TextView text;
	    public ImageView image;
	}
	
	
	public AdressBookArrayAdapter(Activity  context, ArrayList<String> values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		
	    if (rowView == null) {
	      
	    	LayoutInflater inflater = context.getLayoutInflater();
	      
	      rowView = inflater.inflate(R.layout.rowlayout, null);
	      
	      ViewHolder viewHolder = new ViewHolder();
	      
	      viewHolder.text = (TextView) rowView.findViewById(R.id.address_book_optionText);
	      
	      /*viewHolder.image = (ImageView) rowView
	          .findViewById(R.id.address_book_optionImage);*/
	      
	      rowView.setTag(viewHolder);
	    }

	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    
	    String s = values.get(position);
	    
	    holder.text.setText(s);
			

		return rowView;
	}

}
