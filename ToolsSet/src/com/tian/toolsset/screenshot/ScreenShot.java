package com.tian.toolsset.screenshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ScreenShot {
	// 图片存储路径
	public static String SAVE_PATH = getSDCardPath() + "/ScreenImages/";
	// 文件
	public static String filename = SAVE_PATH + System.currentTimeMillis()
			+ ".png";

	private static Bitmap takeScreenShot(Activity activity,int width,int height) {  
		
		  // View是你需要截图的View 
		  View view = activity.getWindow().getDecorView();
		  view.setDrawingCacheEnabled(true); 
		  view.buildDrawingCache(); Bitmap
		  b1 = view.getDrawingCache();
		  
		  // 获取状态栏高度 
		  Rect frame = new Rect();
		  activity.getWindow().getDecorView(
		  ).getWindowVisibleDisplayFrame(frame); 
		  int statusBarHeight = frame.top; // 获取屏幕长和高 
//		  int width = activity.getWindowManager().getDefaultDisplay().getWidth(); 
//		  int height = activity.getWindowManager().getDefaultDisplay().getHeight(); 
		  // 去掉标题栏
		  Bitmap b = Bitmap.createBitmap(b1, 0,statusBarHeight, width, height - statusBarHeight);
		  view.destroyDrawingCache();
    	
        return b;  
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

	public static void shoot(Activity context, int width, int height) {
		if (filename == null) {
			return;
		}
		File file = new File(filename);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		ScreenShot.savePic(ScreenShot.takeScreenShot(context,width, height));
		Toast.makeText(context, "截屏文件已保存至SDCard/ScreenImages/目录下",
				Toast.LENGTH_LONG).show();
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
}