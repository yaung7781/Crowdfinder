package com.matt.crowdfinder.engine;

import java.util.ArrayList;

import com.matt.crowdfinder.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends ArrayAdapter<CategoryInfo>
{
	private final Context context;
	private final ArrayList<CategoryInfo> aryValue;
	
	public CategoryListAdapter(Context context, ArrayList<CategoryInfo> values)
	{
		super(context, R.layout.main_category_cell, values);
		this.context = context;
		this.aryValue = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		CategoryInfo value = (CategoryInfo) aryValue.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.main_category_cell, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.id_main_category_cell_textview);
		textView.setText(value.name);
 
		return rowView;
	}
	
	public CategoryInfo getItemAtPosition(int position)
	{
		CategoryInfo value = (CategoryInfo) aryValue.get(position);
		return value;
	}
}
