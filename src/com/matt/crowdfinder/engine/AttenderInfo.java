package com.matt.crowdfinder.engine;

import org.json.JSONException;
import org.json.JSONObject;

public class AttenderInfo {

	public String		index;
	public String		firstName;
	public String		lastName;
	public String		birthday;
	public String		zipcode;
	public String		email;
	public String		password;
	public String		avatar;
	public String		fbId;
    
	public AttenderInfo(JSONObject info)
	{
		try 
		{
	        index			= info.getString("id");
	        firstName        = info.getString("firstname");
	        lastName         = info.getString("lastname");
	        email            = info.getString("email");
	        birthday         = info.getString("birthday");
	        zipcode          = info.getString("zipcode");
	        password         = info.getString("password");
	        avatar           = info.getString("avatar");
	        fbId             = info.getString("fbid");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
