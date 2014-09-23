package com.tian.toolsset;

import java.util.ArrayList;
import java.util.List;


import com.tian.toolsset.adapter.MenuItemAdapter;
import com.tian.toolsset.contacts.ContactsActivity;
import com.tian.toolsset.qrcode.QRCodeActivity;
import com.tian.toolsset.screenshot.FloatsWindowView;
import com.tian.toolsset.utils.MenuItem;
import com.tian.toolsset.utils.ToolsBroadcastReceiver;
import com.tian.toolsset.utils.ToolsConstants;
import com.tian.toolsset.wifi.WifiInfoActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class ToolsActivity extends Activity{
	private GridView gv_menu;
	private ToolsBroadcastReceiver mMyBroadcastReceiver = new ToolsBroadcastReceiver();
	private Intent intent;
	private boolean isLightOn = false;
	private static WindowManager mWindowMgr = null;
	private WindowManager.LayoutParams mWindowMgrParams = null;
	private static FloatsWindowView mFloatsWindowView = null;
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
	}

	private void init() {
		gv_menu = (GridView) this.findViewById(R.id.gv_menu);
		List<MenuItem> menus = new ArrayList<MenuItem>();
		menus.add(new MenuItem(R.drawable.menu_wifi, "WiFi信息", "wifi名字 密码"));
		menus.add(new MenuItem(R.drawable.menu_rboot, "重启", "单击重启"));
		menus.add(new MenuItem(R.drawable.menu_cut, "截图", "可截长图"));
		menus.add(new MenuItem(R.drawable.menu_file, "备份", "导出/导入通讯录"));
		menus.add(new MenuItem(R.drawable.menu_light, "手电", "打开手电"));
		menus.add(new MenuItem(R.drawable.menu_qrcode, "二维码", "二维码扫面/生成"));
		// 计算margin
		int margin = (int) (getResources().getDisplayMetrics().density * 14 * 13 / 9);
		MenuItemAdapter adapter = new MenuItemAdapter(this, menus, margin);
		gv_menu.setAdapter(adapter);
		gv_menu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				switch(position){
				case 0:
					intent = new Intent();
					intent.setClass(ToolsActivity.this, WifiInfoActivity.class);
					ToolsActivity.this.startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					break;
				case 1:
					 new AlertDialog.Builder(ToolsActivity.this)
			            .setTitle("警告")
			            .setMessage("确定重启?")
			            .setPositiveButton("取消", null)
			            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
			                @Override
			                public void onClick(DialogInterface dialog, int which) {
			                    String cmd = "su -c reboot";
			                    try {
			                        Runtime.getRuntime().exec(cmd);
			                    } catch (Exception e){
			                        Toast.makeText(getApplicationContext(), "Error! Fail to reboot.", Toast.LENGTH_SHORT).show();
			                    }
			                }
			            })
			            .show();
					 break;
				case 2:
//					Toast.makeText(getApplicationContext(), "摇动手机开始截屏", Toast.LENGTH_SHORT).show();
//					intent = new Intent();
//					intent.setAction(ToolsConstants.SHAKE_START_SCREEN_SHOT);
//					startService(intent);
					getWindowLayout();
					break;
				case 3:
					intent = new Intent();
					intent.setClass(ToolsActivity.this, ContactsActivity.class);
					ToolsActivity.this.startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					break;
				case 4:
					if(!isLightOn){
				    intent = new Intent();
			        intent.setAction(ToolsConstants.START_LIGHT_ACTION);
			        ToolsActivity.this.sendBroadcast(intent);
			        isLightOn = true;
					}else{
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
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					break;
				}
				
			}
		});
	}
	 @Override
	    protected void onDestroy() {
	        if (mMyBroadcastReceiver != null) {
	            unregisterReceiver(mMyBroadcastReceiver);
	        }
	        if(isLightOn){
	        	intent = new Intent();
		        intent.setAction(ToolsConstants.OFF_LIGHT_ACTION);
		        ToolsActivity.this.sendBroadcast(intent);
		        isLightOn = false;
	        }
	        super.onDestroy();
	    }
	 private void initParams(){
			DisplayMetrics dm = getResources().getDisplayMetrics();
			mWindowMgrParams.x = dm.widthPixels - 150;
			mWindowMgrParams.y = 300;
			mWindowMgrParams.width = 300;
			mWindowMgrParams.height = 400;
		}

		private void getWindowLayout(){
			if(mFloatsWindowView == null){
				mWindowMgr = (WindowManager)getBaseContext().getSystemService(Context.WINDOW_SERVICE);
				mWindowMgrParams = new WindowManager.LayoutParams();
				
				/*
				 *  2003 在指悬浮在所有界面之上
				 *  (4.0+系统中，在下拉菜单下面，而在2.3中，在上拉菜单之上)
				 */
				mWindowMgrParams.type = 2003;
				mWindowMgrParams.format = 1;
				
				/*
		         * 代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;
		         * 40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
		         */
				mWindowMgrParams.flags = 40;
				mWindowMgrParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
				initParams();
				
				mFloatsWindowView = new FloatsWindowView(this);
				mWindowMgr.addView(mFloatsWindowView, mWindowMgrParams);
			}
		}
}
