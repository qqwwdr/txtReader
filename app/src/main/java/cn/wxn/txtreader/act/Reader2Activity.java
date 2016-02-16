package cn.wxn.txtreader.act;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import cn.wxn.txtreader.R;
import cn.wxn.txtreader.base.BaseActivity;
import cn.wxn.txtreader.bean.ReadItem;
import cn.wxn.txtreader.db.DBDataHelper;
import cn.wxn.txtreader.db.DBReaderHelper;
import cn.wxn.txtreader.utils.FileReaderUtil;
import cn.wxn.txtreader.utils.LogUtil;
import cn.wxn.txtreader.utils.StrUtil;
import cn.wxn.txtreader.view.MScroll;

/**
 * Created by wangxn on 2016/1/21.
 */
@EActivity(R.layout.activity_main2)
public class Reader2Activity extends BaseActivity implements MScroll.OnNeedMoreTxtListener {
	@ViewById
	MScroll sv;

//	@ViewById
//	Toolbar toolbar;

	private static final Object TAG = "Reader2Activity";
	private String   txt;
	private ReadItem readFile;
	private String   file;
	private boolean touchTop    = false;   //文档到达最开始位置
	private boolean touchBottom = false;   //文档到达最末尾位置
	private RandomAccessFile raf;
	private String           code;
	public static final int MAX_LINE_ONCE = 50;

	/**
	 * 设置toolbar
	 */
	private void setToolbar() {
//		setSupportActionBar(toolbar);
//		getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		Toolbar.OnMenuItemClickListener onMeneItemClick = new Toolbar.OnMenuItemClickListener() {
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				return false;
//			}
//		};
//		toolbar.setTitleTextColor(Color.WHITE);
//		toolbar.setOnMenuItemClickListener(onMeneItemClick);
	}

	public static  final String EXTRAS_FILE = "file";

	@AfterViews
	public void initWidget() {
		setToolbar();

		sv.setOnNeedMoreTxtListener(this);

		Intent data = getIntent();
		if (data != null) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				file = extras.getString(EXTRAS_FILE);
				initLoadFile();
			}
		}
	}

	/**
	 * 在初始的接受上个界面传递来的 文档绝对路径时,
	 * 通过路径加载文档信息到控件中,以及部分内容的初始化
	 */
	private void initLoadFile() {
		if (!StrUtil.isEmpty(file)) {
			String title = file.substring(file.lastIndexOf(File.separator));
//			toolbar.setTitle(title);

			new Thread(new Runnable() {
				@Override
				public void run() {
					long seekto = 0;
					readFile = DBReaderHelper.getInstance(getBaseContext()).getReadFile(file);
					if (readFile == null) {
						saveFile(file);                    //如果数据库中没有保存的阅读记录, 那么保存一个阅读记录
					} else {
						seekto = readFile.currentOrbit;   //如果有阅读记录, 获取阅读记录,
						List<Long> readOrbits = readFile.getReadOrbits();       //获取文档的分段偏移指针数组
						if (readOrbits != null && readOrbits.size() > 0) {
							if (!readOrbits.contains(new Long(seekto))) {  //判断阅读记录的 文件偏移指针是否 在 分段偏移指针数组中,不在,则置为0
								seekto = 0L;
							}
						} else {
							seekto = 0L;   //如果文件分段偏移指针数组为空,则 阅读记录的偏移指针也置为0
						}
					}

					try {
						txt = getRandStringFromFile(seekto); //根据偏移指针 获取到文档的一部分文本内容
						if (readFile != null) {                                  //在bean -> readFile,从数据库中获取的,不为空时,
							List<Long> readOrbits = readFile.getReadOrbits();
							if (readOrbits != null && readOrbits.size() > 0) {
								sv.setOrbits(readFile.getReadOrbits(), seekto);  //将整个的文档偏移指针数组传递给控件MScroll
							}
						}
						sv.setStrCode(code);                //将在获取文档字符内容时,获取的文档字符编码设置给 控件MScroll
						if (!StrUtil.isEmpty(txt)) {
							sv.setText(txt, seekto);       //将获取的文档部分文本内容,以及其对应的文档偏移指针  传递给 控件MScroll
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}).start();
		} else {
			LogUtil.e(TAG, "装载文本失败");
		}
	}


	/**
	 * 当 数据库中没有path 对应的文档信息时, 保存文档相关信息到数据库中,
	 * 同时,遍历文档,获取文档中 一系列的文档偏移指针数组, 以及相关信息
	 * 保存第一次阅读的文件信息, 只需要保存一次
	 * @param path 文件路径
	 */
	public void saveFile(final String path) {
		new Thread(new Runnable() {                         //另外启动一个线程
			@Override
			public void run() {
				LogUtil.i(TAG, "saveFile path ===>" + path);
				long begin = System.currentTimeMillis();          //线程开始时的时间戳
				try {
					readFile = new ReadItem();
					int fileId = DBDataHelper.getInstance(getBaseContext()).getFileId(file);   //从文件信息表中查出文件的id,作为外键
					readFile.id = fileId;
					readFile.filePath = file;
					readFile.currentOrbit = 0;

					/////////////////////////以下内容是预读文件内容,获取待分割文件的各个中间点,方便以后访问//////////
					RandomAccessFile raf = new RandomAccessFile(path, "r");                 //新建一个 RandomAccessFile对象, 只读
					code = FileReaderUtil.getRightStringCode(file);                        //解析这个文件,获取文件中文本的 字符编码
					LogUtil.i(TAG, "saveFile path " + path + ", code : " + code);

					List<Long> orbits = new ArrayList<>();                       //列表保存文档中的从开始到结束的一系列位置
					orbits.add(0L);                          //第一个(位置)偏移点必须是 0L

					String s = raf.readLine();    //读取第一行文本
					int index = 1;                //设置index 为1 , 表示已经读取过一行文本
					while (s != null) {                                                      //读取的文本字符串不为null,为null表示到达文档结尾了
						if (index % MAX_LINE_ONCE == 0) {            //每50行,保存一个文件偏移点
							long filePointer = raf.getFilePointer();   //获取 当前 读取文档时的偏移指针,对应读取的内容在文件中的位置
							orbits.add(filePointer);                  //保存这个位置点
						}
						index++;
						s = raf.readLine();                     //读取下一行文本

						if (orbits.size() % 10 == 0) {        //如果位置点的列表 每保存10条数据, 则将这些数据传递给 自定义控件MScroll, 便于文档显示,防止卡顿,文档遍历的时间可能很长
							if (activityState == 0) {     //当前activity必须在onResume状态
								sv.setOrbits(orbits);
							}
						}
					}

					long filePointer = raf.getFilePointer();         //获取文档结尾时的 位置
					orbits.add(filePointer);                     //文件结尾的偏移位置

					readFile.setOrbits(orbits);   //保存文件偏移信息的一个列表到对象bean中

					DBReaderHelper.getInstance(getBaseContext()).saveReadFile(readFile);  //将bean中的内容保存到数据库中

					if (activityState == 0) {                    //当完整的获取到了整个文件中所有的位置列表之后,将完整的位置列表传递给 自定义控件 MScroll
						sv.setOrbits(readFile.getReadOrbits());
					}

					long usedTime = System.currentTimeMillis() - begin;
					LogUtil.i(TAG, "saveFile used time is : " + usedTime);   //对文档遍历时, 线程结束时的时间戳

					/////////////////////////以上内容是预读文件内容,获取待分割文件的各个中间点,方便以后访问//////////
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 根据seekto指定的偏移量 ,从 file中读取偏移量之后的50行文本字符串内容,然后返回内容
	 * @param seekto   在文件中的 位置指针
	 * @return        该位置指针开始,读取50行文本,将这50行文本内容返回
	 * @throws Exception
	 */
	public String getRandStringFromFile(long seekto) throws Exception {
		StringBuffer retVal     = new StringBuffer();   //缓冲读取的文本内容
		int          offset = 0;
		try {
			if (raf == null) {                             //复用一个RandomAccessFile对象就可以了,
				raf = new RandomAccessFile(file, "r");
				code = FileReaderUtil.getRightStringCode(file);
				LogUtil.i(TAG, "getRandStringFromFile path : " + file + ", code : " + code);
			}

			if (seekto >= 0) {            //判断位置是否大于0
				if (seekto == 0) {            //为0,表示从文件最开始位置读取内容
					touchTop = true;     //为true表示从文件最开始位置读取内容,
				} else {
					touchTop = false;   //为false表示该txt内容已经不在是从文件开始读取的了
				}
				raf.seek(seekto);                   //指定RandomAccessFile从那个位置开始读取文本内容
			} else {
				LogUtil.e(TAG, "seek to file point is an negative value!!!!!");
				throw new Exception("seek to file point is an negative value!!!!!");        //小于0 这种情况是不应该出现的,抛一个异常
			}

			touchBottom = false;                              //默认是没有到达文档的最末尾位置
			for (int i = 0; i < MAX_LINE_ONCE; i++) {         //cong 0 dao 50 遍历
				String s = raf.readLine();            //读取一行文本字符串
				if (!TextUtils.isEmpty(s)) {                   //如果文本字符串不为空
					byte[] bytes = s.getBytes("ISO-8859-1");    //获取文本的字节码
					offset += bytes.length;                     //统计字节码的长度,
					retVal.append(new String(bytes, code));    //重新生成字符串,然后添加到StringBuffer缓冲中
					retVal.append('\n');                       //在readLine时,会默认去掉换行符,这里重新加上换行符
				} else {
					if (null == s) {
						touchBottom = true;    //在读取一行的文本为null时,基本上就是表示已经到达文档的最末尾位置了
					}
				}
			}

			return retVal.toString();       //返回整个缓冲之后的文档内容的字符串
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在自定义控件 MScroll 点击屏幕中的左边部分时, 触发一个回掉方法,
	 * @param nextOrbit
	 */
	@Override
	public void onNeedForwardTxt(long nextOrbit) {
		LogUtil.i(TAG, "onNeedForwardTxt : nextOrbit========>" + nextOrbit);
		if (touchBottom) {
			LogUtil.i(TAG, "已到达文档最末尾位置");
			return;
		}
		try {
			String randStringFromFile = getRandStringFromFile(nextOrbit);
			if (!TextUtils.isEmpty(randStringFromFile)) {
				sv.addNextText(randStringFromFile, nextOrbit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNeedBackwardTxt(long preOrbit) {
		LogUtil.i(TAG, "onNeedBackwardTxt : preOrbit======>" + preOrbit);
		if (touchTop) {
			LogUtil.i(TAG, "已到达文档最开始位置");
			return;
		}
		try {
			String randStringFromFile = getRandStringFromFile(preOrbit);
			if (!TextUtils.isEmpty(randStringFromFile)) {
				sv.addPreText(randStringFromFile, preOrbit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
