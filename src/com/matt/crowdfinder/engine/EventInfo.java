package com.matt.crowdfinder.engine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class EventInfo {

	public String		index;
	public String		busiIndex;
	public String		busiName;
	public String		busiHour;
	public String		name;
	public String		description;
	public String		cate;
	public String		date;
	public String		address;
	public float		latitude;
	public float		longitude;
	public String		photoLink1;
	public String		photoLink2;
	public String		photoLink3;
    
	public EventInfo(JSONObject info)
	{
		try 
		{
	        index			= info.getString("id");
            busiIndex		= info.getString("businessid");
            busiName		= info.getString("busi_name");
            busiHour		= info.getString("busi_hour");
            name			= info.getString("eventname");
            description		= info.getString("eventdesc");
            cate			= info.getString("eventcategory");
            date			= info.getString("eventdate");
            address			= info.getString("address");
            latitude		= Float.parseFloat(info.getString("latitude"));
            longitude		= Float.parseFloat(info.getString("longitude"));
            photoLink1		= info.getString("eventphoto1");
            photoLink2		= info.getString("eventphoto2");
            photoLink3		= info.getString("eventphoto3");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isExpired() {
		
		if ( date == null )
			return true;
		
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		try {  
		    Date eventDate = format.parse(date);
		    Date currentDate = Calendar.getInstance().getTime();
		    
		    if ( currentDate.getTime() - eventDate.getTime() > 30 * 3600 )
				return true;
		    
		} catch (ParseException e) {  
		    // TODO Auto-generated catch block  
		    e.printStackTrace();  
		}
		
		return false;
	}
}
