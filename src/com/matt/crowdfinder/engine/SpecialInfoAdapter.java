package com.matt.crowdfinder.engine;

import java.util.ArrayList;

import com.matt.crowdfinder.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpecialInfoAdapter extends ArrayAdapter<SpecialInfo> {

	private final Context context;
	private final ArrayList<SpecialInfo> aryValue;
	
	public SpecialInfoAdapter(Context context, ArrayList<SpecialInfo> values)
	{
		super(context, R.layout.main_category_cell, values);
		this.context = context;
		this.aryValue = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		SpecialInfo value = (SpecialInfo) aryValue.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.special_cell, parent, false);
		
		TextView nameText = (TextView) rowView.findViewById(R.id.id_special_cell_name);
		nameText.setText(value.name);
		
		TextView descripitionText = (TextView) rowView.findViewById(R.id.id_special_cell_offer);
		descripitionText.setText(value.offer);

		return rowView;
	}
}
