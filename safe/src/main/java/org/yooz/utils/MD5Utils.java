package org.yooz.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	public static String encode(String pass) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");// 获取MD5算法对象
			byte[] digest = instance.digest(pass.getBytes());// 对字符串加密,返回字节数组

			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;// 获取字节的低八位有效值
				String hexString = Integer.toHexString(i);// 将整数转为16进制
				if (hexString.length() < 2) {
					hexString = "0" + hexString;// 如果是1位的话,补0
				}
				sb.append(hexString);
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			// 没有该算法时,抛出异常, 不会走到这里
		}

		return "";
	}

	/**
	 * 获取文件md5值
	 * @param sourceDir
	 * @return
	 */
	public static String getFileMD5(String sourceDir) {
		File file = new File(sourceDir);
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = new byte[1014];
			int len = 0;
			//获取到数字摘要
			MessageDigest messageDigest = MessageDigest.getInstance("md5");
			while((len=fileInputStream.read(bytes))!=-1) {
				messageDigest.update(bytes,0,len);
			}
			byte[] digest = messageDigest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;// 获取字节的低八位有效值
				String hexString = Integer.toHexString(i);// 将整数转为16进制
				if (hexString.length() < 2) {
					hexString = "0" + hexString;// 如果是1位的话,补0
				}
				sb.append(hexString);
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
