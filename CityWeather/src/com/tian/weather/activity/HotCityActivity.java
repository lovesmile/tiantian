package com.tian.weather.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.tian.weather.R;
import com.tian.weather.adapter.CityGridViewAdapter;
import com.tian.weather.city.CityAdapter;
import com.tian.weather.city.CityBean;
import com.tian.weather.city.CityQuery;
import com.tian.weather.utils.Location;

public class HotCityActivity extends Activity implements OnItemClickListener {
	private boolean isSelectSearch = false;
	private CityBean mCityBySearch;
	private GridView cityGridView;
	private ListView mSearchListView;
	public static final String CITY_CODE = "city_code";
	/*
	 * 定位
	 */
	private Location location = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot_city);
		location = new Location(HotCityActivity.this);
		location.locationAddress();
		cityGridView = (GridView) findViewById(R.id.hotcity_list);
		cityGridView.setVisibility(View.VISIBLE);
		cityGridView.setAdapter(new CityGridViewAdapter(HotCityActivity.this));
		cityGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		cityGridView.setOnItemClickListener(this);
		mSearchListView = (ListView) this.findViewById(R.id.search_listview);
		mSearchListView.setVisibility(View.GONE);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		long cityCode;
		if (position == 0) {
			String name = location.getCity();
			if(name!=null){
				cityCode = CityQuery.getCityBeanByGridView(HotCityActivity.this,
						name.split("市")[0]).cityCode;
			}else{
				Toast.makeText(this, "定位失败，请检查网络是否连接", Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			CityBean city = (CityBean) cityGridView.getAdapter().getItem(
					position);
			cityCode = city.cityCode;
		}
		Intent it = new Intent();
		it.putExtra(CITY_CODE, cityCode);
		it.setClass(HotCityActivity.this, MainActivity.class);
		startActivity(it);
		this.finish();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			final SearchView searchView = (SearchView) menu.findItem(
					R.id.search).getActionView();
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setQueryHint(getResources().getString(
					R.string.search_hint));
			searchView.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String searchText) {
					if (!TextUtils.isEmpty(searchText) && !isSelectSearch) {
						ArrayList<CityBean> list = CityQuery
								.getCityListBySearch(HotCityActivity.this,
										searchText);
						if (list.size() > 0) {
							mSearchListView.setVisibility(View.VISIBLE);
							final CityAdapter cityAdapter = new CityAdapter(
									HotCityActivity.this, list);
							mSearchListView.setAdapter(cityAdapter);
							mSearchListView
									.setOnItemClickListener(new AdapterView.OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parents,
												View view, int position, long id) {
											isSelectSearch = true;
											mCityBySearch = new CityBean();
											mCityBySearch = cityAdapter
													.getItem(position);
											mSearchListView
													.setVisibility(View.GONE);
											searchView.setQuery(
													mCityBySearch.cityName,
													true);
											Intent it = new Intent();
											it.putExtra(CITY_CODE,
													mCityBySearch.cityCode);
											it.setClass(HotCityActivity.this,
													MainActivity.class);
											startActivity(it);
											overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

										}
									});
						} else {
							isSelectSearch = false;
							mSearchListView.setVisibility(View.GONE);
						}

					} else {
						isSelectSearch = false;
						mSearchListView.setVisibility(View.GONE);
					}
					return false;
				}
			});
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}