package com.matt.crowdfinder.engine;

import org.json.JSONException;
import org.json.JSONObject;

public class SpecialInfo {

	public String	name;
	public String	offer;
    
	public SpecialInfo(JSONObject info)
	{
		try 
		{
			name		= info.getString("specialname");
	        offer		= info.getString("specialoffer");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
