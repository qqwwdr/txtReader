package cn.wxn.txtreader.bean;

/**
 * Created by wangxn on 2016/2/2.
 *  *
 * 一行需要显示的文本
 */
public class LineTxt {
	public LineTxt() {
		this.lineTxt = "";
		isPageFromThisLine = false;
		this.seek = -1;
	}

	public LineTxt(String lineTxt, long offset) {
		this.lineTxt = lineTxt;
		this.seek = offset;
		this.isPageFromThisLine = false;
	}

	public long    seek;   //相对于文档的起点的位置偏移量,
	public String  lineTxt;
	public boolean isPageFromThisLine;
}
