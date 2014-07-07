package com.matt.crowdfinder.engine;

import java.util.ArrayList;

import com.matt.crowdfinder.R;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;

public class BusiListAdapter extends ArrayAdapter<BusinessInfo> implements OnClickListener, OnTouchListener, OnDragListener
{
	private final Context context;
	private final ArrayList<BusinessInfo> aryValue;
	private boolean enabledEditing = false;
	
	private Button deleteBtn;
	
	public BusiListAdapter(Context context, ArrayList<BusinessInfo> values)
	{
		super(context, R.layout.main_category_cell, values);
		this.context = context;
		this.aryValue = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		BusinessInfo busiInfo = (BusinessInfo) aryValue.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.busi_list_cell, parent, false);
		
		TextView nameText = (TextView) rowView.findViewById(R.id.id_busicell_busiNameText);
		nameText.setText(busiInfo.name);
		
		TextView descripitionText = (TextView) rowView.findViewById(R.id.id_busicell_busiDescriptionText);
		descripitionText.setText(busiInfo.description);
		
		TextView openHourText = (TextView) rowView.findViewById(R.id.id_busicell_busiOpenHourText);
		openHourText.setText(busiInfo.openHour);
		
		Location location = new Location("");
		location.setLatitude(busiInfo.latitude);
		location.setLongitude(busiInfo.longitude);
    	
		TextView distanceText = (TextView) rowView.findViewById(R.id.id_busicell_distanceText);
		if ( Global.currentLocation != null )
		{
			float distanceInMeter = Global.currentLocation.distanceTo(location);
			int distanceInMile = (int)(distanceInMeter/1609.34f);
			String text = Integer.toString(distanceInMile) + "mi";
			distanceText.setText(text);
		}
		else
		{
			distanceText.setText("Unknown");
		}
		
		ImageView iconView = (ImageView) rowView.findViewById(R.id.id_busicell_icon);
		if ( !busiInfo.hasVideo() )
			iconView.setImageResource(R.drawable.mark_gray);
		
//		FrameLayout starView = (FrameLayout) rowView.findViewById(R.id.id_busicell_starImage);
//		LayoutParams layoutParams = starView.getLayoutParams();
//		layoutParams.width = (int) ((float)layoutParams.width * (busiInfo.rate / 5.0f));
		
		RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.id_ratingbar);
		ratingBar.setRating(busiInfo.rate);
		ratingBar.setOnTouchListener(new OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            return true;
	        }
	    });
		
		deleteBtn = (Button) rowView.findViewById(R.id.id_deleteBtn);
		deleteBtn.setVisibility(RelativeLayout.GONE);
		
		if ( this.enabledEditing ) {
			rowView.setOnDragListener(new OnDragListener() {
				
				private float startX = 0;
				private float endX = 0;
				@Override
				public boolean onDrag(View v, DragEvent event) {
					
					if ( event.getAction() == DragEvent.ACTION_DRAG_STARTED ) {
						startX = event.getX();
					} else if ( event.getAction() == DragEvent.ACTION_DRAG_ENDED ) {
						endX = event.getX();
						
						float offset = endX - startX;
						if ( offset < 0 ) {
							deleteBtn.setVisibility(RelativeLayout.VISIBLE);
						} else {
							deleteBtn.setVisibility(RelativeLayout.GONE);
						}
					}
					
					return true;					
				}
			});
		}
		
		return rowView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void setEnableEditing() {
		this.enabledEditing = true;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
