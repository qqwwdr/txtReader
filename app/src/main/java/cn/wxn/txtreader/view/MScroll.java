package cn.wxn.txtreader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import cn.wxn.txtreader.R;
import cn.wxn.txtreader.bean.LineTxt;
import cn.wxn.txtreader.utils.LogUtil;

/**
 * Created by wangxn on 2016/1/27.
 * 显示文字的自定义控件,
 * 同时可以缓冲内容,
 */
public class MScroll extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "MScroll";

	/**
	 * 画字符的线程运行的标识
	 */
	private boolean runFlag = true;


	protected SurfaceHolder holder;
	private   Thread        myThread;

	/**
	 * 控件的宽度
	 */
	private int mWidth;

	/**
	 * 控件的高度
	 */
	private int mHeight;


	//	private int wordsInLineMaxCount = -1;

	/**
	 * 一页显示多少行
	 */
	private int linesInPageMaxCount = -1;

	/**
	 * 点击屏幕时 ,位于控件的x轴坐标
	 */
	private float downRawX;

	/**
	 * 点击屏幕时,位于控件的y轴坐标
	 */
	private float downRawY;

	/**
	 * 上下文
	 */
	private Context mContext;

	/**
	 * 翻页时 的监听接口
	 */
	private OnNeedMoreTxtListener onNeedMoreTxtListener;

	/**
	 * 显示的文本的文件中的一系列指针位置,用于读取文本
	 */
	private ArrayList<Long> txtOrbits;

	/**
	 * 一次只加载2端文本, 这个表示第一段文本的位置,这个值在txtOrbits中
	 */
	private int firstOrbitIndex = 0;   //第一个Orbit

	/**
	 * 这个表示第二段文本的位置, 这个值在txtOrbits中
	 */
	private int secondOrbitIndex = 0;     //第二个Orbit

	/**
	 * 文本的字符编码
	 */
	private String strCode;     //字符编码

	/**
	 * 构造函数
	 * @param context
	 */
	public MScroll(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 */
	public MScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public MScroll(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	/**
	 * 调用构造函数时调用的 初始化方法
	 *
	 * @param context
	 */
	private void init(Context context) {
		this.mContext = context;            //获取上下文
		holder = this.getHolder();         //获取SurfaceView的 SurfaceHolder对象
		holder.addCallback(this);                    //添加SurfaceHolder对象的回掉,
		holder.setFormat(PixelFormat.TRANSPARENT); // 顶层绘制SurfaceView设成透明
		this.setZOrderOnTop(true);                   //顶层显示,显示在屏幕最前面 ???
		this.setFocusable(true);                     //获取焦点

		currentTotalWords = Collections.synchronizedList(new LinkedList<LineTxt>());    //初始化按行存储的文本(这个文本是按照行分割的)的集合,获取该集合的 线程安全的集合
	}

	/**
	 * 处理点击事件,目前只处理 翻页的点击事件,
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				LogUtil.i(TAG, "ACTION_DOWN");
				downRawX = event.getRawX();     //获取按下时的x轴坐标
				downRawY = event.getRawY();     //获取按下时y轴坐标
				return true;                   //拦截事件, 只有返回true,才能响应ACTION_UP时的事件
			case MotionEvent.ACTION_MOVE:
				LogUtil.i(TAG, "ACTION_MOVE");
				break;
			case MotionEvent.ACTION_UP:
				LogUtil.i(TAG, "ACTION_UP");
				float upRawX = event.getRawX();     //获取抬起时的x轴坐标
				float upRawY = event.getRawY();     //获取抬起时 的y轴坐标
				if (Math.abs(upRawX - downRawX) < 20 && (Math.abs(upRawY - downRawY) < 20)) {    //判断位置偏移量是否在范围之内
					LogUtil.i(TAG, "ACTION_CLICK");
					//点击事件
					if (downRawX > mWidth / 3) {                                                  //往前翻页
						if (currentTotalWords != null && currentTotalWords.size() > 0) {         //判断是否有可以显示的文本
							LogUtil.i(TAG, "ACTION_CLICK  往前翻页");
							moveForwardBeginIndex();                                           //移动index

						}
					} else {    //往后翻页
						if (currentTotalWords != null && currentTotalWords.size() > 0) {
							LogUtil.i(TAG, "ACTION_CLICK  往后翻页");
							moveBackwordBeginIndex();
						}
					}
				}
				break;
			default:
				break;
		}
		return false;
	}

	private void moveBackwordBeginIndex() {
//		int newBeginIndex = -1;
		if (this.currentTotalWords != null && this.currentTotalWords.size() > 0) {
			int beginIndex = -1;
			boolean hasNewLocation = false;
			synchronized (this) {
				LogUtil.i(TAG, "currentTotalWords.size() -1  ========>" + (currentTotalWords.size() -1));
				ListIterator<LineTxt> lineTxtListIterator = currentTotalWords.listIterator(currentTotalWords.size() -1);
				LineTxt theLastLineTxt = null;
				while (lineTxtListIterator.hasPrevious()) {
					LineTxt previous = lineTxtListIterator.previous();
					if (previous.isPageFromThisLine) {
//						previous.isPageFromThisLine = false;
						theLastLineTxt = previous;
						beginIndex = 0;
					}

					if (beginIndex == linesInPageMaxCount) {   //指定下一个开始显示的行数,
						LogUtil.i(TAG, "moveBackwordBeginIndex  指定新的开始位置" + beginIndex);
						previous.isPageFromThisLine = true;
						hasNewLocation = true;
						if (theLastLineTxt != null) {
							theLastLineTxt.isPageFromThisLine = false;
						}
					}

					if (beginIndex >= 0) {                    //继续++
						beginIndex++;
					}
				}

				if (!hasNewLocation && firstOrbitIndex == 0 && beginIndex == linesInPageMaxCount) { //第一个页面,设置可以翻到第一个页面
					currentTotalWords.get(0).isPageFromThisLine = true;
					if (theLastLineTxt != null) {
						theLastLineTxt.isPageFromThisLine = false;
					}
				}

			}
			LogUtil.i(TAG, "moveBackwordBeginIndex  beginIndex=====>" + beginIndex);
			if (beginIndex > 0 && beginIndex < 2 * linesInPageMaxCount) {
				if (firstOrbitIndex == 0 && !hasNewLocation){
					LogUtil.i(TAG, "到达文档最开始位置");
					return;
				}
				if (this.onNeedMoreTxtListener != null) {
//					int i = this.txtOrbits.indexOf(new Long(currentOrbit));
					if (this.firstOrbitIndex > 0
							    && this.txtOrbits != null && this.txtOrbits.size() > 0) {
						this.firstOrbitIndex--;
						LogUtil.i(TAG, "调用回调接口,获取新的字符串数据");
						this.onNeedMoreTxtListener.onNeedBackwardTxt(this.txtOrbits.get(this.firstOrbitIndex));

						if (this.secondOrbitIndex - this.firstOrbitIndex > 1) {
							//移除secondOrbitIndex对应的文本内容从缓冲中移除
							LogUtil.i(TAG, "移除secondOrbitIndex对应的文本内容从缓冲中移除");
							removeSecondStrs();
							this.secondOrbitIndex--;
						}
					}
				}
			}
		}
	}

	private void removeSecondStrs() {
		synchronized (MScroll.this) {
			ListIterator<LineTxt> lineTxtListIterator = this.currentTotalWords.listIterator(currentTotalWords.size() - 1);
			while (lineTxtListIterator.hasPrevious()) {
				LineTxt previous = lineTxtListIterator.previous();
				if (previous.seek >= this.txtOrbits.get(secondOrbitIndex)) {
					lineTxtListIterator.remove();
				}else {
					break;
				}
			}
		}
	}

	public void setStrCode(String code) {
		this.strCode = code;
	}

	public interface OnNeedMoreTxtListener {
		void onNeedForwardTxt(long nextOrbit);

		void onNeedBackwardTxt(long preOrbit);
	}

	public void setOnNeedMoreTxtListener(OnNeedMoreTxtListener onNeedMoreTxtListener) {
		this.onNeedMoreTxtListener = onNeedMoreTxtListener;
	}

	private void moveForwardBeginIndex() {
		if (currentTotalWords != null && currentTotalWords.size() > 0) {
			int beginIndex = -1;
			boolean hasNewLocation = false;
			synchronized (this) {
				ListIterator<LineTxt> lineTxtListIterator = currentTotalWords.listIterator();
				LineTxt lastLineTxt = null;
				while (lineTxtListIterator.hasNext()) {
					LineTxt next = lineTxtListIterator.next();
					if (next.isPageFromThisLine) {
						beginIndex = 0;
//						next.isPageFromThisLine = false;
						lastLineTxt = next;
					}
					if (beginIndex == linesInPageMaxCount) {   //指定下一个开始显示的行数,
						next.isPageFromThisLine = true;
						hasNewLocation = true;
						if (lastLineTxt != null) {
							lastLineTxt.isPageFromThisLine = false;
						}
					}
					if (beginIndex >= 0) {                    //继续++
						beginIndex++;
					}
				}
			}
			LogUtil.i(TAG, "moveForwardBeginIndex : beginIndex ===" + beginIndex);
			LogUtil.i(TAG, "moveForwardBeginIndex : linesInPageMaxCount ===" + linesInPageMaxCount);
			LogUtil.i(TAG, "moveForwardBeginIndex : secondOrbitIndex ===" + secondOrbitIndex);
			if (beginIndex > 0 && beginIndex < 2 * linesInPageMaxCount) {                //下一页不够显示一页,需要加载更多内容
				if (txtOrbits != null && secondOrbitIndex == txtOrbits.size() - 2 && !hasNewLocation){
					LogUtil.i(TAG, "到达文档最末尾位置");
					return;
				}
				if (this.onNeedMoreTxtListener != null) {
//					int i = this.txtOrbits.indexOf(new Long(currentOrbit));
					if (this.txtOrbits != null  && this.secondOrbitIndex < this.txtOrbits.size() - 1
							    && this.txtOrbits.size() > 0) {
						this.secondOrbitIndex++;
						LogUtil.i(TAG, "moveForwardBeginIndex : secondOrbitIndex === " + secondOrbitIndex);
						this.onNeedMoreTxtListener.onNeedForwardTxt(this.txtOrbits.get(this.secondOrbitIndex));

						if (this.secondOrbitIndex - this.firstOrbitIndex == 2) {
							LogUtil.i(TAG, "移除secondOrbitIndex对应的文本内容从缓冲中移除");
							//移除secondOrbitIndex对应的文本内容从缓冲中移除
							this.firstOrbitIndex++;
							removeFirstStrs();
						}
					}
				}
			}
		}
	}

	private void removeFirstStrs() {
		synchronized (MScroll.this) {
			ListIterator<LineTxt> lineTxtListIterator = this.currentTotalWords.listIterator();
			while (lineTxtListIterator.hasNext()) {
				LineTxt next = lineTxtListIterator.next();
				if (next.seek < this.txtOrbits.get(firstOrbitIndex)) {
					lineTxtListIterator.remove();
				}else {
					break;
				}
			}
		}
	}

//	public int getWordsInLineMaxCount() {
//		return this.wordsInLineMaxCount;
//	}

	private Paint paint = null;

	/* 自定义线程 */
	class MyRunnable implements Runnable {

		public void run() {
			Canvas canvas = null;

			//整个空间的宽度
			mWidth = MScroll.this.getWidth();
			mHeight = getHeight();
			while (runFlag) {
				try {
					if (paint == null) {
						initPaint();
						getLinesInPageMaxCount(paint);
					}

					Rect rect = new Rect(0, 0, mWidth, mHeight);
					canvas = holder.lockCanvas(rect); // 获取画布
					try {
						if (canvas != null) { // 退出时holder.lockCanvas（）方法可能返回空，未免报空指针异常
							// 清除画布方法一
							canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
							if (currentTotalWords != null && currentTotalWords.size() > 0) {
								int index = 0;
								synchronized (MScroll.this) {
									int line = -1;
									ListIterator<LineTxt> lineTxtListIterator = currentTotalWords.listIterator();

									while (lineTxtListIterator.hasNext()) {
										LineTxt next = lineTxtListIterator.next();
										if (next.isPageFromThisLine) {
											line = 0;
										}

										if (line >= 0 && line < linesInPageMaxCount) {
											String lineTxt = next.lineTxt;
											if (!TextUtils.isEmpty(lineTxt)) {
												canvas.drawText(lineTxt, 0, lineTxt.length(), (int)(1.5 * PADDING), PADDING + (line + 1) * (paint.getTextSize() + PADDING), paint);
											}
										}

										if (line >= 0) {
											line++;
										}
									}

								}
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
					}
					Thread.sleep(200);
				} catch (InterruptedException e) {
					LogUtil.e(TAG, "ScrollSurfaceView：绘制失败...\r\n" + e);
				} catch (Exception e) {
					LogUtil.e(TAG, "ScrollSurfaceView：run...\r\n" + e);
				}
			}
		}

		/**
		 * 初始化paint
		 */
		private void initPaint() {
			paint = new Paint();
			initFont(paint);
		}

		/**
		 * 设置TextView的字体
		 *
		 * @param paint
		 */
		void initFont(Paint paint) {
			paint.setTextAlign(Paint.Align.LEFT);
			// 大小
			paint.setTextSize(64);
			// 字体
			paint.setTypeface(Typeface.defaultFromStyle(0));
			// 颜色
			int color = getContext().getResources().getColor(R.color.color_black);
			paint.setColor(color);
		}

	}

	@Override
	/**
	 * 当控件创建时自动执行的方法
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// 启动自定义线程
		myThread = new Thread(new MyRunnable());
		runFlag = true;
		myThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	/**
	 * 当控件销毁时自动执行的方法
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 终止自定义线程
		runFlag = false;
		myThread.interrupt();
	}

	public static final int PADDING = 40;
	private List<LineTxt> currentTotalWords;

	/**
	 * 初始内容增加
	 * 设置显示的内容
	 *
	 * @param txt
	 */
	public void setText(String txt, long startOffset) {
		this.currentTotalWords.clear();
		this.currentTotalWords.addAll(split2Lines(txt, startOffset));
		this.currentTotalWords.get(0).isPageFromThisLine = true;
	}

	public void setOrbits(List<Long> orbits){
		if (this.txtOrbits == null) {
			this.txtOrbits = new ArrayList<Long>();
		} else {
			this.txtOrbits.clear();
		}
		this.txtOrbits.addAll(orbits);
	}

	/**
	 * 设置文档轨迹列表,同时设置读取的起始位置
	 *
	 * @param orbits
	 * @param startOrbit
	 */
	public void setOrbits(List<Long> orbits, long startOrbit) {
		if (this.txtOrbits == null) {
			this.txtOrbits = new ArrayList<Long>();
		} else {
			this.txtOrbits.clear();
		}
		this.txtOrbits.addAll(orbits);

		int index = this.txtOrbits.indexOf(new Long(startOrbit));
		if (index > 0) {
			this.firstOrbitIndex = this.secondOrbitIndex = index;
			LogUtil.i(TAG, "setOrbits  txtOrbits size ==========>" + txtOrbits.size());
			LogUtil.i(TAG, "setOrbits  firstOrbitIndex, secondOrbitIndex ====> " + "(" + firstOrbitIndex + " ," + secondOrbitIndex + ")");
		}
	}

//	public void setCurrentOrbit(long currentOrbit){
////		this.currentOrbit = currentOrbit;
//		this.firstOrbit = currentOrbit;
//		this.secondOrbit = currentOrbit;
//	}

	/**
	 * 添加更多显示内容
	 *
	 * @param txt
	 */
	public void addPreText(String txt, long startOffset) {
		synchronized (this) {
			this.currentTotalWords.addAll(0, split2Lines(txt, startOffset));
		}
	}

	/**
	 * 往回翻页时添加内容
	 * @param txt
	 * @param startOffset 开始的在文件中的位置
	 */
	public void addNextText(String txt, long startOffset) {
		synchronized (this) {
			this.currentTotalWords.addAll(split2Lines(txt, startOffset));
		}
	}


	private void getLinesInPageMaxCount(Paint paint) {
		Float v1 = (mHeight - 2 * PADDING) / (paint.getTextSize() + PADDING);
		linesInPageMaxCount = v1.intValue();                            //每一页显示多少行
	}

	public List<LineTxt> split2Lines(String txt, long startOffset) {
		List<LineTxt> retVal = new LinkedList<>();
		StringBuilder sb     = new StringBuilder();
		char          tmp    = '\0';
		long          offset = startOffset;
		try {
			for (int i = 0; i < txt.length(); i++) {
				tmp = txt.charAt(i);
				sb.append(tmp);
				if (i == txt.length() - 1) {                                   //txt的结尾
					String str = sb.toString();
					int strOffset = str.getBytes(strCode).length;  //字符串字节偏移量
					LineTxt lineTxt = new LineTxt(str, offset);
					retVal.add(lineTxt);
					sb.delete(0, sb.length());
					offset += strOffset;                    //增加下一个时的字节偏移量
				} else if ('\n' == tmp) {                //换行符
					String str = sb.toString();
					int strOffset = str.getBytes(strCode).length;   //字符串字节偏移量
					LineTxt lineTxt = new LineTxt(str, offset);
					retVal.add(lineTxt);
					sb.delete(0, sb.length());
					offset += strOffset;                    //增加下一个时的字节偏移量
				} else {
					float v = paint.measureText(sb.toString(), 0, sb.length());  //测量的当前分割的字符串显示的长度是否可以塞满一行
					if (v >= mWidth - 4 * PADDING) {      //可以塞满屏幕宽度
						String str = sb.toString();
						int strOffset = str.getBytes(strCode).length;   //字符串字节偏移量
						LineTxt lineTxt = new LineTxt(str, offset);
						retVal.add(lineTxt);
						sb.delete(0, sb.length());
						offset += strOffset;                    //增加下一个时的字节偏移量
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return retVal;
	}
}
