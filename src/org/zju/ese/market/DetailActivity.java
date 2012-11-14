package org.zju.ese.market;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import org.zju.ese.util.Md5Util;

import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DetailActivity extends Activity {
	private static final String DL_ID = "downloadId"; 
	TextView nameText;
	TextView detailText;
	EditText keyEditText;
	Button button;
	String baseUrl;
	String file;
	String key;
	String keyMd5;
	private SharedPreferences mPerferences;
	private DownloadManager dm; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        Bundle bundle = getIntent().getExtras();
        dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE); 
        
        mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		key = mPerferences.getString("pref_apps_key", "");
		
		
        nameText = (TextView)this.findViewById(R.id.nameText);
        detailText = (TextView)this.findViewById(R.id.detailText);
        button = (Button)this.findViewById(R.id.button);
        keyEditText = (EditText)this.findViewById(R.id.keyEditText);
        
        nameText.setText(bundle.getString("name"));
        detailText.setText(bundle.getString("description"));
        file = bundle.getString("file");
        baseUrl = bundle.getString("baseUrl");
        
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long time = (new Date()).getTime();
				keyMd5 = Md5Util.getMd5(keyEditText.getText().toString());
				String skey = Md5Util.getMd5(keyMd5+time);
				String surl = baseUrl+ "apps?file=" + file + "&time=" + time + "&key=" + skey;
				//Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(surl));  
		        //it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");  
				//DetailActivity.this.startActivity(it);  
				
				Uri resource = Uri.parse(surl); 
		        DownloadManager.Request request = new DownloadManager.Request(resource); 
		        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE |  
		            Request.NETWORK_WIFI); 
		        request.setAllowedOverRoaming(false); 
		        //在通知栏中显示 
		        request.setTitle("Downloading"); 
		        long id = dm.enqueue(request); 
		        //保存id 
		        mPerferences.edit().putLong(DL_ID, id).commit(); 
		        registerReceiver(receiver, 
		                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); 
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail, menu);
        return true;
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 
            queryDownloadStatus(); 
        } 
    }; 

    private void queryDownloadStatus() { 
        DownloadManager.Query query = new DownloadManager.Query(); 
        query.setFilterById(mPerferences.getLong(DL_ID, 0)); 
        Cursor c = dm.query(query); 
        if(c.moveToFirst()) { 
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)); 
            switch(status) { 
            case DownloadManager.STATUS_PAUSED: 
            case DownloadManager.STATUS_PENDING: 
            case DownloadManager.STATUS_RUNNING: 
                //正在下载，不做任何事情
                break; 
            case DownloadManager.STATUS_SUCCESSFUL: 
                //完成，显示图片
                try { 
                	ParcelFileDescriptor fileDescriptor =  
                            dm.openDownloadedFile(mPerferences.getLong(DL_ID, 0)); 
                        FileInputStream fis =  
                            new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor); 
                    File f = new File("/sdcard/download/"+file);
                    FileOutputStream fos = new FileOutputStream(f);
                    
                    byte[] buff = new byte[2048];
        			int bytesRead;   
        			while (-1 != (bytesRead = fis.read(buff, 0, buff.length))) {  
        				fos.write(buff, 0, bytesRead);   
        				} 
        			fos.close();
        			fis.close();
                
                    Intent intent = new Intent();   
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
                    intent.setAction(android.content.Intent.ACTION_VIEW); 
                    //final File fileLocal = new File("/sdcard/download/" + file);  
                    intent.setDataAndType(Uri.fromFile(f),   
                                    "application/vnd.android.package-archive");   
                    startActivity(intent); 
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } 
                break; 
            case DownloadManager.STATUS_FAILED: 
                //清除已下载的内容，重新下载
                dm.remove(mPerferences.getLong(DL_ID, 0)); 
                mPerferences.edit().clear().commit(); 
                break; 
            }
        } 
    }
}
