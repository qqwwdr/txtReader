package cn.wxn.txtreader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 操作SharedPreferences的工具类
 *
 * @author 新年
 */
public class SPUtils {

	//sharePreferences的文件的名称
	public static final String SP_NAME = "config";
	private static SharedPreferences sp;

	/**
	 * 保存 值是字符串的  键值对
	 *
	 * @param context 上下文
	 * @param key     关键字
	 * @param value   对应于关键字的值
	 */
	public static void saveString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		}
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
//		LogUtils.logI(sp, "saveString -->" + key +", " + value);
	}

	/**
	 * @param context  上下文
	 * @param key      关键字
	 * @param defValue 默认值
	 * @return 得到的关键字的对应的String值
	 */
	public static String getString(Context context, String key, String defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		}
//		LogUtils.logI(sp, "getString -->" + key +", defValue : _-->" + defValue);
		return sp.getString(key, defValue);
	}

	/**
	 * 保存 值是boolean 的键值对
	 *
	 * @param context 上下文
	 * @param key     关键字
	 * @param value   值
	 */
	public static void saveBoolean(Context context, String key, boolean value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		}
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * @param context  上下文
	 * @param key      关键字
	 * @param defValue 默认值
	 * @return 得到关键字对应的 boolean值
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}
}
