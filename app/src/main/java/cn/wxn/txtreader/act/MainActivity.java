package cn.wxn.txtreader.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.wxn.txtreader.R;
import cn.wxn.txtreader.base.AppManager;
import cn.wxn.txtreader.base.BaseActivity;
import cn.wxn.txtreader.base.BaseFragment;
import cn.wxn.txtreader.db.DBReaderHelper;
import cn.wxn.txtreader.frag.AutoFilesFragment_;
import cn.wxn.txtreader.frag.HomeFragment_;
import cn.wxn.txtreader.service.AutoSearchService;
import cn.wxn.txtreader.utils.LogUtil;
import cn.wxn.txtreader.utils.ScreenUtils;
import cn.wxn.txtreader.utils.ToastUtil;

@EActivity(R.layout.activity_home)
public class MainActivity extends BaseActivity {

	private static final Object TAG = "MainActivity";
	@ViewById
	LinearLayout ll_container;

	@ViewById
	DrawerLayout drawer;

	@ViewById
	LinearLayout left_drawer;

	@ViewById
	ListView drawer_menu;

	@ViewById
	Toolbar toolbar;

	private List                mPlanetTitles;
	private FragmentTransaction fragmentTransaction;
	private long lastBackPress = -1;
	BaseFragment currentFragment = null;
	private BroadcastReceiver receiver1;
	private Animation         rorateAnimation;
	private ImageView iv;

	@Override
	protected void onDestroy() {
		if (receiver1 != null) {
			unregisterReceiver(receiver1);
		}
		super.onDestroy();
	}

	@AfterViews
	void initWidget() {
		setToolbar();
		setSlideMenu();

		showFragment(new HomeFragment_());

		registBroadcast();

		rorateAnimation = AnimationUtils.loadAnimation(this, R.anim.tip);
		rorateAnimation.setInterpolator(new LinearInterpolator());
	}

	/**
	 * 注册广播接收者
	 */
	private void registBroadcast() {
		receiver1 = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle extras  = intent.getExtras();
				String refresh = extras.getString(AutoSearchService.REFRESH_FLAG);
				if (AutoSearchService.REFRESH_FLAG.equals(refresh)) {
					//后台已经完成了对存储目录的扫描
					LogUtil.i(TAG, "后台已经完成了对存储目录的扫描");
					if (activityState == ACTIVITY_RESUME && currentFragment.getClass().getSimpleName().equals("AutoFilesFragment_")) {
						if (rorateAnimation != null && iv != null) {
							iv.clearAnimation();
						}
						currentFragment.onCall(null);
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AutoSearchService.ACTION_REFRESH);
		registerReceiver(receiver1, intentFilter);   //注册广播接收者
	}

	private void setSlideMenu() {
		///////////////////////////////////////////////////////
		mPlanetTitles = new ArrayList<String>() {
		};
//		mPlanetTitles.add("打开目录");
		mPlanetTitles.add("自动搜索");
		mPlanetTitles.add("书架");
		mPlanetTitles.add("最近浏览");
		mPlanetTitles.add("清空记录");
		drawer_menu.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));  //侧滑菜单的内容及事件
		drawer_menu.setOnItemClickListener(new DrawerItemClickListener());

		////////////////////////////////////////////////////////////
		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close); //管理toolbar和侧滑菜单的动效
		actionBarDrawerToggle.syncState();
		drawer.setDrawerListener(actionBarDrawerToggle);
	}

	private void setToolbar() {
		setSupportActionBar(toolbar);
		getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		Toolbar.OnMenuItemClickListener onMeneItemClick = new Toolbar.OnMenuItemClickListener() {
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				return false;
//			}
//		};
		toolbar.setTitleTextColor(Color.WHITE);
//		toolbar.setOnMenuItemClickListener(onMeneItemClick);
	}

	private void showFragment(BaseFragment frag) {
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.ll_container, frag);
//		fragmentTransaction.addToBackStack(null);   // 不适用回退栈
		fragmentTransaction.commit();
		currentFragment = frag;
	}

	class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			drawer.closeDrawer(left_drawer);
			switch (position) {
				case 0:
					ToastUtil.showShort(getApplication(), (String) mPlanetTitles.get(position));

					if (currentFragment != null && "AutoFilesFragment_".equals(currentFragment.getClass().getSimpleName())) {
						break;
					}
					AutoFilesFragment_ autoFilesFragment = new AutoFilesFragment_();
					showFragment(autoFilesFragment);
					currentFragment = autoFilesFragment;
					setMenu();
					break;
				case 1:
					ToastUtil.showShort(getApplication(), (String) mPlanetTitles.get(position));
					break;
				case 2:
					ToastUtil.showShort(getApplication(), (String) mPlanetTitles.get(position));
					break;
				case 3:
					ToastUtil.showShort(getApplication(), (String) mPlanetTitles.get(position));
					break;
				case 4:
					DBReaderHelper.getInstance(getBaseContext()).clearTable();  //清空浏览记录
					break;
				default:
					break;
			}
		}
	}

	private void setMenu() {
		iv = new ImageView(MainActivity.this);
		iv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_rotate));
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		toolbar.addView(iv);
		Toolbar.LayoutParams params = new Toolbar.LayoutParams(
				                                                      Toolbar.LayoutParams.WRAP_CONTENT,
				                                                      Toolbar.LayoutParams.WRAP_CONTENT,
				                                                      Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		params.rightMargin = ScreenUtils.getInstance(MainActivity.this).dip2px(10);
		iv.setLayoutParams(params);

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentFragment.refresh(null);
				startMenuRotateAnimation();
			}
		});
	}

	public void startMenuRotateAnimation() {
		if (rorateAnimation != null && iv != null) {
			iv.startAnimation(rorateAnimation);
		}
	}


	/**
	 * 当双击后退键，并且双击的时间间隔小于3秒，结束所有的activity，也就是退出app
	 */
	public void onBackPressed() {
		if (lastBackPress <= 0) {
			ToastUtil.showShort(this, R.string.quit_app);
			lastBackPress = System.currentTimeMillis();
		} else if (System.currentTimeMillis() - lastBackPress < 3000) { // 两次点击回退按钮的时间间隔小于3秒,则退出系统
			// 小于3秒,退出系统
			super.onBackPressed();
			AppManager.getAppManager().finishAllActivity();
		} else {
			ToastUtil.showShort(this, R.string.quit_app);
			lastBackPress = System.currentTimeMillis();
		}
	}
}
