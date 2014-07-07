package com.matt.crowdfinder.engine;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryInfo
{
	public String id;
	public String name;
	public String description;
	
	public CategoryInfo(JSONObject json)
	{
		try
		{
			id				= json.getString("id");
	        name			= json.getString("name");
	        description		= json.getString("desc");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
