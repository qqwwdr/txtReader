package cn.wxn.txtreader.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.wxn.txtreader.utils.StrUtil;

/**
 * Created by wangxn on 2016/1/21.
 */
public class BaseFragment extends Fragment {

	private final String TAG = "BaseFragment";

	//获得activity的传递的值
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onAttach");
	}

	//实例化成员变量
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onCreate");
	}

	//给当前的fragment绘制UI布局，可以使用线程更新UI
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	//表示activity执行oncreate方法完成了的时候会调用此方法
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onActivityCreated");
	}

	//和activity一致
	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onStart");
	}

	//和activity一致
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onResume");
	}

	//和activity一致
	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onPause");
	}

	//和activity一致
	@Override
	public void onStop() {
		super.onStop();
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onStop");
	}

	//表示fragment销毁相关联的UI布局
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onDestroyView");
	}

	//销毁fragment对象
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onDestroy");
	}

	//脱离activity
	@Override
	public void onDetach() {
		super.onDetach();
		Log.i(TAG, this.getClass().getSimpleName() + "--t->>onDetach");
	}

	public void onCall(Object obj){}

	public void refresh(Object obj){}


	/**
	 * 指定的TextView显示指定的文本内容信息, 如果文本内容为空, TextView显示空白
	 *
	 * @param tv
	 * @param txt
	 */
	protected void showText(TextView tv, String txt) {
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
