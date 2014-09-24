package com.tian.weather.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.tian.weather.R;
import com.tian.weather.city.CityBean;

public class CityListAdapter extends  BaseExpandableListAdapter{
	private List<String> proviceList = new ArrayList<String>();
    private HashMap<String,List<CityBean>> cityMap = new HashMap<String, List<CityBean>>();
    private Context mContext;
	public CityListAdapter(List<String> proviceList,
			HashMap<String, List<CityBean>> cityMap,Context context) {
		super();
		this.proviceList = proviceList;
		this.cityMap = cityMap;
		this.mContext = context;
	}

	@Override
	public int getGroupCount() {
            if(proviceList!=null){
            	return proviceList.size();
            }else{
            	return 0;
            }
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(cityMap.get(proviceList.get(groupPosition))!=null){
			return cityMap.get(proviceList.get(groupPosition)).size();
		}else{
			return 0;
		}
	}

	@Override
	public String getGroup(int groupPosition) {
		return proviceList.get(groupPosition);
	}

	@Override
	public CityBean getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return cityMap.get(proviceList.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder viewHolder;
		if(v==null){
			v = LayoutInflater.from(mContext).inflate(R.layout.provice_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView)v.findViewById(R.id.item_text);
			v.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) v.getTag();
		}
		String name = getGroup(groupPosition);
		if(name!=null){
			viewHolder.name.setText(name);
		}
		return v;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder viewHolder;
		if(v==null){
			v = LayoutInflater.from(mContext).inflate(R.layout.city_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView)v.findViewById(R.id.item_text);
			v.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) v.getTag();
		}
		String name = getChild(groupPosition, childPosition).cityName;
		if(name!=null){
			viewHolder.name.setText(name);
		}
		return v;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private static class ViewHolder{
		TextView name;
	}
}
