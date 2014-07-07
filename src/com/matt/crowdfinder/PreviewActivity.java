package com.matt.crowdfinder;

import com.matt.crowdfinder.engine.Global;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class PreviewActivity extends Activity implements OnClickListener
{
	public static String imageURL = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_preview);
	    
	    loadUIElements();
	}
	
	private void loadUIElements()
	{
		ImageView imageView = (ImageView) findViewById(R.id.id_imageView);
		imageView.setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				finish();
			}
        });
		
		if ( imageURL != null ) {
			new Global.DownloadPhotoForImageViewTask(imageView).execute(imageURL);
		}
	}
	
	@Override
	public void onClick(View v)
	{
	}
}
