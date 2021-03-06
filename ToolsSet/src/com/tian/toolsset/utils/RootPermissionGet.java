package com.tian.toolsset.utils;

import java.io.DataOutputStream;

public class RootPermissionGet {
	 /**
	  * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	  * 
	  * @return 应用程序是/否获取Root权限
	  */
	private static boolean isRoot = false;
    public static boolean upgradeRootPermission(String pkgCodePath) {
	     Process process = null;
	     DataOutputStream os = null;
	     try {
	         String cmd="chmod 777 " + pkgCodePath;
	         process = Runtime.getRuntime().exec("su"); //切换到root帐号
	         os = new DataOutputStream(process.getOutputStream());
	         os.writeBytes(cmd + "\n");
	         os.writeBytes("exit\n");
	         os.flush();
	         int exitValue = process.waitFor();  
	         if (exitValue == 0)  
	         {  
	        	 isRoot = true;
	         } else  
	         {  
	        	 isRoot = false;
	         }  
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
