package com.matt.crowdfinder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import com.matt.crowdfinder.engine.Account;
import com.matt.crowdfinder.engine.Global;
import com.revmob.RevMob;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RecommendActivity extends Activity implements OnClickListener
{
	EditText titleText;
	AutoCompleteTextView commentText;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_recommend);
	    
	    loadUIElements();
	    
	    RevMob revmob = RevMob.start(this);
        revmob.showFullscreen(this);
	}
	
	private void loadUIElements()
	{
		titleText = (EditText) findViewById(R.id.id_title);
		commentText = (AutoCompleteTextView) findViewById(R.id.id_comment);
		
		Button submitBtn = (Button) findViewById(R.id.id_submitBtn);
		submitBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String strTitle = titleText.getText().toString();
				String strComment = commentText.getText().toString();
				if ( strTitle.length() <= 0 ) {
					Toast.makeText(getApplicationContext(), "Please input recommend title.", Toast.LENGTH_LONG).show();
				}
				
				if ( strComment.length() <= 0 ) {
					Toast.makeText(getApplicationContext(), "Please input recommend text.", Toast.LENGTH_LONG).show();
				}
				
				new SendRecommendRequest().execute();
			}
        });
		
		ImageView logoBtn = (ImageView) findViewById(R.id.logo);
		logoBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
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
				
//				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//				intent.putExtra(MainActivity.EXIT_CODE, MainActivity.GOTO_RECOMMEND);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				startActivity(intent);
//				finish();
			}
        });
	}

	@Override
	public void onClick(View v)
	{
	}

	class SendRecommendRequest extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RecommendActivity.this, "Please wait...", "Processing...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.RECOMMEND_API_URL;
			Account account = Account.getInstance();
			
			String strTitle = titleText.getText().toString();
			String strComment = commentText.getText().toString();
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				MultipartEntity multientity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
						         
		        multientity.addPart("userid", new StringBody(account.userIndex));
		        multientity.addPart("title", new StringBody(strTitle));
		        multientity.addPart("comment", new StringBody(strComment));

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
				
				if (res.equals("succ")) {
					Toast.makeText(getApplicationContext(), "Recommend Submission Successful!", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Recommend Submission Failed!", Toast.LENGTH_LONG).show();
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
