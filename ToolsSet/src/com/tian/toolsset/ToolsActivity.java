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
		// ��ǰӦ�õĴ���ִ��Ŀ¼
		if (RootPermissionGet.upgradeRootPermission(getPackageCodePath())) {
			isRoot = true;
		}
	}

	private void init() {
		gv_menu = (GridView) this.findViewById(R.id.gv_menu);
		List<MenuItem> menus = new ArrayList<MenuItem>();
		menus.add(new MenuItem(R.drawable.menu_wifi, "WiFi��Ϣ",
				"���ӹ���wifi��Ϣ root"));
		menus.add(new MenuItem(R.drawable.menu_rboot, "����", "��������"));
		menus.add(new MenuItem(R.drawable.menu_cut, "��ͼ", "��root ������ͼ ��ͼ"));
		menus.add(new MenuItem(R.drawable.menu_file, "����", "����/����ͨѶ¼"));
		menus.add(new MenuItem(R.drawable.menu_light, "�ֵ�", "���ֵ�"));
		menus.add(new MenuItem(R.drawable.menu_qrcode, "��ά��", "��ά��ɨ��/����"));
		// ����margin
		int margin = (int) (getResources().getDisplayMetrics().density * 14 * 13 / 9);
		MenuItemAdapter adapter = new MenuItemAdapter(this, menus, margin);
		gv_menu.setAdapter(adapter);
		gv_menu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					// ��ǰӦ�õĴ���ִ��Ŀ¼
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
							.setTitle("����")
							.setMessage("ȷ������?")
							.setPositiveButton("ȡ��", null)
							.setNegativeButton("ȷ��",
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
														"����ʧ��.",
														Toast.LENGTH_SHORT)
														.show();
											}
										}
									}).show();
					break;
				case 2:
					// ��ǰӦ�õĴ���ִ��Ŀ¼
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
		new AlertDialog.Builder(ToolsActivity.this).setTitle("����")
				.setMessage("û��rootȨ�ޣ��ݲ�֧�ִ˹���,�������root����Ӧ���´�����ʱ����Ȩ�޼���!")
				.setPositiveButton("ȷ��", null).show();
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
