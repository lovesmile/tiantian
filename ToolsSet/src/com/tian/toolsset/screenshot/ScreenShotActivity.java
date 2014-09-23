package com.tian.toolsset.screenshot;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.tian.toolsset.R;
import com.tian.toolsset.ToolsActivity;
import com.tian.toolsset.utils.ToolsBroadcastReceiver;
import com.tian.toolsset.utils.ToolsConstants;

public class ScreenShotActivity extends Activity implements OnClickListener{
    public static ScreenShotActivity activity = null;
    private ImageView back;
    private Button shotOnce;
	private Button shotMore;
	private Button end; 
	private Intent intent;
	private ToolsBroadcastReceiver mMyBroadcastReceiver = new ToolsBroadcastReceiver();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shot);
		activity = ScreenShotActivity.this;
		back = (ImageView)findViewById(R.id.iv_back);
		shotOnce = (Button)findViewById(R.id.button1);
		shotMore = (Button)findViewById(R.id.button2);
		end = (Button)findViewById(R.id.button3);
		
		back.setOnClickListener(this);
		shotOnce.setOnClickListener(this);
		shotMore.setOnClickListener(this);
		end.setOnClickListener(this);
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ToolsConstants.START_SCREEN_SHOT_ONCE);
        intentFilter.addAction(ToolsConstants.START_SCREEN_SHOT_MORE);
        intentFilter.addAction(ToolsConstants.START_SCREEN_SHOT_END);
        registerReceiver(mMyBroadcastReceiver, intentFilter);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.iv_back:
			onBackPressed();
			break;
        case R.id.button1:
        	intent = new Intent();
        	intent.setAction(ToolsConstants.START_SCREEN_SHOT_ONCE);
	        sendBroadcast(intent);
				break;
	    case R.id.button2:
	    	intent = new Intent();
        	intent.setAction(ToolsConstants.START_SCREEN_SHOT_MORE);
        	sendBroadcast(intent);
				break;
	    case R.id.button3:
	    	intent = new Intent();
        	intent.setAction(ToolsConstants.START_SCREEN_SHOT_END);
        	sendBroadcast(intent);
			break;
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent it = new Intent();
		it.setClass(ScreenShotActivity.this, ToolsActivity.class);
		startActivity(it);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		super.onBackPressed();
	}
	 @Override
	    protected void onDestroy() {
	        if (mMyBroadcastReceiver != null) {
	            unregisterReceiver(mMyBroadcastReceiver);
	        }
	 }
}
