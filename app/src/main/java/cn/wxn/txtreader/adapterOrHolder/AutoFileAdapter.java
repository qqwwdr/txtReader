package cn.wxn.txtreader.adapterOrHolder;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.wxn.txtreader.R;
import cn.wxn.txtreader.bean.FileItem;
import cn.wxn.txtreader.utils.FileSizeUtil;

/**
 * Created by wangxn on 2016/2/15.
 */
public class AutoFileAdapter  extends RecyclerView.Adapter<MyViewHolder>  {
	private Activity mAc;
	List<FileItem> mDatas;
	private OnItemClickLitener mOnItemClickListener;

	public AutoFileAdapter(Activity ac, List<FileItem> datas) {
		this.mDatas = datas;
		this.mAc = ac;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View         inflate      = View.inflate(mAc, R.layout.item_file2, null);
		MyViewHolder myViewHolder = new MyViewHolder(inflate);
		return myViewHolder;
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		FileItem bean = mDatas.get(position);

		holder.tv_name.setText(bean.fileName.substring(0, bean.fileName.indexOf('.')));
		holder.tv_size.setText(FileSizeUtil.formetFileSize(bean.fileCapacity));
		holder.tv_path.setText(bean.abstractPath);

		if (mOnItemClickListener != null){
			holder.rl_main.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int layoutPosition = holder.getLayoutPosition();
					mOnItemClickListener.onItemClick(holder.rl_main, layoutPosition);
				}
			});

			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					int pos = holder.getLayoutPosition();
					mOnItemClickListener.onItemLongClick(holder.itemView, pos);
					return false;
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	public void setOnItemClickListener(OnItemClickLitener mOnItemClickListener){
		this.mOnItemClickListener = mOnItemClickListener;
	}


	public interface OnItemClickLitener
	{
		void onItemClick(View view, int position);
		void onItemLongClick(View view , int position);
	}
}
