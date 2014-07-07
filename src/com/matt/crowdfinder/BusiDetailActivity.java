package com.matt.crowdfinder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.matt.crowdfinder.engine.Account;
import com.matt.crowdfinder.engine.AttenderInfo;
import com.matt.crowdfinder.engine.BusinessInfo;
import com.matt.crowdfinder.engine.CategoryInfo;
import com.matt.crowdfinder.engine.Global;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class BusiDetailActivity extends Activity implements OnClickListener
{
	public static BusinessInfo busiInfo;
	ImageView photoView0;
	ImageView photoView1;
	ImageView photoView2;
	RatingBar ratingBar;
	
	Button addFavourite;
	Button whoIsGoing;
	Button shareLocation;
	
	Button attendBtn;
	WebView webView;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_busi_detail);
	    
	    loadUIElements();
	    
	    if ( busiInfo.photo0 == null )
			photoView0.setImageDrawable(null);
		else
			new DownloadPhotoTask(photoView0).execute(busiInfo.photo0);
		
		if ( busiInfo.photo1 == null )
			photoView1.setImageDrawable(null);
		else
			new DownloadPhotoTask(photoView1).execute(busiInfo.photo1);
		
		if ( busiInfo.photo2 == null )
			photoView2.setImageDrawable(null);
		else
			new DownloadPhotoTask(photoView2).execute(busiInfo.photo2);
		
	    new SendCheckFavoriteReq().execute();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if ( busiInfo.video.equals("") )
			webView.setVisibility(View.INVISIBLE);
		else
		{
			WebSettings ws = webView.getSettings();
		    ws.setPluginState(PluginState.ON);
		    ws.setJavaScriptEnabled(true);
		    ws.setJavaScriptCanOpenWindowsAutomatically(true);
		    
		    webView.loadData(busiInfo.video, "text/html", null);
		    webView.setWebViewClient(new VideoWebViewClient());
			webView.loadDataWithBaseURL(null, busiInfo.video, "text/html", "utf-8", null);
		}
		
		ratingBar.setRating(busiInfo.rate);
	}
	
	private void loadUIElements()
	{
		webView = (WebView) findViewById(R.id.id_busidetail_webview);
		
		TextView nameText = (TextView) findViewById(R.id.id_busidetail_busiNameText);
		nameText.setText(busiInfo.name);
		
		TextView descriptionText = (TextView) findViewById(R.id.id_busidetail_busiDescriptionText);
		descriptionText.setText(busiInfo.description);
		
		TextView openHourText = (TextView) findViewById(R.id.id_busidetail_busiOpenHourText);
		openHourText.setText(busiInfo.openHour);
		
		photoView0 = (ImageView) findViewById(R.id.id_busidetail_image0);
		photoView0.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if ( busiInfo.photo0 != null ) {
					PreviewActivity.imageURL = busiInfo.photo0;
					Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
					startActivity(intent);
				}
			}
        });
		
		photoView1 = (ImageView) findViewById(R.id.id_busidetail_image1);
		photoView1.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if ( busiInfo.photo1 != null ) {
					PreviewActivity.imageURL = busiInfo.photo1;
					Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
					startActivity(intent);
				}
			}
        });
		
		photoView2 = (ImageView) findViewById(R.id.id_busidetail_image2);
		photoView2.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if ( busiInfo.photo2 != null ) {
					PreviewActivity.imageURL = busiInfo.photo2;
					Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
					startActivity(intent);
				}
			}
        });
		
		ratingBar = (RatingBar) findViewById(R.id.id_busidetail_ratingbar);
		ratingBar.setRating(busiInfo.rate);
		ratingBar.setOnTouchListener(new OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            return true;
	        }
	    });
		
		ImageView moreInfo = (ImageView) findViewById(R.id.id_busidetail_moreInfoBtn);
		moreInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				BusiContactActivity.busiInfo = busiInfo;
				Intent goToNextActivity = new Intent(getApplicationContext(), BusiContactActivity.class);
				startActivity(goToNextActivity);
			}
		});
		
		addFavourite = (Button) findViewById(R.id.id_busidetail_addFavourite);
		addFavourite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new SendAddFavoriteReq().execute();
			}
		});
		
		Button whosGoingBtn = (Button) findViewById(R.id.id_busidetail_whoIsGoing);
		whosGoingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if ( Account.getInstance().facebookId == null || Account.getInstance().facebookId.length() <= 0 ) {
					Toast.makeText(getApplicationContext(), "Sorry, You can find your facebook friend when you had login with your facebook account.", Toast.LENGTH_LONG).show();
				} else if ( Global.aryAttender != null ) {
					Intent goToNextActivity = new Intent(getApplicationContext(), AttenderListActivity.class);
					startActivity(goToNextActivity);
				}
			}
		});
		
		Button shareLocationBtn = (Button) findViewById(R.id.id_busidetail_shareLocation);
		shareLocationBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BusiDetailActivity.this);
				alertDialogBuilder.setTitle("Share Business Location");
				alertDialogBuilder.setPositiveButton("Facebook", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					}
				})
				.setNegativeButton("Twitter", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});
		
		attendBtn = (Button) findViewById(R.id.id_busidetail_attend);
		attendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new SendAttendReq().execute();
			}
		});
		
		Button specialBtn = (Button) findViewById(R.id.id_busidetail_special);
		specialBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SpecialActivity.busiInfo = busiInfo;
				Intent goToNextActivity = new Intent(getApplicationContext(), SpecialActivity.class);
				startActivity(goToNextActivity);
			}
		});
		
		Button eventBtn = (Button) findViewById(R.id.id_busidetail_event);
		eventBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EventListActivity.busiInfo = busiInfo;
				Intent goToNextActivity = new Intent(getApplicationContext(), EventListActivity.class);
				startActivity(goToNextActivity);
			}
		});
		
		ImageView logo = (ImageView) findViewById(R.id.id_busidetail_logo);
		logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		
		loadTabBarElements();
	}
	
	private void loadTabBarElements() {
		ImageView favoriteBtn = (ImageView) findViewById(R.id.id_favoriteBtn);
		favoriteBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra(MainActivity.EXIT_CODE, MainActivity.GOTO_FAVOURITE);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
        });
		
		ImageView eventBtn = (ImageView) findViewById(R.id.id_eventBtn);
		eventBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra(MainActivity.EXIT_CODE, MainActivity.GOTO_EVENT);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
        });
		
		ImageView accountBtn = (ImageView) findViewById(R.id.id_accountBtn);
		accountBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra(MainActivity.EXIT_CODE, MainActivity.GOTO_ACCOUNT);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
        });
		
		ImageView recommendBtn = (ImageView) findViewById(R.id.id_recommendBtn);
		recommendBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra(MainActivity.EXIT_CODE, MainActivity.GOTO_RECOMMEND);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
        });
	}
	
	private void enableButton(Button button, boolean bEnabled)
	{
		button.setEnabled(bEnabled);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	private class VideoWebViewClient extends WebViewClient
	{
        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url)
        {
        	webview.setWebChromeClient(new WebChromeClient());
        	webview.loadUrl(url);
        	return true;
        }
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
			String imageUrl = params[0];
			Bitmap bitmap = null;
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				bitmap = BitmapFactory.decodeStream(input);
				
			} catch (IOException e) {
				e.printStackTrace();
				bitmap = null;
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
	
	class SendCheckFavoriteReq extends AsyncTask<String, Void, Void>
	{
		String sResponse = "";
		private boolean bIsFavourite = false;
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
//			progressDialog = ProgressDialog.show(BusiDetailActivity.this, "Please wait...", "Loading...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.IS_FAVOURITE_URL;
			Account account = Account.getInstance();
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				MultipartEntity multientity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
						         
		        multientity.addPart("userid", new StringBody(account.userIndex));
		        multientity.addPart("businessid", new StringBody(busiInfo.index));
		        
		        httppost.setEntity(multientity);				
				
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				sResponse = EntityUtils.toString(entity);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				sResponse = "";
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			try
			{
				Log.v("info", sResponse);
				JSONObject json = new JSONObject(sResponse);
				String res = json.getString("status");
				
				if (res.equals("succ"))
				{
					bIsFavourite = true;
				}
				
				enableButton(addFavourite, !bIsFavourite);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			
//			progressDialog.dismiss();

		}
	}
	
	class SendAddFavoriteReq extends AsyncTask<String, Void, Void>
	{
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(BusiDetailActivity.this, "Please wait...", "Processing...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.ADD_FAVOURITE_URL;
			Account account = Account.getInstance();
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				MultipartEntity multientity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
						         
		        multientity.addPart("userid", new StringBody(account.userIndex));
		        multientity.addPart("businessid", new StringBody(busiInfo.index));
		        
		        httppost.setEntity(multientity);
		        
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				sResponse = EntityUtils.toString(entity);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				sResponse = "";
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			try
			{
				JSONObject json = new JSONObject(sResponse);
				String res = json.getString("status");
				
				Log.v("info", sResponse.toString());
				
				if (res.equals("succ"))
				{
					enableButton(addFavourite, false);
					Toast.makeText(getApplicationContext(), "Successed to add favourite.", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Failed to add favourite. Try again later", Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			progressDialog.dismiss();
		}
	}
	
	class SendAttendReq extends AsyncTask<String, Void, Void>
	{
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(BusiDetailActivity.this, "Please wait...", "Processing...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.ATTEND_URL;
			Account account = Account.getInstance();
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				MultipartEntity multientity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
						         
		        multientity.addPart("userid", new StringBody(account.userIndex));
		        multientity.addPart("businessid", new StringBody(busiInfo.index));
		        
		        httppost.setEntity(multientity);
		        
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				sResponse = EntityUtils.toString(entity);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				sResponse = "";
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			try
			{
				JSONObject json = new JSONObject(sResponse);
				String res = json.getString("status");
				
				Log.v("info", sResponse.toString());
				
				ArrayList<AttenderInfo> aryAttender = new ArrayList<AttenderInfo>();
				if (res.equals("succ")) {
					enableButton(attendBtn, false);
					
					JSONArray aryValues = json.getJSONArray("value");
					for ( int i = 0; i < aryValues.length(); i++ )
					{
						JSONObject info = (JSONObject) aryValues.get(i);
						AttenderInfo attenderInfo = new AttenderInfo(info);
						aryAttender.add(attenderInfo);
					}
					Global.aryAttender = aryAttender;
					
				} else {
					Toast.makeText(getApplicationContext(), "Failed to attend. Try again later", Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			progressDialog.dismiss();
		}
	}
}
