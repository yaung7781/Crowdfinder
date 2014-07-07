package com.matt.crowdfinder;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import com.matt.crowdfinder.engine.Account;
import com.matt.crowdfinder.engine.Global;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SignUpActivity extends Activity implements OnClickListener
{
	private RelativeLayout scrollView;
	
	private ImageView avatarView;
	private EditText firstNameText;
	private EditText lastNameText;
	private EditText birthdayText;
	private EditText zipCodeText;
	private EditText emailText;
	private EditText passwordText;
	private EditText confirmPasswordText;
	
	private Button signupBtn;
	
	private String firstName, lastName, birthday, zipCode, email, password, confirmPassword;
	private String avartarPath;	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_signup);
	    
	    loadUIElements();
	}
	
	private void loadUIElements()
	{
		scrollView = (RelativeLayout) findViewById(R.id.signupScrollView);
		scrollView.setOnClickListener(this);
		
		avatarView = (ImageView) findViewById(R.id.avatarImageView);
		avatarView.setOnClickListener(this);
		
		firstNameText		= (EditText) findViewById(R.id.firstNameText);
		lastNameText		= (EditText) findViewById(R.id.lastNameText);
		birthdayText		= (EditText) findViewById(R.id.birthdayText);
		zipCodeText			= (EditText) findViewById(R.id.zipCodeText);
		emailText			= (EditText) findViewById(R.id.emailText);
		passwordText		= (EditText) findViewById(R.id.passwordText);
		confirmPasswordText	= (EditText) findViewById(R.id.confirmPasswordText);
		
		signupBtn			= (Button) findViewById(R.id.id_home_loginBtn);
		signupBtn.setOnClickListener(this);
		
		ImageView logoBtn = (ImageView) findViewById(R.id.logo);
		logoBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				onBackPressed();
			}
        });
	}

	@Override
	public void onClick(View v)
	{
		if ( v == scrollView )
		{
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(firstNameText.getWindowToken(), 0);
		}
		
		if ( v == avatarView )
		{
			onAvartarViewClicked();
		}
		
		if ( v == signupBtn )
		{
			onSignupBtnClicked();
		}
	}
	
	private void onAvartarViewClicked()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
		alertDialogBuilder.setTitle("Capture");
		alertDialogBuilder.setPositiveButton("From Camera",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, Global.CAMERA_REQUEST);
			}
		})
		.setNegativeButton("From Gallery",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, Global.RESULT_LOAD_IMAGE);
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	private void onSignupBtnClicked()
	{
		Log.v("info", "Starting signup...");
		
		firstName = firstNameText.getText().toString();
		lastName = lastNameText.getText().toString();
		birthday = birthdayText.getText().toString();
		zipCode = zipCodeText.getText().toString();
		email = emailText.getText().toString();
		password = passwordText.getText().toString();
		confirmPassword = confirmPasswordText.getText().toString();
		
		if ( firstName.equals("") )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
			builder.setTitle("Crowdfinder Signup");
			builder.setMessage("Enter your first name.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		if ( lastName.equals("") )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
			builder.setTitle("Crowdfinder Signup");
			builder.setMessage("Enter your last name.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		if ( birthday.equals("") )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
			builder.setTitle("Crowdfinder Signup");
			builder.setMessage("Enter Enter birthday.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		if ( zipCode.equals("") )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
			builder.setTitle("Crowdfinder Signup");
			builder.setMessage("Enter Zipcode.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		if ( email.equals("") )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
			builder.setTitle("Crowdfinder Signup");
			builder.setMessage("Enter your email address.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		if ( password.equals("") )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
			builder.setTitle("Crowdfinder Signup");
			builder.setMessage("Enter password.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		if ( confirmPassword.equals("") )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
			builder.setTitle("Crowdfinder Signup");
			builder.setMessage("Enter confirm password.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		if ( !password.equals(confirmPassword) )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
			builder.setTitle("Crowdfinder Signup");
			builder.setMessage("Password does not match.");
			builder.setPositiveButton("OK", null);
			builder.show();
			return;
		}
		
		new SendReqSignup().execute();
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Bitmap avartar = null;
		if (requestCode == Global.CAMERA_REQUEST && resultCode != RESULT_CANCELED && data != null)
		{
			avartar = (Bitmap) data.getExtras().get("data");
			avartar = Bitmap.createScaledBitmap(avartar, 120, 120, false);
			avatarView.setImageDrawable(new BitmapDrawable(getResources(), avartar));
		}
		   
		if (requestCode == Global.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data)
		{
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			avartarPath = cursor.getString(columnIndex);
			cursor.close();
			
			File imgFile = new  File(avartarPath);
			if(imgFile.exists())
			{
				avartar = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				avartar = Bitmap.createScaledBitmap(avartar, 120, 120, false);
			    avatarView.setImageDrawable(new BitmapDrawable(getResources(), avartar));
			}
		}
		
		if ( null != avartar )
		{
			try
			{
				String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
				avartarPath = basePath + "/avartar.png";
				
				File file = new File(avartarPath);
	        	FileOutputStream fout = new FileOutputStream(file);
	        	avartar.compress(Bitmap.CompressFormat.PNG, 85, fout);
	        	fout.flush();
	        	fout.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	class SendReqSignup extends AsyncTask<String, Void, Void> {
		String sResponse = "";
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SignUpActivity.this,"Please wait...", "Logging in...", true);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String url = Global.SIGNUP_URL;
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				MultipartEntity multientity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
						         
		        multientity.addPart("firstname", new StringBody(firstName));
		        multientity.addPart("lastname", new StringBody(lastName));
		        multientity.addPart("birthday", new StringBody(birthday));
		        multientity.addPart("zipcode", new StringBody(zipCode));
		        multientity.addPart("email", new StringBody(email));
		        multientity.addPart("password", new StringBody(password));
		        
		        if ( null != avartarPath )
		        {
		        	multientity.addPart("photo", new FileBody(new File(avartarPath)));
		        }
		        
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
					Toast.makeText(getApplicationContext(), "Signup has been successed.", Toast.LENGTH_LONG).show();
					
					JSONObject userDetail = json.getJSONObject("value");
					
					Account account = Account.getInstance();
					if ( account.fillWithJSONObject(userDetail) )
					{
						
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Already Existing email in Crowdfinder.", Toast.LENGTH_LONG).show();
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
