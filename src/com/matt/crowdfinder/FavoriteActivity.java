package com.matt.crowdfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
import com.matt.crowdfinder.engine.BusiListAdapter;
import com.matt.crowdfinder.engine.BusinessInfo;
import com.matt.crowdfinder.engine.Global;
import com.matt.crowdfinder.R;
import com.revmob.RevMob;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FavoriteActivity extends Activity implements OnClickListener, OnItemLongClickListener {
	private BusiListAdapter favouriteListAdapter = null;
	
	ListView listView;
	protected Object mActionMode;
	public int selectedItem = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_favourite_list);
	    
	    loadUIElements();
	    
	    new SendGetFavoriteReq().execute();
	    
	    RevMob revmob = RevMob.start(this);
        revmob.showFullscreen(this);
	}
	
	private void loadUIElements() {
		
		listView = (ListView) findViewById(R.id.id_list);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BusinessInfo value = (BusinessInfo) Global.aryFavorite.get(position);
				
				BusiDetailActivity.busiInfo = value;
				Intent intent = new Intent(getApplicationContext(), BusiDetailActivity.class);
				startActivity(intent);
            }
		});
	    listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				selectedItem = position;
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FavoriteActivity.this);
				alertDialogBuilder.setTitle("Are you sure want to delete this favourite?");
				alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					}
				})
				.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						new SendDeleteFavoriteReq().execute();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				
				return true;
			}
		});

	    ImageView logo = (ImageView) findViewById(R.id.id_logo);
		logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				onBackPressed();
				finish();
			}
		});
		
		loadTabBarElements();
	}
	
	private void loadTabBarElements() {
		ImageView favoriteBtn = (ImageView) findViewById(R.id.id_favoriteBtn);
		favoriteBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
//				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//				intent.putExtra(MainActivity.EXIT_CODE, MainActivity.GOTO_FAVOURITE);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				startActivity(intent);
//				finish();
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
	
	class SendGetFavoriteReq extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(FavoriteActivity.this,"Please wait...", "Loading Favorite...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.GET_FAVOUR_BUSINESS_URL;
			Account account = Account.getInstance();
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				MultipartEntity multientity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
		         
		        multientity.addPart("userid", new StringBody(account.userIndex));
		        
		        httppost.setEntity(multientity);
				
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity =response.getEntity();
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
					ArrayList<BusinessInfo> aryFavorite = new ArrayList<BusinessInfo>();
					
					JSONArray aryValues = json.getJSONArray("value");
					for ( int i = 0; i < aryValues.length(); i++ )
					{
						JSONObject info = (JSONObject) aryValues.get(i);
						BusinessInfo busiInfo = new BusinessInfo(info);
						aryFavorite.add(busiInfo);
					}
					
					if ( aryFavorite.size() > 0 ) {
						Collections.sort(aryFavorite, new BusiComparator());
					}
					
					Global.aryFavorite = aryFavorite;
					
					favouriteListAdapter = new BusiListAdapter(FavoriteActivity.this, Global.aryFavorite);
					favouriteListAdapter.setEnableEditing();
				    listView.setAdapter(favouriteListAdapter);
				    
				    Toast.makeText(getApplicationContext(), "To delete a favorite, please click one as long.", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Failed to get business information.", Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			progressDialog.dismiss();
		}
	}
	
	public class BusiComparator implements Comparator<BusinessInfo>
	{
	    public int compare(BusinessInfo left, BusinessInfo right)
	    {
	    	if ( left == null || right == null ) {
	    		return 0;
	    	}
	    	
	    	Location locationA = new Location("");
	    	locationA.setLatitude(left.latitude);
	    	locationA.setLongitude(left.longitude);
	    	
	    	Location locationB = new Location("");
	    	locationB.setLatitude(right.latitude);
	    	locationB.setLongitude(right.longitude);
	    	
	    	float distanceA = Global.currentLocation.distanceTo(locationA);
	    	float distanceB = Global.currentLocation.distanceTo(locationB);
	    	
	        return Float.compare(distanceA, distanceB);
	    }
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}

	class SendDeleteFavoriteReq extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(FavoriteActivity.this,"Please wait...", "Deleting Favorite...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.DELETE_FAVOUR_BUSINESS_URL;
			Account account = Account.getInstance();
			
			BusinessInfo busiInfo = Global.aryFavorite.get(selectedItem);
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				MultipartEntity multientity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
		         
		        multientity.addPart("userid", new StringBody(account.userIndex));
		        multientity.addPart("businessid", new StringBody(busiInfo.index));
		        
		        httppost.setEntity(multientity);
				
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity =response.getEntity();
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
			
			try {
				JSONObject json = new JSONObject(sResponse);
				String res = json.getString("status");
				
				if (res.equals("succ")) {
					ArrayList<BusinessInfo> aryFavourite = new ArrayList<BusinessInfo>();
					
					JSONArray aryValues = json.getJSONArray("value");
					for ( int i = 0; i < aryValues.length(); i++ ) {
						JSONObject info = (JSONObject) aryValues.get(i);
						BusinessInfo busiInfo = new BusinessInfo(info);
						aryFavourite.add(busiInfo);
					}
					
					if ( aryFavourite.size() > 0 ) {
						Collections.sort(aryFavourite, new BusiComparator());
					}
					
					Global.aryFavorite = aryFavourite;
					
					favouriteListAdapter = new BusiListAdapter(FavoriteActivity.this, Global.aryFavorite);
				    listView.setAdapter(favouriteListAdapter);
				    
				    Toast.makeText(getApplicationContext(), "You have deleted a favourite successfully.", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Failed to get business information.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			progressDialog.dismiss();
		}
	}
}
