package com.tian.weather.adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import com.tian.weather.R;
import com.tian.weather.city.CityBean;
import com.tian.weather.city.CityQuery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityGridViewAdapter extends BaseAdapter{
	private List<CityBean> cityBeanList = new ArrayList<CityBean>();
	private String [] cityNames = {"定位","北京","南京","上海","合肥",
			"广州","深圳","武汉","杭州","西安","郑州","成都","东莞",
			"沈阳","天津","哈尔滨","长沙","福州","石家庄","苏州","重庆",
			"无锡","济南","大连","佛山","厦门","南昌","太原","长春",
			"浦东","青岛","汕头","昆明","南宁",
			
	};
	private final WeakReference<Activity> mContext;
	private final LayoutInflater mInflater;
	public CityGridViewAdapter(Activity mContext) {
		super();
		this.mContext = new WeakReference<Activity>(mContext);
		this.mInflater = LayoutInflater.from(mContext);
		CityBean city;
		for(int i=0;i<cityNames.length;i++){
			if(i==0){
				city = new CityBean();
				city.cityName = cityNames[0];
				cityBeanList.add(city);
			}else{
				city = new CityBean();
				city = CityQuery.getCityBeanByGridView(mContext, cityNames[i]);
				cityBeanList.add(city);
			}
			 
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cityBeanList.size();
	}

	@Override
	public CityBean getItem(int position) {
		// TODO Auto-generated method stub
		return cityBeanList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder viewHolder;
		CityBean city = cityBeanList.get(position);
		if(v==null){
			v = mInflater.inflate(R.layout.hotcity_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView)v.findViewById(R.id.hotcity);
			v.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) v.getTag();
		}
		   viewHolder.name.setText(city.cityName);
		return v;
	}

	private static class ViewHolder{
		TextView name;
	}
}
