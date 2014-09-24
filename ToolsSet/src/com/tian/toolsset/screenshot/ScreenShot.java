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
	// 图片存储路径
	public static String SAVE_PATH = getSDCardPath() + "/ScreenImages/";
	public static String SAVE_CACHE_PATH = getSDCardPath()
			+ "/ScreenImages/cache/";
	private static File baseFile;
	// 文件
	public static String filename;
	private ShakeListener mShakeListener = null;
	private Vibrator mVibrator;
	private Context mContext;
	private MyHandler handler = new MyHandler();
	private static ArrayList<String> paths = new ArrayList<String>();
	private long lastTime = 0;
	private boolean isShow =false;
	/**
	 * 截图控制条件：1 单张 ，2 连续
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
	 * // View是你需要截图的View View view = activity.getWindow().getDecorView();
	 * view.setDrawingCacheEnabled(true); view.buildDrawingCache(); Bitmap b1 =
	 * view.getDrawingCache();
	 * 
	 * // 获取状态栏高度 Rect frame = new Rect();
	 * activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
	 * int statusBarHeight = frame.top; // 获取屏幕长和高 int width =
	 * activity.getWindowManager().getDefaultDisplay().getWidth(); int height =
	 * activity.getWindowManager().getDefaultDisplay() .getHeight(); // 去掉标题栏
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
	 * Toast.makeText(context, "截屏文件已保存至SDCard/ScreenImages/目录下",
	 * Toast.LENGTH_LONG).show(); }
	 * 
	 * public static void shoot(Activity context) { filename = SAVE_PATH +
	 * System.currentTimeMillis() + ".png"; if (filename == null) { return; }
	 * File file = new File(filename); if (!file.getParentFile().exists()) {
	 * file.getParentFile().mkdirs(); }
	 * ScreenShot.savePic(ScreenShot.takeScreenShot(context));
	 * Toast.makeText(context, "截屏文件已保存至SDCard/ScreenImages/目录下",
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
						Toast.makeText(mContext, "截图间隔时间太短，偶会忙不过来的O(∩_∩)O", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(mContext, "停止截图，图片已保存到SDCard/ScreenImages/目录下",
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
							Toast.makeText(mContext, "长图生成失败",
									Toast.LENGTH_SHORT).show();
							if (baseFile.exists()) {
								deleteFile(baseFile);
							}
						}

					}
				}).start();
				Toast.makeText(mContext, "正在生成长图", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "没有开始截图", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	/**
	 * 截图
	 */
	public void shoot(int action) {
		maction = action;
		if (count > 15) {
			Toast.makeText(mContext, "连续截图已达15次上限", Toast.LENGTH_SHORT).show();
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
	 * 获取SDCard的目录路径功能
	 * 
	 * @return
	 */
	private static String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}

	/**
	 * 获取图片地址列表
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
	 * 读取sdcard文件夹中的图片，并生成略缩图
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
					options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
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

	// 将SD卡文件删除
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			}
			// 如果它是一个目录
			else if (file.isDirectory()) {
				// 声明目录下所有的文件 files[];
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		}
	}

	/**
	 * 拼接图片
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
	 * 接受消息,处理消息 ,此Handler会与当前主线程一块运行
	 * */

	public class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 此处可以更新UI
			switch (msg.what) {
			case 0:
				Toast.makeText(mContext, "截图失败", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(mContext, ""+count+"", 500).show();;
				break;
			case 2:
				Toast.makeText(mContext, "长图已保存到SDCard/ScreenImages/目录下",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}