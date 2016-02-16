package cn.wxn.txtreader.bean;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.wxn.txtreader.utils.StrUtil;

/**
 * Created by wangxn on 2016/1/27.
 */
public class ReadItem {
	public static final String ID = "_id";
	public static final String FILE_PATH = "path";
	public static final String READ_ORBITS = "read_orbits";
	public static final String BOOKMARKS = "bookmarks";

	public static final String CURRENT_ORBIT = "current_orbit";    //新增字段  ,用户当前正在阅读的文件偏移位置,这个值一定在read_orbits中


	public int id;
	public String filePath;
	private List<Long> readOrbits;
	private List<Long> bookmarks;

	public long currentOrbit = 0;

	public void setBookmarks(String bookmarks){
		List<Long> marks = getLongs(bookmarks);
		if (marks != null && marks.size() > 0) {
			if (this.bookmarks == null) {
				this.bookmarks = new ArrayList<>();
			}else {
				this.bookmarks.clear();
			}
			this.bookmarks.addAll(marks);
		}
	}

	public void addBookmark(long mark){
		if (this.bookmarks == null){
			this.bookmarks = new ArrayList<>();
		}
		this.bookmarks.add(mark);
	}

	/**
	 * 设置 Orbits的集合内容,清空原来的内容
	 * @param orbits
	 */
	public void setOrbits(List<Long> orbits){
		if (this.readOrbits == null){
			this.readOrbits = new ArrayList<>();
		}else {
			this.readOrbits.clear();
		}
		this.readOrbits.addAll(orbits);
	}

	public boolean addOrbit(long orbit){
		if (this.readOrbits == null){
			this.readOrbits = new ArrayList<>();
		}

		if (this.readOrbits.size() > 0 && orbit == this.readOrbits.get(this.readOrbits.size() -1)){
			return false;
		}

		return this.readOrbits.add(orbit);
	}

	public List<Long> getBookmarks(){
		return this.bookmarks;
	}

	public String getBookmarksStr(){
		return getStringFromList(this.bookmarks);
	}

	public void setReadOrbits(String orbis){
		List<Long> orbits = getLongs(orbis);
		if (orbits != null && orbits.size() > 0) {
			if (this.readOrbits == null) {
				this.readOrbits = new ArrayList<>();
			}else {
				this.readOrbits.clear();
			}
			this.readOrbits.addAll(orbits);
		}
	}

	public List<Long> getReadOrbits(){
		return this.readOrbits;
	}

	public String getReadOrbitsStr(){
		return getStringFromList(this.readOrbits);
	}

	private String getStringFromList(List<Long> items) {
		if (items == null || items.size() == 0){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int          size = items.size();
		for (int i = 0; i<size; i++ ){
			sb.append(items.get(i));
			if (i != size -1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	@NonNull
	private List<Long> getLongs(String orbis) {
		List<String> strings = StrUtil.split2List(orbis, ",");
		if (strings == null || strings.size() == 0){
			return null;
		}
		List<Long> orbits = new ArrayList<>();
		for (String s : strings){
			if (!StrUtil.isEmpty(s)){
				Long mark = StrUtil.getLong(s);
				if (mark != null && mark >= 0){
					orbits.add(mark);
				}
			}
		}
		return orbits;
	}
}
