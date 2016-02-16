package feezu.cn.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangxn on 2016/1/27.
 */
public class MScroll extends SurfaceView implements SurfaceHolder.Callback {
	private static final String  TAG     = "MScroll";
	private              boolean runFlag = true;
	protected SurfaceHolder holder;
	private   Thread        myThread;
	private   int           mWidth;
	private   int           mHeight;

	private String currentText;
	private String nextText;
	
	private int    wordsInLine = -1;
	private List<String> mTexts;
	private int wordsInLineMaxCount = -1;
	private int linesInPageMaxCount = -1;
	private float downRawX;
	private float downRawY;
	private int currentPage = 0;

	public MScroll(Context context) {
		super(context);
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.TRANSPARENT); // 顶层绘制SurfaceView设成透明
		this.setZOrderOnTop(true);
		this.setFocusable(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				LogUtil.i(TAG, "ACTION_DOWN");
				downRawX = event.getRawX();
				downRawY = event.getRawY();
				return true;
			case MotionEvent.ACTION_MOVE:
				LogUtil.i(TAG, "ACTION_MOVE");
				break;
			case MotionEvent.ACTION_UP:
				LogUtil.i(TAG, "ACTION_UP");
				float upRawX = event.getRawX();
				float upRawY = event.getRawY();
				if (Math.abs(upRawX - downRawX) < 20 && (Math.abs(upRawY - downRawY) < 20)) {
					LogUtil.i(TAG, "ACTION_CLICK");
					//点击事件
					if (downRawX > mWidth / 3) {  //往前翻页
						if (currentTotalWords != null && currentTotalWords.size() > 0 && currentPage < currentTotalWords.size() - 1) {
							LogUtil.i(TAG, "ACTION_CLICK  往前翻页");
							currentPage++;
						}
					} else {    //往后翻页
						if (currentTotalWords != null && currentTotalWords.size() > 0 && currentPage > 0) {
							LogUtil.i(TAG, "ACTION_CLICK  往后翻页");
							currentPage--;
						}
					}
				}
				break;
			default:
				break;
		}
		return false;
	}

	/* 自定义线程 */
	class MyRunnable implements Runnable {
		private Paint paint = null;

		public void run() {
			Canvas canvas = null;

			//整个空间的宽度
			mWidth = MScroll.this.getWidth();
			mHeight = getHeight();
			while (runFlag) {
				try {
					if (paint == null) {
						initPaint();
					}

					if (currentText != null && currentText.length() > 0) {
						if (currentTotalWords == null || currentTotalWords.size() == 0) {
							split2Lines(paint);
						}
					}
					
					if (nextText != null && nextText.length() > 0){
						
					}

					Rect rect = new Rect(0, 0, mWidth, mHeight);
					canvas = holder.lockCanvas(rect); // 获取画布
					if (canvas != null) { // 退出时holder.lockCanvas（）方法可能返回空，未免报空指针异常
						// 清除画布方法一
						canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
						char[][] chars = currentTotalWords.get(currentPage);
						for (int i = 0; i < chars.length; i++) {
							canvas.drawText((chars)[i], 0, (chars)[i].length, PADDING, (i + 1) * paint.getTextSize(), paint);
						}
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
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	                           int height) {
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

	public static final int PADDING = 20;

	public void setText(String text) {
		this.currentText = text;
	}


	public static final char CHINESE_SPACE = '　';
	private char[][] words;

	private List<char[][]> currentTotalWords = new ArrayList<>();
	private List<char[][]> nextTotalWords = new ArrayList<>();

	private void split2Lines(Paint paint) {
		initWords(paint);

		if (currentTotalWords == null) {
			currentTotalWords = new ArrayList<>();
		} else {
			currentTotalWords.clear();
		}

		int wordsInLineCount = 0;
		int linesInPageCount = 0;
		for (int i = 0; i < currentText.length(); i++) {
			char tmp = currentText.charAt(i);

			if (i == 0) {
				words[0][0] = CHINESE_SPACE;
				words[0][1] = CHINESE_SPACE;
				wordsInLineCount += 2;
			}

			if (i == currentText.length() -1){
				char[][] clone = cloneChars();
				currentTotalWords.add(clone);
				clearWords();
				words[0][0] = tmp;
				wordsInLineCount = 1;               //二维数组的索引置为0,从头开始装载
				linesInPageCount = 0;
				continue;                           //跳过本次循环,继续装载字符
			}

			if ('\n' == tmp || wordsInLineCount >= wordsInLineMaxCount - 1) {   //换行
				if (linesInPageCount >= linesInPageMaxCount - 1) {               //换页
//					char[][] clone = Arrays.copyOf(words, wordsInLineMaxCount);    //保存当前的二维char数组的副本,放置到currentTotalWords的列表中, 用于显示一页
					char[][] clone = cloneChars();
					currentTotalWords.add(clone);
					clearWords();
					words[0][0] = tmp;
					wordsInLineCount = 1;               //二维数组的索引置为0,从头开始装载
					linesInPageCount = 0;
					continue;                           //跳过本次循环,继续装载字符
				}

				//只是换行
				linesInPageCount++;   //行标+1
				wordsInLineCount = 0; //列标置0

				if ('\n' == tmp) {              //如果当前的字符是换行符,那么下一行,首行缩进2个字符
					words[linesInPageCount][0] = CHINESE_SPACE;
					words[linesInPageCount][1] = CHINESE_SPACE;
					wordsInLineCount += 2;
				}
			}

			words[linesInPageCount][wordsInLineCount] = tmp;
			wordsInLineCount++;      //行字符自增
		}

		LogUtil.i(TAG, "currentTotalWords length is : " + currentTotalWords.size());
	}

	private char[][] cloneChars() {
		char[][] clone = new char[linesInPageMaxCount][wordsInLineMaxCount];
		for (int j = 0; j<words.length; j++) {
			char[] item = new char[wordsInLineMaxCount];
			System.arraycopy(words[j], 0, item, 0, wordsInLineMaxCount);
			clone[j] = item;
		}
		return clone;
	}

	/**
	 * //清空用于遍历的二维数组,用于装载第二页的字符
	 */
	private void clearWords() {
		for (int k = 0; k < words.length; k++) {
			for (int j = 0; j < words[k].length; j++) {
				words[k][j] = '\0';
			}
		}
	}

	/**
	 * 初始化字符串数组,二维数组
	 * @param paint
	 */
	private void initWords(Paint paint) {
		if (words == null) {
			float textSize = paint.getTextSize();
			Float v = (mWidth - 2 * PADDING) / textSize;
			wordsInLineMaxCount = v.intValue();                              //每一行显示多少字
			Float v1 = (mHeight - 2 * PADDING) / textSize;
			linesInPageMaxCount = v1.intValue();                            //每一页显示多少行
			words = new char[linesInPageMaxCount][wordsInLineMaxCount];

			LogUtil.i(TAG, "[linesInPageMaxCount][wordsInLineMaxCount] =" + linesInPageMaxCount + "," + wordsInLineMaxCount);
		}
	}
}
