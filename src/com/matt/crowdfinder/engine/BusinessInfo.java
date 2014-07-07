package com.matt.crowdfinder.engine;

import org.json.JSONException;
import org.json.JSONObject;

public class BusinessInfo
{
	public String		index;
	public String		name;
	public String		description;
	public float		latitude;
	public float		longitude;
	public boolean		isVideo;
	public String		category;
	public String		video;
	public String		address;
	public float		rate;
	public String		openHour;
	public String		contact;
	public String		photo0;
	public String		photo1;
	public String		photo2;
	public String		fbURL;
	public String		twitterURL;
	public String		otherUrl;
	
	public BusinessInfo(JSONObject info)
	{
		try  {
			
			index           = info.getString("id");
	        name         = info.getString("name");
	        description  = info.getString("description");
	        latitude     = Float.parseFloat(info.getString("latitude"));
	        longitude    = Float.parseFloat(info.getString("longitude"));
	        isVideo      = Integer.parseInt(info.getString("partnership")) == 1 ? true : false;
	        category     = info.getString("categoryid");
	        video        = info.getString("video");
	        address      = info.getString("address");
	        rate         = Float.parseFloat((info.getString("rate")));
	        openHour     = info.getString("businesshour");
	        contact      = info.getString("contact");
	        
	        photo0		= (info.getString("photo1") != null && info.getString("photo1").length() > 0 ) ? Global.SERVER_URL + info.getString("photo1") : null;
	        photo1		= (info.getString("photo2") != null && info.getString("photo2").length() > 0 ) ? Global.SERVER_URL + info.getString("photo2") : null;
	        photo2		= (info.getString("photo3") != null && info.getString("photo3").length() > 0 ) ? Global.SERVER_URL + info.getString("photo3") : null;
	        
	        fbURL		= info.getString("fb_url");
	        twitterURL	= info.getString("tw_url");
	        otherUrl	= info.getString("link_url");
	        
		} catch (JSONException e) {
			e.printStackTrace();
			
			index           = "";
	        name         = "";
	        description  = "";
	        latitude     = 0;
	        longitude    = 0;
	        isVideo      = false;
	        category     = "";
	        video        = "";
	        address      = "";
	        rate         = 0;
	        openHour     = "";
	        contact      = "";
	        photo0       = "";
	        photo1       = ""; 
	        photo2		= "";
	        fbURL		= "";
	        twitterURL	= "";
	        otherUrl	= "";
		}
	}
	
	public boolean hasVideo()
	{
		if ( !isVideo )
			return false;
		
		if ( video.equals("") )
			return false;
		
		return true;
	}
}