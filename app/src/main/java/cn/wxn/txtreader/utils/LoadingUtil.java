package cn.wxn.txtreader.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.wxn.txtreader.R;


public class LoadingUtil {

	private Dialog    pd;
	private ImageView loading;
	private boolean showLoading = true;
	private Activity activity;
	private String   dialogTitle;

	/**
	 * 构造方法
	 *
	 * @param activity 这个context 其实应该是Activity , 否则要报错
	 */
	public LoadingUtil(Activity activity) {
		this(activity, activity.getResources().getString(R.string.loading));
	}

	/**
	 * 构造方法
	 *
	 * @param dialogTitle
	 */
	public LoadingUtil(Activity activity, String dialogTitle) {
		super();
		this.activity = activity;
		this.dialogTitle = dialogTitle;
	}

	/**
	 * @param activity
	 * @param dialogTitle
	 * @param canCancel
	 */
	public LoadingUtil(Activity activity, String dialogTitle, boolean canCancel) {
		super();
		this.activity = activity;
		this.dialogTitle = dialogTitle;
		this.canCancel = canCancel;
	}


	private TextView tv_loading;

	public void startShowLoading() {
		if (activity instanceof Activity) {
			if (activity.isFinishing()) return;
		}
		if (showLoading && activity != null) {
			if (this.pd == null || !this.pd.isShowing()) {
				this.pd = createDialog();
				this.pd.show();
			}
		} else {
			this.pd = null;
		}
	}

	public boolean isShowLoading() {
		if (this.pd != null && this.pd.isShowing()) {
			return true;
		}
		return false;
	}

	public void stopShowLoading() {
		// 回调
		if (this.pd != null && this.pd.isShowing() && activity != null
				    && !activity.isFinishing()) {
			try {
				this.pd.dismiss();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}
	}

	private Dialog createDialog() {

		View view = View.inflate(activity, R.layout.layout_loading, null);
		loading = (ImageView) view.findViewById(R.id.loading);
		tv_loading = (TextView) view.findViewById(R.id.tv_loading);

		tv_loading.setText(dialogTitle);

		Animation          operatingAnim = AnimationUtils.loadAnimation(activity, R.anim.tip);
		LinearInterpolator lin           = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		loading.setAnimation(operatingAnim);

//////////////////////////////////////////////////
		Dialog dialog = new Dialog(activity, R.style.update_dialog1);
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				                                                         LinearLayout.LayoutParams.MATCH_PARENT,
				                                                         LinearLayout.LayoutParams.MATCH_PARENT));
		dialog.setCancelable(canCancel);
		dialog.setCanceledOnTouchOutside(touchCancel);

		Window                     window = dialog.getWindow();
		WindowManager.LayoutParams lp     = window.getAttributes();
		// 设置透明度为0.3
		lp.alpha = 0.6f;
//        lp.width = ScreenUtils.getInstance(activity).dip2px(135);
//        lp.height = ScreenUtils.getInstance(activity).dip2px(90);
		window.setAttributes(lp);

		return dialog;
	}

	private boolean canCancel   = true;
	private boolean touchCancel = false;

	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
		if (tv_loading != null) {
			tv_loading.setText(dialogTitle);
		}
	}
}
