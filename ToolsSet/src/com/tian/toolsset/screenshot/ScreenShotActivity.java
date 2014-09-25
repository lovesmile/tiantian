package com.tian.toolsset.screenshot;

import java.io.File;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.tian.toolsset.R;
import com.tian.toolsset.utils.RootPermissionGet;
import com.tian.toolsset.utils.ToastUtils;
import com.tian.toolsset.utils.ToolsBroadcastReceiver;
import com.tian.toolsset.utils.ToolsConstants;

public class ScreenShotActivity extends Activity implements OnClickListener {
	public static ScreenShotActivity activity = null;
	private ImageView back;
	private Button shotOnce;
	private Button shotMore;
	private Button end;
	private Intent intent;
	private ToolsBroadcastReceiver mMyBroadcastReceiver = new ToolsBroadcastReceiver();
	// 通知管理器
	private NotificationManager nm;
	// 通知显示内容
	private PendingIntent pd;
    private boolean moreShot =false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shot);
		activity = ScreenShotActivity.this;
		back = (ImageView) findViewById(R.id.iv_back);
		shotOnce = (Button) findViewById(R.id.button1);
		shotMore = (Button) findViewById(R.id.button2);
		end = (Button) findViewById(R.id.button3);

		back.setOnClickListener(this);
		shotOnce.setOnClickListener(this);
		shotMore.setOnClickListener(this);
		end.setOnClickListener(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ToolsConstants.START_SCREEN_SHOT_ONCE);
		intentFilter.addAction(ToolsConstants.START_SCREEN_SHOT_MORE);
		intentFilter.addAction(ToolsConstants.START_SCREEN_SHOT_END);
		registerReceiver(mMyBroadcastReceiver, intentFilter);

		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, ScreenShotActivity.class);
		pd = PendingIntent.getActivity(ScreenShotActivity.this, 0, intent, 0);
		if (!RootPermissionGet.upgradeRootPermission(getPackageCodePath())) {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// once Notification ID
		int Notification_ID_ONCE = 110;
		Notification onceNF = null;
		// once Notification ID
		int Notification_ID_MORE = 111;
		Notification moreNF = null;
		// 自定义界面
		RemoteViews rv = new RemoteViews(getPackageName(),
				R.layout.notification);
		switch (v.getId()) {
		case R.id.iv_back:
			onBackPressed();
			break;
		case R.id.button1:
			if(moreShot){
				ToastUtils toast = new ToastUtils(ScreenShotActivity.this);
				toast.show("目前是连续截图模式，如果要切换单屏截图请先点击结束按钮",1000);
				return;
			}
			// 清除 NF
			nm.cancel(Notification_ID_MORE);
			// 新建状态栏通知
			onceNF = new Notification();
			// 设置通知在状态栏显示的图标
			onceNF.icon = R.drawable.icon;
			// 通知时在状态栏显示的内容
			onceNF.tickerText = "单屏截屏";
			// 通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
			// 如果要全部采用默认值, 用 DEFAULT_ALL.
			// 此处采用默认声音
			onceNF.defaults |= Notification.DEFAULT_SOUND;
			onceNF.defaults |= Notification.DEFAULT_VIBRATE;
			onceNF.defaults |= Notification.DEFAULT_LIGHTS;
			// 点击'Clear'时，不清楚该通知(QQ的通知无法清除，就是用的这个)
			onceNF.flags |= Notification.FLAG_NO_CLEAR;
			// 第二个参数 ：下拉状态栏时显示的消息标题 expanded message title
			// 第三个参数：下拉状态栏时显示的消息内容 expanded message text
			// 第四个参数：点击该通知时执行页面跳转
			rv.setTextViewText(R.id.tv_rv, "单张截图开始");
			rv.setTextViewText(R.id.tv_rv2, "摇动手机截图，点击回到截屏页");
			onceNF.contentIntent = pd;
			onceNF.contentView = rv;
			// 发出状态栏通知
			// The first parameter is the unique ID for the Notification
			// and the second is the Notification object.
			nm.notify(Notification_ID_ONCE, onceNF);
			intent = new Intent();
			intent.setAction(ToolsConstants.START_SCREEN_SHOT_ONCE);
			sendBroadcast(intent);
			break;
		case R.id.button2:
			moreShot = true;
			// 清除 NF
			nm.cancel(Notification_ID_ONCE);
			// 新建状态栏通知
			moreNF = new Notification();
			// 设置通知在状态栏显示的图标
			moreNF.icon = R.drawable.icon;
			// 通知时在状态栏显示的内容
			moreNF.tickerText = "连续截图";
			// 通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
			// 如果要全部采用默认值, 用 DEFAULT_ALL.
			// 此处采用默认声音
			moreNF.defaults |= Notification.DEFAULT_SOUND;
			moreNF.defaults |= Notification.DEFAULT_VIBRATE;
			moreNF.defaults |= Notification.DEFAULT_LIGHTS;
			// 点击'Clear'时，不清楚该通知(QQ的通知无法清除，就是用的这个)
			moreNF.flags |= Notification.FLAG_NO_CLEAR;
			// 第二个参数 ：下拉状态栏时显示的消息标题 expanded message title
			// 第三个参数：下拉状态栏时显示的消息内容 expanded message text
			// 第四个参数：点击该通知时执行页面跳转
			moreNF.contentIntent = pd;
			moreNF.contentView = rv;
			// 发出状态栏通知
			// The first parameter is the unique ID for the Notification
			// and the second is the Notification object.
			nm.notify(Notification_ID_MORE, moreNF);
			rv.setTextViewText(R.id.tv_rv, "连续截图开始");
			rv.setTextViewText(R.id.tv_rv2,
					"滑动页面后摇动手机连续截取，点击回到截屏页");
			moreNF.contentView = rv;
			nm.notify(Notification_ID_MORE, moreNF);
			intent = new Intent();
			intent.setAction(ToolsConstants.START_SCREEN_SHOT_MORE);
			sendBroadcast(intent);
			break;
		case R.id.button3:
			moreShot = false;
			nm.cancelAll();
			intent = new Intent();
			intent.setAction(ToolsConstants.START_SCREEN_SHOT_END);
			sendBroadcast(intent);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	@Override
	protected void onDestroy() {
		if (mMyBroadcastReceiver != null) {
			unregisterReceiver(mMyBroadcastReceiver);
		}
		super.onDestroy();
	}
}
