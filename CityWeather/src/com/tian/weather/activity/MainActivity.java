package com.tian.weather.activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.tian.weather.R;
import com.tian.weather.city.CityAdapter;
import com.tian.weather.city.CityBean;
import com.tian.weather.city.CityQuery;
import com.tian.weather.fragment.WeatherFragment;
import com.tian.weather.fragment.AboutFragment;
import com.tian.weather.fragment.CityListFragment;
import com.tian.weather.slidingtab.PagerSlidingTabStrip;
import com.tian.weather.utils.Location;

/**
 * 
 */
public class MainActivity extends FragmentActivity {

	public static final String TAG = "MainActivity";

	/**
	 * 天气界面的Fragment
	 */
	private WeatherFragment weatherFragment;

	/**
	 * city界面的Fragment
	 */
	private CityListFragment cityListFragment;

	/**
	 * about 界面的Fragment
	 */
	private AboutFragment aboutFragment;

	/**
	 * PagerSlidingTabStrip的实例
	 */
	private PagerSlidingTabStrip tabs;
	/**
	 * ViewPager
	 */
	private ViewPager pager;
	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;

	private long cityCode;
    private Location location;
	private ListView mSearchListView;
	private boolean isSelectSearch = false;
	private CityBean mCityBySearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setOverflowShowingAlways();
		location = new Location(getApplicationContext());
		location.locationAddress();
		dm = getResources().getDisplayMetrics();
		pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		tabs.setViewPager(pager);
		setTabsValue();
		mSearchListView = (ListView) findViewById(R.id.search_listview);
		mSearchListView.setVisibility(View.GONE);
		Intent intent = getIntent();
		if (intent != null) {
			cityCode = intent.getLongExtra(HotCityActivity.CITY_CODE, 0);
			if(cityCode==0){
				try {
					String name = location.getCity();
					cityCode = CityQuery.getCityBeanByGridView(MainActivity.this,
							name.split("市")[0]).cityCode;
				} catch (Exception e) {
					//默认设置成南京，防止崩溃
					cityCode = 1944;
				}
			}
			 
		}
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#45c01a"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
		//
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "天气", "城市列表" ,"关于"};

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (weatherFragment == null) {
					weatherFragment = new WeatherFragment(MainActivity.this, cityCode);
				}
				return weatherFragment;
			case 1:
				if (cityListFragment == null) {
					cityListFragment = new CityListFragment(MainActivity.this,weatherFragment,pager);
				}
				return cityListFragment;
			case 2:
				if (aboutFragment == null) {
					aboutFragment = new AboutFragment(MainActivity.this);
				}
				return aboutFragment;
			default:
				return null;
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			final SearchView searchView = (SearchView) menu.findItem(
					R.id.action_search).getActionView();
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
								.getCityListBySearch(MainActivity.this,
										searchText);
						if (list != null && list.size() > 0) {
							mSearchListView.setVisibility(View.VISIBLE);
							pager.setVisibility(View.GONE);
							tabs.setVisibility(View.GONE);
							final CityAdapter cityAdapter = new CityAdapter(
									MainActivity.this, list);
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
											pager.setVisibility(View.VISIBLE);
											tabs.setVisibility(View.VISIBLE);
											searchView.setQuery(
													mCityBySearch.cityName,
													true);
											cityCode = mCityBySearch.cityCode;
											if (!weatherFragment
													.showCityWeather(mCityBySearch.cityCode)) {
												weatherFragment
														.startToRefreshWeather(mCityBySearch.cityCode);
											}
											pager.setCurrentItem(0);
										}
									});
						} else {
							isSelectSearch = false;
							pager.setVisibility(View.VISIBLE);
							tabs.setVisibility(View.VISIBLE);
							mSearchListView.setVisibility(View.GONE);
						}

					} else {
						isSelectSearch = false;
						pager.setVisibility(View.VISIBLE);
						tabs.setVisibility(View.VISIBLE);
						mSearchListView.setVisibility(View.GONE);
					}
					return false;
				}
			});
		}
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			onSearchRequested();
			return true;
		case R.id.re_get_city_button:
			weatherFragment.startToGetCity();
			return true;
		case R.id.set_default_city:
			Intent it = new Intent();
			it.setClass(this, HotCityActivity.class);
			startActivity(it);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		case R.id.action_location:
			try {
				String name = location.getCity();
				cityCode = CityQuery.getCityBeanByGridView(MainActivity.this,
						name.split("市")[0]).cityCode;
				if (!weatherFragment
						.showCityWeather(cityCode)) {
					weatherFragment
							.startToRefreshWeather(cityCode);
				}
				pager.setCurrentItem(0);
			} catch (Exception e) {
				Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.KEYCODE_HOME){
        	onBackPressed();
        }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
   
}