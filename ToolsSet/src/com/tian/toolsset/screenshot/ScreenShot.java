package com.tian.toolsset.screenshot;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ScreenShot {
	private static final String TAG = null;
	// ͼƬ�洢·��
	public static String SAVE_PATH = getSDCardPath() + "/ScreenImages/";
	// �ļ�
	public static String filename;
	public static boolean isNoPor = false;
	private static Bitmap takeScreenShot(Activity activity) {

		// View������Ҫ��ͼ��View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// ��ȡ״̬���߶�
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top; // ��ȡ��Ļ���͸�
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// ȥ��������
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();

		return b;
	}

	private static Bitmap takeScreenShot(View v) {
		Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight() - 75,
				Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(bitmap);
		v.draw(canvas);
		return bitmap;
	}

	private static void savePic(Bitmap b) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public static void shoot(Context context, final View view) {
		filename = SAVE_PATH + System.currentTimeMillis() + ".png";
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
				ScreenShot.savePic(ScreenShot.takeScreenShot(view));
			}
		}).start();
		Toast.makeText(context, "�����ļ��ѱ�����SDCard/ScreenImages/Ŀ¼��",
				Toast.LENGTH_LONG).show();
	}

	public static void shoot(Activity context) {
		filename = SAVE_PATH + System.currentTimeMillis() + ".png";
		if (filename == null) {
			return;
		}
		File file = new File(filename);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		ScreenShot.savePic(ScreenShot.takeScreenShot(context));
		Toast.makeText(context, "�����ļ��ѱ�����SDCard/ScreenImages/Ŀ¼��",
				Toast.LENGTH_LONG).show();
	}

	public static void shoot(Context context) {
		filename = SAVE_PATH + System.currentTimeMillis() + ".png";
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
						isNoPor =true;
					} catch (Exception e) {
						isNoPor =false;
					} finally {
						if (outputStream != null) {
							outputStream.close();
						}
					}
					process.waitFor();
				} catch (Exception e) {
					isNoPor =false;
				} finally {
					if (process != null) {
						process.destroy();
					}
				}
			}
		}).start();
		if(isNoPor){
			Toast.makeText(context, "�����ļ��ѱ�����SDCard/ScreenImages/Ŀ¼��",
					Toast.LENGTH_LONG).show();
		}
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
}