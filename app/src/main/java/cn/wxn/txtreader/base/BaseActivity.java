package cn.wxn.txtreader.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import cn.wxn.txtreader.utils.LogUtil;
import cn.wxn.txtreader.utils.StrUtil;


/**
 * 应用程序Activity的基类
 *
 * @author kymjs
 * @version 1.0
 * @created 2013-11-24
 */
public abstract class BaseActivity extends AppCompatActivity {
	public static final int    ACTIVITY_ONCREATE  = -2;
	public static final int    ACTIVITY_ONSTART   = -1;
	public static final int    ACTIVITY_ONRESTART = -10;
	public static final int    ACTIVITY_RESUME    = 0;
	public static final int    ACTIVITY_STOP      = 1;
	public static final int    ACTIVITY_PAUSE     = 2;
	public static final int    ACTIVITY_DESTROY   = 3;
	private static final Object TAG                = "BaseActivity";
	public               int    activityState      = 100;

	/**
	 * ************************************************************************
	 * <p/>
	 * 打印Activity生命周期
	 * <p/>
	 * *************************************************************************
	 */


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.i(this, this.toString() + "---------onCreat ");
		super.onCreate(savedInstanceState);
		activityState = ACTIVITY_ONCREATE;
		//复写initContentView()这个方法之后,就可以加载布局了,
		//同时不需要复写onCteate()方法了
		//如果不复写initCOntentView()方法则需要onCreate方法加载布局
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.e(this, this.toString() + "---------onStart*** ");
		activityState = ACTIVITY_ONSTART;
	}

	@Override
	protected void onResume() {
		super.onResume();
//		MobclickAgent.onResume(this);
		activityState = ACTIVITY_RESUME;
		LogUtil.e(this, this.toString() + "---------onResume*** ");
	}


	@Override
	protected void onStop() {
		super.onStop();
		activityState = ACTIVITY_STOP;
		LogUtil.i(this, this.toString() + "---------onStop ");
	}

	@Override
	protected void onPause() {
		activityState = ACTIVITY_PAUSE;
		LogUtil.i(this, this.toString() + "---------onPause ");
		super.onPause();
//		MobclickAgent.onPause(this);
	}

	@Override
	protected void onRestart() {
		LogUtil.i(this, this.toString() + "---------onRestart ");
		super.onRestart();
		activityState = ACTIVITY_ONRESTART;
	}

	@Override
	protected void onDestroy() {
		activityState = ACTIVITY_DESTROY;
		LogUtil.i(this, this.toString() + "---------onDestroy ");
		super.onDestroy();
	}

	/**
	 * 通过Class跳转界面
	 */
	public void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/**
	 * 含有Bundle通过Class跳转界面
	 */
	public void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
		//这里有2个动画的配置文件
//        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
//        overridePendingTransition(R.anim.hyperspace_in, R.anim.hyperspace_out);
//        overridePendingTransition(R.anim.page_jump_in, R.anim.page_jump_out);

	}

	/**
	 * 通过Action跳转界面
	 */
	public void startActivity(String action) {
		startActivity(action, null);
	}

	/**
	 * 含有Bundle通过Action跳转界面
	 */
	public void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
//        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
//        overridePendingTransition(R.anim.hyperspace_in, R.anim.hyperspace_out);
//        overridePendingTransition(R.anim.page_jump_in, R.anim.page_jump_out);

	}


	/**
	 * 含有Bundle通过Class打开编辑界面
	 */
	public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
//        overridePendingTransition(R.anim.hyperspace_in, R.anim.hyperspace_out);
//        overridePendingTransition(R.anim.page_jump_in, R.anim.page_jump_out);

	}

	/**
	 * 跳转到某个Activity ,同时清空改activity上的所有activity
	 *
	 * @param context
	 * @param clazz
	 */
	public void startActivityClearTop(Context context, Class clazz, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(context, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/**
	 * 含有Bundle通过Class跳转界面
	 */
	public void startActivityAndFinish(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
		// 这里有2个动画的配置文件
		// overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		// overridePendingTransition(R.anim.hyperspace_in,
		// R.anim.hyperspace_out);
//		overridePendingTransition(R.anim.page_jump_in, R.anim.page_jump_out);
		this.finish();
//		overridePendingTransition(R.anim.page_jump_in, R.anim.page_jump_out);
	}


	public void startActivityAndFinish(Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		startActivity(intent);
		// 这里有2个动画的配置文件
		// overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		// overridePendingTransition(R.anim.hyperspace_in,
		// R.anim.hyperspace_out);
//		overridePendingTransition(R.anim.page_jump_in, R.anim.page_jump_out);
		this.finish();
//		overridePendingTransition(R.anim.page_jump_in, R.anim.page_jump_out);
	}

	/**
	 * 指定的TextView显示指定的文本内容信息, 如果文本内容为空, TextView显示空白
	 *
	 * @param tv
	 * @param txt
	 */
	protected void showText(TextView tv, String txt) {
		if (this.isFinishing()) {
			return;
		}
		if (!StrUtil.isEmpty(txt)) {
			tv.setText(txt);
		} else {
			tv.setText("");
		}
	}


	public <T> T find(View parent, int resId) {
		return (T) parent.findViewById(resId);
	}

}
