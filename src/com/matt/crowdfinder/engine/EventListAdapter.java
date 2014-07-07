package com.matt.crowdfinder.engine;

import java.util.ArrayList;

import com.matt.crowdfinder.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventListAdapter extends ArrayAdapter<EventInfo> {

	private final Context context;
	private final ArrayList<EventInfo> aryValue;
	
	public EventListAdapter(Context context, ArrayList<EventInfo> values)
	{
		super(context, R.layout.main_category_cell, values);
		this.context = context;
		this.aryValue = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		EventInfo value = (EventInfo) aryValue.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.event_list_cell, parent, false);
		
		TextView nameText = (TextView) rowView.findViewById(R.id.id_eventcell_name);
		nameText.setText(value.name);
		
		TextView descripitionText = (TextView) rowView.findViewById(R.id.id_eventcell_description);
		descripitionText.setText(value.description);
		
		TextView dateText = (TextView) rowView.findViewById(R.id.id_eventcell_date);
		dateText.setText(value.date);
		
		TextView busiNameText = (TextView) rowView.findViewById(R.id.id_eventcell_businame);
		busiNameText.setText(value.busiName);

		return rowView;
	}
}
