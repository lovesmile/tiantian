package com.tian.toolsset.utils;

import java.io.DataOutputStream;

public class RootPermissionGet {
	 /**
	  * Ӧ�ó������������ȡ RootȨ�ޣ��豸�������ƽ�(���ROOTȨ��)
	  * 
	  * @return Ӧ�ó�����/���ȡRootȨ��
	  */
	private static boolean isRoot = false;
    public static boolean upgradeRootPermission(String pkgCodePath) {
	     Process process = null;
	     DataOutputStream os = null;
	     try {
	         String cmd="chmod 777 " + pkgCodePath;
	         process = Runtime.getRuntime().exec("su"); //�л���root�ʺ�
	         os = new DataOutputStream(process.getOutputStream());
	         os.writeBytes(cmd + "\n");
	         os.writeBytes("exit\n");
	         os.flush();
	         process.waitFor();
	         isRoot = true;
	     } catch (Exception e) {
	    	 isRoot = false;
	     } finally {
	         try {
	             if (os != null) {
	                 os.close();
	             }
	             process.destroy();
	         } catch (Exception e) {
	        	 isRoot = false;
	         }
	     }
	     return isRoot;
	 }
}
