package cn.wxn.txtreader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.wxn.txtreader.bean.ReadItem;
import cn.wxn.txtreader.utils.LogUtil;

/**
 * Created by wangxn on 2016/1/25.
 */
public class DBReaderHelper {

	private static final Object TAG = "DBReaderHelper";

	public static DBReaderHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBReaderHelper(context);
		}
		return instance;
	}

	private static DBReaderHelper instance;

	public static final String DB_NAME = "readers.db"; //数据库名

	private DBHelper       dbHelper;
	private SQLiteDatabase db;

	private DBReaderHelper(Context context) {
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
	 * 增
	 *
	 * @param item
	 * @return 是否写成功
	 */
	public boolean saveReadFile(ReadItem item) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ReadItem.ID, item.id);
		contentValues.put(ReadItem.FILE_PATH, item.filePath);
		contentValues.put(ReadItem.READ_ORBITS, item.getReadOrbitsStr());
//		contentValues.put(ReadItem.CURRENT_ORBIT, item.currentOrbit);
		contentValues.put(ReadItem.BOOKMARKS, item.getBookmarksStr());
		LogUtil.i(TAG, "save read file id = " + item.id);
		return db.insert(DBHelper.TB_READ, ReadItem.ID, contentValues) != -1 ? true : false;
	}

//	public boolean setCurrentOrbit(ReadItem item){
//		ContentValues contentValues = new ContentValues();
//		contentValues.put(ReadItem.CURRENT_ORBIT, item.currentOrbit);
//		return db.update(DBHelper.TB_READ, contentValues, ReadItem.ID + "=?", new String[]{"" + item.id}) == 1 ? true : false;
//	}

//	public boolean addReadOrbits(ReadItem item) {
//		ContentValues contentValues = new ContentValues();
//		contentValues.put(ReadItem.READ_ORBITS, item.getReadOrbitsStr());
//		LogUtil.i(TAG, "设置浏览节点: " + item.getReadOrbitsStr());
//		return db.update(DBHelper.TB_READ, contentValues, ReadItem.ID + "=?", new String[]{"" + item.id}) == 1 ? true : false;
//	}

	public boolean addBookmark(ReadItem item) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ReadItem.BOOKMARKS, item.getBookmarksStr());
		return db.update(DBHelper.TB_READ, contentValues, ReadItem.ID + "=?", new String[]{"" + item.id}) == 1 ? true : false;
	}

	/**
	 * @param abstractPath
	 * @return 查一个文件的信息
	 */
	public ReadItem getReadFile(String abstractPath) {
		Cursor cursor = db.rawQuery("select * from " + DBHelper.TB_READ + " where " + ReadItem.FILE_PATH + "=?", new String[]{abstractPath});
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			ReadItem readItem = new ReadItem();
			readItem.id = cursor.getInt(0);
			readItem.filePath = cursor.getString(1);
			readItem.setReadOrbits(cursor.getString(2));
			readItem.setBookmarks(cursor.getString(3));
//			readItem.currentOrbit = cursor.getLong(4);
			return readItem;
		}
		cursor.close();
		return null;
	}

	/**
	 * @param filePath 文件路径
	 * @return 获取文件id
	 */
	public int getReadFileId(String filePath) {
		Cursor cursor = db.rawQuery("select " + ReadItem.ID + " from " + DBHelper.TB_READ + " where " + ReadItem.FILE_PATH + "=?", new String[]{filePath});
		cursor.moveToFirst();
		int _id = 0;
		if (cursor.getCount() > 0) {
			_id = cursor.getInt(0);
		}
		cursor.close();
		return _id;
	}

	/**
	 * 删除一个在数据库中对应的文件信息
	 *
	 * @param filePath
	 * @return
	 */
	public boolean delReadFile(String filePath) {
		return db.delete(DBHelper.TB_READ, "where " + ReadItem.FILE_PATH + "=?", new String[]{filePath}) == 1 ? true : false;
	}

	private boolean delFileById(int id) {
		return db.delete(DBHelper.TB_READ, "where " + ReadItem.ID + "=?", new String[]{"" + id}) == 1 ? true : false;
	}

	/**
	 * 清空表的内容
	 */
	public void clearTable() {
		db.delete(DBHelper.TB_READ, null, null);
	}
}
