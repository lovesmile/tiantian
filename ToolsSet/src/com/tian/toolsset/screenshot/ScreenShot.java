package com.tian.toolsset.screenshot;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
import android.widget.Toast;

import com.tian.toolsset.screenshot.ShakeListener.OnShakeListener;

public class ScreenShot {
	private static final String TAG = "ScreenShot";
	// ͼƬ�洢·��
	public static String SAVE_PATH = getSDCardPath() + "/ScreenImages/";
	public static String SAVE_CACHE_PATH = getSDCardPath()
			+ "/ScreenImages/cache/";
	private static File baseFile;
	// �ļ�
	public static String filename;
	private ShakeListener mShakeListener = null;
	private Vibrator mVibrator;
	private Context mContext;
	private MyHandler handler = new MyHandler();
	private static ArrayList<String> paths = new ArrayList<String>();
	private long lastTime = 0;
	private boolean isShow =false;
	/**
	 * ��ͼ����������1 ���� ��2 ����
	 */
	private static int maction;
	private int count = 0;

	public ScreenShot(Context context) {
		super();
		mContext = context;
		mShakeListener = new ShakeListener(context);
		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	/*
	 * private static Bitmap takeScreenShot(Activity activity) {
	 * 
	 * // View������Ҫ��ͼ��View View view = activity.getWindow().getDecorView();
	 * view.setDrawingCacheEnabled(true); view.buildDrawingCache(); Bitmap b1 =
	 * view.getDrawingCache();
	 * 
	 * // ��ȡ״̬���߶� Rect frame = new Rect();
	 * activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
	 * int statusBarHeight = frame.top; // ��ȡ��Ļ���͸� int width =
	 * activity.getWindowManager().getDefaultDisplay().getWidth(); int height =
	 * activity.getWindowManager().getDefaultDisplay() .getHeight(); // ȥ��������
	 * Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height -
	 * statusBarHeight); view.destroyDrawingCache();
	 * 
	 * return b; }
	 * 
	 * private static Bitmap takeScreenShot(View v) { Bitmap bitmap =
	 * Bitmap.createBitmap(v.getWidth(), v.getHeight() - 75, Config.ARGB_8888);
	 * Canvas canvas = new Canvas(); canvas.setBitmap(bitmap); v.draw(canvas);
	 * return bitmap; }
	 */
	private void savePic(Bitmap b) {
		FileOutputStream fos = null;
		String str = SAVE_PATH + System.currentTimeMillis() + ".png";
		try {
			fos = new FileOutputStream(str);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				handler.sendEmptyMessage(2);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * public static void shoot(Context context, final View view) { filename =
	 * SAVE_PATH + System.currentTimeMillis() + ".png"; if (filename == null) {
	 * return; } File file = new File(filename); if
	 * (!file.getParentFile().exists()) { file.getParentFile().mkdirs(); } new
	 * Thread(new Runnable() {
	 * 
	 * @Override public void run() {
	 * ScreenShot.savePic(ScreenShot.takeScreenShot(view)); } }).start();
	 * Toast.makeText(context, "�����ļ��ѱ�����SDCard/ScreenImages/Ŀ¼��",
	 * Toast.LENGTH_LONG).show(); }
	 * 
	 * public static void shoot(Activity context) { filename = SAVE_PATH +
	 * System.currentTimeMillis() + ".png"; if (filename == null) { return; }
	 * File file = new File(filename); if (!file.getParentFile().exists()) {
	 * file.getParentFile().mkdirs(); }
	 * ScreenShot.savePic(ScreenShot.takeScreenShot(context));
	 * Toast.makeText(context, "�����ļ��ѱ�����SDCard/ScreenImages/Ŀ¼��",
	 * Toast.LENGTH_LONG).show(); }
	 */
	public void shakeShot(final int action) {
		if (mShakeListener.isStart) {
			mShakeListener.stop();
		}
		mShakeListener.start();
		lastTime = 0;
		count = 0;
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake() {
				long currentTime = System.currentTimeMillis();
				mVibrator.vibrate(new long[] { 400, 100, 400, 100 }, -1);
				if(currentTime-lastTime<2000){
					if(isShow){
						isShow =false;
					}else{
						Toast.makeText(mContext, "��ͼ���ʱ��̫�̣�ż��æ��������O(��_��)O", Toast.LENGTH_SHORT).show();
						isShow =true;
					}
					return;
				}
				lastTime = currentTime;
				shoot(action);
			}
		});
	}

	public void stopShot() {
		if (mShakeListener != null && mVibrator != null) {
			count = 0;
			mVibrator.vibrate(new long[] { 400, 100, 400, 100 }, -1);
			if (mShakeListener.isStart) {
				mShakeListener.stop();
			}
			if (maction == 1) {
				Toast.makeText(mContext, "ֹͣ��ͼ��ͼƬ�ѱ��浽SDCard/ScreenImages/Ŀ¼��",
						Toast.LENGTH_SHORT).show();
			} else if (maction == 2) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Looper.prepare();
							savePic(buildThum());
							Looper.loop();
							if (baseFile.exists()) {
								deleteFile(baseFile);
							}
						} catch (Exception e) {
							Toast.makeText(mContext, "��ͼ����ʧ��",
									Toast.LENGTH_SHORT).show();
							if (baseFile.exists()) {
								deleteFile(baseFile);
							}
						}

					}
				}).start();
				Toast.makeText(mContext, "�������ɳ�ͼ", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "û�п�ʼ��ͼ", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	/**
	 * ��ͼ
	 */
	public void shoot(int action) {
		maction = action;
		if (count > 15) {
			Toast.makeText(mContext, "������ͼ�Ѵ�15������", Toast.LENGTH_SHORT).show();
			return;
		}
		count++;
		if (action == 1) {
			filename = SAVE_PATH + System.currentTimeMillis() + ".png";
		} else if (action == 2) {
			filename = SAVE_CACHE_PATH + System.currentTimeMillis() + ".png";
		}
		if (filename == null) {
			return;
		}
		File file = new File(filename);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				Process process = null;
				try {
					process = Runtime.getRuntime().exec("su");
					PrintStream outputStream = null;
					try {
						outputStream = new PrintStream(
								new BufferedOutputStream(
										process.getOutputStream(), 8192));
						outputStream.println("screencap -p " + filename);
						outputStream.flush();
					} catch (Exception e) {
						handler.sendEmptyMessage(0);
						return;
					} finally {
						if (outputStream != null) {
							outputStream.close();
						}
					}
					process.waitFor();
				} catch (Exception e) {
					handler.sendEmptyMessage(0);
					return;
				} finally {
					if (process != null) {
						process.destroy();
					}
				}
			}
		}).start();
		handler.sendEmptyMessage(1);
	}

	/**
	 * ��ȡSDCard��Ŀ¼·������
	 * 
	 * @return
	 */
	private static String getSDCardPath() {
		File sdcardDir = null;
		// �ж�SDCard�Ƿ����
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}

	/**
	 * ��ȡͼƬ��ַ�б�
	 * 
	 * @param file
	 * @return
	 */
	private static ArrayList<String> imagePath(File file) {
		ArrayList<String> list = new ArrayList<String>();

		File[] files = file.listFiles();
		for (File f : files) {
			list.add(f.getAbsolutePath());
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * ��ȡsdcard�ļ����е�ͼƬ������������ͼ
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	private static Bitmap buildThum() throws FileNotFoundException {
		baseFile = new File(SAVE_CACHE_PATH);
		Bitmap result = null;
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		if (baseFile != null && baseFile.exists()) {
			paths = imagePath(baseFile);
			if (!paths.isEmpty()) {
				for (int i = 0; i < paths.size(); i++) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = false; // �����˴�����һ��Ҫ�ǵý�ֵ����Ϊfalse
					Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i),
							options);
					bitmaps.add(bitmap);
				}
				result = addxBitmap(bitmaps);
			}
			deleteFile(baseFile);
		}
		return result;
	}

	// ��SD���ļ�ɾ��
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			}
			// �������һ��Ŀ¼
			else if (file.isDirectory()) {
				// ����Ŀ¼�����е��ļ� files[];
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) { // ����Ŀ¼�����е��ļ�
					deleteFile(files[i]); // ��ÿ���ļ� ������������е���
				}
			}
			file.delete();
		}
	}

	/**
	 * ƴ��ͼƬ
	 */
	private static Bitmap addxBitmap(List<Bitmap> list) {
		int width;
		int height1;
		int height2 = 0;
		width = list.get(0).getWidth();
		height1 = list.get(0).getHeight() * list.size();
		Bitmap result = Bitmap.createBitmap(width, height1, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		for (Bitmap bitmap : list) {
			canvas.drawBitmap(bitmap, 0, height2, null);
			height2 += bitmap.getHeight();
		}
		return result;
	}

	/**
	 * ������Ϣ,������Ϣ ,��Handler���뵱ǰ���߳�һ������
	 * */

	public class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// ���������д�˷���,��������
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// �˴����Ը���UI
			switch (msg.what) {
			case 0:
				Toast.makeText(mContext, "��ͼʧ��", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(mContext, ""+count+"", 500).show();;
				break;
			case 2:
				Toast.makeText(mContext, "��ͼ�ѱ��浽SDCard/ScreenImages/Ŀ¼��",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}