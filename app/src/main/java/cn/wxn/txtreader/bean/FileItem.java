package cn.wxn.txtreader.bean;

/**
 * Created by wangxn on 2015/12/24.
 */
public class FileItem {
	public static final String[] _FILE_TYPE = {"unknown", "pic", "doc", "music", "video"};

	public static final String ID            = "_id";                      //文件id
	public static final String PARENT_ID     = "parent_id";         //父目录的id
	public static final String FILE_NAME     = "file_name";         //文件名
	public static final String IS_DIRECTORY  = "is_dir";         //是否是文件夹
	public static final String ABSTRACT_PATH = "abstract_path"; // 绝对路径
	public static final String FILE_TYPE     = "file_type";         //文件类型
	public static final String FILE_CAPACITY = "file_capacity"; //文件或者文件夹的容量

	public int     id;
	public int     parent_id;
	public boolean isDirectory;
	public String  fileName;
	public String  abstractPath;

	/**
	 * 取值 0 : unknown
	 *     1 : pic
	 *     2 : doc
	 *     3 : music
	 *     4 : video
	 */
	public int  fileType     = 0;
	public long fileCapacity = 0;
}
