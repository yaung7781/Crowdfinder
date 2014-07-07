package com.matt.crowdfinder;

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

import com.matt.crowdfinder.engine.BusinessInfo;
import com.matt.crowdfinder.engine.Global;
import com.matt.crowdfinder.engine.SpecialInfo;
import com.matt.crowdfinder.engine.SpecialInfoAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class SpecialActivity extends Activity implements OnClickListener {
	
	public static BusinessInfo busiInfo;
	ListView listView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_special);
	    
	    loadUIElements();
	    
	    new SendGetSpecialReq().execute();
	}
	
	private void loadUIElements()
	{
		listView = (ListView) findViewById(R.id.id_special_list);
		
		ImageView logo = (ImageView) findViewById(R.id.id_special_logo);
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
	
	class SendGetSpecialReq extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SpecialActivity.this,"Please wait...", "Loading...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.GET_SPECIAL_URL;
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				MultipartEntity multientity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
						         
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
				JSONObject json = new JSONObject(sResponse);
				String res = json.getString("status");
				
				if (res.equals("succ"))
				{
					ArrayList<SpecialInfo> arySpecial = new ArrayList<SpecialInfo>();
					
					JSONArray aryValues = json.getJSONArray("value");
					for ( int i = 0; i < aryValues.length(); i++ )
					{
						JSONObject info = (JSONObject) aryValues.get(i);
						SpecialInfo busiInfo = new SpecialInfo(info);
						arySpecial.add(busiInfo);
					}
					
					Global.arySpecialInfo = arySpecial;
					
					SpecialInfoAdapter adapter = new SpecialInfoAdapter(SpecialActivity.this, arySpecial);
				    listView.setAdapter(adapter);
				    
				    if (arySpecial.size() == 0 ) {
				    	Toast.makeText(getApplicationContext(), "Sorry! There is no special offer for business now.", Toast.LENGTH_LONG).show();
				    }
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Sorry! Can not get special offer for business now.", Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Sorry! Can not get special offer for business now.", Toast.LENGTH_LONG).show();
			}

			progressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
