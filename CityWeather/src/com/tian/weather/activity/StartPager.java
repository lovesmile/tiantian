/*
 * Copyright (C) 2013 Suning Corporation
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tian.weather.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.tian.weather.R;
import com.tian.weather.utils.EngLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Startup splash screen
 * 
 * <p>
 * {Notes here}.
 */
public class StartPager extends Activity {

	private final String TAG = "StartPager";
	private static final long TIMEOUT_CHECK_VERION_UPDATE = 2000L;
	private ImageView mStartPage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.start_pager);
		mStartPage = (ImageView) this.findViewById(R.id.start);

		System.currentTimeMillis();
		waitToHome();
	}

	/**
	 * 跳转HomeActivity,如果是第一次登陆则先跳转城市选择
	 */
	private void goHome() {
		SharedPreferences preferences = getSharedPreferences("count",
				Context.MODE_PRIVATE);
		if (preferences.getBoolean("firststart", true)) {
			copyDatabase(StartPager.this);
			Editor e = preferences.edit();
			e.putBoolean("firststart", false);
			e.commit();
			Intent intent = new Intent();
			// 更换引导页
			intent.setClass(getApplicationContext(), HotCityActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		StartPager.this.finish();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	private void waitToHome() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(TIMEOUT_CHECK_VERION_UPDATE);
					goHome();
				} catch (InterruptedException e) {
					EngLog.e(TAG, e.toString());
				}
			}
		}).start();

	}

	@Override
	public void onResume() {
		EngLog.d(TAG, "StartPager.OnResume()");
		super.onResume();

	}

	@Override
	public void onPause() {
		EngLog.d(TAG, "StartPager.onPause()");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (null != mStartPage) {
			mStartPage.setBackgroundDrawable(null);
			mStartPage = null;
		}
	}
	/**
	 *  把数据库复制到指定目录
	 */
	public void copyDatabase(Context context)
	{
		 String databasepath = "data/data/com.tian.weather/databases/";
		 String databasefn = "chinacity.db";
		try
		{
			// 获得chinacity.db 绝对路径
			String DATABASEFN = databasepath + databasefn;
			File dir = new File(databasepath);
			
			// 如果目录中存在，创建这个目录
			if(!dir.exists())
			{
				EngLog.i(TAG, "dir is exist,create file");
			}
			dir.mkdir();
			
			// 如果在目录中不存在
			// 文件，则从res\raw目录中复制这个文件到
			if(!(new File(DATABASEFN).exists()))
			{
				// 获得封装  文件的InputStream对象
				InputStream is = context.getResources().openRawResource(R.raw.chinacity);
				FileOutputStream fos = new FileOutputStream(DATABASEFN);
				byte[] buffer = new byte[1024];
				int count = 0;
				// 开始复制db文件
				while((count=is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
					EngLog.i(TAG, "db copy done");
				}
				fos.close();
				is.close();
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
