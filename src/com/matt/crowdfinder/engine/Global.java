package com.matt.crowdfinder.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class Global
{
	public static boolean isDebugging = false;
	
	public static String facebookAppId = "437311406394656";
	
	public static String LOGIN_URL			= "http://www.crowdfinder.us/api/login.php";
	public static String SIGNUP_URL			= "http://www.crowdfinder.us/api/signup.php";
	public static String GET_CATEGORY_URL	= "http://www.crowdfinder.us/api/getCategory.php";
	public static String GET_BUSINESS_URL	= "http://www.crowdfinder.us/api/getBusiness.php";
	public static String SERVER_URL			= "http://www.crowdfinder.us/admin/";
	public static String IS_FAVOURITE_URL	= "http://www.crowdfinder.us/api/isFavourite.php";
	public static String ADD_FAVOURITE_URL	= "http://www.crowdfinder.us/api/newFavourite.php";
	public static String ATTEND_URL			= "http://www.crowdfinder.us/api/attend.php";
	public static String GET_SPECIAL_URL	= "http://www.crowdfinder.us/api/getSpecial.php";
	public static String GET_EVENTS_URL		= "http://www.crowdfinder.us/api/getEvent.php";
	public static String CHANGE_RATE_URL	= "http://www.crowdfinder.us/api/rating.php";
	public static String GET_FAVOUR_BUSINESS_URL	= "http://www.crowdfinder.us/api/getFavourBusi.php";
	public static String ACCOUNT_MODIFY_API_URL		= "http://www.crowdfinder.us/api/accountModification.php";
	public static String RECOMMEND_API_URL = "http://www.crowdfinder.us/api/recommend.php";
	public static String DELETE_FAVOUR_BUSINESS_URL		= "http://www.crowdfinder.us/api/deleteFavourite.php";
	
	public static final int CAMERA_REQUEST = 1888;
	public static final int RESULT_LOAD_IMAGE = 1889;
	
	public static ArrayList<BusinessInfo> aryBusiness;
	public static ArrayList<BusinessInfo> aryFavorite;
	public static ArrayList<CategoryInfo> aryCategory;
	public static ArrayList<SpecialInfo> arySpecialInfo;
	public static ArrayList<EventInfo> aryEventInfo;
	public static ArrayList<AttenderInfo> aryAttender;
	
	public static Location currentLocation;
	
	public static Intent mainActivity;
	
	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		
		return isValid;
	}
	
	public static ArrayList<BusinessInfo> getBusinessByCategory(CategoryInfo categoryInfo)
	{
		ArrayList<BusinessInfo> aryBusi = new ArrayList<BusinessInfo>();
    	for ( int i = 0; i < Global.aryBusiness.size(); i++ )
    	{
    		BusinessInfo busiInfo = (BusinessInfo) Global.aryBusiness.get(i);
    		if ( busiInfo.category.toLowerCase().contains(categoryInfo.id.toLowerCase()) )
    		{
    			aryBusi.add(busiInfo);
    		}
    	}
    	
    	return aryBusi;
	}
	
	public static ArrayList<BusinessInfo> filterBusinessByKey(ArrayList<BusinessInfo> arySrc, String key) {
		
		if ( key == null )
			return arySrc;
		
		key = key.toLowerCase();
		ArrayList<BusinessInfo> aryResult = new ArrayList<BusinessInfo>();
		for ( int i = 0; i < arySrc.size(); i++ ) {
			BusinessInfo busiInfo = arySrc.get(i);
			
			String name = busiInfo.name.toLowerCase();
			if ( name.contains(key) ) {
				aryResult.add(busiInfo);
				continue;
			}
			
			for ( int j = 0; j < aryEventInfo.size(); j++ ) {
				EventInfo eventInfo = aryEventInfo.get(j);
				
				if ( eventInfo.busiIndex != busiInfo.index )
					continue;
				
				String eventName = eventInfo.name.toLowerCase();
				if ( eventName.contains(key) ) {
					aryResult.add(busiInfo);
					break;
				}
			}
		}
		
		return aryResult;
	}
	
	public static boolean isViewContains(View view, int rx, int ry) {
	    int[] l = new int[2];
	    view.getLocationOnScreen(l);
	    int x = l[0];
	    int y = l[1];
	    int w = view.getWidth();
	    int h = view.getHeight();

	    if (rx < x || rx > x + w || ry < y || ry > y + h) {
	        return false;
	    }
	    return true;
	}
	
	public static class DownloadPhotoForImageViewTask extends AsyncTask<String, Void, Bitmap>
	{
		ImageView imageView;
		public DownloadPhotoForImageViewTask(ImageView bmImage)
		{
	        this.imageView = bmImage;
	    }
		
		@Override
		protected Bitmap doInBackground(String... params)
		{
			String imageUrl = params[0];
			Bitmap bitmap = null;
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				bitmap = BitmapFactory.decodeStream(input);
				
			} catch (IOException e) {
				e.printStackTrace();
				bitmap = null;
			}
			
			return bitmap;
		}
		
		@Override 
	    protected void onPostExecute(Bitmap result)
		{
	        super.onPostExecute(result);
	        imageView.setImageBitmap(result);
	    }
	}
}