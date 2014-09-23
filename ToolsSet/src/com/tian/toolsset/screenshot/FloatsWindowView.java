package com.tian.toolsset.screenshot;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tian.toolsset.R;
import com.tian.toolsset.ToolsActivity;

public class FloatsWindowView extends RelativeLayout implements View.OnTouchListener ,View.OnClickListener{

	private Context mContext = null;
	private WindowManager mWindowMgr = null;
	private WindowManager.LayoutParams mWindowMgrParams = null;
	
	private int iPosX = 0;
	private int iPosY = 0;
	private int iLastPosX = 0;
	private int iLastPosY = 0;
	private boolean bMoved = false;
	private Button drag;
	private Button shot;
	private Button over;
	private View view ;
	public FloatsWindowView(Context context) {
		this(context, null, 0);
	}
	public FloatsWindowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public FloatsWindowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mContext = context;
		mWindowMgr = (WindowManager)getContext().getApplicationContext().getSystemService("window");
		mWindowMgrParams = new WindowManager.LayoutParams();
		mbParams(); 
	    view = LayoutInflater.from(context).inflate(R.layout.activity_shot, this);
		drag = (Button)view.findViewById(R.id.button1);
		shot = (Button)view.findViewById(R.id.button2);
		over = (Button)view.findViewById(R.id.button3);
		drag.setOnTouchListener(this);
		shot.setOnTouchListener(this);
		over.setOnTouchListener(this);
		
		drag.setOnClickListener(this);
		shot.setOnClickListener(this);
		over.setOnClickListener(this);
		
		
		
	}
	
	private void mbParams(){
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mWindowMgrParams.x = dm.widthPixels - 150;
		mWindowMgrParams.y = 300;
		mWindowMgrParams.width = LayoutParams.MATCH_PARENT;
		mWindowMgrParams.height = LayoutParams.MATCH_PARENT;
	}
	private void msParams(){
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mWindowMgrParams.x = dm.widthPixels - 150;
		mWindowMgrParams.y = 300;
		mWindowMgrParams.width = 300;
		mWindowMgrParams.height = 400;
	}

	private void updatePostion(int x, int y){
		mWindowMgrParams.type = 2002;
		mWindowMgrParams.format = 1;
		mWindowMgrParams.flags = 40;
		mWindowMgrParams.gravity = Gravity.LEFT | Gravity.BOTTOM ;
		mWindowMgrParams.x += x;
		mWindowMgrParams.y += y;
		mWindowMgr.updateViewLayout(this, mWindowMgrParams);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			iPosX = (int)event.getX();
			iPosY = (int)event.getY();
			bMoved = false;
			break;
			
		case MotionEvent.ACTION_MOVE:
			iLastPosX = (int)event.getX();
			iLastPosY = (int)event.getY();
			int distanceX = iLastPosX - iPosX;
			int distanceY = iLastPosY - iPosY;
			bMoved = true;
			updatePostion(distanceX,distanceY);
			break;
			
		case MotionEvent.ACTION_UP:
			if(!bMoved){
			}
			break;
			
		default:
			break;
		}
		return false;
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:
			Toast.makeText(mContext, "button1", 1000).show();
			break;
		case R.id.button2:
			ScreenShot.shoot(mContext);
			msParams();
			break;
		case R.id.button3:
			mWindowMgr.removeView(view);
			view = null;
			break;
		}
		
	}
	
}
