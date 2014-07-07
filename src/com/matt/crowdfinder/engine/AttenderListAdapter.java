package com.matt.crowdfinder.engine;

import java.io.InputStream;
import java.util.ArrayList;

import com.matt.crowdfinder.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AttenderListAdapter extends ArrayAdapter<ArrayList<AttenderInfo>>
{
	private final Context context;
	private final ArrayList<ArrayList<AttenderInfo>> aryValue;
	
	public AttenderListAdapter(Context context, ArrayList<ArrayList<AttenderInfo>> values)
	{
		super(context, R.layout.main_category_cell, values);
		this.context = context;
		this.aryValue = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ArrayList<AttenderInfo> aryAttender = (ArrayList<AttenderInfo>) aryValue.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.attender_cell, parent, false);
		
		ArrayList<ImageView> aryImageView = new ArrayList<ImageView>();
		ImageView imageView1 = (ImageView) rowView.findViewById(R.id.id_profile_1);
		aryImageView.add(imageView1);
		ImageView imageView2 = (ImageView) rowView.findViewById(R.id.id_profile_2);
		aryImageView.add(imageView2);
		ImageView imageView3 = (ImageView) rowView.findViewById(R.id.id_profile_3);
		aryImageView.add(imageView3);
		
		ArrayList<TextView> aryName = new ArrayList<TextView>();
		TextView textView1 = (TextView) rowView.findViewById(R.id.id_name_1);
		aryName.add(textView1);
		TextView textView2 = (TextView) rowView.findViewById(R.id.id_name_2);
		aryName.add(textView2);
		TextView textView3 = (TextView) rowView.findViewById(R.id.id_name_3);
		aryName.add(textView3);
		
		for ( int i = 0; i < aryAttender.size(); i++ ) {
			AttenderInfo attendInfo = aryAttender.get(i);
			
			if ( attendInfo.avatar != null ) {
				ImageView imageView = aryImageView.get(i);
				new DownloadPhotoTask(imageView).execute(attendInfo.avatar);				
			}
			
			TextView nameText = aryName.get(i);
			nameText.setText(attendInfo.firstName + attendInfo.lastName);
		}
 
		return rowView;
	}
	
	class DownloadPhotoTask extends AsyncTask<String, Void, Bitmap>
	{
		ImageView imageView;
		public DownloadPhotoTask(ImageView bmImage)
		{
	        this.imageView = bmImage;
	    }
		
		@Override
		protected Bitmap doInBackground(String... params)
		{
			String url = params[0];
			Bitmap bitmap = null;
			try
			{
				InputStream in = new java.net.URL(url).openStream();
				bitmap = BitmapFactory.decodeStream(in);
			}
			catch (Exception e)
			{
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return bitmap;
		}
		
		@Override 
	    protected void onPostExecute(Bitmap result)
		{
	        super.onPostExecute(result);
	        imageView.setImageBitmap(result);
	    }
	}
}
