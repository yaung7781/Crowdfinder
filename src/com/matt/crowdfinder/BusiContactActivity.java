package com.matt.crowdfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.matt.crowdfinder.BusiDetailActivity.SendCheckFavoriteReq;
import com.matt.crowdfinder.engine.Account;
import com.matt.crowdfinder.engine.BusinessInfo;
import com.matt.crowdfinder.engine.CategoryInfo;
import com.matt.crowdfinder.engine.CategoryListAdapter;
import com.matt.crowdfinder.engine.Global;
import com.matt.crowdfinder.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BusiContactActivity extends Activity implements OnClickListener, OnTouchListener
{
	public static String keyword = null;
	public static BusinessInfo busiInfo;
	private float newRating;

	private static ImageView grayStar;
	private static FrameLayout ratingTouchPan;
	private static FrameLayout starView;
	
	private ScrollView mainScrollView;
	private ScrollView descriptionScrollView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_busi_contact);
	    
	    loadUIElements();
	}
	
	private void loadUIElements() {
		ImageView logo = (ImageView) findViewById(R.id.id_busicontact_logo);
		logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		
		TextView nameText = (TextView) findViewById(R.id.id_busicontact_name);
		nameText.setText(busiInfo.name);
		
		TextView addressText = (TextView) findViewById(R.id.id_busicontact_address);
		addressText.setText(busiInfo.address);
		addressText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AddressActivity.busiInfo = busiInfo;
				Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
				startActivity(intent);
			}
		});
		
		TextView descriptionText = (TextView) findViewById(R.id.id_busicontact_description);
		descriptionText.setText(busiInfo.description);
		
		TextView openHourText = (TextView) findViewById(R.id.id_busicontact_openHour);
		openHourText.setText(busiInfo.openHour);
		
		TextView categoryText = (TextView) findViewById(R.id.id_busicontact_category);
		if ( busiInfo.category.length() > 0 ) {
			StringTokenizer tokenizer = new StringTokenizer(busiInfo.category, "|");
			String category = "";
			while ( tokenizer.hasMoreElements() ) {
				String categoryIndex = tokenizer.nextToken();
				
				for ( int i = 0; i < Global.aryCategory.size(); i++ ) {
					CategoryInfo categoryInfo = Global.aryCategory.get(i);
					if ( categoryInfo.id.equals(categoryIndex) ) {
						if ( tokenizer.hasMoreElements() ) {
							category = category + categoryInfo.name + ", ";
						} else {
							category = category + categoryInfo.name;
						}
					}
				}
			}
			
			categoryText.setText(category);
		}
		
		
		TextView telephoneText = (TextView) findViewById(R.id.id_busicontact_contact);
		telephoneText.setText(busiInfo.contact);
		telephoneText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( busiInfo.contact.length() > 0 ) {
					String url = "tel:" + busiInfo.contact;
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse(url));
					startActivity(callIntent);
				}
			}
		});
		
		TextView facebookText = (TextView) findViewById(R.id.id_busicontact_facebook);
		facebookText.setText(busiInfo.fbURL);
		facebookText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if ( busiInfo.fbURL != null && busiInfo.fbURL.length() > 1 ) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(busiInfo.fbURL));
					startActivity(browserIntent);
				}
			}
		});
		
		TextView twitterText = (TextView) findViewById(R.id.id_busicontact_twitter);
		twitterText.setText(busiInfo.twitterURL);
		twitterText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if ( busiInfo.twitterURL != null && busiInfo.twitterURL.length() > 1 ) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(busiInfo.twitterURL));
					startActivity(browserIntent);
				}
			}
		});
		
		TextView linkText = (TextView) findViewById(R.id.id_busicontact_link);
		linkText.setText(busiInfo.otherUrl);
		linkText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if ( busiInfo.otherUrl != null && busiInfo.otherUrl.length() > 1 ) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(busiInfo.otherUrl));
					startActivity(browserIntent);
				}
			}
		});

		RatingBar ratingBar = (RatingBar) findViewById(R.id.id_ratingbar);
		ratingBar.setRating(busiInfo.rate);
		ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
            	ratingBar.setRating(rating);
            	
            	newRating = rating;
            	
            	Log.v("info", Float.toString(newRating));
            }
        });
		
		Button rateBtn = (Button) findViewById(R.id.id_busicontact_rateBtn);
		rateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( newRating == 0 ) {
					Toast.makeText(getApplicationContext(), "Please input your rating mark for this business.", Toast.LENGTH_LONG).show();
				} else {
					new SendChangeRatingReq().execute();
				}
			}
		});
		
		mainScrollView = (ScrollView) findViewById(R.id.id_mainScrollView);
		descriptionScrollView = (ScrollView) findViewById(R.id.id_descriptionScrollView);
		mainScrollView.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				descriptionScrollView.requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});
		descriptionScrollView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                mainScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/** SendChangeRatingReq */
	class SendChangeRatingReq extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(BusiContactActivity.this, "Please wait...", "Processing...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.CHANGE_RATE_URL;
			Account account = Account.getInstance();
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("userid", account.userIndex));
				nameValuePairs.add(new BasicNameValuePair("businessid", busiInfo.index));
				nameValuePairs.add(new BasicNameValuePair("marks", Float.toString(newRating)));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
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
				JSONObject json = new JSONObject(sResponse);
				String res = json.getString("status");
				
				if (res.equals("succ"))
				{
					float avgMark = Float.parseFloat(json.getString("avg_mark"));
					
					busiInfo.rate = avgMark;
					
					Log.v("info", "business rating has been updated successfully (" + busiInfo.index + ")." );
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Failed to change rating. Please try again later.", Toast.LENGTH_LONG).show();
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
