package cn.wxn.txtreader.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import cn.wxn.txtreader.bean.FileItem;

/**
 * Created by wangxn on 2015/12/24.
 */
public class FileReaderUtil {
	private static final Object TAG = "FileReaderUtil";

	public static String getRightStringCode(String path) {
		File file = new File(path);
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				int len = 1024;
				fis.read(buffer, 0, len);
				String s = bytesToHexString(buffer);
				System.out.println(s);
				String code = "";
				if (s.substring(0, 6).equalsIgnoreCase("EFBBBF")) {
//					code = new String(buffer, "UTF-8");
					code = "UTF-8";
				} else if (s.substring(0, 4).equalsIgnoreCase("FFFE")) {
					//Unicode litter endian
//                  txt = new String(buffer, "Unicode"); //可以
//                  txt = new String(buffer, "UTF-16");  //也可以
//					code = new String(buffer, "UTF-16LE"); //依然可以
					code = "UTF-16LE";
				} else if (s.substring(0, 4).equalsIgnoreCase("FEFF")) {
					//Unicode big endian
//					code = new String(buffer, "UTF-16BE");
					code = "UTF-16BE";
				} else {
					//根据文件头判断不出文件的字符编码的时候,使用UniversalDetector来探测
					UniversalDetector detector = new UniversalDetector(null);
					//开始给一部分数据，让学习一下啊，官方建议是1000个byte左右（当然这1000个byte你得包含中文之类的）
					detector.handleData(buffer, 0, buffer.length);
					//识别结束必须调用这个方法
					detector.dataEnd();
					code = detector.getDetectedCharset();
					if (StrUtil.isEmpty(code)) {
						Charset charset = Charset.defaultCharset();
						code = charset.displayName();
					}

					if ("WINDOWS-1252".equals(code) || "GB2312".equals(code)) {
						LogUtil.i(TAG, "code ===========" + code);
						code = "GBK";
					}
				}
				return code;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 字节数组 转换成 16进制显示的字符串
	 *
	 * @param bArray 字节数组
	 * @return 16进制显示的字符串
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);  //等同于字节数组长度的字符串
		String       sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);  //获取补码,之后的16进制的单个字节的字符
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}




	@Nullable
	public static String getStringFromFile(String path) {
		String code = getRightStringCode(path);
		LogUtil.i(TAG, "code == " + code);
		String txt = null;

		InputStream           is   = null;
		BufferedInputStream   bis  = null;
		ByteArrayOutputStream baos = null;
		try {
			is = new FileInputStream(path);
			bis = new BufferedInputStream(is);
			baos = new ByteArrayOutputStream();
			int    len    = 0;
			byte[] buffer = new byte[1024];
			while ((len = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			txt = baos.toString(code);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeStream(is);
			closeStream(bis);
			closeStream(baos);
		}
		return txt;
	}


	private static void closeStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeStream(OutputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 根据后缀判断文件类型
	 *
	 * @param tag  取值 "pic", "doc" 2种
	 * @param fileName  文件名
	 * @return
	 */
	public static boolean isTheTypeFile(String tag, String fileName) {
		boolean  isPic  = false;
		String[] suffix = null;
		if ("pic".equals(tag)) {
			suffix = pic;
		} else if ("doc".equals(tag)) {
			suffix = doc;
		} else if ("music".equals(tag)) {
			suffix = music;
		} else if ("video".equals(tag)) {
			suffix = video;
		}
		if (suffix != null && suffix.length > 0) {
			for (int i = 0; i < pic.length; i++) {
				if (fileName.endsWith(suffix[i])) {
					isPic = true;
					break;
				}
			}
		}
		return isPic;
	}

	public static final String[] pic   = new String[]{".jpg", ".png", ".gif", ".bmp", ".jpeg",};
	public static final String[] doc   = new String[]{".txt", ".xml", ".log", ".java", ".c", "html", ".css"};
	public static final String[] music = new String[]{".mp3", ".ogg", ".swf", ".mid", "wav", "act", "wma", "rec"};
	public static final String[] video = new String[]{".mp4", ".3gp", ".avi", ".mkv", "wmv"};

}
