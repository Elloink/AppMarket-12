package org.zju.ese.market;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.zju.ese.model.AppItem;
import org.zju.ese.model.Picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureAdapter extends BaseAdapter implements Runnable {
	private LayoutInflater inflater; 
    private AppItem[] appList;
    private Bitmap[] iconBitmaps;
    private Thread iconThread;
    private String baseUrl;
    public PictureAdapter(AppItem[] appList, Context context,String baseUrl) 
    { 
        super(); 
        inflater = LayoutInflater.from(context); 
        this.appList = appList;
        this.baseUrl = baseUrl;
        iconBitmaps = new Bitmap[appList.length];
        iconThread = new Thread(this);
        iconThread.start();
    } 
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null != appList) 
        { 
            return appList.length; 
        } else
        { 
            return 0; 
        } 
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return appList[position]; 
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position; 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		try {
			iconThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ViewHolder viewHolder; 
        if (convertView == null) 
        { 
            convertView = inflater.inflate(R.layout.grid_item, null); 
            viewHolder = new ViewHolder(); 
            viewHolder.title = (TextView) convertView.findViewById(R.id.title); 
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image); 
            convertView.setTag(viewHolder); 
        } else
        { 
            viewHolder = (ViewHolder) convertView.getTag(); 
        } 
        viewHolder.title.setText(appList[position].getName()); 
        //Bitmap bitmap = getHttpBitmap(appList[position].getIcon());
        viewHolder.image.setImageBitmap(iconBitmaps[position]); 
        return convertView; 
	}
	
	public static Bitmap getHttpBitmap(String url){
		URL myFileURL;
		Bitmap bitmap=null;
		try{
			myFileURL = new URL(url);
			//获得连接
			HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
			//设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			//连接设置获得数据流
			conn.setDoInput(true);
			//不使用缓存
			conn.setUseCaches(false);
			//这句可有可无，没有影响
			//conn.connect();
			//得到数据流
			InputStream is = conn.getInputStream();
			//解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			//关闭数据流
			is.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		return bitmap;

	}

	class ViewHolder 
	{ 
	    public TextView title; 
	    public ImageView image; 
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i=0;i<iconBitmaps.length;i++)
		{
			iconBitmaps[i] = getHttpBitmap(baseUrl + "icon/" + appList[i].getIcon());
		}
	}

}
