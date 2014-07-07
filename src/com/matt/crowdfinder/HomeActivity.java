package com.matt.crowdfinder;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.matt.crowdfinder.R;
import com.matt.crowdfinder.engine.Account;
import com.matt.crowdfinder.engine.Global;
import com.matt.crowdfinder.engine.MyLocation;
import com.matt.crowdfinder.engine.MyLocation.LocationResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnClickListener
{
	
	private Button loginBtn;
	private Button loginWithFacebookBtn;
	private Button signupBtn;
	private Button forgotPasswordBtn;
	
	private EditText emailText;
	private EditText passwordText;
	
	public String email;
	public String password;
	
	ProgressDialog progressDialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_home);
	    
	    loadUIElements();

	    progressDialog = ProgressDialog.show(HomeActivity.this, "Please wait...", "Loading current location...", true);
	    
	    LocationResult locationResult = new LocationResult() {
	        @Override
	        public void gotLocation(Location location) {
	        	
	        	Global.currentLocation = location;
	        	
	        	progressDialog.dismiss();
	        	
//	        	Toast.makeText(getApplicationContext(), "You have gotten your current location.", Toast.LENGTH_LONG).show();
	        }
	    };
	    MyLocation myLocation = new MyLocation();
	    myLocation.getLocation(this, locationResult);
	    
	    if ( Global.isDebugging ) {
	    	emailText.setText("yaung7781@gmail.com");
	    	passwordText.setText("123456789");
	    }
	}
	
	private void loadUIElements()
	{
		loginBtn = (Button) findViewById(R.id.id_home_loginBtn);
	    loginBtn.setOnClickListener(this);

	    loginWithFacebookBtn = (Button) findViewById(R.id.id_home_facebookbtn);
	    loginWithFacebookBtn.setOnClickListener(this);
	    
	    signupBtn = (Button) findViewById(R.id.id_home_signupBtn);
	    signupBtn.setOnClickListener(this);
	    
	    forgotPasswordBtn = (Button) findViewById(R.id.id_home_forgotPasswordBtn);
	    forgotPasswordBtn.setOnClickListener(this);
	    
	    emailText = (EditText) findViewById(R.id.emailText);
	    passwordText = (EditText) findViewById(R.id.passwordText);
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String email = myPrefs.getString("email", "");
        String password = myPrefs.getString("password", "");
        emailText.setText(email);
        passwordText.setText(password);
    }

	@Override
	public void onClick(View arg0)
	{
		if ( arg0 == loginBtn )
		{
			onLoginBtnClicked();
		}
		
		if ( arg0 == loginWithFacebookBtn )
		{
			onLoginFacebookBtnClicked();
		}
		
		if ( arg0 == signupBtn )
		{
			onSignUpBtnClicked();
		}
		
		if ( arg0 == forgotPasswordBtn )
		{
			onForgotPasswordBtnClicked();
		}
	}
	
	private void onLoginBtnClicked()
	{
		email = emailText.getText().toString();
		password = passwordText.getText().toString();
		
		if ( !Global.isEmailValid(email) )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
			builder.setTitle("Crowdfinder Login");
			builder.setMessage("Enter your email.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		if ( password.equals("") )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
			builder.setTitle("Crowdfinder Login");
			builder.setMessage("Enter your password.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		new SendReqLogin().execute(email, password);
	}
	
	private void onLoginFacebookBtnClicked()
	{
		
	}
	
	private void onSignUpBtnClicked()
	{
		Intent goToNextActivity = new Intent(getApplicationContext(), SignUpActivity.class);
		startActivity(goToNextActivity);
	}
	
	private void onForgotPasswordBtnClicked()
	{
		
	}
	
	class SendReqLogin extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(HomeActivity.this,"Please wait...", "Logging in...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.LOGIN_URL;
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("email", params[0]));
				nameValuePairs.add(new BasicNameValuePair("password", params[1]));
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
					JSONObject userDetail = json.getJSONObject("value");
					
					Account account = Account.getInstance();
					if ( account.fillWithJSONObject(userDetail) )
					{
						SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
						SharedPreferences.Editor editor = myPrefs.edit();
						editor.putString("email", account.email);
						editor.putString("password", account.password);
						editor.commit();
						
						gotoMainActivity();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Login has been failed. Try again later.", Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			
			progressDialog.dismiss();
		}
	}
	
	
	private void gotoMainActivity()
	{
		Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(goToNextActivity);
	}
}
