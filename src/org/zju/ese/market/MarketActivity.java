package org.zju.ese.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.zju.ese.model.AppItem;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class MarketActivity extends Activity {
	public static final int RESULT_PREF_UPDATE = 1;
	
	AppItem[] appList;
	private GridView gridView;
	private String baseUrl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        initPreference();
        if(baseUrl != null)
        {
	        MyThread thread = new MyThread();
	        thread.start();
	        try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        gridView = (GridView) findViewById(R.id.gridview); 
	        PictureAdapter adapter = new PictureAdapter(appList, this,baseUrl); 
	        gridView.setAdapter(adapter); 
	        gridView.setOnItemClickListener(new OnItemClickListener() 
	        { 
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
	            { 
	            	AppItem item = appList[position];
	            	Intent intent1 = new Intent(MarketActivity.this, DetailActivity.class);
	            	intent1.putExtra("name", item.getName());
	            	intent1.putExtra("description", item.getDescription());
	            	intent1.putExtra("file", item.getUrl());
	            	intent1.putExtra("baseUrl", baseUrl);
	            	startActivity(intent1);
	            } 
	        }); 
        }
        //ListAdapter adapter = new ArrayAdapter<AppItem>(this,android.R.layout.simple_list_item_1,appList);
        //setListAdapter(adapter);
    }
    
    private void refreshData()
    {
    	MyThread thread = new MyThread();
        thread.start();
        try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void initPreference()
	{
		 SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		 String address = mPerferences.getString("pref_server_address", "");
		 String port = mPerferences.getString("pref_server_port", "");
		 //Toast.makeText(this,"address:"+ address, Toast.LENGTH_LONG);
		 if(address.equals(""))
		 {
			 Intent intent = new Intent(this, MarketPreferenceActivity.class);
			 startActivityForResult(intent,RESULT_PREF_UPDATE);
		 }
		 else
		 {
			 baseUrl = "http://" + address + ":" + port + "/partime/";
		 }
	}
    
    private void updateServer()
    {
    	 SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		 String address = mPerferences.getString("pref_server_address", "");
		 String port = mPerferences.getString("pref_server_port", "");
		 baseUrl = "http://" + address + ":" + port+ "/partime/";
    }
    
    @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {     
	        switch (requestCode) {  
	        case RESULT_PREF_UPDATE:
	   		 	updateServer();
	        	break;
	        }
	 }
    
    class MyThread extends Thread
    {
    	//Handler handler;
    	public MyThread()
    	{
    		//handler = mHandler;
    	}
    	public void run()
    	{
    		ObjectMapper mapper = new ObjectMapper();
    		
    		MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
    		messageConverter.setObjectMapper(mapper);
    		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
    		messageConverters.add(messageConverter);
    		
    		HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
			// Create a new RestTemplate instance
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.setMessageConverters(messageConverters);
			// Add the Jackson message converter
			restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
			try{
				appList = restTemplate.getForObject(baseUrl+"/getapps", AppItem[].class);
			}
			catch(Exception e)
			{
				
			}
//			Message message = handler.obtainMessage();
//			message.obj = list[0];
//			handler.sendMessage(message);
    	}
    	
    }
    
//    @Override   
//    protected void onListItemClick(ListView l, 
//    View v, int position, long id) 
//    {
//    	AppItem item = appList[position];
//    	Intent intent1 = new Intent(this, DetailActivity.class);
//    	intent1.putExtra("name", item.getName());
//    	intent1.putExtra("description", item.getDescription());
//    	intent1.putExtra("url", item.getUrl());
//    	startActivity(intent1);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_market, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                //NavUtils.navigateUpFromSameTask(this);
            	Intent intent = new Intent(this, MarketPreferenceActivity.class);
            	startActivityForResult(intent,RESULT_PREF_UPDATE);
                return true;
            case R.id.menu_refresh:
            	refreshData();
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
