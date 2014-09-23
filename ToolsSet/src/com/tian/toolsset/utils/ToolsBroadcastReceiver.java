package com.tian.toolsset.utils;

import com.tian.toolsset.screenshot.ScreenShot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.text.TextUtils;

public class ToolsBroadcastReceiver extends BroadcastReceiver {
	private Camera m_Camera;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (TextUtils.equals(ToolsConstants.START_LIGHT_ACTION, action)) {
			PackageManager pm = context.getPackageManager();
			FeatureInfo[] features = pm.getSystemAvailableFeatures();
			for (FeatureInfo f : features) {
				if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) // 判断设备是否支持闪光灯
				{
					if (null == m_Camera) {
						m_Camera = Camera.open();
					}
					Camera.Parameters parameters = m_Camera.getParameters();
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					m_Camera.setParameters(parameters);
					m_Camera.startPreview();
				}
			}
		} else if(TextUtils.equals(ToolsConstants.OFF_LIGHT_ACTION, action)) {
			if (m_Camera != null) {
				m_Camera.stopPreview();
				m_Camera.release();
				m_Camera = null;
			}
		}else if(TextUtils.equals(ToolsConstants.START_SCREEN_SHOT_ONCE, action)){
			new ScreenShot(context, 1).shoot();
		}else if(TextUtils.equals(ToolsConstants.START_SCREEN_SHOT_MORE,action)){
			
		}else if(TextUtils.equals(ToolsConstants.START_SCREEN_SHOT_END, action)){
			
		}
	}

}
