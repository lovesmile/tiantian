package com.tian.weather.city;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tian.weather.R;

public class ProvinceAdapter extends BaseAdapter{
	
	private Context mContext;
	private ArrayList<ProvinceBean> mProList;
	private LayoutInflater inflater;
	
	public ProvinceAdapter(Context context, ArrayList<ProvinceBean> proList){
		this.mContext = context;
		this.mProList = proList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mProList.size();
	}

	@Override
	public ProvinceBean getItem(int position) {
		return mProList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){ 
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.city_item, null); 
			holder.proName = (TextView)convertView.findViewById(R.id.item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.proName.setText(mProList.get(position).provinceName);
		return convertView;
	}
	
	public int getId(long proCode){
		for(int i = 0; i < mProList.size(); i++){
			if(mProList.get(i).provinceCode == proCode){
				
				return i;
				
			}
		}
		return 0;
	}
	
	public static class ViewHolder{
		TextView proName;
	}
	
}