package com.tian.toolsset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tian.toolsset.adapter.MenuItemAdapter;
import com.tian.toolsset.contacts.ContactsActivity;
import com.tian.toolsset.qrcode.QRCodeActivity;
import com.tian.toolsset.screenshot.ScreenShot;
import com.tian.toolsset.screenshot.ScreenShotActivity;
import com.tian.toolsset.utils.MenuItem;
import com.tian.toolsset.utils.RootPermissionGet;
import com.tian.toolsset.utils.ToolsBroadcastReceiver;
import com.tian.toolsset.utils.ToolsConstants;
import com.tian.toolsset.wifi.WifiInfoActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class ToolsActivity extends Activity {
	private GridView gv_menu;
	private ToolsBroadcastReceiver mMyBroadcastReceiver = new ToolsBroadcastReceiver();
	private Intent intent;
	private boolean isLightOn = false;
	private boolean isRoot = false;
	private File baseFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ToolsConstants.START_LIGHT_ACTION);
		intentFilter.addAction(ToolsConstants.OFF_LIGHT_ACTION);
		registerReceiver(mMyBroadcastReceiver, intentFilter);
		baseFile = new File(ScreenShot.SAVE_CACHE_PATH);
		if (baseFile.exists()) {
			ScreenShot.deleteFile(baseFile);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 当前应用的代码执行目录
		if (RootPermissionGet.upgradeRootPermission(getPackageCodePath())) {
			isRoot = true;
		}
	}

	private void init() {
		gv_menu = (GridView) this.findViewById(R.id.gv_menu);
		List<MenuItem> menus = new ArrayList<MenuItem>();
		menus.add(new MenuItem(R.drawable.menu_wifi, "WiFi信息",
				"连接过的wifi信息 root"));
		menus.add(new MenuItem(R.drawable.menu_rboot, "重启", "单击重启"));
		menus.add(new MenuItem(R.drawable.menu_cut, "截图", "需root 连续截图 长图"));
		menus.add(new MenuItem(R.drawable.menu_file, "备份", "导出/导入通讯录"));
		menus.add(new MenuItem(R.drawable.menu_light, "手电", "打开手电"));
		menus.add(new MenuItem(R.drawable.menu_qrcode, "二维码", "二维码扫描/生成"));
		// 计算margin
		int margin = (int) (getResources().getDisplayMetrics().density * 14 * 13 / 9);
		MenuItemAdapter adapter = new MenuItemAdapter(this, menus, margin);
		gv_menu.setAdapter(adapter);
		gv_menu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					// 当前应用的代码执行目录
					if (isRoot) {
						intent = new Intent();
						intent.setClass(ToolsActivity.this,
								WifiInfoActivity.class);
						ToolsActivity.this.startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					} else {
						rootDialog();
					}
					break;
				case 1:
					new AlertDialog.Builder(ToolsActivity.this)
							.setTitle("警告")
							.setMessage("确定重启?")
							.setPositiveButton("取消", null)
							.setNegativeButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String cmd = "su -c reboot";
											try {
												Runtime.getRuntime().exec(cmd);
											} catch (Exception e) {
												Toast.makeText(
														getApplicationContext(),
														"重启失败.",
														Toast.LENGTH_SHORT)
														.show();
											}
										}
									}).show();
					break;
				case 2:
					// 当前应用的代码执行目录
					if (isRoot) {
						intent = new Intent();
						intent.setClass(ToolsActivity.this,
								ScreenShotActivity.class);
						ToolsActivity.this.startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					} else {
						rootDialog();
					}
					break;
				case 3:
					intent = new Intent();
					intent.setClass(ToolsActivity.this, ContactsActivity.class);
					ToolsActivity.this.startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					break;
				case 4:
					if (!isLightOn) {
						intent = new Intent();
						intent.setAction(ToolsConstants.START_LIGHT_ACTION);
						ToolsActivity.this.sendBroadcast(intent);
						isLightOn = true;
					} else {
						intent = new Intent();
						intent.setAction(ToolsConstants.OFF_LIGHT_ACTION);
						ToolsActivity.this.sendBroadcast(intent);
						isLightOn = false;
					}
					break;
				case 5:
					intent = new Intent();
					intent.setClass(ToolsActivity.this, QRCodeActivity.class);
					ToolsActivity.this.startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					break;
				}

			}
		});
	}

	private void rootDialog() {
		new AlertDialog.Builder(ToolsActivity.this).setTitle("警告")
				.setMessage("没有root权限，暂不支持此功能,如果您已root，在应用下次启动时赋予权限即可!")
				.setPositiveButton("确定", null).show();
	}

	@Override
	protected void onDestroy() {
		if (mMyBroadcastReceiver != null) {
			unregisterReceiver(mMyBroadcastReceiver);
		}
		if (isLightOn) {
			intent = new Intent();
			intent.setAction(ToolsConstants.OFF_LIGHT_ACTION);
			ToolsActivity.this.sendBroadcast(intent);
			isLightOn = false;
		}
		super.onDestroy();
	}
}
