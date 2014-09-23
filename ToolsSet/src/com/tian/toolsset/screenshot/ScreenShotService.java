package com.tian.toolsset.screenshot;

import com.tian.toolsset.screenshot.ShakeListener.OnShakeListener;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

public class ScreenShotService extends Service {
	private ShakeListener mShakeListener = null;
	private Vibrator mVibrator;
	private static final String TAG = "ScreenShotService";
	private IBinder binder = new ScreenShotService.LocalBinder();
    private boolean isStart = false;
	@Override
	public void onCreate() {
		mShakeListener = new ShakeListener(this);
		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake() {
				if(isStart){
					isStart = false;
					// ������
					mVibrator.vibrate(new long[] { 400, 100, 400, 100 }, -1); // ��һ�����������ǽ������飬
					// �ڶ����������ظ�������-1Ϊ���ظ�����-1 ��pattern��ָ���±꿪ʼ�ظ�
					Toast.makeText(getApplicationContext(), "ֹͣ����",
							Toast.LENGTH_SHORT).show();
					mShakeListener.stop();
				}else{
					// ������
					mVibrator.vibrate(new long[] { 400, 100, 400, 100 }, -1); // ��һ�����������ǽ������飬
					// �ڶ����������ظ�������-1Ϊ���ظ�����-1 ��pattern��ָ���±꿪ʼ�ظ�
					Toast.makeText(getApplicationContext(), "��ʼ����",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), ScreenShotActivity.class);
					startActivity(intent);
					isStart = true;
				}
			}
		});
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// ����������̳�Binder
	public class LocalBinder extends Binder {
		// ���ر��ط���
		ScreenShotService getService() {
			return ScreenShotService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

}
