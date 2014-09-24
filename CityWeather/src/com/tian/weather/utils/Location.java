package com.tian.weather.utils;

import android.content.Context;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 
 * @author
 * 
 */
public class Location {
	private static final String tag = "LocationClient";
	private final int UPDATE_TIME = 1000;
	private LocationClient locationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	private int mLocationCounts = 0;
	private String info;
	private String time;
	private int errCode;
	private double latitude = 0;
	private double lontitude = 0;
	private float radius;
	private float speed;
	private int satellite;
	private String address;
	private String count;
	private String poi;
	private String province;
	private String city;
	private String district;
    private String cityCode;
    /**
	 * 
	 * @param locationClient1
	 */
	public Location(Context mContext) {
		super();
		locationClient = new LocationClient(mContext);
		// 注册位置监听器
		locationClient.registerLocationListener(myListener); 
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 是否打开GPS
		option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 设置定位优先级
		option.setProdName("Weather"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
		option.setScanSpan(UPDATE_TIME); // 设置定时定位的时间间隔。单位毫秒
		option.setIsNeedAddress(true);
		locationClient.setLocOption(option);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null) {
				return;
			}
			time = location.getTime();
			errCode = location.getLocType();
			double latitude = location.getLatitude();
			double lontitude = location.getLongitude();
			if (Math.abs(latitude) > 0.01) {
				setLatitude(latitude);
				setLontitude(lontitude);
			}
			radius = location.getRadius();
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				speed = location.getSpeed();
				satellite = location.getSatelliteNumber();
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				address = location.getAddrStr();
				province = location.getProvince();
				city = location.getCity();
				district = location.getDistrict();
				cityCode = location.getCityCode();
				
			}
			mLocationCounts++;
			count = String.valueOf(mLocationCounts);
			info = latitude + "_" + lontitude;
			Log.d(tag, "BAIDU onReceiveLocation() = " + province + city
					+ district);
			locationClient.stop();

		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	public LocationClient getLocationClient() {
		return locationClient;
	}

	/*
	 * public void setLocationClient(LocationClient locationClient) {
	 * this.locationClient = locationClient; }
	 */

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLontitude() {
		return lontitude;
	}

	public void setLontitude(double lontitude) {
		this.lontitude = lontitude;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getSatellite() {
		return satellite;
	}

	public void setSatellite(int satellite) {
		this.satellite = satellite;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public void locationAddress() {

		if (locationClient == null) {
			return;
		} else {
			locationClient.start();
			/*
			 * 当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。 调用requestLocation(
			 * )后，每隔设定的时间，定位SDK就会进行一次定位。 如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
			 * 返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
			 * 定时定位时，调用一次requestLocation，会定时监听到定位结果。
			 */
			locationClient.requestLocation();
		}
	}

}