package com.tian.toolsset.contacts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tian.toolsset.R;
import com.tian.toolsset.ToolsActivity;

public class ContactsActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	Handler mHandler = null;
	boolean isActive;
	VCardIO vcarIO = null;
	int mLastProgress;
	CheckBox mReplaceOnImport = null;
	Button importButton = null;
	Button exportButton = null;
	ProgressDialog progressDlg = null;
	private String importing = "";
	private ImageView ivback;
	private MyHandler handler = new MyHandler();
	public static String FILE_NOT_EXIST = "file_not_exist";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.export_contacts);
        mHandler = new Handler();
        vcarIO = new VCardIO(this);
		// 显示进度条
		progressDlg = new ProgressDialog(this);
		progressDlg.setCancelable(false);
		progressDlg.setProgress(10000);

		importButton = (Button) findViewById(R.id.ImportButton);
		exportButton = (Button) findViewById(R.id.ExportButton);
		mReplaceOnImport = ((CheckBox) findViewById(R.id.ReplaceOnImport));
		
		OnClickListener listenImport = new OnClickListener() {
			public void onClick(View v) {
				if (vcarIO != null) {
					String fileName = ((EditText) findViewById(R.id.ImportFile))
							.getText().toString();
					// 更新进度
					progressDlg.show();
					importing = "正在导入,请稍后...";
					updateProgress(0);
					vcarIO.doImport(fileName, mReplaceOnImport.isChecked(),
							ContactsActivity.this,handler);
				}
			}
		};

		OnClickListener listenExport = new OnClickListener() {
			public void onClick(View v) {
				if (vcarIO != null) {
					String fileName = ((EditText) findViewById(R.id.ExportFile))
							.getText().toString();
					// 更新进度
					progressDlg.show();
					importing = "正在到出,请稍后...";
					updateProgress(0);
					vcarIO.doExport(fileName, ContactsActivity.this,handler);
				}
			}
		};
		importButton.setOnClickListener(listenImport);
		exportButton.setOnClickListener(listenExport);
		
		ivback = (ImageView)findViewById(R.id.iv_back);
		ivback.setOnClickListener(this);
    }
    /**
	 * 更新进度条
	 * 
	 * @param progress
	 *            进度
	 */
	public void updateProgress(final int progress) {
		mHandler.post(new Runnable() {
			public void run() {
				progressDlg.setProgress(progress * 100);
				progressDlg.setMessage(importing + progress + "%");
				if (progress == 100) {
					progressDlg.cancel();
				}

			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.iv_back:
			onBackPressed();
			break;
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		progressDlg.cancel();
		Intent it = new Intent();
		it.setClass(ContactsActivity.this, ToolsActivity.class);
		startActivity(it);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		super.onBackPressed();
	}
	 /**
	    * 接受消息,处理消息 ,此Handler会与当前主线程一块运行
	    * */

	    class MyHandler extends Handler {
	        public MyHandler() {
	        }

	        public MyHandler(Looper L) {
	            super(L);
	        }

	        // 子类必须重写此方法,接受数据
	        @Override
	        public void handleMessage(Message msg) {
	            // TODO Auto-generated method stub
	            super.handleMessage(msg);
	            // 此处可以更新UI
                switch(msg.what){
                case 1:
                	progressDlg.cancel();
                	Toast.makeText(ContactsActivity.this, "您需要先备份通讯录", Toast.LENGTH_SHORT).show();
                	break;
                case 2:
                	progressDlg.cancel();
                	Toast.makeText(ContactsActivity.this, "您的通讯录还没有联系人", Toast.LENGTH_SHORT).show();
                	break;
                }
	        }
	    }
	
	
	
	
}