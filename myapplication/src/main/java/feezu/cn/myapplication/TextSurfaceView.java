package feezu.cn.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


/**
 * Created by wangxn on 2016/1/27.
 */
public class TextSurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable {


	private static final Object TAG = "TextSurfaceView";
	private SurfaceHolder mHolder;
	private Context       mContext;
	private String        mTxt;
	public  int           showCount;
	private boolean       mbLoop;
	private int           height;
	private int width;

	private void init(Context context) {
		this.mContext = context;
		this.mHolder = this.getHolder();
		this.mHolder.addCallback(this);
	}

	public TextSurfaceView(Context context) {
		super(context);
		init(context);
	}


	public TextSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TextSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//开启绘图线程
		mbLoop = true;
		new Thread(this).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mbLoop = false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	public void showText(String txt) {
		this.mTxt = txt;
	}

	@Override
	public void run() {
		height = getHeight();
		width = getWidth();
		while (mbLoop) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (mHolder) {
				draw();
			}
		}
	}

	private void draw() {
		if (mHolder == null) {
			return;
		}
		Canvas canvas = mHolder.lockCanvas();
		if (canvas == null) {
			return;
		}


		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(mContext.getResources().getColor(R.color.light_gray_bg));


		RectF rect   = new RectF(0, 0, width, height);
		canvas.drawRect(rect, paint);

		paint.setColor(mContext.getResources().getColor(R.color.color_black));
		if (!StrUtil.isEmpty(mTxt)) {
			canvas.drawText(mTxt, 0, 0, paint);
		}
		mHolder.unlockCanvasAndPost(canvas);
	}
}
