package com.tian.weather.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tian.weather.R;
import com.tian.weather.WeatherBean;
import com.tian.weather.WeatherDrawable;
import com.tian.weather.WeatherQuery;
import com.tian.weather.WeatherUtils;
import com.tian.weather.city.CityBean;
import com.tian.weather.city.CityQuery;
import com.tian.weather.city.CityUtils;
import com.tian.weather.city.ProvinceAdapter;
import com.tian.weather.statics.Statics;
import com.tian.weather.utils.EngLog;
import com.tian.weather.utils.Location;
import com.tian.weather.utils.NetworkUtils;
import com.tian.weather.utils.NetworkUtils.NetworkState;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
/**
 * 默认城市天气Fragment的界面 
 * @author Tian
 */
public class WeatherFragment extends Fragment implements
PullToRefreshAttacher.OnRefreshListener {
	private static final String TAG = "本地天气";
	private ProgressDialog progress_dialog;

	private String mDownload;
	private String mProvinceName;

	private Spinner mProvinceSpinner;
	private ProvinceAdapter mProAdapter;
	private CityUtils mCityUtils;
	private TextView mCityNameView;
	private TextView mTodayWeatherView;
	private TextView mTodayContentView;
	private TextView mAfterOneWeather;
	private TextView mAfterTwoWeather;
	private TextView mAfterThreeWeather;
	private TextView mAfterFourWeather;
	private ImageView mTodayImg1;
	private ImageView mTodayImg2;
	private ImageView mAfterOneImg1;
	private ImageView mAfterOneImg2;
	private ImageView mAfterTwoImg1;
	private ImageView mAfterTwoImg2;
	private ImageView mAfterThreeImg1;
	private ImageView mAfterThreeImg2;
	private ImageView mAfterFourImg1;
	private ImageView mAfterFourImg2;
	private TextView mTimeView;
    private Context mcontext;
	private long cityCode;

	private Location location;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	public WeatherFragment() {
		super();
	}

	public WeatherFragment(Context context, long cityCode) {
		super();
		this.mcontext = context;
		this.cityCode = cityCode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.weather_layout, container, false);
		location = new Location(mcontext);
		location.locationAddress();
		findView(view);
		return view;
	}

	private void findView(View v) {
		// Create new PullToRefreshAttacher
		if(mPullToRefreshAttacher==null){
			mPullToRefreshAttacher = PullToRefreshAttacher.get((Activity) mcontext);
		}
        // Retrieve the PullToRefreshLayout from the content view
        PullToRefreshLayout ptrLayout = (PullToRefreshLayout) v.findViewById(R.id.swipe_container);

        // Give the PullToRefreshAttacher to the PullToRefreshLayout, along with the refresh
        // listener (this).
        ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);
		// weather view
		mCityNameView = (TextView) v.findViewById(R.id.cityname);
		mTimeView = (TextView) v.findViewById(R.id.weather_time);
		mTodayWeatherView = (TextView) v.findViewById(R.id.today_weather_text);
		mTodayContentView = (TextView) v.findViewById(R.id.today_content);
		mTodayContentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
				builder.setTitle(mcontext.getResources().getString(
						R.string.now_weather));
				builder.setMessage(mTodayContentView.getText());
				final String text = mCityNameView.getText().toString() + mTodayContentView.getText();
				builder.setPositiveButton(R.string.share, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						    Intent intent=new Intent(Intent.ACTION_SEND);   
					        intent.setType("text/*");   
					        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");   
					        intent.putExtra(Intent.EXTRA_TEXT,text);    
					        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
					        startActivity(Intent.createChooser(intent, mCityNameView.getText()+"天气")); 
						
					}
					
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		mTodayImg1 = (ImageView) v.findViewById(R.id.today_weather_img1);
		mTodayImg2 = (ImageView) v.findViewById(R.id.today_weather_img2);

		mAfterOneWeather = (TextView) v.findViewById(R.id.after_one_text);
		mAfterOneImg1 = (ImageView) v.findViewById(R.id.after_one_img1);
		mAfterOneImg2 = (ImageView) v.findViewById(R.id.after_one_img2);

		mAfterTwoWeather = (TextView) v.findViewById(R.id.after_two_text);
		mAfterTwoImg1 = (ImageView) v.findViewById(R.id.after_two_img1);
		mAfterTwoImg2 = (ImageView) v.findViewById(R.id.after_two_img2);

		mAfterThreeWeather = (TextView) v.findViewById(R.id.after_three_text);
		mAfterThreeImg1 = (ImageView) v.findViewById(R.id.after_three_img1);
		mAfterThreeImg2 = (ImageView) v.findViewById(R.id.after_three_img2);

		mAfterFourWeather = (TextView) v.findViewById(R.id.after_four_text);
		mAfterFourImg1 = (ImageView) v.findViewById(R.id.after_four_img1);
		mAfterFourImg2 = (ImageView) v.findViewById(R.id.after_four_img2);
		registerBroadcast();
		mDownload = this.getResources().getString(R.string.start_download);
		mCityUtils = new CityUtils(mcontext);
		setFirstDefaultCity();
		// if(CityQuery.getCityCountFromDb(mcontext) > 0){
		// insertSpinner();
		// setDefaultCity();
		// } else {
		// startToGetCity();
		// }

	}

	private void setDefaultCity() {
		CityBean city = CityQuery.getCityFromDefaultCity(mcontext);
		if (city != null) {
			mProvinceSpinner.setSelection(mProAdapter.getId(city.provinceCode));
		}
	}

	private void setFirstDefaultCity() {
		if (location.getCityCode() == null) {
			if (cityCode != 0) {
				if (!showCityWeather(cityCode)) {
					startToRefreshWeather(cityCode);
				}
			}
		}
	}

	public void startToGetCity() {
		if (NetworkUtils.getNetworkState(mcontext) != NetworkState.NONE) {
			progress_dialog = new ProgressDialog(mcontext);
			progress_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress_dialog.setMessage(this.getResources().getString(
					R.string.downloading_citys));
			progress_dialog.setCancelable(true);
			progress_dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					if (mCityUtils != null) {
						mCityUtils.stopGetCity();
					}

				}

			});

			progress_dialog.show();
			try {
				mCityUtils.saveCity();
			} catch (Exception e) {
				Toast.makeText(mcontext, R.string.alert_download_failure,
						Toast.LENGTH_SHORT).show();
				EngLog.e(TAG, "save city to db failed: error: " + e.toString());
			}
		} else {
			Toast.makeText(mcontext, R.string.alert_download_failure,
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Register the broadcast 1. download city success 2. download city failed
	 * 3. download province for showing in progress dialog
	 */
	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Statics.ACTION_DOWNLOAD_CITYS_SUCCESS);
		filter.addAction(Statics.ACTION_DOWNLOAD_CITYS_FAILED);
		filter.addAction(Statics.ACTION_DOWNLOAD_PROVINCE);
		filter.addAction(Statics.ACTION_REFRESH_WEATHER_SUCCESS);
		filter.addAction(Statics.ACTION_REFRESH_WEATHER_FAILED);
		mcontext.registerReceiver(downloadCityBroadcastReceiver, filter);
	}

	private BroadcastReceiver downloadCityBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals(Statics.ACTION_DOWNLOAD_CITYS_SUCCESS)) {
				progress_dialog.dismiss();
				showToast(mcontext.getResources().getString(
						R.string.download_success));
//				insertSpinner();
			} else if (intent.getAction().equals(
					Statics.ACTION_DOWNLOAD_PROVINCE)) {
				mProvinceName = intent.getStringExtra(Statics.EXTRA_PROVINCE);
				progress_dialog.setMessage(mDownload + "(" + mProvinceName
						+ ")");
			} else if (intent.getAction().equals(
					Statics.ACTION_DOWNLOAD_CITYS_FAILED)) {
				progress_dialog.dismiss();
				showToast(mcontext.getResources().getString(
						R.string.alert_download_failure));
			} else if (intent.getAction().equals(
					Statics.ACTION_REFRESH_WEATHER_SUCCESS)) {
				progress_dialog.dismiss();
				showToast(mcontext.getResources().getString(
						R.string.get_weather_success));
				CityQuery.saveDefaultCity(mcontext, cityCode);
				showCityWeather(cityCode);
			} else if (intent.getAction().equals(
					Statics.ACTION_REFRESH_WEATHER_FAILED)) {
				progress_dialog.dismiss();
				showToast(mcontext.getResources().getString(
						R.string.alert_download_failure));
			}
		}

	};

	private void showToast(String str) {
		Toast.makeText(mcontext, str, Toast.LENGTH_SHORT).show();
	}


//	private void insertSpinner() {
//		ArrayList<ProvinceBean> proList = CityQuery
//				.getAllProvinceList(mcontext);
//		mProAdapter = new ProvinceAdapter(mcontext, proList);
//		mProvinceSpinner.setAdapter(mProAdapter);
//		mProvinceSpinner
//				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//					@Override
//					public void onItemSelected(AdapterView<?> parents,
//							View view, int position, long id) {
//						long proCode = mProAdapter.getItem(position).provinceCode;
//						ArrayList<CityBean> cityList = CityQuery.getCityList(
//								mcontext, proCode);
//						mCityAdapter = new CityAdapter(mcontext, cityList);
//						mCitySpinner.setAdapter(mCityAdapter);
//						if (mCityBySearch != null) {
//							mCitySpinner.setSelection(mCityAdapter
//									.getId(mCityBySearch.cityCode));
//							mCityBySearch = null;
//						}
//
//						if (isDefaultCity) {
//							isDefaultCity = false;
//							mCitySpinner.setSelection(mCityAdapter
//									.getId(mDefaultCityCode));
//						}
//
//						mCitySpinner
//								.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//									@Override
//									public void onItemSelected(
//											AdapterView<?> parents, View view,
//											int position, long id) {
//										mSelectedCityCode = mCityAdapter
//												.getItem(position).cityCode;
//										if (!showCityWeather(mSelectedCityCode)) {
//											startToRefreshWeather(mSelectedCityCode);
//										}
//
//									}
//
//									@Override
//									public void onNothingSelected(
//											AdapterView<?> arg0) {
//
//									}
//
//								});
//
//					}
//
//					@Override
//					public void onNothingSelected(AdapterView<?> arg0) {
//
//					}
//				});
//	}

	public void startToRefreshWeather(long cityCode) {
		if (NetworkUtils.getNetworkState(mcontext) != NetworkState.NONE) {
			progress_dialog = new ProgressDialog(mcontext);
			progress_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress_dialog.setMessage(this.getResources().getString(
					R.string.refreshing_weather));
			progress_dialog.setCancelable(true);
			progress_dialog.show();
			try {
				WeatherUtils.StartToGetWeather(mcontext, cityCode);
				this.cityCode = cityCode;
			} catch (Exception e) {
				Toast.makeText(mcontext, R.string.alert_download_failure,
						Toast.LENGTH_SHORT).show();
				EngLog.e(TAG, "get city weather : error: " + e.toString());
			}
		} else {
			Toast.makeText(mcontext, R.string.alert_download_failure,
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean showCityWeather(long cityCode) {
		this.cityCode = cityCode;
		WeatherBean weather = WeatherQuery.readCityWeatherInDb(mcontext,
				cityCode);
		// if(weather != null){
		// String updateTime = weather.UPDATE_TIME;
		// final DateFormat TIMESTAMP_FORMAT;
		// Date date;
		//
		// TIMESTAMP_FORMAT= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// TIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		// try {
		// date = TIMESTAMP_FORMAT.parse(updateTime);
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		if (weather != null && weather.CITY_CODE > 0) {
			mTimeView.setText(this.getResources().getString(
					R.string.refresh_time)
					+ ":" + weather.UPDATE_TIME);
			mCityNameView.setText(weather.PROVINCE_NAME + " "
					+ weather.CITY_NAME);
			mTodayWeatherView.setText(weather.TODAY_WEATHER+"  "
					+ weather.TODAY_TEMP + "  " + weather.TODAY_WIND);
			mTodayContentView.setText(weather.TODAY_NOW_WEATHER + "\n"
					+ weather.TODAY_AIR + "\n" + weather.TODAY_CONTENT);
			mAfterOneWeather.setText(weather.AFTER_ONE_WEATHER + "\n"
					+ weather.AFTER_ONE_TEMP + "\n" + weather.AFTER_ONE_WIND);
			mAfterTwoWeather.setText(weather.AFTER_TWO_WEATHER + "\n"
					+ weather.AFTER_TWO_TEMP + "\n" + weather.AFTER_TWO_WIND);
			mAfterThreeWeather.setText(weather.AFTER_THREE_WEATHER + "\n"
					+ weather.AFTER_THREE_TEMP + "\n"
					+ weather.AFTER_THREE_WIND);
			mAfterFourWeather.setText(weather.AFTER_FOUR_WEATHER + "\n"
					+ weather.AFTER_FOUR_TEMP + "\n" + weather.AFTER_FOUR_WIND);
			mTodayImg1.setImageResource(WeatherDrawable.getWeatherDrawable(
					mcontext, weather.TODAY_IMG1));
			mTodayImg2.setImageResource(WeatherDrawable.getWeatherDrawable(
					mcontext, weather.TODAY_IMG2));
			mAfterOneImg1.setImageResource(WeatherDrawable.getWeatherDrawable(
					mcontext, weather.AFTER_ONE_IMG1));
			mAfterOneImg2.setImageResource(WeatherDrawable.getWeatherDrawable(
					mcontext, weather.AFTER_ONE_IMG1));
			mAfterTwoImg1.setImageResource(WeatherDrawable.getWeatherDrawable(
					mcontext, weather.AFTER_TWO_IMG1));
			mAfterTwoImg2.setImageResource(WeatherDrawable.getWeatherDrawable(
					mcontext, weather.AFTER_TWO_IMG2));
			mAfterThreeImg1.setImageResource(WeatherDrawable
					.getWeatherDrawable(mcontext, weather.AFTER_THREE_IMG1));
			mAfterThreeImg2.setImageResource(WeatherDrawable
					.getWeatherDrawable(mcontext, weather.AFTER_THREE_IMG2));
			mAfterFourImg1.setImageResource(WeatherDrawable.getWeatherDrawable(
					mcontext, weather.AFTER_FOUR_IMG1));
			mAfterFourImg2.setImageResource(WeatherDrawable.getWeatherDrawable(
					mcontext, weather.AFTER_FOUR_IMG2));
			return true;
		}

		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mCityUtils != null) {
			mCityUtils.stopGetCity();
		}
		if (downloadCityBroadcastReceiver != null) {
			mcontext.unregisterReceiver(downloadCityBroadcastReceiver);
		}

	}

	@Override
	public void onRefreshStarted(View view) {
		 /**
         * Simulate Refresh with 4 seconds sleep
         */
        EngLog.e(TAG, "OnRefresh===============");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                startToRefreshWeather(cityCode);
                // Notify PullToRefreshAttacher that the refresh has finished
                mPullToRefreshAttacher.setRefreshComplete();
            }
        }.execute();
	}

}
