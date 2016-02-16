package cn.wxn.txtreader.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.wxn.txtreader.bean.FileItem;
import cn.wxn.txtreader.bean.ReadItem;
import cn.wxn.txtreader.utils.LogUtil;

/**
 * Created by wangxn on 2016/1/25.
 */
public class DBHelper extends SQLiteOpenHelper {

	public static final String TB_NAME = "files_table"; //表名
	public static final String TB_READ = "read_table"; //表名

	public static final int DB_VERSION = 2;    //数据库版本
	private static final String TAG        = "DBHelper" ;

	public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	private Context context;

	public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		this.context = context;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " +
				           TB_NAME + " ( " +
				           FileItem.ID + " integer primary key autoincrement ," +
				           FileItem.PARENT_ID + " integer, " +
				           FileItem.ABSTRACT_PATH + " varchar, " +
				           FileItem.FILE_NAME + " varchar, " +
				           FileItem.IS_DIRECTORY + " boolean, " +
				           FileItem.FILE_TYPE + " varchar, " +
				           FileItem.FILE_CAPACITY + " bigint" +
				           " )"
		);


		db.execSQL("CREATE TABLE IF NOT EXISTS " +
				           TB_READ + " ( " +
				           ReadItem.ID + " integer primary key , " +
						   ReadItem.FILE_PATH + " varchar, " +
						   ReadItem.READ_ORBITS + " varchar, " +
						   ReadItem.BOOKMARKS + " varchar " +
						   " )"
				);

		LogUtil.i(TAG, "DB onCreate!!!!!");
//		SPUtils.saveBoolean(context, "hasTable", false);
//		Intent intent1 = new Intent();
//		intent1.setAction("cn.wxn.txtreader.fill_table");
//		context.sendBroadcast(intent1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS" + TB_NAME);
		onCreate(db);
		LogUtil.i(TAG, "DB onUpgrade!!!!");

		if (oldVersion == 1 && newVersion == 2){
			// 	增加字段 ReadItem.CURRENT_ORBIT + " bigint"
			db.execSQL("ALTER TABLE " + TB_READ + " ADD " + ReadItem.CURRENT_ORBIT + " bigint");
		}
	}
}
