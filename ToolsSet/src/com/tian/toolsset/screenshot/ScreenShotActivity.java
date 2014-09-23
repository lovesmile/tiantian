package com.tian.toolsset.screenshot;

import android.app.Activity;
import android.os.Bundle;

import com.tian.toolsset.R;

public class ScreenShotActivity extends Activity{
    public static ScreenShotActivity activity = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transparent);
		activity = ScreenShotActivity.this;
	}

}
