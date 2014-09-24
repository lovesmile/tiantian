package com.tian.weather.city;

import java.util.ArrayList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tian.weather.R;

public class CityAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<CityBean> mCityList;
	private LayoutInflater inflater;
	
	public CityAdapter(Context context, ArrayList<CityBean> cityList){
		this.mContext = context;
		this.mCityList = cityList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCityList.size();
	}

	@Override
	public CityBean getItem(int position) {
		// TODO Auto-generated method stub
		return mCityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null){ 
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.search_result_list_item, null); 
			holder.cityName = (TextView)convertView.findViewById(R.id.item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.cityName.setText(mCityList.get(position).cityName);
		return convertView;
	}
	
	public int getId(long cityId){
		for(int i = 0; i < mCityList.size(); i++){
			if(mCityList.get(i).cityCode == cityId){
				
				return i;
				
			}
		}
		return 0;
	}
	
	public static class ViewHolder{
		TextView cityName;
	}

}
