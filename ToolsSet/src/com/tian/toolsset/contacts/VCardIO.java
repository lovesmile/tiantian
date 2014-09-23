package com.tian.toolsset.contacts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.widget.Toast;

public class VCardIO {
	private Context context;

	public VCardIO(Context context) {
		this.context = context;
	}

	/**
	 * ������ϵ����Ϣ
	 * 
	 * @param fileName
	 *            Ҫ������ļ�
	 * @param replace
	 *            �Ƿ��滻������ϵ��
	 * @param activity
	 *            ������
	 * @param progressDlg 
	 */
	public void doImport(final String fileName, final boolean replace,
			final ContactsActivity activity, final Handler handler) {
		new Thread() {
			@Override
			public void run() {
				try {

					File vcfFile = new File(fileName);
                    if(!vcfFile.exists()){
                    	handler.sendEmptyMessage(1);
                    	return;
                    }
					final BufferedReader vcfBuffer = new BufferedReader(
							new FileReader(fileName));
					final long maxlen = vcfFile.length();

					// ��ִ̨�е������
					new Thread(new Runnable() {
						public void run() {
							long importStatus = 0;
							Contact parseContact = new Contact();
							try {
								long ret = 0;
								do {
									ret = parseContact.parseVCard(vcfBuffer);
									if (ret < 0) {
										break;
									}
									parseContact.addContact(
											context.getApplicationContext(), 0,
											replace);
									importStatus += parseContact.getParseLen();

									// ���½�����
									activity.updateProgress((int) (100 * importStatus / maxlen));

								} while (true);
								activity.updateProgress(100);
							} catch (Exception e) {
							}
						}
					}).start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * ������ϵ����Ϣ
	 * 
	 * @param fileName
	 *            ��ŵ�����Ϣ���ļ�
	 * @param activity
	 *            ������
	 * @param progressDlg 
	 */
	public void doExport(final String fileName, final ContactsActivity activity, final Handler handler) {
		new Thread() {
			@Override
			public void run() {
				try {
					final BufferedWriter vcfBuffer = new BufferedWriter(
							new FileWriter(fileName));

					final ContentResolver cResolver = context
							.getContentResolver();
					String[] projection = { ContactsContract.Contacts._ID };
					final Cursor allContacts = cResolver.query(
							ContactsContract.Contacts.CONTENT_URI, projection,
							null, null, null);
					if (!allContacts.moveToFirst()) {
						allContacts.close();
					    handler.sendEmptyMessage(2);
						return;
					}

					final long maxlen = allContacts.getCount();
					// �߳���ִ�е���
					new Thread(new Runnable() {
						public void run() {
							long exportStatus = 0;
							String id = null;
							Contact parseContact = new Contact();
							try {
								do {
									id = allContacts.getString(0);
									parseContact.getContactInfoFromPhone(id, cResolver);
									parseContact.writeVCard(vcfBuffer);
									++exportStatus;
									// ���½�����
									activity.updateProgress((int) (100 * exportStatus / maxlen));
								} while (allContacts.moveToNext());
								activity.updateProgress(100);
								vcfBuffer.close();
								allContacts.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
}
