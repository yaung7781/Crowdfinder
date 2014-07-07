package com.matt.crowdfinder.engine;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Account
{
	private static Account instance = null;
	
	private static String indexKey			= "id";
	private static String firstNameKey		= "firstname";
	private static String lastNameKey		= "lastname";
	private static String emailKey			= "email";
	private static String birthdayKey		= "birthday";
	private static String zipCodeKey		= "zipcode";
	private static String passwordKey		= "password";
	private static String avatarLinkKey		= "avatar";
	private static String facebookIdKey		= "fbid";
	
	public String userIndex = "";
	public String email = "";
	public String password = "";
	public String firstName = "";
	public String lastName = "";
	public String birthday = "";
	public String zipCode = "";
	public String avatarLink = "";
	public String facebookId = "";
	
	private Account() {}
	
	public static synchronized Account getInstance()
	{
		if (instance == null)
		{
			instance = new Account();
		}
		
		return instance;
	}
	
	public boolean fillWithJSONObject(JSONObject object)
	{
		boolean bResult = false;
		
		try 
		{
			this.userIndex		= object.getString(indexKey);
			this.firstName	= object.getString(firstNameKey);
			this.lastName	= object.getString(lastNameKey);
			this.email		= object.getString(emailKey);
			this.birthday	= object.getString(birthdayKey);
			this.zipCode		= object.getString(zipCodeKey);
			this.password	= object.getString(passwordKey);
			this.avatarLink	= object.getString(avatarLinkKey);
			this.facebookId	= object.getString(facebookIdKey);
			
			Log.v("info", "Account(" + userIndex + ") has been filled successfully.");
			
			bResult = true;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			
			this.userIndex		= "";
			this.firstName	= "";
			this.lastName	= "";
			this.email		= "";
			this.birthday	= "";
			this.zipCode		= "";
			this.password	= "";
			this.avatarLink	= "";
			this.facebookId	= "";
			
			Log.v("info", "Fail fill");
			
			bResult = false;
		}
		
		return bResult;
	}
}
