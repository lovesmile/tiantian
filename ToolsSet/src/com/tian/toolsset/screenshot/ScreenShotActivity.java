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
	// ֪ͨ������
	private NotificationManager nm;
	// ֪ͨ��ʾ����
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
		// �Զ������
		RemoteViews rv = new RemoteViews(getPackageName(),
				R.layout.notification);
		switch (v.getId()) {
		case R.id.iv_back:
			onBackPressed();
			break;
		case R.id.button1:
			if(moreShot){
				ToastUtils toast = new ToastUtils(ScreenShotActivity.this);
				toast.show("Ŀǰ��������ͼģʽ�����Ҫ�л�������ͼ���ȵ��������ť",1000);
				return;
			}
			// ��� NF
			nm.cancel(Notification_ID_MORE);
			// �½�״̬��֪ͨ
			onceNF = new Notification();
			// ����֪ͨ��״̬����ʾ��ͼ��
			onceNF.icon = R.drawable.icon;
			// ֪ͨʱ��״̬����ʾ������
			onceNF.tickerText = "��������";
			// ֪ͨ��Ĭ�ϲ��� DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
			// ���Ҫȫ������Ĭ��ֵ, �� DEFAULT_ALL.
			// �˴�����Ĭ������
			onceNF.defaults |= Notification.DEFAULT_SOUND;
			onceNF.defaults |= Notification.DEFAULT_VIBRATE;
			onceNF.defaults |= Notification.DEFAULT_LIGHTS;
			// ���'Clear'ʱ���������֪ͨ(QQ��֪ͨ�޷�����������õ����)
			onceNF.flags |= Notification.FLAG_NO_CLEAR;
			// �ڶ������� ������״̬��ʱ��ʾ����Ϣ���� expanded message title
			// ����������������״̬��ʱ��ʾ����Ϣ���� expanded message text
			// ���ĸ������������֪ͨʱִ��ҳ����ת
			rv.setTextViewText(R.id.tv_rv, "���Ž�ͼ��ʼ");
			rv.setTextViewText(R.id.tv_rv2, "ҡ���ֻ���ͼ������ص�����ҳ");
			onceNF.contentIntent = pd;
			onceNF.contentView = rv;
			// ����״̬��֪ͨ
			// The first parameter is the unique ID for the Notification
			// and the second is the Notification object.
			nm.notify(Notification_ID_ONCE, onceNF);
			intent = new Intent();
			intent.setAction(ToolsConstants.START_SCREEN_SHOT_ONCE);
			sendBroadcast(intent);
			break;
		case R.id.button2:
			moreShot = true;
			// ��� NF
			nm.cancel(Notification_ID_ONCE);
			// �½�״̬��֪ͨ
			moreNF = new Notification();
			// ����֪ͨ��״̬����ʾ��ͼ��
			moreNF.icon = R.drawable.icon;
			// ֪ͨʱ��״̬����ʾ������
			moreNF.tickerText = "������ͼ";
			// ֪ͨ��Ĭ�ϲ��� DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
			// ���Ҫȫ������Ĭ��ֵ, �� DEFAULT_ALL.
			// �˴�����Ĭ������
			moreNF.defaults |= Notification.DEFAULT_SOUND;
			moreNF.defaults |= Notification.DEFAULT_VIBRATE;
			moreNF.defaults |= Notification.DEFAULT_LIGHTS;
			// ���'Clear'ʱ���������֪ͨ(QQ��֪ͨ�޷�����������õ����)
			moreNF.flags |= Notification.FLAG_NO_CLEAR;
			// �ڶ������� ������״̬��ʱ��ʾ����Ϣ���� expanded message title
			// ����������������״̬��ʱ��ʾ����Ϣ���� expanded message text
			// ���ĸ������������֪ͨʱִ��ҳ����ת
			moreNF.contentIntent = pd;
			moreNF.contentView = rv;
			// ����״̬��֪ͨ
			// The first parameter is the unique ID for the Notification
			// and the second is the Notification object.
			nm.notify(Notification_ID_MORE, moreNF);
			rv.setTextViewText(R.id.tv_rv, "������ͼ��ʼ");
			rv.setTextViewText(R.id.tv_rv2,
					"����ҳ���ҡ���ֻ�������ȡ������ص�����ҳ");
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
