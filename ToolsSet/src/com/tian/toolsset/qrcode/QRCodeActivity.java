package com.tian.toolsset.qrcode;

import com.google.zxing.WriterException;
import com.tian.toolsset.R;
import com.tian.toolsset.ToolsActivity;
import com.zxing.encoding.EncodingHandler;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QRCodeActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	private TextView resultTextView;
	private EditText qrStrEditText;
	private ImageView qrImgImageView;
	private ImageView ivback;
	private ImageView ivQrResult;
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private Button generateQRCodeButton;
	private Button scanBarCodeButton;
	private Button copyButton;
	private Bundle bundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);

		resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
		qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
		qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
		ivQrResult = (ImageView) this.findViewById(R.id.iv_qr_result);
		copyButton = (Button) this.findViewById(R.id.btn_copy);
		copyButton.setOnClickListener(this);
		ivback = (ImageView) findViewById(R.id.iv_back);
		ivback.setOnClickListener(this);
		scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
		scanBarCodeButton.setOnClickListener(this);
		generateQRCodeButton = (Button) this.findViewById(R.id.btn_add_qrcode);
		generateQRCodeButton.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 处理扫描结果（在界面上显示）
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
			    bundle = data.getExtras();
				// 显示扫描到的内容
				resultTextView.setText(bundle.getString("result"));
				// 显示
				ivQrResult.setVisibility(View.VISIBLE);
				ivQrResult.setImageBitmap((Bitmap) data
						.getParcelableExtra("bitmap"));
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			onBackPressed();
			break;
		case R.id.btn_scan_barcode:
			// 打开扫描界面扫描条形码或二维码
			Intent openCameraIntent = new Intent(QRCodeActivity.this,
					CaptureActivity.class);
			startActivityForResult(openCameraIntent, SCANNIN_GREQUEST_CODE);
			break;
		case R.id.btn_add_qrcode:
			try {
				String contentString = qrStrEditText.getText().toString();
				if (!contentString.equals("")) {
					// 根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
					Bitmap qrCodeBitmap = EncodingHandler.createQRCode(
							contentString, 500);
					qrImgImageView.setImageBitmap(qrCodeBitmap);
				} else {
					Toast.makeText(QRCodeActivity.this,
							"转换内容不能为空", Toast.LENGTH_SHORT).show();
				}

			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.btn_copy:
			if(bundle!=null){
				copyButton.setVisibility(View.VISIBLE);
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(resultTextView.getText().toString());
				Toast.makeText(QRCodeActivity.this,
						"复制完成", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(QRCodeActivity.this,
						"复制内容不能为空", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent it = new Intent();
		it.setClass(QRCodeActivity.this, ToolsActivity.class);
		startActivity(it);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		super.onBackPressed();
	}
}