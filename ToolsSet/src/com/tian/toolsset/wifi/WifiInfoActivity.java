package com.tian.toolsset.wifi;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tian.toolsset.R;
import com.tian.toolsset.ToolsActivity;


public class WifiInfoActivity extends Activity implements OnClickListener {
	
	private WifiManage wifiManage;
    private List<WifiInfo> wifiInfos;;
    private ProgressDialog progress_dialog ;
    private ListView wifiInfosView;
    private TextView title;
    private ImageView ivback;
    private WifiAdapter ad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wifi_info);
		wifiManage = new WifiManage();
		progress_dialog = new ProgressDialog(WifiInfoActivity.this);
		try {
			Init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Init() throws Exception {
	    title = (TextView)findViewById(R.id.wifi_title);
		wifiInfosView =(ListView)findViewById(R.id.WifiInfosView);
		ivback = (ImageView)findViewById(R.id.iv_back);
		ivback.setOnClickListener(this);
		new MyTask().execute();
		if(wifiInfos.isEmpty()){
			title.setText(getResources().getString(R.string.title_of_wifi_null).toString());
			return;
		}else{
			title.setText(getResources().getString(R.string.title_of_wifi).toString());
		}
		ad = new WifiAdapter(wifiInfos,WifiInfoActivity.this);
		wifiInfosView.setAdapter(ad);	
	}
	
	public class WifiAdapter extends BaseAdapter{

		List<WifiInfo> wifiInfos =null;
		Context con;
		
		public WifiAdapter(List<WifiInfo> wifiInfos,Context con){
			this.wifiInfos =wifiInfos;
			this.con = con;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wifiInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return wifiInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(con).inflate(R.layout.wifi_item, null);
			TextView tv = (TextView)convertView.findViewById(R.id.item_text);
			tv.setText("WiFi名:  "+wifiInfos.get(position).Ssid+"\n密码:  "+wifiInfos.get(position).Password);
			return convertView;
		}
	}
    
	class MyTask extends AsyncTask<Void, Void, List<WifiInfo>>{

		@Override
		protected void onPostExecute(List<WifiInfo> result) {
			if(result!=null){
				progress_dialog.dismiss();
				if(ad!=null){
					ad.notifyDataSetChanged();
				}else{
					ad = new WifiAdapter(wifiInfos,WifiInfoActivity.this);
					wifiInfosView.setAdapter(ad);	
				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress_dialog.setMessage("正在读取WiFi信息");
			progress_dialog.setCancelable(true);
			progress_dialog.show();
			super.onPreExecute();
		}

		@Override
		protected List<WifiInfo> doInBackground(Void... params) {
			try {
				wifiInfos = wifiManage.Read();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return wifiInfos;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.iv_back:
			onBackPressed();
			
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent it = new Intent();
		it.setClass(WifiInfoActivity.this, ToolsActivity.class);
		startActivity(it);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		super.onBackPressed();
	}
	
}