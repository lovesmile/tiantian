package com.tian.toolsset.utils;

import com.tian.toolsset.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
	private Toast toast = null;
	private Context mContext;
	AlertDialog.Builder builder;
	AlertDialog dialog;

	public ToastUtils(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public void show(String str, int dur) {
		Toast.makeText(mContext, str, dur).show();
	}

	public void show(String str, int dur, int loc1, int loc2) {
		toast = Toast.makeText(mContext, str, dur);
		toast.setGravity(loc1 | loc2, 0, 0);
		toast.show();
	}

	public void show(String str, int dur, int loc) {
		toast = Toast.makeText(mContext, str, dur);
		toast.setGravity(loc, 0, 0);
		toast.show();
	}

	public void show(String str, int dur, int loc, int x, int y) {
		toast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, x, -y);
		LinearLayout layout = (LinearLayout) toast.getView();
		ImageView image = new ImageView(mContext);
		layout.addView(image, 0);
		toast.show();
	}

	public void show(String str) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.toast_number, null);
		TextView txt = (TextView) view.findViewById(R.id.txt_no);
		txt.setText(str);
		toast = new Toast(mContext);
		toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
		toast.setDuration(500);
		toast.setView(view);
		toast.show();
	}

	public void show() {
		View view1 = LayoutInflater.from(mContext).inflate(
				R.layout.toast_number, null);
		builder = new AlertDialog.Builder(mContext);
		builder.setView(view1);
		dialog = builder.create();
		dialog.show();
	}

	// private void myToast(){
	// Timer timer = new Timer();
	// timer.schedule(new TimerTask() {
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// initToast();
	// }
	// }, 10);
	// }
	// private void initToast(){
	// toast.show();
	// }

}
