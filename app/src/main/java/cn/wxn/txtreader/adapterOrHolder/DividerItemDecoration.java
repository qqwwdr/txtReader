package cn.wxn.txtreader.adapterOrHolder;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangxn on 2016/1/22.
 */
public class DividerItemDecoration extends DefaultDecorartion {

	private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

	public static final int HORIZONAL_LIST = LinearLayoutManager.HORIZONTAL;
	public static final int VERTICAL_LIST  = LinearLayoutManager.VERTICAL;

	private Drawable mDivider;

	private int mOrientation;

	/**
	 * 构造方法
	 *
	 * @param context
	 * @param orientation
	 */
	public DividerItemDecoration(Context context, int orientation) {
		final TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
		mDivider = typedArray.getDrawable(0);
		typedArray.recycle();

		setOrientation(orientation);
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent) {
//		super.onDraw(c, parent);

		if (mOrientation == VERTICAL_LIST) {
			drawVertical(c, parent);
		} else {
			drawHorizontal(c, parent);
		}
	}

	@Override
	public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
		if (mOrientation == VERTICAL_LIST){
			outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
		}else {
			outRect.set(0,0, mDivider.getIntrinsicWidth(), 0);
		}
	}

	/**
	 * 水平方向排列
	 * @param c
	 * @param parent
	 */
	private void drawHorizontal(Canvas c, RecyclerView parent) {
		final int top    = parent.getPaddingTop();
		final int bottom = parent.getHeight() - parent.getPaddingBottom();

		int childCount = parent.getChildCount();
		for (int i = 0; i<childCount; i++){
			View childAt = parent.getChildAt(i);
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();

			final int left = childAt.getRight() + layoutParams.rightMargin;
			final int right = left + mDivider.getIntrinsicHeight();

			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	/**
	 * 垂直方向排列
	 * @param c
	 * @param parent
	 */
	private void drawVertical(Canvas c, RecyclerView parent) {
		final int paddingLeft = parent.getPaddingLeft();
		final int right       = parent.getWidth() - parent.getPaddingRight();

		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childAt = parent.getChildAt(i);
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();
			final int top = childAt.getBottom() + layoutParams.bottomMargin;
			final int bottom = top + mDivider.getIntrinsicHeight();

			mDivider.setBounds(paddingLeft, top, right, bottom);
			mDivider.draw(c);
		}
	}

	private void setOrientation(int orientation) {
		if (orientation != HORIZONAL_LIST && orientation != VERTICAL_LIST) {
			throw new IllegalArgumentException("invalid orientation");
		}
		mOrientation = orientation;
	}
}
