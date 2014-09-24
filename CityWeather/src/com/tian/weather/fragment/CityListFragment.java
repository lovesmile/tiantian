package com.tian.weather.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tian.weather.R;
import com.tian.weather.adapter.CityListAdapter;
import com.tian.weather.city.CityBean;
import com.tian.weather.city.CityQuery;
import com.tian.weather.city.ProvinceBean;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

/**
 * 
 */
public class CityListFragment extends Fragment {
	private Context mcontext;
    private ExpandableListView  listView;
    private List<String> proviceList = new ArrayList<String>();
    private HashMap<String,List<CityBean>> cityMap = new HashMap<String, List<CityBean>>();
    private CityListAdapter adapter;
    private WeatherFragment weatherFragment;
    private ViewPager viewPager;
	public CityListFragment() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CityListFragment(Context context,WeatherFragment fragment,ViewPager pager) {
		super();
		this.mcontext = context;
		this.weatherFragment = fragment;
		this.viewPager = pager;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.city_list, container, false);
		listView = (ExpandableListView)view.findViewById(R.id.city_list);
		ArrayList<ProvinceBean> list = CityQuery.getAllProvinceList(mcontext);
		if(list!=null&&!list.isEmpty()){
			for(ProvinceBean bean:list){
				long code = bean.provinceCode;
				String provice = bean.provinceName;
				List<CityBean> cityList = CityQuery.getCityList(mcontext, code);
				proviceList.add(provice);
				cityMap.put(provice, cityList);
			}
			adapter = new CityListAdapter(proviceList, cityMap,mcontext);
			listView.setAdapter(adapter);
		}
		listView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				CityBean cityBean = cityMap.get(proviceList.get(groupPosition)).get(childPosition);
				long cityCode = cityBean.cityCode;
				if(!weatherFragment.showCityWeather(cityCode)){
					weatherFragment.startToRefreshWeather(cityCode);
				}
				viewPager.setCurrentItem(0);
				return false;
			}
		});
		return view;
	}
}
