package cn.wxn.txtreader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.wxn.txtreader.bean.FileItem;
import cn.wxn.txtreader.utils.LogUtil;

/**
 * Created by wangxn on 2016/1/25.
 */
public class DBDataHelper {

	private static final Object TAG = "DBDataHelper";

	public static DBDataHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBDataHelper(context);
		}
		return instance;
	}

	private static DBDataHelper instance;

	public static final String DB_NAME = "files.db"; //数据库名

	private DBHelper       dbHelper;
	private SQLiteDatabase db;

	private DBDataHelper(Context context) {
		dbHelper = new DBHelper(context, DB_NAME, null, DBHelper.DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	/**
	 * 添加多个文件信息到数据库
	 *
	 * @param items 多个文件
	 * @return 增加成功的条目数量
	 */
	public int saveFiles(List<FileItem> items) {
//		LogUtil.i(TAG, "saveFiles");
		int successfulCount = 0;
		db.beginTransaction();  //开启事务
		try {
			for (FileItem item : items) {
				if (saveFile(item)) {
					successfulCount++;
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			db.endTransaction(); //结束事务
		}
//		LogUtil.i(TAG, "saveFiles : " + successfulCount);
		return successfulCount;
	}

	/**
	 * 增
	 * @param item
	 * @return 是否写成功
	 */
	public boolean saveFile(FileItem item) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(FileItem.ABSTRACT_PATH, item.abstractPath);
		contentValues.put(FileItem.FILE_NAME, item.fileName);
		contentValues.put(FileItem.IS_DIRECTORY, item.isDirectory);
		contentValues.put(FileItem.FILE_TYPE, item.fileType);
		contentValues.put(FileItem.FILE_CAPACITY, item.fileCapacity);
		contentValues.put(FileItem.PARENT_ID, item.parent_id);
//		LogUtil.i(TAG, "save file parent id = " + item.parent_id);
		return db.insert(DBHelper.TB_NAME, FileItem.ID, contentValues) != -1 ? true : false;
	}

	/**
	 * @param abstractPath
	 * @return 查一个文件的信息
	 */
	public FileItem getFile(String abstractPath) {
		Cursor cursor = db.rawQuery("select * from " + DBHelper.TB_NAME + " where " + FileItem.ABSTRACT_PATH + "=?", new String[]{abstractPath});
		cursor.moveToFirst();
		FileItem fileItem = new FileItem();
		fileItem.id = cursor.getInt(0);
		fileItem.parent_id = cursor.getInt(1);
		fileItem.abstractPath = cursor.getString(2);
		fileItem.fileName = cursor.getString(3);
		fileItem.isDirectory = cursor.getInt(4) == 0 ? false : true;
		fileItem.fileType = cursor.getInt(5);
		fileItem.fileCapacity = cursor.getLong(6);
		cursor.close();
		return fileItem;
	}

	/**
	 * @param filePath 文件路径
	 * @return 获取文件id
	 */
	public int getFileId(String filePath) {
		Cursor cursor = db.rawQuery("select " + FileItem.ID + " from " + DBHelper.TB_NAME + " where " + FileItem.ABSTRACT_PATH + "=?", new String[]{filePath});
		cursor.moveToFirst();
		int _id = 0;
		if (cursor.getCount() > 0) {
			_id = cursor.getInt(0);
		}
		cursor.close();
		return _id;
	}

	public List<FileItem> getFiles(){
		Cursor cursor    = db.rawQuery("select * from " + DBHelper.TB_NAME , new String[]{});
		if (cursor == null){
			return null;
		}
		cursor.moveToFirst();
		List<FileItem> items = new ArrayList<FileItem>();
		while (!cursor.isAfterLast()) {
			FileItem fileItem = new FileItem();
			fileItem.id = cursor.getInt(0);
			fileItem.parent_id = cursor.getInt(1);
			fileItem.abstractPath = cursor.getString(2);
			fileItem.fileName = cursor.getString(3);
			fileItem.isDirectory = cursor.getInt(4) == 0 ? false : true;
			fileItem.fileType = cursor.getInt(5);
			fileItem.fileCapacity = cursor.getLong(6);
			items.add(fileItem);
			cursor.moveToNext();
		}
		cursor.close();
		return items;
	}

	/**
	 * @param dir 目录路径
	 * @return 查一个目录下的所有file
	 */
	public List<FileItem> getFilesInDir(String dir) {
		int    parent_id = getFileId(dir);
		Cursor cursor    = db.rawQuery("select * from " + DBHelper.TB_NAME + " where " + FileItem.PARENT_ID + "= ? ", new String[]{"" + parent_id});
		cursor.moveToFirst();
		List<FileItem> items = new ArrayList<FileItem>();
		while (!cursor.isAfterLast()) {
			FileItem fileItem = new FileItem();
			fileItem.id = cursor.getInt(0);
			fileItem.parent_id = cursor.getInt(1);
			fileItem.abstractPath = cursor.getString(2);
			fileItem.fileName = cursor.getString(3);
			fileItem.isDirectory = cursor.getInt(4) == 0 ? false : true;
			fileItem.fileType = cursor.getInt(5);
			fileItem.fileCapacity = cursor.getLong(6);
			items.add(fileItem);
			cursor.moveToNext();
		}
		cursor.close();
		return items;
	}

	/**
	 * 删除一个在数据库中对应的文件信息
	 * @param filePath
	 * @return
	 */
	public boolean delFile(String filePath) {
		return db.delete(DBHelper.TB_NAME, "where " + FileItem.ABSTRACT_PATH + "=?", new String[]{filePath}) == 1 ? true : false;
	}

	private boolean delFileById(int id){
		return db.delete(DBHelper.TB_NAME, "where " + FileItem.ID + "=?", new String[]{"" + id}) == 1 ? true : false;
	}

	/**
	 * 删除数据库中 一个目录下的所有数据
	 * @param dir
	 */
	public void delDir(String dir) {
		int fileId = getFileId(dir);

		List<FileItem> filesInDir = getFilesInDir(dir);
		for (FileItem item : filesInDir){
			if (item.isDirectory){
				delDir(dir);
			}else {
				delFileById(item.id);
			}
		}
		delFileById(fileId);
	}

	/**
	 * 清空表的内容
	 */
	public void clearTable(){
		db.delete(DBHelper.TB_NAME, null, null);
	}
}
