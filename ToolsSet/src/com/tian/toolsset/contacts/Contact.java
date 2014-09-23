package com.tian.toolsset.contacts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Contact {
	static final String NL = "\r\n";
	static final String IMPROP = "X-IM-PROTO";
	static final String TYPE_PARAM = "TYPE";
	static final String PROTO_PARAM = "PROTO";
	static final String[] PROTO = { "AIM", // ContactsContract.CommonDataKinds.Im.PROTOCOL_AIM
											// = 0
			"MSN", // ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN = 1
			"YAHOO", // ContactsContract.CommonDataKinds.Im.PROTOCOL_YAHOO = 2
			"SKYPE", // ContactsContract.CommonDataKinds.Im.PROTOCOL_SKYPE = 3
			"QQ", // ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ = 4
			"GTALK", // ContactsContract.CommonDataKinds.Im.PROTOCOL_GOOGLE_TALK
						// = 5
			"ICQ", // ContactsContract.CommonDataKinds.Im.PROTOCOL_ICQ = 6
			"JABBER" // ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER = 7
	};

	// ��ϵ����Ϣ
	long parseLen;
	static final String BIRTHDAY_FIELD = "Birthday:";
	private List<RowData> phones;// �绰���룬���
	private List<RowData> emails;// email��ַ
	private List<RowData> addrs;// סַ
	private List<RowData> ims;// ��ʱͨѶ������QQ,MSN
	private List<OrgData> orgs;// ������λ
	private PeopleData peopleData;
	private String notes;// ��ע

	static class RowData {
		public RowData(int type, String data, boolean preferred,
				String customType) {
			this.type = type;
			this.data = data;
			this.preferred = preferred;
			this.customType = customType;
			protocol = null;
		}

		public RowData(int type, String data, boolean preferred) {
			this(type, data, preferred, null);
		}

		public int type;
		public String data;
		public boolean preferred;
		public String customType;
		public String protocol;
	}

	static class PeopleData {
		public String _id;
		public String displayName;
		public String familyNmae;
		public String midName;
		public String givenName;

		public PeopleData() {
			clear();
		}

		public void clear() {
			this._id = null;
			this.displayName = null;
			this.familyNmae = null;
			this.midName = null;
		}
	}

	static class OrgData {
		public OrgData(int type, String title, String company, String customType) {
			this.type = type;
			this.title = title;
			this.company = company;
			this.customType = customType;
		}

		public int type;
		public String title;
		public String company;
		public String customType;
	}

	Hashtable<String, handleProp> propHandlers;

	interface handleProp {
		void parseProp(final String propName, final Vector<String> propVec,
				final String val);
	}

	private void initialize() {
		reset();
		propHandlers = new Hashtable<String, handleProp>();

		handleProp simpleValue = new handleProp() {
			public void parseProp(final String propName,
					final Vector<String> propVec, final String val) {
				if (propName.equals("FN")) {
					peopleData.displayName = val;
				} else if (propName.equals("NOTE")) {
					notes = val;
				} else if (propName.equals("N")) {
					String[] names = StringUtils.split(val, ";");
					if (names[1] != null || names[1] != "") {
						peopleData.familyNmae = names[0];
						peopleData.givenName = names[1];
					} else {
						String[] names2 = StringUtils.split(names[0], " ");
						peopleData.familyNmae = names2[0];
						if (names2.length > 1)
							peopleData.givenName = names2[1];
					}
					if (peopleData.displayName == null) {
						StringBuffer fullname = new StringBuffer();
						if (peopleData.familyNmae != null)
							fullname.append(peopleData.familyNmae);
						if (peopleData.givenName != null) {
							if (peopleData.familyNmae != null) {
								fullname.append(" ");
							}
							fullname.append(peopleData.givenName);
						}
						peopleData.displayName = fullname.toString();
					}
				}
			}
		};

		propHandlers.put("FN", simpleValue);
		propHandlers.put("NOTE", simpleValue);
		propHandlers.put("N", simpleValue);

		handleProp orgHandler = new handleProp() {

			@Override
			public void parseProp(String propName, Vector<String> propVec,
					String val) {
				String label = null;
				for (String prop : propVec) {
					String[] propFields = StringUtils.split(prop, "=");
					if (propFields[0].equalsIgnoreCase(TYPE_PARAM)
							&& propFields.length > 1) {
						label = propFields[1];
					}
				}
				if (propName.equals("TITLE")) {
					boolean setTitle = false;
					for (OrgData org : orgs) {
						if (label == null && org.customType != null)
							continue;
						if (label != null && !label.equals(org.customType))
							continue;

						if (org.title == null) {
							org.title = val;
							setTitle = true;
							break;
						}
					}
					if (!setTitle) {
						orgs.add(new OrgData(
								label == null ? Organization.TYPE_WORK
										: Organization.TYPE_CUSTOM, val, null,
								label));
					}
				} else if (propName.equals("ORG")) {
					String[] orgFields = StringUtils.split(val, ";");
					boolean setCompany = false;
					for (OrgData org : orgs) {
						if (label == null && org.customType != null)
							continue;
						if (label != null && !label.equals(org.customType))
							continue;

						if (org.company == null) {
							org.company = val;
							setCompany = true;
							break;
						}
					}
					if (!setCompany) {
						orgs.add(new OrgData(
								label == null ? Organization.TYPE_WORK
										: Organization.TYPE_CUSTOM, null,
								orgFields[0], label));
					}
				}
			}
		};

		propHandlers.put("ORG", orgHandler);
		propHandlers.put("TITLE", orgHandler);

		propHandlers.put("TEL", new handleProp() {
			public void parseProp(final String propName,
					final Vector<String> propVec, final String val) {
				String label = null;
				int subtype = Phone.TYPE_OTHER;
				boolean preferred = false;
				for (String prop : propVec) {
					if (prop.equalsIgnoreCase("HOME")
							|| prop.equalsIgnoreCase("VOICE")) {
						if (subtype != Phone.TYPE_FAX_HOME)
							subtype = Phone.TYPE_HOME;
					} else if (prop.equalsIgnoreCase("WORK")) {
						if (subtype == Phone.TYPE_FAX_HOME) {
							subtype = Phone.TYPE_FAX_WORK;
						} else
							subtype = Phone.TYPE_WORK;
					} else if (prop.equalsIgnoreCase("CELL")) {
						subtype = Phone.TYPE_MOBILE;
					} else if (prop.equalsIgnoreCase("FAX")) {
						if (subtype == Phone.TYPE_WORK) {
							subtype = Phone.TYPE_FAX_WORK;
						} else
							subtype = Phone.TYPE_FAX_HOME;
					} else if (prop.equalsIgnoreCase("PAGER")) {
						subtype = Phone.TYPE_PAGER;
					} else if (prop.equalsIgnoreCase("PREF")) {
						preferred = true;
					} else {
						String[] propFields = StringUtils.split(prop, "=");

						if (propFields.length > 1
								&& propFields[0].equalsIgnoreCase(TYPE_PARAM)) {
							label = propFields[1];
							subtype = Phone.TYPE_CUSTOM;
						}
					}
				}
				phones.add(new RowData(subtype, toCanonicalPhone(val),
						preferred, label));
			}
		});

		propHandlers.put("ADR", new handleProp() {
			public void parseProp(final String propName,
					final Vector<String> propVec, final String val) {
				boolean preferred = false;
				String label = null;
				int subtype = Email.TYPE_WORK;
				for (String prop : propVec) {
					if (prop.equalsIgnoreCase("WORK")) {
						subtype = Email.TYPE_WORK;
					} else if (prop.equalsIgnoreCase("HOME")) {
						subtype = Email.TYPE_HOME;
					} else if (prop.equalsIgnoreCase("PREF")) {
						preferred = true;
					} else {
						String[] propFields = StringUtils.split(prop, "=");

						if (propFields.length > 1
								&& propFields[0].equalsIgnoreCase(TYPE_PARAM)) {
							label = propFields[1];
							subtype = Email.TYPE_CUSTOM;
						}
					}
				}
				if (val != null) {
					addrs.add(new RowData(subtype, val, preferred, label));
				}
			}
		});

		propHandlers.put("EMAIL", new handleProp() {
			public void parseProp(final String propName,
					final Vector<String> propVec, final String val) {
				boolean preferred = false;
				String label = null;
				int subtype = Email.TYPE_HOME;
				for (String prop : propVec) {
					if (prop.equalsIgnoreCase("PREF")) {
						preferred = true;
					} else if (prop.equalsIgnoreCase("WORK")) {
						subtype = Email.TYPE_WORK;
					} else {
						String[] propFields = StringUtils.split(prop, "=");

						if (propFields.length > 1
								&& propFields[0].equalsIgnoreCase(TYPE_PARAM)) {
							label = propFields[1];
							subtype = Email.TYPE_CUSTOM;
						}
					}
				}
				emails.add(new RowData(subtype, val, preferred, label));
			}
		});

		propHandlers.put(IMPROP, new handleProp() {
			public void parseProp(final String propName,
					final Vector<String> propVec, final String val) {
				boolean preferred = false;
				String label = null;
				String proto = null;
				int subtype = Im.TYPE_HOME;
				for (String prop : propVec) {
					if (prop.equalsIgnoreCase("PREF")) {
						preferred = true;
					} else if (prop.equalsIgnoreCase("WORK")) {
						subtype = Im.TYPE_WORK;
					} else {
						String[] propFields = StringUtils.split(prop, "=");
						if (propFields.length > 1) {
							if (propFields[0].equalsIgnoreCase(PROTO_PARAM)) {
								proto = propFields[1];
							} else if (propFields[0]
									.equalsIgnoreCase(TYPE_PARAM)) {
								label = propFields[1];
							}
						}
					}
				}
				RowData newRow = new RowData(subtype, val, preferred, label);
				newRow.protocol = proto;
				ims.add(newRow);
			}
		});
	}

	/**
	 * ���ñ���
	 */
	private void reset() {
		if (peopleData == null) {
			peopleData = new PeopleData();
		} else {
			peopleData.clear();
		}
		parseLen = 0;
		notes = null;
		if (phones == null)
			phones = new ArrayList<RowData>();
		else
			phones.clear();
		if (emails == null)
			emails = new ArrayList<RowData>();
		else
			emails.clear();
		if (addrs == null)
			addrs = new ArrayList<RowData>();
		else
			addrs.clear();
		if (orgs == null)
			orgs = new ArrayList<OrgData>();
		else
			orgs.clear();
		if (ims == null)
			ims = new ArrayList<RowData>();
		else
			ims.clear();
	}

	/**
	 * ���캯��
	 */
	public Contact() {
		initialize();
	}

	final static Pattern[] phonePatterns = {
			Pattern.compile("[+](1)(\\d\\d\\d)(\\d\\d\\d)(\\d\\d\\d\\d.*)"),
			Pattern.compile("[+](972)(2|3|4|8|9|50|52|54|57|59|77)(\\d\\d\\d)(\\d\\d\\d\\d.*)"), };

	/**
	 * ת���绰�����ʽ����"-"�ָ�
	 * 
	 * @param phone
	 *            �绰����
	 * @return �ָ��ĵ绰����
	 */
	String toCanonicalPhone(String phone) {
		for (final Pattern phonePattern : phonePatterns) {
			Matcher m = phonePattern.matcher(phone);
			if (m.matches()) {
				return "+" + m.group(1) + "-" + m.group(2) + "-" + m.group(3)
						+ "-" + m.group(4);
			}
		}

		return phone;
	}

	/**
	 * ������ϵ�˱�ʶ
	 */
	public void setId(String id) {
		peopleData._id = id;
	}

	/**
	 * ��ȡ��ϵ�˱�ʶ
	 */
	public long getId() {
		return Long.parseLong(peopleData._id);
	}

	final static Pattern beginPattern = Pattern.compile("BEGIN:VCARD*",
			Pattern.CASE_INSENSITIVE);
	final static Pattern propPattern = Pattern.compile("([^:]+):(.*)");
	final static Pattern propParamPattern = Pattern
			.compile("([^;=]+)(=([^;]+))?(;|$)");
	final static Pattern base64Pattern = Pattern
			.compile("\\s*([a-zA-Z0-9+/]+={0,2})\\s*$");
	final static Pattern namePattern = Pattern
			.compile("(([^,]+),(.*))|((.*?)\\s+(\\S+))");
	final static Pattern birthdayPattern = Pattern.compile("^" + BIRTHDAY_FIELD
			+ ":\\s*([^;]+)(;\\s*|\\s*$)", Pattern.CASE_INSENSITIVE);

	public long getParseLen() {
		return parseLen;
	}

	/**
	 * ���ֻ��л�ȡͨѶ¼
	 */
	public void getContactInfoFromPhone(String id, ContentResolver cResolver) {
		reset();
		peopleData._id = id;
		// ȡ����ϵ��������_id
		getPeopleFields(cResolver);
		// ȡ����ϵ��NOTES
		getNoteFields(cResolver);
		// ȡ���绰����
		getPhoneFields(cResolver);
		// ȡ��email��ַ
		getEmailFields(cResolver);
		// ȡ����������
		getAddressFields(cResolver);
		// ȡ��IMS(��ʱͨѶ��ʽ)
		getImFields(cResolver);
		// ���ù�����λ
		getOrgFields(cResolver);
	}

	/**
	 * ��ͨѶ¼д�뵽vcf�ļ���
	 */
	public void writeVCard(Appendable vCardBuff) throws IOException {
		vCardBuff.append("BEGIN:VCARD").append(NL);
		vCardBuff.append("VERSION:2.1").append(NL);

		formatPeople(vCardBuff, peopleData);
		for (RowData email : emails) {
			formatEmail(vCardBuff, email);
		}

		for (RowData phone : phones) {
			formatPhone(vCardBuff, phone);
		}

		for (RowData addr : addrs) {
			formatAddr(vCardBuff, addr);
		}

		for (RowData im : ims) {
			formatIM(vCardBuff, im);
		}
		for (OrgData org : orgs) {
			formatOrg(vCardBuff, org);
		}
		if (notes != null && notes.length() > 0) {
			appendField(vCardBuff, "NOTE", notes);
		}
		vCardBuff.append("END:VCARD").append(NL);
	}

	/**
	 * ��email��ַ��ʽ��Ϊvcard�ֶ�
	 * 
	 * @param cardBuff
	 *            ����ʽ�����email��ַ���䵽�û���
	 * @param email
	 *            email��ַԭ��
	 */
	public static void formatEmail(Appendable cardBuff, RowData email)
			throws IOException {
		cardBuff.append("EMAIL");
		if (email.preferred)
			cardBuff.append(";PREF");

		if (email.customType != null) {
			cardBuff.append(";" + TYPE_PARAM + "=");
			cardBuff.append(email.customType);
		}
		switch (email.type) {
		case Email.TYPE_WORK:
			cardBuff.append(";WORK");
			break;
		case Email.TYPE_HOME:
			cardBuff.append(";HOME");
		}

		if (!StringUtils.isASCII(email.data))
			cardBuff.append(";CHARSET=UTF-8");

		cardBuff.append(":").append(email.data.trim()).append(NL);
	}

	/**
	 * ���绰�����ʽ��Ϊvcard�ֶ�
	 * 
	 * @param formatted
	 *            ��ʽ����ĵ绰�����ֶ�׷�ӵ�formatted����
	 * @param phone
	 *            �绰����ԭ��
	 */
	public static void formatPhone(Appendable formatted, RowData phone)
			throws IOException {
		formatted.append("TEL");
		if (phone.preferred)
			formatted.append(";PREF");

		if (phone.customType != null) {
			formatted.append(";" + TYPE_PARAM + "=");
			formatted.append(phone.customType);
		}
		switch (phone.type) {
		case Phone.TYPE_HOME:
			formatted.append(";VOICE");
			break;
		case Phone.TYPE_WORK:
			formatted.append(";VOICE;WORK");
			break;
		case Phone.TYPE_FAX_WORK:
			formatted.append(";FAX;WORK");
			break;
		case Phone.TYPE_FAX_HOME:
			formatted.append(";FAX;HOME");
			break;
		case Phone.TYPE_MOBILE:
			formatted.append(";CELL");
			break;
		case Phone.TYPE_PAGER:
			formatted.append(";PAGER");
			break;
		}

		if (!StringUtils.isASCII(phone.data))
			formatted.append(";CHARSET=UTF-8");
		formatted.append(":").append(phone.data.trim()).append(NL);
	}

	/**
	 * ����ַ��ʽ��Ϊvcard�ֶ�
	 * 
	 * @param formatted
	 *            ��ʽ����ĵ�ַ��׷�ӵ��û���
	 * @param addr
	 *            ��ַ
	 */
	public static void formatAddr(Appendable formatted, RowData addr)
			throws IOException {
		formatted.append("ADR");
		if (addr.preferred)
			formatted.append(";PREF");

		if (addr.customType != null) {
			formatted.append(";" + TYPE_PARAM + "=");
			formatted.append(addr.customType);
		}

		switch (addr.type) {
		case StructuredPostal.TYPE_HOME:
			formatted.append(";HOME");
			break;
		case StructuredPostal.TYPE_WORK:
			formatted.append(";WORK");
			break;
		}
		if (!StringUtils.isASCII(addr.data))
			formatted.append(";CHARSET=UTF-8");
		formatted.append(":").append(addr.data.replace(", ", ";").trim())
				.append(NL);
	}

	/**
	 * ��IM��ʽ��Ϊvcard�ֶ�
	 * 
	 * @param formatted
	 *            ��ʽ�����IM��׷�ӵ��û���
	 * @param addr
	 *            ��ʱͨѶ
	 */
	public static void formatIM(Appendable formatted, RowData im)
			throws IOException {
		formatted.append(IMPROP);
		if (im.preferred)
			formatted.append(";PREF");

		if (im.customType != null) {
			formatted.append(";" + TYPE_PARAM + "=");
			formatted.append(im.customType);
		}

		switch (im.type) {
		case Im.TYPE_HOME:
			formatted.append(";HOME");
			break;
		case Im.TYPE_WORK:
			formatted.append(";WORK");
			break;
		}

		if (im.protocol != null) {
			formatted.append(";").append(PROTO_PARAM).append("=")
					.append(im.protocol);
		}
		if (!StringUtils.isASCII(im.data))
			formatted.append(";CHARSET=UTF-8");
		formatted.append(":").append(im.data.trim()).append(NL);
	}

	/**
	 * ����ϵ�����ڵ�λ��ʽ��Ϊvcard�ֶ�
	 * 
	 * @param formatted
	 *            ��ʽ��������ݱ�׷�ӵ��û���
	 * @param addr
	 *            ��λ��Ϣ
	 */
	public static void formatOrg(Appendable formatted, OrgData org)
			throws IOException {
		if (org.company != null) {
			formatted.append("ORG");
			if (org.customType != null) {
				formatted.append(";" + TYPE_PARAM + "=");
				formatted.append(org.customType);
			}
			if (!StringUtils.isASCII(org.company))
				formatted.append(";CHARSET=UTF-8");
			formatted.append(":").append(org.company.trim()).append(NL);
			if (org.title == null)
				formatted.append("TITLE:").append(NL);
		}
		if (org.title != null) {
			if (org.company == null)
				formatted.append("ORG:").append(NL);
			formatted.append("TITLE");
			if (org.customType != null) {
				formatted.append(";" + TYPE_PARAM + "=");
				formatted.append(org.customType);
			}
			if (!StringUtils.isASCII(org.title))
				formatted.append(";CHARSET=UTF-8");
			formatted.append(":").append(org.title.trim()).append(NL);
		}
	}

	/**
	 * ����ϵ�˸�ʽΪvcard�ֶ�
	 * 
	 * @param formatted
	 *            ��ʽ���������׷�ӵ�����
	 * @param peoData
	 *            ��ϵ����Ϣ
	 * @throws IOException
	 */
	public static void formatPeople(Appendable formatted, PeopleData peoData)
			throws IOException {
		formatted.append("N");
		if (!StringUtils.isASCII(peoData.midName)
				|| !StringUtils.isASCII(peoData.familyNmae))
			formatted.append(";CHARSET=UTF-8");
		if (peoData.midName != null) {
			formatted
					.append(":")
					.append((peoData.givenName != null) ? peoData.givenName
							.trim() : "")
					.append(";")
					.append((peoData.familyNmae != null) ? peoData.familyNmae
							.trim() : "")
					.append(";")
					.append((peoData.midName != null) ? peoData.midName.trim()
							: "").append(";").append(";").append(NL);
		} else {
			formatted
					.append(":")
					.append((peoData.familyNmae != null) ? peoData.familyNmae
							.trim() : "")
					.append(";")
					.append((peoData.givenName != null) ? peoData.givenName
							.trim() : "").append(";").append(";").append(";")
					.append(NL);
		}
		formatted.append("FN");
		if (peoData.displayName != null) {
			if (!StringUtils.isASCII(peoData.displayName))
				formatted.append(";CHARSET=UTF-8");
			formatted.append(":").append(peoData.displayName.trim());
		} else {
			formatted.append("");
		}
		formatted.append(NL);
	}

	/**
	 * ���ֻ���ȡ��������λ��Ϣ
	 * 
	 * @param cResolver
	 */
	private void getOrgFields(ContentResolver cResolver) {
		String customType = null;
		// ��Data��ȡ��Organization����
		String[] projection = { Organization.TITLE, Organization.COMPANY,
				Organization.TYPE, Organization.LABEL };
		String selection = Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
				+ " = ?";
		String[] selParams = new String[] { peopleData._id,
				Organization.CONTENT_ITEM_TYPE };
		Cursor orgCur = cResolver.query(Data.CONTENT_URI, projection,
				selection, selParams, null);
		if (orgCur != null && orgCur.moveToFirst()) {
			do {
				String title = orgCur.getString(0);
				String company = orgCur.getString(1);
				int type = orgCur.getInt(2);
				if (type == Organization.TYPE_CUSTOM) {
					customType = orgCur.getString(3);
				}
				OrgData newOrg = new OrgData(type, title, company, customType);
				orgs.add(newOrg);
			} while (orgCur.moveToNext());
		}
		orgCur.close();
	}

	/**
	 * ���ֻ���ȡ��IM(��ʱͨѶ��ʽ)
	 * 
	 * @param cResolver
	 */
	private void getImFields(ContentResolver cResolver) {
		// ��Data��ȡ��Im����
		String customType = null;
		String[] projection = { Im.DATA, Im.TYPE, Im.IS_PRIMARY, Im.LABEL,
				Im.PROTOCOL, Im.CUSTOM_PROTOCOL };
		String imWhere = Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ?";
		String[] imWhereParams = new String[] { peopleData._id,
				Im.CONTENT_ITEM_TYPE };
		Cursor imCur = cResolver.query(Data.CONTENT_URI, projection, imWhere,
				imWhereParams, null);
		if (imCur != null && imCur.moveToFirst()) {
			do {
				String data = imCur.getString(0);
				int type = imCur.getInt(1);
				int primary = imCur.getInt(2);
				if (type == Im.TYPE_CUSTOM) {
					customType = imCur.getString(3);
				}
				RowData newRow = new RowData(type, data, primary != 0,
						customType);
				String proNumber = imCur.getString(4);
				Log.i("proNumber", proNumber);
				if (proNumber != null) {
					int proNum = Integer.parseInt(proNumber);
					if (proNum >= 0 && proNum <= 7) {
						newRow.protocol = PROTO[proNum];
					} else {
						newRow.protocol = "UNKNOW-PROTO";
					}
				} else {
					newRow.protocol = imCur.getString(5);
				}
				ims.add(newRow);
			} while (imCur.moveToNext());
		}
		imCur.close();
	}

	/**
	 * ���ֻ���ȡ�������͵�ַ
	 */
	private void getAddressFields(ContentResolver cResolver) {
		String customType = null;
		// ��Data��ȡ��StructurePostal�໥��
		String[] projection = { StructuredPostal.POBOX,
				StructuredPostal.STREET, StructuredPostal.CITY,
				StructuredPostal.REGION, StructuredPostal.POSTCODE,
				StructuredPostal.COUNTRY, StructuredPostal.IS_PRIMARY,
				StructuredPostal.TYPE, StructuredPostal.LABEL };
		StringBuffer dataBuff = new StringBuffer();
		String selection = Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
				+ " = ?";
		String[] selParams = new String[] { peopleData._id,
				StructuredPostal.CONTENT_ITEM_TYPE };
		Cursor postalCur = cResolver.query(Data.CONTENT_URI, projection,
				selection, selParams, null);
		if (postalCur != null && postalCur.moveToFirst()) {
			do {
				customType = null;
				// ����
				String poBox = postalCur.getString(0);
				if (poBox != null) {
					dataBuff.append(poBox);
				}
				dataBuff.append(", ");
				// �ھ�(δ��д)
				dataBuff.append(", ");
				// �ֵ�
				String street = postalCur.getString(1);
				if (street != null) {
					dataBuff.append(street);
				}
				dataBuff.append(", ");
				// ����
				String city = postalCur.getString(2);
				if (city != null) {
					dataBuff.append(city);
				}
				dataBuff.append(", ");
				// ����
				String region = postalCur.getString(3);
				if (region != null) {
					dataBuff.append(region);
				}
				dataBuff.append(", ");
				// ��������
				String postalCode = postalCur.getString(4);
				if (postalCode != null) {
					dataBuff.append(postalCode);
				}
				dataBuff.append(", ");
				// ����
				String country = postalCur.getString(5);
				if (country != null) {
					dataBuff.append(country);
				}
				int primary = postalCur.getInt(6);
				int type = postalCur.getInt(7);
				if (type == StructuredPostal.TYPE_CUSTOM) {
					customType = postalCur.getString(8);
				}
				addrs.add(new RowData(type, dataBuff.toString(), primary != 0,
						customType));
				Log.i("postal", dataBuff + "  addr.num=" + addrs.size());
			} while (postalCur.moveToNext());
		}
		postalCur.close();
	}

	/**
	 * ���ֻ���ȡ��email��ַ
	 */
	private void getEmailFields(ContentResolver cResolver) {
		String customType = null;
		// ��Data��ȡ��Email����
		String[] projection = { Email.DATA, Email.TYPE, Email.IS_PRIMARY,
				Email.LABEL };
		String selection = Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
				+ " = ?";
		String[] selParams = new String[] { peopleData._id,
				Email.CONTENT_ITEM_TYPE };
		Cursor emailCur = cResolver.query(Data.CONTENT_URI, projection,
				selection, selParams, null);
		if (emailCur != null && emailCur.moveToFirst()) {
			do {
				String data = emailCur.getString(0);
				int type = emailCur.getInt(1);
				int primary = emailCur.getInt(2);
				if (type == Email.TYPE_CUSTOM) {
					customType = emailCur.getString(3);
				}
				emails.add(new RowData(type, data, primary != 0, customType));
				Log.i("email", data);
			} while (emailCur.moveToNext());
		}
		emailCur.close();
	}

	/**
	 * ���ֻ���ȡ����ϵ������
	 */
	private void getPeopleFields(ContentResolver cResolver) {
		// ��Data��ȡ��StructuredName����
		String[] projection = { StructuredName.DISPLAY_NAME,
				StructuredName.FAMILY_NAME, StructuredName.MIDDLE_NAME,
				StructuredName.GIVEN_NAME };
		String selection = Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
				+ " = ?";
		String[] selParams = new String[] { peopleData._id,
				StructuredName.CONTENT_ITEM_TYPE };
		Cursor cur = cResolver.query(Data.CONTENT_URI, projection, selection,
				selParams, null);
		if (cur != null && cur.moveToFirst()) {
			peopleData.displayName = cur.getString(0);
			peopleData.familyNmae = cur.getString(1);
			peopleData.midName = null;
			peopleData.givenName = cur.getString(3);
		}
		cur.close();
	}

	/**
	 * ���ֻ���ȡ��Notes��Ϣ
	 */
	private void getNoteFields(ContentResolver cResolver) {
		// ��ȡ��ע��Ϣ,(δ���)
		// ��Data��ȡ��Note����
		String[] projection = { Note.NOTE, Note.IS_PRIMARY };
		String selection = Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
				+ " = ?";
		String[] selParams = new String[] { peopleData._id,
				Note.CONTENT_ITEM_TYPE };
		Cursor cursor = cResolver.query(Data.CONTENT_URI, projection,
				selection, selParams, null);
		if (cursor != null && cursor.moveToFirst()) {
			notes = cursor.getString(0);
			Log.i("note", notes);
			if (notes != null) {
				Matcher ppm = birthdayPattern.matcher(notes);

				if (ppm.find()) {
					notes = ppm.replaceFirst("");
				}
			}
		}
		cursor.close();
	}

	/**
	 * ���ֻ���ȡ���绰����
	 */
	private void getPhoneFields(ContentResolver cResolver) {
		String[] projection = { Phone.NUMBER, Phone.TYPE, Phone.IS_PRIMARY,
				Phone.LABEL };
		String selection = Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
				+ " = ?";
		String[] selParams = new String[] { peopleData._id,
				Phone.CONTENT_ITEM_TYPE };
		Cursor cur = cResolver.query(Data.CONTENT_URI, projection, selection,
				selParams, null);
		Log.i("count = ", "" + cur.getCount());
		if (cur.moveToFirst()) {
			do {
				String customType = null;
				String phone = cur.getString(0);
				int phoneType = cur.getInt(1);
				boolean preferred = cur.getInt(2) != 0;
				if (phoneType == Phone.TYPE_CUSTOM) {
					customType = cur.getString(3);
				}
				RowData rowData = new RowData(phoneType, phone, preferred,
						customType);
				phones.add(rowData);
				Log.i("phone", "" + phone);
			} while (cur.moveToNext());
		}
		cur.close();
	}

	/**
	 * ׷�ӵ��ļ�β��
	 */
	private static void appendField(Appendable out, String name, String val)
			throws IOException {
		if (val != null && val.length() > 0) {
			out.append(name);
			if (!StringUtils.isASCII(val))
				out.append(";CHARSET=UTF-8");
			out.append(":").append(val).append(NL);
		}
	}

	/**
	 * ����vcard
	 */
	public long parseVCard(BufferedReader vCard) throws IOException {
		reset();
		// ��ȡBEGIN
		String line = vCard.readLine();
		if (line == null) {
			return -1;
		}
		while (line != null && !beginPattern.matcher(line).matches()) {
			Log.i("vcard line", line);
			parseLen += line.length();
			line = vCard.readLine();
		}
		// ��ȡ����
		while (line != null) {
			line = vCard.readLine();
			Log.i("vcard line", line);
			if (line == null) {
				return -1;
			}
			vCard.mark(1);
			vCard.reset();
			parseLen += line.length();

			Matcher pm = propPattern.matcher(line);

			if (pm.matches()) {
				String prop = pm.group(1);
				String val = pm.group(2);

				// �ж��Ƿ����һ����ϵ�˵Ľ���
				if (prop.equalsIgnoreCase("END")
						&& val.equalsIgnoreCase("VCARD")) {
					return parseLen;
				}

				// ��������
				Matcher ppm = propParamPattern.matcher(prop);
				if (!ppm.find())
					continue;

				String propName = ppm.group(1).toUpperCase();
				Vector<String> propVec = new Vector<String>();
				String charSet = "UTF-8";
				String encoding = "";
				// �ַ����ͱ��뷽ʽ����
				while (ppm.find()) {
					String param = ppm.group(1);
					String paramVal = ppm.group(3);
					propVec.add(param
							+ (paramVal != null ? "=" + paramVal : ""));
					if (param.equalsIgnoreCase("CHARSET")) {
						charSet = paramVal;
					} else if (param.equalsIgnoreCase("ENCODING")) {
						encoding = paramVal;
					}
				}
				if (encoding.equalsIgnoreCase("QUOTED-PRINTABLE")) {
					try {
						val = QuotedPrintable.decode(val.getBytes(charSet),
								"UTF-8");
					} catch (UnsupportedEncodingException uee) {

					}
				}
				handleProp propHandler = propHandlers.get(propName);
				if (propHandler != null)
					propHandler.parseProp(propName, propVec, val);
			}
		}
		return -1;
	}

	/**
	 * ����keyɾ����ϵ����Ϣ
	 * 
	 * @param cResolver
	 * @param key
	 *            :>0 ����idɾ��ָ����ϵ��,=0ɾ��������ϵ��
	 */
	private void RemoveContact(ContentResolver cResolver, long key) {
		// �Ƴ�������ϵ��
		if (key != 0) {
			cResolver.delete(ContactsContract.RawContacts.CONTENT_URI,
					ContactsContract.RawContacts._ID + " = " + key, null);
		} else {
			cResolver.delete(ContactsContract.RawContacts.CONTENT_URI, null,
					null);
		}
	}

	/**
	 * ��������ϵ����Ϣ
	 * 
	 * @param key
	 *            ���ڵ���ϵ��������
	 * @return �������������
	 */
	public long addContact(Context context, long key, boolean replace) {
		ContentResolver cResolver = context.getContentResolver();
		String[] projection = { StructuredName.DISPLAY_NAME, StructuredName.CONTACT_ID };
		String selection = Data.MIMETYPE + " = ? AND " + StructuredName.DISPLAY_NAME + " = ?";
		String[] selParams = new String[] {StructuredName.CONTENT_ITEM_TYPE, peopleData.displayName };
		Cursor people = cResolver.query(Data.CONTENT_URI, projection, selection,
				selParams, null);

		if (people != null && people.moveToFirst()) {
			Log.i("people.getString(0)", people.getString(0));
			if (replace) {
				do {
					setId(people.getString(1));
					key = getId();
					RemoveContact(cResolver, key);
				} while (people.moveToNext());
			} else {
				people.close();
				return 0;
			}
		}
		people.close();
		// �����ֵ
		ContentValues values = new ContentValues();
		Uri rawContactUri = cResolver.insert(RawContacts.CONTENT_URI, values);
		// ��ȡ_idֵ
		setId(ContentUris.parseId(rawContactUri) + "");
		// ������ϵ������
		insertPeopleValue(cResolver, Data.CONTENT_URI, getPeopleCV());

		Log.i("HERE", "insert phone number");
		// ����绰����
		for (RowData phone : phones) {
			insertPeopleValue(cResolver, Data.CONTENT_URI, getPhoneCV(phone));
		}
		Log.i("HERE", "insert email");
		// email��ַ
		for (RowData email : emails) {
			insertPeopleValue(cResolver, Data.CONTENT_URI, getEmailCV(email));
		}
		Log.i("HERE", "insert address");
		// ��ַ
		for (RowData addr : addrs) {
			insertPeopleValue(cResolver, Data.CONTENT_URI, getAddressCV(addr));
		}
		Log.i("HERE", "insert ims");
		// IMs
		for (RowData im : ims) {
			insertPeopleValue(cResolver, Data.CONTENT_URI, getImCV(im));
		}
		// ������λ
		Log.i("HERE", "insert org");
		for (OrgData org : orgs) {
			insertPeopleValue(cResolver, Data.CONTENT_URI, getOrgnizeCV(org));
		}
		// ��ע��Ϣ
		Log.i("HERE", "insert note");
		if (notes != null) {
			insertPeopleValue(cResolver, Data.CONTENT_URI, getNotesCV(null));
		}
		return key;
	}

	/**
	 * ��vcf�ļ��л�ȡ��ϵ��������Ϣ����ContentValues
	 * 
	 * @return ContentValues ��ϵ��������Ϣ
	 */
	public ContentValues getPeopleCV() {
		ContentValues cv = new ContentValues();
		// ����
		cv.put(Data.RAW_CONTACT_ID, peopleData._id);
		cv.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		// ����
		if (peopleData.familyNmae != null)
			cv.put(StructuredName.FAMILY_NAME, peopleData.familyNmae);
		if (peopleData.midName != null) {
			cv.put(StructuredName.MIDDLE_NAME, peopleData.midName);
		}
		if (peopleData.givenName != null) {
			cv.put(StructuredName.GIVEN_NAME, peopleData.givenName);
		}
		Log.i("peopleData.displayName", peopleData.displayName);
		cv.put(StructuredName.DISPLAY_NAME, peopleData.displayName);
		return cv;
	}

	/**
	 * ��vcf�ļ��л�ȡ��ϵ�˹�����λ��Ϣ����ContentValues
	 * 
	 * @return ContentValues ��ϵ�˹�����λ��Ϣ
	 */
	public ContentValues getOrgnizeCV(OrgData org) {

		if (StringUtils.isNullOrEmpty(org.company)
				&& StringUtils.isNullOrEmpty(org.title)) {
			return null;
		}
		ContentValues cv = new ContentValues();
		// ����
		cv.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
		cv.put(Data.RAW_CONTACT_ID, peopleData._id);
		// ����
		cv.put(Organization.COMPANY, org.company);
		cv.put(Organization.TITLE, org.title);
		cv.put(Organization.TYPE, org.type);
		if (org.customType != null) {
			cv.put(Organization.LABEL, org.customType);
		}
		return cv;
	}

	/**
	 * ��vcf�ļ��л�ȡ�绰������Ϣ����ContentValues
	 * 
	 * @return ContentValues ��ϵ�˵绰��Ϣ
	 */
	public ContentValues getPhoneCV(RowData data) {
		ContentValues cv = new ContentValues();

		// ����
		cv.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		cv.put(Data.RAW_CONTACT_ID, peopleData._id);
		// ����
		cv.put(Phone.NUMBER, data.data);
		cv.put(Phone.TYPE, data.type);
		cv.put(Phone.IS_PRIMARY, data.preferred ? 1 : 0);
		if (data.customType != null) {
			cv.put(Phone.LABEL, data.customType);
		}

		return cv;
	}

	/**
	 * ��vcf�ļ��л�ȡemail��ַ��Ϣ����ContentValues
	 * 
	 * @return ContentValues ��ϵ��Emial��Ϣ
	 */
	public ContentValues getEmailCV(RowData data) {
		ContentValues cv = new ContentValues();

		// ����
		cv.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		cv.put(Data.RAW_CONTACT_ID, peopleData._id);
		// ����
		cv.put(Email.DATA, data.data);
		cv.put(Email.TYPE, data.type);
		cv.put(Email.IS_PRIMARY, data.preferred ? 1 : 0);
		if (data.customType != null) {
			cv.put(Email.LABEL, data.customType);
		}

		return cv;
	}

	/**
	 * ��vcf�ļ��л�ȡ��ַ��Ϣ����ContentValues
	 * 
	 * @return ContentValues ��ϵ�˵�ַ��Ϣ
	 */
	public ContentValues getAddressCV(RowData data) {
		ContentValues cv = new ContentValues();
		// ����
		cv.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
		cv.put(Data.RAW_CONTACT_ID, peopleData._id);
		// ����
		String[] addressFields = StringUtils.split(data.data, ";");
		int maxLen = addressFields.length;
		// ����
		if (addressFields[maxLen - 1] != null) {
			cv.put(StructuredPostal.COUNTRY, addressFields[maxLen - 1]);
		}
		// ��������
		if (addressFields[maxLen - 2] != null) {
			cv.put(StructuredPostal.POSTCODE, addressFields[maxLen - 2]);
		}
		// ����
		if (addressFields[maxLen - 3] != null) {
			cv.put(StructuredPostal.REGION, addressFields[maxLen - 3]);
		}
		// ����
		if (addressFields[maxLen - 4] != null) {
			cv.put(StructuredPostal.CITY, addressFields[maxLen - 4]);
		}
		// �ֵ�
		if (addressFields[maxLen - 5] != null) {
			cv.put(StructuredPostal.STREET, addressFields[maxLen - 5]);
		}
		if (addressFields[maxLen - 6] != null) {
			Log.i("getAddressCV", "no neighborhood");
		}
		// ����
		if (addressFields[maxLen - 7] != null) {
			cv.put(StructuredPostal.POBOX, addressFields[maxLen - 7]);
		}
		cv.put(StructuredPostal.TYPE, data.type);
		cv.put(StructuredPostal.IS_PRIMARY, data.preferred ? 1 : 0);
		if (data.customType != null) {
			cv.put(StructuredPostal.LABEL, data.customType);
		}

		return cv;
	}

	/**
	 * ��vcf�ļ��л�ȡIM��Ϣ����ContentValues
	 * 
	 * @return ContentValues ��ϵ��Im(��ʱͨѶ��ʽ)��Ϣ
	 */
	public ContentValues getImCV(RowData data) {
		ContentValues cv = new ContentValues();

		// ����
		cv.put(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);
		cv.put(Data.RAW_CONTACT_ID, peopleData._id);
		// ����
		cv.put(Im.DATA, data.data);
		cv.put(Im.TYPE, data.type);
		cv.put(Im.IS_PRIMARY, data.preferred ? 1 : 0);
		if (data.customType != null) {
			cv.put(Im.LABEL, data.customType);
		}

		if (data.protocol != null) {
			int protoNum = -1;
			for (int i = 0; i < PROTO.length; ++i) {
				if (data.protocol.equalsIgnoreCase(PROTO[i])) {
					protoNum = i;
					break;
				}
			}
			if (protoNum >= 0) {
				cv.put(Im.PROTOCOL, protoNum);
			} else {
				cv.put(Im.CUSTOM_PROTOCOL, data.protocol);
			}
		}

		return cv;
	}

	/**
	 * ��vcf�ļ��л�ȡ��ע��Ϣ����ContentValues
	 * 
	 * @return ContentValues ��ϵ�˱�ע��Ϣ
	 */
	public ContentValues getNotesCV(RowData data) {
		ContentValues cv = new ContentValues();

		// ����
		cv.put(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE);
		cv.put(Note.RAW_CONTACT_ID, peopleData._id);
		// ����
		cv.put(Note.NOTE, notes);
		return cv;
	}

	/**
	 * ���뵽�绰��
	 */
	private Uri insertPeopleValue(ContentResolver cResolver, Uri uri,
			ContentValues cv) {
		if (cv != null) {
			return cResolver.insert(uri, cv);
		}
		return null;
	}
}
