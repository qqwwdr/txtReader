package cn.wxn.txtreader.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.wxn.txtreader.bean.FileItem;
import cn.wxn.txtreader.db.DBDataHelper;
import cn.wxn.txtreader.utils.FileSizeUtil;
import cn.wxn.txtreader.utils.LogUtil;

/**
 * Created by wangxn on 2016/2/16.
 */
public class AutoSearchService extends IntentService {

	private static final String[] FILE_SUFFIX = {"txt"};
	private static final String   TAG         = "AutoSearchService";
	private List<String> txtFiles;

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public AutoSearchService(String name) {
		super(name);
	}

	public AutoSearchService(){
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {   //没有sd卡挂载
			LogUtil.i(TAG , "没有sd卡挂载");
			return;
		}

		String         path[]         = null;
		StorageManager storageManager = (StorageManager) getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
		try {
			Method getVolumePaths = storageManager.getClass().getMethod("getVolumePaths", null);
			path = (String[]) getVolumePaths.invoke(storageManager, null);

			if (path == null || path.length == 0) {   //通过反射获取的根路径列表没有获取到
				LogUtil.i(TAG, "通过反射获取的根路径列表没有获取到");
				path = new String[1];
				path[0] = Environment.getExternalStorageDirectory().getAbsolutePath();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if (path == null || path.length == 0) {
			LogUtil.i(TAG, "没有外部存储卡路径");
			return;
		}

		txtFiles = new ArrayList<>();
		LogUtil.i(TAG, "开始遍历外部存储卡中所有文件");
		long l = System.currentTimeMillis();
		for (String rootPath : path) {
			swipeDir(rootPath);
		}
		long l1 = (System.currentTimeMillis() - l) / 1000;
		LogUtil.i(TAG,"遍历用时: " + l1 + "秒");
		LogUtil.i(TAG, "获取的txt文件数量为: " + txtFiles.size());

		LogUtil.i(TAG, "开始向数据库中存储获取的txt文件");
		long           l2    = System.currentTimeMillis();
		List<FileItem> items = new ArrayList<>();
		for (int i = 0; i < txtFiles.size() ; i++){
			FileItem item = new FileItem();
			File file = new File(txtFiles.get(i));

			item.fileName = file.getName();
			item.abstractPath = file.getAbsolutePath();
			item.isDirectory = false;
			item.fileType = 2;
			item.fileCapacity = (long) FileSizeUtil.getFileOrFilesSize(item.abstractPath, 1);
			items.add(item);
		}
		DBDataHelper.getInstance(getBaseContext()).clearTable();
		DBDataHelper.getInstance(getBaseContext()).saveFiles(items);
		long l3 = (System.currentTimeMillis() - l2) / 1000;
		LogUtil.i(TAG, "向数据库中存储数据用时 : " + l3  + "秒");

		//发送广播
		Intent intent2 = new Intent();
		intent2.putExtra(REFRESH_FLAG, REFRESH_FLAG);
		intent2.setAction(ACTION_REFRESH);
		sendBroadcast(intent2);
		LogUtil.i(TAG, "发送广播");
	}

	public static final String REFRESH_FLAG = "REFRESH_DOWN";
	public static final String ACTION_REFRESH = "com.wxn.txtreader.AutoSearchService";

	/**
	 * 递归遍历整个外部存储卡中的所有文件
	 * @param path
	 */
	private void swipeDir(String path) {
		File file = new File(path);
		if (!file.exists() || !file.canRead() || !file.isDirectory()) {
			return;
		}

		File[] files = file.listFiles();
		if (files == null || files.length == 0) {  //目录下没有内容
			return;
		}
		for (int i = 0; i < files.length; i++) {  //遍历目录下的各个文件以及文件夹
			if (files[i].isDirectory()) {
				swipeDir(files[i].getPath());
			} else {
				for (String suffix : FILE_SUFFIX) {            //判断后缀名是不是指定的文件类型,是,则保持这个文件的绝对路径
					if (files[i].getName().endsWith(suffix)) {
						if(files[i].length() > 1024) {
							txtFiles.add(files[i].getAbsolutePath());
						}
					}
				}
			}
		}
	}
}
