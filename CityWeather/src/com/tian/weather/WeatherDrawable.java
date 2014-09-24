package com.tian.weather;

import android.content.Context;



public class WeatherDrawable {

	private static final int IMG_0 = R.drawable.a_0;
	private static final int IMG_1 = R.drawable.a_1;
	private static final int IMG_2 = R.drawable.a_2;
	private static final int IMG_3 = R.drawable.a_3;
	private static final int IMG_4 = R.drawable.a_4;
	private static final int IMG_5 = R.drawable.a_5;
	private static final int IMG_6 = R.drawable.a_6;
	private static final int IMG_7 = R.drawable.a_7;
	private static final int IMG_8 = R.drawable.a_8;
	private static final int IMG_9 = R.drawable.a_9;
	private static final int IMG_10 = R.drawable.a_10;
	private static final int IMG_11 = R.drawable.a_11;
	private static final int IMG_12 = R.drawable.a_12;
	private static final int IMG_13 = R.drawable.a_13;
	private static final int IMG_14 = R.drawable.a_14;
	private static final int IMG_15 = R.drawable.a_15;
	private static final int IMG_16 = R.drawable.a_16;
	private static final int IMG_17 = R.drawable.a_17;
	private static final int IMG_18 = R.drawable.a_18;
	private static final int IMG_19 = R.drawable.a_19;
	private static final int IMG_20 = R.drawable.a_20;
	private static final int IMG_21 = R.drawable.a_21;
	private static final int IMG_22 = R.drawable.a_22;
	private static final int IMG_23 = R.drawable.a_23;
	private static final int IMG_24 = R.drawable.a_24;
	private static final int IMG_25 = R.drawable.a_25;
	private static final int IMG_26 = R.drawable.a_26;
	private static final int IMG_27 = R.drawable.a_27;
	private static final int IMG_28 = R.drawable.a_28;
	private static final int IMG_29 = R.drawable.a_29;
	private static final int IMG_30 = R.drawable.a_30;
	private static final int IMG_31 = R.drawable.a_31;
	private static final int NOTHING = R.drawable.a_nothing;
	
	private static final int[] weatherImg = new int[]{IMG_0, IMG_1, IMG_2, IMG_3, IMG_4, IMG_5, IMG_6, IMG_7, IMG_8, IMG_9,
		 IMG_10, IMG_11, IMG_12, IMG_13, IMG_14, IMG_15, IMG_16, IMG_17, IMG_18, IMG_19,
		 IMG_20, IMG_21, IMG_22, IMG_23, IMG_24, IMG_25, IMG_26, IMG_27, IMG_28, IMG_29,
		 IMG_30, IMG_31, NOTHING};
	
	public static int getWeatherDrawable(Context context, int weatherId){
		if(weatherId < 32){
			return weatherImg[weatherId];
		} else {
			return weatherImg[32];
		}
		
	}
}
