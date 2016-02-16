package cn.wxn.txtreader.frag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.wxn.txtreader.R;
import cn.wxn.txtreader.act.MainActivity_;
import cn.wxn.txtreader.act.Reader2Activity;
import cn.wxn.txtreader.act.Reader2Activity_;
import cn.wxn.txtreader.service.AutoSearchService;
import cn.wxn.txtreader.adapterOrHolder.AutoFileAdapter;
import cn.wxn.txtreader.adapterOrHolder.DividerItemDecoration;
import cn.wxn.txtreader.base.BaseFragment;
import cn.wxn.txtreader.bean.FileItem;
import cn.wxn.txtreader.db.DBDataHelper;
import cn.wxn.txtreader.utils.LogUtil;

/**
 * Created by wangxn on 2016/2/15.
 */
@EFragment(R.layout.fragment_auto)
public class AutoFilesFragment extends BaseFragment {

	private static final Object TAG = "AutoFilesFragment";
	@ViewById
	RecyclerView recyclerView;

	private List<FileItem> fileItems = new ArrayList<>();
	private AutoFileAdapter adapter;

	@AfterViews
	void initWidget() {
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		adapter = new AutoFileAdapter(getActivity(), fileItems);
		recyclerView.setAdapter(adapter);
		recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
		adapter.setOnItemClickListener(new AutoFileAdapter.OnItemClickLitener() {
			@Override
			public void onItemClick(View view, int position) {
				LogUtil.i(TAG, "OnItemClickLitener  onItemClick ::: position ::  " + position);
				FileItem fileItem = fileItems.get(position);

				LogUtil.i(TAG, "jump to Reader2Activity ...");
				Intent intent = new Intent();
				intent.setClass(getActivity(), Reader2Activity_.class);
				intent.putExtra(Reader2Activity_.EXTRAS_FILE, fileItem.abstractPath);
				getActivity().startActivity(intent);
			}

			@Override
			public void onItemLongClick(View view, int position) {
			}
		});


		initData();
	}

	private void initData() {
		List<FileItem> files = DBDataHelper.getInstance(getActivity()).getFiles();
		if (files == null || files.size() == 0) {

			MainActivity_ activity = (MainActivity_)getActivity();  //启动菜单栏动画
			activity.startMenuRotateAnimation();

			autoSearch();
		} else {
			//直接从数据库中取出所有的txt文件数据
			onCall(files);
		}
	}

	/**
	 * 启动IntentService, 对文件目录进行扫描
	 */
	private void autoSearch() {
		LogUtil.i(TAG, "启动IntentService, 对文件目录进行扫描");
		Intent intent = new Intent();
		intent.setClass(getActivity(), AutoSearchService.class);
		getActivity().startService(intent);
	}

	@Override
	public void refresh(Object obj) {
		autoSearch();
	}

	@Override
	public void onCall(Object obj) {
		List<FileItem> files = null;
		if (null == obj) {
			files = DBDataHelper.getInstance(getActivity()).getFiles();
		}else {
			files = (List<FileItem>) obj;
		}
		if (files != null && files.size() > 0){
			fileItems.clear();
			fileItems.addAll(files);
			adapter.notifyDataSetChanged();
		}
	}
}
