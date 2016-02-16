package cn.wxn.txtreader.utils;

import android.util.Log;

import java.util.Map;

/**
 * <p>project：Weizuche</p>
 *
 * @author Cooper
 * @Title: LogUtil.java
 * @Description: 应用程序的Log管理
 * @date 2015-6-5 下午2:49:37
 */

public final class LogUtil {
	public static boolean IS_DEBUG            = true;
	public static boolean DEBUG_LOG           = true;
	public static boolean SHOW_ACTIVITY_STATE = true;

	public static final void openDebutLog(boolean enable) {
		IS_DEBUG = enable;
		DEBUG_LOG = enable;
	}

	public static final void openActivityState(boolean enable) {
		SHOW_ACTIVITY_STATE = enable;
	}

	public static final void debug(String msg) {
		if (IS_DEBUG) {
			Log.i("debug", msg);
		}
	}

	public static final void log(String packName, String state) {
		debugLog(packName, state);
	}

	public static final void debug(String msg, Throwable tr) {
		if (IS_DEBUG) {
			Log.i("debug", msg, tr);
		}
	}

	public static final void state(String packName, String state) {
		if (SHOW_ACTIVITY_STATE) {
			Log.d("activity_state", packName + state);
		}
	}

	public static final void debugLog(String packName, String state) {
		if (DEBUG_LOG) {
			Log.d("debug", packName + state);
		}
	}

	public static final void exception(Exception e) {
		if (DEBUG_LOG) {
			e.printStackTrace();
		}
	}

	public static final void debug(String msg, Object... format) {
		debug(String.format(msg, format));
	}

	public static void showMap(Object obj, Map map) {
		if (IS_DEBUG && obj != null && map != null && map.size() > 0) {

			StringBuilder sb = new StringBuilder("请求参数:---------> ");
			for (Object key : map.keySet()) {
				if (key != null && map.get(key) != null) {
					sb.append(key.toString() + "=" + map.get(key).toString() + ",");
				}
			}
			String result = sb.substring(0, sb.length() - 1) + "<----------------";

			if (obj instanceof String) {
				Log.i(obj.toString(), result);
			} else {
				Log.i(obj.getClass().getSimpleName(), result);
			}
		}
	}

	public static void i(Object obj, String msg) {
		if (IS_DEBUG && obj != null && !StrUtil.isEmpty(msg)) {
			if (obj instanceof String) {
				Log.i(obj.toString(), msg);
			} else {
				Log.i(obj.getClass().getSimpleName(), msg);
			}
		}
	}

	public static void v(Object obj, String msg) {
		if (IS_DEBUG && obj != null && !StrUtil.isEmpty(msg)) {
			if (obj instanceof String) {
				Log.v(obj.toString(), msg);
			} else {
				Log.v(obj.getClass().getSimpleName(), msg);
			}
		}
	}

	public static void e(Object obj, String msg) {
		if (IS_DEBUG && obj != null && !StrUtil.isEmpty(msg)) {
			if (obj instanceof String) {
				Log.e(obj.toString(), msg);
			} else {
				Log.e(obj.getClass().getSimpleName(), msg);
			}
		}
	}

	public static void d(Object obj, String msg) {
		if (IS_DEBUG && obj != null && !StrUtil.isEmpty(msg)) {
			if (obj instanceof String) {
				Log.d(obj.toString(), msg);
			} else {
				Log.d(obj.getClass().getSimpleName(), msg);
			}
		}
	}

	public static void w(Object obj, String msg) {
		if (IS_DEBUG && obj != null && !StrUtil.isEmpty(msg)) {
			if (obj instanceof String) {
				Log.w(obj.toString(), msg);
			} else {
				Log.w(obj.getClass().getSimpleName(), msg);
			}
		}
	}
}
