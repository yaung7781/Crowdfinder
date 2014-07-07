package com.matt.crowdfinder;

import java.util.ArrayList;

import com.matt.crowdfinder.engine.BusiListAdapter;
import com.matt.crowdfinder.engine.BusinessInfo;
import com.matt.crowdfinder.engine.CategoryInfo;
import com.matt.crowdfinder.engine.Global;
import com.matt.crowdfinder.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BusiListActivity extends Activity implements OnClickListener
{
	public static String keyword = null;
	public static CategoryInfo categoryInfo = null;
	private ArrayList<BusinessInfo> aryBusiness;
	private BusiListAdapter busilistAdapter;
	
	ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_busi_list);
	    
	    loadUIElements();
	}
	
	private void loadUIElements() {
		
		listView = (ListView) findViewById(R.id.id_busilist_list);
	    
	    ArrayList<BusinessInfo> aryTempBusi = null;
	    if ( categoryInfo != null )
	    {
	    	aryTempBusi = Global.getBusinessByCategory(categoryInfo);
	    }
	    else
	    {
	    	aryTempBusi = Global.aryBusiness;
	    }
	    
	    aryBusiness = Global.filterBusinessByKey(aryTempBusi, keyword);
	    
	    busilistAdapter = new BusiListAdapter(BusiListActivity.this, aryBusiness);
	    listView.setAdapter(busilistAdapter);
	    
	    listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
				BusinessInfo value = (BusinessInfo) aryBusiness.get(position);
				
				BusiDetailActivity.busiInfo = value;
				Intent goToNextActivity = new Intent(getApplicationContext(), BusiDetailActivity.class);
				startActivity(goToNextActivity);
            }
		});
	    
	    ImageView logo = (ImageView) findViewById(R.id.id_busilist_logo);
		logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				onBackPressed();
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
				
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra(MainActivity.EXIT_CODE, MainActivity.GOTO_RECOMMEND);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
        });
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		busilistAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
