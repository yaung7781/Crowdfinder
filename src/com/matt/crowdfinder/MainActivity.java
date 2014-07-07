package com.matt.crowdfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView; 
import android.widget.Toast; 
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matt.crowdfinder.engine.BusinessInfo;
import com.matt.crowdfinder.engine.CategoryInfo;
import com.matt.crowdfinder.engine.CategoryListAdapter;
import com.matt.crowdfinder.engine.EventInfo;
import com.matt.crowdfinder.engine.Global;

public class MainActivity extends android.support.v4.app.FragmentActivity implements OnMarkerClickListener, OnInfoWindowClickListener {
	
	public static final String EXIT_CODE			= "EXIT_CODE";
	public static final String GOTO_FAVOURITE		= "GOTO_FAVOURITE";
	public static final String GOTO_EVENT			= "GOTO_EVENT";
	public static final String GOTO_ACCOUNT		= "GOTO_ACCOUNT";
	public static final String GOTO_RECOMMEND		= "GOTO_RECOMMEND";
	
	private RelativeLayout searchLayout;
	private GoogleMap mapView;
	
	private RelativeLayout categoryView;
	private ListView categoryList;
	
	private EditText searchText;
	
	private int categoryViewOriginY = 0;
	private ArrayList<Marker> aryMarker;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_main);

	    loadUIElements();
	    
	    if ( Global.currentLocation != null && mapView != null ) {
	    	LatLng latLng = new LatLng(Global.currentLocation.getLatitude(), Global.currentLocation.getLongitude());
	    	mapView.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	    	mapView.animateCamera(CameraUpdateFactory.zoomTo(15));
	    }
	    
	    new SendGetBusinessReq().execute();
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		String exitCode = intent.getStringExtra(MainActivity.EXIT_CODE);
		if ( exitCode == null )
			return;
		
		if ( exitCode.equals(MainActivity.GOTO_FAVOURITE) ) {
			
			Intent newIntent = new Intent(getApplicationContext(), FavoriteActivity.class);
			startActivity(newIntent);
		}
		
		if ( exitCode.equals(MainActivity.GOTO_EVENT) ) {
			
			Intent newIntent = new Intent(getApplicationContext(), EventListActivity.class);
			startActivity(newIntent);
		}

		if ( exitCode.equals(MainActivity.GOTO_ACCOUNT) ) {
			
			Intent newIntent = new Intent(getApplicationContext(), AccountActivity.class);
			startActivity(newIntent);
		}

		if ( exitCode.equals(MainActivity.GOTO_RECOMMEND) ) {
			
			Intent newIntent = new Intent(getApplicationContext(), RecommendActivity.class);
			startActivity(newIntent);
		}
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		if( v == searchText ) {
			return false;
		} else {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			return true;
		}
	}
	
	/** Base Proc */
	private void loadUIElements()
	{
		mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_main_mapView)).getMap();
		mapView.setOnInfoWindowClickListener(this);
		mapView.setMyLocationEnabled(true);
		
		Button signoutBtn = (Button) findViewById(R.id.id_main_signoutBtn);
		signoutBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				onBackPressed();
			}
        });
		
		searchLayout = (RelativeLayout) findViewById(R.id.id_main_searchView);
		searchLayout.setVisibility(RelativeLayout.GONE);
		
		Button searchBtn = (Button) findViewById(R.id.id_main_searchBtn);
		searchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if ( searchLayout.getVisibility() == RelativeLayout.GONE ) {
					searchLayout.setVisibility(RelativeLayout.VISIBLE);
					searchText.requestFocus();
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
				} else {
					searchLayout.setVisibility(RelativeLayout.GONE);
					
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
				}
			}
        });
		
		categoryView = (RelativeLayout) findViewById(R.id.id_main_categoryBkgnd);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)categoryView.getLayoutParams();
		categoryViewOriginY = params.topMargin;
		
		categoryList = (ListView) findViewById(R.id.id_main_categoryList);
		
		View categoryHeader = (View) findViewById(R.id.id_main_categoryHeader);
		categoryHeader.setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)categoryView.getLayoutParams();
				
				if ( params.topMargin < categoryViewOriginY )
					showCategoryList(false);
				else
					showCategoryList(true);
			}
        });
		
		searchText = (EditText) findViewById(R.id.id_main_searchText);
		
		Button goBtn = (Button) findViewById(R.id.id_main_gobtn);
		goBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				BusiListActivity.keyword = searchText.getText().toString();
				BusiListActivity.categoryInfo = null;
				Intent intent = new Intent(getApplicationContext(), BusiListActivity.class);
				startActivity(intent);
			}
        });
		
		loadTabBarElements();
	}
	
	private void loadTabBarElements() {
		ImageView favoriteBtn = (ImageView) findViewById(R.id.id_favoriteBtn);
		favoriteBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
				Intent newIntent = new Intent(getApplicationContext(), FavoriteActivity.class);
				startActivity(newIntent);
			}
        });
		
		ImageView eventBtn = (ImageView) findViewById(R.id.id_eventBtn);
		eventBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent newIntent = new Intent(getApplicationContext(), EventListActivity.class);
				startActivity(newIntent);
			}
        });
		
		ImageView accountBtn = (ImageView) findViewById(R.id.id_accountBtn);
		accountBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				Intent newIntent = new Intent(getApplicationContext(), AccountActivity.class);
				startActivity(newIntent);
			}
        });
		
		ImageView recommendBtn = (ImageView) findViewById(R.id.id_recommendBtn);
		recommendBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent newIntent = new Intent(getApplicationContext(), RecommendActivity.class);
				startActivity(newIntent);
			}
        });
	}
	
	private void showCategoryList(final boolean bShow)
	{
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)categoryView.getLayoutParams();
	    params.topMargin = bShow ? -params.height : categoryViewOriginY;
	    categoryView.setLayoutParams(params);
	    
//	    ImageView arrow = (ImageView) findViewById(R.id.id_main_categoryArrow);
//	    arrow.setRotation(bShow ? 180 : 0);
	}
	
	/** Control Proc */
	
	/** SendGetBusinessReq */
	class SendGetBusinessReq extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MainActivity.this,"Please wait...", "Loading Business...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.GET_BUSINESS_URL;
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
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
					ArrayList<BusinessInfo> aryBusiness = new ArrayList<BusinessInfo>();
					
					JSONArray aryValues = json.getJSONArray("value");
					for ( int i = 0; i < aryValues.length(); i++ )
					{
						JSONObject info = (JSONObject) aryValues.get(i);
						BusinessInfo busiInfo = new BusinessInfo(info);
						aryBusiness.add(busiInfo);
					}
					
					if ( aryBusiness.size() > 0 ) {
						Collections.sort(aryBusiness, new BusiComparator());
					}
					
					Global.aryBusiness = aryBusiness;
					aryMarker = new ArrayList<Marker>();
					for ( int i = 0; i < Global.aryBusiness.size(); i++ )
					{
						BusinessInfo busiInfo = (BusinessInfo) Global.aryBusiness.get(i);
						
						MarkerOptions markerOption = new MarkerOptions();
						markerOption.position(new LatLng(busiInfo.latitude, busiInfo.longitude));
						markerOption.title(busiInfo.name);
						markerOption.snippet(busiInfo.description);
						
						if ( busiInfo.hasVideo() )
							markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red));
						else
							markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red_gray));
						
						if ( mapView != null ) {
							Marker marker = mapView.addMarker(markerOption);
							aryMarker.add(marker);
						}
					}
					
					Log.v("info", Integer.toString(Global.aryBusiness.size()) + " businesses have been loaded.");
					
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
			
			new SendGetCategoryReq().execute();
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
	
	/** SendGetCategoryReq */
	class SendGetCategoryReq extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MainActivity.this, "Please wait...", "Loading Categories...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.GET_CATEGORY_URL;
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
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
					ArrayList<CategoryInfo> aryCategory = new ArrayList<CategoryInfo>();
					
					JSONArray aryValues = json.getJSONArray("value");
					for ( int i = 0; i < aryValues.length(); i++ )
					{
						JSONObject info = (JSONObject) aryValues.get(i);
						CategoryInfo categoryInfo = new CategoryInfo(info);
						aryCategory.add(categoryInfo);
					}
					Global.aryCategory = aryCategory;
					
					CategoryListAdapter adapter = new CategoryListAdapter(MainActivity.this, aryCategory);
					categoryList.setAdapter(adapter);             
					
					categoryList.setOnItemClickListener(new OnItemClickListener() {
						@Override
			            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			            {
							CategoryInfo value = (CategoryInfo) Global.aryCategory.get(position);
							
							BusiListActivity.categoryInfo = value;
							BusiListActivity.keyword = null;
							Intent goToNextActivity = new Intent(getApplicationContext(), BusiListActivity.class);
							startActivity(goToNextActivity);
			            }
					});
					
					Log.v("info", Integer.toString(Global.aryCategory.size()) + " categories have been loaded.");
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Failed to get category information.", Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			progressDialog.dismiss();
			
			new SendGetEventReq().execute();
		}
	}
	
	class SendGetEventReq extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MainActivity.this,"Please wait...", "Loading Events...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.GET_EVENTS_URL;
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
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
					ArrayList<EventInfo> aryEvent = new ArrayList<EventInfo>();
					
					JSONArray aryValues = json.getJSONArray("value");
					for ( int i = 0; i < aryValues.length(); i++ )
					{
						JSONObject info = (JSONObject) aryValues.get(i);
						EventInfo eventInfo = new EventInfo(info);
						
						if ( eventInfo.isExpired() )
							continue;
						
						aryEvent.add(eventInfo);
					}
					
					Collections.sort(aryEvent, new EventComparator());
					
					Global.aryEventInfo = aryEvent;
					
					Log.v("info", Integer.toString(Global.aryEventInfo.size()) + " events have been loaded.");
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Sorry! Can not get event datas now.", Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Sorry! Can not get event datas now.", Toast.LENGTH_LONG).show();
			}

			progressDialog.dismiss();
		}
	}
	
	public class EventComparator implements Comparator<EventInfo>
	{
	    public int compare(EventInfo left, EventInfo right)
	    {
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
	public boolean onMarkerClick(Marker arg0) {
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		
		int index = aryMarker.indexOf(marker);
		BusinessInfo busiInfo = Global.aryBusiness.get(index);
		BusiDetailActivity.busiInfo = busiInfo;
		Intent intent = new Intent(getApplicationContext(), BusiDetailActivity.class);
		startActivity(intent);
	}
}
